package com.haitao.arglasses.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.haitao.arglasses.R;
import com.haitao.arglasses.databinding.ActivityMvvmBaseBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 开发人员: 周海涛
 * 开发时间: 2024年8月6日
 * Activity基类
 */
public abstract class BaseActivity<DB extends ViewDataBinding, VM extends ViewModel & LifecycleObserver> extends AppCompatActivity {
    protected final String TAG = getClass().getName();
    protected Context mContext;
    protected DB mDataBinding;
    protected VM mViewModel;
    protected ActivityMvvmBaseBinding mBaseDataBinding;

    /**
     * 初始化
     */
    protected abstract void initView();

    /**
     * setContentLayout之前调用
     */
    protected void onSetBeforeLayout() {
    }

    /**
     * @return 当前页面是否可见
     */
    protected final boolean getIsVisible() {
        return mIsVisible;
    }

    /*===============================================以下为方法实现区===============================================*/
    private boolean mIsVisible = false;   //当前页面是否可见

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        //解决修改字体之后，页面重复绘制，出现叠加
        if (savedInstanceState != null) {
            savedInstanceState = null;
        }
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mContext = this;
        onSetBeforeLayout();
        mBaseDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_mvvm_base);
        Type superclass = getClass().getGenericSuperclass();
        if (superclass != null) {
            Class<DB> aClass = null;
            Class<VM> modelClass = null;
            if (superclass instanceof ParameterizedType) {
                ParameterizedType nSuperclass = (ParameterizedType) superclass;
                aClass = (Class<DB>) nSuperclass.getActualTypeArguments()[0];
                modelClass = (Class<VM>) nSuperclass.getActualTypeArguments()[1];
            }
            try {
                if (aClass != null && mBaseDataBinding != null) {
                    Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
                    mDataBinding = (DB) method.invoke(null, getLayoutInflater());
                    if (mDataBinding != null) {
                        mBaseDataBinding.flBaseContainer.addView(mDataBinding.getRoot());
                        mDataBinding.setLifecycleOwner(this);
                    }
                    if (modelClass != null) {
                        mViewModel = new ViewModelProvider(this).get(modelClass);
                        // ViewModel订阅生命周期事件
                        getLifecycle().addObserver(mViewModel);
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("未找到使用inflate方式导入的布局文件");
            }
        }
        initOther();
        initView();
    }

    private void initOther() {
        // 注册返回回调
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    finish();
                } else {
                    getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    /**
     * @param fragment 替换添加fragment
     */
    public void replaceFragment(Fragment fragment) {
        if (fragment == null) return;
        String tag = fragment.getClass().getName();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(mBaseDataBinding.flBaseContainer.getId(), fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    /**
     * @param fragment 展示fragment
     */
    public void showFragment(Fragment fragment) {
        if (fragment == null) return;
        String tag = fragment.getClass().getName();
        FragmentManager nFragmentManager = getSupportFragmentManager();
        FragmentTransaction nTransaction = nFragmentManager.beginTransaction();
        for (Fragment nFragment : nFragmentManager.getFragments()) {
            if (!nFragment.isHidden()) {
                nTransaction.hide(nFragment);
            }
        }
        if (fragment.isAdded()) {
            nTransaction.show(fragment);
        } else {
            nTransaction.add(mBaseDataBinding.flBaseContainer.getId(), fragment, tag);
        }
        nTransaction.addToBackStack(tag).commitAllowingStateLoss();
    }

    /**
     * @param fragment 隐藏fragment
     */
    public void showFragment(Class<? extends Fragment> fragment) {
        if (fragment == null) return;
        String tag = fragment.getName();
        FragmentManager nFragmentManager = getSupportFragmentManager();
        FragmentTransaction nTransaction = nFragmentManager.beginTransaction();
        Fragment nFragment = null;
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f.getClass().getName().equals(tag)) {
                nFragment = f;
                break;
            }
        }
        if (nFragment == null) return;
        for (Fragment f : nFragmentManager.getFragments()) {
            if (!nFragment.isHidden()) {
                nTransaction.hide(f);
            }
        }
        if (nFragment.isAdded()) {
            nTransaction.show(nFragment);
        } else {
            nTransaction.add(mBaseDataBinding.flBaseContainer.getId(), nFragment, tag);
        }
        nTransaction.addToBackStack(tag).commitAllowingStateLoss();
    }

    /**
     * @param fragment 隐藏fragment
     */
    public void hideFragment(Fragment fragment) {
        if (fragment == null) return;
        FragmentTransaction nTransaction = getSupportFragmentManager().beginTransaction();
        nTransaction.hide(fragment).commitAllowingStateLoss();
    }

    /**
     * @param fragment 隐藏fragment
     */
    public void hideFragment(Class<? extends Fragment> fragment) {
        if (fragment == null) return;
        for (Fragment nFragment : getSupportFragmentManager().getFragments()) {
            if (nFragment.getClass().getName().equals(fragment.getName())) {
                FragmentTransaction nTransaction = getSupportFragmentManager().beginTransaction();
                nTransaction.hide(nFragment).commitAllowingStateLoss();
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        mIsVisible = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        mIsVisible = false;
        super.onPause();
    }
}
