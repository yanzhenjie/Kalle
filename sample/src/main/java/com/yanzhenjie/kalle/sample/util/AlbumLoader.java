/*
 * Copyright 2018 Yan Zhenjie.
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
package com.yanzhenjie.kalle.sample.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.task.DefaultAlbumLoader;

/**
 * Created by YanZhenjie on 2018/3/29.
 */
public class AlbumLoader implements com.yanzhenjie.album.AlbumLoader {

    public static final Drawable DEFAULT_DRAWABLE = new ColorDrawable(Color.parseColor("#FF2B2B2B"));

    @Override
    public void loadAlbumFile(ImageView imageView, AlbumFile albumFile, int viewWidth, int viewHeight) {
        if (albumFile.getMediaType() == AlbumFile.TYPE_IMAGE) {
            Glide.with(imageView.getContext())
                    .load(albumFile.getPath())
                    .placeholder(DEFAULT_DRAWABLE)
                    .error(DEFAULT_DRAWABLE)
                    .override(viewWidth, viewHeight)
                    .crossFade()
                    .into(imageView);
        } else if (albumFile.getMediaType() == AlbumFile.TYPE_VIDEO) {
            DefaultAlbumLoader.getInstance().loadAlbumFile(imageView, albumFile, viewWidth, viewHeight);
        }
    }

    @Override
    public void loadImage(ImageView imageView, String imagePath, int viewWidth, int viewHeight) {
        Glide.with(imageView.getContext())
                .load(imagePath)
                .placeholder(DEFAULT_DRAWABLE)
                .error(DEFAULT_DRAWABLE)
                .crossFade()
                .into(imageView);
    }

}