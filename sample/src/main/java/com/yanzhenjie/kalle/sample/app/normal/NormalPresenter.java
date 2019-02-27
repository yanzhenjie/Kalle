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
package com.yanzhenjie.kalle.sample.app.normal;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.sample.R;
import com.yanzhenjie.kalle.sample.app.BaseActivity;
import com.yanzhenjie.kalle.sample.config.UrlConfig;
import com.yanzhenjie.kalle.sample.entity.News;
import com.yanzhenjie.kalle.sample.entity.NewsWrapper;
import com.yanzhenjie.kalle.sample.entity.Page;
import com.yanzhenjie.kalle.sample.http.SimpleCallback;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/3/27.
 */
public class NormalPresenter extends BaseActivity implements Contract.NormalPresenter {

    private Contract.NormalView mView;

    private List<News> mDataList;
    private Page mPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        mView = new NormalView(this, this);

        mView.setRefresh(true);
        refresh();
    }

    @Override
    public void refresh() {
        Kalle.get(UrlConfig.GET_LIST)
            .param("pageNum", 1)
            .param("pageSize", 50)
            .tag(this)
            .perform(new SimpleCallback<NewsWrapper>(this) {
                @Override
                public void onResponse(SimpleResponse<NewsWrapper, String> response) {
                    if (response.isSucceed()) {
                        NewsWrapper wrapper = response.succeed();
                        mDataList = wrapper.getDataList();
                        mPage = wrapper.getPage();

                        mView.setDataList(mDataList, mPage);
                    } else {
                        mView.toast(response.failed());
                    }

                    // Finish refresh.
                    mView.setRefresh(false);
                }
            });
    }

    @Override
    public void loadMore() {
        Kalle.get(UrlConfig.GET_LIST)
            .param("pageNum", mPage.getPageNum() + 1)
            .param("pageSize", 50)
            .perform(new SimpleCallback<NewsWrapper>(this) {
                @Override
                public void onResponse(SimpleResponse<NewsWrapper, String> response) {
                    if (response.isSucceed()) {
                        NewsWrapper wrapper = response.succeed();
                        List<News> dataList = wrapper.getDataList();
                        if (dataList != null && !dataList.isEmpty()) {
                            mDataList.addAll(dataList);
                            mPage = wrapper.getPage();
                        }
                    } else {
                        mView.toast(response.failed());
                    }

                    // Finish load more.
                    mView.addDataList(mPage);
                }
            });
    }

    @Override
    public void clickItem(int position) {
        News news = mDataList.get(position);
        mView.toast(news.getTitle());
    }
}