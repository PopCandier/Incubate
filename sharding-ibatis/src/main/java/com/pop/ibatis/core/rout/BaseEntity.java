package com.pop.ibatis.core.rout;

import lombok.Data;

import java.util.Date;

/**
 * @author Pop
 * @date 2020/2/3 19:14
 *
 * 默认对象，用于生成最基本的路由规则实体
 */
@Data
public class BaseEntity {
    private int id;
    private Date time;
}
