# &根节点

空行输入`&`，IDE会提示所有被允许的根节点。

> 由于`&`开头表示根节点，请删除`&`前所有的空格。

支持以下类型的根节点：

- [RESTful-API定义](#restful-api-definition)
- [定时任务](#scheduled-task)
- [函数定义](#function-definition)

<a id="restful-api-definition"></a>
# RESTful-API定义

定义一个RESTful API入口，允许POST、DELETE、PUT、GET这4种请求方式。

- POST，用作增加
- DELETE，用作删除
- PUT，用作修改
- GET，用作查找

基本格式如下：

```
&post /[name]
&delete /[name]
&put /[name]
&get /[name]
```

`[name]`是Java函数名的一部分，允许：_、字母、数字(第二位开始)。

> 最终生成的Java代码，函数名为`[name]POST`、`[name]DELETE`、`[name]PUT`、`[name]GET`。

`[name]`是需要自定义修改的部分，比如我们可以定义一组`test`接口。

```
&post /test
		...
&delete /test
		...
&put /test
		...
&get /test
		...
```

## > API路径

本地运行时，默认的API路径为`/once/当前文件名/[name]`。

比如，在`abc.xmas`文件中定义了`&post /test`，则API路径为`/once/abc/test`。

第一层路径`once`，可以在`config/Xmas.Config`中修改，此设置仅在本地运行时生效。

```
server.servlet.context-path=/once
```

## > 请求数据：GET请求方式

`GET`请求方式，请求参数只能拼接在URL中。

```
/once/source/source?key1=value1&key2=value2
```

如果希望传输数组，可以使用相同的key设置。

```
/once/source/source?key1=list1&key1=list2
```

`Once`会自动将URL参数转换为Json格式并存入`passParam`。

请求前，客户端需要对URL参数的key/value部分进行`URL编码`，不然可能会报错。

## > 请求数据：其他请求方式

其他请求方式（POST/DELETE/PUT），支持3种请求数据类型。

`Once`会自动适配以下请求数据类型，并自动转换为Json格式并存入`passParam`。

- application/json
- application/x-www-form-urlencoded
- multipart/form-data

`application/json`是最为推荐的请求参数类型，这种参数类型可以构造多层数据，但是除非是批量操作，推荐限制在单层。

> 请求数据的最外层必须为`{}`类型

```
{
		"user_password":"xxx",
		"temp_newPassword":"xxx"
}
```

`application/x-www-form-urlencoded`是HTML的`form`表单数据。

`multipart/form-data`是可以上传文件的表单数据。

- 调用需要上传文件的接口，客户端必须使用此数据类型进行请求
- `Once`处理此类型数据时，不仅会把表单数据记录下来，也会把URL参数一并记录
- 在转换数据为Json格式时，文件会使用`##file##`标记，并存入`passParam`

## > 返回数据

接口返回的数据统一采用Json格式，其中`$code`、`$message`是固定记录错误码、错误信息的。

```
{
    "$code": "200",
    "$message": "OK"
}
```

<a id="scheduled-task"></a>
# 定时任务

定义定时任务，相当于SpringBoot会定时自动调用这个API。

基本格式如下：

```
&timer [name] [scheduled]
```

`[name]`是Java函数名的一部分，允许：_、字母、数字(第二位开始)。

> 最终生成的Java代码，函数名为`[name]Scheduled`。

`[scheduled]`是定时设置，采用`cron`表达式。

`[name]`、`[scheduled]`是需要自定义修改的部分，比如我们可以定义一个每5秒执行一次，名为`test`的任务。

```
&timer test 0 0/5 * * * *
```

定时任务的`passParam`是一个空的Json Object`{}`，需要设置初始数据请使用`_DataFilling`模块。

## >> cron表达式

cron表达式常用于设置定时任务，会被设置到SpringBoot的`@Scheduled(cron="")`。

常用的cron表达式：

```
#每5分钟执行一次
0 0/5 * * * ?

#每小时整点执行
0 0 * * * ?

#每日9:00:00执行
0 0 9 * * ?
#每日14:30:00执行
0 30 14 * * ?
#每天午夜执行
0 0 0 * * ?

#每周日午夜执行
0 0 0 ? * SUN
#工作日中午12点执行
0 0 12 ? * MON-FRI

#每月1日午夜执行
0 0 0 1 * ?
#每月15日中午12点执行
0 0 12 15 * ?
```

SpringBoot 的 Cron 表达式通常有`6 个`位置：

| 位置 | 含义                | 取值范围                          |
| :--- | :------------------ | :-------------------------------- |
| 1    | 秒（Second）        | 0–59                              |
| 2    | 分（Minute）        | 0–59                              |
| 3    | 时（Hour）          | 0–23                              |
| 4    | 日（Day）           | 1–31                              |
| 5    | 月（Month）         | 1–12 或 JAN–DEC                   |
| 6    | 星期（Day of week） | 0–6 或 SUN–SAT，0 和 7 都是星期日 |

以下例子为，每天凌晨 2 点执行：

```
0 0 2 * * ?
```

常用通配符与符号说明：

- `*`：任意值
    - 例如 `* * * * * ?`：每秒都执行
- `?`：在“日”和“星期”两个字段中，表示“不指定”，只用一个字段来定时间。
    - 例如：`0 0 0 ? * MON` 表示“每月的星期一凌晨 0 点”
- `,`：枚举多个值
    - 例如 `0 0 7,12 * * ?` 表示“每天 7 点和 12 点”执行
- `-`：连续范围
    - 例如 `0 0 9-17 * * MON-FRI`：工作日上午 9 点到下午 5 点每小时执行一次
- `/`：步长（从某值开始，每隔多少步）
    - `0/10 * * * * ?`：每 10 秒执行一次（从第 0 秒开始）
    - `0 0/15 * * * ?`：每 15 分钟执行一次
- `L`：在“日”字段中，表示“这个月的最后一天”；在“星期”字段中，表示“这个月的最后一个星期几”。
    - 例如 `0 0 1 L * ?`：每月最后一天凌晨 1 点执行

<a id="function-definition"></a>
# 函数定义

函数定义，可以被当前文件的其他函数、API、定时任务使用。

> 除非某个API/定时任务的逻辑非常复杂，否则不建议使用函数

基本格式如下：

```
&function [name]
```

`[name]`是Java函数名的一部分，允许：_、字母、数字(第二位开始)。

> 最终生成的Java代码，函数名为`[name]FUNCTION`。

`[name]`是需要自定义修改的部分，比如我们可以定义一个`abc`函数。

```
&function abc
```

当前文件的其他函数、API、定时任务需要使用某个函数时，需要使用逻辑选择器`@call`。以下例子表示调用`abc`函数。

```
@call &abc
```

调用函数时，数据池会被正常传入，若函数内对数据池进行修改，会被保留。