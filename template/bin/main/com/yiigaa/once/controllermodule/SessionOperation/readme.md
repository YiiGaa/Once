# Controller 模块：SessionOperation

## 模块功能简述

SessionOperation模块是操作Session的，主要功能包括：

> 从passParam数据池中的数据进行检查。

- 写入Session数据；
- 删除Session数据；
- 删除当前Session（整个Session将会失效）；
- 共享Session（Reids），详见此文档末尾的“共享Session”说明；

| 模块名           | 最新版本 | 模块位置   | 依赖服务 |
| ---------------- | -------- | ---------- | -------- |
| SessionOperation | v1.0     | Controller | 无       |

## Christmas 使用例子

使用此模块，在/Christmas/Input/MakeCodeNormal/Controller@xxx/target.json中使用。

- 写入Session数据，一般从passParam数据池中获取数据，如果获取不到，则使用此值作为写入的值

```
                        {
                            "templ":"MODULE-SessionOperation",
                            "param_templ":"PARAM-Hash",
                            "param":[{
                                "control":"save",
                                "session_id":"userId"
                            }]
                        }
```

- 删除Session的数据，多个key用","隔开

```
                        {
                            "templ":"MODULE-SessionOperation",
                            "param_templ":"PARAM-Hash",
                            "param":[{
																"control":"delete",
																"module_deleteKey":"session_id,session_power"
                            }]
                        }
```

- 删除当前Session（整个session将会失效），用于用户注销

```
                        {
                            "templ":"MODULE-SessionOperation",
                            "param_templ":"PARAM-Hash",
                            "param":[{
																"control":"delete",
																"module_deleteKey":"all"
                            }]
                        }
```

## 配置文件设置

| 参数                                 | 必要 | 缺省值 | 说明                                                         |
| ------------------------------------ | ---- | ------ | ------------------------------------------------------------ |
| server.servlet.session.cookie.name   | 否   |        | header中Session ID的名称                                     |
| server.servlet.session.timeout       | 否   |        | Session过期时间                                              |
| spring.session.store-type            | 否   |        | 是否开启redis Session共享，默认注释关闭                      |
| spring.redis.host                    | 否   |        | redis的IP，需要先开启redis Session共享                       |
| spring.redis.port                    | 否   |        | redis的端口，需要先开启redis Session共享                     |
| spring.redis.cluster.nodes           | 否   |        | redis cluster集群节点，与host、port排他，需要先开启redis Session共享 |
| spring.redis.password                | 否   |        | redis的密码，需要先开启redis Session共享                     |
| spring.redis.lettuce.pool.max-active | 否   |        | redis连接池最大线程数，需要先开启redis Session共享           |
| spring.redis.lettuce.pool.max-wait   | 否   |        | redis连接池最大阻塞等待时间（毫秒）                          |

> 关于开启Session共享，详见此文档末尾的“共享Session”说明。

