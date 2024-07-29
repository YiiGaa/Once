# Christmas是一个通用代码生成器，如果代码不能只写一次，那就生成它。

最新稳定版本: v2.0
使用文档详见官网，[点击跳转使用手册](https://stoprefactoring.com/#content@content#framework/christmas/)

Christmas is a generic code generator that generates code if it can't be written just once.

Latest stable version: v2.0
For detailed documentation, please refer to the official website, [click to jump to the manual](https://stoprefactoring.com/#content@content#framework/christmas/).

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