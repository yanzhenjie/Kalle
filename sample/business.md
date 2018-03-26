# 业务封装
在封装业务之前开发者应该先了解[示例](../sample)和[转换器](../config/converter.md)对Kalle发起请求和`Converter`有个基本的了解。

本文将以JSON为例，结合比较常见的几种业务场景做示例。由于要服务端数据需要描述业务是否成功和业务失败的原因，因此我们来约定一下服务端返回数据的结构。

在任何情况下，服务器返回的`body`中的JSON数据必须是一个对象，用`code`返回业务状态，用`data`返回客户端要请求的实际数据，用`message`返回业务失败后的提示信息；其中`code`值为1时表示业务成功，`code`值为其它时表示业务失败；其中`data`可以是`Entity`，也可以是`List<Entity>`。**需要特别注意**的是有些服务端会把`httpCode`同时作为业务状态码，这也是完全正确的，封装原理与本文相同。

服务端返回的数据的结构应该是：
```java
{
    "code": 1,
    "data": ?,
    "message": "I am message."
}
```

例如返回的业务数据是空时：
```json
{
    "code": 1,
    "data": null,
    "message": "Succeed"
}
```

例如返回的业务数据是字符串或者数字时：
```json
{
    "code": 1,
    "data": "20180101",
    "message": "Succeed"
}
```

例如返回的业务数据是一个对象时：
```json
{
    "code": 1,
    "data": {
        "name": "Kalle",
        "url": "https://github.com/yanzhenjie/Kalle",
    },
    "message": "Succeed"
}
```

例如返回的业务数据是一个列表时：
```json
{
    "code": 1,
    "data": [
        {
            "name": "Kalle",
            "url": "https://github.com/yanzhenjie/Kalle",
        },
        {
            "name": "Kalle",
            "url": "https://github.com/yanzhenjie/Kalle",
        },
    ],
    "message": "Succeed."
}
```

例如当业务失败时返回失败原因：
```json
{
    "code": 0,
    "data": null,
    "message": "帐号密码错误"
}
```

客户端对应的`JavaBean`是这样子：
```java
/**
 * 目标实体类。
 */
public class UserInfo {
    String name;
    String url;
    ...
}

/**
 * Http业务包装类，Entity。
 */
public class HttpEntity<T> {
    int code;
    String data;
    String message;
    ...
}
```

上述数据的结构是有规则的，`code`和`message`的数据类型是没有变化过的，只有`data`的类型会变化，但是`data`的类型无论怎么变，它的根本形式还是`String`，因此我们先用`String`来接受，然后再解析成目标实体对象。这只是用来辅助我们封装的，不会在每一个接口都需要这样去解析。

## Converter和Callback的关系
在[示例](../sample)和[转换器](../config/converter.md)中分别介绍了`Callback`和`Converter`，我们在`Callback`中指定了业务成功和业务失败时的本地数据类型，在`Converter`中把服务器的返回的数据转换为本地数据类型的数据。

在异步请求中需要用`Callback()`实例来接受响应结果，`Callback`被初始化时需要指定两个泛型，第一个泛型是当业务成功时需要返回的对象的类型，第二个泛型是当业务失败时需要返回的对象的类型。

为了便于理解，先举个例子。我们在成功时返回AB，在失败时返回CD：
```java
Kalle.get("http://www.example.com")
    .perform(new Callback<AB, CD>() {
        @Override
        public void onResponse(SimpleResponse<AB, CD> response) {
            if(response.isSucceed()) {
                // 业务成功，拿到业务成功的数据。
        	    AB ab = response.succeed();
                ...
            } else {
                // 业务失败，拿到业务失败的数据。
                CD cd = response.failed();
                ...
            }
            ...
        }
    });
```

解析数据时需要在`Converter`的`convert()`方法中解析，`convert()`方法规定了两个泛型，分别对应上述`Callback`的两个泛型，`convert()`方法入参有两个`Type`分别对应上述两个泛型的具体类型。
```java
public interface Converter {
    <S, F> SimpleResponse<S, F> convert(Type succeed, Type failed,
            Response response, boolean fromCache) throws Exception;
}
```

这里的`S`对应上面`AB`，`F`对应上面的`CD`，`succeed`是`S`的具体类型，`failed`是`F`的具体类型。例如当`AB`是`UserInfo`时，那么`succeed`就是`UserInfo.class`，当`CD`是`String`时，那么`failed`就是`String.class`。

