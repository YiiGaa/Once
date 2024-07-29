# _ServeDao-MySQL database operation

The main functions include:

- insert-insert data

- insert batch-insert data in batches

- delete-delete data

- update-update data

- select object-query data

- select list-query data list

- sql update-SQL statement update (insert/delete/update data)

- sql select-SQL statement query

- txn start-start database transactions

- txn end-end database transaction

Necessary modifications before the module is used:

- Set the MySQL database connection information and set it in the `_ServeDao/Once.Config` file
- If the function of automatically obtaining table information is turned on (on by default), the data is required to be MySQL8

Description of the value of module parameters:

- If the value you want to set is a fixed value, you can directly set the value of the corresponding type.

- If you want to dynamically obtain the value from `passParam` as the set value, please use `get##key` as the setting value of the module parameter.

- If the value you want to get is in the inner layer of passParam (nested json), please locate it with `>>`, such as `get##key_1>>key_2`

# ※ insert-insert data

- Add version: 1.0=2024.06.20
- It is required to turn on the function of automatically obtaining table fields, otherwise this function is invalid

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

| Key            | Description                                                  | Necessary | Default                    | Type   | Add/Update     |
| -------------- | ------------------------------------------------------------ | --------- | -------------------------- | ------ | -------------- |
| _action        | Specify the module behavior, fixed to "insert"               | Yes       |                            | String | 1.0=2024.06.20 |
| _table         | Database table name                                          | Yes       |                            | String | 1.0=2024.06.20 |
| _target        | Target data                                                  | No        | Target data with passParam | Object | 1.0=2024.06.20 |
| _isReplace     | Does the SQL that insert the data start with `REPLACE`, true: use `REPLACE`, false: use `INSERT` | No        | false                      | Bool   | 1.0=2024.06.20 |
| _resultKey     | Key to record the results                                    | No        | Do not record the results  | String | 1.0=2024.06.20 |
| _isPutReturn   | Whether to record the result in returnParam, true: record in returnParam, false: record in passParam | No        | true                       | Bool   | 1.0=2024.06.20 |
| _isCheckAffect | Whether to check the execution result, true: check (the number of updated rows <=0 error), false: do not check | No        | true                       | Bool   | 1.0=2024.06.20 |

Supplementary description of module parameters:

### > _table

- Database table name

- The database table must be an ordinary table, not a view

### > _target

- Target data, type Json Object

- `PassParam` is the target data by default

- The module will extract the inserted value from the target data according to the automatically obtained table field

- When the table field is not empty and cannot be extracted from the target data, the module will report an error

- There are three situations where the table field can be empty, set the table field can be empty, set the default value of the table field, and set the table field to increase

- When the table field is a text type, the module will check the data length, and if it is over limited, an error will be reported

- Database table fields of tinytext, text, mediumtext, longtext, char and varchar types will be considered as text types

- The maximum length of the above text types is, tinytext: 255 bytes, text: 65,535 bytes, mediumtext:16,777,215 bytes (16MB), longtext:4,294,967,295 bytes (4 GB), char/varchar: custom length

- In order to prevent SQL injection, when splicing the data, the module will use `''` to wrap the data, and the `'` in the data is escaped

### > _isReplace

- Does the SQL that insert data start with `REPLACE`, true: use `REPLACE`, false: use `INSERT`

- `INSERT` When inserting data, if the same primary key already exists, an error will be thrown

- `REPLACE` When inserting data, if the same primary key already exists, the data will be updated. If the same primary key does not exist, the new data will be inserted

- The update operation of `REPLACE` is not exactly equal to the `UPDATE` operation. The update operation of `REPLACE` is actually to delete the data first and then insert the data

### > _resultKey

- Key to record the results

- Omit the setting, or set it to empty `""`, and the module will not record the results

- The result of inserting data is the number of updated rows

- When `REPLACE` is used to insert data and the primary key already exists, the number of updated rows is 2

# ※ insert batch-insert data in batches

- Add version: 1.0=2024.06.20
- It is required to turn on the function of automatically obtaining table fields, otherwise this function is invalid

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

| Key            | Description                                                  | Necessary | Default                   | Type   | Add/Update     |
| -------------- | ------------------------------------------------------------ | --------- | ------------------------- | ------ | -------------- |
| _action        | Specify the module behavior, fixed to "insert batch"         | Yes       |                           | String | 1.0=2024.06.20 |
| _table         | Database table name                                          | Yes       |                           | String | 1.0=2024.06.20 |
| _target        | Target data                                                  | Yes       |                           | Array  | 1.0=2024.06.20 |
| _isReplace     | Does the SQL that insert the data start with `REPLACE`, true: use `REPLACE`, false: use `INSERT` | No        | false                     | Bool   | 1.0=2024.06.20 |
| _resultKey     | Key to record the results                                    | No        | Do not record the results | String | 1.0=2024.06.20 |
| _isPutReturn   | Whether to record the result in returnParam, true: record in returnParam, false: record in passParam | No        | true                      | Bool   | 1.0=2024.06.20 |
| _isCheckAffect | Whether to check the execution result, true: check (the number of updated rows <=0 error), false: do not check | No        | true                      | Bool   | 1.0=2024.06.20 |

