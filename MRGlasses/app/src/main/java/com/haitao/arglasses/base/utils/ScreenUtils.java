package com.haitao.arglasses.base.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.haitao.arglasses.base.BaseApplication;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 开发人员：周海涛
 * 文档说明：获取屏幕相关工具类
 */
public class ScreenUtils {
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private boolean mHasCheckAllScreen;
    private boolean mIsAllScreenDevice;
    private static ScreenUtils mScreenUtils = null;

    public static ScreenUtils getInstance() {
        synchronized (ScreenUtils.class) {
            if (mScreenUtils == null) {
                mScreenUtils = new ScreenUtils();
            }
            return mScreenUtils;
        }
    }

    private ScreenUtils() {
    }

    private final Point[] mRealSizes = new Point[2];

    /***
     * 获取当前手机是否是全面屏
     * @return
     */
    @SuppressLint("ObsoleteSdkInt")
    public boolean isAllScreenDevice(@NonNull Context context) {
        if (mHasCheckAllScreen) {
            return mIsAllScreenDevice;
        }
        mHasCheckAllScreen = true;
        mIsAllScreenDevice = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            float width, height;
            if (point.x < point.y) {
                width = point.x;
                height = point.y;
            } else {
                width = point.y;
                height = point.x;
            }
            if (height / width >= 1.97f) {
                return true;
            }
        }
        return mIsAllScreenDevice;
    }

    @SuppressLint("ObsoleteSdkInt")
    public void init(@NonNull Context context) {
        mScreenWidth = SPUtil.getInstance().getIntFromAPP("ScreenWidth", 0);
        mScreenHeight = SPUtil.getInstance().getIntFromAPP("ScreenHeight", 0);
        if (mScreenWidth != 0 && mScreenHeight != 0) return;
        if (isAllScreenDevice(context)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                DisplayMetrics nDisplayMetrics = context.getResources().getDisplayMetrics();
                mScreenWidth = nDisplayMetrics.widthPixels;
                mScreenHeight = nDisplayMetrics.heightPixels;
                return;
            }
            int orientation = context.getResources().getConfiguration().orientation;
            orientation = orientation == Configuration.ORIENTATION_PORTRAIT ? 0 : 1;
            if (mRealSizes[orientation] == null) {
                WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                if (windowManager == null) {
                    DisplayMetrics nDisplayMetrics = context.getResources().getDisplayMetrics();
                    mScreenWidth = nDisplayMetrics.widthPixels;
                    mScreenHeight = nDisplayMetrics.heightPixels;
                    return;
                }
                Display display = windowManager.getDefaultDisplay();
                Point point = new Point();
                display.getRealSize(point);
                mRealSizes[orientation] = point;
            }
            mScreenWidth = mRealSizes[orientation].x;
            mScreenHeight = mRealSizes[orientation].y;
        } else {
            DisplayMetrics nMetrics = context.getResources().getDisplayMetrics();
            mScreenWidth = nMetrics.widthPixels;
            mScreenHeight = nMetrics.heightPixels;
        }
        SPUtil.getInstance().saveToApp("ScreenWidth", mScreenWidth);
        SPUtil.getInstance().saveToApp("ScreenHeight", mScreenHeight);
    }

    /**
     * 获取屏幕宽度
     */
    public int getScreenWidth() {
        if (mScreenWidth == 0) init(BaseApplication.getContext());
        return mScreenWidth;
    }

    /**
     * 获取屏幕高度
     */
    public int getScreenHeight() {
        if (mScreenHeight == 0) init(BaseApplication.getContext());
        return mScreenHeight;
    }

    /**
     * 获得状态栏的高度
     */
    @SuppressLint("PrivateApi")
    public int getStatusHeight(Context context) {
        int statusHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            LogUtil.e("获取状态栏高度异常", e.getMessage());
        }
        return statusHeight;
    }

    //获取虚拟按键的高度
    public int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     */
    public boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else {
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    public Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Bitmap bp = Bitmap.createBitmap(bmp, 0, 0, mScreenWidth, mScreenHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    @SuppressLint("NewApi")
    public Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, mScreenWidth, mScreenHeight - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 设置窗口透明度
     *
     * @param activity 最好传activity
     * @param alpha    背景透明度(1f-0f)
     */
    public static void setWindowAlpha(Context activity, @FloatRange(from = 0.0f, to = 1.0f) float alpha) {
        if (alpha > 1f || alpha < 0f) return;
        Activity nActivity;
        if (activity instanceof Activity) {
            nActivity = (Activity) activity;
        } else {
            ArrayList<Activity> nActivities = BaseApplication.getActivities();
            nActivity = BaseApplication.getActivities().get(nActivities.size() - 1);
        }
        if (nActivity == null) return;
        Window nWindow = nActivity.getWindow();
        if (nWindow == null) return;
        WindowManager.LayoutParams lp = nWindow.getAttributes();
        if (lp.alpha != 1f && alpha != 1f) return;
        lp.alpha = alpha; //设置透明度
        nWindow.setAttributes(lp);
    }

    /**
     * 判断虚拟按键栏是否重写
     */
    @SuppressLint({"PrivateApi", "ObsoleteSdkInt"})
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
                LogUtil.e("判断虚拟按键栏是否重写异常", e.getMessage());
            }
        }
        return sNavBarOverride;
    }
}
