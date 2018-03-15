# 个性化配置

如果需要，我们可以做一些个性化的配置，但是所有的配置项都不是必须的。

配置的Api是这样：
```java
Kalle.setConfig(...);
```

我们需要为它构建一个参数：
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .build();

Kalle.setConfig(config);
```

## 详细配置
* 配置工作线程执行器，如果不配置，将根据设备的CPU数量，计算出一个合适的队列线程池。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .workThreadExecutor(...)
    .build();
```

* 配置主线程执行器，如果不配置，将会使用`Handler`自动生成一个。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .mainThreadExecutor(...)
    .build();
```

* 配置发送`Body`时的编码格式，如果不配置将会采用系统默认编码，一般为`UTF-8`：
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .charset(...)
    .build();
```

* 添加全局`Header`，添加后会为每一个`Request`添加这个头，除非开发者为某个`Request`覆盖这个头的`key`。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .addHeader(...)
    .build();
```

* 配置全局代理，配置后每一个`Request`都会使用这个代理，除非开发者为某个`Request`设置空的代理或者指定其它代理：
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .proxy(...)
    .build();
```

* 配置全局`SSL`，`Https`需要我们决定怎样创建`SSLSocket`，同时告诉底层要信任哪些`Host`，我们通过`SSLSocketFactory`和`HostnameVerifier`来做这两件事。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .sslSocketFactory(...)
    .hostnameVerifier(...)
    .build();
```

* 配置全局`Request`连接服务器超时时间和读取响应数据超时时间。
```
KalleConfig config = KalleConfig.newBuilder()
    ...
    .connectionTimeout(...)
    .readTimeout(...)
    .build();
```

* 添加全局`Param`，添加后会为每一个`Request`添加这个参数，对于`Url`类型的`Request`，会把参数添加到`url`中；对于`Body`类型的`Request`，会把参数添加到`Body`中，暂不支持为`Body`类型`Request`添加全局参数。开发者可以为某个`Request`覆盖这个参数的`key`。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .addParam(...)
    .build();
```

* 配置全局`CacheStore`，用来增删改查缓存，如果不配置，将不会根据任何缓存模式对任何数据进行缓存。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .cacheStore(...)
    .build();
```

* 配置全局网络，用来让Kalle检查网络是否可用，如果不配置将会默认网络是可用的。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .network(...)
    .build();
```

* 配置全局连接生成工厂，决定底层使用`OkHttp`、`UrlConnection`或者`HttpClient`。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .connectFactory(...)
    .build();
```

* 配置全局`CookieStore`，用来增删改查`Cookie`，如果不配置将不会自动管理`Cookie`。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .cookieStore(...)
    .build();
```

* 添加全局拦截器，这里不做过多解释，请移步到[拦截器](/config/interceptor.md)查看。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .addInterceptor(...)
    .build();
```

* 配置全局转换器，变流器用来将服务器响应数据转化为开发者想要的格式，比如`JavaBean`。
```java
KalleConfig config = KalleConfig.newBuilder()
    ...
    .converter(...)
    .build();
```

至此，个性化配置的相关介绍全部介绍完毕，在左侧菜单栏还提供了一些常用的较复杂的配置教程，请移步查看。