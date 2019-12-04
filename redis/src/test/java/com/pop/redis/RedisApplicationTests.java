package com.pop.redis;

import com.pop.redis.lock.RedisDistLock;
import com.pop.redis.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
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

    @Autowired
    private RedisDistLock lock;
    private Random random = new Random();
    @Test
    void lockTest() throws Exception {

//        if(lock.tryLock("123")){
////            if(lock.lock("123")){
////                System.out.println("获取锁成功");
////            }else{
////                System.out.println("获取锁失败");
////            }
//
////            lock.lock("123");
//////            redis.set("pop","11111");
//////            TimeUnit.SECONDS.sleep(5);
////
//////            lock.unlock("123");lock.unlock("123");
////            lock.unlock("123");
//        }
//        if(lock.unlock("123")){
//            System.out.println("释放锁成功");
//        }else{
//            System.out.println("释放锁失败");
//        }

        for (int i = 0; i <10 ; i++) {
            Thread t = new Thread(()->{
                for(;;){
                    String currName = Thread.currentThread().getName();
                    if(lock.lock(currName)){

                        log.info(currName+" 获取锁成功");
                        try {
                            TimeUnit.SECONDS.sleep(random.nextInt(3));//模仿业务时间
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        lock.unlock(currName);
                        log.info(currName+" 释放锁成功");
                        break;
                    }else{
                        log.info(currName+" 获取锁失败，进入等待");
                        try {
                            TimeUnit.SECONDS.sleep(random.nextInt(2));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            t.start();
        }
        System.in.read();
    }

}
