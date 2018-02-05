package com.poemsmap.darren.poemsmap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.NaviPara;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

/**
 * Created by Darren on 2017/12/21.
 */

public class PoetMapFragment extends Fragment implements View.OnClickListener, AMap.OnMarkerClickListener{

    private AMap aMap;
    private EditText content;
    private Button search;
    private View rootView;
    private TextView des1;
    private TextView des2;
    private TextView des3;
    private TextView des4;

    public PoetMapFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_poetmaps, container, false);

        if (aMap == null) {
            MapView mapView = (MapView) rootView.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            aMap = mapView.getMap();
            content = (EditText) rootView.findViewById(R.id.et_content);
            search = (Button) rootView.findViewById(R.id.bt_search);
            des1 = (TextView) rootView.findViewById(R.id.txt_description1);
            des2 = (TextView) rootView.findViewById(R.id.txt_description2);
            des3 = (TextView) rootView.findViewById(R.id.txt_description3);
            des4 = (TextView) rootView.findViewById(R.id.txt_description4);
            search.setOnClickListener(this);
        }

        setUpMap();
        return rootView;
    }

    private void setUpMap() {
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                Constants.HEFEI, 7, 30, 30)));


//        // 绘制庐山到金陵的线
//        aMap.addPolyline((new PolylineOptions())
//                .add(Constants.LUSHAN,  Constants.JINLING)
//                .color(Color.RED));



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_search:
                des1.setVisibility(View.VISIBLE);
                des2.setVisibility(View.VISIBLE);
                des3.setVisibility(View.VISIBLE);
                des4.setVisibility(View.VISIBLE);


                // 绘制丹阳到常州晋陵，鄱阳，浔阳的线
                aMap.addPolyline((new PolylineOptions())
                        .add(Constants.DANYANG,  Constants.JINLING1)
                        .color(Color.RED));
                aMap.addPolyline((new PolylineOptions())
                        .add(Constants.JINLING1,  Constants.BOYANG)
                        .color(Color.GREEN));
                aMap.addPolyline((new PolylineOptions())
                        .add(Constants.BOYANG,  Constants.XUNYANG)
                        .color(Color.BLUE));
                break;
            default:
                break;
        }
    }


    public void startAMapNavi(Marker marker) {
        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        naviPara.setTargetPoint(marker.getPosition());
        // 设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);

        // 调起高德地图导航
        try {
            AMapUtils.openAMapNavi(naviPara, getActivity());
        } catch (com.amap.api.maps2d.AMapException e) {

            // 如果没安装会进入异常，调起下载页面
            AMapUtils.getLatestAMapApp(getActivity());

        }

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }


}
