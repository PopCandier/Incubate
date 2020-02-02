--
-- Created by IntelliJ IDEA.
-- User: Pop
-- Date: 2019/12/4
-- Time: 16:46
-- To change this template use File | Settings | File Templates.
--
local exists = redis.call('exists', KEYS[1])
if tonumber(exists) == 1 then
    --无法释放别人的锁
    local result = redis.call('hmget', KEYS[1], 'cid', 'count')
    if tostring(result[1]) == ARGV[1] then
        if tonumber(result[2]) > 0 then
            redis.call('HINCRBY', KEYS[1], 'count', -1)
            return true
        else
            return false
        end
    else
        -- 不是自己无法释放
        return false
    end
else
    return false
end


