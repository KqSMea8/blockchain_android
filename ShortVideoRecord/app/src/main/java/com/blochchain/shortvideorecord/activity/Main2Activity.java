package com.blochchain.shortvideorecord.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.blochchain.shortvideorecord.tabpager.ControlTabFragment;
import com.blochchain.shortvideorecord.utils.LogUtils;
import com.blockchain.shortvideorecord.R;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;
import java.util.Locale;

public class Main2Activity extends BaseActivity {
    public static ControlTabFragment ctf;
    private Handler mHandler = new Handler();
    private Location mLocation;
    public static final int EDITVIDEO = 10;   //刷新作品
    public static final int REFRESHMINE = 11;  //刷新我的
    public static final int TOLOGIN = 11;  //去登录

    private boolean useThemestatusBarColor = true;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    private boolean useStatusBarColor = true;//是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            List<Address> address = getAddress(mLocation);
            if(address != null && address.size()>0){
                LogUtils.e("11111:"+address.get(0).getLocality()+"--"+this.hashCode());
            }
        }
    };

    @Override
    protected View initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = View.inflate(this, R.layout.activity_main2, null);
        setContentView(rootView);

//        setStatusBar(R.color.white);
        initFragment();

//        getLocation();
        return rootView;
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        // 1. 开启事务
        FragmentTransaction transaction = fm.beginTransaction();
        // 添加主页fragment
        ctf = new ControlTabFragment();
        transaction.replace(R.id.main_container, ctf);
        transaction.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ctf.RECORD_CODE){
            ctf.mCurrentIndex = 0;
            ctf.setChecked(0);
        }else if(requestCode == EDITVIDEO){
            if(resultCode == 200){
                if(ctf.getTabProductPager() != null){
                    ctf.getTabProductPager().initData();
                }
            }
        } else if(requestCode == REFRESHMINE){
            if(resultCode == 200){
                if(ctf.getTabMinePager() != null){
                    ctf.getTabMinePager().initData();
                }
            }
        } else if(requestCode == TOLOGIN){
            if(resultCode == 200){
                ctf.mCurrentIndex = 4;
                ctf.setChecked(4);
                if(ctf.getTabMinePager() != null){
                    ctf.getTabMinePager().initData();
                }
            }else {
                ctf.mCurrentIndex = 0;
                ctf.setChecked(0);
            }
        }
    }

    public static ControlTabFragment getCtf() {
        if(ctf==null){
            ctf = new ControlTabFragment();
        }
        return ctf;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onKeyDownBack();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    public void getLocation() {
        String locationProvider;
        //获取地理位置管理器
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }
        //获取Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            //不为空,显示地理位置经纬度
            showLocation(location);
        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
    }

    /**
     * 显示地理位置经度和纬度信息
     *
     * @param location
     */
    private void showLocation(final Location location) {
        String locationStr = "纬度：" + location.getLatitude() + "\n"
                + "经度：" + location.getLongitude();
//        LogUtils.e("111111:"+locationStr);
//        updateVersion(location.getLatitude() + "", location.getLongitude() + "");
//        thread =new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<Address> address = getAddress(location);
//                if(address != null && address.size()>0){
//                    LogUtils.e("11111:"+address.get(0).getLocality()+"--"+this.hashCode());
//                }
//            }
//        });
        mLocation = location;
        mHandler.post(mRunnable);
    }

    // 获取地址信息
    private List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            showLocation(location);
        }
    };

    public void setStatusBar(int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(colorId));//设置状态栏背景色
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
            }
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else {
//            Toast.makeText(this, "低于4.4的android系统版本不存在沉浸式状态栏", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


    }
}
