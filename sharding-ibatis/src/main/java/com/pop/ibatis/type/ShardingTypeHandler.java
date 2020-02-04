package com.pop.ibatis.type;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pop
 * @date 2020/2/4 22:05
 */
public final class ShardingTypeHandler {

    private final static Map<Class,TypeNode> jdbcTypeRegister = new HashMap<>();
    private final static TypeNode DEFAULT_NODE = new TypeNode("int(?)","8");
    static {
        register(Boolean.class,new TypeNode("tinyint(?)","1"));
        register(boolean.class,new TypeNode("tinyint(?)","1"));

        register(Long.class,new TypeNode("int(?)","11"));
        register(long.class,new TypeNode("int(?)","11"));
        register(Integer.class,DEFAULT_NODE);
        register(int.class,DEFAULT_NODE);

        register(Double.class,new TypeNode("decimal(?)","10,0"));
        register(double.class,new TypeNode("decimal(?)","10,0"));
        register(Float.class,new TypeNode("decimal(?)","10,0"));
        register(float.class,new TypeNode("decimal(?)","10,0"));

        register(String.class,new TypeNode("varchar(?)","255"));

        register(Date.class,new TypeNode("datetime",""));
    }

    public static  TypeNode getJDBCNode(Class javaType){ return jdbcTypeRegister.getOrDefault(javaType,DEFAULT_NODE); }

    private static void register(Class javaType, TypeNode jdbcNode){jdbcTypeRegister.put(javaType, jdbcNode);}
}
