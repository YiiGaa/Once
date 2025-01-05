<img src="https://raw.githubusercontents.com/YiiGaa/Trick/master/Prop/Common/Img/logo.png" width="300"/>

- 最新稳定版本: 2.2=2025.01.06
- 使用文档详见官网，[点击跳转使用手册](https://stoprefactoring.com/#content@content#framework/once/)

- Latest stable version: 2.1=2024.12.29
- For the user manual, please refer to the official website, [click to jump to the user manual](https://stoprefactoring.com/#content@content#framework/once/)

# 编写RESTful-API就像画流程图一样

Once架构分离了业务代码、模块代码。业务代码由JSON配置，是对一个API的模块使用顺序进行编排；模块代码是实际实现功能的代码，可无条件复用多个项目。

Once架构关注的是开发效率和维护成本，业务开发由于不需要关心实际的运行原理，可交由经验较浅的程序员（或其他领域程序员）完成；模块代码虽然需要有一定SpringBoot开发经验的程序员才能胜任，但是一经开发完成，即可无条件复用在多个项目。

Once架构是一套后端应用的顶层架构，架构本身只约束了工程结构和开发过程，对后端应用的基础技术无任何改造和深度封装，没有黑盒代码。

Once架构只是一套规则，实际上是一个SpringBoot工程，任何问题都可以通过翻看实际代码排查。

# Writing an RESTful-API is like drawing a flowchart

The Once architecture separates business code and module code. The business code is configured by JSON and is arranged in the order of the module use of an API; the module code is the code that actually implements the function and can unconditionally reuse multiple projects.

The Once architecture focuses on development efficiency and maintenance costs. Since business development does not need to care about the actual operating principle, it can be completed by inexperienced programmers (or programmers in other fields). Although module code requires programmers with certain SpringBoot development experience to be competent, one After the development is completed, it can be unconditionally reused in multiple projects.

Once architecture is a set of top-level architecture for back-end applications. The architecture itself only constrains the engineering structure and development process. There is no transformation and in-depth encapsulation of the basic technology of back-end applications, and there is no black box code.

The Once architecture is just a set of rules. It is actually a SpringBoot project. Any problem can be solved by looking through the actual code.

# 基础技术

Once架构是一种顶层架构，架构本身只约束了工程结构和开发过程，对基础技术无任何改造和深度封装。

架构中采用的基础技术如下：

- 开发语言：Java (JDK21或以上)
- 基础框架：SpringBoot 3.3.1
- 项目自动构建工具：Gradle

其中，由于Once架构只是一种规则，基础技术甚至是开发语言都是可以替换的，可联系官方打造个性化架构。

# Basic technology

Once architecture is a top-level architecture. The architecture itself only constrains the engineering structure and development process, and does not have any transformation or deep encapsulation of basic technology.

The basic technologies used in the architecture are as follows:

- Development language: Java (JDK21 or above)

- Basic framework: SpringBoot 3.3.1

- Project automatic construction tool: Gradle

Among them, because the Once architecture is only a rule, the basic technology and even the development language can be replaced, and you can contact the official to create a personalized architecture.

# 前提知识

在使用Once架构前，最好先学习SpringBoot、Java程序、Http请求等相关知识。

Once架构只是一种规则，实际上是一个SpringBoot工程，要想深度使用、或者排查一些错误，最好先学习这些知识。

# Prerequisite knowledge

Before using the Once architecture, it is best to learn SpringBoot, Java programs, Http requests and other relevant knowledge.

Once architecture is just a rule. It is actually a SpringBoot project. If you want to use it in depth or check some errors, it is best to learn this knowledge first.

# 设计思想

Once架构的设计思想可以直白地理解为：希望所有代码都只写一次。

代码可以只写一次的部分，希望能无条件复用在多个项目。代码不能只写一次的部分（业务强关联），那就简化其表达，用代码生成器生成它。

从宏观上讲，后端应用程序是多个请求接口的集合。而对单个接口来讲，是多个步骤的集合。

以一个审核博客的接口为例，可以对其理解为：第一步“用户鉴权”、第二步“检查必要参数”、第三步“填充默认参数”、第四步“数据库操作”。

![](design/overview-work-1.jpg)

所以Once架构将代码分离成了两层：模块代码、业务代码。

模块代码是实际执行功能的代码，只关心通用功能的实现，拥有统一的使用方式，所以这部分的代码可以只写一次，且可以无条件复用在多个项目。

业务代码是跟业务强关联的，只关心实现业务功能的步骤，也就是对模块使用顺序进行编排，例如：第一步检查参数，第二步操作数据库。所以业务代码可以使用JSON进行简化，并使用Christmas（代码生成器）生成代码。

![](design/overview-work-2.jpg)

# Design ideas

The design idea of the Once architecture can be straightforwardly understood as: It want all the code to be written only once.

The part of the code can only be written once, hoping that it can be unconditionally reused in multiple projects. The part of the code cannot be written only once (strong business association), then simplify its expression and generate it with a code generator.

Macroscopically speaking, a back-end application is a collection of multiple request APIs. For a single API, it is a collection of multiple steps.

Taking the API of a review blog as an example, it can be understood as: the first step is "user authentication", the second step is "checking the necessary parameters", the third step is "filling the default parameters", and the fourth step is "database operation".

![](design/overview-work-1-en.jpg)

Therefore, the Once architecture separates the code into two layers: module code and business code.

Module code is the code that actually performs functions. It only cares about the implementation of general functions and has a unified way of use, so this part of the code can be written only once and can be unconditionally reused in multiple projects.

The business code is strongly related to the business. It only cares about the steps to realize the business function, that is, arrange the order of use of the module, for example, the first step is to check the parameters, and the second step is to operate the database. Therefore, the business code can be simplified with JSON and generate code with Christmas (code generator).

![](design/overview-work-2-en.jpg)

# 工作原理

为了实现以上模块代码、业务代码分离，Once架构加入了数据池

数据池可以看作是一个接口中的全局变量，所有模块都可以对其进行添加、删除数据

数据池中包含两个重要部分，`passParam`和`returnParam`：

- `passParam`是用于临时存放数据，主要用于临时存放模块处理完的数据，接口逻辑开始时，会将接口请求参数存放在此
- `returnParam`是用于存放接口返回的数据，接口逻辑结束时，会自动将此部分的数据返回客户端

具体工作原理为：

- 在接口逻辑开始时，将请求参数及其他重要对象存放在数据池中

- 接口逻辑调用模块时，需要设置模块参数，以及将数据池传递给模块

- 模块按模块参数执行任务时，根据模块参数从数据池获取数据，或根据模块参数对数据池进行修改

- 模块执行任务完毕后，检查模块是否报错，不报错继续下一步，否则中断逻辑，接口返回

  > 报错返回只是默认行为，可以设置报错执行其他步骤的逻辑

- 接口返回时，自动将数据池的`returnParam`部分转换为Json字符串，并返回客户端

![](design/overview-work-3.jpg)

# Working principle

In order to realize the separation of the above module code and business code, the Once architecture has added a data pool

The data pool can be regarded as a global variable in an API, and all modules can add and delete data

The data pool contains two important parts, `passParam` and `returnParam`:

- `passParam` is the part used for temporary storage of data, which is mainly used to temporarily store the data processed by the module. At the beginning of the API logic, the API request parameters will be stored here.

- `returnParam` is used to store the data returned by the API. At the end of the interface logic, this part of the data will be automatically returned to the client.

The specific working principle is:

- At the beginning of the API logic, store the request parameters and other important objects in the data pool.

- When the API logic calls the module, it is necessary to set the module parameters and pass the data pool to the module.

- When the module performs tasks according to the module parameters, it obtains data from the data pool according to the module parameters, or modifies the data pool according to the module parameters.

- After the module completes the task, check whether the module reports an error. If there is no error, continue to the next step, otherwise the logic will be interrupted and the interface will return.

> Error return is only the default behavior, and you can set the logic of error execution of other steps.

- When the API returns, automatically convert the `returnParam` part of the data pool into a Json string and return the client.

![](design/overview-work-3-en.jpg)

# 历史版本
## 2.2
- [update]升级Christmas 2.3
- [bug]修复Windows下，Christmas及插件无法正常使用

## 2.1
- [update]业务代码（target.json）迁移到java代码同级目录
- [bug]当API请求方式为GET时，会对url参数进行多余的URL解码（报错）

## 2.0

- 升级Christmas 2
- 简化Controller、Service、Dao分层。Servcie层现为可选层，用作处理复杂接口；去除Dao层，将Dao层归为模块`_ServeDao`
- 模块迁移到新项目时，只需要复制文件夹
- 模块不再划分Controller/Service
- 模块新增初始化机制，如模块`_ServeDao`可自动获取数据库表属性，不再需要手工填写表属性
- 模块新增自动清理机制，如模块`_ServeDao`自动提交事务，`_OperFile`自动清理文件等
- 增加模块库功能，可通过Christmas下载/更新模块代码
- 请求参数不再限制在单层Json，支持多层嵌套Json
- 增加框架更新功能，可通过Christmas更新框架代码

## 1.0（停止维护）

- 严格按照Spring建议的Controller、Service、Dao分层
- 业务代码、模块代码分层
- 模块迁移时，只需要修改packeg位置
- 业务代码由Json配置，由代码生成器生成Java代码

# Historical version

## 2.2
- [update] Upgrade to Christmas 2.3
- [bug] Fixed the issue where Christmas and plugins could not be used normally under Windows

## 2.1

- [update] Business code (target.json) is migrated to the same-level directory of java code
- [bug] When the API request method is GET, the url parameters will be decoded (error)

## 2.0

- Upgrade Christmas 2

- Simplify the layering of Controller, Service and Dao. The Servcie layer is now an optional layer, which is used to handle complex interfaces; remove the Dao layer and classify the Dao layer as a module `_ServeDao`

- When the module is migrated to a new project, you only need to copy the folder

- The module is no longer divided into Controller/Service

- A new initialization mechanism is added to the module. For example, the module `_ServeDao` can automatically obtain database table attributes, and there is no need to fill in the table attributes manually

- A new automatic cleaning mechanism has been added to the module, such as the module `_ServeDao` automatic submission of transactions, `_OperFile` automatic cleaning of files, etc

- Add the function of module library to download/update module code through Christmas

- Request parameters are no longer limited to single-layer Json, and multi-layer nested Json is supported

- Add the framework update function and update the framework code through Christmas

## 1.0 (stop maintenance)

- Strictly follow the Controller, Service and Dao recommended by Spring

- Business code, module code layering

- When the module is migrated, you only need to modify the package location

- The business code is configured by Json, and the Java code is generated by the code generator
