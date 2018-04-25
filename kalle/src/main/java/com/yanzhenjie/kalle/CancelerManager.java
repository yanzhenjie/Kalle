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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by YanZhenjie on 2018/2/27.
 */
public class CancelerManager {

    private final List<CancelEntity> mRequestList;

    public CancelerManager() {
        this.mRequestList = new ArrayList<>();
    }

    /**
     * Add a task to cancel.
     *
     * @param request   target request.
     * @param canceller canceller.
     */
    public synchronized void addCancel(Request request, Canceller canceller) {
        CancelEntity cancelTag = new CancelEntity(request, canceller);
        mRequestList.add(cancelTag);
    }

    /**
     * Remove a task.
     *
     * @param request target request.
     */
    public synchronized void removeCancel(final Request request) {
        final Iterator<CancelEntity> it = mRequestList.iterator();
        while (it.hasNext()) {
            if (request == it.next().mRequest) {
                it.remove();
                break;
            }
        }
    }

    /**
     * According to the tag to cancel a task.
     *
     * @param tag tag.
     */
    public synchronized void cancel(Object tag) {
        for (CancelEntity entity : mRequestList) {
            Object newTag = entity.mRequest.tag();
            if (tag == newTag || (tag != null && newTag != null && tag.equals(newTag))) {
                entity.mCanceller.cancel();
            }
        }
    }

    private static class CancelEntity {
        private final Request mRequest;
        private final Canceller mCanceller;

        private CancelEntity(Request request, Canceller canceller) {
            this.mRequest = request;
            this.mCanceller = canceller;
        }
    }

}
