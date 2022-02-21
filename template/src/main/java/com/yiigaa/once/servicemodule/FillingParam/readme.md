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
