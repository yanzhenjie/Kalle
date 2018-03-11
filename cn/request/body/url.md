# UrlBody

在实际开发中，开发者不应该使用`UrlBody`，不过它被大量使用在Kalle内部，如果开发者感兴趣可以了解一下：
```java
Charset utf8 = Charset.forname("utf-8");

UrlBody body = UrlBody.newBuilder()
    .charset(utf8)
    .param("name", "kalle")
    .build();
```

`charset()`方法用来指定写出包体的编码，`param()`方法用来添加参数，还有一些其它有趣的Api，开发者可以自行查看源码进行学习。