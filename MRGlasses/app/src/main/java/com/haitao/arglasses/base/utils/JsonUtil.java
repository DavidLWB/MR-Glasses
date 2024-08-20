package com.haitao.arglasses.base.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.haitao.arglasses.BuildConfig;

import org.json.JSONObject;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 开发人员：周海涛
 * 文档说明：Json数据解析工具类
 */
public class JsonUtil {
    private final static String TAG = "数据解析";
    private static Gson mGson, mGsonIgnoreNull;

    //解析数据
    public static String getString(String json, String key) {
        if (StringUtils.isEmptyNull(json)) {
            return null;
        } else {
            try {
                JSONObject jsonObject = new JSONObject(json);
                return jsonObject.getString(key);
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
            }
            return null;
        }
    }

    //解析数据
    public static <T> T getJsonBean(String json, Class<T> beanClass) {
        if (StringUtils.isEmptyNull(json)) {
            return null;
        } else {
            if (BuildConfig.DEBUG) {
                return getGson().fromJson(json, beanClass);
            } else {
                try {
                    return getGson().fromJson(json, beanClass);
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage());
                }
                return null;
            }
        }
    }

    //解析数据
    public static <T> T getJsonBean(String json, Type typeOfT) {
        if (StringUtils.isEmptyNull(json)) {
            return null;
        } else {
            if (BuildConfig.DEBUG) {
                return getGson().fromJson(json, typeOfT);
            } else {
                try {
                    return getGson().fromJson(json, typeOfT);
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage());
                }
                return null;
            }
        }
    }

    //解析数据
    public static <T> ArrayList<T> getJsonList(String json, Class<T> cls) {
        if (StringUtils.isEmptyNull(json)) {
            return null;
        } else {
            ArrayList<T> nList = new ArrayList<>();
            JsonElement nReader = JsonParser.parseReader(new StringReader(json));
            if (nReader != null) {
                JsonArray nJsonArray = nReader.getAsJsonArray();
                if (nJsonArray != null) {
                    if (BuildConfig.DEBUG) {
                        for (JsonElement nJsonElement : nJsonArray) {
                            nList.add(getGson().fromJson(nJsonElement, cls));
                        }
                    } else {
                        try {
                            for (JsonElement nJsonElement : nJsonArray) {
                                nList.add(getGson().fromJson(nJsonElement, cls));
                            }
                        } catch (Exception e) {
                            LogUtil.e(TAG, e.getMessage());
                        }
                    }
                }
            }
            return nList;
        }
    }

    //解析数据
    public static <T> T getJsonBeanIgnoreNull(String json, Class<T> beanClass) {
        if (StringUtils.isEmptyNull(json)) {
            return null;
        } else {
            if (BuildConfig.DEBUG) {
                return getGsonIgnoreNull().fromJson(json, beanClass);
            } else {
                try {
                    return getGsonIgnoreNull().fromJson(json, beanClass);
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage());
                }
                return null;
            }
        }
    }

    //解析数据
    public static <T> T getJsonBeanIgnoreNull(String json, Type typeOfT) {
        if (StringUtils.isEmptyNull(json)) {
            return null;
        } else {
            if (BuildConfig.DEBUG) {
                return getGsonIgnoreNull().fromJson(json, typeOfT);
            } else {
                try {
                    return getGsonIgnoreNull().fromJson(json, typeOfT);
                } catch (Exception e) {
                    LogUtil.e(TAG, e.getMessage());
                }
                return null;
            }
        }
    }

    public static String toJson(Object bean) {
        return getGson().toJson(bean);
    }

    public static String toJsonIgnoreNull(Object bean) {
        return getGsonIgnoreNull().toJson(bean);
    }

    public static String toJsonIgnoreNull(Object bean, Type type) {
        return getGsonIgnoreNull().toJson(bean, type);
    }

    public static Gson getGson() {
        if (mGson == null) mGson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
        return mGson;
    }

    public static Gson getGsonIgnoreNull() {
        if (mGsonIgnoreNull == null) mGsonIgnoreNull = new GsonBuilder().disableHtmlEscaping().create();
        return mGsonIgnoreNull;
    }
}
