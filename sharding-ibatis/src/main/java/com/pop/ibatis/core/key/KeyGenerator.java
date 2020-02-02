package com.pop.ibatis.core.key;

import java.util.List;

/**
 * @author Pop
 * @date 2020/2/2 18:52
 *
 * 关键字生成
 */
public interface KeyGenerator {

    /**
     * 生成一段自己定义规范的key，用来路由到具体库或者表
     * @return
     */
    List<String> generator(List<String> names);

    boolean isSupport(Object object);
}
