package com.poemsmap.darren.poemsmap;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.ArcOptions;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NavigateArrowOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.poemsmap.darren.poemsmap.model.PoetMap;
import com.poemsmap.darren.poemsmap.model.PoetMapItem;
import com.poemsmap.darren.poemsmap.model.SearchMap;
import com.poemsmap.darren.poemsmap.supercache.NewsCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Bind(R.id.txt_description)
    TextView txtDescription;

    @Bind(R.id.map)
    MapView map;
    private AMap aMap;
    private View rootView;

    private List<PoetMapItem> cacheList;
    private Map<Marker, String> cacheMarker;


    public static PoetMapFragment newInstance() {
        return new PoetMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_poetmaps, container, false);
        ButterKnife.bind(this, rootView);
        if (aMap == null) {

            if (cacheMarker == null) {
                cacheMarker = new HashMap<>();
            }

            map = rootView.findViewById(R.id.map);
            map.onCreate(savedInstanceState);
            aMap = map.getMap();
            btSearch.setOnClickListener(this);
            aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
        }
        setUpMap();
        return rootView;
    }

    private void setUpMap() {
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                Constants.HEFEI, 7, 30, 30)));

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
                aMap.clear();
                txtDescription.setVisibility(View.VISIBLE);
                String name = etContent.getText().toString();

                OkGo.<PoetMap>get("http://218.244.140.234:8085/api/Values/GetPoemsMapList?name=" + name)
                        .cacheKey("TabFragment_PoetMapFragment")
                        .tag(this)
                        .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                        .execute(new NewsCallback<PoetMap>() {
                            @Override
                            public void onSuccess(Response<PoetMap> response) {
                                cacheList = response.body().Items;
                                int lengh = response.body().Items.size();


                                aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        new LatLng(cacheList.get(0).Lat, cacheList.get(0).Lng), 15, 30, 30)));

                                for (int i = 0; i < lengh; i++) {

                                    PoetMapItem item = response.body().Items.get(i);

                                    MarkerOptions markerOption = new MarkerOptions()
                                            .icon(BitmapDescriptorFactory
                                                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                            .position(new LatLng(item.Lat, item.Lng))
                                            .title("序列" + (Integer.parseInt(item.Number) + 1))
                                            .snippet(item.Contents.substring(0, 10) + "...")
                                            .draggable(true);
                                    aMap.addMarker(markerOption);
                                    cacheMarker.put(aMap.addMarker(markerOption), item.Id);


                                    if (i < lengh - 1) {
                                        PoetMapItem item1 = response.body().Items.get(i + 1);
                                        aMap.addNavigateArrow((new NavigateArrowOptions())
                                                .add(new LatLng(item.Lat, item.Lng), new LatLng(item1.Lat, item1.Lng))
                                                .width(30)
                                                .topColor(FormatColor(item.LineColor == null ? 0 : item.LineColor)));
                                    }
                                }
                            }
                        });

                break;
            default:
                break;
        }
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (aMap != null) {
//           jumpPoint(marker);
            String id = cacheMarker.get(marker);
            for (PoetMapItem map : cacheList) {
                if (map.Id.equals(id)) {
                    txtDescription.setText(map.Contents);
                }
            }
            marker.showInfoWindow();
        }
        return true;
    }


    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        final LatLng markerLatlng = marker.getPosition();
        Point markerPoint = proj.toScreenLocation(markerLatlng);
        markerPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(markerPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * markerLatlng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * markerLatlng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private int FormatColor(int i) {

        switch (i) {
            case 0:
                return Color.RED;
            case 1:
                return Color.BLACK;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.GRAY;
            case 5:
                return Color.BLUE;
            case 6:
                return Color.DKGRAY;


            default:
                return Color.RED;
        }
    }
}
