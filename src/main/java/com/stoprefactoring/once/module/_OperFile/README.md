# _OperFile-文件操作

主要功能包括：
- upload-上传文件
- delete-删除文件/目录
- move-迁移文件/目录

模块使用前必要修改：

- 设置文件操作的父目录，在`_OperFile/Once.Config`文件中的`module._OperFile.rootPath`，并确保程序有操作此目录的权限

模块参数的值说明：

- 如果希望设置的值为固定值，则直接设置对应类型的值即可
- 如果希望从`passParam`中动态获取值作为设置的值，则请以`get##key`作为模块参数的设置值
- 如果想要获取的值在passParam的里层（嵌套json），则请以`>>`定位，如`get##key_1>>key_2`

# ※ upload-上传文件
- 组入版本：1.0=2024.07.02
- 只适合小文件上传，如果是大文件，请考虑分片上传等其他方案
- API请求时，请以`multipart/form-data`的方式请求，并在表单中记录文件信息
- 请仔细考虑这些设置：`_isExistCover`、`_isErrorRollBack`、`_isAutoRollBack

```json
{
	"name":"_OperFile",
	"param":{
		"_action":"upload",
        "_targetKey":"file",
        "_resultPathKey":"filePath",
        "_isPutReturn":true
	}
}
```
| key                    | 说明                                                         | 是否必要 | 默认值                 | 类型   | 组入/更新版本  |
| ---------------------- | ------------------------------------------------------------ | -------- | ---------------------- | ------ | -------------- |
| _action                | 指定模块动作，固定为"upload"                                 | 是       |                        | String | 1.0=2024.07.02 |
| _targetKey             | 上传文件在请求表单中对应的key                                | 否       | file                   | String | 1.0=2024.07.02 |
| _isNullError           | 当请求表单中获取不到文件时，是否报错。true:报错，false:不报错 | 否       | true                   | Bool   | 1.0=2024.07.02 |
| _maxCount              | 上传文件的最大个数                                           | 否       | 不限制                 | Int    | 1.0=2024.07.02 |
| _maxFileSize           | 单个文件的最大存储空间限制（单位：字节）                     | 否       | 不限制                 | Long   | 1.0=2024.07.02 |
| _AllowSuffix           | 允许的文件后缀，例如`".jpg|.png|.gif"`                       | 否       | 不限制                 | String | 1.0=2024.07.02 |
| _targetFileName        | 保存的文件名，文件后缀会自动拼接，例如"/dirs/filename"       | 否       | uuid                   | String | 1.0=2024.07.02 |
| _isPutReturn           | 是否将结果存放在`returnParam`，false: 将结果存放在`passParam` | 否       | true                   | Bool   | 1.0=2024.07.02 |
| _resultPathKey         | 存放上传完成的文件路径的key                                  | 否       | 采用`_targetKey`的设置 | String | 1.0=2024.07.02 |
| _resultOriginalNameKey | 存放上传完成的文件原始名称的key                              | 否       | 不记录                 | String | 1.0=2024.07.02 |
| _resultFileSizeKey     | 存放上传完成的文件大小的key，单位：字节                      | 否       | 不记录                 | String | 1.0=2024.07.02 |
| _isExistCover          | 当目标路径已经存在文件，是否覆盖，true:覆盖，false:报错退出  | 否       | false                  | Bool   | 1.0=2024.07.02 |
| _isErrorRollBack       | 当模块报错时，是否删除已经上传的文件，true:自动删除，false:不删除 | 否       | true                   | Bool   | 1.0=2024.07.02 |
| _isAutoRollBack        | 当业务逻辑报错时（Controller、Service调用其他模块报错），是否删除已上传的文件，true:自动删除，false:不删除 | 否       | true                   | Bool   | 1.0=2024.07.02 |

模块参数补充说明：

### > _targetKey

- 上传文件在请求表单中对应的key
- 如果表单中采用多个相同的key记录多个文件，模块会自动将多个文件一并上传
- 如果是上传多个文件，模块会自动在文件名后缀前，加上序号标识，如`dirs/name-0.png`、`dirs/name-1.png`

### > _maxCount

- 上传文件的最大个数
- 当请求表单中采用多个相同的key记录多个文件时，且`值>0`，此设置才会生效
- 如果`值<1`，则视为不限制

### > _maxFileSize

- 单个文件的最大存储空间限制（单位：字节）
- 当请求表单中采用多个相同的key记录多个文件时，且`值>0`，此设置才会生效
- 如果`值<1`，则视为不限制
- 限制存在优先级，nginx、tomcat请求数据的限制 > spring.servlet.multipart.max-file-size > spring.servlet.multipart.max-file-size > 模块上传时的文件大小限制

### > _AllowSuffix

- 允许的文件后缀，如".jpg"
- 如果没有文件后缀，则设置为`""`
- 设置多个文件后缀，用`|`分隔，如`".jpg|.png"`
- 如果希望设置为排除在外的范围，可以添加`not##`前缀，如`"not##.jpg|.png"`

