package com.pop.ibatis.annotations;

import java.lang.annotation.*;

/**
 * @author Pop
 * @date 2020/2/1 23:53
 *
 * mybatis 中 一些格式的转换
 *
 * mysql datetime->java.util.Date转换
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DateFormat {

    String pattern() default "yyyy-MM-dd";

}
