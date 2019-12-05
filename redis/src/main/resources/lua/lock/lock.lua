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
    redis.call('hmset',KEYS[1],'cid',ARGV[1],'count',1)
    redis.call('set',KEYS[2],ARGV[1],'PX',ARGV[2],'NX')
    return true
else
        -- 存在的情况判断是否需要重入
        local result = redis.call('hmget',KEYS[1],'cid','count')
-- 上一步存在的时候，设置了一次锁持有的超时时间
        if  tonumber(result[2])==0 then
        -- 表示锁已经释放完毕，重新设值，并重新设id的超时时间
            redis.call('hmset',KEYS[1],'cid',ARGV[1],'count',1)
            redis.call('set',KEYS[2],ARGV[1],'PX',ARGV[2])
            return true
        elseif tostring(result[1])==ARGV[1] then
            -- 延长存活周期
            redis.call('EXPIRE',KEYS[2],ARGV[3])
            redis.call('HINCRBY',KEYS[1],'count',1)
        -- 是重入
            return true
        elseif  tonumber(redis.call('exists',KEYS[2]))==0 then
            --帮他减少1，并尝试获取锁
            local count=redis.call('hget',KEYS[1],'count')
            if tonumber(count)>0 then
                count = redis.call('hincrby',KEYS[1],'count',-1)
                if tonumber(count)==0 then
                    -- 表示锁已经释放完毕，重新设值，并重新设id的超时时间
                    redis.call('hmset',KEYS[1],'cid',ARGV[1],'count',1)
                    redis.call('set',KEYS[2],ARGV[1],'PX',ARGV[2])
                    return true
                end
            end
        else
            -- 存在的情况，获取锁失败
            return false
        end
end