### > _targetFileName

- 保存的文件名，文件后缀会自动拼接，例如"/dirs/filename"
- 最终的文件路径为：`rootPath(配置文件)+_targetFileName+源文件后缀(自动获取)`
- 文件名请以英文、数字、下划线为主
- 建议`_targetFileName`包含`uuid`，且每次调用模块接收上传文件时都更新`uuid`
- 文件名可以设置多层目录，如`/aa/bbb/ccc`
- 如果是上传多个文件，模块会自动在文件名后缀前，加上序号标识，如`dirs/name_0.png`、`dirs/name_1.png`

### > _resultPathKey

- 存放上传完成的文件路径的key

- 如果上传文件为单个文件，结果为：

    ```
    {
    	"result path key":"/rootPath/aa/bb.jpg"
    }
    ```

- 如果上传文件为多个文件，将在key的后面自动添加序号作为标识，结果为

    ```
    {
    	"result path key0":"/rootPath/aa/bb_0.jpg",
    	"result path key1":"/rootPath/aa/bb_1.jpg",
    	"result path key2":"/rootPath/aa/bb_2.jpg"
    }
    ```

### > _resultOriginalNameKey

- 存放上传完成的文件原始名称的key

- 设置为`""`，视为不记录

- 如果上传文件为单个文件，结果为：

    ```
    {
    	"result original name key":"originalName.jpg"
    }
    ```

- 如果上传文件为多个文件，将在key的后面自动添加序号作为标识，顺序与`_resultPathKey`记录的相同

    ```
    {
    	"result original name key0":"aa.jpg",
    	"result original name key1":"bb.jpg",
    	"result original name key2":"cc.jpg"
    }
    ```

### > _resultFileSizeKey

- 存放上传完成的文件大小的key

- 结果为Long类型的数据（单位：字节）

- 设置为`""`，视为不记录

- 如果上传文件为单个文件，结果为：

    ```
    {
    	"result file size key": 760462
    }
    ```

- 如果上传文件为多个文件，将在key的后面自动添加序号作为标识，顺序与`_resultPathKey`记录的相同

    ```
    {
    	"result path key0":760462,
    	"result path key1":790960462,
    	"result path key2":790960462879
    }
    ```

### > _isExistCover

- 当目标路径已经存在文件，是否覆盖，true:覆盖，false:报错退出
- `_isErrorRollBack` 或者`_isAutoRollBack`为true时，`_isExistCover`尽量不要设置为true，因为可能会出现，旧文件被覆盖后，由于报错又自动删除了文件的情况

### > _isErrorRollBack

- 当模块报错时，是否删除已经上传的文件，true:自动删除，false:不删除
- 自动删除文件为异步线程执行，所以模块返回时不一定文件已经被删除
- `_isErrorRollBack`设置为true时，不建议将`_isExistCover`设置为true，如果两者都为true，可能会出现上传覆盖了旧文件后，自动删除的异步线程又将新文件删除的情况

