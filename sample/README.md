# 示例

正式开始之前的说明，Http请求从请求方法上可以分为两大类，我们把它们称为Url类请求（[UrlRequest](../request)）和Body类请求（[BodyRequest](../request)），因为一类只可以是简单的`url`，而另一类不仅仅可以是简单的`url`，也可以使用流来发送自定义`RequestBody`。

Url类的请求方法：
```
GET, HEAD, OPTIONS, TRACE
```

Body类的请求方法：
```
POST, PUT, DELETE, PATCH
```

在示例中，Url类的请求我们以`GET`为代表，Body类的请求我们以`POST`为代表。

## 对Callback的说明
这里我们先以`GET`请求方法为例，我们请求一个`UserInfo`，先写一个完整的请求。
```java
Kalle.get("http://www.example.com")
    .perform(UserInfo.class, new Callback<UserInfo>() {
        @Override
        public void onStart() {
            // 请求开始了，可以显示个dialog。
        }

        @Override
        public void onResponse(SimpleResponse<UserInfo> response) {
        	// 请求响应了。
        	UserInfo user = response.result();
        }

        @Override
        public void onException(Exception e) {
        	// 请求发生异常了。
        }

        @Override
        public void onCancel() {
        	// 请求被取消了。
        }

        @Override
        public void onEnd() {
        	// 请求结束了，可以关闭之前显示的dialog。
        }
    });
```

* 如果我们把这个请求简化或者封装一下，其实我们只需要实现`onResponse()`方法即可。
* 只要回调了`onStart()`，就肯定会回调`onEnd()`。
* `onCancel()`可能随时被回调，因为不确定开发者在什么时候取消请求。
* `onException()`是客户端环境发生异常时回调，比如超时、网络错误、发送数据失败。
* `onResponse()`只要服务器有响应就会回调，不论响应码是100、200、300、400、500段中的任何一个。

### 简化
乍一看上面的回调方法有些多，有时候我们只想简单的测试一下的时候就可以这样做：
```java
Kalle.get("http://www.example.com")
    .perform(UserInfo.class, new SimpleCallback<UserInfo>() {
        @Override
        public void onResponse(SimpleResponse<UserInfo> response) {
        	// 请求响应了。
        	UserInfo user = response.result();
        }
    });
```

我们可以看到上面的`Callback`在这里变为`SimpleCallback`了，`SimpleCallback`是`Callback`的一个实现类，所以我们只需要重写`onResponse()`方法就可以，当然这样做的弊端就是不能处理异常了，不过这不是问题，因为我们可以封装统一回调一个方法，后面会讲到。

让有些有些开发者不能接受的是，想请求一个`JavaBean`还需要传一个`JavaBean.class`的参数进去，那么我们可以这样写：
```java
Kalle.get("http://www.example.com")
    .perform(new SimpleCallback<UserInfo>() {
        @Override
        public void onResponse(SimpleResponse<UserInfo> response) {
        	// 请求响应了。
        	UserInfo user = response.result();
        }
    });
```
这样就做到最简化了，不过我们必须使用`new SimpleCallback<UserInfo>()`，如果开发者想把这个请求代码封装起来，通过外部传入泛型来解析时就不好使了，可以通过传入明确的`UserInfo.class`来解析，传入`SimpleCallback`的子类也可以。

## Url中的PATH
很多开发者的`url`中的`path`段会带有需要`encode`的字符（例如中文），在Kalle中开发者不需要关注自己的`path`中是否带有需要`encode`的字符，例如这样的`url`是完全没问题的：
```java
http://www.example.com/示例/演示.apk
```

值得一提的是，Kalle中支持开发者拼接`path`，例如有些按照RESTFUL风格设计的服务器接口，如果我们要读取用户信息可能是这样：
```
http://www.example.com/{userId}/info
```
这样的情况并不少见，在Kalle中我们可以这样写：
```java
String userId = ...;

Kalle.get("http://www.example.com")
    .path(userId)
    .path("info")
    .perform(new SimpleCallback<UserInfo>() {
        @Override
        public void onResponse(SimpleResponse<UserInfo> response) {
        	// 请求响应了。
        	UserInfo = response.result();
        }
    });
```

