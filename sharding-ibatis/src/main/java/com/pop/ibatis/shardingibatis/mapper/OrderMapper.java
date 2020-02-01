package com.pop.ibatis.shardingibatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pop.ibatis.annotations.Sharding;
import com.pop.ibatis.annotations.enums.ShardingType;
import com.pop.ibatis.shardingibatis.entity.Order;
import org.apache.ibatis.annotations.Param;

/**
 * @author Pop
 * @date 2020/2/1 22:30
 */

public interface OrderMapper extends BaseMapper<Order> {

    @Sharding(type = ShardingType.TABLE,rule = "")
    Order queryById(@Param("order_id") int id);
}
