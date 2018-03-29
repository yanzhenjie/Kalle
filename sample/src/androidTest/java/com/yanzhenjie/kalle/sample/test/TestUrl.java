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
package com.yanzhenjie.kalle.sample.test;

import android.support.test.runner.AndroidJUnit4;

import com.yanzhenjie.kalle.Params;
import com.yanzhenjie.kalle.Url;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by YanZhenjie on 2017/12/7.
 */
@RunWith(AndroidJUnit4.class)
public class TestUrl {

    @Test
    public void testBase() throws Exception {
        Url url = Url.newBuilder("http://www.example.com")
                .setScheme("https")
                .setHost("github.com")
                .setPort(666)
                .build();
        Assert.assertEquals(url.toString(), "https://github.com:666");
    }

    @Test
    public void testAddPath() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/")
                .addPath("abc")
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/user/abc");
    }

    @Test
    public void testSetPath() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/album/photo")
                .setPath("/account/name")
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/account/name");
    }

    @Test
    public void testClearPath() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/album/photo")
                .clearPath()
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com");
    }

    @Test
    public void testAddQuery() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/?name=abc")
                .addQuery("age", 18)
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/user?name=abc&age=18");
    }

    @Test
    public void testSetQuery() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/?name=abc")
                .setQuery("name=xyz&sex=0")
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/user?name=xyz&sex=0");
    }

    @Test
    public void testSetQuery2() throws Exception {
        Params params = Params.newBuilder()
                .add("name", "mln")
                .add("height", 170)
                .build();
        Url url = Url.newBuilder("http://www.example.com/user?name=abc")
                .setQuery(params)
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/user?name=mln&height=170");
    }

    @Test
    public void testClearQuery() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user?name=abc&sex=1")
                .clearQuery()
                .build();
        Assert.assertEquals(url.toString(), "http://www.example.com/user");
    }

    @Test
    public void testLocation() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/album/photo?name=abc").build();
        Url newUrl = url.location("/account/name?age=18");
        Assert.assertEquals(newUrl.toString(), "http://www.example.com/account/name?age=18");
    }

    @Test
    public void testRelativeLocation() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user?name=abc").build();
        Url newUrl = url.location("abc");
        Assert.assertEquals(newUrl.toString(), "http://www.example.com/user/abc");
    }

    @Test
    public void testMatchLocation() throws Exception {
        Url url = Url.newBuilder("http://www.example.com/user/album/photo?name=abc").build();
        Url newUrl = url.location("../../get?name=mln");
        Assert.assertEquals(newUrl.toString(), "http://www.example.com/get?name=mln");
    }
}