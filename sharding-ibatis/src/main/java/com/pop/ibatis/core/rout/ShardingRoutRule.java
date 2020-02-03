package com.pop.ibatis.core.rout;

/**
 * @author Pop
 * @date 2020/2/3 19:02
 *
 * 路由规则
 *
 * E 无论传入的是什么类型，只要符合你的生成规则就可以
 */
public interface ShardingRoutRule<E> {

    /**
     * 用于生成规则的字符串
     * @param e 参数
     * @return
     */
    String rule(E e);

    String build(E e);

}
