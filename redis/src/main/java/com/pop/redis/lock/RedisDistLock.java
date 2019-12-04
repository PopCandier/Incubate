package com.pop.redis.lock;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: incubate
 * @description: redis 分布式锁
 * @author: Pop
 * @create: 2019-12-04 16:10
 **/
@Component
public class RedisDistLock implements IRedisLock{

    @Resource(name = "redisTemplateIncubate")
    private RedisTemplate<Object, Object> redis;

    @Override
    public boolean tryLock() {

        return false;
    }

    @Override
    public boolean lock() {
        return false;
    }

    @Override
    public void unlock() {

    }
}
