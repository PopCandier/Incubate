package com.pop.ibatis.shardingibatis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pop.ibatis.shardingibatis.entity.Order;
import com.pop.ibatis.shardingibatis.mapper.OrderMapper;
import com.pop.ibatis.shardingibatis.service.OrderService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Pop
 * @date 2020/2/1 22:38
 *
 */
public class OrderServiceImp extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Transactional
    public void test(){}
}
