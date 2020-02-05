package com.pop.ibatis.builder;

import com.pop.ibatis.annotations.ShardingIgnoreField;
import com.pop.ibatis.annotations.ShardingTable;
import com.pop.ibatis.annotations.ShardingTableField;
import com.pop.ibatis.annotations.ShardingTableID;
import com.pop.ibatis.type.ShardingTypeHandler;
import com.pop.ibatis.type.TypeNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pop
 * @date 2020/2/4 23:30
 */
@Slf4j
public class CreateTableBuilder {

    /**
     * 用于insert 语句创建不存在表的时候<br/>
     * 用于记录塞入实体对象的方法列表的缓存
     */
    private static Map<Class<?>,Field[]> reflectEntityFieldsCache = new ConcurrentHashMap<>();
    private static final String PREFIX_ = "CREATE TABLE IF NOT EXISTS ";
    private static final String AUTO_INCREMENT = "AUTO_INCREMENT ";
    private static final String COMMENT = "COMMENT ";
    private static final String DEFAULT_NULL = "DEFAULT NULL ";
    private static final String NOT_NULL = "NOT NULL ";
    private static final String KEY = "KEY";
    private static final String LIKE = "LIKE";
    private static final String PRIMARY_KEY = "PRIMARY KEY ";
    private static final String UNIQUE_KEY = "UNIQUE KEY ";
    private static final String USING_BTREE = "USING BTREE";
    private static final String RH = "(";
    private static final String MARK = "(?)";
    private static final String COMMA = ",";
    private static final String QUO_ = "'";
    private static final String SEMI = ";";
    private static final String SEM = "));";
    private static final String HOLDER = "?";
    private static final String UNDERLINE = "_";
    private static final String SPACE = " ";
    private static final String UID = "serialVersionUID";
    private static Pattern pattern = Pattern.compile("(\\s[a-zA-Z_$0-9]+[(])");
    private static Pattern patternConvert = Pattern.compile("([A-Z]+)");

