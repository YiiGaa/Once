# Controller 模块：CacheOperation

## 1 模块功能简述

CacheOperation模块是用redis缓存接口返回参数，根据关键字和参数缓存，主要功能包括：
- 获取API缓存，当passParam相同时
- 设置API缓存
- 清理指定API缓存


## 2 Christmas 引入例子

- 获取缓存

```
                        {
                            "templ":"MODULE-CacheOperation",
                            "param_templ":"PARAM-Hash",
                            "param":[
								{"control":"apiget"},
                                   {"module_origPrefix":"tempCacheBackUp_"}
                            ]
                        }
```

- 设置缓存
```
                        {
                            "templ":"MODULE-CacheOperation",
                            "param_templ":"PARAM-Hash",
                            "param":[
								{"control":"apiset"},
                                   {"module_origPrefix":"tempCacheBackUp_"}
                            ]
                        }
```
- 清除缓存

```
                        {
                            "templ":"MODULE-CacheOperation",
                            "param_templ":"PARAM-Hash",
                            "param":[
								{"control":"apiclean"},
                                   {"/once/test":"include"}
                            ]
                        }
```

## 3 参数配置

生产环境在/config/application.properties文件中配置

本地环境在/config/application-local.properties文件中配置

| 参数                                                 | 必要 | 默认参数 | 说明                        |
| ---------------------------------------------------- | ---- | -------- | --------------------------- |
| controller.cacheoperation.redis.hostName             | 是   |          | redis连接地址               |
| controller.cacheoperation.redis.port                 | 是   |          | redis连接端口               |
| controller.cacheoperation.redis.password             | 否   |          | redis连接密码，没有可不填写 |
| controller.cacheoperation.redis.timeout              | 否   |          | redis缓存失效时长（秒）     |
| controller.cacheoperation.lettuce.pool.maxTotal      | 否   |          | redis连接池最大线程数       |
| controller.cacheoperation.lettuce.pool.maxWaitMillis | 否   |          | redis连接池最大等待线时长   |
| controller.cacheoperation.lettuce.pool.maxIdle       | 否   |          | redis连接池最大空闲线程数   |
| controller.cacheoperation.lettuce.pool.minIdle       | 否   |          | redis连接池最小空闲线程数   |


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
| control | 是 | string |        |          | 操作方式，具体包括apiget（获取API缓存）,apiset（设置API缓存），apiclean（清理API缓存） |

### 4.1  获取API缓存的moduleParam（control:apiset）

缓存采用redis的hash数据结构，通过指定API（redis的key）和请求参数（redis的field）自动将returnParam里的数据存进去

| 参数    | 必要 | 类型   | 缺省值 | 前提条件 | 说明                                                      |
| ------- | ---- | ------ | ------ | -------- | --------------------------------------------------------- |
| field | 否 | string | 缺省时，会自动将api路劲代替。如/once/test.GET | control:apiset | 作为redis缓存的key |
| module_origPrefix | 否 | string | 缺省时，会将整个passParam作为关键字 | control:apiset | 不缺省时，会只用passParam中以module_origPrefix对应值为前缀的数据作为redis缓存的field |

### 4.2  获取API缓存的moduleParam（control:apiget）

缓存采用redis的hash数据结构，通过指定API（redis的key）和请求参数（redis的field）获取缓存里returnParam的数据取出来，如果成功，会阻断后续模块的调用

| 参数    | 必要 | 类型   | 缺省值 | 前提条件 | 说明                                                      |
| ------- | ---- | ------ | ------ | -------- | --------------------------------------------------------- |
| field | 否 | string | 缺省时，会自动将api路劲代替。如/once/test.GET | control:apiget | 作为redis缓存的key |
| module_origPrefix | 否 | string | 缺省时，会将整个passParam作为关键字 | control:apiget | 不缺省时，会只用passParam中以module_origPrefix对应值为前缀的数据作为redis缓存的field |

### 4.2  获取API缓存的moduleParam（control:apiclean）

删除以指定自定义字段的缓存，可以是API路劲中的一个关键字，可以达到删除一组api缓存

