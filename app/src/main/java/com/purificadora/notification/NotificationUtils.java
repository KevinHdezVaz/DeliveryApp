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

import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.internal.service.Common;
import com.purificadora.MainActivity;
import com.purificadora.MyApplication;
import com.purificadora.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class NotificationUtils {
    private static final String CHANNEL_ID = "my_channel_id"; // ID del canal de notificaciones
    private String TAG = NotificationUtils.class.getSimpleName();

    public NotificationUtils() {
    }
    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
    }

    private Context mContext;
    public static void displayNotification(Context context, String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Icono de mensaje de SMS
                .setContentTitle(title)
                .setContentText(body)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Categoría de mensaje
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Alta prioridad
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // Agregar sonido
                .setStyle(new NotificationCompat.InboxStyle() // Estilo de mensaje de SMS
                        .addLine(body));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_HIGH; // IMPORTANCE_HIGH para alta prioridad

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }



        notificationManager.notify(1, builder.build()); // Puedes cambiar el ID para mostrar múltiples notificaciones
    }

    public void showNotificationOrder(String title, String message){
        Intent reuseIntent;

        reuseIntent = new Intent(mContext, MainActivity.class);
        reuseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        showNotification(title, message, reuseIntent);

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
            PendingIntent resultPendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                resultPendingIntent =
                        PendingIntent.getActivity(
                                mContext,
                                uniqueInt,
                                intent,
                                PendingIntent.FLAG_IMMUTABLE
                        );
            }else{
                resultPendingIntent =
                        PendingIntent.getActivity(
                                mContext,
                                uniqueInt,
                                intent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );
            }

            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(message);

            String channelId = mContext.getString(R.string.default_notification_channel_id);

            //NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
            //        mContext);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, channelId);

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
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_notification))
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH);
            //.build();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                mBuilder.setSound(sound);
            }

            Notification notification = mBuilder.build();


            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
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
          //  Common.logError(TAG, ex.toString());
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

    /**
     * Obtener el tiempo
     * @return
     */
    public String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return  format.format(new Date());
    }
}
