# _DataFilling-Modify the data of passParam/returnParam

The main functions include:

- Modify the data of passParam/returnParam
- Generate uuid, get time, random number, get session value, get header value

Description of the value of module parameters:

- If the value you want to set is a fixed value, you can directly set the value of the corresponding type.

- If you want to dynamically obtain the value from `passParam` as the set value, please use `get##key` as the setting value of the module parameter.

- If the value you want to get is in the inner layer of passParam (nested json), please locate it with `>>`, such as `get##key_1>>key_2`

# ※ Modify the data of passParam/returnParam

- Add version: 1.0=2024.06.10

```json
{
	"name":"_DataFilling",
	"param":{
      "_isSetReturnParam":false,
      "_setting":{
          "key-1":"uuid",
          "opt##key-2":4.5
     }
  }
}
```

| Key                 | Description                                                  | Necessary | Default | Type   | Add/Update     |
| ------------------- | ------------------------------------------------------------ | --------- | ------- | ------ | -------------- |
| _isSetReturnParam   | Whether to set returnParam, false: set passParam, true: set returnParam | No        | false   | Bool   | 1.0=2024.06.10 |
| _isSessionNullError | When obtaining the Session, if the corresponding Session value is missing, whether the error is reported, false: no error, use "null" as the corresponding value, true: error exit | No        | true    | Bool   | 1.0=2024.06.10 |
| _setting            | Template for modifying data                                  | Yes       |         | Object | 1.0=2024.06.10 |

Supplementary description of module parameters:

### > _setting

- Template for modifying data, type Json Object

- The module does not directly overwrite the target data pool, but traverses the Json Object and inserts the data into the target data pool one by one (passParam/returnParam)

- If you want to clean/limit the data of the data pool (passParam), please use the `_DataCheck` module

- Templates can set up multi-layer nested relationships, but in order to make it easier to understand, please try to limit it to a single layer

- The following is a detailed description of the Key/Value of the template

### > Key description of the template

- The Key corresponds to the key of the target (returnParam or passParam)

- The Key can add `nec##` or `opt##` as the prefix to indicate whether to force the modification of parameters, `nec#` is the forced modification (default), and `opt##` is non-compulsory (not modified when it exists)

- The key can be dynamically defined by using the syntax of `Value of the template`, and can be wrapped with `@@`. For example, `key_@get##id@`, the module will automatically replace the data of `@get##id@` part. The internal syntax description of `@@` refers to the special description of `Value description of the template`.

- Key can use `>>` to locate the nested relationship, such as `xxx>>yyy>>1>>zz`, indicating that it is located to the `zz` field

    ```
    {
    	"xxx":{
    		"yyy":[
    			{},
    			{"zz":""}
    		]
    	}
    }
    ```

- When using `>>` positioning, the default `number` represents the index of the array, and the string is the key of the object. If the multi-layer location is not found, the object will be automatically created.

- The value corresponding to key is the array `[]`, and the target data is an empty array `[]` (or the field does not exist), then it will be inserted data.

    ```
    #setting
    {
    	"xxx":[
    		"key",
    		{"key":"value"}
    	]
    }
    
    #The target data is an empty array, or the field xxx does not exist
    {
    		"xxx":[]
    }
    
    #Data results
    {
    	"xxx":[
    		"key",
    		{"key":"value"}
    	]
    }
    ```

- When the value corresponding to Key is the array `[]`, and the target data is a non-empty array `[]`, the target array will be overwritten

- When the value corresponding to the key is the array `[]`, and the target data is a non-empty array `[]`, the prefix `push##` can be added, which is the inserted data. When the key has multiple prefixes, the correct prefix order is `opt##push##key`

    ```
    #setting
    {
    	"push##xxx":[
    		"key",
    		{"key":"value"}
    	]
    }
    
    #Target data, non-empty array
    {
    		"xxx":[
    			"yyy"
    		]
    }
    
    #Data results
    {
    	"xxx":[
    		"yyy",
    		"key",
    		{"key":"value"}
    	]
    }
    ```

### > Value description of the template

