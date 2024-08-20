package com.haitao.arglasses.base.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.haitao.arglasses.R;

/**
 * 开发人员：周海涛
 * 文档说明：永久权限被拒提示弹窗
 */
public class PermissionSettingDialog extends AlertDialog {
    private TextView mTvTitle, mTvContent;
    private final Context mContext;
    private final OnButtonClickListener mListener;

    public PermissionSettingDialog(Context context, OnButtonClickListener listener) {
        super(context, R.style.TransparentDialogTheme);
        mContext = context;
        mListener = listener;
    }

    public PermissionSettingDialog setTitle(String title) {
        if (mTvTitle != null && title != null && !TextUtils.isEmpty(title)) mTvTitle.setText(title);
        return this;
    }

    public PermissionSettingDialog setMessage(String msg) {
        if (mTvContent != null && msg != null && !TextUtils.isEmpty(msg)) mTvContent.setText(msg);
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        Window nWindow = getWindow();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(outMetrics);
        WindowManager.LayoutParams lp = nWindow.getAttributes();
        lp.width = (int) (outMetrics.widthPixels * 0.8);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.dimAmount = 0.3f;//设置背景透明度
        nWindow.setAttributes(lp);
        nWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ;
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    private void init() {
        View layout = View.inflate(mContext, R.layout.dialog_permission_setting, null);
        setContentView(layout);
        mTvTitle = layout.findViewById(R.id.tv_title);
        mTvContent = layout.findViewById(R.id.tv_content);
        findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            dismiss();
            if (mListener != null) mListener.left();
        });
        findViewById(R.id.tv_ok).setOnClickListener(v -> {
            dismiss();
            if (mListener != null) mListener.right();
        });
    }

    @Override
    public void show() {
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        //防止activity被finish之前,dialog还没dismiss而报错
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            super.dismiss();
        }
    }

    public interface OnButtonClickListener {
        void left();

        void right();
    }
}
