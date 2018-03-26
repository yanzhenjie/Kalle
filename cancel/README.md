# 取消
Kalle中每一个异步请求都是可以取消的，无论在请求已经发起，还是请求已经在执行。

```java
Canceller canceller = Kalle.get("http://www.example.com")
    .perform(UserInfo.class, new SimpleCallback<UserInfo>() {
        @Override
        public void onResponse(SimpleResponse<UserInfo> response) {
        	// 请求响应了。
        	UserInfo user = response.result();
        }
    });
```
`Canceler`是一个接口，内部已经实现，开发者不需要关心。  

判断当前请求是否已经取消：
```java
if(canceller.isCancelled()) {
    // 这个请求是被取消的。
}
```

取消请求：
```java
canceller.cancel();
```

## 批量取消
很多时候我们不会对单个请求进行取消，而是在页面退出的时候取消这个页面发起的所有请求以释放资源。  

在Kalle中，批量取消是通过Tag实现的，内部通过对比Tag的一致性进行取消操作，因此开发者必须要给`Request`设置Tag：
```java
Object tag = ...;

Kalle.get("http://www.example.com")
    .tag(tag)
    .perform(new SimpleCallback<UserInfo>() {
        @Override
        public void onResponse(SimpleResponse<UserInfo> response) {
            UserInfo user = response.result();
        }
    });
```

当我们需要批量取消的时候：
```java
private Object cancelTag = ...;

private void request() {
    Object tag = cancelTag;
    Kalle.get...
}

@override
public void onDestroy() {
    super.onDestroy();
    // 批量取消：
    Kalle.cancelRequest(cancelTag);
    // 或者：
    RequestManager.getInstance().cancel(cancelTag);
}
```

内部对比Tag一致性的的原理：
```
public void cancel(Object tag) {
    Object newTag = mRequest.tag();
    if (tag == newTag || (tag != null && newTag != null && tag.equals(newTag))) {
        mCanceller.cancel();
    }
}
```