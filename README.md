# Pek Common

### 作者

**PeKnight**，Java码农，2015年毕业的新司机。

#### 联系方式

* E-mail peknight@qq.com
* Web-Site [PeKnight.com(尚未开发)](http://www.peknight.com/)

***

### 说明

本工程用于存放通用的工具类

***

### 内容

#### collection 包

* ArrayUtils 集合操作工具类

#### concurrent 包

* ThreadUtils 线程相关工具类

#### config 包

加载Common工程的配置包，其他Spring Boot工程引用Common工程时，可以在启动类上添加@EnableCommonConfiguration注解。
这个注解目前将自动加载com.peknight.common.springframework.context.ApplicationContextHolder类
以及 com.peknight.common.logging.CommonLogAspect切面。

#### IO 包

* InputUtils 输入相关工具类

* OutputUtils 输出相关工具类

#### logging 包

通用日志输出，在方法/类上添加@CommonLog注解将被CommonLogAspect拦截并输出方法执行参数、返回值、用时等信息。
resources中logback目录下提供了通用的日志输出配置。
此配置根据SpringBoot默认日志配置修改而来，区别在于日志文件按天输出。

#### string 包

* StringUtils 字符串相关工具类
