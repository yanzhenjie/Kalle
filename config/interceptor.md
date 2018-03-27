# 拦截器

拦截器是非常有用的，比如参数签名、Token/Cookie失效时登录重试、失败后重试、Log打印、重定向等等，我们是这样添加拦截器的：
```java
KalleConfig.newBuilder()
    .addInterceptor(new LoginInterceptor())
    .build()
```

## 原理解析
拦截器是一个接口，它的源码很简单：
```java
public interface Interceptor {
    Response intercept(Chain chain) throws IOException;
}
```

开发者可以进行一些的操作全部依靠`Chain`类，`Chain`类也是一个接口，但是开发者不需要关注它的具体实现：
```java
public interface Chain {
    Request request();
    Response proceed(Request request);
    Call newCall();
}
```

一般情况下，我们会像下面这样使用：
```java
public class MyInterceptor implements Interceptor {
    @ovvride
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        ...; // 对Request做一些事情。

        return chain.proceed(request);
    }
}
```

比如开发者想在网络失败或者超时后重试一次，执行了`proceed(Request)`方法之后发生了了异常，再执行一次即可：
```java
@ovvride
public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();

    try {
        return chain.proceed(request);
    } catch(e) {
        return chain.proceed(request);
    }
}
```

对于`Chain.newCall()`一般是用与当前请求是成功的时候，开发者还想执行一次时使用。比如执行A请求时，服务端返回的结果是用户登录失效，客户端开发者需要使用客户端保存的用户密码重新登录后再执行上A请求，因为A请求是成功的（只是业务级别的失败），所以不能再次调用`Chain.proceed(Request)`了，只能从头再执行一遍上A请求，就要使用`Chain.newCall().execute()`，相当于`reset()->restart()`过程。

## 重试拦截器
重试拦截器（`RetryInterceptor`）对所有请求的失败都会重试*开发者可以指定重试次数*次，Kalle默认不会使用重试拦截器，开发者可以自行添加，重试拦截器实现如下：
```java
public class RetryInterceptor implements Interceptor {

    private int mCount;

    public RetryInterceptor(int count) {
        this.mCount = count;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        try {
            return chain.proceed(chain.request());
        } catch (IOException e) {
            if (mCount > 0) {
                mCount--;
                return intercept(chain);
            }
            throw e;
        }
    }
}
```

## 重定向拦截器
重定向拦截器（`RedirectInterceptor`）对所有重定向都不会拒绝，也就是说如果有100个接口一直重定向Kalle也不会拒绝，例如从`0->1->2->3->...->100`。Kalle默认不会使用重试拦截器，开发者可以自行添加，重定向拦截器实现如下：
```java
public class RedirectInterceptor implements Interceptor {

    public RedirectInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.isRedirect()) {
            Url oldUrl = request.url();
            Url url = oldUrl.location(response.headers().getLocation());
            Headers headers = request.headers();
            headers.remove(KEY_COOKIE);

            RequestMethod method = request.method();
            Request newRequest;
            if (method.allowBody()) {
                newRequest = BodyRequest.newBuilder(url.builder(), request.method())
                        .setHeaders(headers)
                        .setParams(request.copyParams())
                        .body(request.body())
                        .build();
            } else {
                newRequest = UrlRequest.newBuilder(url.builder(), request.method())
                        .setHeaders(headers)
                        .build();
            }
            IOUtils.closeQuietly(response);
            return chain.proceed(newRequest);
        }
        return response;
    }
}
```

## Log拦截器
Log拦截器打印了请求和响应的主要信息，打印的请求信息含请求地址、请求方法和请求头，打印的响应信息含响应码和响应头。可以通过构造方法控制`tag`和`enable`。
```java
public class LoggerInterceptor implements Interceptor {

    private final String mTag;
    private final boolean isEnable;

    public LoggerInterceptor(String tag, boolean isEnable) {
        this.mTag = tag;
        this.isEnable = isEnable;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (isEnable) {
            Log.i(mTag, "Print Request.");
            Log.i(mTag, String.format("Url: %1$s.", request.url().toString()));
            Log.i(mTag, String.format("Method: %1$s.", request.method().name()));
            Headers toHeaders = request.headers();
            for (Map.Entry<String, List<String>> entry : toHeaders.entrySet()) {
                Log.i(mTag, String.format("%1$s: %2$s.", entry.getKey(), entry.getValue()));
            }

            Response response = chain.proceed(request);
            Log.i(mTag, "Print Response.");
            Log.i(mTag, String.format("Code: %1$d", response.code()));
            Headers fromHeaders = request.headers();
            for (Map.Entry<String, List<String>> entry : fromHeaders.entrySet()) {
                Log.i(mTag, String.format("%1$s: %2$s.", entry.getKey(), entry.getValue()));
            }
            return response;
        }
        return chain.proceed(request);
    }

}
```

## 演示：Token/Cookie失效后登录重试
这是一个Token/Cookie失效后重新登录的拦截器示例：
```java
public class LoginInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originResponse = chain.proceed(request);
        if (originResponse.code() == 401) { // 业务失败，因为登录失效。
        	// 调用登录接口。
        	Url.Builder urlBuilder = Url.newBuilder(UrlConfig.LOGIN);
            BodyRequest loginRequest = BodyRequest.newBuilder(urlBuilder, RequestMethod.POST)
                .param("name", 123)
                .param("password", 456)
                .build();
            Response loginResponse = new Call(loginRequest).execute();

            // 登录成功。
            if (loginResponse.code() == 200) {
                // 关闭原始请求的连接。
                IOUtils.closeQuietly(originResponse);
                // 关闭登录请求的连接。
                IOUtils.closeQuietly(loginResponse);

                // 重新执行原始请求。
                return chain.call().execute();
            } else {
            	// 尝试登录未成功关闭登录连接，极少出现，除非服务器挂了。
                IOUtils.closeQuietly(loginResponse);

                // 不关闭原始请求连接，因为下面要返回原始请求的结果。
            }
        }
        return originResponse;
    }
}
```

## 演示：为Request签名
```java
public class SignInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // 第一步，获取所有请求参数。
        Params params = request.copyParams();

        // 第二步，定义List用于存储所有请求参数的key。
        List<String> keyList = new ArrayList<>();

        // 第三步，定义Map用于存储所有请求参数的value。
        Map<String, String> paramMap = new HashMap<>();

        // 第四步，拿到所有具体请求参数。
        for (Map.Entry<String, List<Object>> paramsEntry : params.entrySet()) {
            String key = paramsEntry.getKey();
            List<Object> values = paramsEntry.getValue();
            for (Object value : values) {
                if (value instanceof String) {

                    //第五步，将请求参数的key添加到list中用于排序。
                    keyList.add(key);

                    //第六步，将请求参数的value添加到Map中。
                    paramMap.put(key, (String) value);
                }
            }
        }

        // 第七步，对请求参数key进行排序。
        Collections.sort(keyList);

        StringBuilder builder = new StringBuilder();

        // 第八步，依次取出排序之后的key-value，并拼接。
        Iterator<String> keyIterator = keyList.iterator();
        if (keyIterator.hasNext()) {
            String key = keyIterator.next();
            builder.append(key).append("=").append(paramMap.get(key));
            while (keyIterator.hasNext()) {
                builder.append("&").append(key).append("=").append(paramMap.get(key));
            }
        }

        String query = builder.toString();

        // 第九步，对拼接好的参数签名。
        String signValue = ""; // Encryption.md5(query); // 一般是取MD5值吧。

        // 最后，把签名值设置到Header中。
        request.headers().set("sign", signValue);
        return chain.proceed(request); // 执行请求。
    }
}
```