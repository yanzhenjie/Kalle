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
package com.yanzhenjie.kalle.sample.app.form;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.kalle.FileBinary;
import com.yanzhenjie.kalle.FormBody;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.ProgressBar;
import com.yanzhenjie.kalle.sample.R;
import com.yanzhenjie.kalle.sample.app.BaseActivity;
import com.yanzhenjie.kalle.sample.app.form.entity.FileInfo;
import com.yanzhenjie.kalle.sample.app.form.entity.FileItem;
import com.yanzhenjie.kalle.sample.config.UrlConfig;
import com.yanzhenjie.kalle.simple.SimpleCallback;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhenjie Yan on 2018/3/22.
 */
public class FormPresenter extends BaseActivity implements Contract.FormPresenter {

    private Contract.FormView mView;

    private ArrayList<AlbumFile> mAlbumList;
    private List<FileItem> mFileItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        mView = new FormView(this, this);
    }

    @Override
    public void addFile() {
        Album.image(this)
            .multipleChoice()
            .selectCount(3)
            .camera(true)
            .checkedList(mAlbumList)
            .onResult(new Action<ArrayList<AlbumFile>>() {
                @Override
                public void onAction(@NonNull ArrayList<AlbumFile> albumFiles) {
                    mAlbumList = albumFiles;

                    mFileItems = new ArrayList<>();
                    for (AlbumFile albumFile : mAlbumList) {
                        FileItem fileItem = new FileItem();
                        fileItem.setAlbumFile(albumFile);
                        mFileItems.add(fileItem);
                    }
                    mView.setFileList(mFileItems);

                    mView.setStatusText(getString(R.string.form_upload_wait));
                }
            })
            .start();
    }

    @Override
    public void uploadFile() {
        if (mAlbumList != null) {
            executeUpload();
        } else {
            mView.toast(R.string.form_upload_select_error);
        }
    }

    private void executeUpload() {
        FileBinary binary1 = new FileBinary(new File(mAlbumList.get(0).getPath())).onProgress(
            new ProgressBar<FileBinary>() {
                @Override
                public void progress(FileBinary origin, int progress) {
                    mFileItems.get(0).setProgress(progress);
                    mView.notifyItem(0);
                }
            });
        FileBinary binary2 = null;
        if (mAlbumList.size() > 1) {
            binary2 = new FileBinary(new File(mAlbumList.get(1).getPath())).onProgress(new ProgressBar<FileBinary>() {
                @Override
                public void progress(FileBinary origin, int progress) {
                    mFileItems.get(1).setProgress(progress);
                    mView.notifyItem(1);
                }
            });
        }
        FileBinary binary3 = null;
        if (mAlbumList.size() > 2) {
            binary3 = new FileBinary(new File(mAlbumList.get(2).getPath())).onProgress(new ProgressBar<FileBinary>() {
                @Override
                public void progress(FileBinary origin, int progress) {
                    mFileItems.get(2).setProgress(progress);
                    mView.notifyItem(2);
                }
            });
        }

        FormBody formBody = FormBody.newBuilder()
            .param("name", "kalle")
            .param("age", 18)
            .binary("file1", binary1)
            .binary("file2", binary2)
            .binary("file3", binary3)
            .build();
        formBody.onProgress(new ProgressBar<FormBody>() {
            @Override
            public void progress(FormBody origin, int progress) {
                String text = getString(R.string.form_progress, progress);
                mView.setStatusText(text);
            }
        });

        Kalle.post(UrlConfig.UPLOAD_FORM).body(formBody).tag(this).perform(new SimpleCallback<FileInfo>() {
            @Override
            public void onResponse(SimpleResponse<FileInfo, String> response) {
                if (response.isSucceed()) {
                    mAlbumList = null;

                    mView.setStatusText(getString(R.string.form_upload_result));
                } else {
                    mView.toast(response.failed());
                }
            }
        });
    }

}