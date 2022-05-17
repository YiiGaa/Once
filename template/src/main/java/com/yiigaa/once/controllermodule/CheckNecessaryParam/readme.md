# Controller 模块：CheckNecessaryParam

## 模块功能简述

CheckNecessaryParam模块作为API的参数拦截，检查必要的参数与防止多余参数进入，主要功能包括：

> 对passParam数据池中的数据进行检查。

- 检查必要参数；
- 清除多余参数；
- 备份原始参数，备份数据的key会加上指定前缀；

| 模块名              | 最新版本 | 模块位置   | 依赖服务 |
| ------------------- | -------- | ---------- | -------- |
| CheckNecessaryParam | v1.0     | Controller | 无       |

## Christmas 使用例子

使用此模块，在/Christmas/Input/MakeCodeNormal/Controller@xxx/target.json中使用。

- 普通例子，会自动清理多余的key

```
                        {
                            "templ":"MODULE-CheckNecessaryParam",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                            			"module_isClean":"true",
                                  "aa":"nec",
                                  "bb":"opt",
                                  "cc":"nec@@list",
                                  "dd":"opt@@list"
                            }]
                        }
```

- 备份数据，备份数据的key会加上指定前缀（tempCacheBackUp_），一般对应缓存模块（CacheOperation）使用

```
                        {
                            "templ":"MODULE-CheckNecessaryParam",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                            			"module_isClean":"true",
                            			"module_backUpPrefix":"tempCacheBackUp_",
                                  "aa":"nec",
                                  "bb":"opt",
                                  "cc":"nec@@list",
                                  "dd":"opt@@list"
                            }]
                        }
```

## 配置文件设置

| 参数 | 必要 | 缺省值 | 说明 |
| ---- | ---- | ------ | ---- |
|      |      |        |      |

配置文件配置项在CheckNecessaryParam/Templates.tmpl中，修改配置后，需要重新[连接模块](https://stoprefactoring.com/#content@content#framework/once/module-link)才能让配置生效。

- 生产环境的配置在propertiesTemplate中，连接模块后，会自动生成到/config/application.properties文件中；
- 开发环境的配置在propertiesTemplate local中，连接模块后，会自动生成到/config/application-local.properties文件中；

## moduleParam参数说明

### 通用参数

| 参数                | 必要 | 类型   | 缺省值 | 取值范围                    | 说明                                                         |
| ------------------- | ---- | ------ | ------ | --------------------------- | ------------------------------------------------------------ |
| module_isClean      | 否   | bool   | false  |                             | 是否清除多余参数，即清除moduleParam中自定义的参数以外的所有参数 |
| 自定义              | 否   | string |        | nec/opt/nec@@list/opt@@list | 检查参数的key，取值范围说明：nec（必要的），opt（可选的），nec@@list（必要的数组，参数的值必需是长度大于1的数组），opt@@list(可选的) 检查必要参数 |
| module_backUpPrefix | 否   | string |        |                             | 备份原始请求数据（会在清理数据后备份），备份数据的key上加入module_backUpPrefix对应值的前缀，一般对应缓存模块（CacheOperation）使用 |

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

报错说明详细见CheckNecessaryParam.java中的ErrorCodes。

## SpringBoot(Gradle)依赖

依赖包详细见CheckNecessaryParam.java中的GradleImport。

## 其他说明