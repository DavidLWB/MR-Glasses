package com.haitao.arglasses.base.utils;

import android.util.Log;

import com.haitao.arglasses.BuildConfig;


/**
 * 开发人员：周海涛
 * 文档说明：日志打印工具类
 */
public class LogUtil {
    private static final boolean isShowLog = BuildConfig.DEBUG;
    private static final String TAG = "TagLog";
    private static final int MAX_LENGTH = 3000;//msg支持的最大长度
    private static int MSG_COUNT = 0;//msg截取的第几段

    public static void d(String tag, Object msg) {
        if (isShowLog) {
            String info = msg == null ? "null" : msg.toString();
            MSG_COUNT = 0;
            if (tag == null || TAG.equals(tag) || "".equals(tag)) {
                if (info.length() <= MAX_LENGTH) {
                    Log.d(TAG, info);
                } else {
                    while (info.length() > MAX_LENGTH) {
                        MSG_COUNT++;
                        Log.d(TAG, "第" + MSG_COUNT + "段:::" + info.substring(0, MAX_LENGTH));
                        info = info.substring(MAX_LENGTH);
                    }
                    Log.d(TAG, "第" + (MSG_COUNT + 1) + "段:::" + info);
                }
            } else {
                if (info.length() <= MAX_LENGTH) {
                    Log.d(TAG, tag + ":::" + info);
                } else {
                    while (info.length() > MAX_LENGTH) {
                        MSG_COUNT++;
                        Log.d(TAG, tag + ":第" + MSG_COUNT + "段:::" + info.substring(0, MAX_LENGTH));
                        info = info.substring(MAX_LENGTH);
                    }
                    Log.d(TAG, tag + ":" + "第" + (MSG_COUNT + 1) + "段:::" + info);
                }
            }
        }
    }

    public static void d(Object msg) {
        d(TAG, msg.toString());
    }

    public static void i(String tag, Object msg) {
        if (isShowLog) {
            String info = msg == null ? "null" : msg.toString();
            MSG_COUNT = 0;
            if (tag == null || TAG.equals(tag) || "".equals(tag)) {
                if (info.length() <= MAX_LENGTH) {
                    Log.i(TAG, info);
                } else {
                    while (info.length() > MAX_LENGTH) {
                        MSG_COUNT++;
                        Log.i(TAG, "第" + MSG_COUNT + "段:::" + info.substring(0, MAX_LENGTH));
                        info = info.substring(MAX_LENGTH);
                    }
                    Log.i(TAG, "第" + (MSG_COUNT + 1) + "段:::" + info);
                }
            } else {
                if (info.length() <= MAX_LENGTH) {
                    Log.i(TAG, tag + ":::" + info);
                } else {
                    while (info.length() > MAX_LENGTH) {
                        MSG_COUNT++;
                        Log.i(TAG, tag + ":第" + MSG_COUNT + "段:::" + info.substring(0, MAX_LENGTH));
                        info = info.substring(MAX_LENGTH);
                    }
                    Log.i(TAG, tag + ":" + "第" + (MSG_COUNT + 1) + "段:::" + info);
                }
            }
        }
    }

    public static void i(Object msg) {
        i(TAG, msg.toString());
    }

    public static void w(String tag, Object msg) {
        if (isShowLog) {
            String info = msg == null ? "null" : msg.toString();
            MSG_COUNT = 0;
            if (tag == null || TAG.equals(tag) || "".equals(tag)) {
                if (info.length() <= MAX_LENGTH) {
                    Log.w(TAG, info);
                } else {
                    while (info.length() > MAX_LENGTH) {
                        MSG_COUNT++;
                        Log.w(TAG, "第" + MSG_COUNT + "段:::" + info.substring(0, MAX_LENGTH));
                        info = info.substring(MAX_LENGTH);
                    }
                    Log.w(TAG, "第" + (MSG_COUNT + 1) + "段:::" + info);
                }
            } else {
                if (info.length() <= MAX_LENGTH) {
                    Log.w(TAG, tag + ":::" + info);
                } else {
                    while (info.length() > MAX_LENGTH) {
                        MSG_COUNT++;
                        Log.w(TAG, tag + ":第" + MSG_COUNT + "段:::" + info.substring(0, MAX_LENGTH));
                        info = info.substring(MAX_LENGTH);
                    }
                    Log.w(TAG, tag + ":" + "第" + (MSG_COUNT + 1) + "段:::" + info);
                }
            }
        }
    }

    public static void w(Object msg) {
        w(TAG, msg.toString());
    }

    public static void e(String tag, Object msg) {
        if (isShowLog) {
            String info = msg == null ? "null" : msg.toString();
            MSG_COUNT = 0;
            if (tag == null || TAG.equals(tag) || "".equals(tag)) {
                if (info.length() <= MAX_LENGTH) {
                    Log.e(TAG, info);
                } else {
                    while (info.length() > MAX_LENGTH) {
                        MSG_COUNT++;
                        Log.e(TAG, "第" + MSG_COUNT + "段:::" + info.substring(0, MAX_LENGTH));
                        info = info.substring(MAX_LENGTH);
                    }
                    Log.e(TAG, "第" + (MSG_COUNT + 1) + "段:::" + info);
                }
            } else {
                if (info.length() <= MAX_LENGTH) {
                    Log.e(TAG, tag + ":::" + info);
                } else {
                    while (info.length() > MAX_LENGTH) {
                        MSG_COUNT++;
                        Log.e(TAG, tag + ":第" + MSG_COUNT + "段:::" + info.substring(0, MAX_LENGTH));
                        info = info.substring(MAX_LENGTH);
                    }
                    Log.e(TAG, tag + ":" + "第" + (MSG_COUNT + 1) + "段:::" + info);
                }
            }
        }
    }

    public static void e(Object msg) {
        e(TAG, msg.toString());
    }
}
