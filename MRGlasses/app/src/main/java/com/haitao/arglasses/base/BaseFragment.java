package com.haitao.arglasses.base;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.haitao.arglasses.R;
import com.haitao.arglasses.base.utils.LogUtil;
import com.haitao.arglasses.databinding.ActivityMvvmBaseBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 开发人员：周海涛
 * 文档说明：Fragment基类
 */
public abstract class BaseFragment<DB extends ViewDataBinding, VM extends ViewModel & LifecycleObserver> extends Fragment {
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
     * 展示内部子fragment(调用showChildFragment()之前必须重写此方法)
     */
    protected int setFragmentLayoutId() {
        return 0;
    }

    /**
     * @param fragment 展示内部子fragment
     */
    protected void showChildFragment(Fragment fragment) {
        if (fragment == null) return;
        if (setFragmentLayoutId() == 0) {
            throw new NullPointerException("请先重写getFragmentLayoutId()且保证布局内包含FragmentLayout");
        }
        FragmentManager nFragmentManager = getChildFragmentManager();
        FragmentTransaction nTransaction = nFragmentManager.beginTransaction();
        for (Fragment nFragment : nFragmentManager.getFragments()) {
            if (!nFragment.isHidden()) {
                nTransaction.hide(nFragment);
            }
        }
        if (fragment.isAdded()) {
            nTransaction.show(fragment);
        } else {
            nTransaction.add(setFragmentLayoutId(), fragment, fragment.getClass().getName());
        }
        nTransaction.addToBackStack(fragment.getClass().getName()).commitAllowingStateLoss();
    }

    /**
     * @param fragment 替换添加fragment
     */
    public void replaceFragment(Fragment fragment) {
        if (fragment == null) return;
        if (setFragmentLayoutId() == 0 && mBaseDataBinding == null) {
            throw new NullPointerException("请先重写getFragmentLayoutId()或指定DataBinding");
        }
        String tag = fragment.getClass().getName();
        FragmentTransaction nTransaction = getChildFragmentManager().beginTransaction();
        if (mBaseDataBinding == null) {
            nTransaction.replace(setFragmentLayoutId(), fragment, tag);
        } else {
            nTransaction.replace(mBaseDataBinding.flBaseContainer.getId(), fragment, tag);
        }
        nTransaction.addToBackStack(tag).commitAllowingStateLoss();
    }

    /**
     * @param fragment 展示内部子fragment
     */
    protected void showFragment(Fragment fragment) {
        if (fragment == null) return;
        if (setFragmentLayoutId() == 0 && mBaseDataBinding == null) {
            throw new NullPointerException("请先重写getFragmentLayoutId()或指定DataBinding");
        }
        FragmentManager nFragmentManager = getChildFragmentManager();
        FragmentTransaction nTransaction = nFragmentManager.beginTransaction();
        for (Fragment nFragment : nFragmentManager.getFragments()) {
            if (!nFragment.isHidden()) {
                nTransaction.hide(nFragment);
            }
        }
        if (fragment.isAdded()) {
            nTransaction.show(fragment);
        } else {
            if (mBaseDataBinding == null) {
                nTransaction.add(setFragmentLayoutId(), fragment, fragment.getClass().getName());
            } else {
                nTransaction.add(mBaseDataBinding.flBaseContainer.getId(), fragment, fragment.getClass().getName());
            }
        }
        nTransaction.addToBackStack(fragment.getClass().getName()).commitAllowingStateLoss();
    }

    /**
     * @param fragment 隐藏fragment
     */
    public void showFragment(Class<? extends Fragment> fragment) {
        if (fragment == null) return;
        if (setFragmentLayoutId() == 0 && mBaseDataBinding == null) {
            throw new NullPointerException("请先重写getFragmentLayoutId()或指定DataBinding");
        }
        String tag = fragment.getName();
        FragmentManager nFragmentManager = getChildFragmentManager();
        FragmentTransaction nTransaction = nFragmentManager.beginTransaction();
        Fragment nFragment = null;
        for (Fragment f : getChildFragmentManager().getFragments()) {
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
            if (mBaseDataBinding == null) {
                nTransaction.add(setFragmentLayoutId(), nFragment, tag);
            } else {
                nTransaction.add(mBaseDataBinding.flBaseContainer.getId(), nFragment, tag);
            }
        }
        nTransaction.addToBackStack(tag).commitAllowingStateLoss();
    }

    /**
     * @param fragment 隐藏fragment
     */
    public void hideFragment(Fragment fragment) {
        if (fragment == null) return;
        FragmentTransaction nTransaction = getChildFragmentManager().beginTransaction();
        nTransaction.hide(fragment).commitAllowingStateLoss();
    }

    /**
     * @param fragment 隐藏fragment
     */
    public void hideFragment(Class<? extends Fragment> fragment) {
        if (fragment == null) return;
        for (Fragment nFragment : getChildFragmentManager().getFragments()) {
            if (nFragment.getClass().getName().equals(fragment.getName())) {
                FragmentTransaction nTransaction = getChildFragmentManager().beginTransaction();
                nTransaction.hide(nFragment).commitAllowingStateLoss();
                break;
            }
        }
    }

    /**
     * @return 当前页面是否可见
     */
    protected final boolean getIsVisible() {
        return mShowHideVisible;
    }

    /*===============================================以下为方法实现区===============================================*/
    private boolean mShowHideVisible = true;//Show()Hide()Fragment是否可见

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        onSetBeforeLayout();
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
                if (aClass != null) {
                    Method method = aClass.getDeclaredMethod("inflate", LayoutInflater.class);
                    mDataBinding = (DB) method.invoke(null, getLayoutInflater());
                    if (mDataBinding != null) {
                        mBaseDataBinding = DataBindingUtil.inflate(inflater, R.layout.activity_mvvm_base, container, false);
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
        return null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mShowHideVisible = !hidden;
        LogUtil.d(TAG, "show()或hide()执行回调是否显示 : " + !hidden);
    }
}
