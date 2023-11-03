package com.purificadora.utils;

import android.util.Log;

public class Common {
    public static int JOB_ID_NOTIFICATION_ORDER = 10694;
    public static final String TAG = Common.class.getSimpleName();
    public static boolean DEBUG = true;

    public static void logError(String tag, String log) {
        if(DEBUG)
            Log.e(tag, log);
    }

}