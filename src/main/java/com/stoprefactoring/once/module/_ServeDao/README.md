# _ServeDao-MySQL数据库操作

主要功能包括：
- insert-插入数据
- insert batch-批量插入数据
- delete-删除数据
- update-更新数据
- select object-查询数据
- select list-查询数据列表
- sql update-SQL语句更新（插入/删除/更新数据）
- sql select-SQL语句查询
- txn start-开启数据库事务
- txn end-结束数据库事务

模块使用前必要修改：

- 设置MySQL数据库连接信息，在`_ServeDao/Once.Config`文件中进行设置
- 如果开启自动获取表信息的功能（默认开启），要求数据为MySQL8

模块参数的值说明：

- 如果希望设置的值为固定值，则直接设置对应类型的值即可
- 如果希望从`passParam`中动态获取值作为设置的值，则请以`get##key`作为模块参数的设置值
- 如果想要获取的值在passParam的里层（嵌套json），则请以`>>`定位，如`get##key_1>>key_2`

# ※ insert-插入数据

- 组入版本：1.0=2024.06.20
- 要求开启自动获取表字段功能，不然此功能无效

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"insert",
    "_table":"table name",
    "_resultKey":"result",
    "_isPutReturn":false,
    "_isCheckAffect":true
	}
}
```

| key            | 说明                                                         | 是否必要 | 默认值                | 类型   | 组入/更新版本  |
| -------------- | ------------------------------------------------------------ | -------- | --------------------- | ------ | -------------- |
| _action        | 指定模块行为，固定为"insert"                                 | 是       |                       | String | 1.0=2024.06.20 |
| _table         | 数据库表名                                                   | 是       |                       | String | 1.0=2024.06.20 |
| _target        | 目标数据                                                     | 否       | 以passParam为目标数据 | Object | 1.0=2024.06.20 |
| _isReplace     | 插入数据的SQL是否使用`REPLACE`开头，true:使用`REPLACE`，false:使用`INSERT` | 否       | false                 | Bool   | 1.0=2024.06.20 |
| _resultKey     | 记录结果的Key                                                | 否       | 不记录结果            | String | 1.0=2024.06.20 |
| _isPutReturn   | 是否将结果记录在returnParam，true:记录在returnParam，false:记录在passParam | 否       | true                  | Bool   | 1.0=2024.06.20 |
| _isCheckAffect | 是否检查执行结果，true:检查(影响行数<=0报错)，false:不检查   | 否       | true                  | Bool   | 1.0=2024.06.20 |

模块参数补充说明：

### > _table

- 数据库表名
- 数据库表必须是普通的表，不能是视图

### > _target

- 目标数据，类型为Json Object
- 默认以`passParam`作为目标数据
- 模块会根据自动获取的表字段，从目标数据中提取插入的值
- 当表字段要求不为空，且不能从目标数据中提取时，模块会报错
- 表字段可以为空有3种情况，设置表字段可以为空、设置表字段默认值、设置表字段自增
- 当表字段为文本类型时，模块会检查数据长度，超长会报错
- tinytext、text、mediumtext、longtext、char、varchar类型的数据库表字段，都会被认为是文本类型
- 以上文本类型的最大长度为，tinytext:255字节，text:65,535字节，mediumtext:16,777,215字节(16MB)，longtext:4,294,967,295字节(4GB)，char、varchar:自定义长度
- 为了防止SQL注入，在拼接数据时，模块会用`''`包裹数据，且数据中的`'`都被转义

### > _isReplace

- 插入数据的SQL是否使用`REPLACE`开头，true:使用`REPLACE`，false:使用`INSERT`
- `INSERT`在插入数据时，如果已经存在相同主键，会抛出错误
- `REPLACE`在插入数据时，如果已经存在相同主键，会更新数据，如果不存在相同主键，则插入新数据
- `REPLACE`的更新操作不完全等于`UPDATE`操作，`REPLACE`的更新操作实际上是先删除数据再插入数据

### > _resultKey

- 记录结果的Key
- 省略设置，或设置为空`""`，模块都不会记录结果
- 插入数据的结果为更新行数
- 当使用`REPLACE`插入数据，且主键已经存在时，更新行数为2

# ※ insert batch-批量插入数据

