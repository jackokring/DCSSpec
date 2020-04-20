package uk.co.kring.android.dcs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyService extends Service {

    NotificationManagerCompat nm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        nm = NotificationManagerCompat.from(getApplicationContext());
        return null;
    }

    public void notify(String title, String content) {
        //createNotificationChannel();
        Notification builder = new NotificationCompat.Builder(this,
                NotificationChannel.DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setGroup("all")
                .build();
        nm.notify(title.hashCode(), builder);
    }
}
