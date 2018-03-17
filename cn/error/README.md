# 异常和失败
Http请求过程大概有三个阶段：连接-发送数据-读取数据，相应的在三个不同的阶段也会发生想听或者不同的异常，在Kalle中定义了三大类异常对应这三个过程中的异常，外加一个未知异常。

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
* ConnectException，发生其它连接时未预料到的异常时返回

例如`HostError`，有些开发者在某个局域网内设置了虚拟服务器，在这个局域网内可以正常连接服务器，但是一旦设备所在网络不在这个局域网内就会找不到这个虚拟服务器，就会报这个错。

## WriteException
`WriteException`很少发生，例如传文件时，开发者指定的这个文件被占用或者突然被删除时可能发生，还有一些情况是不可预料的。

## ReadException
`ReadException`并没有具体的定义什么类型的错误，它有四个子类具体定义了在读取服务器响应数据时可能发生的异常，另外一些没有预料到的异常还是通过`ReadException`返回：
* ReadTimeoutError，读取服务器数据超时时返回
* NoCacheError，在指定使用`READ_CACHE`[缓存模式](../cache)但是没有找到缓存时返回
* ParseError，Converter解析数据异常时返回
* ReadException，发生其它连接时未预料到的异常时返回

`ReadTimeoutError`发生的情景有两种，一种是网络不好时，客户端在一段时间内没有从流内读取到新的字节时发生；第二种是网络是好的，但是服务器在发送数据时由于去查询数据库或者IO操作，长时间未做出响应，客户端在一段时间内没有从流内读取到新的字节时发生。  

`ParseError`是为了防止开发者在`Converter`中处理不当时发生异常造成程序崩溃而加入的，对于有经验的开发者应该会自己在`Converter`中处理好，并结合自己的业务解析。

## 异步请求时接受异常回调
```java
Kalle.get("http://www.example.com")
    .perform(UserInfo.class, new SimpleCallback<UserInfo>() {
        @Override
        public void onResponse(SimpleResponse<UserInfo> response) {
        	// 请求响应了。
        	UserInfo user = response.result();
        }

        @Override
        public void onException(Exception e) {
        	// 请求发生异常了。
        }
    });
```

## 同步请求时处理异常
```java
try {
	SimpleResponse<UserInfo> response = Kalle.get(url).perform(UserInfo.class);
} cache(e) {
    ...; // 这里处理或者分发异常。
}
```