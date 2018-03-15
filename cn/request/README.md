# Request
在[示例](../sample)中有提到，我们把请求根据请求方法的不同，分为了Url类请求和Body类请求。

在Kalle中，有一个`Request`基类，它有两个直接子类，一个`UrlRequest`，另一个是`BodyRequest`，它们分别根据Url类请求和Body类请求的不同特点实现了的`Request`基类规定的抽象方法。

`UrlRequest`和`BodyRequest`是最基本的子类了，使用它们发起请求后，我们可以得到[Response](../response)，这两个类的使用在[拦截器](../config/interceptor.md)中也有示例。

`UrlRequest`有一个直接子类是`SimpleUrlRequest`，`BodyRequest`有一个直接子类是`SimpleBodyRequest`。`SimpleUrlRequest`和`SimpleBodyRequest`只是实现了缓存，并且把服务器的响应流转为`byte[]`，因此它们可以支持异步，在Android开发中，这二者是我们使用频率最高的两个类。不过由于Kalle提供的链式调用的Api，在实际开发中我们并不会直接使用`SimpleUrlRequest`和`SimpleBodyRequest`这两个类，具体用法详见[示例](../sample)。

## UrlRequest
对于Url类请求，需要构建`UrlRequest`，例如下面这段代码构建的`UrlRequest`，最终的`url`是`http://www.example.com?name=kalle&password=123`:
```java
Url.Builder url = Url.newBuilder("http://www.example.com");
UrlRequest urlRequest = UrlRequest.newBuilder(url, RequestMethod.GET)
    .param("name", kalle)
    .param("password", 123)
    .build();
```

开发者也可以直接**同步**执行`UrlReuqest`来获取`Response`对象：
```java
Url.Builder url = Url.newBuilder("http://www.example.com");
UrlRequest urlRequest = UrlRequest.newBuilder(url, RequestMethod.GET)
    .param("name", kalle)
    .param("password", 123)
    .build();
Response response = new Call(urlRequest).execute();
```

关于`Url`的更多用法，开发者可以参考[Url](../url)

## BodyRequest
`BodyRequest`的用法和`UrlRequest`基本是完全一致的：
```java
Url.Builder url = Url.newBuilder("http://www.example.com");
BodyRequest bodyRequest = BodyRequest.newBuilder(url, RequestMethod.GET)
    .param("name", kalle)
    .param("password", 123)
    .build();
```

可以简单的理解为换了个单词，不过`BodyRequest`还可以添加`url`参数和`Binary`参数。

例如下面这段代码，最终的`url`是`http://www.example.com?name=kalle&age=18`，但是`RequestBody`中也会发送`sex=1&height=180cm`:
```java
Kalle.post("http://www.example.com")
    .urlParam("name", "kalle")
    .urlParam("age", 18)
    .param("sex", 1)
    .param("height", "180cm")
    .perform(...);
```

我们注意到，`BodyRequest`通过`urlParam()`添加的参数会拼接在`url`中，而通过`param()`方法添加的参数会通过`RequestBody`发送。