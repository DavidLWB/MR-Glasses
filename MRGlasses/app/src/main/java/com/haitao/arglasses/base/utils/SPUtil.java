package com.haitao.arglasses.base.utils;

import android.app.Application;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;

import java.util.Collections;
import java.util.Set;

/**
 * 开发人员：周海涛
 * 文档说明：本地SP存储工具类
 * 依赖版本: implementation 'com.tencent:mmkv:1.3.0'
 */
public class SPUtil {
    /*================以下信息存储到User表==================*/
    public String getUserJson() {
        return getStringFromUser("LoginSuccessJson", "");
    }

    public String getToken(boolean isUserBearer) {
        String token = getStringFromUser("Authorization", "");
        if (isUserBearer) {
            if (token.startsWith("Bearer ")) {
                return token;
            } else {
                return "Bearer " + token;
            }
        } else {
            return token.replace("Bearer ", "");
        }
    }

    public String getRefreshToken() {
        return getStringFromUser("refresh_token", "");
    }

    public String getUserAccount() {
        return getStringFromUser("userCode", "");
    }

    public String getRoleName() {
        return getStringFromUser("roleName", "");
    }

    public void saveUserAccount(String userCode) {
        saveToUser("userCode", userCode);
    }

    public void saveToken(String token) {
        saveToUser("Authorization", token);
    }

    public void saveUserJson(String json) {
        saveToUser("LoginSuccessJson", json);
    }