## 封装
如果客户端老老实实先请求`HttpEntity`，那么在`onResponse()`中的业务判断过程肯定会特别繁琐。能不能直接请求到服务器返回的`UserInfo`或者`List<UserInfo>`，并且仅仅只做一次业务成功与否的判断就可以呢，答案是肯定的。

理想的情况下我们想这样写代码：
```java
Kalle.get("http://www.example.com")
    .perform(new SimpleCallback<UserInfo>() {
        @Override
        public void onResponse(SimpleResponse<UserInfo> response) {
            if(response.isSucceed())) { // Http成功，业务也成功。
                UserInfo user = response.succeed();
                ...
            } else {
                Toast.show(response.failed());
            }
        }
    });

...

Kalle.get("http://www.example.com")
    .perform(new SimpleCallback<List<UserInfo>>() {
        @Override
        public void onResponse(SimpleResponse<List<UserInfo>> response) {
            if(response.isSucceed())) { // Http成功，业务也成功。
                List<UserInfo> userList = response.succeed();
                ...
            } else {
                Toast.show(response.failed());
            }
        }
    });
```

然后我们按照理想情况来封装`Converter`：
```java
public class GsonConverter imeplement Converter {

    private static final Gson GSON = new Gson();

    @Override
    public <S, F> SimpleResponse<S, F> convert(Type succeed, Type failed,
            Response response, boolean fromCache) throws Exception {
        S succeedData = null; // 业务成功的数据。
        F failedData = null; // 业务失败的数据。

        int code = response.code();
        String serverJson = response.body().string();
        if (code >= 200 && code < 300) { // Http请求成功。
            try {
                HttpEntity http = GSON.fromJson(serverJson, HttpEntity.class);

                if (http.getCode() == 1) { // 服务端业务处理成功。
                    String data = http.getData();
                    // 如果是请求String或者int。
                    if (succeed == String.class) {
                        succeedData = (S) data;
                    } else if (succeed == Integer.class) {
                        try {
                            Integer succeedInt = Integer.parseInt(data);
                            succeedData = (S) succeedInt;
                        } catch (NumberFormatException e) {
                            failedData = (F) "服务器数据格式错误";
                        }
                    } else {
                        // 请求JavaBean、List或者Map。
                        try {
                            succeedData = GSON.fromJson(data, succeed);
                        } catch (Exception e) {
                            failedData = (F) "服务器数据格式错误";
                        }
                    }
                } else {
                    // 服务端业务处理失败，读取错误信息。
                    failedData = (F) http.getMessage();
                }
            } catch (Exception e) {
                failedData = (F) "服务器数据格式错误";
            }
        } else if (code >= 400 && code < 500) {// 一般是由于不符合接口要求。
            failedData = (F) "发生未知异常";
        } else if (code >= 500) {
            failedData = (F) "服务器开小差啦";
        }

        // 包装成SimpleResponse返回。
        return SimpleResponse.<S, F>newBuilder()
                .code(response.code())
                .headers(response.headers())
                .fromCache(fromCache)
                .succeed(succeedData)
                .failed(failedData)
                .build();
    }
}
```

`Converter`中代码初看起来比较多，但是一点也无复杂。我们来分析一下解析过程：
1. 先拿到`httpCode`和`httpBody`
2. 根据`httpCode`判断客户端是否正常请求，服务端接口是否正常响应
3. 解析`httpBody`数据为`HttpEntity`实体
4. 从`HttpEntity`实体判断业务是否成功
5. 业务成功后解析业务数据中的`data`为我们真正想要的数据类型
6. 对各个解析加一些`try-catch`的异常兼容处理

## 封装之后的使用示例

### 请求JavaBean
JSON数据如下：
```json
{
    "code": 1,
    "data": {
        "name": "Kalle",
        "url": "https://github.com/yanzhenjie/Kalle",
    },
    "message": "Succeed"
}
```

请求的代码如下：
```java
Kalle.get("http://www.example.com")
    .perform(new SimpleCallback<UserInfo>() {
        @Override
        public void onResponse(SimpleResponse<UserInfo> response) {
            if(response.isSucceed())) { // Http成功，业务也成功。
                UserInfo user = response.succeed();
                ...
            } else {
                Toast.show(response.failed());
            }
        }
    });
```

