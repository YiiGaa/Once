# _DataCheck-Check the data and value range of passParam


The main functions include:

- Check the data and value range of passParam

Description of the value of module parameters:

- If the value you want to set is a fixed value, you can directly set the value of the corresponding type.

- If you want to dynamically obtain the value from `passParam` as the set value, please use `get##key` as the setting value of the module parameter.

- If the value you want to get is in the inner layer of passParam (nested json), please locate it with `>>`, such as `get##key_1>>key_2`

# ※ Check the data and value range of passParam

- Add version: 1.0=2024.06.15

```json
	{
		"name":"DataCheck",
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

| Key      | Description                                                  | Necessary | Default | Type   | Add/Update     |
| -------- | ------------------------------------------------------------ | --------- | ------- | ------ | -------------- |
| _isClean | Whether to clean redundant fields, false: not cleaned, true: cleaned | No        | false   | Bool   | 1.0=2024.06.15 |
| _param   | Check the template of passParam                              | Yes       |         | Object | 1.0=2024.06.15 |

Supplementary description of module parameters:

### > _isClean

- Whether to clean redundant fields, false: not cleaned, true: cleaned

- Keys that are not set in the check template will be cleaned up, mainly to clean up unnecessary parameters (security considerations)

### > _param

- Template for modifying data, type Json Object

- The key of the template Json is the key of passParam that needs to be checked. By default, passParam does not contain this key, and an error will be reported

- For the key of the template Json, you can add `nec##` or `opt##` as the prefix to mark whether the key is necessary, `nec##` is necessary (default), and `opt##` is not necessary. If the outer key is set to `opt##` and the inner key is set to `nec##`, it will continue to check the inner key after it is judged that passParam has an outer key

- The key of the template Json, the corresponding value is the array `[]`, and you can add the `number##` prefix, indicating the maximum number of elements in the array, such as `10##key`, which means that the array contains up to 10 elements. If the custom key contains `opt##` and `nec##` prefixes, then the `number##` prefix must be after them, such as `opt##10##key`

- The key of the template Json can use `>>` to locate the nested relationship, such as `xxx>>yyy>>1>>zz`, indicating that it is positioned to the `zz` field.

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

- When using `>>` positioning, the default `number` represents the index of the array, and the string is the key of the object. In Clean mode, you should avoid using `number` positioning, otherwise the module will think that this is an object field

     ```
    #The original structure
    {
    	"xxx":[
    		"xx",
    		"yyy"
    	]
    }
    
    #Use digital positioning to yyy
    xxx>>1
    
    #Results generated in Clean mode
    {
    	"xxx":{
    		"1":{
          "key":"value"
        }
    	}
    }
    ```

- The value of the template Json is an optional range of values, separated by `/`, such as `ab/sample test/c`, which means that the allowed values are `ab`, `sample test`, `c`

- The value of the template Json is set to `""`, it means that there is no limit on the range and type of value

- The value of the template Json can be bool, int, double, Long type, represents a fixed value

- The value of the template Json can add `int##`, `str##`, `double` as prefixes to check the value type, `int#` as the number (including bool type, true for 1, false for 0), and `str##` as the string (mute Recognize), `double##` is a floating point type, `long##` is a long integer. For example, `double##3.5/8.3` indicates that the value requirement is 3.5 or 8.3

- The value of the template Json can add `reg##` as a prefix to check values in the form of regular expressions, such as `reg##[a-z]`, where `[a-z]` is a regular expression. It should be noted that when verifying in the form of regular expressions, it defaults to the string type. If you need to check the number type, set it to `int##reg##[0-9]`, which means `0-9`

- The value of the template Json can add `not##` as the prefix, indicating that the check result is reversed.

- When the value of the template Json contains multiple prefixes, attention should be paid to the order. The correct order is `value type##reg##not##`

- The value of the template Json can be `{}`, which means that the value type is limited to the Json object. If it is empty `{}`, it means that the internal data is not limited

- The value of the template Json is `{}`, which can set the value range internally, such as `{"opt##xx":"1/2/3", "nec##yy":"1/2/3"}`

- The value of the template Json is `[]`, which means that the value type is limited to the Json array. If it is empty `[]`, it means that the internal data is not limited

- The value of the template Json is `[]`, and the value range can be set internally, such as `["1","2",{"key":"value"}]`. As long as it meets an element setting, it can be checked

# ◎ Module test separately

In the project root directory, run the shell instruction

```
python3 Christmas/Christmas.py ShellExcute/Compile#TestModule _DataCheck
```

You can also run ShellExcute>>Compile#TestModule through the Christmas plug-in, and enter `_DataCheck` in the terminal opened by the plug-in

### > Sample

- URL：http://127.0.0.1:8080/once/module/sample

- Request method：POST

- Content type: application/json

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

- Expected results, Request return:
    ```
    {
        "code": "200",
        "message": "OK"
    }
    ```

# ◎ Update list

**1.0=2024.06.15**

- Module create