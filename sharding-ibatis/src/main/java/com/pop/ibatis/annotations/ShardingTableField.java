package com.pop.ibatis.annotations;

import java.lang.annotation.*;

/**
 * @author Pop
 * @date 2020/2/4 22:41
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ShardingTableField {

    /**
     * 数据库上的备注
     * @return
     */
    String comment() default "";

    /**
     * 是否允许为空
     * @return
     */
    boolean isNull() default true;
}
