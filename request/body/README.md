# 请求包体

在其它章节提到过，`RequestBody`是一个接口：
```java
public interface RequestBody {

    /**
     * Returns the size of the data.
     */
    long length();

    /**
     * Get the content type of data.
     */
    String contentType();

    /**
     * OutData data.
     */
    void writeTo(OutputStream writer) throws IOException;
}
```

第一个方法负责返回包体的长度，第二个方法负责返回包体的类型，第三个方法负责把包体写出去。

## 用法
`RequestBody`一般用于Body类型的请求的包体的自定义，例如表单、PUSH自定义JSON、XML、File等。

```java
RequestBody body = ...;

Kalle.post("http://www.example.com/user")
    .body(body)
    .perform(...);
```

Kalle已经提供了几个具体的实现
* FileBody
* StringBody
* JsonBody
* XmlBody
* FormBody
* UrlBody

## FileBody
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

## StringBody
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

## JsonBody
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

## XmlBody
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

## FormBody
`FormBody`用来模拟表单，同时开发者在使用Body类请求添加`Binary`参数后，内部会把`RequestBody`自动转化为`FormBody`的。

表单上传文件是Http中上传文件最常见的一种，几乎90%的上传文件都以form方式上传的，在Kalle中有一下几种写法，但是最终实现的功能效果都是相同的。

第一种：
```java
File file = ...;

Kalle.post("http://www.example.com")
    .file("header", file)
    .perform(...);
```

第二种：
```java
File file = ...;
Binary binary = new FileBinary(file);

Kalle.post("http://www.example.com")
    .binary("header", binary)
    .perform(...);
```

这样就把一个文件作为名为`header`的参数的值，以form的形式提交服务器了。

你也可以为`header`参数提交多个文件（前提是你们服务端支持或者需要），这里有几种方式：

第一种，为一个`key`添加多次`File`：
```java
File file1 = ...;
File file2 = ...;

Kalle.post("http://www.example.com")
    .file("header", file1)
    .file("header", file2)
    .perform(...);
```

第二种，添加`List<File>`:
```java
List<File> fileList = ...;

Kalle.post("http://www.example.com")
    .files("header", fileList)
    .perform(...);
```

第三种，为一个`key`添加多次`Binary`：
```java
Binary binary1 = ...;
Binary binary2 = ...;

Kalle.post("http://www.example.com")
    .binary("header", binary1)
    .binary("header", binary2)
    .perform(...);
```

第四种，添加`List<Binary>`:
```java
List<Binary> binaries = ...;

Kalle.post("http://www.example.com")
    .binary("header", binaries)
    .perform(...);
```

### 强制以表单形式提交RequestBody
在Body类请求中，在不添加`Binary`参数时不会以表单的形式提交`RequestBody`，但是开发如果需要，可以自行设置`RequestBody`为`FormBody`：
```java
File file = ...;

FormBody body = FormBody.newBuilder()
    .param("name", "Kalle")
    .param("age", 18)
    .param("sex", 1)
    .file("header", file)
    .build();

Kalle.post("http://www.example.com")
    .body(body)
    .perform(...);
```

## UrlBody
在实际开发中，开发者不应该使用`UrlBody`，不过它被大量使用在Kalle内部，如果开发者感兴趣可以了解一下：
```java
Charset utf8 = Charset.forname("utf-8");

UrlBody body = UrlBody.newBuilder()
    .charset(utf8)
    .param("name", "kalle")
    .build();
```

`charset()`方法用来指定写出包体的编码，`param()`方法用来添加参数，还有一些其它有趣的Api，开发者可以自行查看源码进行学习。