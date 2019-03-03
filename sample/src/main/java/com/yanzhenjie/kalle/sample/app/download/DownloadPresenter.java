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
package com.yanzhenjie.kalle.sample.app.download;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yanzhenjie.kalle.Canceller;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.download.Download;
import com.yanzhenjie.kalle.sample.R;
import com.yanzhenjie.kalle.sample.app.BaseActivity;
import com.yanzhenjie.kalle.sample.config.AppConfig;
import com.yanzhenjie.kalle.sample.config.UrlConfig;
import com.yanzhenjie.kalle.sample.http.DownloadCallback;

import java.math.BigDecimal;

/**
 * Created by Zhenjie Yan on 2018/3/27.
 */
public class DownloadPresenter extends BaseActivity implements Contract.DownloadPresenter {

    private Contract.DownloadView mView;

    private Canceller mCanceller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mView = new DownloadView(this, this);
    }

    @Override
    public void tryDownload() {
        if (mCanceller != null) {
            mCanceller.cancel();
        } else {
            mCanceller = Kalle.Download.get(UrlConfig.DOWNLOAD)
                .directory(AppConfig.get().PATH_APP_DOWNLOAD)
                .fileName("sou.apk")
                .onProgress(new Download.ProgressBar() {
                    @Override
                    public void onProgress(int progress, long byteCount, long speed) {
                        BigDecimal bg = new BigDecimal(speed / 1024D / 1024D);
                        String speedText = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
                        speedText = getString(R.string.download_speed, speedText);
                        mView.setProgress(progress, speedText);
                    }
                })
                .perform(new DownloadCallback(this) {
                    @Override
                    public void onStart() {
                        mView.onStart();
                    }

                    @Override
                    public void onException(String message) {
                        mCanceller = null;
                        mView.onError(message);
                    }

                    @Override
                    public void onFinish(String path) {
                        mCanceller = null;
                        mView.onFinish();
                    }

                    @Override
                    public void onCancel() {
                        mCanceller = null;
                        if (mView != null) {
                            mView.onCancel();
                        }
                    }
                });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCanceller != null) {
            mView = null;
            mCanceller.cancel();
        }
    }
}