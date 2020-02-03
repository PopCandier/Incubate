package com.pop.ibatis.annotations;

import java.lang.annotation.*;

/**
 * @author Pop
 * @date 2020/2/3 21:19
 *
 * 路由会用到的key
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ShardingFieldFey {

}
