package com.haitao.arglasses.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.haitao.arglasses.BuildConfig;
import com.haitao.arglasses.base.utils.LogUtil;
import com.haitao.arglasses.base.utils.NetUtil;
import com.haitao.arglasses.base.utils.SPUtil;
import com.haitao.arglasses.base.utils.ToastUtil;

import java.util.ArrayList;

/**
 * 开发人员：周海涛
 * 文档说明：
 */
public class BaseApplication extends Application {
    private static final ArrayList<Activity> mActivities = new ArrayList<>();
    private long mStartTime;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        init();
    }

    private void init() {
        String Tag = "初始化";
        createCatch();
        //本地存储
        SPUtil.getInstance().init(this);
        LogUtil.d(Tag, "已执行 SPUtil 初始化操作");
        //toast
        ToastUtil.init(this);
        LogUtil.d(Tag, "已执行 ToastUtil 初始化操作");
        //网络状态
        NetUtil.getInstance().init(this);
        LogUtil.d(Tag, "已执行 NetUtil 初始化操作");
        // registerActivityListener();
    }

    /**
     * APP整个生命周期异常捕捉,无感知崩溃
     */
    private void createCatch() {
        //正式上线抓取全部异常,禁止崩溃
        if (!BuildConfig.DEBUG) {
            new Handler(getMainLooper()).post(() -> {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Activity nActivity = mActivities.get(mActivities.size() - 1);
                        LogUtil.e("全局异常-主线程", nActivity.getLocalClassName() + " : " + e.getMessage());
                        if (System.currentTimeMillis() - mStartTime < 500) {
                            LogUtil.e("全局异常-主线程", "已销毁异常启动的Activity : " + nActivity.getLocalClassName());
                            nActivity.finish();
                        }
                    }
                }
            });
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                e.printStackTrace();
                Activity nActivity = mActivities.get(mActivities.size() - 1);
                LogUtil.e("全局异常-子线程", nActivity.getLocalClassName() + " : " + e.getMessage());
                if (System.currentTimeMillis() - mStartTime < 500) {
                    LogUtil.e("全局异常-子线程", "已销毁异常启动的Activity : " + nActivity.getLocalClassName());
                    nActivity.finish();
                }
            });
        }
    }

    private void registerActivityListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtil.d("Activity生命周期", activity.getClass().getName() + "-->Created");
                addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtil.d("Activity生命周期", activity.getClass().getName() + "-->Started");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtil.d("Activity生命周期", activity.getClass().getName() + "-->Resumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtil.d("Activity生命周期", activity.getClass().getName() + "-->Paused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtil.d("Activity生命周期", activity.getClass().getName() + "-->Stopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LogUtil.d("Activity生命周期", activity.getClass().getName() + "-->(系统主动回收)SaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtil.d("Activity生命周期", activity.getClass().getName() + "-->Destroyed");
                if (mActivities.isEmpty()) return;
                removeActivity(activity);
            }
        });
    }

    private void addActivity(Activity activity) {
        mActivities.remove(activity);
        mActivities.add(activity);
        mStartTime = System.currentTimeMillis();
    }

    //銷毀指定activity
    private void removeActivity(Activity activity) {
        if (mActivities.contains(activity)) {
            mActivities.remove(activity);
            activity.finish();
        }
    }

    public static ArrayList<Activity> getActivities() {
        return mActivities;
    }

    public static Activity getTopActivity() {
        if (mActivities.isEmpty()) return null;
        return mActivities.get(mActivities.size() - 1);
    }
}
