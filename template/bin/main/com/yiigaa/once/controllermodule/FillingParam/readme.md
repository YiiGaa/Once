# Controller 模块：FillingParam

## 模块功能简述

FillingParam的主要功能包括：

> 对passParam、returnParam数据池中的数据进行填充。

- 向passParam填充参数，包括生成uuid、获取session值、时间、拼接参数等

- 备份当前passParam参数（给每一个参数的key添加前缀）
- 向returnParam写入参数（向返回数据写入值）


| 模块名       | 最新版本 | 模块位置   | 依赖服务 |
| ------------ | -------- | ---------- | -------- |
| FillingParam | v1.0     | Controller | 无       |

## Christmas 使用例子

使用此模块，在/Christmas/Input/MakeCodeNormal/Controller@xxx/target.json中使用。

- 普通例子

```
                        {
                            "templ":"MODULE-FillingParam",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                            		"id":"abs@@uuid",
                                "time":"abs@@time##yyyy-MM-dd HH:mm:ss",
                                "logoSrc":"session##userid+_logo"
                            }]
                        }
```

- 备份数据，备份数据的key会加上指定前缀（tempCacheBackUp_），一般对应缓存模块（CacheOperation）使用

```
                        {
                            "templ":"MODULE-FillingParam",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                            		"module_backUpPrefix":"tempCacheBackUp_",
                            		"id":"abs@@uuid",
                                "time":"abs@@time##yyyy-MM-dd HH:mm:ss"
                            }]
                        }
```

- 向returnParam写入参数（向返回数据写入值）

```
                        {
                            "templ":"MODULE-FillingParam",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                            		"id":"abs@@uuid",
                                "returnId":"return@@get##id"
                            }]
                        }
```

## 配置文件设置

| 参数 | 必要 | 缺省值 | 说明 |
| ---- | ---- | ------ | ---- |
|      |      |        |      |

配置文件配置项在FillingParam/Templates.tmpl中，修改配置后，需要重新[连接模块](https://stoprefactoring.com/#content@content#framework/once/module-link)才能让配置生效。

- 生产环境的配置在propertiesTemplate中，连接模块后，会自动生成到/config/application.properties文件中；
- 开发环境的配置在propertiesTemplate local中，连接模块后，会自动生成到/config/application-local.properties文件中；

## moduleParam参数说明

### 通用参数

| 参数                | 必要 | 类型                 | 缺省值 | 取值范围 | 说明                                                         |
| ------------------- | ---- | -------------------- | ------ | -------- | ------------------------------------------------------------ |
| module_backUpPrefix | 否   | String               |        |          | 备份原始请求数据（会在清理数据后备份），备份数据的key上加入module_backUpPrefix对应值的前缀，一般对应缓存模块（CacheOperation）使用 |
| 自定义              | 否   | 特殊参数，详见以下说 |        |          | 需要填充到passParam、returnParam的目标key                    |

#### FillingParam特殊参数说明：

参数格式：

```
param_1@@param_2##param_3+param_4##param_5+param_6
```

##### @@前的参数（param_1）说明

| 可选值                     | 说明                                                         |
| -------------------------- | ------------------------------------------------------------ |
| abs                        | 绝对的，无论目标参数在passParam中是否有对应值，都会强行替换  |
| return                     | 绝对的，无论目标参数在returnParam中是否有对应值，都会强行替换 |
| 缺省，缺省时需要把@@也去掉 | 非绝对的，如果目标参数在passParam中有对应值，则不会替换      |

例子：

- {"test":"abs@@123"}：无论test字段在passParam中是否有对应值，把123覆盖到test字段对应的值，执行后test字段的值：{"test":"123"}
- {"test":"return@@123"}：无论test字段在returnParam中是否有对应值，把123覆盖到test字段对应的值，执行后test字段的值：{"test":"123"}
- {"test":"123"}：如果test字段在passParam中有对应值，则不会替换值

##### +号连接符的说明

+号是连接字符串的意思，把+号前后独立处理，最后再拼接起来

如{"test":"abs@@123+test"}，执行后passParam中的test字段为：{"test":"123test"}

##### ##前后的参数（param_2，param_4，param_5，param_6）说明

>  ##前后被认为是一个整体，如：param_2##param_3是一个整体，param_4##param_5是一个整体，param_6不存在##时自己就是一个整体。即##前是功能选择，##后是对应功能的补充参数

| ##前的可选值 | 是否需要##后的补充参数 | 补充参数说明                            | 说明                                                         |
| ------------ | ---------------------- | --------------------------------------- | ------------------------------------------------------------ |
| uuid         | 否                     |                                         | 自动生成uuid                                                 |
| session      | 是                     | 存在session中的字段                     | 获取session中的值，如果不存在会报错退出，中断其他模块调用，如果session中不存在此值，会报错（防止未登录用户使用此接口） |
| time         | 是                     | 时间字符串格式，如：yyyy-MM-dd HH:mm:ss | 自动生成当前时间对应格式的字符串                             |
| get          | 是                     | passParam数据池中的key                  | 获取passParam中其他key对应的值                               |
| self         | 否                     |                                         | 获取passParam中当前key对应的值                               |
| 自定义       | 否                     |                                         | 直接使用此字符串拼接                                         |

例子：

- {"test":"abs@@uuid"}，执行后passParam中的test字段为：{"test":"44a4ae638ad44ec2bc6834c0a2d69168"}，其中44a4ae638ad44ec2bc6834c0a2d69168为生成的uuid，可作为某个数据库字段的唯一值
- {"test":"abs@@session##userid"}，执行后passParam中的test字段为：{"test":"123456"}，其中123456为在session中的userid的值
- {"test":"abs@@time##yyyy-MM-dd HH:mm:ss"}，执行后passParam中的test字段为：{"test":"2019-10-16 00:00:00"}，其中2019-10-16 00:00:00为在当前时间，按照yyyy-MM-dd HH:mm:ss生成的
- {"test":"abs@@get##abc"}，执行后passParam中的test字段为：{"test":"123"}，其中123为在passParam中abc字段对应的值
- {"test":"abs@@abc"}，执行后passParam中的test字段为：{"test":"abc"}，其中abc为自定义值，直接拼接
- {"test":"abs@@self+abc"}，执行后passParam中的test字段为：{"test":"111abc"}，其中111为在passParam中test字段本来对应的值，abc是自定义字段

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

报错说明详细见FillingParam.java中的ErrorCodes。

## SpringBoot(Gradle)依赖

依赖包详细见FillingParam.java中的GradleImport。

## 其他说明

