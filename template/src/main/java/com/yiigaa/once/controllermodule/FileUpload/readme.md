# Controller 模块：FileUpload

## 模块功能简述

FileUpload的主要功能包括：

> 把请求中的Form表单中的文件上传到服务器，接口的function模板一定要是FUNCTION-GET，因为文件流只能通过表单获取。所以上传文件的接口的请求参数需要以Form表单作为数据格式。前端请求时，只需要把文件对象放进Form表单即可。另外，本模块只适合低于10MB的文件，如果文件太大，请采用大文件上传模块（FileBigUpload）。

- 上传文件，如果一个接口需要同时上传多个文件，请引入多个文件上传模块，一个模块处理一个文件；
- 限制文件大小；


| 模块名     | 最新版本 | 模块位置   | 依赖服务 |
| ---------- | -------- | ---------- | -------- |
| FileUpload | v1.0     | Controller | 无       |

## Christmas 使用例子

使用此模块，在/Christmas/Input/MakeCodeNormal/Controller@xxx/target.json中使用。

- 普通例子，上传文件

  > 接口的function模板一定要是FUNCTION-GET，因为文件流只能通过表单获取。所以上传文件的接口的请求参数需要以Form表单作为数据格式。前端请求时，只需要把文件对象放进Form表单即可。

```
                        {
                            "templ":"MODULE-FileUpload",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                            		"formKey":"file",
                            		"resultKey":"source_path",
                                "uploadName":"temp_uploadName",
                                "maxFileSize":"2000000",
                                "allow":".png,.jpg"
                            }]
                        }
```

## 配置文件设置

| 参数                                      | 必要 | 缺省值 | 说明                                                         |
| ----------------------------------------- | ---- | ------ | ------------------------------------------------------------ |
| controller.uploadfile.rootpath            | 是   |        | 文件存储的磁盘根目录，Java程序需要有写入改目录的权限         |
| spring.servlet.multipart.max-file-size    | 否   | 1MB    | 单个文件的最大大小（SpringBoot自带参数），必需大于模块参数maxFileSize |
| spring.servlet.multipart.max-request-size | 否   | 1MB    | 单个请求的总大小（SpringBoot自带参数），必需大于spring.servlet.multipart.max-file-size |

配置文件配置项在FileUpload/Templates.tmpl中，修改配置后，需要重新[连接模块](https://stoprefactoring.com/#content@content#framework/once/module-link)才能让配置生效。

- 生产环境的配置在propertiesTemplate中，连接模块后，会自动生成到/config/application.properties文件中；
- 开发环境的配置在propertiesTemplate local中，连接模块后，会自动生成到/config/application-local.properties文件中；

## moduleParam参数说明

### 通用参数

| 参数        | 必要 | 类型   | 缺省值                                                       | 取值范围   | 说明                                                         |
| ----------- | ---- | ------ | ------------------------------------------------------------ | ---------- | ------------------------------------------------------------ |
| formKey     | 否   | String | file                                                         |            | form表单中对应文件对象的key                                  |
| uploadName  | 否   | String | 当前时间戳                                                   |            | 写到磁盘的文件名，根据值先到passParam中获取值，如果失败，再直接使用该值作为文件名 |
| resultKey   | 否   | String | fileUpload                                                   |            | 把最终文件磁盘路径放到数据池（passParam或returnParam）中的key，存放到passParam或returnParam由isAsResult参数决定 |
| maxFileSize | 否   | Int    | -1（不限制，但仍然受spring.servlet.multipart.max-file-size的限制） |            | 当前接口接受文件的大小上限，单位为Byte（2MB写作"2000000"，严谨的话可以写成"2097152"） |
| allow       | 否   | String | 不限制                                                       |            | 允许的文件后缀，后缀需要添加"."，如".png"，多个后缀用","隔开，如".png,.jpg,.jpeg" |
| isNecessary | 否   | Bool   | true                                                         | true/false | form表单中不包含文件对象是否报错，true（报错），false（不报错，继续后续逻辑），设置为false且无文件时，存放到数据池的文件路径为空字符串"" |
| isAsResult  | 否   | Bool   | false                                                        | true/false | 决定最终文件磁盘路径放到哪个数据池，true存放到returnParam，false存放到passParam。如果两者都需要存放，可以先在此模块存放到passParam，然后在接口返回前通过参数填充模块（FillingParam）存放到返回参数当中。 |

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

报错说明详细见FileUpload.java中的ErrorCodes。

## SpringBoot(Gradle)依赖

依赖包详细见FileUpload.java中的GradleImport。

## 上传大小拦截说明

由于文件上传的特殊性，上传文件的大小可能会被三个地方连接

- Nginx等负载均衡拦截，此类拦截返回的状态码为500
- SprinBoot的spring.servlet.multipart拦截，文件过大会出现崩溃，返回的状态码为500
- 模块参数maxFileSize拦截，文件过大会正常运行，返回的code为200，返回的数据中errorCode为E-CM04(FileUpload)