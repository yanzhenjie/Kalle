# 进度
进度监听一般用于Body类型的请求，一般用于表单文件上传、PUSH自定义`RequestBody`等。

## 基于表单
基于表单时我们可以监听某个文件的上传进度，也可以监听整个表单的发送进度：
```java
FileBinary header = new FileBinary(new File("/sdcard/header.jpg"))
    .onProgress(new OnProgress<FileBinary>() {
        @Override
        public void progress(FileBinary origin, int progress) {
            // 文件1的进度：progress.
        }
    });
FileBinary banner = new FileBinary(new File("/sdcard/banner.jpg"))
    .onProgress(new OnProgress<FileBinary>() {
        @Override
        public void progress(FileBinary origin, int progress) {
            // 文件2的进度：progress.
        }
    });

FormBody formBody = FormBody.newBuilder()
    .param("name", "kalle")
    .param("age", 18)
    .binary("header", header)
    .binary("banner", banner)
    .build();
formBody.onProgress(new OnProgress<FormBody>() {
    @Override
    public void progress(FormBody origin, int progress) {
        // 整体进度：progress.
    }
});

Kalle.post(UrlConfig.UPLOAD_BODY_FILE)
    .urlParam("filename", "qq.apk")
    .body(formBody)
    .perform(...);
```

## 基于RequestBody
基于`RequestBody`的进度监听和基于表单的进度监听其实是一样的。  

基于`FormBody`的上面已经有了详细的介绍，这里以`FileBody`举例：
```java
File file = ...;
FileBody body = new FileBody(file);
body.onProgress(new OnProgress<FormBody>() {
    @Override
    public void progress(FormBody origin, int progress) {
        // 包体的PUSH进度：progress.
    }
});
```

更过关于的`RequestBody`的用法可以查看[RequestBody](../request/body)和[示例](../sample)进行学习。