# 异常和失败
Http请求过程大概有三个阶段：连接-发送数据-读取数据，相应的在三个不同的阶段也会发生想听或者不同的异常，在Kalle中定义了三大类异常对应这三个过程中的异常，另外需要考虑到未知异常。

* ConnectException，在连接服务器时发生的异常
* WriteException，在向服务器写出数据时发生的异常
* ReadException，在读取服务器响应数据时发生的异常
* Exception，其它未预料到的异常

## ConnectException
`ConnectException`并没有具体的定义什么类型的错误，它有四个子类具体定义了在连接服务器时可能发生的异常，另外一些没有预料到的异常还是通过`ConnectException`返回：
* URLError，Url格式错误或者无法解析时返回
* HostError，Url指定的Host在当前网络中找不到时返回
* NetworkError，网络不可用时返回
* ConnectTimeoutError，连接服务器超时时返回

例如`HostError`，有些开发者在某个局域网内设置了虚拟服务器，在这个局域网内可以正常连接服务器，但是一旦设备所在网络不在这个局域网内就会找不到这个虚拟服务器，就会报这个错。

## WriteException
`WriteException`很少发生，例如传文件时，开发者指定的这个文件被占用或者突然被删除时可能发生，还有一些情况是不可预料的。

## ReadException
`ReadException`并没有具体的定义什么类型的错误，它有四个子类具体定义了在读取服务器响应数据时可能发生的异常，另外一些没有预料到的异常还是通过`ReadException`返回：
* ReadTimeoutError，读取服务器数据超时时返回
* NoCacheError，在指定使用`READ_CACHE`[缓存模式](../cache)但是没有找到缓存时返回
* ParseError，Converter解析数据异常时返回
* DownloadError，当开发者设置的[下载策略](../download)不允许继续下载文件时抛出

`ReadTimeoutError`发生的情景有两种，一种是网络不好时，客户端在一段时间内没有从流内读取到新的字节时发生；第二种是网络是好的，但是服务器在发送数据时由于去查询数据库或者IO操作，长时间未做出响应，客户端在一段时间内没有从流内读取到新的字节时发生。  

`ParseError`是为了防止开发者在`Converter`中处理不当时发生异常造成程序发生异常，建议开发者应该在`Converter`中处理好异常情况，并结合自己的[业务封装](../sample/business.md)。

`DownloadError`是当开发者使用[下载策略](../download)插入业务时，中断下载过程时发生。通过`DownloadError`可以拿到当前请求的响应码和响应头。
```java
DownloadError e = ...;
int code = e.getCode();
Headers headers = e.getHeaders();
```

## 异步请求时接受异常回调
普通请求：
```java
Kalle.get("http://www.example.com")
    ...
    .perform(new SimpleCallback<UserInfo>() {

        ...

        @Override
        public void onException(Exception e) {
        	// 请求发生异常了。
        }
    });
```

下载请求：
```java
Kalle.Download.get("http://www.example.com")
    ...
    .perform(new SimpleCallback() {

        ...

        @Override
        public void onException(Exception e) {
        	// 请求发生异常了。
        }
    });
```

## 同步请求时处理异常
普通请求：
```java
try {
	SimpleResponse<UserInfo> response = Kalle.get(url).perform(UserInfo.class);
} cache(Exceptionh e) {
    ...; // 这里处理或者分发异常。
}
```

下载请求：
```java
try {
   String filePath = Kalle.Download.get("http://www.example.com")
      .directory("/sdcard")
      .perform();
} cache(Exceptionh e) {
    ...; // 这里处理或者分发异常。
}
```

## 处理异常
开发者应该处理请求时发生的异常，否则呈现给用户的可能是程序无反应（不是ANR）。

对于普通请求：
```java
Exception e = ...;

// 判断异常类型。
String message;
if (e instanceof NetworkError) {
    message = "网络不可用";
} else if (e instanceof URLError) {
    message = "Url格式错误";
} else if (e instanceof HostError) {
    message = "没有找到Url指定服务器";
} else if (e instanceof ConnectTimeoutError) {
    message = "连接服务器超时，请重试";
} else if (e instanceof WriteException) {
    message = "发送数据错误，请检查网络";
} else if (e instanceof ReadTimeoutError) {
    message = "读取服务器数据超时，请检查网络";
} else if (e instanceof ParseError) {
    message = "解析数据时发生异常";
} else {
    message = "发生未知异常，请稍后重试";
}
...
```

对于下载请求：
```java
Exception e = ...;

// 判断异常类型。
String message;
if (e instanceof NetworkError) {
    message = "网络不可用";
} else if (e instanceof URLError) {
    message = "Url格式错误";
} else if (e instanceof HostError) {
    message = "没有找到Url指定服务器";
} else if (e instanceof ConnectTimeoutError) {
    message = "连接服务器超时，请重试";
} else if (e instanceof WriteException) {
    message = "发送数据错误，请检查网络";
} else if (e instanceof ReadTimeoutError) {
    message = "读取服务器数据超时，请检查网络";
} else if (e instanceof DownloadError) {
    // TODO 这里只是简单的写了原因，具体业务需要开发者自行处理。
    message = "开发者中断下载";
} else {
    message = "发生未知异常，请稍后重试";
}
...
}
```

对于普通请求和下载请求的区别是`ParseError`和`DownloadError`，这两个异常的特点都是为了抓取开发者插入业务时发生的异常。