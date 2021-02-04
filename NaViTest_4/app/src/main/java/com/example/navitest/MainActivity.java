package com.example.navitest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.RotateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.RouteSearch;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import androidx.appcompat.widget.Toolbar;

/**
 * 高德地图Demo
 * @author DonStark
 */
public class MainActivity extends AppCompatActivity implements
        AMapLocationListener, LocationSource, PoiSearch.OnPoiSearchListener,
        AMap.OnMapClickListener, AMap.OnMapLongClickListener, GeocodeSearch.OnGeocodeSearchListener,
        EditText.OnKeyListener, AMap.OnMarkerClickListener, AMap.OnMarkerDragListener,
        AMap.InfoWindowAdapter{

    //内容
    //private TextView tvContent;

    //搜索框
    private EditText etAddress;
    //地理编码搜索
    private GeocodeSearch geocodeSearch;
    //解析成功标志码
    private static final int PARSE_SUCCESS_CODE = 1000;
    //POI查询对象
    private PoiSearch.Query query;
    //POI搜索对象
    private PoiSearch poiSearch;
    //城市码
    private String cityCode = null;
    //浮动按钮
    private FloatingActionButton fabPOI;
    //地图控制器
    private AMap aMap = null;
    //定义一个UiSetting对象
    private UiSettings mUiSettings;
    //位置更新监听
    private LocationSource.OnLocationChangedListener mListener;
    //定义样式
    private MyLocationStyle myLocationStyle = new MyLocationStyle();

    private MapView mapView;
    //请求权限码
    private static final int REQUEST_PERMISSIONS = 9527;
    //声明MapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption类对象
    public AMapLocationClientOption mLocationOption = null;
    //浮动按钮，清空地图标记
    private FloatingActionButton fabClearMarker;

    //标记列表
    private List<Marker> markerList = new ArrayList<>();

    //切换卫星图层按钮
    private FloatingActionButton fabToSatelliteLayer,fabToNightLayer,fabToDayLayer;
    //转到导航Activity
    private Button mBtnNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map_view);
        fabPOI = findViewById(R.id.fab_poi);
        etAddress = findViewById(R.id.et_address);
        fabClearMarker = findViewById(R.id.fab_clear_marker);
        fabToSatelliteLayer = findViewById(R.id.fab_satellite);
        fabToNightLayer = findViewById(R.id.fab_nightMode);
        fabToDayLayer = findViewById(R.id.fab_dayMode);
        mBtnNavigation = findViewById(R.id.btn_toNavigation);

        mapView.onCreate(savedInstanceState);

        etAddress.setOnKeyListener((View.OnKeyListener) this);

        //tvContent = findViewById(R.id.tv_content);

        //初始化定位
        initLocation();

        //初始化地图
        initMap(savedInstanceState);

        //检查Android版本
        checkingAndroidVersion();

        fabToSatelliteLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
            }
        });
        fabToNightLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);
            }
        });
        fabToDayLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            }
        });
        mBtnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NavigationActivity.class);
                startActivity(intent);
            }
        });

    }

    //检查Android版本
    private void checkingAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Android6.0及以上先获取权限在定位
            requestPermission();
        } else {
            //Android6.0以下直接定位
            //启动定位
            mLocationClient.startLocation();
        }
    }

    //动态权限请求
    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    private void requestPermission() {
        String[] permission = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (EasyPermissions.hasPermissions(this, permission)) {//true有权限，开始定位
            showMsg("已获得权限，开始定位！");
            mLocationClient.startLocation();
        } else {//false无权限
            EasyPermissions.requestPermissions(this, "需要权限！", REQUEST_PERMISSIONS, permission);
        }
    }

    //请求权限结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //设置请求权限结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //封装Toast提示方法
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //初始化定位
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener((AMapLocationListener) this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取最近3s内精度最高的一次定位结果
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //定位请求超时时间，默认3000毫秒
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制，高精度定位会产生缓存
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    //接受异步返回的定位结果
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //地址
                String address = aMapLocation.getAddress();
                double latitude = aMapLocation.getLatitude();
                double longitude = aMapLocation.getLongitude();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("纬度：").append(latitude).append("\n");
                stringBuffer.append("经度：").append(longitude).append("\n");
                stringBuffer.append("地址：").append(address).append("\n");

                Log.d("MainActivity", stringBuffer.toString());
                showMsg(address);

                mLocationClient.stopLocation();

                //显示地图定位结果
                if (mListener != null) {
                    //显示系统图标
                    mListener.onLocationChanged(aMapLocation);
                }
                //显示浮动按钮
                fabPOI.show();
                //赋值
                cityCode = aMapLocation.getCityCode();
                //tvContent.setText(stringBuffer.toString());
            } else {
                //定位失败时，通过ErrorCode错误码信息来确定失败的原因，errInfo是错误信息
                Log.e("AMapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    //增加地图生命周期的管理方法
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    //初始化地图
    private void initMap(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        //在activity执行onCreate时执行该语句时，创建地图
        mapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        aMap = mapView.getMap();
        //设置缩放等级
        aMap.setMinZoomLevel(12);

        // 设置定位监听
        aMap.setLocationSource((LocationSource) this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位曾并不可触发定位，默认是false

        //设置地图点击事件
        aMap.setOnMapClickListener((AMap.OnMapClickListener) this);
        //设置地图长按事件
        aMap.setOnMapLongClickListener((AMap.OnMapLongClickListener) this);
        //设置marker点击事件
        aMap.setOnMarkerClickListener((AMap.OnMarkerClickListener) this);

        //设置InfoWindowAdapter监听
        aMap.setInfoWindowAdapter(this);

        //构造GeocodeSearch对象
        geocodeSearch = new GeocodeSearch(this);
        //设置监听
        geocodeSearch.setOnGeocodeSearchListener((GeocodeSearch.OnGeocodeSearchListener) this);

        aMap.setMyLocationEnabled(true);
        //定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_icon));
        //精度范围的圆形边框颜色，都为0则透明
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        //精度范围的圆形边框宽度 0 无宽度
        myLocationStyle.strokeWidth(0);
        //圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));

        //设置该蓝点的style
        aMap.setMyLocationStyle(myLocationStyle);

        //实例化UiSettings对象
        mUiSettings = aMap.getUiSettings();
        //隐藏缩放按钮
        mUiSettings.setZoomControlsEnabled(false);
        //默认不显示比例尺
        mUiSettings.setScaleControlsEnabled(false);

        //开启室内地图
        aMap.showIndoorMap(true);
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient.startLocation();// 启动定位
        }
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    //浮动按钮点击查询附近POI
    public void queryPOI(View view) {
        //构造query对象
        query = new PoiSearch.Query("购物", "", cityCode);
        //设置每页最多返回多少条poiItem
        query.setPageSize(10);
        //设置查询页码
        query.setPageNum(1);
        //构造PoiSearch对象
        poiSearch = new PoiSearch(this, query);
        //设置搜索监听回调
        poiSearch.setOnPoiSearchListener((PoiSearch.OnPoiSearchListener) this);
        //发起搜索附近POI异步请求
        poiSearch.searchPOIAsyn();
        showMsg("兴趣点已在控制台输出");
    }

    //POI搜索返回
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        //解析result获取POI信息

        //获取POI数组列表
        ArrayList<PoiItem> poiItems = poiResult.getPois();
        for (PoiItem poiItem : poiItems) {
            Log.d("MainActivity", " Title: " + poiItem.getTitle() + " Snippet: " + poiItem.getSnippet());
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        addMarker(latLng);
        latLonToAddress(latLng);
        updateMapCenter(latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        showMsg("该点的坐标为\n经度：" + latLng.latitude + "\n纬度" + latLng.longitude + "\n若要添加标记并查看地址，请点按");
        //aMap.addMarker(new MarkerOptions().position(latLng).snippet("DefaultMarker"));

    }

    //坐标转地址
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
        //解析result获取地址描述信息
        if (rCode == PARSE_SUCCESS_CODE) {
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            //显示解析后的地址
            showMsg("地址： " + regeocodeAddress.getFormatAddress());
        } else {
            showMsg("地址解析失败！");
        }
    }

    //地址转坐标
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
        if (rCode == PARSE_SUCCESS_CODE) {
            List<GeocodeAddress> geocodeAddressList = geocodeResult.getGeocodeAddressList();
            if (geocodeAddressList != null && geocodeAddressList.size() > 0) {
                LatLonPoint latLonPoint = geocodeAddressList.get(0).getLatLonPoint();
                //显示解析后的坐标
                showMsg("坐标： " + latLonPoint.getLongitude() + "，" + latLonPoint.getLatitude());
            }
        } else {
            showMsg("获取坐标失败!");
        }
    }

    //通过经纬度获取地址
    private void latLonToAddress(LatLng latLng) {
        //位置点
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        //逆编码查询
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 20, GeocodeSearch.AMAP);
        //异步获取地址信息
        geocodeSearch.getFromLocationAsyn(query);
    }

    //搜索框键盘点击
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            //获取输入框的内容
            //trim()方法trim掉了字符串两端Unicode编码小于等于32（\u0020）的所有字符
            String address = etAddress.getText().toString().trim();
            if (address == null || address.isEmpty()) {
                showMsg("请输入地址");
            } else {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                //name表示地址
                GeocodeQuery query = new GeocodeQuery(address, "江苏");
                geocodeSearch.getFromLocationNameAsyn(query);
            }
            return true;
        }
        return false;
    }

    //添加地图标记
    private void addMarker(LatLng latLng) {
        //显示浮动按钮
        fabClearMarker.show();
        //添加标记
        Marker marker = aMap.addMarker(new MarkerOptions()
                .draggable(true)
                .position(latLng)
                .title("标题")
                .snippet("详细信息"));

        //绘制marker时就显示InfoWindow
        //marker.showInfoWindow();

        //设置标记的绘制动画效果
        Animation animation = new RotateAnimation(marker.getRotateAngle() + 180, marker.getRotateAngle(), 0, 0, 0);
        long duration = 1000L;
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());

        marker.setAnimation(animation);
        marker.startAnimation();

        markerList.add(marker);
    }

    //清空地图Marker
    public void clearAllMarker(View view) {
        if (markerList != null && markerList.size() > 0) {
            for (Marker markerItem : markerList) {
                markerItem.remove();
            }
        }
        fabClearMarker.hide();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //显示InfoWindow
        if (!marker.isInfoWindowShown()) {
            //显示
            marker.showInfoWindow();
        }else {
            //隐藏
            marker.hideInfoWindow();
        }
        return true;
    }


    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d("MainActivity", "开始拖动");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d("MainActivity", "拖动中");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d("MainActivity", "拖动完成");
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window,null);
        render(marker,infoWindow);
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View infoContent = getLayoutInflater().inflate(R.layout.custom_info_contents,null);
        render(marker,infoContent);
        return infoContent;
    }

    private void render(Marker marker, View view) {
        ((ImageView)view.findViewById(R.id.badge)).setImageResource(R.drawable.icon_twinkle);

        //修改InfoWindow标题内容样式
        String title = marker.getTitle();
        TextView titleUi = ((TextView)view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED),0,titleText.length(),0);
            titleUi.setTextSize(15);
            titleUi.setText(titleText);
        }else {
            titleUi.setText("");
        }
        //修改InfoWindow片段内容样式
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(20);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
    }

    //更新地图中心点的方法
    private void updateMapCenter(LatLng latLng) {
        //CameraPosition 第一个参数：目标位置中心点的经纬度坐标
        //CameraPosition 第二个参数：目标可视区域的缩放级别
        //CameraPosition 第三个参数：目标可视区域的倾斜度，以角度为单位
        //CameraPosition 第四个参数：可视区域指定的方向，以角度为单位，从正北顺时针方向计算，从0-360
        CameraPosition cameraPosition = new CameraPosition(latLng,16,30,0);
        //位置变更
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        //改变位置
        aMap.animateCamera(cameraUpdate);
    }






}
