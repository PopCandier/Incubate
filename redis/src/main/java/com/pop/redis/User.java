package com.pop.redis;

import lombok.Data;

/**
 * @author Pop
 * @date 2020/2/17 21:56
 */
@Data
public class User {
    private String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}