- 组入版本：1.0=2024.06.20
- 要求开启自动获取表字段功能，不然此功能无效

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"insert batch",
    "_target":"get##insertList",
    "_table":"table name",
    "_resultKey":"result",
    "_isPutReturn":false,
    "_isCheckAffect":true
	}
}
```

| key            | 说明                                                         | 是否必要 | 默认值     | 类型   | 组入/更新版本  |
| -------------- | ------------------------------------------------------------ | -------- | ---------- | ------ | -------------- |
| _action        | 指定模块行为，固定为"insert batch"                           | 是       |            | String | 1.0=2024.06.20 |
| _table         | 数据库表名                                                   | 是       |            | String | 1.0=2024.06.20 |
| _target        | 目标数据                                                     | 是       |            | Array  | 1.0=2024.06.20 |
| _isReplace     | 插入数据的SQL是否使用`REPLACE`开头，true:使用`REPLACE`，false:使用`INSERT` | 否       | false      | Bool   | 1.0=2024.06.20 |
| _resultKey     | 记录结果的Key                                                | 否       | 不记录结果 | String | 1.0=2024.06.20 |
| _isPutReturn   | 是否将结果记录在returnParam，true:记录在returnParam，false:记录在passParam | 否       | true       | Bool   | 1.0=2024.06.20 |
| _isCheckAffect | 是否检查执行结果，true:检查(影响行数<=0报错)，false:不检查   | 否       | true       | Bool   | 1.0=2024.06.20 |

模块参数补充说明：

### > _table

- 数据库表名
- 数据库表必须是普通的表，不能是视图

### > _target

- 目标数据，类型为Json Array

    ```
    [
    		{"key 1-1":"value 1-1", "key 1-2": "value 1-2"},
    		{"key 2-1":"value 2-1", "key 2-2": "value 2-2"}
    ]
    ```

- 模块会根据自动获取的表字段，从目标数据中提取插入的值

- 当表字段要求不为空，且不能从目标数据中提取时，模块会报错

- 表字段可以为空有3种情况，设置表字段可以为空、设置表字段默认值、设置表字段自增

- 当表字段为文本类型时，模块会检查数据长度，超长会报错

- tinytext、text、mediumtext、longtext、char、varchar类型的数据库表字段，都会被认为是文本类型

- 以上文本类型的最大长度为，tinytext:255字节，text:65,535字节，mediumtext:16,777,215字节(16MB)，longtext:4,294,967,295字节(4GB)，char、varchar:自定义长度

- 为了防止SQL注入，在拼接数据时，模块会用`''`包裹数据，且数据中的`'`都被转义

### > _isReplace

- 插入数据的SQL是否使用`REPLACE`开头，true:使用`REPLACE`，false:使用`INSERT`
- `INSERT`在插入数据时，如果已经存在相同主键，会抛出错误
- `REPLACE`在插入数据时，如果已经存在相同主键，会更新数据，如果不存在相同主键，则插入新数据
- `REPLACE`的更新操作不完全等于`UPDATE`操作，`REPLACE`的更新操作实际上是先删除数据再插入数据

### > _resultKey

- 记录结果的Key
- 省略设置，或设置为空`""`，模块都不会记录结果
- 插入数据的结果为更新行数
- 当使用`REPLACE`插入数据，且主键已经存在时，单个插入数据的更新行数为2

# ※ delete-删除数据

- 组入版本：1.0=2024.06.20
- 要求开启自动获取表字段功能，不然此功能无效

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"delete",
    "_table":"table name",
    "_isCheckAffect":true,
    "_resultKey":"result",
    "_isPutReturn":true
	}
}
```

| key                 | 说明                                                         | 是否必要 | 默认值                | 类型   | 组入/更新版本  |
| ------------------- | ------------------------------------------------------------ | -------- | --------------------- | ------ | -------------- |
| _action             | 指定模块行为，固定为"delete"                                 | 是       |                       | String | 1.0=2024.06.20 |
| _table              | 数据库表名                                                   | 是       |                       | String | 1.0=2024.06.20 |
| _target             | 目标数据                                                     | 否       | 以passParam为目标数据 | Object | 1.0=2024.06.20 |
| _filter             | 用于判断的SQL碎片（DELETE SQL的WHERE部分）                   | 否       | 自动拼接              | String | 1.0=2024.06.20 |
| _isSQLReplaceStrict | 动态替换SQL碎片数据时，是否以严格方式替换，true:严格替换，false:不检查字符 | 否       | true                  | Bool   | 1.0=2024.06.20 |
| _resultKey          | 记录结果的Key                                                | 否       | 不记录结果            | String | 1.0=2024.06.20 |
| _isPutReturn        | 是否将结果记录在returnParam，true:记录在returnParam，false:记录在passParam | 否       | true                  | Bool   | 1.0=2024.06.20 |
| _isCheckAffect      | 是否检查执行结果，true:检查(影响行数<=0报错)，false:不检查   | 否       | true                  | Bool   | 1.0=2024.06.20 |

模块参数补充说明：

### > _table

- 数据库表名
- 数据库表必须是普通的表，不能是视图

### > _target

- 目标数据，类型为Json Object
- 默认以`passParam`作为目标数据
- 当设置了`_filter`，将无视此设置
- 模块会根据自动获取的表字段，从目标数据中提取删除数据的判断条件（DELETE SQL的WHERE部分）
- 如果数据库表字段对应的目标数据为数组，会以`key IN ('value 1', 'value 2')`的形式拼接值范围
- 如果数据库表字段对应的目标数据为数组以外的类型，则以`key = 'value'`的形式拼接
- 为了防止SQL注入，在拼接数据时，模块会用`''`包裹数据，且数据中的`'`都被转义

### > _filter

- 用于判断的SQL碎片（DELETE SQL的WHERE部分）
- 当`_filter`设置了非空`""`字符串，将忽略`_target`的设置
- 可以使用`@@`标记需要动态替换的数据，详细说明请参考`特别说明-动态替换数据`

### > _isSQLReplaceStrict

- 动态替换SQL碎片数据时，是否以严格方式替换，true:严格替换，false:不检查字符
- 只在`_filter`替换数据时生效
- 严格替换的详细说明参考`特别说明-动态替换数据`

# ※ update-更新数据

