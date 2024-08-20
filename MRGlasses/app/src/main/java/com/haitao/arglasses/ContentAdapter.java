package com.haitao.arglasses;

import android.view.ViewGroup;

import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

/**
 * 开发人员：周海涛
 * 开发时间：2024/8/11
 * 文档说明：
 */
public class ContentAdapter extends BannerAdapter<MainMenuBean, QuickViewHolder> {

    public ContentAdapter(List<MainMenuBean> list) {
        super(list);
    }

    @Override
    public QuickViewHolder onCreateHolder(ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindView(QuickViewHolder quickViewHolder, MainMenuBean mainMenuBean, int i, int i1) {

    }
}
