package com.pop.redis.lock;

/**
 * redis 锁
 */
public interface IRedisLock {

    /**
     * 尝试获取锁
     *
     * @param requestId 请求id
     * @return
     */
    boolean tryLock(String requestId);

    /**
     * 获取锁
     *
     * @param requestId  请求id
     * @param expireTime 超时时间，单位为毫秒
     * @return 是否获取成功
     * <p>
     * 将会在指定时间拥有锁，当超过指定超时时间还未释放锁的时候
     * 将会自动减少锁的重入次数。
     */
    boolean lock(String requestId, long expireTime);

    /**
     * 释放锁
     *
     * @param requestId 请求id
     */
    boolean unlock(String requestId);
}
