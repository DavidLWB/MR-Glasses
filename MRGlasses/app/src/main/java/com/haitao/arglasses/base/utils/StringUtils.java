package com.haitao.arglasses.base.utils;

import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import java.text.DecimalFormat;

/**
 * 开发人员：周海涛
 * 文档说明：字符串处理工具类
 */
public class StringUtils {

    /**
     * 判断字符串是否相等,有null则为false
     */
    public static boolean equals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    /**
     * 判断字符串是否为空,不能为null字符串
     */
    public static boolean equalsIgnoreNull(String a, String b) {
        if (isEmptyNull(a) || isEmptyNull(b)) {
            return false;
        }
        return a.equals(b);
    }

    /**
     * 判断字符串是否为空,都是null为true
     */
    public static boolean equalsNull(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    // 判断字符串是否为空
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        } else {
            return str.trim().isEmpty();
        }
    }

    // 判断字符串是否为空且字符串不能是"null"
    public static boolean isEmptyNull(String str) {
        if (str == null) {
            return true;
        } else {
            String content = str.trim();
            return content.isEmpty() || content.equalsIgnoreCase("null");
        }
    }

    /**
     * 利用正则表达式判断字符串是否是数字
     */
    public static boolean isNumber(String str) {
        if (isEmptyNull(str)) {
            return false;
        }
        return str.matches("[0-9]+");
    }

    /**
     * 利用正则表达式判断字符串是否是小数点
     */
    public static boolean isFloat(String str) {
        if (isEmptyNull(str)) {
            return false;
        }
        return str.matches("\\d\\.\\d+");
    }

    /**
     * 利用正则表达式判断字符串是否包含中文
     */
    public static boolean isContainsChinese(String str) {
        if (isEmptyNull(str)) {
            return false;
        }
        return str.matches("^[\\u4e00-\\u9fa5]");
    }

    /**
     * 利用正则表达式判断字符串是否是手机号
     */
    public static boolean isMobilePhoneNum(String str) {
        if (isEmptyNull(str)) {
            return false;
        }
        return str.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 利用正则表达式判断字符串是否是电话号
     * @param str 要判断的字符串
     * @param regionCodeIsMust 是否必须填写区域号
     *                         false (可以省略区号和-)
     *                         true (必须带区号可以不带-)
     */
    public static boolean isTelephone(String str, boolean regionCodeIsMust) {
        if (isEmptyNull(str)) {
            return false;
        }
        if (regionCodeIsMust) {
            return str.matches("^\\d{3,4}-?\\d{7,8}$");
        } else {
            return str.matches("^\\d{7,8}|^\\d{3,4}-?\\d{7,8}$");
        }
    }

    /**
     * 利用正则表达式判断字符串是否是电话或手机号
     */
    public static boolean isMobilePhoneNumOrTelephone(String str) {
        if (isEmptyNull(str)) {
            return false;
        }
        return str.matches("^1[3-9]\\d{9}|^\\d{7,8}|^\\d{3,4}-?\\d{7,8}$");
    }

    /**
     * 是否是Html文本
     */
    public static boolean isHtmlText(String text) {
        return !isEmptyNull(text) && text.startsWith("<") && text.endsWith(">");
    }

    /**
     * 判断字符串是否是url地址
     */
    public static boolean isWebUrl(String url) {
        if (isEmptyNull(url)) {
            return false;
        }
        return url.matches("^((https|http|ftp|rtsp|mms)?://)[^\\s]+");
    }

    /**
     * 字符串数值转int
     */
    public static int getStrInt(String str, int defaultValue) {
        if (!isNumber(str)) {
            return defaultValue;
        }
        return Integer.parseInt(str);
    }

    /**
     * 字符串数值转float
     */
    public static float getStrFloat(String num, float defaultValue) {
        if (!isFloat(num)) {
            return defaultValue;
        }
        return Float.parseFloat(num);
    }

    /**
     * 获取字符串值,默认空字符串,绝不会为null
     */
    public static String getStr(String str) {
        return isEmpty(str) ? "" : str;
    }

    /**
     * 获取字符串值,默认空字符串,绝不会为null
     */
    public static String getStr(Object str) {
        return str == null ? "" : str.toString();
    }

    /**
     * 获取字符串值,可以设置默认值
     */
    public static String getStr(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * 获取字符串值 且不能为null字符串
     */
    public static String getStrIgnoreNull(String str, String defaultStr) {
        return isEmptyNull(str) ? defaultStr : str;
    }

    /**
     * 获取拼接开头后的字符串值,若字符串为null则返回空字符串,不会拼接(会忽略null字符串)
     */
    public static String getStrSpliceStart(String str, @NonNull String start) {
        return isEmptyNull(str) ? "" : start + str;
    }

    /**
     * 获取拼接开头后的字符串值,若字符串为null则返回空字符串,不会拼接(会忽略null字符串)
     */
    public static String getStrSpliceStart(String str, @NonNull String start, String defaultStr) {
        return isEmptyNull(str) ? defaultStr : start + str;
    }

    /**
     * 获取拼接结尾后的字符串值,若字符串为null则返回空字符串,不会拼接(会忽略null字符串)
     */
    public static String getStrSpliceEnd(String str, @NonNull String end) {
        return isEmptyNull(str) ? "" : str + end;
    }

    /**
     * 获取拼接结尾后的字符串值,若字符串为null则返回空字符串,不会拼接(会忽略null字符串)
     */
    public static String getStrSpliceEnd(String str, @NonNull String end, String defaultStr) {
        return isEmptyNull(str) ? defaultStr : str + end;
    }

    /**
     * 获取地址前缀拼接
     */
    public static String getUrlSplice(String url, @NonNull String http) {
        if (!isWebUrl(url)) {
            return http + url;
        }
        return url;
    }

    /**
     * 获取字符串长度
     */
    public static int getLength(String str) {
        return isEmpty(str) ? 0 : str.length();
    }

    /**
     * 获取颜色值
     */
    public static String getColor(String color, @NonNull String defaultColor) {
        if (isEmptyNull(color)) {
            return defaultColor;
        } else {
            if (color.startsWith("#")) {
                if (color.matches("^#[a-z0-9A-Z]{6}$")) {
                    return color;
                } else {
                    return defaultColor;
                }
            } else {
                if (color.matches("[a-z0-9A-Z]{6}$")) {
                    return "#" + color;
                } else {
                    return defaultColor;
                }
            }
        }
    }

    /**
     * 获取颜色值
     */
    public static int getColor(String color, @ColorRes int defaultColor) {
        if (isEmptyNull(color)) {
            return defaultColor;
        } else {
            if (color.startsWith("#")) {
                if (color.matches("^#[a-z0-9A-Z]{6}$")) {
                    return Color.parseColor(color);
                } else {
                    return defaultColor;
                }
            } else {
                if (color.matches("[a-z0-9A-Z]{6}$")) {
                    return Color.parseColor("#" + color);
                } else {
                    return defaultColor;
                }
            }
        }
    }

    /**
     * 精确后两位
     */
    public static String forMathDouble(double number) {
        String format = "";
        try {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            format = decimalFormat.format(number);
        } catch (Exception e) {
            LogUtil.e("StringUtils.forMatDouble(double number)", e.toString());
        }
        return format;
    }

    /**
     * 获取NFC刷卡结果  卡片内容
     */
    public static String getNFCContent(Intent intent) {
        if (intent == null) {
            return "";
        }
        //获取到读取的卡片内容
        Parcelable[] info = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        String content = "";
        if (info != null) {
            NdefMessage[] msg = new NdefMessage[info.length];
            for (int i = 0; i < info.length; i++) {
                msg[i] = (NdefMessage) info[i];
            }
            if (msg.length > 0) {
                NdefRecord record = msg[0].getRecords()[0];
                byte[] payload = record.getPayload();
                String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                int languageCodeLength = payload[0] & 63;
                try {
                    String data = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                    if (data.startsWith("#")) {
                        content = data.substring(1);
                    } else {
                        content = data;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }
}
