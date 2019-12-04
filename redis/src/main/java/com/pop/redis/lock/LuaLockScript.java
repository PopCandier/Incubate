package com.pop.redis.lock;

import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

/**
 * @program: incubate
 * @description: lua 与 分布式锁相关的脚本
 * @author: Pop
 * @create: 2019-12-04 16:12
 **/
@Component
public class LuaLockScript {

    DefaultRedisScript<Boolean> lockScript;

}
