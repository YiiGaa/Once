# Service 模块：DataBaseDao

## 1 模块功能简述

DataBaseDao模块是想通过moduleParam和DataBaseForm做到自动生成sql语句，达到查询数据库的目的。主要功能包括：
- 插入数据操
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
                                {"isNullError":false},
                                {"isNotNullError":false},
                                {"isAsResult":false},
                                {"isAllGet":false},
                                {"isOnlyCount":false},
                                {"isOnlyList":false},
                                {"isSaveSession":false}
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

# Service 模块：AuditContent

## 1 模块功能简述

AuditContent模块的主要功能包括：
- 同步调用移动云文本审查
- 同步调用移动云图片审查（http url地址）
- 同步调用移动云图片审查（发送base64文件）


## 2 Christmas 引入例子

- 审查内容

```
                        {
                            "templ":"MODULE-AuditContent",
                            "param_templ":"PARAM-Hash",
                            "param":[
                                {"aaa":"text"},
                                {"bbb":"text##base64"},
                                {"ccc":"image"},
                                {"ddd":"image##base64"}
                            ]
                        }
```


## 3 参数配置

生产环境在/config/application.properties文件中配置

本地环境在/config/application-local.properties文件中配置

| 参数                                | 必要 | 默认参数 | 说明                                               |
| ----------------------------------- | ---- | -------- | -----------------------------------------------|
| service.auditcontent.apikey         | 是   |          | 移动云视频图片审核服务的api key                     |
| service.auditcontent.secretkey      | 是   |          | 移动云视频图片审核服务的secret key                  |
| service.auditcontent.suscept        | 是   |          | 判断违规的阀值                                    |
| service.auditcontent.dataurl        | 是   |          | 图片http请求的url头部                             |


## 4 moduleParam参数说明

#### 架构数据池说明：
- passParam：中间参数，所有模块共享的数据池，API请求时传过来的参数在一开始也会放在里面
- moduleParam：模块独有参数，模块调用前传入
- sessionSave：准备存进session的数据池，所有模块共享的数据池，需要之后调用sessionSave模块才能把数据存进session
- returnParam：接口返回的数据池，所有模块共享的数据池，存进去后，最后会自动返回给请求端
- httpRequest：HttpServletRequest，接口访问时的原始数据对象

其中passParam，sessionSave，returnParam的数据类型为JsonObject，moduleParam的数据类型为HashMap<String, String> 即字符串的键值对

#### 参数类型说明：

- string：普通的字符串
- stringX：超级字符串，可以添加多个@xx@在里面，模块会自动把passParam中xx字段的值替换到这个字符串中。如，moduleParam中有{"abc":"@@onceid@@"}，模块会自动把passParam中onceid字段的值替换进去，如果passParam中不存在onceid即不替换。
- bool：bool型，true/false
- int：int型，数字类型

`注意：`moduleParam的参数类型是指参数的真实类型，比如isNotNullError的类型为bool，只有true和false，在填入Christmas时，依然以字符串形式（如：{"isNotNullError":"true"}）写入
### 4.0 通用参数

| 参数    | 必要 | 类型   | 缺省值 | 前提条件 | 说明                                                      |
| ------- | ---- | ------ | ------ | -------- | --------------------------------------------------------- |
| 自定义 | 否 | string |  | | 值的范围：text（文本），text##base64（base64文本），image（图片），image##base64（图片转base64发送）。自定义参数，会自动在passParam中获取对应的值 |


## 5 部署依赖

xx，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见AuditContent.java中的GradleImport，一般情况下不要修改

## 7 其他说明
# Service 模块：DeleteFile

## 1 模块功能简述

DeleteFile模块的主要功能包括：
- 删除文件


## 2 Christmas 引入例子

```
                        {
                            "templ":"MODULE-DeleteFile",
                            "param_templ":"PARAM-Hash",
                            "param":[
                                {"deletepath":"/home/data"},
                                {"name":"abc.txt"}
                            ]
                        }
```


## 3 参数配置

生产环境在/config/application.properties文件中配置

本地环境在/config/application-local.properties文件中配置

| 参数                                | 必要 | 默认参数 | 说明                                               |
| ----------------------------------- | ---- | -------- | -------------------------------------------------- |
|                                     |      |          |                                                     |


## 4 moduleParam参数说明

#### 架构数据池说明：
- passParam：中间参数，所有模块共享的数据池，API请求时传过来的参数在一开始也会放在里面
- moduleParam：模块独有参数，模块调用前传入
- sessionSave：准备存进session的数据池，所有模块共享的数据池，需要之后调用sessionSave模块才能把数据存进session
- returnParam：接口返回的数据池，所有模块共享的数据池，存进去后，最后会自动返回给请求端
- httpRequest：HttpServletRequest，接口访问时的原始数据对象

其中passParam，sessionSave，returnParam的数据类型为JsonObject，moduleParam的数据类型为HashMap<String, String> 即字符串的键值对

#### 参数类型说明：

