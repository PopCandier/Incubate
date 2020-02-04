package com.pop.ibatis.annotations;

import com.pop.ibatis.annotations.enums.ShardingType;

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

    /**
     * 当表不存时（只限于 insert 方法），是否使用数据库的某个表作为模版创建 {@link ShardingTable#name}<br/>
     * 如未修饰，将会忽略该配置。<b>如果这个设置为true，{@link ShardingTable#name}必须设置模版名</b>
     * @return
     */
    boolean fromTemplate() default false;

    /**
     * 当表不存在时 （只限于 insert 方法），是否使用 {@link ShardingTable#name} 修饰的
     *  当某个字段 被{@link ShardingIgnoreField}修饰的时候，该字段将不会生成到数据库的字段里。
     * @return
     */
    boolean fromEntity() default false;


}
