package com.pop.ibatis.builder;

import com.pop.ibatis.annotations.ShardingIgnoreField;
import com.pop.ibatis.annotations.ShardingTable;
import com.pop.ibatis.annotations.ShardingTableField;
import com.pop.ibatis.type.ShardingTypeHandler;
import com.pop.ibatis.type.TypeNode;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pop
 * @date 2020/2/4 23:30
 */
public class CreateTableBuilder {

    /**
     * 用于insert 语句创建不存在表的时候<br/>
     * 用于记录塞入实体对象的方法列表的缓存
     */
    private static Map<Class<?>,Field[]> reflectEntityFieldsCache = new ConcurrentHashMap<>();
    private static final String PREFIX_ = "CREATE TABLE IF NOT EXISTS";
    private static final String DEFAULT_NULL = "DEFAULT NULL";
    private static final String NOT_NULL = "NOT NULL";
    private static final String COMMA = ",";
    private static final String QUO_ = "'";
    private static final String SEM = ");";
    private static final String HOLDER = "?";
    private static final String SPACE = " ";
    private static Pattern pattern = Pattern.compile("(\\s[a-zA-Z_][(])");

    public static String build(Class clazz,String tableName){

        //取出表名 （由于 正则表达式 编写不够，所以tableName这时为 insert into tableName(）
        Matcher matcher=pattern.matcher(tableName);
        String realTableName = "";
        if(matcher.find()){
            realTableName = matcher.group();// tableName(
        }
        StringBuilder findSql = new StringBuilder(PREFIX_).append(realTableName).append(SPACE);
        //忽略字段的配置
        // 从实体对象
        Field[] fields=reflectEntityFieldsCache.computeIfAbsent(clazz,c->c.getDeclaredFields());
        for (Field field:fields) {
            if(Objects.isNull(field.getAnnotation(ShardingIgnoreField.class))){continue;}
            //字段的映射
            TypeNode node=ShardingTypeHandler.getJDBCNode(field.getType());
            //这有有个驼峰转换法的问题 orderId -> order_id todo 需要解决
            findSql.append(convertName(field.getName())).append(SPACE);
            findSql.append(node.getFieldName().replace(HOLDER,node.getValue())).append(SPACE);// todo 如果用户设置了默认值需要额外处理

            //数据库的备注说明
            ShardingTableField shardingTableField=field.getAnnotation(ShardingTableField.class);
            if(Objects.isNull(shardingTableField)){continue;}
            //**************** 默认值
            if(shardingTableField.isNull()){
                findSql.append(DEFAULT_NULL).append(SPACE);
            }else{
                findSql.append(NOT_NULL).append(SPACE);
            }
            //****************
            //注解
            findSql.append(QUO_).append(shardingTableField.comment()).append(QUO_).append(COMMA);


            //主键另外算 还有其他二级索引，唯一索引,联合索引的配置

        }
        //去掉最后一个逗号
        return findSql.substring(0,findSql.lastIndexOf(COMMA))+SEM;
    }

    /**
     * 驼峰转换
     * @param target
     * @return
     */
    private static String convertName(String target){
        return "";
    }

}
