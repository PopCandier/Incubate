-- 判断锁是否存在 KEYS[1] key ARGV[1] cid
local exists = redis.call('exists', KEYS[1])
if tonumber(exists) == 0 then
    --不存在，表示第一次，可以获取锁
    return true
else
    local result = redis.call('hmget', KEYS[1], 'cid', 'count')
    --判断能不能拿锁
    if tonumber(result[2]) == 0 then
        return true
    elseif tonumber(redis.call('exists', KEYS[2])) == 0 then
        return true
    elseif tostring(result[1]) == ARGV[1] then
        --第一种情况，cid是自己，可以重入
        return true
    else
        -- 无法拿到锁
        return false
    end
end
