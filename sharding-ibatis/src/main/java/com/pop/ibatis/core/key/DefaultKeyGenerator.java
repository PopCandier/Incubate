package com.pop.ibatis.core.key;

import java.util.List;

/**
 * @author Pop
 * @date 2020/2/2 18:54
 */
public class DefaultKeyGenerator implements KeyGenerator {

    @Override
    public List<String> generator(List<String> names) {
        return names;
    }

    @Override
    public boolean isSupport(Object object) { return object instanceof KeyGenerator; }
}
