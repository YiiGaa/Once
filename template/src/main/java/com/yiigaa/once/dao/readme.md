# Service 模块：DataBaseDao

## 1 模块功能简述

DataBaseDao模块是想通过moduleParam和DataBaseForm做到自动生成sql语句，达到查询数据库的目的。主要功能包括：
- 插入数据
- 删除数据
- 更新数据
- 查找数据

## 2 Christmas 引入例子

- 插入数据操作

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[
                                {"control":"insert"},
                                {"form":"t_test"}
                            ]
                        }
```
- 批量插入数据操作

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[
                                {"control":"batchInsert"},
                                {"targetKey":"target"},
                                {"form":"t_test"}
                            ]
                        }
```
- 删除数据操作

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[
                                {"control":"delete"},
                                {"form":"t_test"},
                                {"filter":""}
                            ]
                        }
```

- 更新数据操作

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[
                                {"control":"update"},
                                {"form":"t_test"},
                               	{"filter":""}
                            ]
                        }
```

- 查找数据操作

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[
                                {"control":"select"},
                                {"form":"t_test"},
                                {"filter":""},
                                {"isNullError":"false"},
                                {"isNotNullError":"false"},
                                {"isAsResult":"false"},
                                {"isAllGet":"false"},
                                {"isOnlyCount":"false"},
                                {"isOnlyList":"false"},
                                {"isSaveSession":"false"}
                            ]
                        }
