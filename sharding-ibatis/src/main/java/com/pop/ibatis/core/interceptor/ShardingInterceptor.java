package com.pop.ibatis.core.interceptor;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.pop.ibatis.annotations.Sharding;
import com.pop.ibatis.annotations.ShardingRule;
import com.pop.ibatis.annotations.enums.ShardingType;
import com.pop.ibatis.core.rout.ShardingRoutRule;
import com.pop.ibatis.datasource.ShardingDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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

    private Map<String, Method> reflectCache = new HashMap<>();
    private Map<SqlCommandType,Pattern> sqlKey = new HashMap(){{
        put(SqlCommandType.SELECT,"");
        put(SqlCommandType.UPDATE,pattern("insert\\s+into\\s+([a-zA-Z_])+[(]"));
        put(SqlCommandType.INSERT,pattern("insert\\s+into\\s+([a-zA-Z_])+[(]"));
        put(SqlCommandType.DELETE,"");
    }};

    private ShardingDataSource shardingDataSource =null;
    public ShardingInterceptor(ShardingDataSource shardingDataSource) { this.shardingDataSource = shardingDataSource; }

    private Pattern pattern(String reg){return Pattern.compile(reg);}

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
        BoundSql boundSql=ms.getBoundSql(param);
        String sql=boundSql.getSql();
        Sharding sharding = interceptMethod.getAnnotation(Sharding.class);
        if(sharding==null){return invocation.proceed();}
        //读取 数据库设置
        String baseKey = sharding.baseKey();
        if(!StringUtils.isEmpty(baseKey)){ShardingDataSource.setDataSource(baseKey);}
        //读取表的设置
        ShardingRule[] rules = sharding.rule();
        for (ShardingRule rule:rules) {
            ShardingType type = rule.type();
            Class routRule = rule.routRule();
            String fieldName = rule.fieldName();
            if(param instanceof MapperMethod.ParamMap){
                // 说明含有多个参数，暂不考虑
            }else{
                //只有一个对象作为参数
                Class c=param.getClass();
                Field field=c.getDeclaredField(fieldName);
                field.setAccessible(true);
                ShardingRoutRule shardingRoutRule =null;
                try {
                    shardingRoutRule = (ShardingRoutRule) routRule.newInstance();
                }catch (Exception e){
                    log.error(String.format(" 路由规则 %s 未实现 ShardingRoutRule 接口规范"),routRule.getName());
                }
                String tableKey=shardingRoutRule.build(field.get(param));
                //是否是对应sql，取出匹配正则表达式 select update insert delete
                //目前只支持替换表
                Matcher matcher=sqlKey.get(ms.getSqlCommandType()).matcher(sql);
                while(matcher.find()) {
                    String matcherKey = matcher.group();
                    String s = matcherKey.substring(0, matcherKey.lastIndexOf("(")) + tableKey + "(";
                    String replaceSql = sql.replace(matcherKey, s);
                    //重新创建一个对象
                    BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), replaceSql, boundSql.getParameterMappings(), param);
                    //重新复制一个ms
                    MappedStatement newStatement = copyFromMappedStatement(ms, new ShardingSqlSource(newBoundSql));
                    for (ParameterMapping mapping : boundSql.getParameterMappings()) {
                        String prop = mapping.getProperty();
                        if (boundSql.hasAdditionalParameter(prop)) {
                            newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
                        }
                    }
                    //将invocation中的ms替换掉
                    args[0] = newStatement;
                }
            }

        }

        Object returnObject = invocation.proceed();
        return returnObject;
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
