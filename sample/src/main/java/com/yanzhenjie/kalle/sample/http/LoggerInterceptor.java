package com.yanzhenjie.kalle.sample.http;

import android.text.TextUtils;
import android.util.Log;

import com.yanzhenjie.kalle.Headers;
import com.yanzhenjie.kalle.Request;
import com.yanzhenjie.kalle.RequestBody;
import com.yanzhenjie.kalle.Response;
import com.yanzhenjie.kalle.StringBody;
import com.yanzhenjie.kalle.UrlBody;
import com.yanzhenjie.kalle.connect.Interceptor;
import com.yanzhenjie.kalle.connect.http.Chain;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ZuoHailong on 2019/12/31.
 */
public class LoggerInterceptor implements Interceptor {

    private final String mTag;
    private final boolean isEnable;

    public LoggerInterceptor(String tag, boolean isEnable) {
        this.mTag = tag;
        this.isEnable = isEnable;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (isEnable) {
            Response response = chain.proceed(request);

            String url = request.url().toString();

            StringBuilder log = new StringBuilder(String.format(" \nPrint Request: %1$s.", url));
            log.append(String.format("\nMethod: %1$s.", request.method().name()));

            Headers toHeaders = request.headers();
            for (Map.Entry<String, List<String>> entry : toHeaders.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                log.append(String.format("\n%1$s: %2$s.", key, TextUtils.join(";", values)));
            }

            if (request.method().allowBody()) {
                RequestBody body = request.body();
                if (body instanceof StringBody || body instanceof UrlBody) {
                    log.append(String.format(" \nRequest Body: %1$s.", body.toString()));
                }
            }

            log.append(String.format(" \nPrint Response: %1$s.", url));
            log.append(String.format(Locale.getDefault(), "\nCode: %1$d", response.code()));

            Headers fromHeaders = response.headers();
            for (Map.Entry<String, List<String>> entry : fromHeaders.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                log.append(String.format("\n%1$s: %2$s.", key, TextUtils.join(";", values)));
            }
            // 打印 response.body
            log.append(String.format(" \nResponse Body: %1$s.", response.body().string()));
            Log.i(mTag, log.toString());
            return response;
        }
        return chain.proceed(request);
    }

}