package com.pop.ibatis.annotations;

import java.lang.annotation.*;

/**
 * @author Pop
 * @date 2020/2/5 0:18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ShardingTableID {

    boolean autoIncrement() default true;
}
