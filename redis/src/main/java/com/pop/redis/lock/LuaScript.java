package com.pop.redis.lock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pop
 * @date 2019/12/4 20:17
 */
public class LuaScript {

    public List<String> keys(String... keys) {
        return Arrays.asList(keys);
    }

    @Deprecated
    public Map<String, Object> argv(String... args) {
        Map<String, Object> map = new HashMap<>();
        Arrays.stream(args).forEach((s) -> {
                    String[] v = s.split(":");
                    map.put(v[0], v[1]);
                }
        );
        return map;
    }
}
