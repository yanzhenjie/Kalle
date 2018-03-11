# FileBody

`FileBody`在使用时只需要传入要PUSH的`File`即可：
```java
File file = ...;
FileBody body = new FileBody(file);
body.onProgress(new OnProgress<FileBody>() {
    @Override
    public void progress(FileBinary origin, int progress) {
        // 包体的PUSH进度：progress.
    }
});

Kalle.post("http://www.example.com")
    .body(body)
    .perform(...);
```