### > _isAutoRollBack

- 当业务逻辑报错时（Controller、Service调用其他模块报错），是否删除已上传的文件，true:自动删除，false:不删除
- 当一个接口使用了多个Service逻辑，且Controller、多个Service都调用了模块上传文件，当某个Service业务逻辑报错时，只会删除此Service上传的文件，其他Service上传的文件、Controller上传的文件暂时不受影响。当Controller业务逻辑结束报错时，会一并回滚所有Controller、Service上传的所有文件
- 自动删除文件为异步线程执行，Controller、Service返回时不一定文件已经被删除
- `_isAutoRollBack`设置为true时，不建议将`_isExistCover`设置为true，如果两者都为true，可能会出现上传覆盖了旧文件后，自动删除的异步线程又将新文件删除的情况

# ※ delete-删除文件/目录

- 组入版本：1.0=2024.07.02
- 请慎重删除文件，删除文件后，一般是无法回滚/复原的

```json
{
	"name":"_OperFile",
	"param":{
		"_action":"delete",
    "_path":""
	}
}
```

| key                     | 说明                                                         | 是否必要 | 默认值 | 类型   | 组入/更新版本  |
| ----------------------- | ------------------------------------------------------------ | -------- | ------ | ------ | -------------- |
| _action                 | 指定模块动作，固定为"delete"                                 | 是       |        | String | 1.0=2024.07.02 |
| _path                   | 需要删除的文件/目录路径                                      | 是       |        | String | 1.0=2024.07.02 |
| _isNullError            | 当文件/目录不存在时，是否报错，true:报错，false:不报错       | 否       | true   | Bool   | 1.0=2024.07.02 |
| _isSync                 | 是否采用同步的方式删除，true:同步删除，false:异步线程删除    | 否       | false  | Bool   | 1.0=2024.07.02 |
| _isDeleteErrorInterrupt | 删除目录时，如果中途出现错误，是否中断。true:中断，false:继续 | 否       | false  | Bool   | 1.0=2024.07.02 |

模块参数补充说明：

### > _path

- 需要删除的文件/目录路径
- 为了文件安全，路径的开头要求符合`module._OperFile.rootPath`或`module._OperFile.checkPath`的配置，不然会报错
- 请规划好用于接收上传文件的目录结构

### > _isSync

- 是否采用同步的方式删除，true:同步删除，false:异步线程删除
- 当文件较小时，可以采用同步删除
- 当文件较大，或者可能包含大文件的情况下，建议采用异步线程删除的方式。因为对大文件进行同步删除需要花很长时间，可能会引起接口超时等问题
- 删除目录时，建议采用异步线程删除的方式，因为对于删除目录，实际上是递归删除目录下的每个文件
- 异步删除是由一个线程池完成的，在高并发下场景下，可能会堆积很多待处理的删除任务，在关闭后端应用前，应该先停止此后端应用的请求接收（网关截流等方式），等待1-2小时后，再关闭后端应用程序。不然大概率会导致删除任务丢失，当然等待1-2小时也无法保证所有任务都处理完成
- 如果希望保证不丢失删除任务，则需要采用别的方案，例如采用Hive (stoprefactoring.com)架构下发云计算任务等 
- 同步删除，模块会返回删除结果，而异步删除，则只能通过日志确认是否删除成功

### > _isDeleteErrorInterrupt

- 删除目录时，如果中途出现错误，是否中断。true:中断，false:继续
- 删除目录时，实际上是递归删除目录下的每个文件，可能会由于权限等问题导致某些文件删不掉，如果`_isDeleteErrorInterrupt`设置为true，会中断退出，设置为false，则继续尝试删除其他文件
- 以同步方式删除时，无论`_isDeleteErrorInterrupt`如何设置，都会返回错误

