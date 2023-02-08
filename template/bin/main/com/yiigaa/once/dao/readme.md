# Service 模块：Dao

## 模块功能简述

Dao的主要功能包括：

> 从passParam获取数据的更新到数据库，从数据库获取数据到returnParam、passParam。

- 插入数据
- 插入数据（批量）
- 删除数据
- 更新数据
- 查找数据


| 模块名        | 最新版本 | 模块位置 | 依赖服务 |
| ------------- | -------- | -------- | -------- |
| Dao | v1.0     | Service  | 无       |

## 下载模块

dao模块初始工程自带，无需下载。

## Christmas 使用例子

使用此模块，在/Christmas/Input/MakeCodeNormal/Service@xxx/target.json中使用。

- 插入数据

```
												{
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                                "control":"insert",
                                "query":"INSERT INTO t_test (test_id, test_name) VALUES ('@test_id@', '@test_name@')"
                            }]
                        }
```

- 插入数据（批量）

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                                "control":"batchInsert",
                                "targetKey":"temp_insert",
                                "query":"INSERT INTO t_test (test_id, test_name) VALUES #'@test_id@', '@test_name@'#"
                            }]
                        }
```

> 批量插入的passParam数据结构示例如下

  ```
{
    "temp_insert":[
        {"test_id":"1", "test_name":"2"},
        {"test_id":"2", "test_name":"3"}
    ]
}
  ```

- 删除数据

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                                "control":"delete",
                                "query":"DELETE t_test WHERE test_id = '@test_id@'"
                            }]
                        }
```

- 更新数据

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                                "control":"delete",
                                "query":"UPDATE t_test SET test_name='@test_name@' WHERE test_id='@test_id@'"
                            }]
                        }
```

- 查询数据到returnParam（返回数据）

> 第几页由passParam中的page字段决定

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                                "control":"select",
                                "isAsResult":"true",
                                "per_page":"20",
                                "query":"SELECT * t_test WHERE test_id = '@test_id@'"
                            }]
                        }
```

- 查询数据到passParam（不返回数据，下一步使用）

  > 只获取一条数据，并把数据填充到passParam。以下例子中查询到的test_id、test_name值会放到passParam中。

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                                "control":"select",
                                "query":"SELECT * t_test WHERE test_id = '@test_id@'"
                            }]
                        }
