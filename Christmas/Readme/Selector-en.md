# @ Logical Selector

Enter `@` on an empty line, and the IDE will suggest all logical selectors.

> Pay attention to the indentation level of logical selectors.

The following logical selectors are provided:

- [Multi-branch Selection](#multi-branch-selection)
- [Function Call](#function-call)
- [Error Capture](#error-capture)
- [Retry on Failure](#failed-retry)

**If the logic is too complex to be implemented even with logical selectors, it means this part has gone beyond the scope of business logic. In that case, create a new module and implement the specific logic in code.**

> In the sentence "What steps are needed to put an elephant into a refrigerator?", the "first step, second step, third step" part belongs to business logic. Questions such as "how exactly to put the elephant into the refrigerator" or "how large the refrigerator needs to be" are not concerns of business logic. Those are problems that should be solved by a `specific module`.

<a id="multi-branch-selection"></a>

# Multi-branch Selection

Multi-branch selection is equivalent to the `switch/case` structure supported by most programming languages.

The basic format is as follows. `@switch` and `@case` statements with the same indentation level are treated as one group:

```
@switch [option]
    ...default logic
@case [value]
    ...branch logic
@case ...
```

`[option]` is the key used to retrieve a value from `passParam`. The program automatically gets the corresponding value from `passParam` based on this key. If you want to access a nested JSON value, you can use `>>` to locate it, such as `key_1>>key_2`. If the value does not exist, the default is the string `"null"`.

`[value]` is the branch value. It is compared with the value retrieved by `[option]`. If they match, that branch logic is executed.

> The value retrieved by `[option]` is forcibly converted to a string, so `[value]` does not need to consider the original value type.

For example, the following defines multi-branch logic based on the value of `key` in `passParam`:

```
@switch key
    ...default logic
@case value 1
    ...branch logic
@case value 2
    ...branch logic
```

<a id="function-call"></a>

# Function Call

A function call invokes a custom function in the current `.xmas` file.

The basic format is as follows:

```
@call &[function]
```

`[function]` is the custom function name.

For example, to call the `abc` function:

```
@call &abc
```

When a function is called, the data pool is passed in normally, and any modifications made to the data pool inside the function are retained.

<a id="error-capture"></a>

# Error Capture

Error capture is mainly used to run exception-handling logic when normal logic fails.

The basic format is as follows. `@try` and `@error` statements with the same indentation level are treated as one group:

```
@try
    ...normal logic
@error
    ...error logic
```

<a id="failed-retry"></a>

# Retry on Failure

Retry on failure is mainly used for scenarios where an operation should be retried after it fails. For example, if a newly generated 8-digit random number already exists in the database and causes an error, the system needs to retry and generate a new random number.

The basic format is as follows:

```
@retry [count] [interval]
	...sub logic
```

`[count]` is the number of retry attempts. When the sub-logic finishes, if the result is an error and `retryTime>0`, the logic is executed again. When the logic is retried, `passParam` is restored to the state it had before entering `@retry`.

> No matter what value `[count]` is set to, the logic is executed at least once. Before the logic starts, 1 is subtracted first.

`[interval]` is the time interval between retries, in milliseconds. `1000=1s`. It can be set to `0`, which means no waiting interval is required.

For example, retry 10 times with a 1-second interval between attempts.

```
@retry 10 1000
	...sub logic
```
