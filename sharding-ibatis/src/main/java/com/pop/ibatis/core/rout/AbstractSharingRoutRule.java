package com.pop.ibatis.core.rout;

/**
 * @author Pop
 * @date 2020/2/3 20:26
 */
public abstract class AbstractSharingRoutRule<E> implements ShardingRoutRule<E> {
    protected static final String RULE_PREFIX = "_$";

    public String build(E e){return RULE_PREFIX+rule(e);}

}
