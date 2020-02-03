package com.pop.ibatis.annotations;

import com.pop.ibatis.annotations.enums.ShardingType;
import com.pop.ibatis.core.key.DefaultKeyGenerator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Pop
 * @date 2020/2/3 18:50
 *
 * 定义更加丰富的规则
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface ShardingRule {

    /**
     * 具体是分库还是分表
     *
     * 无论是分库还是分表，routRule的方法将会使用entity内的定义生成规则来产生后缀key
     * 默认分表
     * @return
     */
    ShardingType type() default ShardingType.TABLE;

    /**
     * 路由规则
     * @return
     */
    Class<?> routRule();

    /**
     * 希望作为依据的字段名
     * @return
     */
    String fieldName() default "";


}
