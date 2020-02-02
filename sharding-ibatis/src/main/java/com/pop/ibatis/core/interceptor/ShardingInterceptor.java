package com.pop.ibatis.core.interceptor;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.pop.ibatis.annotations.Sharding;
import com.pop.ibatis.datasource.ShardingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;



/**
 * @author Pop
 * @date 2020/2/2 17:12
 *
 * 分库分表的拦截器
 */
@Intercepts({
        @Signature(type = Executor.class,method = "query",args = {MappedStatement.class,Object.class, RowBounds.class, ResultHandler.class})
})
@Slf4j
public class ShardingInterceptor implements Interceptor {

    private Map<String, Method> reflectCache = new HashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
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
        //读取设置
        String baseKey = sharding.baseKey();
        if(!StringUtils.isEmpty(baseKey)){ShardingDataSource.setDataSource(baseKey);}
        Object returnObject = invocation.proceed();
        return returnObject;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target,this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
