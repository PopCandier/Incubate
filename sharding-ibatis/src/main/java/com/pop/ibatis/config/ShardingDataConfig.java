package com.pop.ibatis.config;

import com.pop.ibatis.datasource.IBatisDataSource;
import lombok.Data;

import java.util.List;

/**
 * @author Pop
 * @date 2020/2/2 19:07
 */
@Data
public class ShardingDataConfig {
    private int sourcesCount=0;
    private String[] dataSourceNames;
    private List<IBatisDataSource> dataSources;
    /**
     * 自己定义的库名键值生成策略
     */
    private Class dataBaseRule;
}
