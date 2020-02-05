package com.pop.ibatis.shardingibatis.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pop.ibatis.annotations.ShardingIgnoreField;
import com.pop.ibatis.annotations.ShardingTable;
import com.pop.ibatis.annotations.ShardingTableField;
import com.pop.ibatis.annotations.ShardingTableID;
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
@ShardingTable(name="order_info")
public class Order implements Serializable {

    @ShardingIgnoreField
    private static final long serialVersionUID = 1180708624961431032L;
    //订单编号
    @ShardingTableID
    private Integer orderId;
    //订单名称
    @ShardingTableField(comment = "订单名称",isNull = false,index = "idx_name")
    private String orderName;

    @ShardingTableField(comment = "订单名称地址")
    private String orderNameAddress;
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
