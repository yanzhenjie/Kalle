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
package com.yanzhenjie.kalle.connect.http;

import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.Request;
import com.yanzhenjie.kalle.Response;
import com.yanzhenjie.kalle.connect.Interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YanZhenjie on 2018/2/24.
 */
public class Call {

    private final Request mRequest;

    public Call(Request request) {
        this.mRequest = request;
    }

    /**
     * Execute request.
     */
    public Response execute() throws IOException {
        List<Interceptor> interceptors = new ArrayList<>(Kalle.getConfig().getInterceptor());
        interceptors.add(new ConnectInterceptor());

        Chain chain = new AppChain(interceptors, 0, mRequest, this);
        return chain.proceed(mRequest);
    }
}