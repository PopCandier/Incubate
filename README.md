#### Pop 的 孵化室

将会编写多个中间件的不同场景的解决方案。





##### MyBatis用于分库分表的插件，Sharding-iBatis

该插件用于继承与spring-boot项目，目前可用于简单的分库分表

```java

```

注解和mybatis的插件联合使用，注解放在需要路由的方法上，插件经过时尝试获取方法，然后获得值。适用crud

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Sharding {
    /**
     * 路由规则
     * 默认的实现按照主键取模
     * @return 具体的路由实现 class 全路径
     */
    String rule() default "";

    /**
     * 具体是分库还是分表
     * 默认分表
     * @return
     */
    ShardingType type() default ShardingType.TABLE;

}
```

```java
public interface OrderMapper extends BaseMapper<Order> {

    @Sharding(type = ShardingType.TABLE,rule = "")
    Order queryById(@Param("order_id") int id);
}
```

#### 数据库的关键字如何生成？

配置选项里面增加数据库关键字的生成规则

* 可以自定义
* 可以默认（默认就是按照加入list的顺序进行路由）

默认就是添加数据库名列表。

如果数据库的names没有配置，默认是url里面的库名字（有待完成）

#### 分表的实现

* 查询（query）：

  根据生成的key来具体路由到哪个表，如果找不到指定的表，就默认是原来的表

* 更新 （update ，delete，insert）

  * 对于删除而言，要注意具体的删除会落到哪个表上否则会出现找不到的情况
  * 对于update与insert，也要区别路由到具体表的规则，这个规则差不多是前缀是表名后面是具体的规则生成的名字例如，`user_info_#具体规则生成的key`
    * 此外，当表不存在的时候，应该创建表

路由接口`ShardingRoutRule`用于将mybatis的原型放入接口，可更加某些参数进行生成

日期路由的规则，一般是实体类中的日期字段作为依据



目前insert 按照某个规范分表已经实现，但是由于重新构建了sqlSource，可能导致主键获取有些问题，无法自增长主键。



```java
@Sharding(rule = {
            @ShardingRule(type = ShardingType.TABLE, routRule = ShardingDateRoutRule.class,
                    fieldName = "orderCreatetime",fromEntity = true,fromTemplate = false)
    },baseKey = "ibatis1")
    int save(Order order1);
```

假设这是你所使用的注解。