| 参数   | 必要 | 类型   | 缺省值 | 前提条件         | 说明                                                         |
| ------ | ---- | ------ | ------ | ---------------- | ------------------------------------------------------------ |
| 自定义 | 是   | string | equal  | control:apiclean | 取值包括equal（等于）、include（包含）。删除以指定自定义字段的缓存，即可以是API路劲中的一个关键字 |

## 5 部署依赖

redis，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见CacheOperation.java中的GradleImport，一般情况下不要修改

## 7 其他说明
# Controller 模块：CheckIdentity

## 1 模块功能简述

CheckIdentity模块是检查用户权限，报错时，断绝后续模块调用，主要功能包括：
- 检查session中的用户权限


## 2 Christmas 引入例子

- 检查session中的用户权限

```
                        {
                            "templ":"MODULE-CheckIdentity",
                            "param_templ":"PARAM-Hash",
                            "param":[
                            	{"powernormal@except":"xx"},
                            	{"powernormal@incluce":"xx"},
                            	{"poweradmin@except":"xx"},
                            	{"poweradmin@incluce":"xx"},
                            	{"normal":"false"},
                            	{"login":"true"}
                            ]
                        }
```


## 3 参数配置

生产环境在/config/application.properties文件中配置

本地环境在/config/application-local.properties文件中配置

以下对session配置

| 参数                                    | 必要 | 默认参数 | 说明                    |
| --------------------------------------- | ---- | -------- | ----------------------- |
| spring.session.store-type               | 否   |          | 省略会不缓存到redis     |
| server.servlet.session.timeout          | 否   |          | session有效时长         |
| spring.redis.hostName                   | 否   |          | redis连接host           |
| spring.redis.port                       | 否   |          | redis连接port           |
| spring.redis.password                   | 否   |          | redis连接密码           |
| spring.redis.lettuce.pool.maxTotal      | 否   |          | redis连接池线程总个数   |
| spring.redis.lettuce.pool.maxWaitMillis | 否   |          | redis连接池最大等待时长 |
| spring.redis.lettuce.pool.maxIdle       | 否   |          | redis连接池最大连接个数 |
| spring.redis.lettuce.pool.minIdle       | 否   |          | redis连接池最小连接个数 |


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

这几个参数同时设置，为或的关系，即一个通过就可以

| 参数    | 必要 | 类型   | 缺省值 | 前提条件 | 说明                                                      |
| ------- | ---- | ------ | ------ | -------- | --------------------------------------------------------- |
| powernormal@except | 否 | string |        |          | 除去这些普通用户权限都通过，即检查session中powernormal字段的数组，非空，且不出现对应值。字符串可用,隔开，检索多个权限，如{"powernormal@except":"xx,bb"} |
| powernormal@incluce | 否 | string | | | 只有这些普通用户权限能通过，即检查session中powernormal字段的数组，非空，且出现对应值。字符串可用,隔开，检索多个权限，如{"powernormal@incluce":"xx,bb"} |
| poweradmin@except | 否 | string | | | 除去这些普通用户权限都通过，即检查session中poweradmin字段的数组，非空，且不出现对应值。字符串可用,隔开，检索多个权限，如{"poweradmin@except":"xx,bb"} |
| poweradmin@incluce | 否 | string | | | 只有这些普通用户权限能通过，即检查session中poweradmin字段的数组，非空，且出现对应值。字符串可用,隔开，检索多个权限，如{"poweradmin@incluce":"xx,bb"} |
| normal | 否 | bool | | | 设置为true时，不登录也可以继续访问 |
| login | 否 | bool | | | 是否登录，即检查session中是否有userid字段，true时，session必须要有userid字段 |


## 5 部署依赖

如果夸war包访问session，需要做session共享，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见CheckIdentity.java中的GradleImport，一般情况下不要修改

## 7 其他说明
# Controller 模块：CheckNecessaryParam

## 1 模块功能简述

CheckNecessaryParam模块作为API的参数拦截，防止多余参数进入，主要功能包括：
- 检查参数
- 清除多余参数
- 备份原始请求参数（给每一个参数的key添加前缀）


