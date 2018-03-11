# 拦截器

拦截器是非常有用的，比如参数签名、Token/Cookie失效时登录重试、失败后重试、Log打印、重定向等等，我们是这样添加拦截器的：
```java
KalleConfig.newBuilder()
    .addInterceptor(new LoginInterceptor())
    .build()
```

## 演示：Token/Cookie失效后登录重试
这是一个Token/Cookie失效后重新登录的拦截器示例：
```
public class LoginInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originResponse = chain.proceed(request);
        if (originResponse.code() == 401) { // 原始请求失败，因为登录失效。
        	// 调用一下登录接口。
        	Url.Builder urlBuilder = Url.newBuilder(UrlConfig.LOGIN);
            Response loginResponse = BodyRequest.newBuilder(urlBuilder, RequestMethod.POST)
                    .param("name", 123)
                    .param("password", 456)
                    .perform();

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