```

## 配置文件设置

| 参数 | 必要 | 缺省值 | 说明 |
| ---- | ---- | ------ | ---- |
|      |      |        |      |

配置文件配置项在FillingParams/Templates.tmpl中，修改配置后，需要重新[连接模块](/#content@content#framework/once/module-link)才能让配置生效。

- 生产环境的配置在propertiesTemplate中，连接模块后，会自动生成到/config/application.properties文件中；
- 开发环境的配置在propertiesTemplate local中，连接模块后，会自动生成到/config/application-local.properties文件中；

## moduleParam参数说明

### 通用参数

| 参数    | 必要 | 类型   | 缺省值 | 前提条件 | 说明                                                         |
| ------- | ---- | ------ | ------ | -------- | ------------------------------------------------------------ |
| control | 是   | String |        |          | 目标操作，包括insert（插入），batchInsert（插入），select（查询），delete（删除），update（更新） |

### 插入数据的moduleParam（control:insert）

| 参数      | 必要 | 类型    | 取值范围                  | 缺省值 | 前提条件       | 说明                                                         |
| --------- | ---- | ------- | ------------------------- | ------ | -------------- | ------------------------------------------------------------ |
| query     | 否   | StringX |                           |        | control:insert | 跳过模块自动构造，直接执行query参数的语句                    |
| form      | 否   | String  | DataBaseForm.java中的表名 |        | control:insert | 模块会根据form的值在DataBaseForm.java中查询数据表的字段，根据字段自动从passParam中取出数据插入数据表中。如果存在query，此字段无效。 |
| isReplace | 否   | Bool    |                           |        | control:insert | 是否被把SQL的Insert替换成replace，query存在时无效            |

### 删除数据的moduleParam（control:delete）

| 参数   | 必要 | 类型    | 取值范围                  | 缺省值 | 前提条件       | 说明                                                         |
| ------ | ---- | ------- | ------------------------- | ------ | -------------- | ------------------------------------------------------------ |
| query  | 否   | StringX |                           |        | control:delete | 跳过模块自动构造，直接执行query参数的语句                    |
| filter | 否   | StringX |                           |        | control:delete | 跳过模块自动构造的where删除条件。如果存在query，此字段无效。 |
| form   | 否   | String  | DataBaseForm.java中的表名 |        | control:delete | 模块会根据form的值在DataBaseForm.java中查询数据表的主键，根据主键和passParam中对应主键的值筛选删除的数据。如果存在query，此字段无效。 |

### 改数据的moduleParam（control:update）

| 参数   | 必要 | 类型    | 取值范围                  | 缺省值 | 前提条件       | 说明                                                         |
| ------ | ---- | ------- | ------------------------- | ------ | -------------- | ------------------------------------------------------------ |
| query  | 否   | StringX |                           |        | control:update | 跳过模块自动构造，直接执行query参数的语句                    |
| filter | 否   | StringX |                           |        | control:update | 跳过模块自动构造的where删除条件。如果存在query，此字段无效。 |
| form   | 否   | String  | DataBaseForm.java中的表名 |        | control:update | 模块会根据form的值在DataBaseForm.java中查询数据表的字段，根据字段自动从passParam中取出数据更新到数据表中，根据主键和passParam中对应主键的值筛选需要更新的数据。如果存在query，此字段无效。 |

### 查数据的moduleParam（control:select）

| 参数           | 必要 | 类型    | 取值范围                                                 | 缺省值     | 前提条件                        | 说明                                                         |
| -------------- | ---- | ------- | -------------------------------------------------------- | ---------- | ------------------------------- | ------------------------------------------------------------ |
| query          | 否   | StringX |                                                          |            | control:select                  | 跳过模块自动构造，直接执行query参数的语句                    |
| filter         | 否   | StringX |                                                          |            | control:select                  | 跳过模块自动构造的where删除条件。如果存在query，此字段无效。 |
| form           | 是   | String  | DataBaseForm.java中的表名，可填多个表，如t_test1,t_test2 |            | control:select                  | 模块会根据form的值在DataBaseForm.java中查询数据表的字段，根据字段自动从passParam中取出数据更新到数据表中，根据主键和passParam中对应主键的值筛选需要更新的数据。如果存在query，此字段无效。 |
| page           | 否   | Int     |                                                          | 1          | control:select                  | 分页第几页，会在passParam中尝试获取，即可以接受API传参。此值moduleParam传入无效。 |
| per_page       | 否   | Int     |                                                          | 1          | control:select                  | 设置每页有多少个数据，会在passParam和moduleParam中获取此字段的值，如果两个数据池都存在，passParam中的值优先级比moduleParam高 |
| isNullError    | 否   | bool    |                                                          | false      | control:select                  | 查询不到会报错，中断后续模块操作                             |
| isNotNullError | 否   | bool    |                                                          | false      | control:select                  | 查询到会报错，中断后续模块操作                               |
| isAsResult     | 否   | bool    |                                                          | false      | control:select                  | 值为true时把结果写到returnParam，默然输出查到的总个数和列表。                                       值为false时，把结果写到passParam。值为false时，当resultKey为不存在值时，默认把第一条数据按字段全插到passParam里，如果resultKey存在值，则把查到的总个数和列表插到passParam的resultKey对应字段，详情见下面例子。需要在DataBaseForm.java中设置允许被存到return的字段（is can as result 配置为true） |
| isAllGet       | 否   | bool    |                                                          | false      | control:select                  | 全部获取，不分页。值为true时，page和per_page无效             |
| isOnlyCount    | 否   | bool    |                                                          | false      | control:select                  | 只获取个数，个数的字段为count                                |
| isOnlyList     | 否   | bool    |                                                          | false      | control:select                  | 只获取列表，列表的字段为resultList                           |
| isSaveSession  | 否   | bool    |                                                          | false      | control:select                  | 值为true时把结果写到sessionSave，之后需要调用sessionSave模块写进session。需要在DataBaseForm.java中设置允许被存到session的字段（is can save session 配置为true）才能被存进去 |
| resultKey      | 否   | String  |                                                          | dataResult | control:select                  | 结果字段                                                     |
| order          | 否   | String  |                                                          |            | control:select                  | 同sql中的order写法，当moduleParam该参数不存在时，会在passParam中尝试获取，即可以接受API传参 |
| countKey       | 否   | String  |                                                          | dataCount  | control:select,isAsResult:false | passParam记录查询个数的key                                   |

### 批量插入数据的moduleParam（control:batchInsert）

| 参数                                                         | 必要 | 类型    | 取值范围                  | 缺省值 | 前提条件            | 说明                                                         |
| ------------------------------------------------------------ | ---- | ------- | ------------------------- | ------ | ------------------- | ------------------------------------------------------------ |
| query                                                        | 否   | StringX |                           |        | control:batchInsert | 跳过模块自动构造，直接执行query参数的语句                    |
| form                                                         | 否   | String  | DataBaseForm.java中的表名 |        | control:batchInsert | 模块会根据form的值在DataBaseForm.java中查询数据表的字段，根据字段自动从passParam中取出数据插入数据表中。如果存在query，此字段无效。 |
| targetKey                                                    | 是   | String  |                           |        | control:batchInsert | 指定解析的KEY,从passParam传过来的JSON对象数组，从中取解析数据 |
| 当操作为批量操作，并使用query字段时，需要用#号标注数据部分，例子如下 |      |         |                           |        |                     |                                                              |

```
                        {
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                                "control":"batchInsert",
                                "targetKey":"temp_insert",
                                "query":"INSERT INTO t_test (test_id, test_name) VALUES #'@test_id@', '@test_name@'#"
                            }]
                        }