- 组入版本：1.0=2024.06.20
- 要求开启自动获取表字段功能，不然此功能无效

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"update",
    "_table":"table name",
    "_isCheckAffect":true,
    "_resultKey":"result",
    "_isPutReturn":true
	}
}
```

| key                 | 说明                                                         | 是否必要 | 默认值                | 类型   | 组入/更新版本  |
| ------------------- | ------------------------------------------------------------ | -------- | --------------------- | ------ | -------------- |
| _action             | 指定模块行为，固定为"update"                                 | 是       |                       | String | 1.0=2024.06.20 |
| _table              | 数据库表名                                                   | 是       |                       | String | 1.0=2024.06.20 |
| _target             | 目标数据                                                     | 否       | 以passParam为目标数据 | Object | 1.0=2024.06.20 |
| _update             | 用于设置更新数据的SQL碎片（UPDATE SQL的SET部分）             | 否       | 自动拼接              | String | 1.0=2024.06.20 |
| _filter             | 用于判断的SQL碎片（UPDATE SQL的WHERE部分）                   | 否       | 自动拼接              | String | 1.0=2024.06.20 |
| _isSQLReplaceStrict | 动态替换SQL碎片数据时，是否以严格方式替换，true:严格替换，false:不检查字符 | 否       | true                  | Bool   | 1.0=2024.06.20 |
| _resultKey          | 记录结果的Key                                                | 否       | 不记录结果            | String | 1.0=2024.06.20 |
| _isPutReturn        | 是否将结果记录在returnParam，true:记录在returnParam，false:记录在passParam | 否       | true                  | Bool   | 1.0=2024.06.20 |
| _isCheckAffect      | 是否检查执行结果，true:检查(影响行数<=0报错)，false:不检查   | 否       | true                  | Bool   | 1.0=2024.06.20 |

模块参数补充说明：

### > _table

- 数据库表名
- 数据库表必须是普通的表，不能是视图

### > _target

- 目标数据，类型为Json Object
- 默认以`passParam`作为目标数据
- 当设置了同时设置了`_filter`和`_update`，将无视此设置
- 模块会根据自动获取的表字段，从目标数据中提取主键数据作为判断条件（UPDATE SQL的WHERE部分），从目标数据中提取其他数据作为设置更新数据部分（UPDATE SQL的SET部分）
- 如果数据库表主键对应的目标数据为数组，会以`key IN ('value 1', 'value 2')`的形式拼接值范围
- 如果数据库表主键对应的目标数据为数组以外的类型，则以`key = 'value'`的形式拼接
- 为了防止SQL注入，在拼接数据时，模块会用`''`包裹数据，且数据中的`'`都被转义

### > _update

- 用于设置更新数据的SQL碎片（UPDATE SQL的SET部分）
- 当`_update`设置了非空`""`字符串，将不再使用`_target`以自动拼接更新数据的SQL碎片（UPDATE SQL的SET部分）
- 可以使用`@@`标记需要动态替换的数据，详细说明请参考`特别说明-动态替换数据`

### > _filter

- 用于判断的SQL碎片（UPDATE SQL的WHERE部分）
- 当`_filter`设置了非空`""`字符串，将不再使用`_target`以自动拼接用作判断的SQL碎片（UPDATE SQL的WHERE部分）
- 可以使用`@@`标记需要动态替换的数据，详细说明请参考`特别说明-动态替换数据`

### > _isSQLReplaceStrict

- 动态替换SQL碎片数据时，是否以严格方式替换，true:严格替换，false:不检查字符
- 只在`_filter`、`_update`替换数据时生效
- 严格替换的详细说明参考`特别说明-动态替换数据`

# ※ select object-查询数据

- 组入版本：1.0=2024.06.20
- 要求开启自动获取表字段功能，不然此功能无效
- 查询结果被限制在第1条数据

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"select object",
    "_table":"table name",
    "_isGetNullError":true,
    "_isPutReturn":true
	}
}
```

| key                 | 说明                                                         | 是否必要 | 默认值                                                  | 类型   | 组入/更新版本  |
| ------------------- | ------------------------------------------------------------ | -------- | ------------------------------------------------------- | ------ | -------------- |
| _action             | 指定模块行为，固定为"select object"                          | 是       |                                                         | String | 1.0=2024.06.20 |
| _table              | 数据库表名                                                   | 是       |                                                         | String | 1.0=2024.06.20 |
| _target             | 目标数据                                                     | 否       | 以passParam为目标数据                                   | Object | 1.0=2024.06.20 |
| _get                | 用于设置更新数据的SQL碎片（SELECT SQL的设置获取字段部分）    | 否       | *（获取全部字段）                                       | String | 1.0=2024.06.20 |
| _filter             | 用于判断的SQL碎片（SELECT SQL的WHERE部分）                   | 否       | 自动拼接                                                | String | 1.0=2024.06.20 |
| _isSQLReplaceStrict | 动态替换SQL碎片数据时，是否以严格方式替换，true:严格替换，false:不检查字符 | 否       | true                                                    | Bool   | 1.0=2024.06.20 |
| _resultKey          | 记录结果的Key                                                | 否       | 将查询结果按字段直接插入数据池（passParam/returnParam） | String | 1.0=2024.06.20 |
| _isPutReturn        | 是否将结果记录在returnParam，true:记录在returnParam，false:记录在passParam | 否       | true                                                    | Bool   | 1.0=2024.06.20 |
| _isGetNullError     | 获取不到数据时是否报错，true:报错，false:不报错              | 否       | false                                                   | Bool   | 1.0=2024.06.20 |
| _isGetNotNullError  | 获取到数据时是否报错，true:报错，false:不报错                | 否       | false                                                   | Bool   | 1.0=2024.06.20 |

模块参数补充说明：

### > _table

- 数据库表名
- 数据库表可以是普通的表，也可以是视图
- 夸表、夸数据库查询，建议使用视图，以降低SQL的复杂度

### > _target

- 目标数据，类型为Json Object
- 默认以`passParam`作为目标数据
- 当设置了设置了`_filter`，将无视此设置
- 模块会根据自动获取的表字段，从目标数据中提取数据作为判断条件（SELECT SQL的WHERE部分）
- 如果数据库表字段对应的目标数据为数组，会以`key IN ('value 1', 'value 2')`的形式拼接值范围
- 如果数据库表字段对应的目标数据为数组以外的类型，则以`key = 'value'`的形式拼接
- 为了防止SQL注入，在拼接数据时，模块会用`''`包裹数据，且数据中的`'`都被转义

