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

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.yanzhenjie.kalle.Response;
import com.yanzhenjie.kalle.sample.R;
import com.yanzhenjie.kalle.sample.util.Logger;
import com.yanzhenjie.kalle.simple.Converter;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import java.lang.reflect.Type;

/**
 * Created by Zhenjie Yan on 2018/3/26.
 */
public class JsonConverter implements Converter {

    private Context mContext;

    public JsonConverter(Context context) {
        this.mContext = context;
    }

    @Override
    public <S, F> SimpleResponse<S, F> convert(Type succeed, Type failed, Response response, boolean fromCache)
        throws Exception {
        S succeedData = null; // The data when the business successful.
        F failedData = null; // The data when the business failed.

        int code = response.code();
        String serverJson = response.body().string();
        Logger.i("Server Data: " + serverJson);
        if (code >= 200 && code < 300) { // Http is successful.
            HttpEntity httpEntity;
            try {
                httpEntity = JSON.parseObject(serverJson, HttpEntity.class);
            } catch (Exception e) {
                httpEntity = new HttpEntity();
                httpEntity.setSucceed(false);
                httpEntity.setMessage(mContext.getString(R.string.http_server_data_format_error));
            }

            if (httpEntity.isSucceed()) { // The server successfully processed the business.
                try {
                    succeedData = JSON.parseObject(httpEntity.getData(), succeed);
                } catch (Exception e) {
                    failedData = (F)mContext.getString(R.string.http_server_data_format_error);
                }
            } else {
                // The server failed to read the wrong information.
                failedData = (F)httpEntity.getMessage();
            }

        } else if (code >= 400 && code < 500) {
            failedData = (F)mContext.getString(R.string.http_unknow_error);
        } else if (code >= 500) {
            failedData = (F)mContext.getString(R.string.http_server_error);
        }

        return SimpleResponse.<S, F>newBuilder().code(response.code())
            .headers(response.headers())
            .fromCache(fromCache)
            .succeed(succeedData)
            .failed(failedData)
            .build();
    }
}