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
                                        {"bb":"opt"},
                                        {"cc":nec@@list},
                                        {"dd":opt@@list}
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
| 自定义 | 否 | string |  | | 取值范围：nec（必要的），opt（可选的）自定义参数,nec@@list(必要的数组),opt@@list(可选的) 检查必要参数 |
| module_backUpPrefix | 否 | string |  | | 备份原始请求数据，原始的key上加入module_backUpPrefix对应值的前缀，对应CacheOperation模块使用 |


## 5 部署依赖

xx，连接信息详见`参数配置`

## 6 SpringBoot(Gradle)依赖
**详细见CheckNecessaryParam.java中的GradleImport，一般情况下不要修改

## 7 其他说明
