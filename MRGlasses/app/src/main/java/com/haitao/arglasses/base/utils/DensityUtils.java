package com.haitao.arglasses.base.utils;

import android.content.Context;

/**
 * 开发人员：周海涛
 * 文档说明：像素转换工具类
 */
public class DensityUtils {

    /**
     * @param sp 需要转换的sp值
     * @return 转换px后的值
     */
    public static float sp2px(Context context, float sp) {
        float nV = sp * context.getResources().getDisplayMetrics().scaledDensity;
        return nV < 0 ? nV - 0.5f : nV + 0.5f;
    }

    /**
     * @param dp 需要转换的dp值
     * @return 转换px后的值
     */
    public static float dp2px(Context context, float dp) {
        float nV = dp * context.getResources().getDisplayMetrics().density;
        return nV < 0 ? nV - 0.5f : nV + 0.5f;
    }
}