    public void saveRefreshToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");
        }
        saveToUser("refresh_token", token);
    }

    /*================以下信息存储到App表==================*/

    public void setAppIsFirstOpen(boolean isFirst) {
        saveToApp("IsFirstOpen", isFirst ? 0 : 1);
    }

    public boolean getAppIsFirstOpen() {
        return getIntFromAPP("IsFirstOpen", 0) == 0;
    }

    /*========================以下为实现方法===========================*/
    private static SPUtil mSPUtil = null;
    private static boolean mIsInit = false;

    public static SPUtil getInstance() {
        synchronized (SPUtil.class) {
            if (mSPUtil == null) {
                mSPUtil = new SPUtil();
            } else {
                if (!mIsInit) {
                    throw new RuntimeException("未初始化异常,请先调用 ToastUtil.getInstance().init(this);");
                }
            }
            return mSPUtil;
        }
    }

    private SPUtil() {
    }

    private MMKV mUserSP, mAppSP;

    /**
     * 初始化  必须调用
     */
    public final void init(Application application) {
        MMKV.initialize(application);
        mUserSP = MMKV.mmkvWithID("user", MMKV.MULTI_PROCESS_MODE);
        mAppSP = MMKV.mmkvWithID("muzhitong", MMKV.MULTI_PROCESS_MODE);
        mIsInit = true;
    }

    /**
     * 保存用户相关数据
     */
    public final void saveToUser(@NonNull String key, @NonNull Object object) {
        if (object instanceof String) {
            mUserSP.encode(key, (String) object);
        } else if (object instanceof Integer) {
            mUserSP.encode(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mUserSP.encode(key, (Boolean) object);
        } else if (object instanceof Float) {
            mUserSP.encode(key, (Float) object);
        } else if (object instanceof Long) {
            mUserSP.encode(key, (Long) object);
        } else if (object instanceof Double) {
            mUserSP.encode(key, (Double) object);
        } else if (object instanceof byte[]) {
            mUserSP.encode(key, (byte[]) object);
        } else {
            mUserSP.encode(key, object.toString());
        }
    }

    /**
     * 保存APP相关数据
     */
    public final void saveToApp(@NonNull String key, @NonNull Object object) {
        if (object instanceof String) {
            mAppSP.encode(key, (String) object);
        } else if (object instanceof Integer) {
            mAppSP.encode(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mAppSP.encode(key, (Boolean) object);
        } else if (object instanceof Float) {
            mAppSP.encode(key, (Float) object);
        } else if (object instanceof Long) {
            mAppSP.encode(key, (Long) object);
        } else if (object instanceof Double) {
            mAppSP.encode(key, (Double) object);
        } else if (object instanceof byte[]) {
            mAppSP.encode(key, (byte[]) object);
        } else {
            mAppSP.encode(key, object.toString());
        }
    }

    public final void saveSetToUser(@NonNull String key, @NonNull Set<String> sets) {
        mUserSP.encode(key, sets);
    }

    public final void saveSetToApp(@NonNull String key, @NonNull Set<String> sets) {
        mAppSP.encode(key, sets);
    }

    public final void saveParcelableToUser(@NonNull String key, @NonNull Parcelable obj) {
        mUserSP.encode(key, obj);
    }

    public final void saveParcelableToApp(@NonNull String key, @NonNull Parcelable obj) {
        mAppSP.encode(key, obj);
    }

    /**
     * 移除某个key对
     */
    public final void removeUserKey(@NonNull String key) {
        mUserSP.removeValueForKey(key);
    }

    public final void removeAppKey(@NonNull String key) {
        mAppSP.removeValueForKey(key);
    }

    /**
     * 是否包含某个key
     */
    public final boolean containsUserKey(@NonNull String key) {
        return mUserSP.containsKey(key);
    }

    public final boolean containsAppKey(@NonNull String key) {
        return mAppSP.containsKey(key);
    }

    /**
     * 清除用户相关所有SP
     */
    public final void clearUser() {
        new Thread(() -> mUserSP.clearAll()).start();
    }

    /**
     * 清除App所有SP
     */
    public final void clearApp() {
        new Thread(() -> mAppSP.clearAll()).start();
    }

    /**
     * 清除所有SP
     */
    public final void clearAll() {
        new Thread(() -> {
            mUserSP.clearAll();
            mAppSP.clearAll();
        }).start();
    }

    public final int getIntFromUser(@NonNull String key, int defaultValue) {
        return mUserSP.decodeInt(key, defaultValue);
    }

    public final int getIntFromAPP(@NonNull String key, int defaultValue) {
        return mAppSP.decodeInt(key, defaultValue);
    }

    public final double getDoubleFromUser(@NonNull String key, double defaultValue) {
        return mUserSP.decodeDouble(key, defaultValue);
    }

    public final double getDoubleFromAPP(@NonNull String key, double defaultValue) {
        return mAppSP.decodeDouble(key, defaultValue);
    }

    public final long getLongFromUser(@NonNull String key, long defaultValue) {
        return mUserSP.decodeLong(key, defaultValue);
    }

    public final long getLongFromAPP(@NonNull String key, long defaultValue) {
        return mAppSP.decodeLong(key, defaultValue);
    }

    public final boolean getBooleanFromUser(@NonNull String key, boolean defaultValue) {
        return mUserSP.decodeBool(key, defaultValue);
    }

    public final boolean getBooleanFromApp(@NonNull String key, boolean defaultValue) {
        return mAppSP.decodeBool(key, false);
    }

    public final float getFloatFromUser(@NonNull String key, float defaultValue) {
        return mUserSP.decodeFloat(key, defaultValue);
    }

    public final float getFloatFromApp(@NonNull String key, float defaultValue) {
        return mAppSP.decodeFloat(key, defaultValue);
    }

    public final byte[] getBytesFromUser(@NonNull String key, byte[] defaultValue) {
        return mUserSP.decodeBytes(key, defaultValue);
    }

    public final byte[] getBytesFromApp(@NonNull String key, byte[] defaultValue) {
        return mAppSP.decodeBytes(key, defaultValue);
    }

    public final String getStringFromUser(@NonNull String key, String defaultValue) {
        return mUserSP.decodeString(key, defaultValue);
    }

    public final String getStringFromApp(@NonNull String key, String defaultValue) {
        return mAppSP.decodeString(key, defaultValue);
    }

    public final Set<String> getStringSetFromUser(@NonNull String key) {
        return mUserSP.decodeStringSet(key, Collections.emptySet());
    }

    public final Set<String> getStringSetFromApp(@NonNull String key) {
        return mAppSP.decodeStringSet(key, Collections.emptySet());
    }

    public final Parcelable getParcelableFromUser(@NonNull String key) {
        return mUserSP.decodeParcelable(key, null);
    }

    public final Parcelable getParcelableFromApp(@NonNull String key) {
        return mAppSP.decodeParcelable(key, null);
    }
}