配置文件配置项在SessionOperation/Templates.tmpl中，修改配置后，需要重新[连接模块](https://stoprefactoring.com/#content@content#framework/once/module-link)才能让配置生效。

- 生产环境的配置在propertiesTemplate中，连接模块后，会自动生成到/config/application.properties文件中；
- 开发环境的配置在propertiesTemplate local中，连接模块后，会自动生成到/config/application-local.properties文件中；

## moduleParam参数说明

### 通用参数

| 参数    | 必要 | 类型   | 缺省值 | 取值范围    | 说明                                                         |
| ------- | ---- | ------ | ------ | ----------- | ------------------------------------------------------------ |
| control | 是   | String |        | save/delete | save，写入Session数据；delete，删除Session数据或删除当前Session |

### 写入session参数（control:save）

| 参数   | 必要 | 类型   | 缺省值 | 取值范围 | 说明                                                         |
| ------ | ---- | ------ | ------ | -------- | ------------------------------------------------------------ |
| 自定义 | 是   | String |        |          | save，写入Session数据；delete，删除Session数据或删除当前Session |

### 通用参数

| 参数             | 必要 | 类型   | 缺省值 | 取值范围 | 说明                                                         |
| ---------------- | ---- | ------ | ------ | -------- | ------------------------------------------------------------ |
| module_deleteKey | 是   | String | all    |          | 要删除Session中数据的key，多个key用","隔开，如"session_id,session_power"。如果想要删除当前Session（整个Session将会失效），则可设置为"all" |

#### 模块参数参数类型说明：

- String：普通的字符串
- StringX：超级字符串，可以添加多个@xx@在里面，模块会自动把passParam中xx字段的值替换到这个字符串中。如，moduleParam中有{"abc":"@userid@"}，模块会自动把passParam中userid字段的值替换进去，如果passParam中不存在userid则保留原状。
- Bool：Bool型，true/false
- Int：Int型，数字类型

>注意：moduleParam的参数类型是指参数的真实类型，比如isNotNullError的类型为bool（true/false），但是在填入Christmas时，依然以字符串形式（如：{"isNotNullError":"true"}）写入，另外，int类型也需要写成字符串的形式如（如：{"maxFileSize":"20000"}）。

#### Once架构数据池说明：

- passParam：中间参数，所有模块共享的数据池，API请求时传过来的参数在会自动放在里面；

- moduleParam：模块独有参数，模块调用时传入，Christmas使用例子中的param实际上就是模块参数；

- returnParam：接口返回的数据池，所有模块共享的数据池，存进去后，最后会自动返回给请求端；

- httpRequest：HttpServletRequest，接口访问时的原始数据对象；

> passParam，sessionSave，returnParam的数据类型为JsonObject，moduleParam的数据类型为HashMap<String, String> 即字符串的键值对。

## 报错说明

报错说明详细见SessionOperation.java中的ErrorCodes。

## SpringBoot(Gradle)依赖

依赖包详细见SessionOperation.java中的GradleImport。

## 共享Session

当非单应用运行时，需要开启共享Session，这样才能保证多应用协调。开启Session共享，需要搭建Redis服务，Redis服务可以是单机模式、哨兵模式、Cluster集群模式。根据具体方式修改配置即可。

> Redis服务最好是独立的，不与其他功能共用，因为Redis是单线程读写的。如果有多个功能用到Redis，最好独立这些Redis服务，即使一台服务器运行多个Redis服务，也会比共用一个Redis服务的性能高。

开启共享Session无需改动现有的业务代码，只需要修改当前模块，并重新[连接模块](https://stoprefactoring.com/#content@content#framework/once/module-link)即可。

- 打开SessionOperation/Templates.tmpl中关于spring.redis的配置，并根据实际环境修改配置，配置说明详见上面的配置文件说明；

  > 最终配置会生成到/config/application-local.properties和/config/application.properties。这里的配置是Spring自带的，关于Session Redis更多的配置可查看Spring官方说明。

```
====== propertiesTemplate start ======
##ControllerModuel: SessionOperation
#server.servlet.session.cookie.name=identify
server.servlet.session.timeout=7200s
spring.session.store-type=redis
spring.redis.host=192.168.1.100
spring.redis.port=6380
#spring.redis.cluster.nodes=192.168.1.100:6379,192.168.1.100:6380,192.168.1.100:6381
spring.redis.password=YiiGaa@session0101!
spring.redis.lettuce.pool.max-active=50
spring.redis.lettuce.pool.max-wait=3000
====== propertiesTemplate end ======

====== propertiesTemplate local start ======
##ControllerModuel: SessionOperation
#server.servlet.session.cookie.name=identify
server.servlet.session.timeout=7200s
spring.session.store-type=redis
spring.redis.host=192.168.1.100
spring.redis.port=6380
#spring.redis.cluster.nodes=192.168.1.100:6379,192.168.1.100:6380,192.168.1.100:6381
spring.redis.password=YiiGaa@session0101!
spring.redis.lettuce.pool.max-active=50
spring.redis.lettuce.pool.max-wait=3000
====== propertiesTemplate local end ======
```


- 将SessionOperation/SessionConfig.java中的注释全部打开，这样在Redis中记录的信息才会是字符串，而非默认的序列化对象字节流；

- 将SessionOperation/SessionOperation.java中的redis session相关的依赖包打开（打开注释）

```
######GradleImport start######
//SessionOperation
        implementation 'org.springframework.session:spring-session-data-redis'
        implementation 'org.springframework.boot:spring-boot-starter-data-redis'
        implementation 'org.apache.commons:commons-pool2'
######GradleImport end######
```

- 最后，需要重新[连接模块](https://stoprefactoring.com/#content@content#framework/once/module-link)才能让共享Session生效。

```
#打开终端，进入工程中的Christmas文件夹中，操作命令前，需要先安装ruby环境
cd Christmas

#模块连接命令
ruby Christmas.rb ./Menu/MakeEngineeringNormal/AutoLinkModule
```

##   Redis连接池

在上面的设置中，Redis连接池lettuce只配置了spring.redis.lettuce.pool.max-active。此值无需改动，因为lettuce默认只会使用2个物理连接，即使配置再大的连接池也是没有用。

当然lettuce是可以扩大物理连接的，增加以下配置即可把连接池扩大到20个线程。

```
spring.redis.lettuce.pool.max-idle=20
spring.redis.lettuce.pool.min-idle=20
spring.redis.lettuce.pool.time-between-eviction-runs=1
```

但是，由于redis在实际读写数据时是单线程运行，所以无论是保持原来2个线程还是扩展到200的值都不会对并发性能有所提高。

> 测试使用的是redis6服务。

而且，经过测试，无论线程池扩大与否，性能（TPS）并无任何提高。

所以不用太过纠结连接池的大小。

##   Redis关键设置

由于此Redis是用于Session存储的，当大并发来临时，可能会存在Rdis内存溢出而崩溃的问题，所以需要设置Redis的内存上限。以下设置在redis.conf中设置即可。

```
#当达到该值时,将会执行配置的缓存淘汰策略(maxmemory的单位为字节)，本机80%
maxmemory 6gb
#淘汰策略
#noeviction：不淘汰任何数据，当内存不足时，新增操作会报错，Redis 默认内存淘汰策略。
#allkeys-lru：淘汰整个键值中最久未使用的键值。
#allkeys-random：随机淘汰任意键值;。
#volatile-lru：淘汰所有设置了过期时间的键值中最久未使用的键值。
#volatile-random：随机淘汰设置了过期时间的任意键值。
#volatile-ttl：优先淘汰更早过期的键值。
#volatile-lfu：淘汰所有设置了过期时间的键值中，最少使用的键值。
#allkeys-lfu：淘汰整个键值中最少使用的键值。
maxmemory-policy allkeys-lru
```

提高Redis性能的一些配置如下（Redis6.0）。

```
#表示当 Redis 运行内存超过最大内存时，是否开启 lazy free 机制删除
lazyfree-lazy-eviction yes
#表示设置了过期时间的键值，当过期之后是否开启 lazy free 机制删除
lazyfree-lazy-expire yes
#有些指令在处理已存在的键时，会带有一个隐式的 del 键的操作，比如rename(修改key的名称) 命令，当目标键已存在，Redis 会先删除目标键，如果这些目标键是一个 big key，就会造成阻塞删除的问题，此配置表示在这种场景中是否开启 lazy free 机制删除
lazyfree-lazy-server-del yes
#针对 slave(从节点) 进行全量数据同步，slave 在加载 master 的 RDB 文件前，会运行 flushall 来清理自己的数据，它表示此时是否开启 lazy free 机制删除
slave-lazy-flush yes

#把del命令自动换成unlink
lazyfree-lazy-user-del yes

#最大连接数
maxclients 30000

#最大连接数
tcp-backlog 1000

#用于设置慢查询的评定时间(微秒)，也就是说超过此配置项的命令，将会被当成慢操作记录在慢查询日志中，它执行单位是微秒 (1 秒等于 1000000 微秒)
slowlog-log-slower-than 20000
#用来配置慢查询日志的最大记录数
slowlog-max-len 100

#开启多io线程，cpu数减1，最大设置为6，主要为了改善写操作
io-threads 2
io-threads-do-reads yes
```


