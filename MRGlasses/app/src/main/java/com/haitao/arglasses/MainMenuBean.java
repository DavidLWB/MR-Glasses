package com.haitao.arglasses;

import android.graphics.drawable.Drawable;

/**
 * 开发人员：周海涛
 * 开发时间：2024/8/10
 * 文档说明：
 */
public class MainMenuBean {
    public Drawable icon = null;
    public String name = "";
    public String packageName = "";
    public String activityName = "";
    public String info = "";

    public MainMenuBean(String name) {
        this.name = name;
    }

    public MainMenuBean(String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
    }
}
