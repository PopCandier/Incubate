package com.pop.ibatis.type;

import lombok.Data;

/**
 * @author Pop
 * @date 2020/2/4 22:07
 */
@Data
public class TypeNode {
    /**
     * JDBC类型
     */
    private String fieldName;

    public TypeNode(String fieldName, String defaultValue) {
        this.fieldName = fieldName;
        this.value = defaultValue;
    }

    /**
     * 长度
     */
    private String value;
}
