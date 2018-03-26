# 参数
请求参数可以通过全局配置，也可以为某个请求单独添加或者设置，一般情况下我们都会为某个请求单独设置。

## Url参数和Body参数
在[示例](../sample)中我们把请求分为Url类请求和Body类请求，Url类请的请求参数都是通过`url`发送的，也就是以`?key=value&key=value`的形式携带的。而Body类的请求即可以通过`url`携带，也可以通过`RequestBody`以流的形式发送。

在Kalle中，Url类的请求参数会自动携带的`url`中；Body类的请求参数分为`url`参数和Body参数，所以需要开发者指定参数是跟在`url`后面还是放在`RequestBody`中发送。

对于Url类请求，例如下面这段代码，最终的`url`是`http://www.example.com?name=kalle&age=18`:
```java
Kalle.get("http://www.example.com")
    .param("name", "kalle")
    .params("age", 18)
    .perform(...);
```

对于Body类请求，例如下面这段代码，最终的`url`是`http://www.example.com?name=kalle&age=18`，但是`RequestBody`中也会发送`sex=1&height=180cm`:
```java
Kalle.post("http://www.example.com")
    .urlParam("name", "kalle")
    .urlParam("age", 18)
    .param("sex", 1)
    .param("height", "180cm")
    .perform(...);
```

我们注意到，在Body类请求中，通过`urlParam()`添加的参数会拼接在`url`中，而通过`param()`方法添加的参数会通过`RequestBody`发送。

## 全局配置
如果有一部分参数是每一个请求都需要带上的，那么开发者可以在Kalle中添加全局配置，Kalle会自动为每一个请求加上这些参数：
```java
Kalle.setConfig(
    KalleConfig.newBuilder()
        .addParam("name", "kalle")
        .addParam("name2", "okalle")
        .build()
);
```

这里需要注意的是，全局参数对于Url类的请求中会拼接在`url`中，对于Body类请求会通过`RequestBody`发送。

另外值得一提的是，全局参数也是可以被覆盖的，例如上面这段代码为每一个`Request`都添加了值为`kalle`的`name`参数，如果开发者想在某个`Request`修改这个值为`new-value`：
```java
Kalle.get("http://www.example.com")
    .param("name", "new-value")
    .perform(...);
```

## 单独指定
每一个`Reuqest`都可以添加自己的参数，这是几个示例：
```java
List<String> nameList = ...;

Kalle.get("http://www.example.com")
    .param("name", "kalle")
    .params("nameList", nameList)
    .perform(...);
```

单独指定时使用`Params`对象：
```java
Params params = new Params();
params.add("name", "kalle");
params.set("name", "okalle");

Kalle.get("http://www.example.com")
    .setParams(params)
    .perform(...);
```

Body类的请求还可以直接添加`File`和`Binary`模拟表单发送请求：
```java
File file = ...;
Binary binary = ...;

Kalle.post("http://www.example.com")
    .file("header", file)
    .binary("banner", binary)
    .perform(...);
```

更多的用法也可以参考在[示例](../sample)。