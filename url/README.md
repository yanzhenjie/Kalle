# Url

在Kalle中存在一个`Url`类，`Url`在Kalle中是一个有趣且非常有用的存在，它以类的形式表达`url`字符串，通俗的说就是它会把开发者指定的`url`以特定的格式拆分开，然后以属性的方式记录。

其实`url`是有规则的，下面用一个图说明：
![url](../images/url.svg)

`Url`类在实际开发中没有太多用法，因为它被大量使用在Kalle内部，有些类中也有面向开发者的Api。

## 构建Url
如果开发者需要构建`Url`：
```java
Url url = Url.newBuilder("http://www.example.com")
    .build();
```

这是Kalle中提供的一些示例，供开发者理解Kalle中的`Url`：
```java
@RunWith(AndroidJUnit4.class)
public class TestUrl {

    @Test
    public void testBase() throws Exception {
        Url url = Url.newBuilder("http://www.example.com")
                .setScheme("https")
                .setHost("github.com")
                .setPort(8080)
                .build();
        Assert.assertEquals(url.toString(), "https://github.com:8080");
    }

    @Test
    public void testAddPath() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/")
                .addPath("abc")
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/user/abc");
    }

    @Test
    public void testSetPath() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/album/photo")
                .setPath("/account/name")
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/account/name");
    }

    @Test
    public void testClearPath() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/album/photo")
                .clearPath()
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com");
    }

    @Test
    public void testAddQuery() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/?name=abc")
                .addQuery("age", 18)
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/user?name=abc&age=18");
    }

    @Test
    public void testSetQuery() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/?name=abc")
                .setQuery("name=xyz&sex=0")
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/user?name=xyz&sex=0");
    }

    @Test
    public void testSetQuery2() throws Exception {
        Params params = Params.newBuilder()
                .add("name", "mln")
                .add("height", 170)
                .build();
        Url url = Url.newBuilder("http://www.example.com/user?name=abc")
                .setQuery(params)
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/user?name=mln&height=170");
    }

    @Test
    public void testClearQuery() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user?name=abc&sex=1")
                .clearQuery()
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/user");
    }

    @Test
    public void testLocation() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/album/photo?name=abc").build();
        Url newUrl = url.location("/account/name?age=18");
        Assert.assertEquals(newUrl.toString(), "http://www.example.com/account/name?age=18");
    }

    @Test
    public void testRelativeLocation() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user?name=abc").build();
        Url newUrl = url.location("abc");
        Assert.assertEquals(newUrl.toString(), "http://www.example.com/user/abc");
    }

    @Test
    public void testMatchLocation() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/album/photo?name=abc").build();
        Url newUrl = url.location("../../get?name=mln");
        Assert.assertEquals(newUrl.toString(), "http://www.example.com/get?name=mln");
    }
}
```