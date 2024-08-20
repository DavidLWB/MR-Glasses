package com.haitao.arglasses.base;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.haitao.arglasses.base.utils.LogUtil;
import com.haitao.arglasses.base.utils.ToastUtil;

/**
 * 开发人员：周海涛
 * 文档说明：处理按键切换焦点
 */
public class KeystrokeManager {
    private final AppCompatActivity mActivity;
    private float mStartX, mStartY, mEndX, mEndY;

    public KeystrokeManager(AppCompatActivity activity) {
        mActivity = activity;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.ACTION_DOWN) {
            LogUtil.d("系统-按键触发", "KeyCode: " + keyCode);
            ToastUtil.showDebug("按键触发:" + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP://19
                    moveFocusUp();
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN://20
                    moveFocusDown();
                    return true;
                case KeyEvent.KEYCODE_DPAD_LEFT://21
                    moveFocusLeft();
                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT://22
                    moveFocusRight();
                    return true;
                case KeyEvent.KEYCODE_ENTER://66
                    clickView();
                    return true;
            }
        }
        return false;
    }

    public int onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                mEndX = event.getX();
                mEndY = event.getY();
                mEndX = mEndX - mStartX;
                mEndY = mEndY - mStartY;
                mStartX = Math.abs(mEndX);
                mStartY = Math.abs(mEndY);
                if (mStartX > mStartY) {
                    //横滑
                    if (mStartX < 100) {
                        //横滑距离不足判定为点击
                        return KeyEvent.KEYCODE_ENTER;
                    } else {
                        if (mEndX > 0) {
                            //右滑
                            return KeyEvent.KEYCODE_DPAD_RIGHT;
                        } else {
                            //左滑
                            return KeyEvent.KEYCODE_DPAD_LEFT;
                        }
                    }
                } else {
                    //竖滑
                    if (mStartY < 100) {
                        //竖滑距离不足判定为点击
                        return KeyEvent.KEYCODE_ENTER;
                    } else {
                        if (mEndY > 0) {
                            //下滑
                            return KeyEvent.KEYCODE_DPAD_DOWN;
                        } else {
                            //上滑
                            return KeyEvent.KEYCODE_DPAD_UP;
                        }
                    }
                }
        }
        return -1;
    }

    public void moveFocusUp() {
        View focusedView = mActivity.getCurrentFocus();
        if (focusedView != null) {
            View nextFocus = focusedView.focusSearch(View.FOCUS_UP);
            if (nextFocus != null) {
                nextFocus.requestFocus();
            }
        }
    }

    public void moveFocusDown() {
        View focusedView = mActivity.getCurrentFocus();
        if (focusedView != null) {
            View nextFocus = focusedView.focusSearch(View.FOCUS_DOWN);
            if (nextFocus != null) {
                nextFocus.requestFocus();
            }
        }
    }

    public void moveFocusLeft() {
        View focusedView = mActivity.getCurrentFocus();
        if (focusedView != null) {
            View nextFocus = focusedView.focusSearch(View.FOCUS_LEFT);
            if (nextFocus != null) {
                nextFocus.requestFocus();
            }
        }
    }

    public void moveFocusRight() {
        View focusedView = mActivity.getCurrentFocus();
        if (focusedView != null) {
            View nextFocus = focusedView.focusSearch(View.FOCUS_RIGHT);
            if (nextFocus != null) {
                nextFocus.requestFocus();
            }
        }
    }

    public void clickView() {
        View focusedView = mActivity.getCurrentFocus();
        if (focusedView != null) {
            focusedView.performClick();
        }
    }
}
