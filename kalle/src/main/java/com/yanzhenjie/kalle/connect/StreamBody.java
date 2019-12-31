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
package com.yanzhenjie.kalle.connect;

import android.text.TextUtils;

import com.yanzhenjie.kalle.Headers;
import com.yanzhenjie.kalle.ResponseBody;
import com.yanzhenjie.kalle.util.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Zhenjie Yan on 2018/2/22.
 */
public class StreamBody implements ResponseBody {

    private String mContentType;
    private BufferedInputStream mStream;

    public StreamBody(String contentType, InputStream stream) {
        this.mContentType = contentType;
        this.mStream = new BufferedInputStream(stream);
    }

    @Override
    public String string() throws IOException {
        if (mStream.markSupported()) {
            mStream.mark(Integer.MAX_VALUE);
        }
        String charset = Headers.parseSubValue(mContentType, "charset", null);
        String stringBody = TextUtils.isEmpty(charset) ? IOUtils.toString(mStream) : IOUtils.toString(mStream, charset);
        mStream.reset();
        return stringBody;
    }

    @Override
    public byte[] byteArray() throws IOException {
        if (mStream.markSupported()) {
            mStream.mark(Integer.MAX_VALUE);
        }
        byte[] byteArrayBody = IOUtils.toByteArray(mStream);
        mStream.reset();
        return byteArrayBody;
    }

    @Override
    public InputStream stream() throws IOException {
        return mStream;
    }

    @Override
    public void close() throws IOException {
        mStream.close();
    }
}