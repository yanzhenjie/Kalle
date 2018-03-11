# 请求头

* 一般情况下，Http协议中的那些请求头是不建议开发者进行设置的，Kalle会自动判断并设置，如果开发者需要更改时才建议设置。
* 一些自定义请求头的设置，可以通过全局配置，也可以为某个请求单独添加或者设置。

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