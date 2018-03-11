# RequestBody

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

Kalle已经提供了几个具体的实现，开发者可以点击文档左侧的菜单栏中的具体项目学习，也可以参考[示例](../../sample)学习。