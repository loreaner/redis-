-- Lua script for acquiring a lock
if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then
    return redis.call('pexpire', KEYS[1], ARGV[2])
else
    return 0
end

-- Lua script for releasing a lock
if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end