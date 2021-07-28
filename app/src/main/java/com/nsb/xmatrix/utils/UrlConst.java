package com.nsb.xmatrix.utils;

import com.nsb.xmatrix.MyApp;

public class UrlConst {
    //Todo: replace the URL with your own Flask restful API backend
    public static String baseUrl = MyApp.isDebug() ? "http://192.168.3.13:5000" : "http://http://47.100.80.164";
    // farm_land API URL
    public static String upload =  baseUrl + "/disease_predict";
    // harvest predict API URL
    public static String harvest_predict = "/harvest_predict";
}
