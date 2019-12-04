--
-- Created by IntelliJ IDEA.
-- User: Pop
-- Date: 2019/12/4
-- Time: 16:46
-- To change this template use File | Settings | File Templates.
--
local exists = redis.call('exists',KEYS[1])
if tonumber(exists)==0 then
    --无法释放别人的锁
    local cid=redis.call('hget',KEYS[1],'cid')
    if tostring(cid)==ARGV[1] then
        redis.call('HINCRBY',KEYS[1],-1)
        return true
    else
        -- 不是自己无法释放
        return false
    end
end


