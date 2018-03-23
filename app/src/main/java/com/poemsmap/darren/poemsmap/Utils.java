package com.poemsmap.darren.poemsmap;

/**
 * Created by Darren on 2017/12/11.
 */

import android.text.TextUtils;


import java.text.SimpleDateFormat;
import java.util.Locale;


public class Utils {
    /**
     *  开始定位
     */
    public final static int MSG_LOCATION_START = 0;
    /**
     * 定位完成
     */
    public final static int MSG_LOCATION_FINISH = 1;
    /**
     * 停止定位
     */
    public final static int MSG_LOCATION_STOP= 2;

    public final static String KEY_URL = "URL";
    public final static String URL_H5LOCATION = "file:///android_asset/location.html";
    /**
     * 根据定位结果返回定位信息的字符串
     * @param location
     * @return
     */


    private static SimpleDateFormat sdf = null;
    public  static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }
}
