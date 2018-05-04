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
package com.yanzhenjie.kalle;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by YanZhenjie on 2018/2/27.
 */
public class CancelerManager {

    private final Lock mLock;
    private final Map<Request, Canceller> mCancelMap;

    public CancelerManager() {
        this.mLock = new ReentrantLock();
        this.mCancelMap = new HashMap<>();
    }

    /**
     * Add a task to cancel.
     *
     * @param request   target request.
     * @param canceller canceller.
     */
    public void addCancel(Request request, Canceller canceller) {
        mLock.lock();
        mCancelMap.put(request, canceller);
        mLock.unlock();
    }

    /**
     * Remove a task.
     *
     * @param request target request.
     */
    public void removeCancel(Request request) {
        mLock.lock();
        mCancelMap.remove(request);
        mLock.unlock();
    }

    /**
     * According to the tag to cancel a task.
     *
     * @param tag tag.
     */
    public void cancel(Object tag) {
        mLock.lock();
        for (Map.Entry<Request, Canceller> entry : mCancelMap.entrySet()) {
            Request request = entry.getKey();
            Object oldTag = request.tag();
            if ((tag == oldTag) || (tag != null && tag.equals(oldTag))) {
                entry.getValue().cancel();
            }
        }
        mLock.unlock();
    }
}