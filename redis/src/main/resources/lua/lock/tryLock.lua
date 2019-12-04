-- 判断锁是否存在 KEYS[1] key ARGV[1] cid
local exists = redis.call('exists',KEYS[1])
if tonumber(exists)==0 then
    --不存在，表示第一次，可以获取锁
    return true
else
    local cid=redis.call('hget',KEYS[1],'cid')
    if tostring(cid)==ARGV[1] then
        --第一种情况，cid是自己，可以重入
        return true
        else
        --不相同情况，无法获得锁
        return false
    end
end