### 请求List
JSON数据如下：
```json
{
    "code": 1,
    "data": [
        {
            "name": "Kalle",
            "url": "https://github.com/yanzhenjie/Kalle",
        },
        {
            "name": "Kalle",
            "url": "https://github.com/yanzhenjie/Kalle",
        },
    ],
    "message": "Succeed."
}
```

请求的代码如下：
```java
Kalle.get("http://www.example.com")
    .perform(new SimpleCallback<List<UserInfo>>() {
        @Override
        public void onResponse(SimpleResponse<List<UserInfo>> response) {
            if(response.isSucceed())) { // Http成功，业务也成功。
                List<UserInfo> userList = response.succeed();
                ...
            } else {
                Toast.show(response.failed());
            }
        }
    });
```

### 请求null数据
JSON数据如下：
```json
{
    "code": 1,
    "data": null,
    "message": "Succeed"
}
```

请求的代码如下：
```java
Kalle.get("http://www.example.com")
    .perform(new SimpleCallback<String>() {
        @Override
        public void onResponse(SimpleResponse<String> response) {
            if(response.isSucceed())) { // Http成功，业务也成功。
                ...
            } else {
                Toast.show(response.failed());
            }
        }
    });
```

### 请求String或者Integer数据
例如返回的业务数据是字符串或者数字时：
```json
{
    "code": 1,
    "data": "20180101",
    "message": "Succeed"
}
```

请求的代码如下：
```java
Kalle.get("http://www.example.com")
    .perform(new SimpleCallback<String>() {
        @Override
        public void onResponse(SimpleResponse<String> response) {
            if(response.isSucceed())) { // Http成功，业务也成功。
                String orderId = response.succeed();
            } else {
                Toast.show(response.failed());
            }
        }
    });
```

```java
Kalle.get("http://www.example.com")
    .perform(new SimpleCallback<Integer>() {
        @Override
        public void onResponse(SimpleResponse<Integer> response) {
            if(response.isSucceed())) { // Http成功，业务也成功。
                int age = response.succeed();
            } else {
                Toast.show(response.failed());
            }
        }
    });
```

## 封装Dialog和处理异常
上面已经封装了业务数据的处理，但是还有Dialog和异常没处理。在Kalle中这些都通过`Callback`来做，`Callback`的`onStart()`方法和`onEnd()`方法可以用来显示和关闭Dialog，`onException()`方法用来处理异常。

**异常**是指本地网络错误、Url解析失败、连接服务器超时和读取响应超时等一系列Kalle无法直接处理的异常，这些错误也必须由开发者来处理，因为这属于业务的范畴。

需要自定义一个`Callback`，用来替换上面例子中使用的`SimpleCallback`：
```java
public abstract class DefineCallback<S> extends Callback<S, String> {

    private Dialog mDialog;

    public DefineCallback(Context context) {
        mDialog = new WaitDialog(context);
    }

    @Override
    public Type getSucceed() {
        // 通过反射获取业务成功的数据类型。
        Type superClass = getClass().getGenericSuperclass();
        return ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    @Override
    public Type getFailed() {
        // 返回失败时的数据类型，String。
        return String.class;
    }

    @Override
    public void onStart() {
        // 请求开始啦，显示Dialog。
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    public void onException(Exception e) {
        // 发生异常了，回调到onResonse()中。
        String message;
        if (e instanceof NetworkError) {
            message = "网络不可用";
        } else if (e instanceof URLError) {
            message = "Url格式错误";
        } else if (e instanceof HostError) {
            message = "没有找到Url指定服务器";
        } else if (e instanceof ConnectTimeoutError) {
            message = "连接服务器超时，请重试";
        } else if (e instanceof WriteException) {
            message = "发送数据错误，请检查网络";
        } else if (e instanceof ReadTimeoutError) {
            message = "读取服务器数据超时，请检查网络";
        } else {
            message = "发生未知异常，请稍后重试";
        }

        SimpleResponse<S, String> response = SimpleResponse.<S, String>newBuilder()
                .failed(message)
                .build();
        onResponse(response);
    }

    @Override
    public void onCancel() {
        // 请求被取消了，如果开发者需要，请自行处理。
    }

    @Override
    public void onEnd() {
        // 请求结束啦，关闭Dialog。
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
```

这里例举了一部分常见的业务场景，开发者可根据自身需求扩展。