### > _get

- 用于设置更新数据的SQL碎片（SELECT SQL的设置获取字段部分）
- 如果省略，相当于`*`，获取全部字段
- 可以使用`@@`标记需要动态替换的数据，详细说明请参考`特别说明-动态替换数据`

### > _filter

- 用于判断的SQL碎片（SELECT SQL的WHERE部分）
- 当`_filter`设置了非空`""`字符串，将不再使用`_target`以自动拼接用作判断的SQL碎片（UPDATE SQL的WHERE部分）
- 可以使用`@@`标记需要动态替换的数据，详细说明请参考`特别说明-动态替换数据`

### > _isSQLReplaceStrict

- 动态替换SQL碎片数据时，是否以严格方式替换，true:严格替换，false:不检查字符
- 只在`_filter`、`_get`替换数据时生效
- 严格替换的详细说明参考`特别说明-动态替换数据`

### > _resultKey

- 记录结果的Key
- 默认情况下，或者将`_resultKey`设置为空字符串`""`，会将查询结果按字段直接插入数据池（passParam/returnParam）
- 查询结果被限制在第1条数据

# ※ select list-查询数据列表

- 组入版本：1.0=2024.06.20
- 要求开启自动获取表字段功能，不然此功能无效
- 用于查询数据列表

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"select list",
    "_countResultKey":"count",
    "_resultKey":"result",
    "_isPutReturn":true
	}
}
```

| key                 | 说明                                                         | 是否必要 | 默认值                | 类型   | 组入/更新版本  |
| ------------------- | ------------------------------------------------------------ | -------- | --------------------- | ------ | -------------- |
| _action             | 指定模块行为，固定为"select list"                            | 是       |                       | String | 1.0=2024.06.20 |
| _table              | 数据库表名                                                   | 是       |                       | String | 1.0=2024.06.20 |
| _target             | 目标数据                                                     | 否       | 以passParam为目标数据 | Object | 1.0=2024.06.20 |
| _get                | 用于设置更新数据的SQL碎片（SELECT SQL的设置获取字段部分）    | 否       | *（获取全部字段）     | String | 1.0=2024.06.20 |
| _filter             | 用于判断的SQL碎片（SELECT SQL的WHERE部分）                   | 否       | 自动拼接              | String | 1.0=2024.06.20 |
| _orderBy            | 用于排序的SQL碎片（SELECT SQL的ORDER BY部分）                | 否       | 不进行额外排序        | String | 1.0=2024.06.20 |
| _page               | 第几页数据（SELECT SQL的LIMIT部分）                          | 否       | 不限制分页            | Int    | 1.0=2024.06.20 |
| _pageSize           | 每页数据（SELECT SQL的LIMIT部分）                            | 否       | 不限制分页            | Int    | 1.0=2024.06.20 |
| _isSQLReplaceStrict | 动态替换SQL碎片数据时，是否以严格方式替换，true:严格替换，false:不检查字符 | 否       | true                  | Bool   | 1.0=2024.06.20 |
| _countResultKey     | 记录符合条件的总条数                                         | 否       | count                 | String | 1.0=2024.06.20 |
| _resultKey          | 记录结果列表的Key                                            | 否       | result                | String | 1.0=2024.06.20 |
| _isPutReturn        | 是否将结果记录在returnParam，true:记录在returnParam，false:记录在passParam | 否       | true                  | Bool   | 1.0=2024.06.20 |
| _isGetNullError     | 获取不到数据时是否报错，true:报错，false:不报错              | 否       | false                 | Bool   | 1.0=2024.06.20 |
| _isGetNotNullError  | 获取到数据时是否报错，true:报错，false:不报错                | 否       | false                 | Bool   | 1.0=2024.06.20 |

模块参数补充说明：

### > _table

- 数据库表名
- 数据库表可以是普通的表，也可以是视图
- 夸表、夸数据库查询，建议使用视图，以降低SQL的复杂度

### > _target

- 目标数据，类型为Json Object
- 默认以`passParam`作为目标数据
- 当设置了`_filter`，将无视此设置
- 模块会根据自动获取的表字段，从目标数据中提取数据作为判断条件（SELECT SQL的WHERE部分）
- 如果数据库表字段对应的目标数据为数组，会以`key IN ('value 1', 'value 2')`的形式拼接值范围
- 如果数据库表字段对应的目标数据为数组以外的类型，则以`key = 'value'`的形式拼接
- 为了防止SQL注入，在拼接数据时，模块会用`''`包裹数据，且数据中的`'`都被转义

### > _get

