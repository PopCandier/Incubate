package com.pop.redis.lock;

/**
 * redis 锁
 */
public interface IRedisLock {

    /**
     * 尝试获取锁
     * @return
     */
    boolean tryLock();

    /**
     * 获取锁
     * @return
     */
    boolean lock();

    /**
     * 释放锁
     */
    void unlock();
}