Supplementary description of module parameters:

### > _table

- Database table name

- The database table must be an ordinary table, not a view

### > _target

- Target data, type Json Array

    ```
    [
    		{"key 1-1":"value 1-1", "key 1-2": "value 1-2"},
    		{"key 2-1":"value 2-1", "key 2-2": "value 2-2"}
    ]
    ```

- The module will extract the inserted value from the target data according to the automatically obtained table field

- When the table field is not empty and cannot be extracted from the target data, the module will report an error

- When the table field is a text type, the module will check the data length, and if it is over limited, an error will be reported

- There are three situations where the table field can be empty, set the table field can be empty, set the default value of the table field, and set the table field to increase

- Database table fields of tinytext, text, mediumtext, longtext, char and varchar types will be considered as text types

- The maximum length of the above text types is, tinytext: 255 bytes, text: 65,535 bytes, mediumtext:16,777,215 bytes (16MB), longtext:4,294,967,295 bytes (4 GB), char/varchar: custom length

- In order to prevent SQL injection, when splicing the data, the module will use `''` to wrap the data, and the `'` in the data is escaped

### > _isReplace

- Does the SQL that insert data start with `REPLACE`, true: use `REPLACE`, false: use `INSERT`

- `INSERT` When inserting data, if the same primary key already exists, an error will be thrown

- `REPLACE` When inserting data, if the same primary key already exists, the data will be updated. If the same primary key does not exist, the new data will be inserted

- The update operation of `REPLACE` is not exactly equal to the `UPDATE` operation. The update operation of `REPLACE` is actually to delete the data first and then insert the data

### > _resultKey

- Key to record the results

- Omit the setting, or set it to empty, and the module will not record the results

- The result of inserting data is the number of updated rows

- When `REPLACE` is used to insert data and the primary key already exists, the number of updated rows of a single inserted data is 2

# ※ delete-delete data

- Add version: 1.0=2024.06.20
- It is required to turn on the function of automatically obtaining table fields, otherwise this function is invalid

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

| Key                 | Description                                                  | Necessary | Default                    | Type   | Add/Update     |
| ------------------- | ------------------------------------------------------------ | --------- | -------------------------- | ------ | -------------- |
| _action             | Specify the module behavior, fixed to "delete"               | Yes       |                            | String | 1.0=2024.06.20 |
| _table              | Database table name                                          | Yes       |                            | String | 1.0=2024.06.20 |
| _target             | Target data                                                  | No        | Target data with passParam | Object | 1.0=2024.06.20 |
| _filter             | SQL fragments for judgment (WHERE part of DELETE SQL)        | No        | Automatic splicing         | String | 1.0=2024.06.20 |
| _isSQLReplaceStrict | When dynamically replacing SQL fragmented data, whether to replace it in a strict way, true: strictly replace, false: do not check the characters | No        | true                       | Bool   | 1.0=2024.06.20 |
| _resultKey          | Key to record the results                                    | No        | Do not record the results  | String | 1.0=2024.06.20 |
| _isPutReturn        | Whether to record the result in returnParam, true: record in returnParam, false: record in passParam | No        | true                       | Bool   | 1.0=2024.06.20 |
| _isCheckAffect      | Whether to check the execution result, true: check (the number of updated rows <=0 error), false: do not check | No        | true                       | Bool   | 1.0=2024.06.20 |

Supplementary description of module parameters:

### > _table

- Database table name

- The database table must be an ordinary table, not a view

### > _target

- Target data, type Json Object

- Default to `passParam` as the target data

- When `_filter` is set, this setting will be ignored

- The module will extract the judgment conditions of deleted data from the target data according to the automatically obtained table fields (WHERE part of DELETE SQL)

- If the target data corresponding to the database table field is an array, the value range will be spliced in the form of `key IN ('value 1', 'value 2')`

- If the target data corresponding to the database table field is a type other than the array, it will be spliced in the form of `key = 'value'`
- In order to prevent SQL injection, when splicing the data, the module will use `''` to wrap the data, and the `'` in the data is escaped

### > _filter

- SQL fragment for judgment (WHERE part of DELETE SQL)

- When `filter` sets a non-empty `""` string, the setting of `_target` will be ignored

- You can use `@@` to mark the data that needs to be dynamically replaced. For details, please refer to `Special Notes-Dynamic Replacement Data`

### > _isSQLReplaceStrict

- When dynamically replacing SQL fragmented data, whether to replace it in a strict way, true: strictly replace, false: do not check the characters
- Only take effect when the data is replaced by `_filter`

- Detailed description of strict replacement reference `Special Notes-Dynamic Replacement Data`

# ※ update-update data

- Add version: 1.0=2024.06.20
- It is required to turn on the function of automatically obtaining table fields, otherwise this function is invalid

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

