package com.pop.redis.lock;

import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String lockName = "distLock";
    private RedisTemplate redis;
    @Autowired
    private LuaLockScript script;
    public RedisDistLock(RedisTemplate redisTemplateIncubate) {
        this.redis = redisTemplateIncubate;
    }

    public boolean tryLock(String requestId){
        return tryLock(requestId,5);
    }

    @Override
    public boolean tryLock(String requestId, long expireTime) {
        return (boolean) redis.execute(script.getTryLockScript(),script.keys(lockName),requestId,String.valueOf(expireTime));
    }

    public boolean lock(String requestId){
        return lock(requestId,5);
    }

    @Override
    public boolean lock(String requestId, long expireTime) {
        return (boolean) redis.execute(script.getLockScript(),script.keys(lockName),requestId,String.valueOf(expireTime));
    }

    @Override
    public boolean unlock(String requestId) {
        return (boolean) redis.execute(script.getUnLockScript(),script.keys(lockName),requestId);
    }
}