```

## 3 参数配置

生产环境在/config/application.properties文件中配置

本地环境在/config/application-local.properties文件中配置

| 参数                                | 必要 | 默认参数 | 说明                                               |
| ----------------------------------- | ---- | -------- | -------------------------------------------------- |
| spring.datasource.url               | 是   |          | 数据库连接url                                      |
| spring.datasource.username          | 是   |          | 数据库用户名                                       |
| spring.datasource.password          | 是   |          | 数据库密码                                         |
| spring.datasource.driver-class-name | 是   |          | 数据库操作的Class，Mysql为com.mysql.cj.jdbc.Driver |
| spring.datasource.type              | 否   |          | 连接池的Class                                      |
| spring.datasource.max-active        | 否   |          | 最大活跃线程数                                     |
| spring.datasource.max-idle          | 否   |          | 最大空闲线程数                                     |
| spring.datasource.min-idle          | 否   |          | 最小空闲线程数                                     |
| spring.datasource.initial-size      | 否   |          | 初始连接池线程个数                                 |

## 4 moduleParam参数说明

#### 架构数据池说明：
- passParam：中间参数，所有模块共享的数据池，API请求时传过来的参数在一开始也会放在里面
- moduleParam：模块独有参数，模块调用前传入
- sessionSave：准备存进session的数据池，所有模块共享的数据池，需要之后调用sessionSave模块才能把数据存进session
- returnParam：接口返回的数据池，所有模块共享的数据池，存进去后，最后会自动返回给请求端
- httpRequest：HttpServletRequest，接口访问时的原始数据对象


#### 参数类型说明：

- string：普通的字符串
- stringX：超级字符串，可以添加多个@xx@在里面，模块会自动把passParam中xx字段的值替换到这个字符串中。如，moduleParam中有{"abc":"@@userid@@"}，模块会自动把passParam中userid字段的值替换进去，如果passParam中不存在userid即不替换。
- bool：bool型，true/false
- int：int型，数字类型
- String[]:数组,#xx#包裹，会将passParam里指定的xx替换成数组形式的值,拼接成 in('id1','id2')形式的子查询语句
- value[]:批量插入的value数组,#'@xx@,'@aa@'#。将passParam中的target进行循环遍历,替换##里面的@@值。用于query形式的批量插入


`注意：`moduleParam的参数类型是指参数的真实类型，比如isNotNullError的类型为bool，只有true和false，在填入Christmas时，依然以字符串形式（如：{"isNotNullError":"true"}）写入
### 4.0 通用参数

| 参数    | 必要 | 类型   | 缺省值 | 前提条件 | 说明                                                      |
| ------- | ---- | ------ | ------ | -------- | --------------------------------------------------------- |
| control | 是   | string |        |          | 目标操作，包括insert（插入）,select（查询）,delete,update |



### 4.1  插入数据的moduleParam（control:insert）

| 参数  | 必要 | 类型    | 取值范围                  | 缺省值 | 前提条件       | 说明                                                         |
| ----- | ---- | ------- | ------------------------- | ------ | -------------- | ------------------------------------------------------------ |
| query | 否   | stringX |                           |        | control:insert | 跳过模块自动构造，直接执行query参数的语句                    |
| form  | 是   | string  | DataBaseForm.java中的表名 |        | control:insert | 模块会根据form的值在DataBaseForm.java中查询数据表的字段，根据字段自动从passParam中取出数据插入数据表中。如果存在query，此字段无效。 |
| isReplace | 否   | bool  |                           |        | control:insert | 是否被把SQL的Insert替换成replace，query存在时无效                   |

### 4.2  删除数据的moduleParam（control:delete）

| 参数   | 必要 | 类型    | 取值范围                  | 缺省值 | 前提条件       | 说明                                                         |
| ------ | ---- | ------- | ------------------------- | ------ | -------------- | ------------------------------------------------------------ |
| query  | 否   | stringX |                           |        | control:delete | 跳过模块自动构造，直接执行query参数的语句                    |
| filter | 否   | stringX |                           |        | control:delete | 跳过模块自动构造的where删除条件。如果存在query，此字段无效。 |
| form   | 是   | string  | DataBaseForm.java中的表名 |        | control:delete | 模块会根据form的值在DataBaseForm.java中查询数据表的主键，根据主键和passParam中对应主键的值筛选删除的数据。如果存在query，此字段无效。 |

### 4.3  改数据的moduleParam（control:update）

| 参数   | 必要 | 类型    | 取值范围                  | 缺省值 | 前提条件       | 说明                                                         |
| ------ | ---- | ------- | ------------------------- | ------ | -------------- | ------------------------------------------------------------ |
| query  | 否   | stringX |                           |        | control:update | 跳过模块自动构造，直接执行query参数的语句                    |
| filter | 否   | stringX |                           |        | control:update | 跳过模块自动构造的where删除条件。如果存在query，此字段无效。 |
| form   | 是   | string  | DataBaseForm.java中的表名 |        | control:update | 模块会根据form的值在DataBaseForm.java中查询数据表的字段，根据字段自动从passParam中取出数据更新到数据表中，根据主键和passParam中对应主键的值筛选需要更新的数据。如果存在query，此字段无效。 |

### 4.4  查数据的moduleParam（control:select）

| 参数           | 必要 | 类型    | 取值范围                                                 | 缺省值     | 前提条件       | 说明                                                         |
| -------------- | ---- | ------- | -------------------------------------------------------- | ---------- | -------------- | ------------------------------------------------------------ |
| query          | 否   | stringX |                                                          |            | control:select | 跳过模块自动构造，直接执行query参数的语句                    |
| filter         | 否   | stringX |                                                          |            | control:select | 跳过模块自动构造的where删除条件。如果存在query，此字段无效。 |
| form           | 是   | string  | DataBaseForm.java中的表名，可填多个表，如t_test1,t_test2 |            | control:select | 模块会根据form的值在DataBaseForm.java中查询数据表的字段，根据字段自动从passParam中取出数据更新到数据表中，根据主键和passParam中对应主键的值筛选需要更新的数据。如果存在query，此字段无效。 |
| page           | 否   | int     |                                                          | 1          | control:select | 分页第几页，当moduleParam该参数不存在时，会在passParam中尝试获取，即可以接受API传参 |
| per_page       | 否   | int     |                                                          | 1          | control:select | 设置每页有多少个数据，当moduleParam该参数不存在时，会在passParam中尝试获取，即可以接受API传参 |
| isNullError    | 否   | bool    |                                                          | false      | control:select | 查询不到会报错，中断后续模块操作                             |
| isNotNullError | 否   | bool    |                                                          | false      | control:select | 查询到会报错，中断后续模块操作                               |
| isAsResult     | 否   | bool    |                                                          | false      | control:select | 值为true时把结果写到returnParam，默然输出查到的总个数和列表。                                       值为false时，把结果写到passParam。值为false时，当resultKey为不存在值时，默认把第一条数据按字段全插到passParam里，如果resultKey存在值，则把查到的总个数和列表插到passParam的resultKey对应字段，详情见下面例子。需要在DataBaseForm.java中设置允许被存到return的字段（is can as result 配置为true） |
| isAllGet       | 否   | bool    |                                                          | false      | control:select | 全部获取，不分页。值为true时，page和per_page无效             |
| isOnlyCount    | 否   | bool    |                                                          | false      | control:select | 只获取个数，个数的字段为count                                |
| isOnlyList     | 否   | bool    |                                                          | false      | control:select | 只获取列表，列表的字段为resultList                           |
| isSaveSession  | 否   | bool    |                                                          | false      | control:select | 值为true时把结果写到sessionSave，之后需要调用sessionSave模块写进session。需要在DataBaseForm.java中设置允许被存到session的字段（is can save session 配置为true）才能被存进去 |
| resultKey      | 否   | string  |                                                          | dataResult | control:select | 结果字段                                                     |
| order          | 否   | string  |                                                          |            | control:select | 同sql中的order写法，当moduleParam该参数不存在时，会在passParam中尝试获取，即可以接受API传参 |
| countKey       | 否   | string  |                                                          | dataCount           | control:select,isAsResult:false | passParam记录查询个数的key |

### 4.5  批量插入数据的moduleParam（control:batchInsert）

| 参数  | 必要 | 类型    | 取值范围                  | 缺省值 | 前提条件       | 说明                                                         |
| ----- | ---- | ------- | ------------------------- | ------ | -------------- | ------------------------------------------------------------ |
| query | 否   | stringX |                           |        | control:insert | 跳过模块自动构造，直接执行query参数的语句                    |
| form  | 是   | string  | DataBaseForm.java中的表名 |        | control:insert | 模块会根据form的值在DataBaseForm.java中查询数据表的字段，根据字段自动从passParam中取出数据插入数据表中。如果存在query，此字段无效。 |
|targetKey|是  |String   |                         |         |control:batchInsert|指定解析的KEY,从passParam传过来的JSON对象数组，从中取解析数据
```
 {
              "templ": "MODULE-DAO",
              "param_templ": "PARAM-Hash",
              "param": [
                {
                  "control": "batchInsert"
                },
                {
                  "query": "insert t_test(test_id,test_name,test_ser) values#'@test_id@','@test_name@','@test_ser@'#"
                },
                {
                  "targetKey": "target"
                }
              ]
            }
