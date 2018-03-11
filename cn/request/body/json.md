# JsonBody

`JsonBody`在使用时只需要传入要PUSH的Json字符串即可：
```java
String json = ...;
JsonBody body = new JsonBody(json);
body.onProgress(new OnProgress<JsonBody>() {
    @Override
    public void progress(JsonBody origin, int progress) {
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
JsonBody body = new JsonBody(json, utf8);
```