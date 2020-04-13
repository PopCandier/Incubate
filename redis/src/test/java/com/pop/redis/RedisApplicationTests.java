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
//        redis.set("pop","11111");
        System.out.println(TimeUnit.MILLISECONDS.toSeconds(2000));
//        redisTemplate.opsForValue().set("章炎1","是小点点");

    }

    @Autowired
    private RedisDistLock lock;
    private Random random = new Random();

    @Test
    void serializeObject(){
//        User user = new User("Pop");
        redis.set("pop2".getBytes(),"123".getBytes());
        User user1= (User) redis.get("pop2");
        System.out.println(user1);
    }

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

        for (int i = 0; i < 50; i++) {
            Thread t = new Thread(() -> {
                for (; ; ) {
                    String currName = Thread.currentThread().getName();
                    if (lock.tryLock(currName)) {
//                        lock.lock(currName,2000);
                        log.info(currName + " 正在尝试获取...");
                        if (lock.lock(currName, 3000)) {
                            log.info(currName + " 获取锁成功");
//                            try {
//                                TimeUnit.SECONDS.sleep(2);//模仿业务时间
//                                //在这个时候，尝试重入
//                                if(lock.lock(currName,3000)){
//                                    log.info(currName+ " 重入成功！");
//                                    TimeUnit.SECONDS.sleep(1);//第二次业务时间
//                                    lock.unlock(currName);
//                                    log.info(currName+ " 重入释放！");
//                                }else{
//                                    log.info(currName+ " 重入失败！");
//                                }
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            lock.unlock(currName);
                            log.info(currName + " 释放锁成功");
                            break;
                        } else {
                            log.info(currName + " 获取锁失败，进入等待");
                            try {
                                TimeUnit.SECONDS.sleep(random.nextInt(2));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        log.info(currName + " 尝试获取失败");
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

    @Test
    void lockTest0() throws Exception {

        for (int i = 0; i < 3; i++) {
            Thread t = new Thread(() -> {
                String[] s = {"1号", "2号", "3号"};
                for (; ; ) {

                    String currName = Thread.currentThread().getName();
                    if (lock.tryLock(currName)) {
//                        lock.lock(currName,2000);
                        log.info(currName + " 正在尝试获取...");
                        if (lock.lock(currName, 3000)) {
                            log.info(currName + " 获取锁成功");
                            try {
                                TimeUnit.SECONDS.sleep(2);//模仿业务时间
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            lock.unlock(currName);
                            log.info(currName + " 释放锁成功");
                            break;
                        } else {
                            log.info(currName + " 获取锁失败，进入等待");
                            try {
                                TimeUnit.SECONDS.sleep(random.nextInt(2));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        log.info(currName + " 尝试获取失败");
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

        for (int i = 0; i < 3; i++) {
            Thread t = new Thread(() -> {
                for (; ; ) {
                    String currName = Thread.currentThread().getName();
                    if (lock.tryLock(currName)) {
//                        lock.lock(currName,2000);
                        log.info(currName + " 正在尝试获取...");
                        if (lock.lock(currName, 3000)) {
                            log.info(currName + " 获取锁成功");
                            try {
                                TimeUnit.SECONDS.sleep(2);//模仿业务时间
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            lock.unlock(currName);
                            log.info(currName + " 释放锁成功");
                            break;
                        } else {
                            log.info(currName + " 获取锁失败，进入等待");
                            try {
                                TimeUnit.SECONDS.sleep(random.nextInt(2));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        log.info(currName + " 尝试获取失败");
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
