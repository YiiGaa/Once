<img src="Test/logo.png" width="300"/>

Christmas是一个通用代码生成器，如果代码不能只写一次，那就生成它。

最新稳定版本: 2.2=2024.12.29
使用文档详见官网，[点击跳转使用手册](https://stoprefactoring.com/#content@content#framework/christmas/)

Christmas is a generic code generator that generates code if it can't be written just once.

Latest stable version: 2.2=2024.12.29
For detailed documentation, please refer to the official website, [click to jump to the manual](https://stoprefactoring.com/#content@content#framework/christmas/).

---

## 概述 Overview

Christmas是python3编写的开源代码生成器。

设计之初是为了用更简明的方式表达代码，希望枯燥重复的代码只写一次，开发者只需要关注最核心的逻辑问题，以降低调试、沟通成本，以及提高开发效率。

另外，Christmas也帮助代码工程拥有更加自由的代码结构，满足诸如模块代码完全独立、调试代码与生产代码分离等场景。

Christmas虽然是为代码服务的，但是实际上，它能处理所有的文件文本信息。

> 代码生成不仅能提高生产效率，防止一些无聊的代码错误而浪费的调试成本，而且能大幅简化代码，规整化工程，降低维护时重新理解代码的时间。

![](test/overview.png)

Christmas is an open source code generator written in python3.

It was designed to express code in a more concise way, hoping that boring and repetitive code is only written once and developers only need to focus on the most core logic issues to reduce debugging and communication costs, as well as improve development efficiency.

In addition, Christmas also helps code engineering to have a more free code structure to meet such scenarios as module code is completely independent and debugging code is separated from production code.

Although Christmas is for the code , but in fact , it can handle all the file text information .

> Code generation not only improves productivity and prevents wasted debugging costs from some boring code errors, but also drastically simplifies the code, regularizes the project, and reduces the time spent on re-understanding the code during maintenance.

![](test/en-overview.png)

---

## 功能概要 Functional Summary

Christmas含有五大功能：

- GenCode：通过代码模板生成代码
- MergeCode：从现有代码剪切合并代码，改变代码结构；
- CleanCode：清理文件、目录；
- DownloadCode：下载代码；
- ShellExcute：shell命令行编排；

Christmas contains five major functions

- GenCode: Generate code from code templates
- MergeCode: Cut and merge code from existing code , for change the code structure
- CleanCode: Clean up/Back up files or directories
- DownloadCode: download code
- ShellExcute: shell command line programming

## 历史版本 Revision History

### 2.2
- [update]config.json增加通用字段targetFile，允许引用外部target.json
- [update]GenCode(config.json)增加字段fillLackMap，用于设置在缺少参数时，替换为默认字符串
- [bug]DownloadCode下载非文本文件（图片等）会报错
- [bug]GenCode，替换@@value@@模板为数组数据时，转换异常
- [update] config.json adds the general field targetFile to allow references to external target.json
- [update] GenCode(config.json) adds the field fillLackMap, which is used to replace it with the default string when the parameter is missing
- [bug] DownloadCode will report an error when downloading non-text files (pictures, etc.)
- [bug] GenCode, when replacing @@value@@ template with array data, conversion exception

### 2.1

- [update]去除version.txt文件，改用Load.json文件，方便软件升级
- [update]GenCode增加isFillLack配置，用于设置在缺少参数时，是否填充为空字符串
- [update]DownloadCode 增加版本切换，增加License传参
- [bug]在非Christmas目录下运行，获取不了target.json
- [bug]MergeCode的Xms_path中的变量，如果变量后不加上其他字符，会匹配到空字符串
- [bug]GenCode中直接输出json数据，若json数据为多级嵌套，会输出文本不正确
- [update]Remove the version.txt file and use the Load.json file instead, which is convenient for software upgrade
- [update]GenCode adds the isFillLack configuration to set whether to fill an empty string when parameters are missing
- [update]DownloadCode adds version query switching, add License transmission parameters
- [bug]Running in a non-Christmas directory, you can't get target.json
- [bug]The variable in the Xms_path of MergeCode will match the empty string if no other characters are added after the variable
- [bug]Directly output json data in GenCode. If the json data is multi-level nested, the output text will be incorrect

### 2.0

- python3重新编写
- 追加下载代码DownloadCode、命令行编排ShellExcute功能
- 模板代码生成MakeCodeNormal调整至GenCode
- 模板代码生成MakeEngineeringNormal调整至MergeCode，设置简化
- 去除菜单生成MakeMenu功能
- 新增配套VScode插件
- 简化目录结构、配置规则
- python3 rewrite
- Add DownloadCode and ShellExcute function for command line programming.
- Adjust template code generation MakeCodeNormal to GenCode.
- Template code generation MakeEngineeringNormal adjusted to MergeCode, settings simplified.
- Remove MakeMenu function.
- Add VScode plugin.
- Simplify directory structure and configuration rules

### 1.0 （停止维护 Stop Maintenance）

- ruby编写
- 包含模板代码生成MakeCodeNormal、现有代码合并MakeEngineeringNormal、清理代码CleanFile、菜单生成MakeMenu功能
- ruby writing
- Include template code generation MakeCodeNormal, existing code merge MakeEngineeringNormal, clean code CleanFile, menu generation MakeMenu function
