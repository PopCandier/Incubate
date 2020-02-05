package com.pop.ibatis.shardingibatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pop.ibatis.annotations.Sharding;
import com.pop.ibatis.annotations.ShardingFieldFey;
import com.pop.ibatis.annotations.ShardingRule;
import com.pop.ibatis.annotations.enums.ShardingType;
import com.pop.ibatis.core.rout.ShardingDateRoutRule;
import com.pop.ibatis.shardingibatis.entity.Order;
import org.apache.ibatis.annotations.Param;

/**
 * @author Pop
 * @date 2020/2/1 22:30
 */

public interface OrderMapper extends BaseMapper<Order> {


    @Sharding(rule = {
            @ShardingRule(type = ShardingType.TABLE, routRule = ShardingDateRoutRule.class, fieldName = "orderCreatetime")
    },baseKey = "ibatis1")
    Order queryById(@Param("order_id") int id);//_#2020_2_3


    @Sharding(rule = {
            @ShardingRule(type = ShardingType.TABLE, routRule = ShardingDateRoutRule.class,
                    fieldName = "orderCreatetime",fromEntity = true,fromTemplate = false)
    },baseKey = "ibatis1")
    int save(Order order1);
}