    public static String build(Class clazz,String tableName){

        //取出表名 （由于 正则表达式 编写不够，所以tableName这时为 insert into tableName(）
        String realTableName = getRealTableName(tableName);
        StringBuilder findSql = new StringBuilder(PREFIX_).append(realTableName).append(RH).append(SPACE);
        //忽略字段的配置
        // 从实体对象
        Field[] fields=reflectEntityFieldsCache.computeIfAbsent(clazz,c->c.getDeclaredFields());
        boolean overID = false;
        ShardingTableID shardingTableID = null;
        Map<IndexKey,Node> indexKeyFieldMap = new HashMap<>();
        for (Field field:fields) {
            if(Objects.nonNull(field.getAnnotation(ShardingIgnoreField.class))){continue;}
            //这有有个驼峰转换法的问题 orderId -> order_id
            String FieldName = field.getName();
            if(UID.equals(FieldName)){continue;}
            //字段的映射
            TypeNode node=ShardingTypeHandler.getJDBCNode(field.getType());
            String changeFieldName = convertName(FieldName);
            findSql.append(changeFieldName).append(SPACE);
            findSql.append(node.getFieldName().replace(HOLDER,node.getValue())).append(SPACE);// todo 如果用户设置了默认值需要额外处理

            //数据库的备注说明
            ShardingTableField shardingTableField=field.getAnnotation(ShardingTableField.class);
            //**************** 默认值 id只访问一次，访问过就不再访问
            if(!overID){
                shardingTableID= field.getAnnotation(ShardingTableID.class);
                findSql.append(NOT_NULL).append(SPACE);
                if(shardingTableID.autoIncrement()){findSql.append(AUTO_INCREMENT).append(SPACE).append(COMMA);}
                indexKeyFieldMap.put(IndexKey.PRIMARY,new Node("",changeFieldName));
                overID = true;
            }else{
                if(Objects.isNull(shardingTableField)){
                    findSql.append(COMMA);
                    continue;
                }else{
                    if(shardingTableField.isNull()){
                        findSql.append(DEFAULT_NULL).append(SPACE);
                    }else{
                        findSql.append(NOT_NULL).append(SPACE);
                    }
                    String uniqueName = shardingTableField.unique();
                    String indexName = shardingTableField.index();
                    if(!StringUtils.isEmpty(uniqueName)){ indexKeyFieldMap.put(IndexKey.UNIQUE,new Node(uniqueName,changeFieldName)); }
                    if(!StringUtils.isEmpty(indexName)){ indexKeyFieldMap.put(IndexKey.NORMAL,new Node(indexName,changeFieldName)); }
                    //****************
                    //注解
                    findSql.append(COMMENT).append(QUO_).append(shardingTableField.comment()).append(QUO_).append(COMMA).append(SPACE);
                }
            }
        }
        //索引添加
        for (Map.Entry<IndexKey,Node> entry: indexKeyFieldMap.entrySet()) {
            switch (entry.getKey()){
                case PRIMARY:
                    findSql.append(PRIMARY_KEY).append(SPACE).append(MARK.replace(HOLDER,entry.getValue().fieldName)).append(COMMA);
                    break;
                case NORMAL:
                    Node node = entry.getValue();
                    findSql.append(KEY).append(SPACE).append(node.idxName).append(SPACE).append(MARK.replace(HOLDER,node.fieldName)).append(SPACE).append(USING_BTREE).append(COMMA);
                    break;
                case UNIQUE:
                    Node node_ = entry.getValue();
                    findSql.append(UNIQUE_KEY).append(node_.idxName).append(SPACE).append(MARK.replace(HOLDER,entry.getValue().fieldName)).append(COMMA);
                    break;
                default:break;
            }
        }
        //去掉最后一个逗号
        return findSql.substring(0,findSql.lastIndexOf(COMMA)-1)+SEM;
    }

    private static String getRealTableName(String target){
        Matcher matcher=pattern.matcher(target);
        String realTableName = "";
        if(matcher.find()){
            realTableName = matcher.group();// tableName(
        }
        return StringUtils.isEmpty(realTableName)?target:realTableName.replace(RH,"");
    }

    public static String buildFromTemplate(Class clazz,String tableName){

        ShardingTable table= (ShardingTable) clazz.getAnnotation(ShardingTable.class);
        if(Objects.isNull(table)){
            log.warn(" 注解: @ShardingTable 不存在，无法按照模版建表。");
            return SPACE;
        }else{
            String templateName = table.name();
            if(StringUtils.isEmpty(templateName)){
                log.warn(" 注解: @ShardingTable 上 name 属性不存在，无法按照模版建表。");
                return SPACE;
            }else{
                StringBuilder findSql = new StringBuilder(PREFIX_).append(getRealTableName(tableName))
                        .append(SPACE).append(LIKE).append(SPACE).append(templateName).append(SEMI);
                return findSql.toString();
            }
        }
    }

    /**
     * 驼峰转换
     * @param target
     * orderId -> order_id
     * 65 -> 97 -> 32
     * @return
     */
    private static String convertName(String target){
        Matcher matcher=patternConvert.matcher(target);
        String convertName=target;
        while (matcher.find()){
            String needConvert = matcher.group();
            convertName = convertName.replace(needConvert,convert(needConvert));
        }
        return StringUtils.isEmpty(convertName)?target:convertName;
    }

    private static String convert(String target){
        char[] chars=target.toCharArray();
        chars[0]+=32;
        return UNDERLINE+new String(chars);
    }


    static class Node{
        public Node(String idxName, String fieldName) {
            this.idxName = idxName;
            this.fieldName = fieldName;
        }
        private String idxName;
        private String fieldName;
    }

    enum IndexKey{
        PRIMARY,UNIQUE,NORMAL
    }
}


