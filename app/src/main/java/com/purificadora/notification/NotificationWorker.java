package com.purificadora.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.purificadora.MainActivity;
import com.purificadora.MyApplication;
import com.purificadora.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class NotificationWorker extends Worker {
    private static final String CHANNEL_ID = "NOTIFICATIONWORK_1";


    public NotificationWorker(@NonNull Context mContext, @NonNull WorkerParameters workerParams) {
        super(mContext, workerParams);
    }



    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String title = inputData.getString("title");
        String message = inputData.getString("message");

        if (title != null && message != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            showNotification(title, message, intent);
        }

        return Result.success();
    }


    private void showNotification(String title, String message, Intent intent){
        try {
            if (TextUtils.isEmpty(message))
                return;
            Date Obj = new Date();
            int icon = R.drawable.ic_notification;
            int mNotificationId = MyApplication.ID_NOTIFICATION + Obj.getSeconds() +
                    Obj.getMinutes() + Obj.getHours() + Obj.getDay();
            Random random = new Random();
            mNotificationId += random.nextInt(10000);

            int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            getApplicationContext(),
                            uniqueInt,
                            intent,
                            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT
                  );



            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(message);

            String channelId = getApplicationContext().getString(R.string.default_notification_channel_id);

            //NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
            //        mContext);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId);

            mBuilder
                    .setSmallIcon(icon)
                    .setTicker(title)
                    .setWhen(getTimeMilliSec(getCurrentTime()))
                    .setAutoCancel(true)
                    //.setSound(sound)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    //.setStyle(inboxStyle)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setLights(Color.RED, 3000, 3000)
                    .setContentIntent(resultPendingIntent)
                    //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_notification))
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH);
            //.build();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                mBuilder.setSound(sound);
            }

            Notification notification = mBuilder.build();


            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            // NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

            NotificationChannel channel;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channel = new NotificationChannel(channelId,
                        NotificationConstants.CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_HIGH);
                //notificationManager.createNotificationChannel(channel);
                channel.setLightColor(Color.RED);
                channel.enableLights(true);
                channel.setDescription(NotificationConstants.CHANNEL_SIREN_DESCRIPTION);
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                channel.setSound(sound, audioAttributes);

                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }

            notificationManager.notify(mNotificationId, notification);

        }catch (Exception ex){
          //  Common.logError("Error", ex.toString());
        }
    }
    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  format.format(new Date());
    }

}
