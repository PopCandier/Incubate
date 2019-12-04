package com.pop.redis.lock;

import lombok.Data;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @program: incubate
 * @description: lua 与 分布式锁相关的脚本
 * @author: Pop
 * @create: 2019-12-04 16:12
 **/
@Component
@Data
public class LuaLockScript extends LuaScript{
    @Resource(name = "lockScript")
    private DefaultRedisScript<Boolean> lockScript;
    @Resource(name = "tryLockScript")
    private DefaultRedisScript<Boolean> tryLockScript;
    @Resource(name = "unLockScript")
    private DefaultRedisScript<Boolean> unLockScript;
}
