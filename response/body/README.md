# 响应包体

`ResponseBody`是服务器的响应包体，为了方便它提供了3个简单的实现方法获取服务器的包体数据：
```java
public interface ResponseBody extends Closeable {

    /**
     * Transform the response data into a string.
     */
    String string() throws IOException;

    /**
     * Transform the response data into a byte array.
     */
    byte[] byteArray() throws IOException;

    /**
     * Transform the response data into a stream.
     */
    InputStream stream() throws IOException;
}
```

* `string()`是把服务器包体数据转为String，内部所有的实现类跟根据响应头中的`ContentType`自动解码。
* `byteArray()`是把服务器包体数据转为`byte`数组。
* `stream()`是把服务器包体数据转为流，它的可塑性是最强的。