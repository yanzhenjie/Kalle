# BodyRequest
在[示例](../sample)中有提到，我们把请求根据请求方法的不同，分为了[Url类](./url.md)请求和Body类请求，这里我们对Url类请求进行详解。

在Kalle中，有一个`Request`基类，它有两个直接子类，一个[`UrlRequest`](./url.md)，另一个是`BodyRequest`，它们分别根据Url类请求和Body类请求的不同特点实现了的`Request`基类规定的抽象方法。