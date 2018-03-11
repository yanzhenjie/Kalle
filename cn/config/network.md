# 网络

在Kalle中，每一个请求都会做网络是否可用的检查，因为Kalle中是拿不到`Context`实例的，所以Kalle提供了一个接口来做网络检查：
```java
public interface Network {

    /**
     * Check the network is enable.
     */
    boolean isAvailable();
}
```

如果开发者不配置这个接口，Kalle默认网络是可用的。在网络不可用时发起`Socket`连接和读写数据时可能会抛出意想不到的异常，因为**强烈建议**开发者实现并配置这个接口。

## 一些建议
每次执行网络检查都会损耗性能，因此这里推荐使用缓存原理，在每次网络发生变化的时候执行一次网络检查，并记录网络是否可用，在这个接口的回调中返回检查结果即可。根据这个原理，Kalle已经提供了一个默认实现类：`BroadcastNetwork`，开发者只要在个性化配置的实现，传入这个类的实例即可：
```java
Kalle.setConfig(
    KalleConfig.newBuilder()
        ...
        .network(new BroadcastNetwork(this))
        .build()
);
```