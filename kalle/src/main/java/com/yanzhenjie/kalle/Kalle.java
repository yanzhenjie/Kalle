/*
 * Copyright © 2018 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.kalle;

import android.util.Log;

import com.yanzhenjie.kalle.download.BodyDownload;
import com.yanzhenjie.kalle.download.DownloadManager;
import com.yanzhenjie.kalle.download.UrlDownload;
import com.yanzhenjie.kalle.simple.RequestManager;
import com.yanzhenjie.kalle.simple.SimpleBodyRequest;
import com.yanzhenjie.kalle.simple.SimpleUrlRequest;

/**
 * <p>
 * Kalle.
 * </p>
 * Created in Jul 28, 2015 7:32:22 PM.
 */
public final class Kalle {

    private static KalleConfig sConfig;

    public static void setConfig(KalleConfig config) {
        if (sConfig == null) sConfig = config;
        else {
            Log.w("Kalle", new IllegalStateException("Illegal operation, only allowed to configure once."));
        }
    }

    public static KalleConfig getConfig() {
        if (sConfig == null) {
            synchronized (KalleConfig.class) {
                if (sConfig == null) {
                    sConfig = KalleConfig.newBuilder().build();
                }
            }
        }
        return sConfig;
    }

    public static SimpleUrlRequest.Api get(String url) {
        return SimpleUrlRequest.newApi(Url.newBuilder(url), RequestMethod.GET);
    }

    public static SimpleUrlRequest.Api head(String url) {
        return SimpleUrlRequest.newApi(Url.newBuilder(url), RequestMethod.HEAD);
    }

    public static SimpleUrlRequest.Api options(String url) {
        return SimpleUrlRequest.newApi(Url.newBuilder(url), RequestMethod.OPTIONS);
    }

    public static SimpleUrlRequest.Api trace(String url) {
        return SimpleUrlRequest.newApi(Url.newBuilder(url), RequestMethod.TRACE);
    }

    public static SimpleBodyRequest.Api post(String url) {
        return SimpleBodyRequest.newApi(Url.newBuilder(url), RequestMethod.POST);
    }

    public static SimpleBodyRequest.Api put(String url) {
        return SimpleBodyRequest.newApi(Url.newBuilder(url), RequestMethod.PUT);
    }

    public static SimpleBodyRequest.Api patch(String url) {
        return SimpleBodyRequest.newApi(Url.newBuilder(url), RequestMethod.PATCH);
    }

    public static SimpleBodyRequest.Api delete(String url) {
        return SimpleBodyRequest.newApi(Url.newBuilder(url), RequestMethod.DELETE);
    }

    public static void cancel(Object tag) {
        RequestManager.getInstance().cancel(tag);
    }

    public static class Download {

        public static UrlDownload.Api get(String url) {
            return UrlDownload.newApi(Url.newBuilder(url), RequestMethod.GET);
        }

        public static UrlDownload.Api head(String url) {
            return UrlDownload.newApi(Url.newBuilder(url), RequestMethod.HEAD);
        }

        public static UrlDownload.Api options(String url) {
            return UrlDownload.newApi(Url.newBuilder(url), RequestMethod.OPTIONS);
        }

        public static UrlDownload.Api trace(String url) {
            return UrlDownload.newApi(Url.newBuilder(url), RequestMethod.TRACE);
        }

        public static BodyDownload.Api post(String url) {
            return BodyDownload.newApi(Url.newBuilder(url), RequestMethod.POST);
        }

        public static BodyDownload.Api put(String url) {
            return BodyDownload.newApi(Url.newBuilder(url), RequestMethod.PUT);
        }

        public static BodyDownload.Api patch(String url) {
            return BodyDownload.newApi(Url.newBuilder(url), RequestMethod.PATCH);
        }

        public static BodyDownload.Api delete(String url) {
            return BodyDownload.newApi(Url.newBuilder(url), RequestMethod.DELETE);
        }

        public static void cancel(Object tag) {
            DownloadManager.getInstance().cancel(tag);
        }
    }

    private Kalle() {
    }
}
