package com.pop.ibatis.annotations;

import java.lang.annotation.*;

/**
 * @author Pop
 * @date 2020/2/4 21:30
 *
 * 当表不存在的时( 限于insert) ，将会用到此注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ShardingTable {

    /**
     * 表的名字，必须指定
     * @return
     */
    String name();
}
