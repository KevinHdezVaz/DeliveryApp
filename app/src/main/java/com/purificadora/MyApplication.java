package com.purificadora;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory;
import com.onesignal.OneSignal;
import com.purificadora.notification.NotificationHelper;

public class MyApplication extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;



        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


    }
}