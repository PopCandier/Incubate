package com.pop.ibatis.core.rout;

import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.text.DateFormat;
import java.util.Date;

/**
 * @author Pop
 * @date 2020/2/3 19:24
 *
 * 通过日期路由的规则
 */
public class ShardingDateRoutRule extends AbstractSharingRoutRule<Date> {

    @Override
    public String rule(Date date) {
        DateFormat dateFormat= DateFormat.getDateInstance();
        return dateFormat.format(date).replace("-","_");
    }

}
