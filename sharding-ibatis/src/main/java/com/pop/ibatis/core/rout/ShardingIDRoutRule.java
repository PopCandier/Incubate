package com.pop.ibatis.core.rout;

/**
 * @author Pop
 * @date 2020/2/3 19:23
 *
 * 通过主键路由的规则
 */
public class ShardingIDRoutRule extends AbstractSharingRoutRule<BaseEntity> {

    @Override
    public String rule(BaseEntity baseEntity) {
        return null;
    }
}
