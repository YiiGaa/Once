# Service 模块：DeleteFile

## 模块功能简述

DeleteFile的主要功能包括：

> 删除磁盘上的文件，删除文件为异步操作。由于文件删除是异步的，所以请求返回结果无法判断是否删除成功。接口测试时，需要检验文件是否已经被删除，避免由于权限问题实际无法删除的问题。

- 删除磁盘上的文件/目录；
- 根据关键字删除文件；


| 模块名       | 最新版本 | 模块位置 | 依赖服务 |
| ------------ | -------- | -------- | -------- |
| DeleteFile | v1.0     | Service  | 无       |

## Christmas 使用例子

使用此模块，在/Christmas/Input/MakeCodeNormal/Service@xxx/target.json中使用。

- 普通例子，删除制定文件

```
                        {
                            "templ":"MODULE-DeleteFile",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                            		"deletePath":"/date/logo.png"
                            }]
                        }
```

- 从passParam中获取删除文件路径

```
                        {
                            "templ":"MODULE-DeleteFile",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                            		"deletePath":"temp_filePath"
                            }]
                        }
```

- 删除文件夹，会删除所有内容

```
                        {
                            "templ":"MODULE-DeleteFile",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                            		"deletePath":"/date/text/",
                            		"investigate":"true"
                            }]
                        }
```

## 配置文件设置

| 参数                        | 必要 | 缺省值 | 说明                                                         |
| --------------------------- | ---- | ------ | ------------------------------------------------------------ |
| service.deletefile.rootpath | 是   |        | 删除文件的根目录，用作校验删除目录是否合法，避免由于接口漏洞删除了其他文件 |

配置文件配置项在DeleteFile/Templates.tmpl中，修改配置后，需要重新[连接模块](https://stoprefactoring.com/#content@content#framework/once/module-link)才能让配置生效。

- 生产环境的配置在propertiesTemplate中，连接模块后，会自动生成到/config/application.properties文件中；
- 开发环境的配置在propertiesTemplate local中，连接模块后，会自动生成到/config/application-local.properties文件中；

## moduleParam参数说明

### 通用参数

| 参数        | 必要 | 类型   | 缺省值 | 取值范围 | 说明                                                         |
| ----------- | ---- | ------ | ------ | -------- | ------------------------------------------------------------ |
| deletePath  | 是   | String |        |          | 删除文件的路径（可以是文件目录），先到passParam中取值，如果取不到，以当前值作为文件路径。 |
| contain     | 否   | String | 所有   |          | deletePath为一个文件目录时，含有contain的值才会删除，空字符串("")表示删除所有 |
| investigate | 否   | Bool   |        |          | 是否深入多级目录，deletePath为一个文件目录时，是否遍历下级所有目录。true（遍历下级所有目录），false（不遍历下级目录） |

> 文件路径需要与service.deletefile.rootpath对应，不然会报文件非法错误。

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

报错说明详细见DeleteFile.java中的ErrorCodes。

## SpringBoot(Gradle)依赖

依赖包详细见DeleteFile.java中的GradleImport。

## 其他说明