# CookieStore

`CookieStore`用来增删改差App使用Kalle时的Cookie，如果开发者不配置，那么Kalle将不会操作Cookie（包括增删改差），因此如果开发者请求的网站需要保存Cookie或者网站需要读取Cookie时，将会无效。  
Cookie的增删改差需要根据Http协议来做，要分析`path`、`expire`、`domain`等几个属性，对于一般的开发者来说相对复杂，这里不做更详细的解释，如果开发者感兴趣，可以自行搜索相关资料学习，或者查看Kalle提供的基于数据库实现的`DBCookieStore`，开发者如果需要使用Cookie只需要做个简单的配置即可：
```java
KalleConfig.newBuilder()
    ...
    .cookieStore(DBCookieStore.newBuilder(this).build())
    .build()
```

当然你也可以自己实现一个更高效的，因为`CookieStore`是一个接口：
```java
public interface CookieStore {
    /**
     * According to url loading cookies.
     *
     * @param uri uri.
     * @return all cookies that match the rules.
     */
    List<HttpCookie> get(URI uri);

    /**
     * Save cookie for the specified url.
     *
     * @param uri    uri.
     * @param cookie cookie.
     */
    void add(URI uri, HttpCookie cookie);

    /**
     * Remove the specified cookie.
     *
     * @param cookie cookie.
     */
    void remove(HttpCookie cookie);

    /**
     * Clear the cookie.
     */
    void clear();
}
```

## 一些扩展
一般情况下，对同一个服务端，在客户端第一次发起请求的时候，服务端为了维持会话有效性，会为这个会话建立Session，并把Session的ID转化为Cookie发送到客户端（它一定是一个临时Cookie，也就是客户端/浏览器此次关闭后即失效），客户端把这个Cookie保存起来，并在客户端/浏览器关闭之前的每一个请求中都带上这个Cookie（会做一些匹配，比如根据`path`、`domain`、`expire`）。除了SessionID这个Cookie，还有些Cookie是服务端自定义的一些Cookie，这里不做过多详解。

对于上述场景，转换到App中来，我们应该在用户登录成功后记录用户登录成功，并且保存用户的帐号和密码，在每一次App重新打开时，我们应该判断用户是是登录的，如果用户是登录的我们应该使用保存的用户帐号和密码调用一次登录接口来保证用户的登录状态。这么做的原因是，当某一次用户登录成功，服务端会把用户绑定到这个会话的Session上，并把SessionID转化为一个Cookie发送给客户端，等App关闭时这个Cookie在客户端就失效了，失效的Cookie在App重启后的请求中是不会被带上的（一般都已经删除了），所以服务端检测到客户端请求没有带上SessionID的Cookie时，服务端无法判断是哪个用户，它会认为用户在客户端是没登录的，所以它会认为这是一个新的会话，并新建一个Session，发送新的SessionID转化的Cookie到客户端。因此我们需要重新调一下登录接口，让服务端把我们之前保存的用户和它新生成的Session关联，这样用户就不用重新登录一遍了。

## 把Cookie同步到WebView
如果开发者通过WebView打开的页面也需要维持用户登录状态，那么这里会有一些参考资料。

对于原生WebView，重写`WebView#loadUrl(String url, Map<String, String> httpHeader)`方法：
```java
public class MyWebView extends WebView {

    private CookieManager mCookieManager;

    public MyWebView(Context context) {
        super(context);
        mCookieManager = new CookieManager(Kalle.getConfig().getCookieStore());
    }

    @Override
    public void loadUrl(String url, Map<String, String> httpHeader) {
        if (httpHeader == null) {
            httpHeader = new HashMap<>();
        }

        // 这里你还可以添加一些自定头。
        httpHeader.put("AppVersion", "1.0.0");

        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (uri != null) {
            List<String> cookies = mCookieManager.get(uri);

            android.webkit.CookieManager manager = android.webkit.CookieManager.getInstance();
            manager.setAcceptCookie(true);
            manager.setCookie(uri.getHost(), TextUtils.join("; ", cookies));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                manager.flush();
            } else {
                android.webkit.CookieSyncManager.createInstance(getContext()).sync();
            }
        }
        super.loadUrl(url, httpHeader);
    }
}
```