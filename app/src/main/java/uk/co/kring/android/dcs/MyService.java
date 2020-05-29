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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MyService extends Service {

    NotificationManagerCompat nm;
    CodeStatic dcs = CodeStatic.getInstance();
    AppDatabase db;
    boolean recordPermission;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        recordPermission = intent.getBooleanExtra("record", false);
        db = AppDatabase.getInstance(getApplicationContext());
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
