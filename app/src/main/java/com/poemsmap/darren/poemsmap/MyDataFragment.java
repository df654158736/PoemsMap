package com.poemsmap.darren.poemsmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amap.api.maps.offlinemap.OfflineMapActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Darren on 2018/3/23.
 */

public class MyDataFragment extends Fragment implements View.OnClickListener {


    @Bind(R.id.bt_offlineMap)
    Button btOfflineMap;

    public static MyDataFragment newInstance() {
        return new MyDataFragment();
    }


    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_mydata, container, false);
        ButterKnife.bind(this, rootView);

        btOfflineMap.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            /**
             * 点击下载离线地图
             */
            case R.id.bt_offlineMap:
                startActivity(new Intent(getActivity().getApplicationContext(),
                        OfflineMapActivity.class));
                break;
            default:
                break;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}