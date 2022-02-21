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
