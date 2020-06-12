package uk.co.kring.android.dcs.statics;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.preference.PreferenceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import uk.co.kring.android.dcs.ActivityException;
import uk.co.kring.android.dcs.MessageActivity;
import uk.co.kring.android.dcs.R;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

public class UtilStatic {

    static Bitmap f;
    static GoogleApiAvailability google;
    static FirebaseAnalytics analytics;
    static FirebaseRemoteConfig config;

    //================================= PUBLIC INTERFACE
    @SuppressWarnings("deprecation")
    public static void dialog(Context here, int title, int icon, String text,
                              DialogInterface.OnClickListener ok,
                              DialogInterface.OnClickListener cancel,
                              String more) {
        AlertDialog.Builder builder = new AlertDialog.Builder(here);
        builder.setTitle(here.getString(title));
        builder.setIcon(here.getResources().getDrawable(icon));
        builder.setMessage(text);
        if(ok != null) builder.setPositiveButton(R.string.ok, ok);
        if(cancel != null) builder.setNegativeButton(R.string.cancel, cancel);
        if(more != null) builder.setNeutralButton(R.string.more,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog(here, title, icon, more,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface2, int i) {
                                    dialogInterface2.dismiss();
                                }
                            }, null, null);
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        //alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    public static void toast(Context here, String toast) {
        Toast.makeText(here, toast, Toast.LENGTH_SHORT).show();
    }

    public static Bundle bundleFromMap(Map<String, String> data) {
        Bundle b = new Bundle();
        Iterator<String> k = data.keySet().iterator();
        while(k.hasNext()) {
            String key = k.next();
            b.putString(key, data.get(key));//add keys
        };
        return b;
    }

    static int msg = 2;

    public static void notify(String title, Bundle data,
                              String body, Context here) {
        NotificationManagerCompat nm = NotificationManagerCompat.from(here);
        // Create an Intent for the activity you want to start
        Intent intent = new Intent(here, MessageActivity.class);
        intent.putExtras(data);
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
        nm.notify(msg++, builder);
    }

    public static Bitmap getBitmap(Context here, String res) throws IOException {
        InputStream i = here.getAssets().open(res);
        f = BitmapFactory.decodeStream(i);
        i.close();
        return f;
    }

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
        //TODO: 1200 -> 1024 and 26 plus 30 (@ 20 * 20)
        for(char i = 0; i < arr.length; ++i) {
            arr[i] = getCharBitmap(i);
        }
        return arr;//font array
    }

    public static boolean googleAPI() {
        return google != null;
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
                        fetchRemoteConfig(c);
                    }
                });
        }
    }

    public static String pref(Context c, String key, String unset) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String chan9;
        if((chan9 = getPrefRemote("!" + key,null)) != null) {//override
            return chan9;
        } else {
            return sp.getString(key, getPrefRemote(key, unset));
        }
    }

    public static boolean prefSave(Context c, String key, String val) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        if(getPrefRemote("!" + key,null) != null) {//override
            return false;//can't save
        } else {
            sp.edit().putString(key, val).commit();
            return true;
        }
    }

    public static void postAnalytic(Context c, Bundle b) {
        if(analytics == null) {
            if(c == null) throw new ActivityException(new Exception());
            analytics = FirebaseAnalytics.getInstance(c);
        }
        if(b != null) analytics.logEvent("post", b);
    }

    public static void postAnalyticContext(Context c) {
        postAnalytic(c, null);
    }

    public static void postAnalyticBundle(Bundle b) {
        postAnalytic(null, b);
    }

    public static String loadLatin(InputStream is) throws IOException {
        StringBuffer in = new StringBuffer();
        int i;
        while((i = is.read()) != -1) {
            in.append((char)i);
        }
        is.close();
        return in.toString();
    }

    public static String loadUTF(InputStream is) throws IOException {
        StringBuffer in = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is, "UTF8");
        int i;
        while((i = isr.read()) != -1) {
            in.append((char)i);
        }
        isr.close();
        return in.toString();
    }

    //directions
    final static int UP       = KeyEvent.KEYCODE_DPAD_UP;
    final static int LEFT     = KeyEvent.KEYCODE_DPAD_LEFT;
    final static int RIGHT    = KeyEvent.KEYCODE_DPAD_RIGHT;
    final static int DOWN     = KeyEvent.KEYCODE_DPAD_DOWN;
    final static int CENTER   = KeyEvent.KEYCODE_DPAD_CENTER;

    //action buttons
    final static int A        = KeyEvent.KEYCODE_BUTTON_A;//primary
    final static int B        = KeyEvent.KEYCODE_BUTTON_B;//exit/back
    final static int X        = KeyEvent.KEYCODE_BUTTON_X;
    final static int Y        = KeyEvent.KEYCODE_BUTTON_Y;
    final static int LT       = KeyEvent.KEYCODE_BUTTON_L1;
    final static int RT       = KeyEvent.KEYCODE_BUTTON_R1;

    //special action buttons (for menus and pause)
    final static int PAUSE    = KeyEvent.KEYCODE_BUTTON_START;
    final static int MENU     = KeyEvent.KEYCODE_BUTTON_SELECT;//not all controllers?
    //NB. paused and ACTION is MENU?
    final static int BACK     = KeyEvent.KEYCODE_BUTTON_B;//easy check back
    final static int ACTION   = KeyEvent.KEYCODE_BUTTON_A;

    static int directionPressed = -1; // initialized to -1

    public static int getDirectionPressed(InputEvent event) {
        if (!isDpadDevice(event)) {
            return -1;
        }

        // If the input event is a MotionEvent, check its hat axis values.
        if (event instanceof MotionEvent) {

            // Use the hat axis value to find the D-pad direction
            MotionEvent motionEvent = (MotionEvent) event;
            float xaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
            float yaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);

            // Check if the AXIS_HAT_X value is -1 or 1, and set the D-pad
            // LEFT and RIGHT direction accordingly.
            if (Float.compare(xaxis, -1.0f) == 0) {
                directionPressed =  LEFT;
            } else if (Float.compare(xaxis, 1.0f) == 0) {
                directionPressed =  RIGHT;
            }
            // Check if the AXIS_HAT_Y value is -1 or 1, and set the D-pad
            // UP and DOWN direction accordingly.
            else if (Float.compare(yaxis, -1.0f) == 0) {
                directionPressed =  UP;
            } else if (Float.compare(yaxis, 1.0f) == 0) {
                directionPressed =  DOWN;
            }
        }

        // If the input event is a KeyEvent, check its key code.
        else if (event instanceof KeyEvent) {

            // Use the key code to find the D-pad direction.
            KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                directionPressed = LEFT;
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                directionPressed = RIGHT;
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                directionPressed = UP;
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                directionPressed = DOWN;
            }
        }
        return directionPressed;
    }

    public static boolean isKey(InputEvent event, int keyCode, boolean paused) {
        if (event instanceof KeyEvent) {
            KeyEvent k = (KeyEvent)event;
            if(k.getRepeatCount() == 0) {
                int c = k.getKeyCode();
                if(c == CENTER) c = A;//primary action android suggestion
                if(c == KeyEvent.KEYCODE_MENU) c = MENU;//map suggestion
                if(c == KeyEvent.KEYCODE_BUTTON_L2) c = LT;//map suggestion
                if(c == KeyEvent.KEYCODE_BUTTON_R2) c = RT;//map suggestion
                if(paused && c == A) c = MENU;//if no SELECT button?
                if(c == keyCode) return true;
            }
        }
        return false;
    }

    public static float joystickXL(MotionEvent event) {
        InputDevice inputDevice = event.getDevice();
        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis.
        float x = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_X);
        if (x == 0) {
            x = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_X);
        }
        return x;
    }

    public static float joystickYL(MotionEvent event) {
        InputDevice inputDevice = event.getDevice();
        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch.
        float y = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Y);
        if (y == 0) {
            y = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_Y);
        }
        return y;
    }

    public static float joystickXR(MotionEvent event) {
        InputDevice inputDevice = event.getDevice();
        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis.
        float x = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Z);
        return x;
    }

    public static float joystickYR(MotionEvent event) {
        InputDevice inputDevice = event.getDevice();
        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch.
        float y = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_RZ);
        return y;
    }

    //================================= PACKAGED
    static float getCenteredAxis(MotionEvent event,
                                         InputDevice device, int axis) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value = event.getAxisValue(axis);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    static boolean isDpadDevice(InputEvent event) {
        // Check that input comes from a device with directional pads.
        if ((event.getSource() & InputDevice.SOURCE_DPAD)
                != InputDevice.SOURCE_DPAD) {
            return true;
        } else {
            return false;
        }
    }

    static String getPrefRemote(String key, String unset) {
        if(config != null) return config.getString(key);
        return unset;
    }

    static void fetchRemoteConfig(Activity here) {
        FirebaseRemoteConfig rc = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(3600 * 24)
                        .build();
        rc.setConfigSettingsAsync(configSettings);
        rc.fetchAndActivate()
                .addOnCompleteListener(here, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(Task<Boolean> task) {
                        config = rc;
                    }
                });
    }
}
