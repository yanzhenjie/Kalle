# 下载
下载其实也是普通Http请求，只是它的响应体比我们常说的接口响应体大，因此下载的所有Api和普通Http相同，只是没有缓存模式。但是它也有自己的特性，比如下载进度的监听，如果目标文件已经在本地存在需要怎样处理，是否支持断点续传等。

这是一个最简单的例子：
```java
Kalle.Download.get(url)
    .directory("/sdcard")
    .fileName("kalle.apk")
    .perform(new Callback() {
        @Override
        public void onStart() {
            // 请求开始了。
        }

        @Override
        public void onFinish(String path) {
            // 请求完成，文件路径：path。
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
        	// 请求结束了。
        }
    });
```

* `directory(String)`  
  用来指定保存文件的目录，必须要指定保存目录，否则会回调失败。
* `fileName(String)`  
  用来指定文件名，如果开发者未指定文件名，则会尝试获取服务器指定的文件名，首先尝试从`Content-Disposition`头中获取，如果获取不到则使用`url`的path中获取最后一个`/`后的字符串作为文件名。例如`http://www.example.com/files/abc.apk`，那么从`url`中获取到的文件名就是`abc.apk`。

## 进度和网速
```java
Kalle.Download.get(url)
    .directory("/sdcard")
    .fileName("kalle.apk")
    .onProgress(new Download.ProgressBar() {
        @Override
        public void onProgress(int progress, long byteCount, long speed) {
            // progress：进度，[0, 100]。
            // byteCount: 目前已经下载的byte大小。
            // speed：此时每秒下载的byte大小。
        }
    })
    .perform(...);
```

**注意**：计算进度必须要先知道要下载的文件的大小，用当前已经下载的`byte`数量除以要下载的总大小就是进度。这个大小是从响应头中得到的，服务器通过`Content-Length`或者`Content-Range`来告诉客户端本次会输出的包体大小，**但是**有些服务器不会返回这两个头，所以会无法计算下载进度。这种情况下Kalle返回的进度会是0，如果有开发者遇到回调的进度是0的时候，请自行排查服务器是否返回了`Content-Length`或者`Content-Range`。

## 下载策略
往往我们指定的目录中已经存在目标文件了，例如：
```java
Kalle.Download.get(url)
    .directory("/sdcard")
    .fileName("kalle.apk")
    ...
```

此时我们需要选择删除旧文件重新下载，或者使用旧文件。但是真实的业务往往比我们想想的更加复杂，是否直接删除文件可能需要结合请求响应码，或许还需要备份旧文件，重新下载新文件等等。

Kalle提供了下载策略来支持此类业务，同时包括断点续传也需要通过下载策略来配置：
```java
interface Policy {

    boolean isRange();

    boolean allowDownload(int code, Headers headers);

    boolean oldAvailable(String path, int code, Headers headers);
}
```

### isRange()
`isRange()`是让开发者选择是否使用断点续传，断点续传需要服务器的支持。例如客户端下载一个100M的文件，下载到50M时客户端突然中断下载（例如用户取消、设备突然断电等），下一次继续下载此文件时，客户端可以要求服务器从50M的地方开始传输，客户端也从50M处续写入之前保存的临时文件，这样会省下50M流量和一定的时间，比如文件下载的暂停功能就是这个原理。如果开发者使用了断点续传功能，即使服务器不支持断点续传，Kalle也会自动从0M处开始下载。

### allowDownload()
`allowDownload()`是让开发者插入业务，当前响应码和响应头是否应该下载文件。如果返回`true`则会继续下载文件，返回`false`则会回调失败，返回`DownloadException`，开发者可以从`DownloadException`获取到响应码和响应头，方便开展后续的业务。

`DownloadException`用法请参考[异常](../error)。

### oldAvailable()
`oldAvailable()`是让开发者处理旧文件和相应的业务。返回`true`表示旧文件可用，Kalle不会重新下载，会直接返回旧文件路径给开发者；返回`false`表示旧文件不可用，Kalle会尝试删除旧文件，然后重新下载新文件。

`path`是旧文件的本地地址，既开发者指定的目录和文件名对应的文件，只有这个旧文件存在时才会回调这个方法。因为开发者指定保存要下载文件的目标位置处，已经存在一个同名文件了，Kalle不知道如何处理它。一般情况下，我们会做文件有效性校验，例如验证MD5或者验证响应码，因为验证过程属于业务，所以Kalle把这个过程交给开发者做校验。

如果开发者想重新下载文件，但是也不想删除旧文件，在这里开发者可以备份这个旧文件，Kalle尝试删除时是删除开发者指定保存要下载文件的目标位置处的文件。

### 默认下载策略
默认下载策略适用于大多数开发者，默认使用断点续传，默认允许任何响应码时下载文件，默认旧文件不可用。
```
public boolean isRange() {
    return true;
}

public boolean allowDownload(int code, Headers headers) {
    return true;
}

public boolean oldAvailable(String path, int code, Headers headers) {
    return false;
}
```