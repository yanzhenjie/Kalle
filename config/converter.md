# 转换器

转换器是把服务器的响应转换为本地预期的结果的一个工具，在Kalle中`Converter`可以被配置为全局的，也可以为单个`Request`指定。

`Converter`的作用是将服务器返回的数据转换为本地数据，或者说将服务器返回的结果转换为本地结果，同时可以包装业务的成功与否的结果。带来的好处是不用在主线程解析数据和也不用在主线程做复杂的业务判断。开发者可以参考[示例](../sample)和[业务封装](../sample/business.md)。

Converter是一个接口，只需要把Response的响应体读出来，并转化为指定的类型对象，包装成`SimpleResponse`对象返回即可。
```java
public interface Converter {
    <S, F> SimpleResponse<S, F> convert(Type succeed, Type failed,
            Response response, boolean fromCache) throws Exception;
}
```

## 参数释义
* 泛型`S`和参数succeed  
  用来限定业务成功时的数据类型。例如开发者请求用户信息时，`S`的类型可能是`UserInfo`，`succeed`的值就是`UserInfo.class`。
* 泛型`F`和参数failed  
  用来限定业务失败时的数据类型。例如开发者请求用户信息时服务端找不到指定用户信息，返回的业务数据肯定和`UserInfo`对不上，此时我们需要返回业务错误时的数据，比如返回一段提示`用户不存在...`，那么`S`的类型就是`String`，`faield`对应的值就是`String.class`。
* 参数`Response`  
  请求对应的响应，里面包涵了响应码`code`，响应头`headers`，相应包体`body`。
* 参数`fromCache`  
  请求的响应是否来自缓存。

## 全局配置和单独使用
如果开发者的所有请求的返回结果都是有规则且规则相同，那么`Converter`可以[配置](../config)成全局转换器，避免在每一个请求都需要配置一个`Converter`的麻烦。

```java
Converter converter = ...;

KalleConfig config = KalleConfig.newBuilder()
    ...
    .converter(converter)
    .build();
```

如果只有某几个请求的结果和不符合全局转换器的规则，那么可以单独指定某个请求的转换器。
```java
Converter newConverter = ...;

Kalle.post("http://www.example.com")
    .param("name", "kalle")
    .converter(newConverter)
    .perform(...);
```

**注**：所有没单独指定转换器的请求将自动使用配置的转换器，如果单独指定则仅仅使用单独指定的转换器。

## 响应转换为String
`Response`已经提供了`String`的转换，可以直接把`ResponseBody`转换为`String`。
```java
public class StringConverter implements Converter {

    public <S, F> SimpelResponse<S, F> convert(Type succeed, Type failed,
            Response response, boolean fromCache) throws Exception {
        S succeedData = (S) response.body().string();
        return SimpleResponse.<S, F>newBuilder()
                .code(response.code())
                .headers(response.headers())
                .fromCache(fromCache)
                .succeed(succeedData)
                .build();
    }
}
```

## JSON转化为Java对象
其实只要拿到String了，转化为其它对象就非常简单啦，这里我们分别以Gson为例。
```java
/**
 * Base on gson.
 */
public class GsonConverter implements Converter {

    private static final Gson GSON = new Gson();

    public <S, F> SimpelResponse<S, F> convert(Type succeed, Type failed,
            Response response, boolean fromCache) throws Exception {
        String jsonStrig = response.body().string();
        S succeedData = GSON.fromJson(response.body().string(), succeed);

        return SimpleResponse.<S, F>newBuilder()
                .code(response.code())
                .headers(response.headers())
                .fromCache(fromCache)
                .succeed(succeedData)
                .build();
    }
};
```

结合业务的封装请参考[业务封装](../sample/business.md)。