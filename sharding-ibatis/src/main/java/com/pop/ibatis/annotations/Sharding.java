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
     * 默认的实现按照主键取模
     * @return 具体的路由实现 class 全路径
     */
    String rule() default "";

    /**
     * 具体是分库还是分表
     * 默认分表
     * @return
     */
    ShardingType type() default ShardingType.TABLE;

    /**
     * 数据库关键字
     * @return
     */
    String baseKey() default "";

}