- 用于设置更新数据的SQL碎片（SELECT SQL的设置获取字段部分）
- 如果省略，相当于`*`，获取全部字段
- 可以使用`@@`标记需要动态替换的数据，详细说明请参考`特别说明-动态替换数据`

### > _filter

- 用于判断的SQL碎片（SELECT SQL的WHERE部分）
- 当`_filter`设置了非空`""`字符串，将不再使用`_target`以自动拼接用作判断的SQL碎片（SELECT SQL的WHERE部分）
- 可以使用`@@`标记需要动态替换的数据，详细说明请参考`特别说明-动态替换数据`

### > _orderBy

- 用于排序的SQL碎片（SELECT SQL的ORDER BY部分）
- 默认情况下不进行额外排序
- 这部分要充分考虑SQL注入风险，如果排列字段由客户端传入，请用`_DataCheck`模块检查合法性
- 可以使用`@@`标记需要动态替换的数据，详细说明请参考`特别说明-动态替换数据`

### > \_page、\_pageSize

- 用于设置分页（SELECT SQL的LIMIT部分）
- \_page、\_pageSize都大于等于0时，才会生效
- \_page从0开始

### > _isSQLReplaceStrict

- 动态替换SQL碎片数据时，是否以严格方式替换，true:严格替换，false:不检查字符
- 只在`_filter`、`_get`、`_orderBy`替换数据时生效
- 严格替换的详细说明参考`特别说明-动态替换数据`

### > _countResultKey

- 记录符合条件的总条数
- 总条数会忽略分页的限制
- 当`_countResultKey`设置为`""`空字符串，将不记录符合条件的总条数

### > _resultKey

- 记录结果列表的Key
- 当`_resultKey`设置为`""`空字符串，将不记录符合结果列表

# ※ sql update-SQL语句更新（插入/删除/更新数据）

- 组入版本：1.0=2024.06.20
- 不要求开启自动获取表字段功能

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"sql update",
    "_sql":"",
    "_isCheckAffect":true,
    "_resultKey":"result",
    "_isPutReturn":true
	}
}
```

| key                 | 说明                                                         | 是否必要 | 默认值     | 类型   | 组入/更新版本  |
| ------------------- | ------------------------------------------------------------ | -------- | ---------- | ------ | -------------- |
| _action             | 指定模块行为，固定为"sql update"                             | 是       |            | String | 1.0=2024.06.20 |
| _sql                | 用于更新的SQL语句                                            | 是       |            | String | 1.0=2024.06.20 |
| _isSQLReplaceStrict | 动态替换SQL碎片数据时，是否以严格方式替换，true:严格替换，false:不检查字符 | 否       | true       | Bool   | 1.0=2024.06.20 |
| _resultKey          | 记录结果的Key                                                | 否       | 不记录结果 | String | 1.0=2024.06.20 |
| _isPutReturn        | 是否将结果记录在returnParam，true:记录在returnParam，false:记录在passParam | 否       | true       | Bool   | 1.0=2024.06.20 |
| _isCheckAffect      | 是否检查执行结果，true:检查(影响行数<=0报错)，false:不检查   | 否       | true       | Bool   | 1.0=2024.06.20 |

模块参数补充说明：

### > _sql

- 用于更新的SQL语句
- 请充分考虑SQL注入风险
- 可以使用`@@`标记需要动态替换的数据，详细说明请参考`特别说明-动态替换数据`

### > _isSQLReplaceStrict

- 动态替换用于更新的SQL语句时，是否以严格方式替换，true:严格替换，false:不检查字符
- 只在`_sql`替换数据时生效
- 严格替换的详细说明参考`特别说明-动态替换数据`

# ※ sql select-SQL语句查询

- 组入版本：1.0=2024.06.20
- 不要求开启自动获取表字段功能

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"sql select",
    "_sql":"",
    "_selectType":"select list",
    "_resultKey":"result",
    "_isPutReturn":true
	}
}
```

| key                 | 说明                                                         | 是否必要 | 默认值      | 类型   | 组入/更新版本  |
| ------------------- | ------------------------------------------------------------ | -------- | ----------- | ------ | -------------- |
| _action             | 指定模块行为，固定为"sql select"                             | 是       |             | String | 1.0=2024.06.20 |
| _sql                | 用于查询的SQL语句                                            | 是       |             | String | 1.0=2024.06.20 |
| _selectType         | 指定查询的结果类型                                           | 否       | select list | String | 1.0=2024.06.20 |
| _isSQLReplaceStrict | 动态替换SQL碎片数据时，是否以严格方式替换，true:严格替换，false:不检查字符 | 否       | true        | Bool   | 1.0=2024.06.20 |
| _resultKey          | 记录结果的Key                                                | 否       | 不记录结果  | String | 1.0=2024.06.20 |
| _isPutReturn        | 是否将结果记录在returnParam，true:记录在returnParam，false:记录在passParam | 否       | true        | Bool   | 1.0=2024.06.20 |
| _isGetNullError     | 获取不到数据时是否报错，true:报错，false:不报错              | 否       | false       | Bool   | 1.0=2024.06.20 |
| _isGetNotNullError  | 获取到数据时是否报错，true:报错，false:不报错                | 否       | false       | Bool   | 1.0=2024.06.20 |

模块参数补充说明：

### > _sql

- 用于查询的SQL语句
- 请充分考虑SQL注入风险
- 可以使用`@@`标记需要动态替换的数据，详细说明请参考`特别说明-动态替换数据`

