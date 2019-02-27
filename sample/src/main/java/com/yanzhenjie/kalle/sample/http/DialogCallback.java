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
package com.yanzhenjie.kalle.sample.http;

import android.app.Dialog;
import android.content.Context;

import com.yanzhenjie.kalle.exception.ConnectTimeoutError;
import com.yanzhenjie.kalle.exception.HostError;
import com.yanzhenjie.kalle.exception.NetworkError;
import com.yanzhenjie.kalle.exception.ParseError;
import com.yanzhenjie.kalle.exception.ReadTimeoutError;
import com.yanzhenjie.kalle.exception.URLError;
import com.yanzhenjie.kalle.exception.WriteException;
import com.yanzhenjie.kalle.sample.R;
import com.yanzhenjie.kalle.sample.util.Logger;
import com.yanzhenjie.kalle.simple.Callback;
import com.yanzhenjie.kalle.simple.SimpleResponse;
import com.yanzhenjie.loading.dialog.LoadingDialog;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Zhenjie Yan on 2018/3/26.
 */
public abstract class DialogCallback<S> extends Callback<S, String> {

    private Context mContext;
    private Dialog mDialog;

    public DialogCallback(Context context) {
        this.mContext = context;
        this.mDialog = new LoadingDialog(mContext);
    }

    @Override
    public Type getSucceed() {
        Type superClass = getClass().getGenericSuperclass();
        return ((ParameterizedType)superClass).getActualTypeArguments()[0];
    }

    @Override
    public Type getFailed() {
        return String.class;
    }

    @Override
    public void onStart() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    @Override
    public void onException(Exception e) {
        String message;
        if (e instanceof NetworkError) {
            message = mContext.getString(R.string.http_exception_network);
        } else if (e instanceof URLError) {
            message = mContext.getString(R.string.http_exception_url);
        } else if (e instanceof HostError) {
            message = mContext.getString(R.string.http_exception_host);
        } else if (e instanceof ConnectTimeoutError) {
            message = mContext.getString(R.string.http_exception_connect_timeout);
        } else if (e instanceof WriteException) {
            message = mContext.getString(R.string.http_exception_write);
        } else if (e instanceof ReadTimeoutError) {
            message = mContext.getString(R.string.http_exception_read_timeout);
        } else if (e instanceof ParseError) {
            message = mContext.getString(R.string.http_exception_parse_error);
        } else {
            message = mContext.getString(R.string.http_exception_unknow_error);
        }

        Logger.e(e);
        onResponse(SimpleResponse.<S, String>newBuilder().failed(message).build());
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onEnd() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}