## 简单的GET请求
```java
Kalle.get("http://www.example.com")
    .header("version", 123) // 添加请求头。
    .setHeader("name", "kalle") // 设置请求头，会覆盖默认头和之前添加的头。
    .param("name", "kalle") // 添加请求参数。
    .perform(...);
```

这里还有一些其它属性可以设置，比如超时时间，代理服务器，SSL证书，域名信任器等等，开发者可以自行探索。  

另外值得注意的是，这里添加的参数最终会拼接到`url`上发送，因为`GET`请求属于我们之前说过的Url类请求，这样的行为是Http协议规定的。

## 简单的POST请求
```java
Kalle.post("http://www.example.com")
    .header("version", 123) // 添加请求头。
    .setHeader("name", "kalle") // 设置请求头，会覆盖默认头和之前添加的头。
    .param("name", "kalle") // 添加请求参数。
    .perform(...);
```

其它的通用设置跟`GET`请求是一样的，这里不再赘述。

## Body类型请求添加参数的两种情况
我们知道Body类型的请求既可以通过`RequestBody`发送参数，也可以通过`url`发送参数（以何种方式发送参数取决与服务端与客户端的约定），Kalle同时支持这两种方式。

例如服务端给一个接口：`http://www.example.com/user?id={userId}&name={username}`，需要我们使用`POST`方法请求，并且需要在`RequestBody`中发送`age`和`sex`的参数，那么我们可以这样做：
```java
String userId = ...;
String userName = ...;
int userAge = ...;
int userSex = ...;

Kalle.post("http://www.example.com/user")
    .urlParam("id", userId)
    .urlParam("name", userName)
    .param("age", userAge)
    .param("sex", userSex)
    .perform(...);
```

服务端的另一个接口：`http://www.example.com/user?id={userId}`，需要我们使用`POST`方法请求，并且需要在`RequestBody`中`push`一段json：
```java
String json = ...;

Kalle.post("http://www.example.com/user")
    .urlParam("id", userId)
    .body(new JsonBody(json))
    .perform(...);
```

更多关于`RequestBody`的使用请继续往下看。

## 表单提交文件
表单上传文件是Http中上传文件最常见的一种，几乎90%的上传文件都以form方式上传的。在Kalle中，在Body类型的请求中，只要添加了`File`或者`Binary`参数，会自动以表单的形式发送请求，写法有如下几种：

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

## 提交自定义Body
只要是Body类型的请求都可以提交自定义`RequestBody`，这是一个示例：
```java
RequestBody body = ...;

Kalle.post("http://www.example.com")
    .body(body)
    .perform(...);
```

我们看到的`RequestBody`是一个接口，在Kalle中已经提供了几种默认实现：
```
FileBody    // 用Body发送文件。
StringBody  // 用Body发送字符串。
JsonBody    // 用Body发送Json字符串。
XmlBody     // 用Body发送Xml字符串。
FormBody    // 用Body模拟发送表单。
UriBody     // 用Body发送Url参数。
```

例如用`RequestBody`发送一个文件：
```java
File file = ...;
RequestBody body = new FileBody(file);

Kalle.post("http://www.example.com")
    .body(body)
    .perform(...);
```

例如用`RequestBody`发送一段字符串：
```java
RequestBody body = new StringBody("I like you.");

Kalle.post("http://www.example.com")
    .body(body)
    .perform(...);
```

发送`Json`和`Xml`的同发送`String`一样：
```java
String json = ...;

RequestBody body = new JsonBody(json);

Kalle.post("http://www.example.com")
    .body(body)
    .perform(...);
```

```java
String xml = ...;

RequestBody body = new XmlBody(xml);

Kalle.post("http://www.example.com")
    .body(body)
    .perform(...);
```

`UrlBody`是在Body类型的请求没有添加`File`和`Binary`时内部自动转换使用的，一般开发者不会使用到，如果开发者想用也是可以的：
```java
UrlBody body = UrlBody.newBuilder()
    .param("name", "Kalle")
    .param("age", 18)
    .param("sex", 1)
    .build();

Kalle.post("http://www.example.com")
    .body(body)
    .perform(...);
```

`FormBody`是在需要以表单实行发送参数时使用的。前面有说到过，Body类型的请求，只要添加了`File`或者`Binary`就会自动转化为表单的形式提交也是使用的`FormBody`。要特别说明的是，有些开发者需要在没有文件的时候也使用表达发送参数，那么我们可以这样做：
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