### > _selectType

- 指定查询的结果类型
- 支持4种类型，`select string`、`select int`、`select list`、`select object`
- `select string`，结果为字符串类型
- `select int`，结果为数字类型
- `select list`，结果为列表类型，默认
- `select object`，结果为对象类型，查询结果被限制在第1条数据

### > _resultKey

- 记录结果的Key
- 省略此设置，或者设置为空字符串`""`，默认将不记录结果
- 如果`_selectType`被设置为`select object`，且`_resultKey`省略或设置为空字符串`""`，将查询结果按字段直接插入数据池（passParam/returnParam）

### > _isSQLReplaceStrict

- 动态替换用于查询的SQL语句时，是否以严格方式替换，true:严格替换，false:不检查字符
- 只在`_sql`替换数据时生效
- 严格替换的详细说明参考`特别说明-动态替换数据`

# ※ txn start-开启数据库事务

- 组入版本：1.0=2024.06.20
- 不要求开启自动获取表字段功能
- 请使用`InnoDB`等支持事务的MySQL存储引擎，`InnoDB`是MySQL8默认的存储引擎
- 上一个事务未结束前，不可开启新事务（模块报错）

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"txn start"
	}
}
```

| key                  | 说明                                                         | 是否必要 | 默认值       | 类型   | 组入/更新版本  |
| -------------------- | ------------------------------------------------------------ | -------- | ------------ | ------ | -------------- |
| _action              | 指定模块行为，固定为"txn start"                              | 是       |              | String | 1.0=2024.06.20 |
| _level               | 数据库事务隔离等级                                           | 否       | 默认隔离等级 | String | 1.0=2024.06.20 |
| _isAutoErrorRollBack | 业务逻辑结束时，发生错误是否自动回滚事务，true:发生错误回滚，不发生错误提交，false:不发生错误回滚，发生错误提交 | 否       | true         | Bool   | 1.0=2024.06.20 |

模块参数补充说明：

### > _level

- 数据库事务隔离等级
- 可选数据库事务隔离等级为`read uncommitted`、`read committed`、`repeatable read`、`serializable`
- `read uncommitted`，读未提交。允许读取未提交的数据，可能会导致脏读，即读取到其他事务未提交的数据
- `read committed`，读已提交。只能读取到其他事务已经提交的数据，可以避免脏读，但可能会出现不可重复读的问题，即在同一事务中，多次读取同一数据集合时可能会得到不同的结果。
- `repeatable read`，可重复读，默认隔离等级。保证在同一事务中，多次读取同一数据集合的结果是一致的，可以避免不可重复读的问题，但可能会出现幻读，即在事务中新增的数据在事务执行期间被其他事务插入，导致读取到的数据集合不一致
- `serializable`，串行化。最高的隔离级别，完全串行执行事务，避免了脏读、不可重复读和幻读，性能较低，因为需要锁定涉及的所有数据，直到事务完成

### > _isAutoErrorRollBack

- 业务逻辑结束时，发生错误是否自动回滚事务，true:发生错误回滚，不发生错误提交，false:不发生错误回滚，发生错误提交
- 在业务执行过程中，可能因为某个模块报错而中断业务逻辑，业务逻辑中断后，会自动执行业务回滚或提交
- 在`Contoller`中打开的数据库事务，会在`Controller`逻辑结束时，自动结束事务
- 在`Service`中打开的数据库事务，会在`Service`逻辑结束时，自动结束事务
- 事务开启后，可以任意位置（Controller/Service）手动调用`txn end`结束事务

# ※ txn end-结束数据库事务

- 组入版本：1.0=2024.06.20
- 不要求开启自动获取表字段功能

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"txn end",
    "_isCommit":true
	}
}
```

| key | 说明 | 是否必要 | 默认值 | 类型 | 组入/更新版本 |
| --- | --- | --- | --- | --- | --- |
| _action | 指定模块行为，固定为"txn end" | 是 |  | String | 1.0=2024.06.20 |
| _isCommit | 是否提交事务，true:提交，false:回滚 | 否 | true | Bool | 1.0=2024.06.20 |

# ◎ 配置说明

模块配置在`_ServeDao/Once.Config`中设置，通过Christmas运行应用时，会自动拼接到`config/application.properties`

| key                                       | 说明                                                 | 是否必要 | 默认值 | 类型   | 组入/更新版本  |
| ----------------------------------------- | ---------------------------------------------------- | -------- | ------ | ------ | -------------- |
| module._ServeDao.isAutoGenerateMap | 是否开启自动获取表信息，true:开启，fase:关闭 | 否       | true | String | 1.0=2024.06.20 |
| module.sr-servedao.datasource.url | 数据库地址             | 是      |    | String | SpringBoot |
| module.sr-servedao.datasource.username | 数据库用户名 | 是       |        | String | SpringBoot |
| module.sr-servedao.datasource.password | 数据库密码                       | 是      |        | String | SpringBoot |
| module.sr-servedao.datasource.hikari.maximum-pool-size | 连接池中最大的连接数量 | 否       | 10  | Int    | SpringBoot |
| module.sr-servedao.datasource.hikari.minimum-idle | 连接池中最小的连接数量 | 否       | 10 | Int    | SpringBoot |
| module.sr-servedao.datasource.hikari.connection-timeout | 等待获取连接的最大时间（单位：毫秒） | 否 |  | Int | SpringBoot |
| module.sr-servedao.datasource.hikari.max-lifetime | 连接池中连接的最大生命周期（单位：毫秒） | 否 |  | Int | SpringBoot |

