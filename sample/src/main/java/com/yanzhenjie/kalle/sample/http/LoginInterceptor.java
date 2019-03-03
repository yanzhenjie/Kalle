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
package com.yanzhenjie.kalle.sample.http;

import com.yanzhenjie.kalle.BodyRequest;
import com.yanzhenjie.kalle.Request;
import com.yanzhenjie.kalle.RequestMethod;
import com.yanzhenjie.kalle.Response;
import com.yanzhenjie.kalle.connect.Interceptor;
import com.yanzhenjie.kalle.connect.http.Call;
import com.yanzhenjie.kalle.connect.http.Chain;
import com.yanzhenjie.kalle.sample.config.UrlConfig;
import com.yanzhenjie.kalle.sample.util.Logger;
import com.yanzhenjie.kalle.util.IOUtils;

import java.io.IOException;

/**
 * Created by Zhenjie Yan on 2018/3/1.
 */
public class LoginInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originResponse = chain.proceed(request);
        if (originResponse.code() == 401) { // If not login, try login.
            Logger.w("Need login: " + request.url().toString());
            BodyRequest loginRequest = BodyRequest.newBuilder(UrlConfig.LOGIN, RequestMethod.POST)
                .param("name", 123)
                .param("password", 456)
                .build();
            Response loginResponse = new Call(loginRequest).execute();
            if (loginResponse.code() == 200) { // Login successfully.
                Logger.i("Re-Request: " + request.url().toString());

                // Login successfully, the original request to re-launch.
                IOUtils.closeQuietly(originResponse);
                IOUtils.closeQuietly(loginResponse);
                return chain.proceed(request); // Execute origin request.
            }
            IOUtils.closeQuietly(loginResponse); // Login failed, close it.
        }
        return originResponse;
    }
}