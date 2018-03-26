# 缓存

`CacheStore`用来增删改差客户端的Httpi缓存数据，如果开发者不配置，那么Kalle将不会操作Cache（包括增删改差），因此如果开发者请求的网站启用了缓存，将会无效，开发者自己设置的`CacheMode`也会没有缓存效果。

Kalle默认提供了一个基于磁盘的缓存类`DiskCacheStore`，这个类只需要开发者指定缓存的文件夹和缓存数据的加密密钥即可使用：
```java
CacheStore cacheStore = DiskCacheStore.newBuilder("/sdcard")
    .password("这是密钥")
    .build()

KalleConfig.newBuilder()
    ...
    .cacheStore(cacheStore)
    .build()
```

## 自定义实现
如果开发者需要自行实现缓存，那么也很简单，`CacheStore`是一个接口，只要开发者实现这个接口即可，数据也是以`key-value`的形式保存的：
```java
public interface CacheStore {
    /**
     * Get the cache.
     *
     * @param key unique key.
     * @return cache.
     */
    Cache get(String key);

    /**
     * Save or set the cache.
     *
     * @param key   unique key.
     * @param cache cache.
     * @return cache.
     */
    boolean replace(String key, Cache cache);

    /**
     * Remove cache.
     *
     * @param key unique.
     * @return cache.
     */
    boolean remove(String key);

    /**
     * Clear all data.
     *
     * @return returns true if successful, false otherwise.
     */
    boolean clear();
}
```