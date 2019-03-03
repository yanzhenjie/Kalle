/*
 * Copyright © 2018 Zhenjie Yan.
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
package com.yanzhenjie.kalle.sample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.KalleConfig;
import com.yanzhenjie.kalle.OkHttpConnectFactory;
import com.yanzhenjie.kalle.connect.BroadcastNetwork;
import com.yanzhenjie.kalle.connect.http.LoggerInterceptor;
import com.yanzhenjie.kalle.cookie.DBCookieStore;
import com.yanzhenjie.kalle.sample.config.AppConfig;
import com.yanzhenjie.kalle.sample.http.JsonConverter;
import com.yanzhenjie.kalle.sample.http.LoginInterceptor;
import com.yanzhenjie.kalle.sample.util.Logger;
import com.yanzhenjie.kalle.sample.util.MediaLoader;
import com.yanzhenjie.kalle.simple.cache.DiskCacheStore;

/**
 * Created by Zhenjie Yan on 2018/3/27.
 */
public class App extends Application {

    private static App _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        if (_instance == null) {
            LeakCanary.install(this);
            _instance = this;

            Logger.setEnable(BuildConfig.DEBUG);
            kalle();
            album();
        }
    }

    private void kalle() {
        Kalle.setConfig(KalleConfig.newBuilder()
            .connectFactory(OkHttpConnectFactory.newBuilder().build())
            .cookieStore(DBCookieStore.newBuilder(this).build())
            .cacheStore(DiskCacheStore.newBuilder(AppConfig.get().PATH_APP_CACHE).build())
            .network(new BroadcastNetwork(this))
            .addInterceptor(new LoginInterceptor())
            .addInterceptor(new LoggerInterceptor("KalleSample", BuildConfig.DEBUG))
            .converter(new JsonConverter(this))
            .build());
    }

    private void album() {
        Album.initialize(AlbumConfig.newBuilder(this).setAlbumLoader(new MediaLoader()).build());
    }

    public static App get() {
        return _instance;
    }

}
