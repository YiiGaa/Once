# _OperSession-Session operation

The main functions include:

- save - add session information
- delete - delete session information

Description of the value of module parameters:

- If the value you want to set is a fixed value, you can directly set the value of the corresponding type.

- If you want to dynamically obtain the value from `passParam` as the set value, please use `get##key` as the setting value of the module parameter.

- If the value you want to get is in the inner layer of passParam (nested json), please locate it with `>>`, such as `get##key_1>>key_2`

# ※ save-add session information

- Add version: 1.0=2024.07.10
- This module does not provide the function of obtaining the session value. The function of obtaining the session value is provided by the module `_DataFilling`

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

| Key    | Description                           | Necessary | Default | Type   | Add/Update     |
| ------ | ------------------------------------- | --------- | ------- | ------ | -------------- |
| _action       | Specify the module action and fix it as "save"               | Yes       |         | String | 1.0=2024.07.10 |
| _isNullError | When the insertion value cannot be obtained, whether an error is reported, true: the error exits, false: try to replace it with the default value | No | true | Bool | 1.0=2024.07.10 |
| _defaultValue | The default replacement value when the insertion value cannot be obtained | No | | Object | 1.0=2024.07.10 |
| xxx | Custom data to be inserted into the session | Yes | | Object | 1.0=2024.07.10 |

Supplementary description of module parameters:

### > _isNullError

- When the insertion value cannot be obtained, whether the error is reported, true: the error exits, false: try to replace it with the default value
- If `_isNullError` is true, when the value of the inserted session cannot be obtained, an error will be reported to exit immediately, ignoring the setting of `_defaultValue`

### > _defaultValue

- The default replacement value when the insertion value cannot be obtained
- This setting will take effect when `_isNullError` is `false`

- When `_isNullError` is `false` and the setting of `_defaultValue` is omitted, the module will skip the saving of this value

### > xxx

- Custom data to be inserted into the session
- Do not prefix the custom key with `get##`, otherwise you may not be able to get or delete this session value

- If the custom key is the same as the key of the existing session, it will overwrite the existing data

- Session is a single-layer map, so if the data is overwritten, the whole data will be overwritten

- Please plan the key of the session data to avoid confusion in the exaggerated application environment (shared session)

- The corresponding value can be Object, but it must be a type supported by Json, such as Bool, Int, Long, String, Array, Object

- If you want to dynamically obtain the value from `passParam` as the set value, please use `get##key` as the setting value, such as "get##key"

- If the value you want to get is in the iner layer of passParam (nested json), please locate it with `>>`, such as `get##key_1>>key_2`

# ※ delete-delete session information

- Add version: 1.0=2024.07.10

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

| key     | Description                                           | Necessary | Default | Type   | Add/Update     |
| ------- | ----------------------------------------------------- | --------- | ------- | ------ | -------------- |
| _action | Specify the module action and fix it as "delete"      | Yes       |         | String | 1.0=2024.07.10 |
| _key    | The key of the session value that needs to be deleted | Yes       |         | Array  | 1.0=2024.07.10 |

Supplementary description of module parameters:

### > _key

- The key of the session value that needs to be deleted

- Set the value to a String array. If it contains a non-String element, the module will skip this element

- If you want to dynamically obtain the value from `passParam` as the set value, please use `get##key` as the setting value, such as "get##key". The obtained value must still be of String type, otherwise the module will skip this element

- If the value you want to get is in the iner layer of passParam (nested json), please locate it with `>>`, such as `get##key_1>>key_2`

- It is necessary to be careful to delete the session value to avoid causing confusion in the praise of the application environment (shared session)

# ◎ Configuration Notes

The module configuration is set in `_OperSession/Once.Config`. When the application is run through Christmas, it will be automatically spliced to `config/application.properties`

| Key    | Description                           | Necessary | Default | Type   | Add/Update     |
| ------ | ------------------------------------- | --------- | ------- | ------ | -------------- |
|      |             |           |         |      |            |

Supplementary description of module parameters:

- You can set the settings for the session in Spring boot here
- However, if there is a `_ConfigSession` module, the relevant settings of the Session must be concentrated in the `_ConfigSession` module, otherwise it may cause settings pollution (such as mutual coverage and so on)

# ◎ Module test separately

In the project root directory, run the shell instruction

```
python3 Christmas/Christmas.py ShellExcute/Compile#TestModule _OperSession
```

You can also run ShellExcute>>Compile#TestModule through the Christmas plug-in, and enter `_OperSession` in the terminal opened by the plug-in.

### > Sample

- URL：http://127.0.0.1:8080/once/module/sample

- Request method：POST

- Content type: application/json

    ```
    {
    
	}
    ```
- Expected results: The running terminal will print the session value, insert key1/key2/key3, and then delete key1

    ```
    > Save session
    - key:key 3, value:{"aa":"bb"}
    - key:key 2, value:{"key":"value"}
    - key:key 1, value:value
    
    > After delete session
    - key:key 3, value:{"aa":"bb"}
    - key:key 2, value:{"key":"value"}
    ```
    
    Request return:
    
    ```
    {
        "code": "200",
        "message": "OK"
    }
    ```

# ◎ Update list

**1.0=2024.07.10**

- Module create