# ※ move-迁移文件/目录

- 组入版本：1.0=2024.07.02
- 对于需要删除的文件，更推荐的做法是，先将文件迁移到特定目录（相当于回收站），过一定时间再通过定时任务等其他手段清理文件
- 如果在同一文件系统下，迁移文件/目录可以简单理解为重命名

```json
{
	"name":"_OperFile",
	"param":{
		"_action":"move",
    "_sourcePath":"",
    "_targetPath":""
	}
}
```

| key                | 说明                                                         | 是否必要 | 默认值 | 类型   | 组入/更新版本  |
| ------------------ | ------------------------------------------------------------ | -------- | ------ | ------ | -------------- |
| _action            | 指定模块动作，固定为"move"                                   | 是       |        | String | 1.0=2024.07.02 |
| _sourcePath        | 源文件/目录路径                                              | 是       |        | String | 1.0=2024.07.02 |
| _targetPath        | 目标文件/目录路径                                            | 是       |        | String | 1.0=2024.07.02 |
| _isSourceNullError | 当源文件/目录不存在时，是否报错，true:报错，false:不报错     | 否       | true   | Bool   | 1.0=2024.07.02 |
| _isTargetCover     | 当目标文件/目录路径存在时，是否覆盖，true:覆盖，false:不覆盖并报错 | 否       | false  | Bool   | 1.0=2024.07.02 |
| _isSync            | 是否采用同步的方式迁移，true:同步迁移，false:异步线程迁移    | 否       | false  | Bool   | 1.0=2024.07.02 |

模块参数补充说明：

### > _sourcePath

- 源文件/目录路径
- 为了文件安全，路径的开头要求符合`module._OperFile.rootPath`或`module._OperFile.checkPath`的配置，不然会报错

### > _sourcePath

- 目标文件/目录路径
- 为了文件安全，路径的开头要求符合`module._OperFile.rootPath`或`module._OperFile.checkPath`的配置，不然会报错

### > _isTargetCover

- 当目标文件/目录路径存在时，是否覆盖，true:覆盖，false:不覆盖并报错
- 当目标路径已存在，且是目录的话，则无法覆盖，仍然会报错退出

### > _isSync

- 是否采用同步的方式迁移，true:同步迁移，false:异步线程迁移
- 如果在同一文件系统下，迁移文件/文件夹，一般可以采用同步的方式。
- 如果在非同一文件系统下，迁移文件/文件夹，可能一些时间，更推荐采用异步的方式
- 异步迁移是由一个线程池完成的，在高并发下场景下，可能会堆积很多待处理的迁移任务，在关闭后端应用前，应该先停止此后端应用的请求接收（网关截流等方式），等待1-2小时后，再关闭后端应用程序。不然大概率会导致迁移任务丢失，当然，等待1-2小时也无法保证所有任务都处理完成
- 如果希望保证不丢失迁移任务，则需要采用别的方案，例如采用Hive (stoprefactoring.com)架构下发云计算任务等 
- 同步迁移，模块会返回迁移结果，而异步迁移，则只能通过日志确认是否迁移成功

# ◎ 配置说明

模块配置在`_OperFile/Once.Config`中设置，通过Christmas运行应用时，会自动拼接到`config/application.properties`

| key                                       | 说明                                                 | 是否必要 | 默认值 | 类型   | 组入/更新版本  |
| ----------------------------------------- | ---------------------------------------------------- | -------- | ------ | ------ | -------------- |
| spring.servlet.multipart.max-file-size    | 通过servlet预先限制单个文件大小                      | 否       | 1MB    | String | SpringBoot |
| spring.servlet.multipart.max-request-size | 通过servlet限制整个请求数据的大小                    | 否       | 10MB   | String | SpringBoot |
| module._OperFile.rootPath                 | 上传文件的父目录，此设置也作为允许移动、删除的父目录 | 是       |        | String | 1.0=2024.07.02 |
| module._OperFile.checkPath                | 允许移动、删除的父目录                               | 否       |        | String | 1.0=2024.07.02 |
| module._OperFile.poolSize                 | 设置异步删除、移动文件的线程池的最大线程个数         | 否       | 20     | Int    | 1.0=2024.07.02 |
| module._OperFile.poolQueue                | 设置异步删除、移动文件的线程池的队列数               | 否       | 不限制 | Int    | 1.0=2024.07.02 |

