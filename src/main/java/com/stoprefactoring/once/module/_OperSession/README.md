# _OperSession-Session操作

主要功能包括：

- save-添加session信息
- delete-删除session信息

模块参数的值说明：

- 如果希望设置的值为固定值，则直接设置对应类型的值即可
- 如果希望从`passParam`中动态获取值作为设置的值，则请以`get##key`作为模块参数的设置值
- 如果想要获取的值在passParam的里层（嵌套json），则请以`>>`定位，如`get##key_1>>key_2`

# ※ save-添加session信息
- 组入版本：1.0=2024.07.10
- 此模块不提供获取session值的功能，获取session值的功能由模块`_DataFilling`提供

```json
{
	"name":"_OperSession",
	"param":{
		"_action":"save",
    "key_1":"value",
    "key_2":"get##key"
	}
}
```

| key | 说明 | 是否必要 | 默认值 | 类型 | 组入/更新版本 |
| --- | --- | --- | --- | --- | --- |
| _action | 指定模块动作，固定为"save" | 是 |  | String | 1.0=2024.07.10 |
| _isNullError | 当不能获取插入值时，是否报错，true: 报错退出，false: 尝试用默认值代替 | 否 | true | Bool | 1.0=2024.07.10 |
| _defaultValue | 当获取不到插入值时的默认替代值 | 否 |  | Object | 1.0=2024.07.10 |
| xxx | 将要插入session的自定义数据 | 是 |  | Object | 1.0=2024.07.10 |

模块参数补充说明：

### > _isNullError

- 当不能获取插入值时，是否报错，true: 报错退出，false: 尝试用默认值代替
- 如果`_isNullError`为true，当获取不到插入session的值时，会立刻报错退出，忽略`_defaultValue`的设置

### > _defaultValue

- 当获取不到插入值时的默认替代值
- `_isNullError`为`false`时，此设置才会生效
- `_isNullError`为`false`，且省略`_defaultValue`的设置时，模块会跳过此值的保存

### > xxx

- 将要插入session的自定义数据
- 自定义的key不要以`get##`作为前缀，否则可能会无法获取或删除此session值
- 自定义的key如果与现有session的key相同，会覆盖现有数据
- session是一个单层的map，所以如果是覆盖数据的话，将是整个数据被覆盖
- 请规划好session数据的key，避免在夸应用环境（共享session）引起混乱
- 对应的value可以是Object，但必须是Json支持的类型，如Bool、Int、Long、String、Array、Object
- 如果希望从`passParam`中动态获取值作为设置的值，则请以`get##key`作为设置值，如"get##key"
- 如果想要获取的值在passParam的里层（嵌套json），则请以`>>`定位，如`get##key_1>>key_2`

# ※ delete-删除session信息

- 组入版本：1.0=2024.07.10

```json
{
	"name":"_OperSession",
	"param":{
		"_action":"delete",
    "_key":[
      "session key 1",
      "get##key"
    ]
	}
}
```

| key     | 说明                         | 是否必要 | 默认值 | 类型   | 组入/更新版本  |
| ------- | ---------------------------- | -------- | ------ | ------ | -------------- |
| _action | 指定模块动作，固定为"delete" | 是       |        | String | 1.0=2024.07.10 |
| _key    | 需要删除的session值的key     | 是       |        | Array  | 1.0=2024.07.10 |

模块参数补充说明：

### > _key

- 需要删除的session值的key
- 设置值为一个String数组，如果包含非String的元素，模块会跳过此元素
- 如果希望从`passParam`中动态获取值作为设置的值，则请以`get##key`作为设置值，如"get##key"，获取的值仍必须为String类型，否则模块会跳过此元素
- 如果想要获取的值在passParam的里层（嵌套json），则请以`>>`定位，如`get##key_1>>key_2`
- 删除session值需要慎重，避免在夸应用环境（共享session）引起混乱

# ◎ 配置说明

模块配置在`_OperSession/Once.Config`中设置，通过Christmas运行应用时，会自动拼接到`config/application.properties`

| key | 说明 | 是否必要 | 默认值 | 类型 | 组入/更新版本 |
| --- | --- | --- | --- | --- | --- |
|      |      |          |        |      |               |

配置补充说明：

- 可以在此设置Spring boot中关于session的设置

- 但如果有`_ConfigSession`模块的话，必须将Session的相关设置都集中在`_ConfigSession`模块，不然可能会造成设置污染（相互覆盖等问题）


# ◎ 模块单独测试

在工程根目录，运行shell指令

```
python3 Christmas/Christmas.py ShellExcute/Compile#TestModule _OperSession
```

也可以通过`Christmas插件`运行`ShellExcute>>Compile#TestModule`，并在插件打开的终端中输入`_OperSession`

### > 测试例子

- URL：http://127.0.0.1:8080/once/module/sample

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
        "put":{"key":"value"}
    }
    ```
    
- 期待结果：运行终端会打印session值，插入了key1/key2/key3，然后删除了key1

    ```
    > Save session
    - key:key 3, value:{"aa":"bb"}
    - key:key 2, value:{"key":"value"}
    - key:key 1, value:value
    
    > After delete session
    - key:key 3, value:{"aa":"bb"}
    - key:key 2, value:{"key":"value"}
    ```
    
    请求返回：
    
    ```
    {
        "code": "200",
        "message": "OK"
    }
    ```

# ◎ 更新列表

**1.0=2024.07.10**

- 模块建立