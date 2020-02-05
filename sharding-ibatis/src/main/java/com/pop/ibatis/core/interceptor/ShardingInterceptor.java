package com.pop.ibatis.core.interceptor;

import com.pop.ibatis.annotations.Sharding;
import com.pop.ibatis.annotations.ShardingRule;
import com.pop.ibatis.annotations.enums.ShardingType;
import com.pop.ibatis.builder.CreateTableBuilder;
import com.pop.ibatis.core.rout.ShardingRoutRule;
import com.pop.ibatis.datasource.ShardingDataSource;
import com.pop.ibatis.function.WuFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Pop
 * @date 2020/2/2 17:12
 *
 * 分库分表的拦截器
 */
@Intercepts({
        @Signature(type = Executor.class,method = "query",args = {MappedStatement.class,Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Slf4j
public class ShardingInterceptor implements Interceptor {

    /**
     * {@link MappedStatement#getId()}语句与对应方法的缓存
     *
     */
    private static Map<String, Method> reflectCache = new ConcurrentHashMap<>();

    /**
     * 路由规则的缓存，class对象与其实例的缓存
     *
     */
    private static Map<Class<?>,ShardingRoutRule> routRuleCache = new ConcurrentHashMap<>();

    private static Pattern pattern(String reg){return Pattern.compile(reg);}

    private static String afterCheckSql(String sql,String replaceSql){return StringUtils.isEmpty(replaceSql)?sql:replaceSql;}

    static WuFunction<String,String,Pattern,ShardingRule,Object,String> queryOperation=null;
    static WuFunction<String,String,Pattern,ShardingRule,Object,String> updateOperation=null;
    static WuFunction<String,String,Pattern,ShardingRule,Object,String> deleteOperation=null;
    static WuFunction<String,String,Pattern,ShardingRule,Object,String> insertOperation=null;

    private static Map<SqlCommandType,OperationNode> sqlKey = null;

    static {

        queryOperation = query();
        updateOperation = update();
        insertOperation = insert();
        deleteOperation = delete();

        sqlKey=new HashMap(){{
            put(SqlCommandType.SELECT,new OperationNode(queryOperation,pattern("")));
            put(SqlCommandType.UPDATE,new OperationNode(updateOperation,pattern("")));
            put(SqlCommandType.INSERT,new OperationNode(insertOperation,pattern("insert\\s+into\\s+([a-zA-Z_])+[(]")));
            put(SqlCommandType.DELETE,new OperationNode(deleteOperation,pattern("")));
        }};

    }

    private static WuFunction<String,String,Pattern,ShardingRule,Object,String> delete(){
        return (sql,key,pattern,rule,entity)->"";
    }

    private static WuFunction<String,String,Pattern,ShardingRule,Object,String> update(){
        return (sql,key,pattern,rule,entity)->"";
    }

    private static WuFunction<String,String,Pattern,ShardingRule,Object,String> query(){
        return (sql,key,pattern,rule,entity)->"";
    }

    private static WuFunction<String,String,Pattern,ShardingRule,Object,String> insert(){
        return (sql,key,pattern,rule,entity)->{
            Matcher matcher=pattern.matcher(sql);
            String replaceSql = "";
            String replaceTableName = "";
            while(matcher.find()) {
                String matcherKey = matcher.group();
                replaceTableName = matcherKey.substring(0, matcherKey.lastIndexOf("(")) + key + "(";
                replaceSql=sql.replace(matcherKey, replaceTableName);
                break;
            }
            // 查看关于生成表的配置
            Class entityClass = entity.getClass();
            String createTableSql = "";
            if(rule.fromEntity()){
                createTableSql = CreateTableBuilder.build(entityClass,replaceTableName);
            }
            if(rule.fromTemplate()){
                // 从数据库模版
                createTableSql = CreateTableBuilder.buildFromTemplate(entityClass,replaceTableName);
            }
            return createTableSql+afterCheckSql(sql,replaceSql);
        };
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object param = args[1];
        Executor executor = (Executor) invocation.getTarget();
        String id = ms.getId();//分割
        String reflectKey = id.substring(0,id.lastIndexOf("."));
        String methodName = id.substring(id.lastIndexOf(".")+1);
        Method interceptMethod = null;
        if(reflectCache.containsKey(reflectKey)){
            interceptMethod = reflectCache.get(reflectKey);
        }else{
            Class interceptClass=Class.forName(reflectKey);
            Method[] methods=interceptClass.getMethods();
            for (Method method: methods) { if(methodName.equals(method.getName())){
                // fixme  可能存在重载可能性，mapper接口会存在重载的方法吗？
                interceptMethod = method;
                break;
                }
            }
            reflectCache.put(reflectKey,interceptMethod);
        }
        Sharding sharding = interceptMethod.getAnnotation(Sharding.class);
        if(sharding==null){return invocation.proceed();}
        //读取 数据库设置
        String baseKey = sharding.baseKey();
        if(!StringUtils.isEmpty(baseKey)){ShardingDataSource.setDataSource(baseKey);}
        //读取表的设置
        BoundSql boundSql=ms.getBoundSql(param);
        String sql=boundSql.getSql();
        String replaceSql = "";
        ShardingRule[] rules = sharding.rule();
        for (ShardingRule rule:rules) {
            ShardingType type = rule.type();
            Class routRule = rule.routRule();
            String fieldName = rule.fieldName();
            if(param instanceof MapperMethod.ParamMap){
                // todo 说明含有多个参数，暂不考虑
            }else{

                if(param instanceof Map){
                    // todo 只有一个对象作为参数,也有可能是HashMap
                }else{
                    Class c=param.getClass();
                    Field field=c.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    ShardingRoutRule shardingRoutRule =null;
                    boolean isRule = false;
                    try {
                        //加入缓存
                        if(routRuleCache.containsKey(routRule)){
                            shardingRoutRule = routRuleCache.get(routRule);
                        }else{
                            shardingRoutRule = (ShardingRoutRule) routRule.newInstance();
                            routRuleCache.put(routRule,shardingRoutRule);
                        }
                        isRule = true;
                    }catch (Exception e){
                        log.error(" 路由规则 "+routRule.getName()+" 未实现 ShardingRoutRule 接口规范");
                    }
                    String tableKey = "";
                    if(isRule){
                        //是否是对应sql，取出匹配正则表达式 select update insert delete
                        //目前只支持替换表
                        // 这个地方可以改写成 pattern与某个对象组成封装成一个对象
                        tableKey=shardingRoutRule.build(field.get(param));
                        if(sqlKey.containsKey(ms.getSqlCommandType())){
                            OperationNode operationNode=sqlKey.get(ms.getSqlCommandType());
                            replaceSql = operationNode.operator.operation(sql,tableKey,operationNode.pattern,rule,param);
                            args[0] = rebuildMappedStatement(ms, param, boundSql, replaceSql);;
                        }
                    }
                }
            }

        }



        Object returnObject = invocation.proceed();
        return returnObject;
    }

    private MappedStatement rebuildMappedStatement(MappedStatement ms, Object param, BoundSql boundSql, String replaceSql) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), replaceSql, boundSql.getParameterMappings(), param);
        //重新复制一个ms
        MappedStatement newStatement = copyFromMappedStatement(ms, new ShardingSqlSource(newBoundSql));
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newStatement;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
                ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    /**
     * 用于存储截取sql表达式和具体操作的类
     */
    static class OperationNode{
        public OperationNode(WuFunction<String,String,Pattern,ShardingRule,Object,String> operator, Pattern pattern) {
            this.operator = operator;
            this.pattern = pattern;
        }
        private WuFunction<String,String,Pattern,ShardingRule,Object,String> operator;
        private Pattern pattern;
    }

    class ShardingSqlSource implements SqlSource {
        private BoundSql boundSql;

        public ShardingSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
