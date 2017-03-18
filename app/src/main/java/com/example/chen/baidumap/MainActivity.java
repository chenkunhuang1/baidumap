package com.example.chen.baidumap;


import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Button mwei;
    public LocationClient mLocationClient = null;
    private TextureMapView mMapView;
    private BaiduMap mbaiduMap;
    private PoiSearch poisearch;
    private Button mpoi;
    private Boolean isFirstLocate = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mMapView = (TextureMapView) findViewById(R.id.bmapView);
        mbaiduMap = mMapView.getMap();//获取地图
        mbaiduMap.setMyLocationEnabled(true);

        BDLocationListener myListener = new MyLocationListener();
        mLocationClient = new LocationClient(getApplicationContext());

        //注册监听器
        mLocationClient.registerLocationListener(myListener);


        //mbaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//获取普通地图
        //mbaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);//获取卫星地图
        //添加一个标志覆盖物
        //LatLng point = new LatLng(39.96,116.40);
        //BitmapDescriptor bd = new BitmapDescriptorFactory().fromResource(R.mipmap.ic_launcher);
        //创建图层选项
        //OverlayOptions options = new MarkerOptions().position(point).icon(bd);
       // 把图层选项添加到地图上
        //mbaiduMap.addOverlay(options);

        initView();
       poisearch = PoiSearch.newInstance();
        OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                List<PoiInfo> poInfo = poiResult.getAllPoi();
                for(PoiInfo p : poInfo){
                    System.out.print(p.city+ "--" +p.address+"--"+p.phoneNum);
                    BitmapDescriptor bd = new BitmapDescriptorFactory().fromResource(R.mipmap.ic_launcher);
                    //创建图层选项
                    OverlayOptions options = new MarkerOptions().position(p.location).icon(bd);
                    //把图层选项添加到地图上
                    mbaiduMap.addOverlay(options);

                }

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        };
        poisearch.setOnGetPoiSearchResultListener(listener);
    }

    private void initView() {
        mpoi = (Button) findViewById(R.id.poi);
        mpoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //发起一个检索
                poisearch.searchInCity(new PoiCitySearchOption().city("北京").keyword("美食").pageNum(10));
            }
        });
        mwei = (Button) findViewById(R.id.wei);
        mwei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationClientOption option = new LocationClientOption();
                option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
                //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

                option.setCoorType("bd09ll");
                //可选，默认gcj02，设置返回的定位结果坐标系

                //int span=1000;
                //option.setScanSpan(span);
                //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

                option.setIsNeedAddress(true);
                //可选，设置是否需要地址信息，默认不需要

                option.setOpenGps(true);
                //可选，默认false,设置是否使用gps

                option.setLocationNotify(true);
                //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

                option.setIsNeedLocationDescribe(true);
                //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

                option.setIsNeedLocationPoiList(true);
                //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

                option.setIgnoreKillProcess(false);
                //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

                option.SetIgnoreCacheException(false);
                //可选，默认false，设置是否收集CRASH信息，默认收集

                option.setEnableSimulateGps(false);
                //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要


                mLocationClient.setLocOption(option);
                mLocationClient.start();
                mLocationClient.requestLocation();


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mMapView.onDestroy();
        mbaiduMap.setMyLocationEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            //获取定位结果
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度

            if (location.getLocType() == BDLocation.TypeGpsLocation){

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {

                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

            }

            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息

            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }

            Log.i("BaiduLocationApiDem", sb.toString());
            Toast.makeText(MainActivity.this,sb.toString(),Toast.LENGTH_LONG).show();
            if(location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeGpsLocation){
                navigateTo(location);

            }
           /* BitmapDescriptor bd = new BitmapDescriptorFactory().fromResource(R.mipmap.abc);
            LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
            OverlayOptions options = new MarkerOptions().position(latlng).icon(bd);
            mbaiduMap.addOverlay(options);
*/

        }



        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
    private void navigateTo(BDLocation location){
        if (isFirstLocate){
            LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mbaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            //BitmapDescriptor bd = new BitmapDescriptorFactory().fromResource(R.mipmap.abc);
            //OverlayOptions options = new MarkerOptions().position(ll).icon(bd);
            //mbaiduMap.addOverlay(options);
            mbaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        mbaiduMap.setMyLocationData(locationData);
    }

}

