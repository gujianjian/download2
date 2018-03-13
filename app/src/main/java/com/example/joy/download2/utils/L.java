package com.example.joy.download2.utils;

import android.util.Log;

import static java.lang.Boolean.TRUE;

/**
 * Created by joy on 2018/3/13.
 */

public class L {

    public static final boolean DEBUG = TRUE;
    private static final String TAG = "gujianjian";

    public static void D(String msg) {
       if(DEBUG){
           Log.d(TAG, msg);
       }
    }
}