| Key                 | Description                                                  | Necessary | Default                    | Type   | Add/Update     |
| ------------------- | ------------------------------------------------------------ | --------- | -------------------------- | ------ | -------------- |
| _action             | Specify the module behavior, fixed to "update"               | Yes       |                            | String | 1.0=2024.06.20 |
| _table              | Database table name                                          | Yes       |                            | String | 1.0=2024.06.20 |
| _target             | Target data                                                  | No        | Target data with passParam | Object | 1.0=2024.06.20 |
| _update             | SQL fragment for setting up updated data (SET part of UPDATE SQL) | No        | Automatic splicing         | String | 1.0=2024.06.20 |
| _filter             | SQL fragments for judgment (UPDATE part of DELETE SQL)       | No        | Automatic splicing         | String | 1.0=2024.06.20 |
| _isSQLReplaceStrict | When dynamically replacing SQL fragmented data, whether to replace it in a strict way, true: strictly replace, false: do not check the characters | No        | true                       | Bool   | 1.0=2024.06.20 |
| _resultKey          | Key to record the results                                    | No        | Do not record the results  | String | 1.0=2024.06.20 |
| _isPutReturn        | Whether to record the result in returnParam, true: record in returnParam, false: record in passParam | No        | true                       | Bool   | 1.0=2024.06.20 |
| _isCheckAffect      | Whether to check the execution result, true: check (the number of updated rows <=0 error), false: do not check | No        | true                       | Bool   | 1.0=2024.06.20 |

Supplementary description of module parameters:

### > _table

- Database table name

- The database table must be an ordinary table, not a view.

### > _target

- Target data, type Json Object

- Default to `passParam` as the target data

- When ``filter`` and `_update` are set at the same time, this setting will be ignored

- According to the automatically acquired table field, the module will extract the primary key data from the target data as a judgment condition (WHERE part of UPDATE SQL), and extract other data from the target data as the setting update data part (SET part of UPDATE SQL)

- If the target data corresponding to the primary key of the database table is an array, the value range will be spliced in the form of `key IN ('value 1', 'value 2')`

- If the target data corresponding to the primary key of the database table is a type other than the array, it will be spliced in the form of `key = 'value'`
- In order to prevent SQL injection, when splicing the data, the module will use `''` to wrap the data, and the `'` in the data is escaped

### > _update

- SQL fragment for setting update data (SET part of UPDATE SQL)

- When `_update` is set to a non-empty `""` string, `_target` will no longer be used to automatically splice the SQL fragments for setting update data (SET part of UPDATE SQL)

- You can use `@@` to mark the data that needs to be dynamically replaced. For details, please refer to `Special Notes-Dynamic Replacement Data`

### > _filter

- SQL fragment for judgment (WHERE part of UPDATE SQL)

- When `_filter` is set to a non-empty `""` string, `_target` will no longer be used to automatically splice the SQL fragment for judgment (WHERE part of UPDATE SQL)

- You can use `@@` to mark the data that needs to be dynamically replaced. For details, please refer to `Special Notes-Dynamic Replacement Data`

### > _isSQLReplaceStrict

- When dynamically replacing SQL fragmented data, whether to replace it in a strict way, true: strictly replace, false: do not check the characters
- Only take effect when the data is replaced by `_filter` or `_update`

- Detailed description of strict replacement reference `Special Notes-Dynamic Replacement Data`

# ※ select object-query data

- Add version: 1.0=2024.06.20
- It is required to turn on the function of automatically obtaining table fields, otherwise this function is invalid
- The query results are limited to the first data

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

