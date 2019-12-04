--
-- Created by IntelliJ IDEA.
-- User: Pop
-- Date: 2019/12/4
-- Time: 16:46
-- To change this template use File | Settings | File Templates.
-- KEYS[1] 对应key ARGV[1] cid ARGV[2] 超时时间
local exists = redis.call('exists',KEYS[1])
if tonumber(exists)==0 then
    --需要初始化 cid 和 count 属性
    redis.call('hset',KEYS[1],'cid',ARGV[1])
    redis.call('hset',KEYS[1],'count',1)
    return true
else
        -- 存在的情况判断是否需要重入
        local cid=redis.call('hget',KEYS[1],'cid')
        local count=redis.call('hget',KEYS[1],'count')
        if tostring(cid)==ARGV[1] then
            redis.call('HINCRBY',KEYS[1],'count',1)
            return true
        elseif  tonumber(count)==0 then
            -- 表示锁已经释放完毕，重新设值
            redis.call('hset',KEYS[1],'cid',ARGV[1])
            redis.call('hset',KEYS[1],'count',1)
            return true
        else
            -- 存在的情况，获取锁失败
            return false
        end
end