配置补充说明：

### > module._ServeDao.isAutoGenerateMap

- 是否开启自动获取表信息，true:开启，fase:关闭
- 要求数据库为`MySQL8`
- 详细机制说明请参考`特别说明-自动获取表字段机制`

### > module.sr-servedao.datasource

- 数据库相关配置
- 这个配置与Springboot的`spring.database`一致，模块仅仅修改了前缀

### > module.sr-servedao.datasource.hikari

- 数据库连接池配置
- 这个配置与Springboot的`spring.database.hikari`一致，模块仅仅修改了前缀
- 连接池的配置对性能有显著提升
- 一般在性能调优时，将MySQL的执行线程数设置为CPU核数的2倍，接收的连接数可以稍大一些，如100（防止会拒绝多个分布式应用的连接）
- 一般单个应用的数据库连接池，设置为10即可，如果是数据库集群，或者数据库服务器性能比较好，可以适当提高设置

# ◎ 特别说明

### > MySQL8创建账号（限制权限）

- 创建账号的SQL，`newuser`为用户名，`%`为客户端调用IP（%为允许所有IP，可以使用`192.%`等限制IP前缀），`your_password`为账号的密码

    ```
    CREATE USER 'newuser'@'%' IDENTIFIED BY 'your_password';
    
    #刷新
    FLUSH PRIVILEGES;
    ```

- 设置账号权限的SQL，`test`为指定数据库名称

    ```
    #一般只添加增删改查的权限即可，可限制删除数据库、数据表、添加临时表等危险操作
    GRANT SELECT, INSERT, UPDATE, DELETE ON test.* TO 'newuser'@'%';
    
    #刷新
    FLUSH PRIVILEGES;
    
    #以下是相关SQL，不需要一并调用
    #添加某个数据库的所有权限
    GRANT ALL PRIVILEGES ON test.* TO 'newuser'@'%';
    #清理某个数据库的所有权限
    REVOKE ALL PRIVILEGES ON test.* FROM 'newuser'@'%';
    #查看权限
    SELECT * FROM mysql.db WHERE Db = 'test' AND User = 'newuser';
    ```

- 如果开启模块自动获取数据库表、视图信息（默认开启），模块会自动使用以下SQL语句获取信息，一般新账号都具备这样的权限，不需要特别设置

    ```
    #查询当前连接的数据库
    SELECT DATABASE();
    
    #获取数据库下的所有数据表/视图，database为数据库名称
    SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE WHERE TABLE_SCHEMA = 'database' AND (TABLE_TYPE='BASE TABLE' or TABLE_TYPE='VIEW')"
    
    #获取某个数据表/视图下的所有字段信息，database为数据库名称，table/view为数据库表/视图名称
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'database' AND TABLE_NAME = 'table/view' "
    ```

### > 动态替换数据

- 使用`@xxx@`标记字符串中需要动态替换的数据
- 数据从`passParam`中获取，如`@key@`
- 如果获取不到数据，则`@xxx@`保留原样
- 如果想要获取的值在passParam的里层（嵌套json），则请以`>>`定位，如`@key_1>>key_2@`
- 如果替换的目标数据为`Json Array`，则会自动开展数据，如`list1,list2,list3`
- 用`'@xxx@'`标记的部分会直接替换为获取到的数据，如`'data value...'`。为了防止SQL盲注，获取到的数据中的`'`会被转义为`''`
- 如果替换的目标数据为Json Array，且用`'@xxx@'`标记，则最终替换的数据为`'list1','list2','list3'`
- 用`@xxx@`标记的部分（前后不包含`''`）也会直接替换为获取到的数据，但是为了防止SQL注入，在`_isSQLReplaceStrict`设置为true（默认为true）时，只允许数据中含有大小写字母/数字/_/$，如果存在除此以外的字符，模块会报错
- 为了规避SQL盲注风险，请尽量使用`'@xxx@'`，而不要使用`@xxx@`标记除数据以外的SQL部分，如数据库等

### > 自动获取表字段机制

- 自动获取表字段，需要在`_ServeDao/Once.Config`文件中设置

    ```
    module._ServeDao.isAutoGenerateMap=true
    ```

- 程序启动时，模块会自动执行以下语句，获取数据库表/视图的信息

    ```
    #查询当前连接的数据库
    SELECT DATABASE();
    
    #获取数据库下的所有数据表/视图，database为数据库名称
    SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE WHERE TABLE_SCHEMA = 'database' AND (TABLE_TYPE='BASE TABLE' or TABLE_TYPE='VIEW')"
    
    #获取某个数据表/视图下的所有字段信息，database为数据库名称，table/view为数据库表/视图名称
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'database' AND TABLE_NAME = 'table/view' "
    ```

- 模块中记录数据表/视图的结构为

    ```
    {
       "table/view 1 name":{
             "type":"table/view"
             "keyList":["column name(key column)"]
             "column":{
                 "column 1 name":{
                     "type":"",
                     "textMaxLength":255(Long type),
                     "isNullAble":true/false,
                     "isKey":true/false
                 },
                 "column 2 name":{...}
             }
       },
       "table/view 2 name":{...}
    }
    ```

### > 数据库设计规范推荐

