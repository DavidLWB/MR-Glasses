package com.haitao.arglasses.base.utils;

import android.app.Application;
import android.view.Gravity;

import com.haitao.arglasses.BuildConfig;
import com.hjq.toast.ToastStrategy;
import com.hjq.toast.Toaster;
import com.hjq.toast.style.WhiteToastStyle;

/**
 * 开发人员：周海涛
 * 文档说明：提示语工具类
 */
public class ToastUtil {
    private static boolean mIsInit = false;

    /**
     * 初始化,必须调用
     */
    public static void init(Application application) {
        Toaster.init(application);
        // Toaster.setStyle(new BlackToastStyle());
        Toaster.setStyle(new WhiteToastStyle());
        Toaster.setStrategy(new ToastStrategy(ToastStrategy.SHOW_STRATEGY_TYPE_QUEUE));//排队显示
        Toaster.setGravity(Gravity.CENTER);
        mIsInit = true;
    }

    public static void showLong(String msg) {
        if (mIsInit) {
            Toaster.showLong(msg);
        } else {
            throw new RuntimeException("未初始化异常,请先调用SPUtil.getInstance().init(this);");
        }
    }

    public static void showShort(String msg) {
        if (mIsInit) {
            Toaster.showShort(msg);
        } else {
            throw new RuntimeException("未初始化异常,请先调用SPUtil.getInstance().init(this);");
        }
    }

    public static void showDebug(String msg) {
        if (mIsInit) {
            if (BuildConfig.DEBUG) Toaster.showShort(msg);
        } else {
            throw new RuntimeException("未初始化异常,请先调用SPUtil.getInstance().init(this);");
        }
    }

    public static void cancel() {
        if (mIsInit) {
            Toaster.cancel();
        } else {
            throw new RuntimeException("未初始化异常,请先调用SPUtil.getInstance().init(this);");
        }
    }
}
