package com.poemsmap.darren.poemsmap;

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
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.NaviPara;
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

public class SearchMapFragment extends Fragment implements View.OnClickListener, AMap.OnMarkerClickListener, PoiSearch.OnPoiSearchListener {
    private GoogleMap mMap;
    private WebView webView;
    private AMap aMap;
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果
    private EditText content;
    private Button search;
    private TextView des;
    private View rootView;

    public SearchMapFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_searchmaps, container, false);

        if (aMap == null) {
            MapView mapView = (MapView) rootView.findViewById(R.id.map);
            mapView.onCreate(savedInstanceState);
            aMap = mapView.getMap();

            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    Constants.WUXI, 10, 30, 30)));

            content = (EditText) rootView.findViewById(R.id.et_content);
            search = (Button) rootView.findViewById(R.id.bt_search);
            search.setOnClickListener(this);
            des = (TextView) rootView.findViewById(R.id.txt_description);
            aMap.setOnMarkerClickListener(this);// 添加点击marker监听事件
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

                des.setVisibility(View.VISIBLE);

                String loc = content.getText().toString();

                query = new PoiSearch.Query(loc, "", "无锡");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
                query.setCityLimit(true);

                poiSearch = new PoiSearch(getActivity(), query);
                query.setPageSize(1);
                poiSearch.setOnPoiSearchListener(this);
                poiSearch.searchPOIAsyn();

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

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {

        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {

                    }
                }
            } else {

            }
        } else {
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }


}
