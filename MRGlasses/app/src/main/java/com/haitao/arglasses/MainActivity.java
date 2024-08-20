package com.haitao.arglasses;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter4.BaseQuickAdapter;
import com.chad.library.adapter4.viewholder.QuickViewHolder;
import com.haitao.arglasses.base.BaseActivity;
import com.haitao.arglasses.base.BaseViewModel;
import com.haitao.arglasses.base.KeystrokeManager;
import com.haitao.arglasses.base.utils.JsonUtil;
import com.haitao.arglasses.base.utils.LogUtil;
import com.haitao.arglasses.base.utils.StringUtils;
import com.haitao.arglasses.base.utils.ToastUtil;
import com.haitao.arglasses.databinding.ActivityMainBinding;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import cn.xz.mylibrary.GalleryLayoutManager;

public class MainActivity extends BaseActivity<ActivityMainBinding, BaseViewModel> {
    private final int mUpDataTime = 1000;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == mUpDataTime) {
                updateSystemTime();
            } else if (msg.what == 2000) {
                String content = (String) msg.obj;
                if (content.startsWith("Notice:")) {
                    sendNotification(content.replace("Notice:", ""));
                } else {
                    mDataBinding.tvNoteContent.setText(content);
                }
            }
        }
    };

    private final BroadcastReceiver mSystemReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_CHANGED://系统时间改变
                    updateDate();
                    break;
                case Intent.ACTION_BATTERY_CHANGED://电量变化
                    updateSystemElectricity(intent);
                    break;
                case WifiManager.WIFI_STATE_CHANGED_ACTION://wifi开关状态
                    updateSystemWifiState(intent);
                    break;
                case WifiManager.RSSI_CHANGED_ACTION://wifi信号状态
                    changeWIFISignal();
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED://蓝牙状态变化
                    changeBluetoothState(intent);
                    break;
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED://蓝牙连接状态变化
                    changeBluetoothConnectState(intent);
                    break;
                default:
                    break;
            }
        }
    };

    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private String mNowHour, mNewHour, mNowMinute;
    private int mNowSelectedPosition = 0;
    private BaseQuickAdapter<MainMenuBean, QuickViewHolder> mAdapter;
    private GalleryLayoutManager mLayoutManager;
    private List<MainMenuBean> mMenuList;
    private BluetoothAdapter mBluetoothAdapter;
    private AcceptThread mThread;
    private static final String CHANNEL_ID = "channel_id";
    private static final int NOTIFICATION_ID = 100;

    @Override
    protected void initView() {
        requestPermission();
        mHandler.sendEmptyMessage(mUpDataTime);
        initMenu();
        mKeystrokeManager = new KeystrokeManager(this);
        mDataBinding.llHome.setVisibility(View.VISIBLE);
        mDataBinding.llMenuInfo.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMenu() {
        mMenuList = new ArrayList<>();
        mMenuList.add(new MainMenuBean(Constant.Home));
        mMenuList.add(new MainMenuBean(Constant.Music));
        mMenuList.add(new MainMenuBean(Constant.Video));
        mMenuList.add(new MainMenuBean(Constant.Camera));
        mMenuList.add(new MainMenuBean(Constant.Navigation));
        mMenuList.add(new MainMenuBean(Constant.Translation));
        mMenuList.add(new MainMenuBean(Constant.Measurement));
        mMenuList.add(new MainMenuBean(Constant.Notice, ContextCompat.getDrawable(mContext, R.mipmap.icon_notice2)));
        mMenuList.add(new MainMenuBean(Constant.Note, ContextCompat.getDrawable(mContext, R.mipmap.icon_note1)));
        mMenuList.add(new MainMenuBean(Constant.Setup));
        List<ApplicationInfo> apps = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : apps) {
            LogUtil.d("本机应用列表", "应用信息: " + JsonUtil.toJson(appInfo));
            String packageName = appInfo.packageName;
            switch (packageName) {
                case "com.huawei.ar.measure":
                    updateMenuData(Constant.Measurement, appInfo);
                    break;
                case "org.oxycblt.auxio":
                    updateMenuData(Constant.Music, appInfo);
                    break;
                case "com.android.camera":
                    updateMenuData(Constant.Camera, appInfo);
                    break;
                case "com.microsoft.translator":
                    updateMenuData(Constant.Translation, appInfo);
                    break;
                case "org.videolan.vlc":
                    updateMenuData(Constant.Video, appInfo);
                    break;
                case "com.android.setting":
                case "com.android.settings":
                    updateMenuData(Constant.Setup, appInfo);
                    break;
                case "com.autonavi.minimap":
                    updateMenuData(Constant.Navigation, appInfo);
                    break;
                default:
                    break;
            }
        }
        mLayoutManager = new GalleryLayoutManager(RecyclerView.VERTICAL);
        mLayoutManager.attach(mDataBinding.rvMainMenu);
        mLayoutManager.setItemTransformer((layoutManager, item, fraction) -> {
            item.setPivotX(0);
            item.setPivotY(item.getHeight() / 2.0f);
            float scale = 1 - 0.3f * Math.abs(fraction);
            item.setScaleX(scale);
            item.setScaleY(scale);
        });
        mLayoutManager.setOnItemSelectedListener((recyclerView, arrayList, view, position) -> {
            mNowSelectedPosition = position;
            TextView tvMenuName = view.findViewById(R.id.tv_menu_name);
            tvMenuName.setTypeface(null, Typeface.BOLD);
            switch (mMenuList.get(position).name) {
                case Constant.Home:
                    mDataBinding.llHome.setVisibility(View.VISIBLE);
                    mDataBinding.llMenuInfo.setVisibility(View.INVISIBLE);
                    mDataBinding.llTime.setVisibility(View.INVISIBLE);
                    mDataBinding.tvNoteContent.setVisibility(View.INVISIBLE);
                    mDataBinding.llNotice.setVisibility(View.INVISIBLE);
                    break;
                case Constant.Note:
                    mDataBinding.llHome.setVisibility(View.INVISIBLE);
                    mDataBinding.llMenuInfo.setVisibility(View.INVISIBLE);
                    mDataBinding.llTime.setVisibility(View.INVISIBLE);
                    mDataBinding.tvNoteContent.setVisibility(View.VISIBLE);
                    mDataBinding.llNotice.setVisibility(View.INVISIBLE);
                    break;
                case Constant.Notice:
                    mDataBinding.llHome.setVisibility(View.INVISIBLE);
                    mDataBinding.llMenuInfo.setVisibility(View.INVISIBLE);
                    mDataBinding.llTime.setVisibility(View.INVISIBLE);
                    mDataBinding.tvNoteContent.setVisibility(View.INVISIBLE);
                    if (!StringUtils.isEmpty(mDataBinding.tvNoticeContent.getText().toString())) {
                        mDataBinding.llNotice.setVisibility(View.VISIBLE);
                        break;
                    }
                default:
                    mDataBinding.llHome.setVisibility(View.INVISIBLE);
                    mDataBinding.tvNoteContent.setVisibility(View.INVISIBLE);
                    mDataBinding.llNotice.setVisibility(View.INVISIBLE);
                    mDataBinding.llMenuInfo.setVisibility(View.VISIBLE);
                    MainMenuBean nBean = mMenuList.get(position);
                    mDataBinding.ivMenuLogo.setImageDrawable(nBean.icon);
                    mDataBinding.tvMenuName.setText(nBean.name);
                    mDataBinding.tvMenuInfo.setText(nBean.info);
                    mDataBinding.llTime.setVisibility(View.VISIBLE);
                    break;
            }
        });
        mDataBinding.rvMainMenu.setOnTouchListener((v, event) -> true);
        mDataBinding.rvMainMenu.setAdapter(getMenuAdapter());
        mDataBinding.rvMainMenu.requestFocus();
    }

    private void updateMenuData(String menuName, ApplicationInfo appInfo) {
        List<ResolveInfo> activities;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager nPackageManager = getPackageManager();
        for (MainMenuBean nBean : mMenuList) {
            if (nBean.name.equals(menuName)) {
                nBean.packageName = appInfo.packageName;
                intent.setPackage(nBean.packageName);
                activities = nPackageManager.queryIntentActivities(intent, 0);
                for (ResolveInfo activity : activities) {
                    if (activity.activityInfo.packageName.equals(nBean.packageName)) {
                        nBean.activityName = activity.activityInfo.name;
                        break;
                    }
                }
                nBean.icon = nPackageManager.getApplicationIcon(appInfo);
                break;
            }
        }
    }

    private void requestPermission() {
        XXPermissions.with(this)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.BLUETOOTH_CONNECT)
                .permission(Permission.BLUETOOTH_ADVERTISE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                        if (allGranted) {
                            initListener();
                        } else {
                            ToastUtil.showLong("权限是必须权限,请同意后继续!");
                        }
                    }

                    @Override
                    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                        ToastUtil.showLong("权限是必须权限,请同意后继续!");
                        if (doNotAskAgain) {
                            XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                        } else {
                            requestPermission();
                        }
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void initListener() {
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        mNetworkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                mDataBinding.ivSystemWifi.setVisibility(View.VISIBLE);
                changeWIFISignal();
            }

            @Override
            public void onLost(@NonNull Network network) {
                mDataBinding.ivSystemWifi.setVisibility(View.GONE);
                LogUtil.d("系统-网络变化", "当前无可用WIFI");
            }
        };
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        mConnectivityManager.requestNetwork(builder.build(), mNetworkCallback);
        if (mWifiManager.isWifiEnabled()) {
            changeWIFISignal();
        } else {
            mDataBinding.ivSystemWifi.setVisibility(View.GONE);
            LogUtil.d("系统-网络变化", "当前WIFI不可用1");
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mDataBinding.ivSystemBluetooth.setVisibility(View.VISIBLE);
            LogUtil.d("系统-蓝牙变化", "蓝牙已开启");
            Set<BluetoothDevice> nDevices = mBluetoothAdapter.getBondedDevices();
            mDataBinding.ivSystemBluetooth.setSelected(!nDevices.isEmpty());
            openBluetoothServer();
        } else {
            mDataBinding.ivSystemBluetooth.setVisibility(View.GONE);
            LogUtil.d("系统-蓝牙变化", "蓝牙已关闭");
        }

        IntentFilter nIntentFilter = new IntentFilter();
        nIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);//时间
        nIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);//电量
        nIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//WIFI开关状态
        nIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);//WIFI信号变化
        nIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙状态变化
        nIntentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);//蓝牙连接变化
        registerReceiver(mSystemReceiver, nIntentFilter);
    }

    private BaseQuickAdapter<MainMenuBean, QuickViewHolder> getMenuAdapter() {
        mAdapter = new BaseQuickAdapter<MainMenuBean, QuickViewHolder>(mMenuList) {

            @NonNull
            @Override
            protected QuickViewHolder onCreateViewHolder(@NonNull Context context, @NonNull ViewGroup viewGroup, int i) {
                return new QuickViewHolder(R.layout.item_main_menu, viewGroup);
            }

            @Override
            protected void onBindViewHolder(@NonNull QuickViewHolder viewHolder, int position, @NonNull MainMenuBean bean) {
                TextView tvMenuName = viewHolder.getView(R.id.tv_menu_name);
                tvMenuName.setText(bean.name);
                tvMenuName.setTypeface(null, Typeface.NORMAL);
            }
        };
        return mAdapter;
    }

    private void changeBluetoothConnectState(Intent intent) {
        int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED);
        switch (state) {
            case BluetoothProfile.STATE_CONNECTING:
                LogUtil.d("系统-蓝牙变化", "蓝牙连接中: " + state);
                mDataBinding.ivSystemBluetooth.setVisibility(View.VISIBLE);
                break;
            case BluetoothProfile.STATE_CONNECTED:
                LogUtil.d("系统-蓝牙变化", "蓝牙已连接: " + state);
                mDataBinding.ivSystemBluetooth.setVisibility(View.VISIBLE);
                mDataBinding.ivSystemBluetooth.setSelected(true);
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
                LogUtil.d("系统-蓝牙变化", "蓝牙已断开: " + state);
                mDataBinding.ivSystemBluetooth.setVisibility(View.VISIBLE);
                mDataBinding.ivSystemBluetooth.setSelected(false);
                break;
            case BluetoothProfile.STATE_DISCONNECTING:
                LogUtil.d("系统-蓝牙变化", "蓝牙断开中: " + state);
                mDataBinding.ivSystemBluetooth.setVisibility(View.VISIBLE);
                break;
            default:
                LogUtil.d("系统-蓝牙变化", "未知连接状态: " + state);
                mDataBinding.ivSystemBluetooth.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void changeBluetoothState(Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
        switch (state) {
            case BluetoothAdapter.STATE_ON:
                LogUtil.d("系统-蓝牙变化", "蓝牙已开启: " + state);
                mDataBinding.ivSystemBluetooth.setVisibility(View.VISIBLE);
                openBluetoothServer();
                break;
            case BluetoothAdapter.STATE_OFF:
                LogUtil.d("系统-蓝牙变化", "蓝牙已关闭: " + state);
                mDataBinding.ivSystemBluetooth.setVisibility(View.GONE);
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                LogUtil.d("系统-蓝牙变化", "蓝牙开启中: " + state);
                mDataBinding.ivSystemBluetooth.setVisibility(View.GONE);
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                LogUtil.d("系统-蓝牙变化", "蓝牙关闭中: " + state);
                mDataBinding.ivSystemBluetooth.setVisibility(View.GONE);
                break;
            default:
                LogUtil.d("系统-蓝牙变化", "未知状态: " + state);
                mDataBinding.ivSystemBluetooth.setVisibility(View.GONE);
                break;
        }
    }

    @SuppressLint("MissingPermission")
    private void openBluetoothServer() {
        mThread = new AcceptThread();
        mThread.start();
    }

    private void changeWIFISignal() {
        WifiInfo nInfo = mWifiManager.getConnectionInfo();
        if (nInfo == null) {
            mDataBinding.ivSystemWifi.setVisibility(View.GONE);
            LogUtil.d("系统-网络变化", "当前WIFI未连接");
        } else {
            int rssi = mWifiManager.getConnectionInfo().getRssi();
            // 0 格对应RSSI（‌Received Signal Strength Indicator）‌小于等于 - 100d bm。‌
            // 1 格对应RSSI在 - 100d bm到 - 88d bm之间。‌
            // 2 格对应RSSI在 - 88d bm到 - 77d bm之间。‌
            // 3 格对应RSSI在 - 66d bm到 - 55d bm之间。‌
            // 4 格对应RSSI大于等于 - 55d bm。‌
            LogUtil.d("系统-网络变化", "当前WIFI信号强度: " + rssi);
            if (rssi <= -100) {
                mDataBinding.ivSystemWifi.setVisibility(View.GONE);
            } else {
                mDataBinding.ivSystemWifi.setVisibility(View.VISIBLE);
                if (rssi < -88) {
                    mDataBinding.ivSystemWifi.setImageLevel(1);
                } else if (rssi < -77) {
                    mDataBinding.ivSystemWifi.setImageLevel(2);
                } else if (rssi < -55) {
                    mDataBinding.ivSystemWifi.setImageLevel(3);
                } else {
                    mDataBinding.ivSystemWifi.setImageLevel(4);
                }
            }
        }
    }

    private void updateSystemWifiState(Intent intent) {
        int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
        if (state == WifiManager.WIFI_STATE_ENABLED) {
            changeWIFISignal();
        } else if (state == WifiManager.WIFI_STATE_DISABLED) {
            mDataBinding.ivSystemWifi.setVisibility(View.GONE);
            LogUtil.d("系统-网络变化", "当前WIFI不可用2");
        }
    }

    private void updateSystemElectricity(Intent intent) {
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int electricity = (int) (level * 100f / scale);
        LogUtil.d("系统-电量变化", "当前设备电量: " + electricity);
        mDataBinding.tvSystemElectricity.setText(String.valueOf(electricity));
    }

    @SuppressLint("SetTextI18n")
    private void updateSystemTime() {
        LocalTime now = LocalTime.now();
        mNewHour = now.getHour() < 10 ? "0" + now.getHour() : now.getHour() + "";
        mNowMinute = now.getMinute() < 10 ? "0" + now.getMinute() : now.getMinute() + "";
        mDataBinding.tvSystemTime1.setText(mNowHour == null ? "--" : mNowHour);
        mDataBinding.tvSystemTime2.setText(mNowMinute == null ? "--" : mNowMinute);
        mDataBinding.tvSystemHomeTime.setText(((mNowHour == null) ? "--" : mNowHour) + ":" + mNowMinute);
        if (!mNewHour.equals(mNowHour)) {
            mNowHour = mNewHour;
            updateDate();
        }
        mHandler.sendEmptyMessageDelayed(mUpDataTime, mUpDataTime);
    }

    @SuppressLint("SetTextI18n")
    private void updateDate() {
        // 获取年月日
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 月份是从0开始的，所以需要加1
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDataBinding.tvSystemDate.setText(year + "年" + month + "月" + day + "日");
        mDataBinding.tvSystemWeek.setText(DateFormat.format("EEEE", calendar).toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSystemReceiver);
        mHandler.removeMessages(mUpDataTime);
        mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
        if (mThread != null) mThread.close();
    }

    private KeystrokeManager mKeystrokeManager;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event == null || event.getKeyCode() == KeyEvent.ACTION_DOWN) {
            LogUtil.d("系统-按键触发", "KeyCode: " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP://19
                    if (mDataBinding.rvMainMenu.hasFocus()) {
                        int position = Math.max(mNowSelectedPosition - 1, 0);
                        if (position != mNowSelectedPosition) {
                            scrollToPosition(position);
                            return true;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN://20
                    if (mDataBinding.rvMainMenu.hasFocus()) {
                        int position = Math.min(mNowSelectedPosition + 1, mAdapter.getItemCount() - 1);
                        if (position != mNowSelectedPosition) {
                            scrollToPosition(position);
                            return true;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_ENTER://66
                    if (mDataBinding.rvMainMenu.hasFocus()) {
                        MainMenuBean nItem = mAdapter.getItem(mNowSelectedPosition);
                        clickIntent(nItem);
                        return true;
                    }
                    break;
            }
        }
        if (mKeystrokeManager.onKeyDown(keyCode, event)) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void clickIntent(MainMenuBean item) {
        switch (item.name) {
            case Constant.Video:
            case Constant.Music:
            case Constant.Navigation:
            case Constant.Measurement:
            case Constant.Translation:
                launchApp(item.packageName, item.activityName);
                break;
            case Constant.Setup:
                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                break;
            case Constant.Camera:
                startActivity(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
                break;
            default:
                ToastUtil.showShort("开发中");
                break;
        }
    }

    private void scrollToPosition(int position) {
        mLayoutManager.smoothScrollToPosition(mDataBinding.rvMainMenu, null, position);
    }

    private void launchApp(String packageName, String activity) {
        if (StringUtils.isEmpty(activity)) {
            ToastUtil.showShort("未找到应用:" + packageName);
            return;
        }
        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launchIntent.setClassName(packageName, activity);
        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            ToastUtil.showShort("未找到应用:" + packageName);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int keyCode = mKeystrokeManager.onTouchEvent(event);
        if (keyCode > 0) {
            onKeyDown(keyCode, null);
            return true;
        }
        return super.onTouchEvent(event);
    }

    private class AcceptThread extends Thread {
        private BluetoothServerSocket mServerSocket;

        @SuppressLint("MissingPermission")
        public AcceptThread() {
            try {
                mServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("MR", UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66"));
            } catch (IOException e) {
                LogUtil.e("蓝牙连接", "创建蓝牙服务异常:" + e.getMessage());
            }
        }

        public void run() {
            while (true) {
                BluetoothSocket socket;
                try {
                    socket = mServerSocket.accept();
                } catch (IOException e) {
                    LogUtil.e("蓝牙连接", "创建蓝牙服务异常:" + e.getMessage());
                    break;
                }
                if (socket != null) {
                    receiveMessages(socket);
                }
            }
        }

        public void close() {
            try {
                mServerSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void receiveMessages(BluetoothSocket socket) {
        try {
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                String message = new String(buffer, 0, bytesRead);
                LogUtil.d("蓝牙连接", "接收成功:" + message);
                Message nMessage = new Message();
                nMessage.what = 2000;
                nMessage.obj = message;
                mHandler.sendMessage(nMessage);
                ToastUtil.cancel();
                ToastUtil.showShort(message);
            }
        } catch (IOException e) {
            LogUtil.e("蓝牙连接", "接收失败:" + e.getMessage());
        }
    }

    @SuppressLint("NotificationPermission")
    public void sendNotification(@NonNull String content) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 创建通知渠道
        CharSequence name = "MR眼镜";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription("测试通知渠道");
        notificationManager.createNotificationChannel(channel);
        // 构建通知
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon_logo)
                .setContentTitle("通知")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        // 发送通知
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        if (mMenuList.get(mNowSelectedPosition).name.equals(Constant.Notice)) {
            mDataBinding.llHome.setVisibility(View.INVISIBLE);
            mDataBinding.llMenuInfo.setVisibility(View.INVISIBLE);
            mDataBinding.llTime.setVisibility(View.INVISIBLE);
            mDataBinding.tvNoteContent.setVisibility(View.INVISIBLE);
            mDataBinding.llNotice.setVisibility(View.VISIBLE);
            mDataBinding.tvNoticeContent.setText(content);
        }
    }
}