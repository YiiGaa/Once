# & Root Node

Enter `&` on an empty line, and the IDE will suggest all available root nodes.

> Because `&` at the beginning indicates a root node, remove any spaces before `&`.

The following root node types are supported:

- [RESTful API Definition](#restful-api-definition)
- [Scheduled Task](#scheduled-task)
- [Function Definition](#function-definition)

<a id="restful-api-definition"></a>
# RESTful API Definition

Defines a RESTful API entry point and supports the four request methods: POST, DELETE, PUT, and GET.

- POST: used for create operations
- DELETE: used for delete operations
- PUT: used for update operations
- GET: used for query operations

The basic format is as follows:

```
&post /[name]
&delete /[name]
&put /[name]
&get /[name]
```

`[name]` is part of the Java method name. It can contain `_`, letters, and numbers (starting from the second character).

> In the generated Java code, the method names are `[name]POST`, `[name]DELETE`, `[name]PUT`, and `[name]GET`.

`[name]` is the customizable part. For example, we can define a set of `test` APIs.

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

## > API Path

When running locally, the default API path is `/once/.xmas file name/[name]`.

For example, if `&post /test` is defined in the `abc.xmas` file, the API path is `/once/abc/test`.

The first-level path `once` can be changed in `config/Xmas.Config`. This setting only takes effect when running locally.

```
server.servlet.context-path=/once
```

## > Request Data: GET Requests

For `GET` requests, parameters can only be appended to the URL.

```
/once/source/source?key1=value1&key2=value2
```

If you want to pass an array, you can use the same key multiple times.

```
/once/source/source?key1=list1&key1=list2
```

`Once` automatically converts URL parameters into JSON format and stores them in `passParam`.

Before sending the request, the client must URL-encode the key and value parts of the URL parameters. Otherwise, an error may occur.

## > Request Data: Other Request Methods

Other request methods (POST/DELETE/PUT) support three request data types.

`Once` automatically adapts to the following request data types, converts them into JSON format, and stores them in `passParam`.

- application/json
- application/x-www-form-urlencoded
- multipart/form-data

`application/json` is the recommended request parameter type. It can represent nested data, but unless you are performing batch operations, keeping the structure to a single level is recommended.

> The outermost layer of the request data must be of type `{}`.

```
{
		"user_password":"xxx",
		"temp_newPassword":"xxx"
}
```

`application/x-www-form-urlencoded` is the standard HTML `form` data format.

`multipart/form-data` is the form data format used for file uploads.

- When calling an API that uploads files, the client must use this request type
- When `Once` processes this type of data, it records both the form data and the URL parameters
- When converting the data to JSON format, files are marked with `##file##` and stored in `passParam`

## > Return Data

API responses always use JSON format. Among them, `$code` and `$message` are fixed fields used to record the error code and error message.

```
{
    "$code": "200",
    "$message": "OK"
}
```

<a id="scheduled-task"></a>
# Scheduled Tasks

A scheduled task is equivalent to SpringBoot automatically calling this API on a schedule.

Basic format:

```
&timer [name] [scheduled]
```

`[name]` becomes part of the Java method name. It may contain `_`, letters, and digits, but digits are only allowed from the second character onward.

> In the generated Java code, the method name becomes `[name]Scheduled`

`[scheduled]` is the scheduling rule and uses a `cron` expression.

For example, the following defines a task named `test` that runs every 5 seconds:

```
&timer test 0 0/5 * * * *
```

The `passParam` of a scheduled task is an empty JSON object `{}`. If you need initial data, use the `_DataFilling` module.

## > Cron Expressions

Cron expressions are commonly used for scheduled tasks and are passed to SpringBoot's `@Scheduled(cron="")`.

Common examples:

```
# Execute every 5 minutes
0 0/5 * * * ?

# Execute every hour on the hour
0 0 * * * ?

# Execute at 9:00:00 every day
0 0 9 * * ?
# Execute at 14:30:00 every day
0 30 14 * * ?
# Execute at midnight every day
0 0 0 * * ?

# Execute every Sunday at midnight
0 0 0 ? * SUN
# Execute at 12:00 on weekdays
0 0 12 ? * MON-FRI

# Execute at midnight on the 1st day of every month
0 0 0 1 * ?
# Execute at 12:00 on the 15th day of every month
0 0 12 15 * ?
```

SpringBoot cron expressions usually have `6` positions:

| Position | Meaning      | Range                                |
| :------- | :----------- | :----------------------------------- |
| 1        | Second       | 0–59                                 |
| 2        | Minute       | 0–59                                 |
| 3        | Hour         | 0–23                                 |
| 4        | Day          | 1–31                                 |
| 5        | Month        | 1–12 or JAN–DEC                      |
| 6        | Day of week  | 0–6 or SUN–SAT, and 0/7 both mean Sunday |

The following example runs every day at 2:00 AM:

```
0 0 2 * * ?
```

Common wildcards and symbols:

- `*`: any value
    - Example: `* * * * * ?` means execute every second
- `?`: in the Day and Day-of-week fields, means "not specified"
    - Example: `0 0 0 ? * MON` means every Monday at 00:00
- `,`: multiple values
    - Example: `0 0 7,12 * * ?` means execute every day at 7:00 and 12:00
- `-`: a continuous range
    - Example: `0 0 9-17 * * MON-FRI` means execute once every hour from 9 AM to 5 PM on weekdays
- `/`: step value
    - `0/10 * * * * ?` means execute every 10 seconds starting from second 0
    - `0 0/15 * * * ?` means execute every 15 minutes
- `L`: in the Day field, means the last day of the month; in the Day-of-week field, means the last occurrence of that weekday in the month
    - Example: `0 0 1 L * ?` means execute at 1:00 AM on the last day of every month

# Function Definitions

Function definitions can be used by other functions, APIs, and scheduled tasks within the current file.

> Unless the logic of an API or scheduled task is especially complex, using functions is generally not recommended

Basic format:

```
&function [name]
```

`[name]` becomes part of the Java method name. It may contain `_`, letters, and digits, but digits are only allowed from the second character onward.

> In the generated Java code, the method name becomes `[name]FUNCTION`

For example, you can define a function named `abc`.

```
&function abc
```

When other functions, APIs, or scheduled tasks in the current file need to use a function, use the logical selector `@call`.

```
&post /test
		@call &abc
```

When a function is called, the data pool is passed in as usual, and any modifications made inside the function are preserved.

