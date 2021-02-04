package com.example.navitest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.navitest.overlay.WalkRouteOverlay;
import com.example.navitest.util.MapUtil;

import static com.example.navitest.util.MapUtil.convertToLatLng;
import static com.example.navitest.util.MapUtil.convertToLatLonPoint;

public class NavigationActivity extends AppCompatActivity implements
        AMapLocationListener, LocationSource, AMap.OnMapClickListener, RouteSearch.OnRouteSearchListener {
    //地图与定位设置与MainActivity相同
    private static final String TAG = "NavigationActivity";
    private MapView mapView;
    private AMap aMap = null;
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;
    public LocationSource.OnLocationChangedListener mListener = null;
    private UiSettings mUiSettings;
    private MyLocationStyle myLocationStyle = new MyLocationStyle();

    //导航相关变量
    private LatLonPoint mStartPoint, mEndPoint;
    private RouteSearch routeSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        initLocation();

        initMap(savedInstanceState);
        //启动定位
        mLocationClient.startLocation();

    }

    //初始化定位
    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener((AMapLocationListener) this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setHttpTimeOut(20000);
        mLocationOption.setLocationCacheEnable(false);
        mLocationClient.setLocationOption(mLocationOption);
    }

    //初始化地图
    private void initMap(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        aMap = mapView.getMap();
        //设置最小缩放等级为16 ，缩放级别范围为[3, 20]
        aMap.setMinZoomLevel(16);
        //开启室内地图
        aMap.showIndoorMap(true);
        //实例化UiSettings类对象
        mUiSettings = aMap.getUiSettings();
        //隐藏缩放按钮 默认显示
        mUiSettings.setZoomControlsEnabled(false);
        //显示比例尺 默认不显示
        mUiSettings.setScaleControlsEnabled(true);
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_icon));
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        // 设置定位监听
        aMap.setLocationSource((LocationSource) this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        //地图点击监听
        aMap.setOnMapClickListener((AMap.OnMapClickListener) this);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //地址
                String address = aMapLocation.getAddress();
                //获取纬度
                double latitude = aMapLocation.getLatitude();
                //获取经度
                double longitude = aMapLocation.getLongitude();
                Log.d(TAG, aMapLocation.getCity());
                Log.d(TAG, address);

                mStartPoint = convertToLatLonPoint(new LatLng(latitude, longitude));

                mLocationClient.stopLocation();

                //显示地图定位结果
                if (mListener != null) {
                    // 显示系统图标
                    mListener.onLocationChanged(aMapLocation);
                }

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("aMapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient.startLocation();//启动定位
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    //生命管理周期
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁定位客户端，同时销毁本地定位服务。
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
        mapView.onDestroy();
    }

    //设置终点
    @Override
    public void onMapClick(LatLng latLng) {
        //点击的位置是终点
        mEndPoint = convertToLatLonPoint(latLng);
        initRoute();
        //开始步行路线规划
        startRouteSearch();
    }

    //初始化路线
    private void initRoute() {
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
    }

    //开始路线搜索
    private void startRouteSearch() {
        //起点
        aMap.addMarker(new MarkerOptions()
                .position(convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_startpoint)));
        //终点
        aMap.addMarker(new MarkerOptions()
                .position(convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_endpoint)));
        //搜索路线
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        //构建步行路线搜索对象
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
        //异步路径规划步行模式查询
        routeSearch.calculateWalkRouteAsyn(query);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    /**
     * day4完成步行路线规划
     *
     * @param walkRouteResult 结果
     * @param i               结果码
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
        aMap.clear();
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (walkRouteResult != null && walkRouteResult.getPaths() != null) {
                if (walkRouteResult.getPaths().size() > 0) {
                    final WalkPath walkPath = walkRouteResult.getPaths().get(0);
                    if (walkPath == null) {
                        return;
                    }
                    //绘制路线
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            walkRouteResult.getStartPos(),
                            walkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();

                    int distance = (int) walkPath.getDistance();
                    int duration = (int) walkPath.getDuration();
                    String des = MapUtil.getFriendlyTime(duration) + "(" + MapUtil.getFriendlyLength(distance) + ")";
                    Log.d(TAG, des);
                } else if (walkRouteResult.getPaths() == null) {
                    showMsg("对不起，没有搜索到相关数据！");
                }
            } else {
                showMsg("对不起，没有搜索到相关数据！");
            }
        } else {
            showMsg("错误码；" + i);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}