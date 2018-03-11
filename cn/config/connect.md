# 连接工厂

Kalle是一个完全独立的网络库，它限制任何实现Http协议底层连接库，因此它允许开发者自行决定使用何种底层连接库，比如`URLConnection`、`OkHttp、`HttpClient`。为了减小编译后Kalle的大小，Kalle默认提供了基于`URLConnection`的底层连接库（因为URLConnction是Java默认自带的），同时实现了基于`OkHttp`的备用的底层连接库，有兴趣的开发者可以自行实现基于其它项目的底层连接库。

## URLConnection 和 OkHttp
如果没有配置连接工厂，那么Kalle默认使用基于`URLConnection`的的连接工厂，开发者也可以明确指定使用默认的连接工厂：
```java
KalleConfig.newBuilder()
    ...
    .connectFactory(URLConnectionFactory.newBuilder().build())
    .build()
```

如果开发者需要指定底层使用基于OkHttp实现的连接工厂：
```java
KalleConfig.newBuilder()
    ...
    .connectFactory(OkHttpConnectFactory.newBuilder().build())
    .build()
```

`URLConnectionFactory`和`OkHttpConnectFactory`在Android4.4以上没有太大的区别。在Android4.4以下的系统中`URLConnection`不支持`DELETE`的请求方法发送`Body`，这是一个`jre`的底层bug，我们没有任何办法让它支持；在Android4.4及以上的系统中`URLConnection`使用OkHttp2来实现，所以是没有这一个bug的。在Android4.4及以上系统中，使用`URLConnectionFactory`和`OkHttpConnectFactory`的唯一区别是`URLConnectionFactory`用的OkHttp2，`OkHttpConnectFactory`使用的是OkHttp3。  

建议；如果你的应用需要在Android4.4以下的系统中运行，并且使用了`DELETE`请求方法，那么建议你直接使用`URLConnectionFactory`。

## 自定义实现
连接工厂是一个接口：
```java
public interface ConnectFactory {
    /**
     * According to the request attribute,
     * and the server to establish a connection.
     */
    Connection connect(Request request) throws IOException;
}
```

如果开发者自行实现了这个接口，只需要在被调用时，使用`url`建立连接，并且把`Request`中的所有请求头设置给负责底层连接的库即可，然后返回`Conneciton`接口即可。

`Connection`也是一个接口：
```
public interface Connection extends Closeable {

    /**
     * Gets output stream for socket.
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Gets response code for server.
     */
    int getCode() throws IOException;

    /**
     * Gets response headers for server.
     */
    Map<String, List<String>> getHeaders() throws IOException;

    /**
     * Gets input stream for socket.
     */
    InputStream getInputStream() throws IOException;

}
```

这里需要能拿到连接的输入流，以供Kalle发送数据；需要能拿到响应码以供Kalle判断如何读取；需要能拿到响应头，以供Kalle读取并判断如何读取响应包体；需要能拿到输出流，以供Kalle读取响应包体。`Connection`接口继承了`Closeable`接口，在`close()`方法被调用时应该断开此次请求时建立的连接。