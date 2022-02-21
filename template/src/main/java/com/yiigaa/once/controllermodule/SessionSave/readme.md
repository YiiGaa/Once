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
