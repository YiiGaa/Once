# _DataCheck-检查passParam数据、值范围

主要功能包括：

- 检查passParam的数据、值范围

模块参数的值说明：

- 如果希望设置的值为固定值，则直接设置对应类型的值即可
- 如果希望从`passParam`中动态获取值作为设置的值，则请以`get##key`作为模块参数的设置值
- 如果想要获取的值在passParam的里层（嵌套json），则请以`>>`定位，如`get##key_1>>key_2`

# ※ 检查passParam的数据、值范围
- 组入版本：1.0=2024.06.15

```json
{
	"name":"_DataCheck",
	"param":{
		"_isClean":true,
		"_param":{
			"key-1":"int##0/1",
			"key-2":"reg##123",
			"key-3":true,
			"key-4":"str##test/best",
			"key-5":"double##3.5/5.4",
			"opt##key-6":"uuid",
			"key-7":"not##abc/bcd",
			"key-8":{
				"key-8-1":true
			}
    }
  }
}
```

| key      | 说明                                       | 是否必要 | 默认值 | 类型   | 组入/更新版本  |
| -------- | ------------------------------------------ | -------- | ------ | ------ | -------------- |
| _isClean | 是否清空多余字段，false:不清理，false:清理 | 否       | false  | Bool   | 1.0=2024.06.15 |
| _param   | 检查passParam的模板                        | 是       |        | Object | 1.0=2024.06.15 |

模块参数补充说明：

### > _isClean

- 是否清空多余字段，false:不清理，false:清理
- 检查模板中没有设置的Key，都会被清理掉，主要是为了清理不必要的参数（安全性考虑）

### > _param

- 检查passParam的模板，值类型为Json Object

- 模版Json的key，为需要检查的passParam的key，默认情况下，passParam不包含此key，会报错

- 模版Json的key，可以加上`nec##`或`opt##`作为前缀，以标注key是否为必要，`nec##`为必要（默认），`opt##`为非必要。如果外层key设置为`opt##`，内层key设置为`nec##`，则判断passParam存在外层key后，会继续检查内层key

- 模版Json的key，对应的value为数组`[]`，可添加`数字##`前缀，表示数组最多含有多少个元素，如`10##key`，表示数组最多包含10个元素。若自定义key中包含`opt##`、`nec##`前缀，则`数字##`前缀必须在它们的后面，如`opt##10##key`

- 模版Json的key，可以用`>>`定位嵌套关系，如`xxx>>yyy>>1>>zz`，表示定位到`zz`字段。

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

- 采用`>>`定位时，默认`数字`表示数组的index，字符串为对象的key。在Clean模式下，应避免使用`数字`定位，不然模块会认为这是一个对象字段

    ```
    #原来的结构
    {
    	"xxx":[
    		"xx",
    		"yyy"
    	]
    }
    
    #采用数字定位到yyy
    xxx>>1
    
    #Clean模式下，生成的结果
    {
    	"xxx":{
    		"1":{
          "key":"value"
        }
    	}
    }
    ```

- 模版Json的value，为可选的值范围，用`/`隔开，如`ab/sample test/c`，表示允许值为`ab`、`sample test`、`c`

- 模版Json的value，若设置为`""`，表示不限制值范围、类型

- 模版Json的value，可以为bool、int、double、Long类型，表示固定值

- 模版Json的value，可添加`int##`、`str##`、`double##`、`long##`作为前缀，以检查值类型，`int##`为数字(包括bool型，true为1，false为0)，`str##`为字符串（默认），`double##`为浮点型，`long##`为长整型。如`double##3.5/8.3`表示值要求为3.5或者8.3

- 模版Json的value，若仅包含类型限制，如`int##`、`str##`，则模块只进行类型判断，不限制具体的值

- 模版Json的value，可添加`reg##`作为前缀，以采用正则表达式的方式检查值，如`reg##[a-z]`，其中`[a-z]`为正则表达式。需要注意的是，以正则表达式方式校验时，默认为字符串类型。若需要检查数字类型，则设置为`int##reg##[0-9]`，表示`0-9`的数字

- 模版Json的value，可添加`not##`作为前缀，表示检查结果取反

- 模版Json的value，包含多个前缀时，应该注意顺序，正确顺序为`值类型##reg##not##`

- 模版Json的value，可以为`{}`，表示值类型限制为Json对象，如果为空`{}`，表示不限制内部数据

- 模版Json的value为`{}`，可以在内部设置值的取值范围，如`{"opt##xx":"1/2/3", "nec##yy":"1/2/3"}`

- 模版Json的value为`[]`，表示值类型限制为Json数组，如果为空`[]`，表示不限制内部数据

- 模版Json的value为`[]`，可以在内部设置值的取值范围，如`["1","2",{"key":"value"}]`。只要符合一个元素设置，都可以通过检查。若设置了取值范围，则数组不能为空。

# ◎ 模块单独测试

在工程根目录，运行shell指令

```
python3 Christmas/Christmas.py ShellExcute/Compile#TestModule _DataCheck
```

也可以通过`Christmas插件`运行`ShellExcute>>Compile#TestModule`，并在插件打开的终端中输入`_DataCheck`

### > 测试例子

- URL：http://127.0.0.1:8080/once/module/sample

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
        "key-1":12345666L,
        "key-2":123,
        "key-3":true,
        "key-4":"best",
        "key-5":5.4,
        "key-6":"uuid",
        "key-7":"ccc",
        "key-8":{
            "key-8-1":true,
            "key-8-2":[
                "123",
                "456"
            ],
            "key-8-3":[
                {"key-8-3-1":"value","key-8-3-2":"value","key-8-3-3":123},
                {"key-8-3-1":"value","key-8-3-2":"value","key-8-3-3":123},
                true
            ],
            "key-8-4":{
                "abc":{
                    "x":"123"
                },
                "aaa":{
                    "xx":[
                        "true",
                        {"xx":"yy"}
                    ]
                }
            }
        },
        "key-9":[
            {"key-9-1":"value","key-9-2":"value","key-9-3":123},
            true,
            123
        ]
    }
    ```

- 期待结果，请求返回：

    ```
    {
        "code": "200",
        "message": "OK"
    }
    ```

# ◎ 更新列表

**1.0=2024.06.15**

- 模块建立