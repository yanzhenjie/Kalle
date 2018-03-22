# 业务封装
在[示例](../sample)中演示了如何请求`JavaBean`，这里我们可以结合业务把请求结果再封装一下。

我们以JSON为例，这是一段常见的业务JSON：
```json
{
    "code": 1,
    "data": {
        "name": "Kalle",
        "url": "https://github.com/yanzhenjie/Kalle",
    },
    "message": "Succeed."
}
```
或者是这样的：
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
        "message": "Succeed."
    ]
}
```

其中`code`表示业务状态码，`message`表示业务错误后的服务端提示消息，`data`表示请求的实际数据，可能是`Entity`，也可能是`List<Entity>`。**需要特别注意**的是不要把`httpcode`和这里的`code`混合。当然也有把`httpcode`同时作为业务状态码的，但是原理相同。

## 未封装之前
在没有封装的情况下，我们写的`JavaBean`可能是这样子：
```java
public class UserInfo {
    String name;
    String url;
    ...
}

public class HttpEntity {
    int code;
    UserInfo data;
    String message;
    ...
}

// 或者

public class HttpEntity {
    int code;
    List<UserInfo> data;
    String message;
    ...
}
```

请求的代码是这样：
```java
Kalle.get("http://www.example.com")
    .perform(new SimpleCallback<HttpEntity>() {
        @Override
        public void onResponse(SimpleResponse<HttpEntity> response) {
            if(response == 200) {
        	    HttpEntity entity = response.result();
                if(entity.getCode() == 1) {
                    UserInfo user = entity.getData();
                    // 或者
                    List<UserInfo> userList = entity.getData();
                    ...
                } else {
                    toast(entity.getMessage());
                }
            } else {
                ...
            }
        }
    });
```

这样的写法相比先请求到`String`再做解析已经简单了不少了，不过经过封装我们可以直接请求到内部包装的`UserInfo`或者`List<UserInfo>`。

## 封装之后
