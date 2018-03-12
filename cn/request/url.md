# UrlRequest
在[示例](../sample)中有提到，我们把请求根据请求方法的不同，分为了Url类请求和[Body类](./body.md)请求，这里我们对Url类请求进行详解。

在Kalle中，有一个`Request`基类，它有两个直接子类，一个`UrlRequest`，另一个是[`BodyRequest`](./body.md)，它们分别根据Url类请求和Body类请求的不同特点实现了的`Request`基类规定的抽象方法。

`UrlRequest`和`BodyRequest`其实是最基本的子类了，使用它们发起请求后，我们可以得到[Response](../response)，这两个类的时候在[拦截器](../config/interceptor.md)中有示例，下面我们会详细讲解。

## 构建UrlRequest
一般如果开发者不自行扩展Kalle，也不使用拦截器，开发者是不需要构建`UrlRequest`和`BodyRequest`的。

这是最基础的构建用法：
```java
Url.Builder url = Url.newBuilder("http://www.example.com");
UrlRequest urlRequest = UrlRequest.newBuilder(url, RequestMethod.GET)
    .param("name", 123)
    .param("password", 456)
    .build;
```

你也可以直接**同步**执行`UrlReuqest`来获取`Response`对象：
```java
Url.Builder url = Url.newBuilder("http://www.example.com");
Response loginResponse = BodyRequest.newBuilder(url, RequestMethod.GET)
                    .param("name", 123)
                    .param("password", 456)
                    .perform();
```

关于`Url`的更多用法，开发者可以参考[Url](../url)