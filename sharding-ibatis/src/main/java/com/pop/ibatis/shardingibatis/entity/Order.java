package com.pop.ibatis.shardingibatis.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Pop
 * @date 2020/2/1 22:29
 *
 * 订单对象
 */
@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 1180708624961431032L;
    //订单编号
    private Integer orderId;
    //订单名称
    private String orderName;
    //订单创建时间
    private Date orderCreatetime;
    //订单总金额
    private Double orderCost;

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderName='" + orderName + '\'' +
                ", orderCreatetime=" + orderCreatetime +
                ", orderCost=" + orderCost +
                '}';
    }
}
