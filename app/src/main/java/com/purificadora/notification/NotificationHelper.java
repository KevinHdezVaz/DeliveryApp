package com.purificadora.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.purificadora.R;

import java.util.Random;

public class NotificationHelper {
    private static final String CHANNEL_ID = "my_channel_id"; // ID del canal de notificaciones

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


}
