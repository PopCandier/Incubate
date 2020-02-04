package com.pop.ibatis.function;

/**
 * @author Pop
 * @date 2020/2/4 20:32
 */
@FunctionalInterface
public interface WuFunction<T,U,K,S,O,R> {

    /**
     * 支持三合一操作
     * @param t 第一个参数
     * @param u 第二个参数
     * @param k 第三个参数
     *          可能后面还加了很多参数
     * @return
     */
    R operation(T t,U u,K k,S s,O o);
}
