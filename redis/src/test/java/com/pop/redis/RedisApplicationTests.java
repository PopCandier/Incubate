package com.pop.redis;

import com.pop.redis.utils.RedisUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisApplicationTests {

    @Autowired
    private RedisUtils redis;

    @Test
    void contextLoads() {

//        utils.set("all","一段内容");
//
//        String value = (String) utils.get("all");
//        String ob = (String) utils.get("章炎1");
//        System.out.println(value+ob);
//            utils.set("测试1","具体内容");
        redis.set("pop","11111");
        System.out.println(redis.get("pop"));
//        redisTemplate.opsForValue().set("章炎1","是小点点");

    }

}
