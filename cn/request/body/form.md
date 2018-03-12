# FormBody
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

## 强制以表单形式提交`RequestBody`
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