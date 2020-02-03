package com.pop.ibatis.annotations;

import com.pop.ibatis.annotations.enums.ShardingType;

import java.lang.annotation.*;

/**
 * @author Pop
 * @date 2020/2/1 22:08
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Sharding {


    /**
     * 路由规则
     *
     * @return
     */
    ShardingRule[] rule();


    /**
     * 数据库关键字
     * @return
     */
    String baseKey() default "";

}