```
#### resultKey缺省，isAsResult为true，returnParam中返回结果例子
```
{
	“dataResult”：{
		"count":6,
		"dataResult":[
            {"id":"xxx","name":"xx"}
		]
​	}
}
```
#### resultKey为abc，isAsResult为true，returnParam中返回结果例子
```
{
	“abc”：{
		"count":6,
		"dataResult":[
            {"id":"xxx","name":"xx"}
		]
​	}
}
```
#### resultKey缺省，isAsResult为false，passParam中返回结果例子
```
{
    "id":"xxx",
    "name":"xx"
}
```
#### resultKey为abc，isAsResult为false，passParam中返回结果例子
```
{
	“abc”：{
		"count":6,
		"dataResult":[
            {"id":"xxx","name":"xx"}
		]
​	}
}
```

## 5 部署依赖

mysql数据库，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见DataBaseDao.java中的GradleImport，一般情况下不要修改

## 7 其他说明

### DataBaseForm.java说明
```
public class DataBaseForm {
	//工程表
​    static private HashMap<String, Boolean[]> t_test = new HashMap<String, Boolean[]>(){{
​        put("test_id",new Boolean[]{ true, true, false, false });
​        put("test_name",new Boolean[]{ false, true, false, false });
​    }};

​    static public HashMap<String, HashMap<String, Boolean[]>> fromMaps= new HashMap<String, HashMap<String, Boolean[]>>(){{
​        put("t_test", t_test);
​    }};
}
```

- t_test：表名，与数据库相同，引入表时，
- test_id，test_name：字段名，两个表中的字段名字相同时会被认作外键，当操作为select，form为多个表时，会根据同名字段做关联
- { true, true, false, false }：字段属性

  1. 第一个bool（is primary key）：是否是主键，更新和删除数据时，会自动从passParam中取出值作为筛选条件
  2. 第二个bool（is can as result）：是否允许被当做结果。查询时，当isAsResult为true时，这个值为true的字段才能被存进returnParam里
  3. 第三个bool（is can save session）：是否允许存进session。查询时，当isSaveSession为true时，这个值为true的字段才能被存进sessionSave里
  4. 第四个bool（is insert ''）：插入数据时，当该字段的数据为空，是否用''（空字符串）代替null



### 新增表例子

比如需要新增一个表：字段是abc_id（主键），test_name，表名是t_abc的

```
public class DataBaseForm {
	//工程表
​    static private HashMap<String, Boolean[]> t_test = new HashMap<String, Boolean[]>(){{
​        put("test_id",new Boolean[]{ true, true, false, false });
​        put("test_name",new Boolean[]{ false, true, false, false });
​    }};

	static private HashMap<String, Boolean[]> t_abc = new HashMap<String, Boolean[]>(){{
​        put("abc_id",new Boolean[]{ true, true, false, false });
​        put("test_name",new Boolean[]{ false, true, false, false });
​    }};

​    static public HashMap<String, HashMap<String, Boolean[]>> fromMaps= new HashMap<String, HashMap<String, Boolean[]>>(){{
​        put("t_test", t_test);
		put("t_abc", t_abc);
​    }};
}
```

fromMaps这个Hash表的key对应的是moduleParam中的form字段

