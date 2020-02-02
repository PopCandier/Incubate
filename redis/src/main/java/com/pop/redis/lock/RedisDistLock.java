package com.pop.redis.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @program: incubate
 * @description: redis 分布式锁
 * @author: Pop
 * @create: 2019-12-04 16:10
 **/
@Component
public class RedisDistLock implements IRedisLock {

    private static final String lockName = "distLock";
    private static final String expireName = "distLockTime";
    private RedisTemplate redis;
    @Autowired
    private LuaLockScript script;

    public RedisDistLock(RedisTemplate redisTemplateIncubate) {
        this.redis = redisTemplateIncubate;
    }


    @Override
    public boolean tryLock(String requestId) {
        return (boolean) redis.execute(script.getTryLockScript(), script.keys(lockName, expireName), requestId);
    }

    public boolean lock(String requestId) {
        return lock(requestId, 2000);
    }

    @Override
    public boolean lock(String requestId, long expireTime) {
        long second = TimeUnit.MILLISECONDS.toSeconds(expireTime);
        return (boolean) redis.execute(script.getLockScript(), script.keys(lockName, expireName), requestId, String.valueOf(expireTime), String.valueOf(second > 1L ? second : 1));
    }

    @Override
    public boolean unlock(String requestId) {
        return (boolean) redis.execute(script.getUnLockScript(), script.keys(lockName), requestId);
    }
}