配置补充说明：

### > spring.servlet.multipart.max-file-size

- 通过servlet预先限制文件大小，如1MB
- 这个设置是`spring.servlet`的设置，默认是1MB（单个文件）
- 如果文件大于这个设置，会返回413错误，Controller将接收不到这个请求
- 限制存在优先级，nginx、tomcat请求数据的限制 > spring.servlet.multipart.max-file-size > spring.servlet.multipart.max-file-size > 模块对上传文件大小的限制

### > spring.servlet.multipart.max-request-size

- 通过servlet限制整个请求数据的大小
- 这个设置是spring.servlet的设置，默认是10MB
- 如果表单包含多个文件，请按需修改这个值
- 限制存在优先级，nginx、tomcat请求数据的限制 > spring.servlet.multipart.max-file-size > spring.servlet.multipart.max-file-size > 模块上传时的文件大小限制

### > module._OperFile.rootPath

- 上传文件的父目录，此设置也作为允许移动、删除的父目录
- 上传文件时，此父目录是强制添加的
- 请设置好文件操作权限

### > module._OperFile.checkPath

- 允许移动、删除的父目录
- 如果不设置的话，那么只允许`module._OperFile.rootPath`的父目录
- 允许设置多个值，用`,`隔开，如`/data,/backup`
- 请设置好文件操作权限

### > module._OperFile.poolQueue

- 设置异步删除、移动文件的线程池的队列数
- 如果队列已经满了，模块会报错


# ◎ 模块单独测试

测试前先修改`_OperFile/Once.Config`文件中的`module._OperFile.rootPath`设置（用于上传文件的父目录）

然后，需要单独运行模块，在工程根目录，运行shell指令

```
python3 Christmas/Christmas.py ShellExcute/Compile#TestModule _OperFile
```

也可以通过`Christmas插件`运行`ShellExcute>>Compile#TestModule`，并在插件打开的终端中输入`_OperFile`

### > 测试例子-上传

- URL：http://127.0.0.1:8080/once/module/sample/upload

- 请求方式：POST

- 请求体类型：multipart/form-data

    ```
    表单中记录文件的key:file
    文件小于2MB
    ```
    
- 期待结果：在`module._OperFile.rootPath`的上传文件父目录下存在文件，请求返回：

    ```
    {
        "result": "/data/11db62b64c4c4f36b4d0c52a5fa33d5b.png",
        "name": "orginalname.png",
        "size": 1193615,
        "code": "200",
        "message": "OK"
    }
    ```

### > 测试例子-删除

- URL：http://127.0.0.1:8080/once/module/sample/delete

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
    	"file":"/data/11db62b64c4c4f36b4d0c52a5fa33d5b.png(上传时返回的路径)"
    }
    ```

- 期待结果：文件被删除，请求返回：

    ```
    {
        "code": "200",
        "message": "OK"
    }
    ```

### > 测试例子-迁移

- URL： http://127.0.0.1:8080/once/module/sample/move

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
    	"source":"/data/11db62b64c4c4f36b4d0c52a5fa33d5b.png(上传时返回的路径)",
    	"target":"/data/11db62b64c4c4f36b4d0c52a5fa33d5b.png(目标路径)"
    }
    ```

- 期待结果：文件被迁移，请求返回：

    ```
    {
        "code": "200",
        "message": "OK"
    }
    ```

# ◎ 更新历史

**1.0=2024.07.02**

- 模块建立



