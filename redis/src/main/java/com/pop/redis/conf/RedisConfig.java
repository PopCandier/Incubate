package com.pop.redis.conf;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @program: incubate
 * @description:
 * @author: Pop
 * @create: 2019-12-03 15:22
 **/
@Configuration
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    @Bean(name = "redisTemplateIncubate")
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object,Object> redisTemplate = new RedisTemplate();
        //序列化
        FastJsonRedisSerializer redisSerializer = new FastJsonRedisSerializer(Object.class);
        // value值的序列化采用fastJsonRedisSerializer
        redisTemplate.setKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(redisSerializer);
        // key的序列化采用StringRedisSerializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    @Bean
    public DefaultRedisScript<Boolean> lockScript(){ return getScript(Boolean.class,"lua/lock/lock.lua"); }
    @Bean
    public DefaultRedisScript<Boolean> tryLockScript(){ return getScript(Boolean.class,"lua/lock/tryLock.lua"); }
    @Bean
    public DefaultRedisScript<Boolean> unLockScript(){ return getScript(Boolean.class,"lua/lock/unLock.lua");}

    private DefaultRedisScript getScript(Class clazz,String url){
        DefaultRedisScript<Boolean> script = new DefaultRedisScript<>();
        script.setResultType(clazz);
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(url)));
        return script;
    }



}
