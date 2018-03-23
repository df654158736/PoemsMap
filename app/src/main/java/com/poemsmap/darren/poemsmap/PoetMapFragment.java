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
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Marker;
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_search:

                txtDescription1.setVisibility(View.VISIBLE);
                txtDescription2.setVisibility(View.VISIBLE);
                txtDescription3.setVisibility(View.VISIBLE);
                txtDescription4.setVisibility(View.VISIBLE);

                // 绘制丹阳到常州晋陵，鄱阳，浔阳的线
                aMap.addPolyline((new PolylineOptions())
                        .add(Constants.DANYANG, Constants.JINLING1)
                        .color(Color.RED));
                aMap.addPolyline((new PolylineOptions())
                        .add(Constants.JINLING1, Constants.BOYANG)
                        .color(Color.GREEN));
                aMap.addPolyline((new PolylineOptions())
                        .add(Constants.BOYANG, Constants.XUNYANG)
                        .color(Color.BLUE));
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
