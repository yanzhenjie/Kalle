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
package com.yanzhenjie.kalle.sample.app.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.sample.App;
import com.yanzhenjie.kalle.sample.app.main.MainPresenter;
import com.yanzhenjie.kalle.sample.http.SimpleCallback;
import com.yanzhenjie.kalle.sample.util.Delivery;
import com.yanzhenjie.kalle.simple.SimpleResponse;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

import static com.yanzhenjie.kalle.sample.config.UrlConfig.LOGIN;

/**
 * Created by YanZhenjie on 2018/3/1.
 */
public class WelActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Delivery.getInstance().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPermission();
            }
        }, 1000);
    }

    private void requestPermission() {
        AndPermission.with(this)
                .permission(Permission.Group.STORAGE)
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> list) {
                        finish();
                    }
                })
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> list) {
                        App.get().initialize();
                        tryLogin();
                    }
                })
                .start();
    }

    /**
     * Try login.
     */
    private void tryLogin() {
        Kalle.post(LOGIN)
                .param("name", 123)
                .param("password", 456)
                .tag(this)
                .perform(new SimpleCallback<String>(this) {
                    @Override
                    public void onResponse(SimpleResponse<String, String> response) {
                        toLauncher();
                    }
                });
    }

    private void toLauncher() {
        startActivity(new Intent(this, MainPresenter.class));
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}