## 2 Christmas 引入例子

```
                        {
                            "templ":"MODULE-CheckNecessaryParam",
                            "param_templ":"PARAM-Hash",
                            "param":[
									{"module_isClean":"true"},
                                        {"aa":"nec"},
                                        {"bb":"opt"}
                            ]
                        }
```


## 3 参数配置

生产环境在/config/application.properties文件中配置

本地环境在/config/application-local.properties文件中配置

| 参数 | 必要 | 缺省值 | 说明 |
| ---- | ---- | ------ | ---- |
|      |      |        |      |


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

| 参数    | 必要 | 类型   | 缺省值 | 取值范围 | 说明                                                      |
| ------- | ---- | ------ | ------ | -------- | --------------------------------------------------------- |
| module_isClean | 否 | bool | false |          | 是否清除多余参数，即清除moduleParam中自定义的参数意外的所有参数 |
| 自定义 | 否 | string |  | | 取值范围：nec（必要的），opt（可选的）自定义参数。需要检查的参数 |
| module_backUpPrefix | 否 | string |  | | 备份原始请求数据，原始的key上加入module_backUpPrefix对应值的前缀，对应CacheOperation模块使用 |


## 5 部署依赖

xx，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见CheckNecessaryParam.java中的GradleImport，一般情况下不要修改

## 7 其他说明
# Controller 模块：ErgodicGetParam

## 1 模块功能简述

ErgodicGetParam模块的主要功能包括：
- 把GET请求方式的参数存进passParam


## 2 Christmas 引入例子

- 无需引入模块，在配置function时把templ配置为FUNCTION-GET会自动引入

```
                        "function":[{
                        	"templ":"FUNCTION-GET",
                        	"mode":"GET",
                        }]
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
**详细见ErgodicGetParam.java中的GradleImport，一般情况下不要修改

## 7 其他说明
# Controller 模块：FileUpload

## 1 模块功能简述

FileUpload模块的主要功能包括：
- xx


## 2 Christmas 引入例子

- xx

```
                        {
                            "templ":"MODULE-FileUpload",
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
**详细见FileUpload.java中的GradleImport，一般情况下不要修改

## 7 其他说明
# Controller 模块：FillingParam

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
# Controller 模块：SessionCancel

## 1 模块功能简述

SessionCancel模块的主要功能包括：
- 把当前session的所有值清空，相当于退出登录
- 清空指定session值


## 2 Christmas 引入例子

```
                        {
                            "templ":"MODULE-SessionCancel",
                            "param_templ":"PARAM-Array",
                            "param":[
							  "all"
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

其中passParam，sessionSave，returnParam的数据类型为JsonObject，moduleParam的数据类型为String[] 即字符串数组

#### 参数类型说明：

- string：普通的字符串
- stringX：超级字符串，可以添加多个@xx@在里面，模块会自动把passParam中xx字段的值替换到这个字符串中。如，moduleParam中有{"abc":"@@userid@@"}，模块会自动把passParam中userid字段的值替换进去，如果passParam中不存在userid即不替换。
- bool：bool型，true/false
- int：int型，数字类型

`注意：`moduleParam的参数类型是指参数的真实类型，比如isNotNullError的类型为bool，只有true和false，在填入Christmas时，依然以字符串形式（如：{"isNotNullError":"true"}）写入
### 4.0 通用参数

| 参数    | 必要 | 缺省值 | 前提条件 | 说明                                                      |
| ------- | ---- | ------ | -------- | --------------------------------------------------------- |
| all | 否 |        |          | 清除 |
| 自定义 | 否 | | all不存在时生效 | 清除自定义值的session |


## 5 部署依赖

无，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见SessionCancel.java中的GradleImport，一般情况下不要修改

## 7 其他说明
# Controller 模块：SessionSave

## 1 模块功能简述

SessionSave模块的主要功能包括：
- 把sessionSave数据池中的所有值保存到session


## 2 Christmas 引入例子

- xx

```
                        {
                            "templ":"MODULE-SessionSave"
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

无，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见SessionSave.java中的GradleImport，一般情况下不要修改

## 7 其他说明
