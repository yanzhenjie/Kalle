# StringBody

`StringBody`在使用时只需要传入要PUSH的字符串即可：
```java
String string = ...;
StringBody body = new StringBody(string);
body.onProgress(new OnProgress<StringBody>() {
    @Override
    public void progress(StringBody origin, int progress) {
        // 包体的PUSH进度：progress.
    }
});

Kalle.post("http://www.example.com")
    .body(body)
    .perform(...);
```

开发者也可以指定`Charset`和`ContentType`：
```java
Charset utf8 = Charset.forName("utf-8");
StringBody body = new StringBody(string, utf8);
```

```java
Charset utf8 = Charset.forName("utf-8");
String contentType = "application/json";
StringBody body = new StringBody(string, utf8, contentType);
```