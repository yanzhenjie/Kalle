# 概述

[Kalle](https://github.com/yanzhenjie/Kalle)是一个Android平台的Http客户端，它遵循Http标准协议，支持同步请求和异步请求。

如果你想做一些Http功能性的测试，我已经为你准备好了服务端源码和在线测试地址。
* KalleServer源代码: [https://github.com/yanzhenjie/KalleServer](https://github.com/yanzhenjie/KalleServer)
* KalleServer在线接口：[http://kalle.nohttp.net](http://kalle.nohttp.net)

## 特性
* 支持GET、HEAD、OPTIONS、TRACE、POST、PUT、PATCH、DELETE请求方法
* 表单的提交，如普通字符串表单、带文件的表单（含多文件、大文件）
* 自定义RequestBody，如文件、字符串（JSON、XML、普通字符串）
* 支持SSL，默认不校验证书，开发者可以自定义证书
* 9种缓存模式，默认使用AES算法为缓存数据加密
* 自动管理Cookie，遵循Http协议，与浏览器实现相同原理
* 在任何时候取消请求，如未开始、正在执行时
* 全局反序列化转换器，直接请求JavaBean
* 基于拦截器的智能重定向与智能重试
* 支持开发者添加拦截器，例如Log打印、登录重试、参数签名
* 网络可用性缓存检查法，连接层可动态替换，如URLConnection、OkHttp或者HttpClient

更多用法，请开发者点击左侧的菜单栏进行学习。

> **注：**如果觉得左侧菜单一二级不容易区分或者主内容字体太小，可点击主内容左上角**A**切换主题。