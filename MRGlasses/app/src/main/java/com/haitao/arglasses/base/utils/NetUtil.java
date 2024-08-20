package com.haitao.arglasses.base.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import com.haitao.arglasses.base.BaseApplication;

/**
 * 开发人员：周海涛
 * 文档说明：网络相关工具类
 */
public class NetUtil {
    private static NetUtil mNetUtil = null;

    public static NetUtil getInstance() {
        synchronized (NetUtil.class) {
            if (mNetUtil == null) {
                mNetUtil = new NetUtil();
            }
            return mNetUtil;
        }
    }

    private NetUtil() {
    }

    public final void init(Application application) {

    }

    @SuppressLint("ObsoleteSdkInt")
    public boolean hasNetwork(String tag, boolean isShowLog) {
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //新版本调用方法获取网络状态
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo != null) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            if (isShowLog) LogUtil.d(tag, "网络连接状态-->WiFi网络连接正常");
                        }
                        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            if (isShowLog) LogUtil.d(tag, "网络连接状态-->移动网络连接正常");
                        }
                        return true;
                    }
                }
            }
        } else {
            //否则调用旧版本方法
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo networkInfo : info) {
                        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                                if (isShowLog) LogUtil.d(tag, "网络连接状态-->WiFi网络连接正常");
                            }
                            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                                if (isShowLog) LogUtil.d(tag, "网络连接状态-->移动网络连接正常");
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasNetwork(String tag) {
        return hasNetwork(tag, true);
    }
}