- string：普通的字符串
- stringX：超级字符串，可以添加多个@xx@在里面，模块会自动把passParam中xx字段的值替换到这个字符串中。如，moduleParam中有{"abc":"@@userid@@"}，模块会自动把passParam中userid字段的值替换进去，如果passParam中不存在userid即不替换。
- bool：bool型，true/false
- int：int型，数字类型

`注意：`moduleParam的参数类型是指参数的真实类型，比如isNotNullError的类型为bool，只有true和false，在填入Christmas时，依然以字符串形式（如：{"isNotNullError":"true"}）写入
### 4.0 通用参数

| 参数    | 必要 | 类型   | 缺省值 | 前提条件 | 说明                                                      |
| ------- | ---- | ------ | ------ | -------- | --------------------------------------------------------- |
| deletepath | 是 | string |        |          | 指定遍历的文件目录，会在passParam中存在deletepath对应值的字段，则选用passParam中的值，如果不存在，则直接使用deletepath对应值 |
| name | 是 | string | | | 删除指定目录下所有包含该值的文件，会在passParam中存在name对应值的字段，则选用passParam中的值，如果不存在，则直接使用name对应值 |


## 5 部署依赖

xx，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见DeleteFile.java中的GradleImport，一般情况下不要修改

## 7 其他说明
# Service 模块：FillingParam

## 1 模块功能简述

FillingParam模块的主要功能包括：
- 向passParam填充参数
- 备份当前passParam参数（给每一个参数的key添加前缀）


## 2 Christmas 引入例子

```
                        {
                            "templ":"MODULE-FillingParam",
                            "param_templ":"PARAM-Hash",
                            "param":[
                                {"module_backUpPrefix":"tempCacheBackUp_"},
								{"article_id":"abs@@uuid"},
								{"article_id":"abs@@uuid"},
								{"article_date":"time##yyyy-MM-dd HH:mm:ss"},
                            ]
                        }
```


## 3 参数配置

生产环境在/config/application.properties文件中配置

本地环境在/config/application-local.properties文件中配置

| 参数                                | 必要 | 默认参数 | 说明                                               |
| ----------------------------------- | ---- | -------- | -------------------------------------------------- |
|                                     |      |          |                                                     |


## 4 moduleParam参数说明

#### 架构数据池说明：
- passParam：中间参数，所有模块共享的数据池，API请求时传过来的参数在一开始也会放在里面
- moduleParam：模块独有参数，模块调用前传入
- sessionSave：准备存进session的数据池，所有模块共享的数据池，需要之后调用sessionSave模块才能把数据存进session
- returnParam：接口返回的数据池，所有模块共享的数据池，存进去后，最后会自动返回给请求端
- httpRequest：HttpServletRequest，接口访问时的原始数据对象

其中passParam，sessionSave，returnParam的数据类型为JsonObject，moduleParam的数据类型为HashMap<String, String> 即字符串的键值对

#### 参数类型说明：

- string：普通的字符串
- stringX：超级字符串，可以添加多个@xx@在里面，模块会自动把passParam中xx字段的值替换到这个字符串中。如，moduleParam中有{"abc":"@@userid@@"}，模块会自动把passParam中userid字段的值替换进去，如果passParam中不存在userid即不替换。
- bool：bool型，true/false
- int：int型，数字类型

`注意：`moduleParam的参数类型是指参数的真实类型，比如isNotNullError的类型为bool，只有true和false，在填入Christmas时，依然以字符串形式（如：{"isNotNullError":"true"}）写入
### 4.0 通用参数

| 参数    | 必要 | 类型   | 缺省值 | 前提条件 | 说明                                                      |
| ------- | ---- | ------ | ------ | -------- | --------------------------------------------------------- |
| module_backUpPrefix | 否 | string |  | | 备份原始请求数据，原始的key上加入module_backUpPrefix对应值的前缀，对应CacheOperation模块使用 |
| 自定义 | 否 | 值类型详见 4.1特殊参数类型说明 |        |          | 需要填充到passParam的目标参数 |

### 4.1 特殊参数类型说明

参数格式：

param_1@@param_2##param_3+param_4##param_5+param_6

#### 4.1.1 @@前的参数（param_1）说明

| 可选值                     | 说明                                                         |
| -------------------------- | ------------------------------------------------------------ |
| abs                        | 绝对的，无论目标参数在passParam中是否有对应值，都会强行替换  |
| return                     | 绝对的，无论目标参数在returnParam中是否有对应值，都会强行替换 |
| 缺省，缺省时需要把@@也去掉 | 非绝对的，如果目标参数在passParam中有对应值，则不会强行替换  |

例子：

- {"test":"abs@@123"}：无论test字段在passParam中是否有对应值，把123覆盖到test字段对应的值，执行后test字段的值：{"test":"123"}
- {"test":"return@@123"}：无论test字段在returnParam中是否有对应值，把123覆盖到test字段对应的值，执行后test字段的值：{"test":"123"}
- {"test":"123"}：如果test字段在passParam中有对应值，则不会替换值

