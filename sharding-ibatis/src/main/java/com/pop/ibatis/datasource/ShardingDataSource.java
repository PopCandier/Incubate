package com.pop.ibatis.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.pop.ibatis.config.ShardingDataConfig;
import com.pop.ibatis.core.key.DefaultKeyGenerator;
import com.pop.ibatis.core.key.KeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author Pop
 * @date 2020/2/2 18:15
 */
@Slf4j
public class ShardingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> DATASOURCE_HOLDER = new ThreadLocal<>();

    public ShardingDataSource(ShardingDataConfig config){
        List<DataSource> dataSources = getDataSources(config.getDataSources());
        DataSource mainSource = dataSources.get(0);
        if(mainSource==null){return;}
        super.setDefaultTargetDataSource(mainSource);
        super.setTargetDataSources(parseTargetDataSources(config,dataSources));
        super.afterPropertiesSet();
    }

    private List<DataSource> getDataSources(List<IBatisDataSource> dataSources){
        List<DataSource> sources = new ArrayList<>();
        DruidDataSourceBuilder builder = DruidDataSourceBuilder.create();
        for (IBatisDataSource dataSource:dataSources) {
            DruidDataSource d = builder.build();
            d.setUrl(dataSource.getUrl());
            d.setUsername(dataSource.getUsername());
            d.setPassword(dataSource.getPassword());
            sources.add(d);
        }
        return sources;
    }

    private Map<Object,Object> parseTargetDataSources(ShardingDataConfig config,List<DataSource> dataSources){
        Map<Object,Object> targetDataSources =null;
        Class dataBaseRule = config.getDataBaseRule();
        if(config.getDataBaseRule()==null){
            //没有设置生成键规则
            KeyGenerator keyGenerator = new DefaultKeyGenerator();
            targetDataSources = initTargetDataSources(config,keyGenerator,dataSources);
        }else{
            KeyGenerator keyGenerator = null;
            try {
                keyGenerator = (KeyGenerator) dataBaseRule.newInstance();
            } catch (Exception e) {
                log.error(" 自定义 数据库key生成错误，请检查实现类 : "+dataBaseRule.getName());
            }
            targetDataSources = initTargetDataSources(config,keyGenerator,dataSources);
        }
        return targetDataSources;
    }

    private Map<Object,Object> initTargetDataSources(ShardingDataConfig config,KeyGenerator keyGenerator,List<DataSource> dataSources){
        Map<Object,Object> targetSource = new HashMap<>();
        List<String> keys = keyGenerator.generator(Arrays.asList(config.getDataSourceNames()));
        for (int i = 0,count=dataSources.size(); i <count ; i++) { targetSource.put(keys.get(i),dataSources.get(i)); }
        return targetSource;
    }


    @Override
    protected Object determineCurrentLookupKey() { return getDataSource(); }

    public static void setDataSource(String dataSourceName){DATASOURCE_HOLDER.set(dataSourceName);}
    public static String getDataSource(){return DATASOURCE_HOLDER.get();}
    public static void clearDataSource(){DATASOURCE_HOLDER.remove();}
}
