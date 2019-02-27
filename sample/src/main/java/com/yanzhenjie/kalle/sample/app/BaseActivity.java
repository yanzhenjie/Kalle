/*
 * Copyright 2018 Zhenjie Yan.
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
package com.yanzhenjie.kalle.sample.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.sample.mvp.Bye;
import com.yanzhenjie.kalle.sample.util.DisplayUtils;
import com.yanzhenjie.kalle.sample.util.Logger;

/**
 * Created by Zhenjie Yan on 2018/3/27.
 */
public abstract class BaseActivity extends AppCompatActivity implements Bye {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.initScreen(this);
        Logger.e("onCreate() " + getClass().getName());
    }

    @Override
    public void bye() {
        finish();
    }

    @Override
    protected void onDestroy() {
        Logger.e("onDestroy() " + getClass().getName());
        Kalle.cancel(this);
        super.onDestroy();
    }
}