| Key                 | Description                                                  | Necessary | Default                                                      | Type   | Add/Update     |
| ------------------- | ------------------------------------------------------------ | --------- | ------------------------------------------------------------ | ------ | -------------- |
| _action             | Specify the module behavior, fixed to "select object"        | Yes       |                                                              | String | 1.0=2024.06.20 |
| _table              | Database table name                                          | Yes       |                                                              | String | 1.0=2024.06.20 |
| _target             | Target data                                                  | No        | Target data with passParam                                   | Object | 1.0=2024.06.20 |
| _get                | SQL fragment for setting update data (SELECT SQL's settings to get the field part) | No        | * (Get all fields)                                           | String | 1.0=2024.06.20 |
| _filter             | SQL fragments for judgment (WHERE part of SELECT SQL)        | No        | Automatic splicing                                           | String | 1.0=2024.06.20 |
| _isSQLReplaceStrict | When dynamically replacing SQL fragmented data, whether to replace it in a strict way, true: strictly replace, false: do not check the characters | No        | true                                                         | Bool   | 1.0=2024.06.20 |
| _resultKey          | Key to record the results                                    | No        | Insert the query results directly into the data pool by field (passParam/returnParam) | String | 1.0=2024.06.20 |
| _isPutReturn        | Whether to record the result in returnParam, true: record in returnParam, false: record in passParam | No        | true                                                         | Bool   | 1.0=2024.06.20 |
| _isGetNullError     | Whether to report an error when the data cannot be obtained, true: error, false: no error | No        | false                                                        | Bool   | 1.0=2024.06.20 |
| _isGetNotNullError  | Whether to report an error when obtaining the data, true: error, false: no error | No        | false                                                        | Bool   | 1.0=2024.06.20 |

Supplementary description of module parameters:

### > _table

- Database table name

- The database table can be an ordinary table or a view

- Exaggerate tables and database queries. It is recommended to use views to reduce the complexity of SQL

### > _target

- Target data, type Json Object

- Default to `passParam` as the target data

- When `_filter` is set at the same time, this setting will be ignored

- The module will extract data from the target data as a judgment condition according to the automatically obtained table field (WHERE part of SELECT SQL)

- If the target data corresponding to the database table field is an array, the value range will be spliced in the form of `key IN ('value 1', 'value 2')`

- If the target data corresponding to the database table field is a type other than the array, it will be spliced in the form of `key = 'value'`

- In order to prevent SQL injection, when splicing the data, the module will wrap the data with `''`, and the `'` in the data is escaped

### > _get

- SQL fragment for setting updated data (SELECT SQL's settings to get the field part)

- If omitted, it is equivalent to `*`, get all fields

- You can use `@@` to mark the data that needs to be dynamically replaced. For details, please refer to `Special Notes-Dynamic Replacement Data`

### > _filter

- SQL fragment for judgment (WHERE part of SELECT SQL)

- When `_filter` is set to a non-empty `""` string, `_target` will no longer be used to automatically splice the SQL fragment for judgment (WHERE part of UPDATE SQL)

- You can use `@@` to mark the data that needs to be dynamically replaced. For details, please refer to `Special Notes-Dynamic Replacement Data`

### > _isSQLReplaceStrict

- When dynamically replacing SQL fragmented data, whether to replace it in a strict way, true: strictly replace, false: do not check the characters
- Only take effect when the data is replaced by `_filter` or `_get`

- Detailed description of strict replacement reference `Special Notes-Dynamic Replacement Data`

### > _resultKey

- Key to record the results

- By default, or set `_resultKey` to an empty string `""`, the query results will be inserted directly into the data pool (passParam/returnParam) by field

- The query results are limited to the first data

# ※ select list-query data list

- Add version: 1.0=2024.06.20
- It is required to turn on the function of automatically obtaining table fields, otherwise this function is invalid
- Used to query the data list

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

| Key                 | Description                                                  | Necessary | Default                    | Type   | Add/Update     |
| ------------------- | ------------------------------------------------------------ | --------- | -------------------------- | ------ | -------------- |
| _action             | Specify the module behavior, fixed to "select list"          | Yes       |                            | String | 1.0=2024.06.20 |
| _table              | Database table name                                          | Yes       |                            | String | 1.0=2024.06.20 |
| _target             | Target data                                                  | No        | Target data with passParam | Object | 1.0=2024.06.20 |
| _get                | SQL fragment for setting update data (SELECT SQL's settings to get the field part) | No        | * (Get all fields)         | String | 1.0=2024.06.20 |
| _filter             | SQL fragments for judgment (WHERE part of SELECT SQL)        | No        | Automatic splicing         | String | 1.0=2024.06.20 |
| _orderBy            | SQL fragment for sorting (ORDER BY part of SELECT SQL)       | No        | No additional sorting      | String | 1.0=2024.06.20 |
| _page               | Page of data (LIMIT part of SELECT SQL)                      | No        | No restrictions on paging  | Int    | 1.0=2024.06.20 |
| _pageSize           | Size of per page (LIMIT part of SELECT SQL)                  | No        | No restrictions on paging  | Int    | 1.0=2024.06.20 |
| _isSQLReplaceStrict | When dynamically replacing SQL fragmented data, whether to replace it in a strict way, true: strictly replace, false: do not check the characters | No        | true                       | Bool   | 1.0=2024.06.20 |
| _countResultKey     | Record the total number of eligible items                    | No        | count                      | String | 1.0=2024.06.20 |
| _resultKey          | Key to record the results                                    | No        | result                     | String | 1.0=2024.06.20 |
| _isPutReturn        | Whether to record the result in returnParam, true: record in returnParam, false: record in passParam | No        | true                       | Bool   | 1.0=2024.06.20 |
| _isGetNullError     | Whether to report an error when the data cannot be obtained, true: error, false: no error | No        | false                      | Bool   | 1.0=2024.06.20 |
| _isGetNotNullError  | Whether to report an error when obtaining the data, true: error, false: no error | No        | false                      | Bool   | 1.0=2024.06.20 |

Supplementary description of module parameters:

### > _table

- Database table name

- The database table can be an ordinary table or a view

- Exaggerate tables and database queries. It is recommended to use views to reduce the complexity of SQL

### > _target

- Target data, type Json Object

- Default to `passParam` as the target data

- When `_filter` is set, this setting will be ignored

- The module will extract data from the target data as a judgment condition according to the automatically obtained table field (WHERE part of SELECT SQL)

- If the target data corresponding to the database table field is an array, the value range will be spliced in the form of `key IN ('value 1', 'value 2')`

- If the target data corresponding to the database table field is a type other than the array, it will be spliced in the form of `key = 'value'`

- In order to prevent SQL injection, when splicing the data, the module will wrap the data with `''`, and the `'` in the data is escaped

### > _get

- SQL fragment for setting updated data (SELECT SQL's settings to get the field part)

- If omitted, it is equivalent to `*`, get all fields

- You can use `@@` to mark the data that needs to be dynamically replaced. For details, please refer to `Special Notes-Dynamic Replacement Data`

### > _filter

- SQL fragment for judgment (WHERE part of SELECT SQL)

- When `_filter` is set to a non-empty `""` string, `_target` will no longer be used to automatically splice the SQL fragment for judgment (WHERE part of UPDATE SQL)

- You can use `@@` to mark the data that needs to be dynamically replaced. For details, please refer to `Special Notes-Dynamic Replacement Data`

### > _orderBy

- SQL fragments for sorting (ORDER BY part of SELECT SQL)

- No additional sorting by default

- This part should fully consider the risk of SQL injection. If the arranged fields are passed in by the client, please use the `_DataCheck` module to check the legitimacy

- You can use `@@` to mark the data that needs to be dynamically replaced. For details, please refer to `Special Notes-Dynamic Replacement Data`

### > \_page, \_pageSize

- Used to set paging (LIMIT part of SELECT SQL)

- It will only take effect when \_page and \_pageSize are greater than or equal to 0.

- \_page starts from 0

### > _isSQLReplaceStrict

- When dynamically replacing SQL fragmented data, whether to replace it in a strict way, true: strictly replace, false: do not check the characters
- Only take effect when the data is replaced by `_filter` , `_get` or `_orderBy`

- Detailed description of strict replacement reference `Special Notes-Dynamic Replacement Data`

### > _countResultKey

- Record the total number of eligible items

- The total number of strips will ignore the paging limit

- When `_countResultKey` is set to `""` empty string, the total number of eligible items will not be recorded

### > _resultKey

- Key to record the list of results

- When `_resultKey` is set to `""` empty string, the list of matching results will not be recorded

# ※ sql update-SQL statement update (insert/delete/update data)

- Add version: 1.0=2024.06.20
- It is not required to turn on the function of automatically obtaining table fields

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

| Key                 | Description                                                  | Necessary | Default                   | Type   | Add/Update     |
| ------------------- | ------------------------------------------------------------ | --------- | ------------------------- | ------ | -------------- |
| _action             | Specify the module behavior, fixed to "sql update"           | Yes       |                           | String | 1.0=2024.06.20 |
| _sql                | SQL statements for updating                                  | Yes       | Automatic splicing        | String | 1.0=2024.06.20 |
| _isSQLReplaceStrict | When dynamically replacing SQL fragmented data, whether to replace it in a strict way, true: strictly replace, false: do not check the characters | No        | true                      | Bool   | 1.0=2024.06.20 |
| _resultKey          | Key to record the results                                    | No        | Do not record the results | String | 1.0=2024.06.20 |
| _isPutReturn        | Whether to record the result in returnParam, true: record in returnParam, false: record in passParam | No        | true                      | Bool   | 1.0=2024.06.20 |
| _isCheckAffect      | Whether to check the execution result, true: check (the number of updated rows <=0 error), false: do not check | No        | true                      | Bool   | 1.0=2024.06.20 |

Supplementary description of module parameters:

### > _sql

- SQL statements for updating
- Please fully consider the risk of SQL injection
- You can use `@@` to mark the data that needs to be dynamically replaced. For detailed instructions, please refer to `Special Notes-Dynamic Replacement Data`

### > _isSQLReplaceStrict

- When dynamically replacing SQL statements for updating, whether to replace it in a strict way, true: strictly replace, false: do not check the characters
- Only take effect when the data is replaced by `_sql`

- Detailed description of strict replacement reference `Special Notes-Dynamic Replacement Data`

# ※ sql select-SQL statement query

- Add version: 1.0=2024.06.20
- It is not required to turn on the function of automatically obtaining table fields

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

| Key                 | Description                                                  | Necessary | Default                   | Type   | Add/Update     |
| ------------------- | ------------------------------------------------------------ | --------- | ------------------------- | ------ | -------------- |
| _action             | Specify the module behavior, fixed to "sql select"           | Yes       |                           | String | 1.0=2024.06.20 |
| _sql                | SQL statements for query                                     | No        | Automatic splicing        | String | 1.0=2024.06.20 |
| _selectType         | Specify the result type of the query                         | 否        | select list               | String | 1.0=2024.06.20 |
| _isSQLReplaceStrict | When dynamically replacing SQL fragmented data, whether to replace it in a strict way, true: strictly replace, false: do not check the characters | No        | true                      | Bool   | 1.0=2024.06.20 |
| _resultKey          | Key to record the results                                    | No        | Do not record the results | String | 1.0=2024.06.20 |
| _isPutReturn        | Whether to record the result in returnParam, true: record in returnParam, false: record in passParam | No        | true                      | Bool   | 1.0=2024.06.20 |
| _isGetNullError     | Whether to report an error when the data cannot be obtained, true: error, false: no error | No        | false                     | Bool   | 1.0=2024.06.20 |
| _isGetNotNullError  | Whether to report an error when obtaining the data, true: error, false: no error | No        | false                     | Bool   | 1.0=2024.06.20 |

Supplementary description of module parameters:

### > _sql

- SQL statements for query
- Please fully consider the risk of SQL injection
- You can use `@@` to mark the data that needs to be dynamically replaced. For detailed instructions, please refer to `Special Notes-Dynamic Replacement Data`

### > _selectType

- SQL statements for query
- Support 4 types, `select string`, `select int`, `select list`, `select object`
- `select string`, the result is a string type

- `select int`, the result is a number type

- `select list`, the result is a list type, default

- `select object`, the result is the object type, and the query result is limited to the first data

### > _resultKey

- Key to record the results
- Omit this setting, or set it to an empty string `""`, the results will not be recorded by default
- If `_selectType` is set to `select object`, and `_resultKey` is omitted or set to an empty string `""`, insert the query results directly into the data pool by field (passParam/returnParam)

### > _isSQLReplaceStrict

- When dynamically replacing SQL statements for query, whether to replace it in a strict way, true: strictly replace, false: do not check the characters
- Only take effect when the data is replaced by `_sql`

- Detailed description of strict replacement reference `Special Notes-Dynamic Replacement Data`

# ※ txn start-start database transactions

- Add version: 1.0=2024.06.20
- It is not required to turn on the function of automatically obtaining table fields
- Please use MySQL storage engines that support transactions such as InnoDB, which is the default storage engine of MySQL8
- You can't open a new transaction before the last transaction is over (module error)

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"txn start"
	}
}
```

| Key                  | Description                                                  | Necessary | Default                 | Type   | Add/Update     |
| -------------------- | ------------------------------------------------------------ | --------- | ----------------------- | ------ | -------------- |
| _action              | Specify the module behavior, fixed to "txn start"            | Yes       |                         | String | 1.0=2024.06.20 |
| _level               | Database transaction isolation level                         | No        | Default isolation level | String | 1.0=2024.06.20 |
| _isAutoErrorRollBack | At the end of the business logic, whether the error automatically rolls back the transaction, true: error rollback, no error submission, false: no error rollback, error submission | No        | true                    | Bool   | 1.0=2024.06.20 |

Supplementary description of module parameters:

### > _level

- Database transaction isolation level

- The optional database transaction isolation level is `read uncommitted`, `read committed`, `repeatable read`, `serializable`

- `read uncommitted`, read unsubmitted. Allowing unsubmitted data to be read may lead to dirty reading, that is, reading data that has not been submitted by other transactions

- `read committed`, read submitted. Only the data submitted by other transactions can be read, which can avoid dirty reading, but there may be a problem of non-repeatable reading, that is, different results may be obtained when reading the same data set multiple times in the same transaction

- `repeatable read`, the default isolation level, which can be read repeatedly. Ensure that the results of multiple readings of the same data set in the same transaction are consistent, which can avoid the problem of non-repeatable reading, but there may be phantom reading, that is, the data added in the transaction is inserted by other transactions during the execution of the transaction, resulting in inconsistent data sets read

- `serializable`, serialization. The highest isolation level, completely serial execution of transactions, avoid dirty reading, non-repeatable reading and illusion reading, and low performance, because all data involved needs to be locked until the transaction is completed

### > _isAutoErrorRollBack

- At the end of the business logic, whether the error automatically rolls back the transaction, true: error rollback, no error submission, false: no error rollback, error submission
- During business execution, the business logic may be interrupted due to a module error. After the business logic is interrupted, the business rollback or submission will be automatically performed
- The database transaction opened in `Contoller` will automatically end the transaction at the end of the `Controller` logic
- The database transaction opened in `Service` will automatically end the transaction at the end of the `Service` logic
- After the transaction is opened, you can manually call `txn end` to end the transaction at any location (Controller/Service)

# ※ txn end-end database transaction

- Add version: 1.0=2024.06.20
- It is not required to turn on the function of automatically obtaining table fields

```json
{
	"name":"_ServeDao",
	"param":{
		"_action":"txn end",
    "_isCommit":true
	}
}
```

| Key       | Description                                                  | Necessary | Default | Type   | Add/Update     |
| --------- | ------------------------------------------------------------ | --------- | ------- | ------ | -------------- |
| _action   | Specify the module behavior, fixed to "txn end"              | Yes       |         | String | 1.0=2024.06.20 |
| _isCommit | Whether to submit the transaction, true: submit, false: rollback | No        | true    | Bool   | 1.0=2024.06.20 |

# ◎ Configuration Notes

The module configuration is set in `_ServeDao/Once.Config`. When the application is run through Christmas, it will be automatically spliced to `config/application.properties`

| key                                       | Description                                                  | Necessary | Default     | Type   | Add/Update     |
| ----------------------------------------- | ------------------------------------------------------------ | --------- | ----------- | ------ | -------------- |
| module._ServeDao.isAutoGenerateMap                      | 是否开启自动获取表信息，true:开启，fase:关闭 | No        | true    | String | 1.0=2024.06.20 |
| module.sr-servedao.datasource.url                       | 数据库地址                                   | Yes       |         | String | SpringBoot     |
| module.sr-servedao.datasource.username                  | 数据库用户名                                 | Yes       |         | String | SpringBoot     |
| module.sr-servedao.datasource.password                  | 数据库密码                                   | Yes       |         | String | SpringBoot     |
| module.sr-servedao.datasource.hikari.maximum-pool-size  | 连接池中最大的连接数量                       | 否        | 10      | Int    | SpringBoot     |
| module.sr-servedao.datasource.hikari.minimum-idle       | 连接池中最小的连接数量                       | 否        | 10      | Int    | SpringBoot     |
| module.sr-servedao.datasource.hikari.connection-timeout | 等待获取连接的最大时间（单位：毫秒）         | 否        |         | Int    | SpringBoot     |
| module.sr-servedao.datasource.hikari.max-lifetime       | 连接池中连接的最大生命周期（单位：毫秒）     | 否        |         | Int    | SpringBoot     |

Supplementary description of configuration:

### > module. _ServeDao.isAutoGenerateMap

- Whether to turn on automatic acquisition table information, true: open, fase: close

- The database is required to be `MySQL8`

- For a detailed description of the mechanism, please refer to the `special Notes-Automatic acquisition of table field mechanism`

### > module.sr-servedao.datasource

- Database-related configuration

- This configuration is consistent with Springboot's `spring.database`, and the module only modifies the prefix

### > module.sr-servedao.datasource.hikari

- Database connection pool configuration

- This configuration is consistent with Springboot's `spring.database.hikari`, and the module only modifies the prefix

- The configuration of the connection pool has significantly improved the performance.

- Generally, when tuning the performance, the number of execution threads of MySQL is set to twice the number of CPU cores, and the number of connections received can be slightly larger, such as 100 (to prevent the rejection of connections of multiple distributed applications)

- Generally, the database connection pool of a single application can be set to 10. If it is a database cluster, or the performance of the database server is better, the settings can be appropriately improved

# ◎ Special Notes

### > MySQL8 Create Account (Restricted Permissions)

- Create the account (SQL), `newuser` is the user name, `%` is the client call IP (`%` is to allow all IPs, you can use `192. %` for restricted IP prefixes), `your_password` is the password of the account

    ```
    CREATE USER 'newuser'@'%' IDENTIFIED BY 'your_password';
    
    #Refresh
    FLUSH PRIVILEGES;
    ```

- Set the account permissions (SQL), `test` is the specified database name

    ```
    #Generally, you can only add the permission to add, delete, update and select, which can limit the deletion of databases, data tables, adding temporary tables and other dangerous operations
    GRANT SELECT, INSERT, UPDATE, DELETE ON test.* TO 'newuser'@'%';
    
    #Refresh
    FLUSH PRIVILEGES;
    
    #The following are other relevant SQL，There is no need to call together
    #Add all permissions of a database
    GRANT ALL PRIVILEGES ON test.* TO 'newuser'@'%';
    #Clean up all permissions of a database
    REVOKE ALL PRIVILEGES ON test.* FROM 'newuser'@'%';
    #View permissions
    SELECT * FROM mysql.db WHERE Db = 'test' AND User = 'newuser';
    ```

- If you open the module to automatically obtain the database table and view information (open by default), the module will automatically use the following SQL statements to obtain information. Generally, new accounts have such permissions and do not require special settings

    ```
    #Query the currently connected database
    SELECT DATABASE();
    
    #Get all the data tables/views under the database, and the database is the name of the database
    SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE WHERE TABLE_SCHEMA = 'database' AND (TABLE_TYPE='BASE TABLE' or TABLE_TYPE='VIEW')"
    
    #Get all field information under a data table/view, database is the database name, and table/view is the database table/view name
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'database' AND TABLE_NAME = 'table/view' "
    ```

### > Dynamic replacement data

- Use `@xxx@` to mark the data in the string that needs to be dynamically replaced

- Data is obtained from `passParam`, such as `@key@`

- If the data cannot be obtained, `@xxx@` will keep it as it is

- If the value you want to get is in the iner layer (nested json) of passParam, please locate it with `>>`, such as `@key_1>>key_2@`

- If the replacement target data is `Json Array`, the data will be automatically carried out, such as `list1, list2, list3`

- The part marked with `'@xxx@'` will be directly replaced with the obtained data, such as `'data value...'`. In order to prevent SQL blinds, the `'` in the obtained data will be escalated to `''`

- If the replacement target data is Json Array and marked with `'@xxx@'`, the final replacement data is `'list1','list2','list3'`

- The part marked with `@xxx@` (front and back do not contain `''`) will also be directly replaced with the obtained data, but in order to prevent SQL injection, when `_isSQLReplaceStrict` is set to true (default is true), only the data is allowed to contain letters/numbers/_/$, if there are other characters, the module will report an error.

- In order to avoid the risk of SQL injection, please try to use `'@xxx@'` instead of `@xxx@` to mark SQL parts other than data, such as databases, etc.

### > Automatic acquisition of table field mechanism

- Automatically get table fields, which needs to be set in the `_ServeDao/Once.Config` file

    ```
    module._ServeDao.isAutoGenerateMap=true
    ```

- When the program starts, the module will automatically execute the following statements to obtain the information of the database table/view

    ```
    #Query the currently connected database
    SELECT DATABASE();
    
    #Get all the data tables/views under the database, and the database is the name of the database
    SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE WHERE TABLE_SCHEMA = 'database' AND (TABLE_TYPE='BASE TABLE' or TABLE_TYPE='VIEW')"
    
    #Get all field information under a data table/view, database is the database name, and table/view is the database table/view name
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = 'database' AND TABLE_NAME = 'table/view' "
    ```

- The structure of the data table/view recorded in the module is

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

### > Database Design Specification Recommendation

- The database should be divided into independent databases according to the business module as much as possible, which is conducive to the subsequent database performance expansion

- Table name, prefixed with `t_`

- The table field is prefixed with `table name_`. If the field is a foreign key, the name remains unchanged. If multiple identical foreign keys are recorded in a table, it is marked with `$serial number`, such as `resouce_id$0`, `resouce_id$1`

- Table fields need to set the default value. If the default value cannot be determined, please set the required table field not to be empty

- In order to facilitate the automatic injection of module, please try to use the text type (vchar)

- In order to improve execution performance, it is necessary to index the fields used for filtering and sorting

- The view name is prefixed with `v_`, and the view field follows the original table field name

# ◎ Module test separately

Before testing, you need to create a database and data table for testing in MySQL8.

- Database name: test

- Database character set: utf8mb4

- Database collation: utf8mb4_general_ci

- Data table name: test

- Data table field: test_id (vchar:255, primary key), test_value (varchr:255)

- Create SQL statements for data tables

    ```
    CREATE TABLE `test` (
      `test_id` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
      `test_value` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
      PRIMARY KEY (`test_id` DESC)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
    ```
    
- After creating the database and data table for testing, modify the configuration of the database in the `_ServeDao/Once.Config` file.

    ```
    module.sr-servedao.datasource.url=Database connection url
    module.sr-servedao.datasource.username=Username
    module.sr-servedao.datasource.password=Password
    #Open the database table and view to get it automatically, and every time the program starts, it will be automatically obtained
    module._ServeDao.isAutoGenerateMap=true
    ```

In the project root directory, run the shell instruction

```
python3 Christmas/Christmas.py ShellExcute/Compile#TestModule _ServeDao
```

You can also run ShellExcute>>Compile#TestModule through the Christmas plug-in, and enter `_ServeDao` in the terminal opened by the plug-in

### > Sample-insert data

- URL：http://127.0.0.1:8080/once/module/sample/insert

- Request method：POST

- Content type: application/json

    ```
    {
        "test_id":"123",
	    "test_value":"value value"
    }
    ```

- Expected results: The data is inserted into the database. If the same test_id is used, the data will be updated, Request return:

    ```
    {
    		"result": 1,
        "code": "200",
        "message": "OK"
    }
    ```

### > Sample-insert data in batches

- URL：http://127.0.0.1:8080/once/module/sample/insertBatch

- Request method：POST

- Content type: application/json

    ```
    {
        "insertList":[
            {"test_id":"1", "test_value":"value"},
            {"test_id":"2", "test_value":"value"}
        ]
    }
    ```

- Expected results: The data is inserted into the database. If the same test_id is used, the data will be updated, Request return:

    ```
    {
        "result": 2,
        "code": "200",
        "message": "OK"
    }
    ```

### > Sample-delete data

- URL：http://127.0.0.1:8080/once/module/sample/delete

- Request method：POST

- Content type: application/json

    ```
    {
        "test_id":"123"
    }
    ```

- Expected results: Data deleted, Request return:

    ```
    {
    		"result": 1,
        "code": "200",
        "message": "OK"
    }
    ```

### > Sample-update data

- URL：http://127.0.0.1:8080/once/module/sample/update

- Request method：POST

- Content type: application/json

    ```
    {
        "test_id":"1",
        "test_value":"update data"
    }
    ```

- Expected results: Data updated, Request return:

    ```
    {
        "result": 1,
        "code": "200",
        "message": "OK"
    }
    ```

### > Sample-query data

- URL：http://127.0.0.1:8080/once/module/sample/select

- Request method：POST

- Content type: application/json

    ```
    {
        "test_id":"2"
    }
    ```

- Expected results: Request return:

    ```
    {
        "result": 1,
        "code": "200",
        "message": "OK"
    }
    ```

### > Sample-query data list

- URL：http://127.0.0.1:8080/once/module/sample/selectList

- Request method：POST

- Content type: application/json

    ```
    {}
    ```
    
- Expected results: Request return:

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

### > Sample-SQL statement update (insert/delete/update data)

- URL：http://127.0.0.1:8080/once/module/sample/sqlUpdate

- Request method：POST

- Content type: application/json

    ```
    {
        "value":"sql update data",
        "id":"2"
    }
    ```

- Expected results: Request return:

    ```
    {
        "result": 1,
        "code": "200",
        "message": "OK"
    }
    ```

### > Sample-SQL statement query

- URL：http://127.0.0.1:8080/once/module/sample/sqlSelect

- Request method：POST

- Content type: application/json

    ```
    {
        "value":"%update%"
    }
    ```

- Expected results: Request return:

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

### > Sample-Database transactions

- URL：http://127.0.0.1:8080/once/module/sample/transactional

- Request method：POST

- Content type: application/json

    ```
    {
        "test_value":"value",
        "test_id":"3"
    }
    ```

- Expected result: The data is inserted, and the deletion operation is rolled back, and the request is returned:

    ```
    {
        "result": 1,
        "code": "200",
        "message": "OK"
    }
    ```

# ◎ Update list

**1.0=2024.06.20**

- Module create