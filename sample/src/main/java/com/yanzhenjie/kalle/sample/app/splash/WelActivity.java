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
package com.yanzhenjie.kalle.sample.app.splash;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.sample.R;
import com.yanzhenjie.kalle.sample.app.main.MainPresenter;
import com.yanzhenjie.kalle.sample.config.AppConfig;
import com.yanzhenjie.kalle.sample.http.SimpleCallback;
import com.yanzhenjie.kalle.sample.util.Delivery;
import com.yanzhenjie.kalle.simple.SimpleResponse;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

import static com.yanzhenjie.kalle.sample.config.UrlConfig.LOGIN;

/**
 * Created by Zhenjie Yan on 2018/3/1.
 */
public class WelActivity extends Activity {

    private static final int REQUEST_CODE_SETTING_PERMISSION = 1;

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
        AndPermission.with(this).runtime().permission(Permission.Group.STORAGE).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> list) {
                new AlertDialog.Builder(WelActivity.this).setCancelable(false)
                    .setTitle(R.string.tip)
                    .setMessage(R.string.wel_permission_failed)
                    .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            permissionSetting();
                        }
                    })
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
            }
        }).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> list) {
                AppConfig.get().initFileDir();
                tryLogin();
            }
        }).start();
    }

    /**
     * Permission setting.
     */
    private void permissionSetting() {
        AndPermission.with(this).runtime().setting().start(REQUEST_CODE_SETTING_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING_PERMISSION: {
                if (AndPermission.hasPermissions(this, Permission.Group.STORAGE)) {
                    tryLogin();
                } else {
                    finish();
                }
                break;
            }
        }
    }

    /**
     * Try login.
     */
    private void tryLogin() {
        Kalle.post(LOGIN).param("name", 123).param("password", 456).tag(this).perform(new SimpleCallback<String>(this) {
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