```

### 数据查询说明

#### resultKey缺省，isAsResult为true，returnParam中返回结果例子

```
{
    “dataResult”：{
        "count":6,
        "dataResult":[
             {"id":"xxx","name":"xx"}
        ]
   }
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
   }
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
    "abc"：{
      "count":6,
      "dataResult":[
              {"id":"xxx","name":"xx"}
      ]
   }
}
```

#### 模块参数参数类型说明：

- String：普通的字符串
- StringX：超级字符串，可以添加多个@xx@在里面，模块会自动把passParam中xx字段的值替换到这个字符串中。如，moduleParam中有{"abc":"@userid@"}，模块会自动把passParam中userid字段的值替换进去，如果passParam中不存在userid则保留原状。
- Bool：Bool型，true/false
- Int：Int型，数字类型

>注意：moduleParam的参数类型是指参数的真实类型，比如isNotNullError的类型为bool（true/false），但是在填入Christmas时，依然以字符串形式（如：{"isNotNullError":"true"}）写入，另外，int类型也需要写成字符串的形式如（如：{"maxFileSize":"20000"}）。

#### Once架构数据池说明：

- passParam：中间参数，所有模块共享的数据池，API请求时传过来的参数在会自动放在里面；
- moduleParam：模块独有参数，模块调用时传入，Christmas使用例子中的param实际上就是模块参数；
- returnParam：接口返回的数据池，所有模块共享的数据池，存进去后，最后会自动返回给请求端；
- httpRequest：HttpServletRequest，接口访问时的原始数据对象；

> passParam，sessionSave，returnParam的数据类型为JsonObject，moduleParam的数据类型为HashMap<String, String> 即字符串的键值对。

## 报错说明

报错说明详细见FillingParams.java中的ErrorCodes。

## SpringBoot(Gradle)依赖

依赖包详细见FillingParams.java中的GradleImport。

## SQL自动生成说明

DataBaseForm作为数据库的映射，配置后，此模块可自动生成SQL，如插入操作可简化为以下方式

```
												{
                            "templ":"MODULE-DAO",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                                "control":"insert",
                                "form":"t_test"
                            }]
                        }
```

DataBaseForm.java文件说明

```
public class DataBaseForm {
  //工程表
    static private HashMap<String, Boolean[]> t_test = new HashMap<String, Boolean[]>(){{
        put("test_id",new Boolean[]{ true, true, false, false });
        put("test_name",new Boolean[]{ false, true, false, false });
    }};

    static public HashMap<String, HashMap<String, Boolean[]>> fromMaps= new HashMap<String, HashMap<String, Boolean[]>>(){{
        put("t_test", t_test);
    }};
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
    static private HashMap<String, Boolean[]> t_test = new HashMap<String, Boolean[]>(){{
        put("test_id",new Boolean[]{ true, true, false, false });
        put("test_name",new Boolean[]{ false, true, false, false });
    }};

  static private HashMap<String, Boolean[]> t_abc = new HashMap<String, Boolean[]>(){{
        put("abc_id",new Boolean[]{ true, true, false, false });
        put("test_name",new Boolean[]{ false, true, false, false });
    }};
```

## 数据库事务说明

开启事务，只需要在Service的函数中，把function中的templ的值从`FUNCTION-NORMAL`换成`FUNCTION-TRANSACTIONAL`

![](https://stoprefactoring.com/data/document/framework/once/data/2.6Service业务代码.png) 