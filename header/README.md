# 头

* 头分为请求头和响应头，开发者可以通过[`Request`](../request)拿到请求头，通过[`Response`](../response)拿到响应头。
* 一般情况下，Http协议中的那些请求头是不建议开发者进行设置的，Kalle会自动判断并设置，如果开发者需要更改时才建议设置。
* 一些自定义请求头的设置，可以通过全局配置，也可以为某个请求单独添加或者设置。

## 请求头
全局配置：
```java
Kalle.setConfig(
    KalleConfig.newBuilder()
        .addHeader("name", "kalle")
        .addHeader("name2", "okalle")
        .build()
);
```

单独指定：
```java
Kalle.get("http://www.example.com")
    .addHeader("name", "kalle") // 指定header。
    .setHeader("name", "okalle") // 覆盖key为name的header，值为okalle。
    .perform(...);
```

单独指定时使用`Headers`对象：
```java
Headers headers = new Headers();
headers.add("name", "kalle");
headers.set("name", "okalle");

Kalle.get("http://www.example.com")
    .setHeaders(headers)
    .perform(...);
```

## 响应头
响应头一般是从`Response`中获取到：
```java
Response response = ...;

Headers headers = response.headers();
```

从`Headers`中获取值时，Kalle已经提供了一些默认方法：
```java
    public String getCacheControl();

    public String getContentDisposition();

    public String getContentEncoding();

    public int getContentLength();

    public String getContentType();

    public String getContentRange();

    public long getDate();

    public String getETag();

    public long getExpires();

    public long getLastModified();

    public String getLocation();

    private long getDateField(String key);
```

开发者也可以获取某个自定义的`Key`对应的所有值：
```java
List<String> exampleList = headers.get("example");
```

当然还可以获取某个自定义`key`对应的第一个值（除了Cookie，一般都是一个`key`一个`value`）：
```java
String example = headers.getFirst("example");
```