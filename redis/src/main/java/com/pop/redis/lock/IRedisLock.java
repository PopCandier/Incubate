package com.pop.redis.lock;

/**
 * redis 锁
 */
public interface IRedisLock {

    /**
     * 尝试获取锁
     * @param requestId 请求id
     * @param expireTime 超时时间，默认为秒
     * @return
     */
    boolean tryLock(String requestId,long expireTime);

    /**
     * 获取锁
     * @param requestId 请求id
     * @param expireTime 超时时间，默认为秒
     * @return
     */
    boolean lock(String requestId,long expireTime);

    /**
     * 释放锁
     * @param requestId 请求id
     */
    boolean unlock(String requestId);
}
