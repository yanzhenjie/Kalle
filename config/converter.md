# 转换器（Converter）

转换器是把服务器的相应结果转换为本地想要的类型的一个工具，在Kalle中`Converter`可以被配置为全局的，也可以为单个`Request`指定。

在这里，我们只介绍基于全局的，我们只需要在配置时设置`Converter`：
```java
KalleConfig.newBuilder()
    ...
    .converter(...)
    .build()
```

Converter是非常简单的一个接口，只需要把Response的响应体读出来，并转化为指定的类型对象即可。
```java
public interface Converter {
    /**
     * Convert data to the result of the target type.
     */
    <T> T convert(Type type, Response response) throws Exception;
}
```

## 转化为String
转化为`String`时其实非常简单，因为`Response`已经提供了`String`的转换：
```java
public class StringConverter imeplement Converter {
    <T> T convert(Type type, Response response) throws Exception {
        if(type == String.class) {
            return response.body().string();
        }
        ...;
    }
}
```

## JSON转化为Java对象
其实只要拿到String了，转化为其它对象就非常简单啦，这里我们分别以Gson和FastJson为例。
```java
public final class Convert {

    /**
     * Base on fastjson.
     */
    public static final Converter JSON_FASTJSON = new Converter() {
        @Override
        public <T> T convert(Type type, Response response) throws Exception {
            return JSON.parseObject(response.body().string(), type);
        }
    };

    /**
     * Base on gson.
     */
    public static final Converter JSON_GSON = new Converter() {

        final Gson GSON = new Gson();

        @Override
        public <T> T convert(Type type, Response response) throws Exception {
            return GSON.fromJson(response.body().toString(), type);
        }
    };
}
```

## 部分Request不使用全局解析
往往很多时候项目中的网络请求不仅仅是请求一个服务端，可能还要请求某个第三方服务端接口，如果他们返回的数据不是JSON怎么办，或者他们返回的数据格式跟我们预定义的不同怎么办，此时我们只需要给`Request`指定自己的转换器即可：
```
Kalle.get("http://www.example.com")
    .converter(...)
    ...
```

这里先上了一段请求接口的代码，其它详细用法可以看[示例](../sample/README.md)。