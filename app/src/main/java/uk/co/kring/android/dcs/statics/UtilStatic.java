package uk.co.kring.android.dcs.statics;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import uk.co.kring.android.dcs.ActivityException;
import uk.co.kring.android.dcs.MessageActivity;
import uk.co.kring.android.dcs.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

public class UtilStatic {

    @SuppressWarnings("deprecation")
    public static void dialog(Context here, int title, int icon, String text,
                              DialogInterface.OnClickListener ok,
                              DialogInterface.OnClickListener cancel,
                              DialogInterface.OnClickListener more) {
        AlertDialog.Builder builder = new AlertDialog.Builder(here);
        builder.setTitle(here.getString(title));
        builder.setIcon(here.getResources().getDrawable(icon));
        builder.setMessage(text);
        if(ok != null) builder.setPositiveButton(R.string.ok, ok);
        if(cancel != null) builder.setNegativeButton(R.string.cancel, cancel);
        if(more != null) builder.setNeutralButton(R.string.more, more);

        AlertDialog alert = builder.create();
        //alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    public static void toast(Context here, String toast) {
        Toast.makeText(here, toast, Toast.LENGTH_SHORT).show();
    }

    public static void notify(String title, Map<String, String> data,
                              String body, Context here) {
        NotificationManagerCompat nm = NotificationManagerCompat.from(here);
        // Create an Intent for the activity you want to start
        Intent intent = new Intent(here, MessageActivity.class);
        Iterator<String> k = data.keySet().iterator();
        while(k.hasNext()) {
            String key = k.next();
            intent.putExtra(key, data.get(key));//add keys
        };
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(here);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification builder = new NotificationCompat.Builder(here,
                NotificationChannel.DEFAULT_CHANNEL_ID)
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.ic_notify)
                .setContentTitle(title)
                .setContentText(body)
                /* .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body)) */
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        nm.notify(title.hashCode(), builder);
    }

    public static Bitmap getBitmap(Context here, String res) throws IOException {
        InputStream i = here.getAssets().open(res);
        f = BitmapFactory.decodeStream(i);
        i.close();
        return f;
    }

    static Bitmap f;
    public final static int width = 8;
    public final static int height = 10;

    public static Bitmap getCharBitmap(char ch) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Rect copyRect = new Rect((ch % 32) * width, (ch / 32) * height,
                width, height);
        c.drawBitmap(b, copyRect,
                new Rect(0, 0, width, height), null);
        return b;
    }

    public static Bitmap[] getChars() {
        Bitmap[] arr = new Bitmap[f.getWidth() / width * f.getHeight() / height];
        for(char i = 0; i < arr.length; ++i) {
            arr[i] = getCharBitmap(i);
        }
        return arr;//font array
    }

    static GoogleApiAvailability google;

    public static GoogleApiAvailability googleAPI() {
        return google;
    }

    public static void googleAPICheck(Activity c) {
        GoogleApiAvailability g = GoogleApiAvailability.getInstance();
        int status;
        if((status = g.isGooglePlayServicesAvailable(c)) != ConnectionResult.SUCCESS) {
            g.makeGooglePlayServicesAvailable(c)
                .addOnFailureListener(c, new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        g.getErrorDialog(c, status, 1).show();
                    }
                }).addOnSuccessListener(c, new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        google = g;
                    }
                });
        }
    }
}