- Value is the modified value, and Value can be int, double, bool, string, {}, [] and other explicit values

- Value is set to `null`, which means that this Key will be deleted

- When the Value is set to `{}`、`[]`, the inner key and value can be set internally to set the deep data modification

- When the Value is set to String, the processing data expression can be set to achieve complex data settings

- Examples of expressions for processing data: get##key>>key_1+uuid

- The data processing fragment is divided by `+`, and each data processing fragment is processed separately. After processing, multiple fragments are stitched into String

    > When you need to output `+`, use the `++` , when you need to output `++`, use the `+++` , and so on. Add version: 1.0=2024.06.10

- Each data processing fragment is divided by `##`. The previous keyword is the function, and the following keyword is the parameter setting of the corresponding function. Some functions do not require parameter setting, and `##` can be ignored

- If the following functions cannot be matched, it will be directly used as a spliced string

    | Function   | Description                                     | Setting                                                      | Add/Update     |
    | ---------- | ----------------------------------------------- | ------------------------------------------------------------ | -------------- |
    | uuid       | 32-bit uuid                                     | -                                                            | 1.0=2024.06.10 |
    | uuid short | short uuid                                      | -                                                            | 1.0=2024.06.10 |
    | random id  | Random number (default 8 bits)                  | Random number length (default 8 bits)                        | 1.0=2024.06.10 |
    | session    | Get the value from the session                  | The key of the session. If it does not exist and `_isSessionNullError` is set to false, the "null" string is used | 1.0=2024.06.10 |
    | header     | Get the value from the request header           | The key of header. If it does not exist, the "null" string is used | 1.0=2024.06.10 |
    | get        | Get the value from passParam                    | The key in passParam, and it can be used to locate multi-layer search. If it does not exist, use the "null" string | 1.0=2024.06.10 |
    | time       | Time, the default format is yyyy-MM-dd HH:mm:ss | Time format                                                  | 1.0=2024.06.10 |

# ◎ Configuration Notes

The module configuration is set in `_DataFilling/Once.Config`. When the application is run through Christmas, it will be automatically spliced to `config/application.properties`

| Key    | Description                           | Necessary | Default | Type   | Add/Update     |
| ------ | ------------------------------------- | --------- | ------- | ------ | -------------- |
|      |             |           |         |      |            |

Supplementary description of module parameters:

# ◎ Module test separately

In the project root directory, run the shell instruction

```
python3 Christmas/Christmas.py ShellExcute/Compile#TestModule _DataFilling
```

You can also run ShellExcute>>Compile#TestModule through the Christmas plug-in, and enter `_DataFilling` in the terminal opened by the plug-in

### > Sample

- URL：http://127.0.0.1:8080/once/module/sample

- Request method：POST

- Content type: application/json

    ```
    {
        "key-1":{
	        "key-1-1":"value"
        },
        "key-2":2,
        "key-3":"value",
        "key-4":{
            "key-4-1":true,
            "key-4-2":[
                {"key-4-2-2":"2"},
                true
            ]
        },
        "key-5":[
            {"key-5-1":"1", "key-5-2":"2"}
        ],
        "key-6":{
            "key-6-1":[
                "1"
                {"key-6-1-1":"123"}
            ]
        }
    }
    ```

- Expected results, Request return:

    ```
    {
        "key-2": [],
        "key-3": 3.5,
        "key-4": {
            "key-4-1": 3.4,
            "key-4-2": [
                {
                    "key-4-2-1": "value"
                },
                false
            ]
        },
        "key-5": [
            "123",
            true
        ],
        "key-6": {
            "key-6-1": {
                "1": {
                    "key-6-1-1": false
                }
            }
        },
        "key-7": "b8692c9e0bb84ad48c36d806eb3b3f10",
        "key-8": "s4rtekrduwdi",
        "key-9": "RQLhlEOJ",
        "key-10": "value",
        "key-1value": "2024-07-16 21:16:40",
        "code": "200",
        "message": "OK"
    }
    ```

# ◎ Update list

**1.0=2024.06.10**

- Module create