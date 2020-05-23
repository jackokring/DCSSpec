package uk.co.kring.android.dcs;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import uk.co.kring.android.dcs.room.AppDatabase;
import uk.co.kring.android.dcs.statics.CodeStatic;

public class MyService extends Service {

    NotificationManagerCompat nm;
    CodeStatic dcs = CodeStatic.getInstance();
    AppDatabase db;
    boolean recordPermission;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        nm = NotificationManagerCompat.from(getApplicationContext());
        recordPermission = intent.getBooleanExtra("record", false);
        db = AppDatabase.getInstance(getApplicationContext());
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void notify(String title, String content) {
        // Create an Intent for the activity you want to start
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("title", title);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification builder = new NotificationCompat.Builder(this,
                NotificationChannel.DEFAULT_CHANNEL_ID)
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.ic_notify)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        nm.notify(title.hashCode(), builder);
    }
}
