# 响应

`Response`是Kalle的核心类，在[Interceptor](../config/interceptor.md)和[Request](../request)都提到过它的用法，通过`Response`可以获取到服务器响应时的`code`、`headers`和`body`（参考[ResponseBody](./body)）。

一般情况下，开发者不会经常使用`Response`类，但是在拦截器中可能会使用到，比如拦截器中登录的[Token/Cookie失效时后自动重新登录](../config/interceptor.md)的例子。如果开发者想基于Kalle自行封装异步请求和下载请求，就可能会使用到`Response`类。

`Response`的Api如下：
```java
public final class Response implements Closeable {

    /**
     * Get the mCode of response.
     */
    public int code();

    /**
     * Get http headers.
     */
    public Headers headers();

    /**
     * Get http body.
     */
    public ResponseBody body();

    /**
     * It is a redirect response code.
     */
    public boolean isRedirect();
```

`Response`的基础方法有如下三个：
* `code()`方法是获取服务器响应码。
* `headers()`方法是获取服务器响应头。
* `body()`是获取服务器的响应包体，参考[ResponseBody](./body)。

`Response`的扩展方法有两个：
* `close()`是关闭当前包体的流，断开本次请求和服务器建立的连接。
* `isRedirect()`是判断当前响应是否需要重定向。

值得注意的是`close()`方法是实现了`Closeable`接口而得来的，不是由`Response`直接提供。