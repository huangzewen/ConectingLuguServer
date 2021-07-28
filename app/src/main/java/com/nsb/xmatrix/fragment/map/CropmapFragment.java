package com.nsb.xmatrix.fragment.map;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.nsb.xmatrix.R;
import com.nsb.xmatrix.base.BaseFragment;
import com.nsb.xmatrix.utils.DemoDataProvider;
import com.nsb.xmatrix.utils.XToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.kunminx.linkage.LinkageRecyclerView;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.popupwindow.popup.XUISimplePopup;
import com.xuexiang.xutil.display.DensityUtils;

import butterknife.BindView;

@Page(name = "Crop distribution map")
public class CropmapFragment extends BaseFragment  implements CompoundButton.OnCheckedChangeListener,LocationSource,AMapLocationListener {
    private MapView mapView;
    private AMap aMap;
    private RoundButton btnFarmers;
    private RoundButton btnTraders;
    private XUISimplePopup mListPopup;
    private MyLocationStyle myLocationStyle;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption clientOption;

    private LatLng centerPoint = new LatLng(27.28179, 100.8507);// 云南省丽江市宁蒗经纬度

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_cropmap;
    }

    @Override
    protected void initViews() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cropmap, container, false);
        initView(savedInstanceState,view);
        initPopup();
        initListener();
        return view;
    }

    private void drawTraders(){
        LatLng[] latlngs = new LatLng[50];
        double x = 27.28179;
        double y = 100.8507;

        for (int i = 0; i < 6; i++) {
            double x_ = 0;
            double y_ = 0;
            x_ = Math.random() * 0.7 - 0.25;
            y_ = Math.random() * 0.7 - 0.25;
            latlngs[i] = new LatLng(x + x_, y + y_);
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.draggable(true);//设置Marker可拖动
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.trader)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            markerOption.setFlat(true);//设置marker平贴地图效果
            markerOption.position(latlngs[i]);
            markerOption.title("Trader"+String.valueOf(i))
                    .snippet("Trader"+String.valueOf(i)+":"+
                            String.valueOf(latlngs[i].latitude)+","+String.valueOf(latlngs[i].longitude));
            aMap.addMarker(markerOption);
        }

    }

    private void drawCropHeatMap() {
        LatLng[] latlngs = new LatLng[50];
        double x = 27.28179;
        double y = 100.8507;

        for (int i = 0; i < 50; i++) {
            double x_ = 0;
            double y_ = 0;
            x_ = Math.random() * 0.7 - 0.25;
            y_ = Math.random() * 0.7 - 0.25;
            latlngs[i] = new LatLng(x + x_, y + y_);
            MarkerOptions markerOption = new MarkerOptions();
            //markerOption.draggable(true);//设置Marker可拖动
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(),R.drawable.reddot)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            //markerOption.setFlat(true);//设置marker平贴地图效果
            markerOption.position(latlngs[i]);
            aMap.addMarker(markerOption);
        }

    }

    private void initPopup(){
        mListPopup = new XUISimplePopup(getContext(), DemoDataProvider.farmers)
                .create(DensityUtils.dip2px(getContext(), 350),DensityUtils.dip2px(getContext(), 500), (adapter, item, position) -> XToastUtils.toast(item.getTitle().toString()))
                .setHasDivider(true);
    }

    private void initView( Bundle savedInstanceState,View view){
        mapView= (MapView) view.findViewById(R.id.map);
        btnFarmers= (RoundButton) view.findViewById(R.id.btn_farmers);
        btnTraders= (RoundButton) view.findViewById(R.id.btn_traders);
        mapView.onCreate(savedInstanceState);
        if (aMap==null)
        {
            aMap=mapView.getMap();
        }
        aMap.moveCamera(CameraUpdateFactory.zoomTo(10f)); //缩放比例
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(centerPoint)); //change central point
        drawCropHeatMap();
        drawTraders();
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
    }

    private void initListener(){
        btnFarmers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListPopup.showDown(v);
            }
        });
        btnTraders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: replace with real business logic huyq2002 2021-7-24
                XToastUtils.toast("点击了btnTraders");
            }
        });
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener=listener;
        if(locationClient==null){
            locationClient=new AMapLocationClient(getActivity());
            clientOption=new AMapLocationClientOption();
            locationClient.setLocationListener(this);
            clientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//高精度定位
            clientOption.setOnceLocationLatest(true);//设置单次精确定位
            locationClient.setLocationOption(clientOption);
            locationClient.startLocation();
        }

    }
    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener=null;
        if(locationClient!=null){
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient=null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null&&aMapLocation != null) {
            if (aMapLocation != null
                    &&aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        }
        else {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        }
    }

    /**
     * 必须重写以下方法
     */
    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(locationClient!=null){
            locationClient.onDestroy();
        }
    }
}