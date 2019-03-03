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
package com.yanzhenjie.kalle.sample.app.main;

import android.app.Activity;

import com.yanzhenjie.kalle.sample.entity.News;
import com.yanzhenjie.kalle.sample.mvp.BasePresenter;
import com.yanzhenjie.kalle.sample.mvp.BaseView;

import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/3/27.
 */
public final class Contract {

    public interface MainPresenter extends BasePresenter {

        /**
         * Click function item.
         */
        void clickItem(int position);
    }

    public static abstract class MainView extends BaseView<MainPresenter> {

        public MainView(Activity activity, MainPresenter presenter) {
            super(activity, presenter);
        }

        /**
         * Bind data.
         */
        public abstract void setDataList(List<News> dataList);
    }
}