- 分库：数据库尽量按照业务模块划分独立的数据库，利于后续数据库性能扩展
- 表名称，以`t_`为前缀
- 表字段，以`表名_`为前缀，如果字段为外键，则名称不变。如果一张表里记录多个相同的外键，则以`$序号`标记，如`resouce_id$0`、`resouce_id$1`
- 表字段都需要设置默认值，如果无法决定默认值，请设置要求表字段不为空
- 为了方便模块自动注入方便，请尽量使用文本类型（vchar）
- 为了提升执行性能，需要对用于筛选、排序的字段设置索引
- 视图名称，以`v_`为前缀，视图字段沿用原来表字段名称

# ◎ 模块单独测试

在测试前，需要在MySQL8中创建测试用的数据库、数据表

- 数据库名：test

- 数据库字符集：Utf8mb4

- 数据库排列规则：utf8mb4_general_ci

- 数据表名：test

- 数据表字段：test_id（vchar:255，主键），test_value（varchr:255）

- 创建数据表的SQL语句

    ```
    CREATE TABLE `test` (
      `test_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
      `test_value` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
      PRIMARY KEY (`test_id` DESC)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    ```

- 创建完测试用的数据库、数据表后，修改`_ServeDao/Once.Config`文件中的关于数据库的配置

    ```
    module.sr-servedao.datasource.url=数据库连接
    module.sr-servedao.datasource.username=用户名
    module.sr-servedao.datasource.password=密码
    module._ServeDao.isAutoGenerateMap=true  #开启数据库表、视图自动获取，每次程序启动都会自动获取
    ```

在工程根目录，运行shell指令

```
python3 Christmas/Christmas.py ShellExcute/Compile#TestModule _ServeDao
```

也可以通过`Christmas插件`运行`ShellExcute>>Compile#TestModule`，并在插件打开的终端中输入`_ServeDao`

### > 测试例子-插入数据

- URL：http://127.0.0.1:8080/once/module/sample/insert

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
        "test_id":"123",
        "test_value":"value value"
    }
    ```

- 期待结果：数据被插入数据库，如果使用相同同的test_id，数据会被更新，请求返回：

    ```
    {
    		"result": 1,
        "code": "200",
        "message": "OK"
    }
    ```

### > 测试例子-批量插入数据

- URL：http://127.0.0.1:8080/once/module/sample/insertBatch

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
        "insertList":[
            {"test_id":"1", "test_value":"value"},
            {"test_id":"2", "test_value":"value"}
        ]
    }
    ```

- 期待结果：数据被插入数据库，如果使用相同同的test_id，数据会被更新，请求返回：

    ```
    {
        "result": 2,
        "code": "200",
        "message": "OK"
    }
    ```

### > 测试例子-删除数据

- URL：http://127.0.0.1:8080/once/module/sample/delete

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
        "test_id":"123"
    }
    ```

- 期待结果：数据被删除，请求返回：

    ```
    {
        "result": 1,
        "code": "200",
        "message": "OK"
    }
    ```

### > 测试例子-更新数据

- URL：http://127.0.0.1:8080/once/module/sample/update

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
        "test_id":"1",
        "test_value":"update data"
    }
    ```

- 期待结果：数据被更新，请求返回：

    ```
    {
        "result": 1,
        "code": "200",
        "message": "OK"
    }
    ```

### > 测试例子-获取数据

- URL：http://127.0.0.1:8080/once/module/sample/select

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
        "test_id":"2"
    }
    ```

- 期待结果：请求返回：

    ```
    {
        "test_id": "2",
        "test_value": "value",
        "code": "200",
        "message": "OK"
    }
    ```

### > 测试例子-获取数据列表

- URL：http://127.0.0.1:8080/once/module/sample/selectList

- 请求方式：POST

- 请求体类型：application/json

    ```
    {}
    ```
    
- 期待结果：请求返回：

    ```
    {
        "count": 2,
        "result": [
            {
                "test_id": "2",
                "test_value": "value"
            },
            {
                "test_id": "1",
                "test_value": "update data"
            }
        ],
        "code": "200",
        "message": "OK"
    }
    ```

### > 测试例子-SQL语句更新（插入/删除/更新数据）

- URL：http://127.0.0.1:8080/once/module/sample/sqlUpdate

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
        "value":"sql update data",
        "id":"2"
    }
    ```

- 期待结果：请求返回：

    ```
    {
        "result": 1,
        "code": "200",
        "message": "OK"
    }
    ```

### > 测试例子-SQL语句查询

- URL：http://127.0.0.1:8080/once/module/sample/sqlSelect

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
        "value":"%update%"
    }
    ```

- 期待结果：请求返回：

    ```
    {
        "count": 2,
        "result": [
            {
                "test_id": "2",
                "test_value": "sql update data"
            },
            {
                "test_id": "1",
                "test_value": "update date"
            }
        ],
        "code": "200",
        "message": "OK"
    }
    ```

### > 测试例子-数据库事务

- URL：http://127.0.0.1:8080/once/module/sample/transactional

- 请求方式：POST

- 请求体类型：application/json

    ```
    {
        "test_value":"value",
        "test_id":"3"
    }
    ```

- 期待结果：数据被插入，且删除操作被回滚了，请求返回：

    ```
    {
        "result_insert": 1,
        "result_delete": 1,
        "code": "200",
        "message": "OK"
    }
    ```

# ◎ 更新列表

**1.0=2024.06.20**

- 模块建立