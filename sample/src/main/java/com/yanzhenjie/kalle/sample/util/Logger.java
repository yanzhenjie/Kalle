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
package com.yanzhenjie.kalle.sample.util;

import android.util.Log;

import java.util.Locale;

/**
 * Created in Jul 28, 2015 7:32:05 PM.
 */
public class Logger {

    private static final String ERROR_MESSAGE = "An exception occurs.";

    private static String sTag = "KalleSample";
    private static boolean sEnable = false;

    public static void setEnable(boolean enable) {
        Logger.sEnable = enable;
    }

    public static void v(Object message) {
        if (sEnable) Log.v(sTag, String.valueOf(message));
    }

    public static void v(Throwable e) {
        if (sEnable) Log.v(sTag, buildMessage(ERROR_MESSAGE), e);
    }

    public static void v(Object message, Throwable e) {
        if (sEnable) Log.v(sTag, buildMessage(message), e);
    }

    public static void i(Object message) {
        if (sEnable) Log.i(sTag, String.valueOf(message));
    }

    public static void i(Throwable e) {
        if (sEnable) Log.i(sTag, buildMessage(ERROR_MESSAGE), e);
    }

    public static void i(Object message, Throwable e) {
        if (sEnable) Log.i(sTag, buildMessage(message), e);
    }

    public static void d(Object message) {
        if (sEnable) Log.d(sTag, String.valueOf(message));
    }

    public static void d(Throwable e) {
        if (sEnable) Log.d(sTag, buildMessage(ERROR_MESSAGE), e);
    }

    public static void d(Object message, Throwable e) {
        if (sEnable) Log.d(sTag, buildMessage(message), e);
    }

    public static void w(Object message) {
        if (sEnable) Log.w(sTag, String.valueOf(message));
    }

    public static void w(Throwable e) {
        if (sEnable) Log.w(sTag, buildMessage(ERROR_MESSAGE), e);
    }

    public static void w(Object message, Throwable e) {
        if (sEnable) Log.w(sTag, buildMessage(message), e);
    }

    public static void e(Object message) {
        if (sEnable) Log.e(sTag, String.valueOf(message));
    }

    public static void e(Throwable e) {
        if (sEnable) Log.e(sTag, buildMessage(ERROR_MESSAGE), e);
    }

    public static void e(Object message, Throwable e) {
        if (sEnable) Log.e(sTag, buildMessage(message), e);
    }

    private static String buildMessage(Object message) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        for (int i = 2; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(Logger.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), caller, String.valueOf(message));
    }

}