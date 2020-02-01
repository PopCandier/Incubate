package com.pop.ibatis.annotations.enums;

/**
 * @author Pop
 * @date 2020/2/1 22:14
 *
 * 具体是分库还是分表
 *
 */
public enum ShardingType {

    BASE, //库
    TABLE; // 表
}
