package com.pop.ibatis.config;

import com.pop.ibatis.core.interceptor.ShardingInterceptor;
import com.pop.ibatis.datasource.ShardingDataSource;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author Pop
 * @date 2020/2/2 17:09
 */
@Configuration
public class MyBatisConfig {

    @Bean
    public ShardingInterceptor shardingInterceptor(ShardingDataSource shardingDataSource){
        ShardingInterceptor shardingInterceptor = new ShardingInterceptor(shardingDataSource);
        Properties properties = new Properties();
        shardingInterceptor.setProperties(properties);
        return shardingInterceptor;
    }



    @ConfigurationProperties("ibatis.sharding")
    @Bean
    public ShardingDataConfig shardingDataConfig(){ return new ShardingDataConfig(); }


    @Bean
    public ShardingDataSource shardingDataSource(ShardingDataConfig config){
        return new ShardingDataSource(config);
    }


}