### 4.1.2 +号连接符的说明

+号是连接字符串的意思，把+号前后独立处理，最后再拼接起来

如{"test":"abs@@123+test"}，执行后passParam中的test字段为：{"test":"123test"}

#### 4.1.3 ##前后的参数（param_2，param_4，param_5，param_6）说明

说明：##前后被认为是一个整体，如：param_2##param_3是一个整体，param_4##param_5是一个整体，param_6不存在##时自己就是一个整体。

即，##前是功能选择，##后是对应功能的补充参数

| ##前的可选值 | 是否需要##后的补充参数 | 补充参数说明                            | 说明                                                      |
| ------------ | ---------------------- | --------------------------------------- | --------------------------------------------------------- |
| uuid         | 否                     |                                         | 自动生成uuid                                              |
| session      | 是                     | 存在session中的字段                     | 获取session中的值，如果不存在会报错退出，中断其他模块调用 |
| time         | 是                     | 时间字符串格式，如：yyyy-MM-dd HH:mm:ss | 自动生成当前时间对应格式的字符串                          |
| get          | 是                     | passParam数据池中的key                  | 获取passParam中其他key对应的值                            |
| self         | 否                     |                                         | 获取passParam中当前key对应的值                            |
| 自定义       | 否                     |                                         | 直接当字符串拼接                                          |

例子：

- {"test":"abs@@uuidt"}，执行后passParam中的test字段为：{"test":"44a4ae638ad44ec2bc6834c0a2d69168"}，其中44a4ae638ad44ec2bc6834c0a2d69168为生成的uuid，可作为某个数据库字段的唯一值
- {"test":"abs@@session##userid"}，执行后passParam中的test字段为：{"test":"123456"}，其中123456为在session中的userid的值
- {"test":"abs@@time##yyyy-MM-dd HH:mm:ss"}，执行后passParam中的test字段为：{"test":"2019-10-16 00:00:00"}，其中2019-10-16 00:00:00为在当前时间，按照yyyy-MM-dd HH:mm:ss生成的
- {"test":"abs@@get##abc"}，执行后passParam中的test字段为：{"test":"123"}，其中123为在passParam中abc字段对应的值
- {"test":"abs@@abc"}，执行后passParam中的test字段为：{"test":"abc"}，其中abc为自定义值，直接拼接
- {"test":"abs@@self+abc"}，执行后passParam中的test字段为：{"test":"111abc"}，其中111为在passParam中test字段本来对应的值，abc是自定义字段


## 5 部署依赖

无，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见FillingParam.java中的GradleImport，一般情况下不要修改

## 7 其他说明
# Service 模块：RabbitmqRequest

## 1 模块功能简述

RabbitmqRequest模块的主要功能包括：
- xx


## 2 Christmas 引入例子

- xx

```
                        {
                            "templ":"MODULE-RabbitmqRequest",
                            "param_templ":"PARAM-Hash",
                            "param":[

                            ]
                        }
```


## 3 参数配置

生产环境在/config/application.properties文件中配置

本地环境在/config/application-local.properties文件中配置

| 参数                                | 必要 | 默认参数 | 说明                                               |
| ----------------------------------- | ---- | -------- | -------------------------------------------------- |
|                                     |      |          |                                                     |


## 4 moduleParam参数说明

#### 架构数据池说明：
- passParam：中间参数，所有模块共享的数据池，API请求时传过来的参数在一开始也会放在里面
- moduleParam：模块独有参数，模块调用前传入
- sessionSave：准备存进session的数据池，所有模块共享的数据池，需要之后调用sessionSave模块才能把数据存进session
- returnParam：接口返回的数据池，所有模块共享的数据池，存进去后，最后会自动返回给请求端
- httpRequest：HttpServletRequest，接口访问时的原始数据对象

其中passParam，sessionSave，returnParam的数据类型为JsonObject，moduleParam的数据类型为HashMap<String, String> 即字符串的键值对

#### 参数类型说明：

- string：普通的字符串
- stringX：超级字符串，可以添加多个@xx@在里面，模块会自动把passParam中xx字段的值替换到这个字符串中。如，moduleParam中有{"abc":"@@userid@@"}，模块会自动把passParam中userid字段的值替换进去，如果passParam中不存在userid即不替换。
- bool：bool型，true/false
- int：int型，数字类型

`注意：`moduleParam的参数类型是指参数的真实类型，比如isNotNullError的类型为bool，只有true和false，在填入Christmas时，依然以字符串形式（如：{"isNotNullError":"true"}）写入
### 4.0 通用参数

| 参数    | 必要 | 类型   | 缺省值 | 前提条件 | 说明                                                      |
| ------- | ---- | ------ | ------ | -------- | --------------------------------------------------------- |
|        |        |      |        |          |   |


## 5 部署依赖

xx，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见RabbitmqRequest.java中的GradleImport，一般情况下不要修改

## 7 其他说明
