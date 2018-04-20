package com.poemsmap.darren.poemsmap;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.ArcOptions;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.api.maps.model.PolylineOptions;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Darren on 2017/12/21.
 */

public class PoetMapFragment extends Fragment implements View.OnClickListener, AMap.OnMarkerClickListener {

    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.bt_search)
    Button btSearch;
    @Bind(R.id.txt_description1)
    TextView txtDescription1;
    @Bind(R.id.txt_description2)
    TextView txtDescription2;
    @Bind(R.id.txt_description3)
    TextView txtDescription3;
    @Bind(R.id.txt_description4)
    TextView txtDescription4;
    @Bind(R.id.map)
    MapView map;
    private AMap aMap;
    private View rootView;


    public static PoetMapFragment newInstance() {
        return new PoetMapFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_poetmaps, container, false);
        ButterKnife.bind(this, rootView);
        if (aMap == null) {
            map = rootView.findViewById(R.id.map);
            map.onCreate(savedInstanceState);
            aMap = map.getMap();
            btSearch.setOnClickListener(this);
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

    public ArcOptions mArcOptions(LatLng begin, LatLng end, int color) {

        Double clongitude = (((begin.longitude + 0.2) * 1000000 + (end.longitude + 0.2) * 1000000) / 2) * 0.000001;
        Double clatitude = (((begin.latitude + 0.1) * 1000000 + (end.latitude + 0.1) * 1000000) / 2) * 0.000001;
        return new ArcOptions().point(
                begin,
                new LatLng(clatitude, clongitude),
                end)
                .strokeColor(color);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_search:

                txtDescription1.setVisibility(View.VISIBLE);
                txtDescription2.setVisibility(View.VISIBLE);
                txtDescription3.setVisibility(View.VISIBLE);
                txtDescription4.setVisibility(View.VISIBLE);

                // 绘制丹阳到常州晋陵，鄱阳，浔阳的线
                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .position(Constants.DANYANG)
                        .title("丹阳")
                        .snippet("丹阳介绍")
                        .draggable(true));

                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .position(Constants.JINLING1)
                        .title("常州晋陵")
                        .snippet("常州晋陵介绍")
                        .draggable(true));

                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .position(Constants.BOYANG)
                        .title("鄱阳")
                        .snippet("鄱阳介绍")
                        .draggable(true));

                aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .position(Constants.XUNYANG)
                        .title("浔阳")
                        .snippet("浔阳介绍")
                        .draggable(true));


//                aMap.addArc(mArcOptions(Constants.DANYANG, Constants.JINLING1, Color.RED));
//                aMap.addArc(mArcOptions(Constants.JINLING1, Constants.BOYANG, Color.GREEN));
//                aMap.addArc(mArcOptions(Constants.BOYANG, Constants.XUNYANG, Color.BLUE));


                aMap.addNavigateArrow((new NavigateArrowOptions())
                        .add(Constants.DANYANG, Constants.JINLING1)
                        .width(30)
                        .topColor(Color.RED));
                aMap.addNavigateArrow((new NavigateArrowOptions())
                        .add(Constants.JINLING1, Constants.BOYANG)
                        .width(30)
                        .topColor(Color.GREEN));

                aMap.addNavigateArrow((new NavigateArrowOptions())
                        .add(Constants.BOYANG, Constants.XUNYANG)
                        .width(30)
                        .topColor(Color.BLUE));

                aMap.addNavigateArrow((new NavigateArrowOptions())
                        .add(Constants.XUNYANG, Constants.BOYANG)
                        .width(30)
                        .topColor(Color.BLACK));
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
