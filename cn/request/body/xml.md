# XmlBody

`XmlBody`在使用时只需要传入要PUSH的Xml字符串即可：
```java
String xml = ...;
XmlBody body = new XmlBody(xml);
body.onProgress(new OnProgress<XmlBody>() {
    @Override
    public void progress(XmlBody origin, int progress) {
        // 包体的PUSH进度：progress.
    }
});

Kalle.post("http://www.example.com")
    .body(body)
    .perform(...);
```

开发者也可以指定`Charset`：
```java
Charset utf8 = Charset.forName("utf-8");
XmlBody body = new XmlBody(xml, utf8);
```