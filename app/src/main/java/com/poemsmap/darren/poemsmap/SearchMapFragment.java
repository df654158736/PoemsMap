package com.poemsmap.darren.poemsmap;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.poemsmap.darren.poemsmap.model.SearchMap;
import com.poemsmap.darren.poemsmap.supercache.NewsCallback;
import com.poemsmap.darren.poemsmap.util.OffLineMapUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Darren on 2017/12/21.
 */

public class SearchMapFragment extends Fragment implements View.OnClickListener, AMap.OnMarkerClickListener {
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.bt_search)
    Button btSearch;
    @Bind(R.id.txt_description)
    TextView txtDescription;
    @Bind(R.id.map)
    MapView map;
    @Bind(R.id.spinner_dynasty)
    Spinner spinnerDynasty;
    private AMap aMap;
    private List<SearchMap> cacheList;
    private Map<Marker,String> cacheMarker;
    private int dynasty = 0;

    public static SearchMapFragment newInstance() {
        return new SearchMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_searchmaps, container, false);
        ButterKnife.bind(this, rootView);

        MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(getActivity());

        if (aMap == null) {
            map.onCreate(savedInstanceState);
            aMap = map.getMap();
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    Constants.WUXI, 10, 30, 30)));
            btSearch.setOnClickListener(this);
            aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件

            if(cacheMarker == null){
                cacheMarker = new HashMap<>();
            }

            List<String> list = new ArrayList<>();
            list.add("无");
            list.add("先秦两汉");
            list.add("魏晋南北朝");
            list.add("隋唐");
            list.add("宋元");
            list.add("明清");
            list.add("近现代");
            ArrayAdapter<String> adapter=new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,list);
            spinnerDynasty.setAdapter(adapter);
            spinnerDynasty.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    dynasty = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 点击搜索按钮
             */
            case R.id.bt_search:

                txtDescription.setVisibility(View.VISIBLE);
                String loc = etContent.getText().toString();

                Map<String, String> param = new HashMap<>();
                param.put("PoetName", loc);
                param.put("Place", loc);
                param.put("PoemName", loc);
                param.put("Dynasty", dynasty+"");
                JSONObject jsonObject = new JSONObject(param);

                OkGo.<List<SearchMap>>post("http://218.244.140.234:8085/api/Values/LocationList")
                        .cacheKey("TabFragment_SearchMapFragment")
                        .tag(this)
                        .upJson(jsonObject)
                        .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                        .execute(new NewsCallback<List<SearchMap>>() {
                            @Override
                            public void onSuccess(Response<List<SearchMap>> response) {
                                cacheList = response.body();
                                cacheMarker.clear();
                                txtDescription.setText("");
                                aMap.clear();
                                if (cacheList != null) {
                                    for (SearchMap result : cacheList) {
                                        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                                .position(new LatLng(result.Lat, result.Lng))
                                                .title(result.PoetName)
                                                .snippet(result.PoemName)
                                                .draggable(true);
                                        cacheMarker.put(aMap.addMarker(markerOption),result.Id);
                                    }
                                }

                                if(!cacheList.isEmpty()){
                                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                            new LatLng(cacheList.get(0).Lat, cacheList.get(0).Lng) , 15, 30, 30)));
                                }else{
                                    aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                            Constants.WUXI , 10, 30, 30)));
                                }


                            }
                        });


                break;
            default:
                break;
        }
    }



    /**
     * 方法必须重写
     */

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }


    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (aMap != null) {
            jumpPoint(marker);
            String id = cacheMarker.get(marker);
            for (SearchMap map : cacheList) {
                if (map.Id.equals(id)) {
                    String description = map.Description == null? "<b>正文：</b><br/>"+ map.PoemContent.replace("\r\n","<br/>"):"<b>描述：</b><br/>"+map.Description+"<br/><b>正文：</b><br/>"+ map.PoemContent.replace("\r\n","<br/>");
                    txtDescription.setText(Html.fromHtml(description));
                }
            }
            marker.showInfoWindow();
        }

//        Toast.makeText(getActivity(), "您点击了Marker", Toast.LENGTH_LONG).show();
        return true;
    }

    /**
     * marker点击时跳动一下
     */
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
}
