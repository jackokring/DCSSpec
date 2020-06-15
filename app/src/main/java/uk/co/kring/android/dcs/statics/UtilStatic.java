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
    public final static int height = 12;
    public final static int sprite = 16;

    final static Rect destChar = new Rect(0, 0, width, height);
    final static Rect destSprite = new Rect(0, 0, sprite, sprite);
    final static Rect destSprite2 =
            new Rect(0, 0, sprite * 2, sprite * 2);

    public static Bitmap getCharBitmap(char ch) {
        //1024 bitmaps
        //512 characters
        //256 sprites (higher 256 have double size)
        //first 32 sprites in each 256 set (totalling 512) are a stretched font
        //from the highest 32 characters
        Bitmap b;
        Rect dest;
        if(ch < 512 - 32 * 4) {
            dest = destChar;
        } else if(ch < 512 + 256) {//middle set scale
            dest = destSprite;
        } else {
            dest = destSprite2;
        }
        b = Bitmap.createBitmap(dest.width(), dest.height(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Rect copyRect;
        if(ch < 512 - 32 * 4) {
            copyRect = new Rect((ch % 32) * width, (ch / 32) * height,
                    width, height);//char
        } else if(ch < 512) {
            ch -= 32 * 4;
            copyRect = new Rect((ch % 32) * width, (ch / 32) * height,
                    width, height);//expand last font lines (sprite)
        } else {
            //sprite y adjust
            final int adj = 12 * 12;
            ch &= 255;
            copyRect = new Rect((ch % 16) * sprite, (ch / 16) * sprite + adj,
                    sprite, sprite);
        }
        c.drawBitmap(f, copyRect, destChar, null);//to display
        return b;
    }

    public static Bitmap[] getChars() {
        Bitmap[] arr = new Bitmap[1024];
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
    public final static int CENTER   = KeyEvent.KEYCODE_DPAD_CENTER;

    //action buttons
    public static int A        = KeyEvent.KEYCODE_BUTTON_A;//primary
    public static int B        = KeyEvent.KEYCODE_BUTTON_B;//exit/back
    public static int X        = KeyEvent.KEYCODE_BUTTON_X;
    public static int Y        = KeyEvent.KEYCODE_BUTTON_Y;
    public static int L1       = KeyEvent.KEYCODE_BUTTON_L1;
    public static int R1       = KeyEvent.KEYCODE_BUTTON_R1;
    static int L2              = KeyEvent.KEYCODE_BUTTON_L2;
    static int R2              = KeyEvent.KEYCODE_BUTTON_R2;

    //special action buttons (for menus and pause)
    public static int PAUSE    = KeyEvent.KEYCODE_BUTTON_START;
    public static int MENU     = KeyEvent.KEYCODE_BUTTON_SELECT;//not all controllers?
    public static int META     = KeyEvent.KEYCODE_META_LEFT;
    //NB. paused and ACTION is MENU?
    public static int BACK     = B;//back (shield)
    public static int ACTION   = A;//order (attack/fire)
    public static int SCAN     = X;//warning (hazard/seek)
    public static int INFO     = Y;//information (status)

    public final static String SUP       = "UP";
    public final static String SDOWN     = "DOWN";
    public final static String SLEFT     = "LEFT";
    public final static String SRIGHT    = "RIGHT";

    public final static String SBACK     = "B-Cross-AltGr";
    public final static String SACTION   = "A-Circle-Space";
    public final static String SSCAN     = "X-Triangle-ShiftRight";
    public final static String SINFO     = "Y-Square-/";
    public final static String SPAUSE    = "START-Enter";
    public final static String SMENU     = "SELECT-Esc";

    public final static String SL1       = "L1-,";
    public final static String SR1       = "R1-.";

    public static void configJoystick(Context c) {
        String j = pref(c, "joystick", "1");
        if(j.compareTo("1") == 0) initCheapGenericHID1();
        if(j.compareTo("2") == 0) initCheapGenericHID2();
        if(j.compareTo("3") == 0) initCheapGenericHID3();
        if(j.compareTo("4") == 0) initCheapGenericHID4();
    }

    public static float getDirectionPressedX(InputEvent event, float def, boolean down) {
        float directionPressed = def;
        // If the input event is a MotionEvent, check its hat axis values.
        if (event instanceof MotionEvent) {
            MotionEvent e = (MotionEvent)event;
            float xaxis = joystickXL(e);

            // Check if the AXIS_HAT_X value is -1 or 1, and set the D-pad
            // LEFT and RIGHT direction accordingly.
            if (Float.compare(xaxis, -0.5f) < 0) {
                directionPressed = -1;
            } else if (Float.compare(xaxis, 0.5f) > 0) {
                directionPressed = 1;
            }
        }

        // If the input event is a KeyEvent, check its key code.
        else if (event instanceof KeyEvent) {
            if(down) {
                // Use the key code to find the D-pad direction.
                KeyEvent keyEvent = (KeyEvent) event;
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    directionPressed = -1;
                } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    directionPressed = 1;
                }
            } else {
                // Use the key code to find the D-pad direction.
                KeyEvent keyEvent = (KeyEvent) event;
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    directionPressed = 0;
                } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    directionPressed = 0;
                }
            }
        }
        return directionPressed;
    }

    public static float getDirectionPressedY(InputEvent event, float def, boolean down) {
        float directionPressed = def;
        // If the input event is a MotionEvent, check its hat axis values.
        if (event instanceof MotionEvent) {
            MotionEvent e = (MotionEvent)event;
            float yaxis = joystickYL(e);

            // Check if the AXIS_HAT_Y value is -1 or 1, and set the D-pad
            // UP and DOWN direction accordingly.
            if (Float.compare(yaxis, -0.5f) < 0) {
                directionPressed = -1;
            } else if (Float.compare(yaxis, 0.5f) > 0) {
                directionPressed = 1;
            }
        }

        // If the input event is a KeyEvent, check its key code.
        else if (event instanceof KeyEvent) {
            if(down) {
                // Use the key code to find the D-pad direction.
                KeyEvent keyEvent = (KeyEvent) event;
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    directionPressed = -1;
                } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    directionPressed = 1;
                }
            } else {
                // Use the key code to find the D-pad direction.
                KeyEvent keyEvent = (KeyEvent) event;
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    directionPressed = 0;
                } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    directionPressed = 0;
                }
            }
        }
        return directionPressed;
    }

    static KeyEvent lastButton = null;

    public static boolean isButton(KeyEvent event, int keyCode, boolean paused,
                                   boolean downEvent) {
        KeyEvent k = (KeyEvent)event;
        if(k.getRepeatCount() == 0) {
            int c = k.getKeyCode();
            if(c == KeyEvent.KEYCODE_ENTER) c = PAUSE;
            if(c == KeyEvent.KEYCODE_SPACE) c = A;
            if(c == KeyEvent.KEYCODE_ALT_RIGHT) c = B;
            if(c == KeyEvent.KEYCODE_SHIFT_RIGHT) c = X;
            if(c == KeyEvent.KEYCODE_SLASH) c = Y;
            if(c == KeyEvent.KEYCODE_A) c = A;
            if(c == KeyEvent.KEYCODE_B) c = B;
            if(c == KeyEvent.KEYCODE_X) c = X;
            if(c == KeyEvent.KEYCODE_Y) c = Y;
            if(c == KeyEvent.KEYCODE_ESCAPE) c = MENU;
            if(c == KeyEvent.KEYCODE_COMMA) c = L1;
            if(c == KeyEvent.KEYCODE_PERIOD) c = R1;
            if(c == CENTER) c = A;//primary action android suggestion
            if(c == KeyEvent.KEYCODE_MENU) c = MENU;//map suggestion
            if(c == L2) c = L1;//map suggestion
            if(c == R2) c = R1;//map suggestion
            if(paused) {
                if(c == A) c = MENU;//if no SELECT button?
                if(c == B) c = BACK;//BACK exits pause
                if(c == X) c = META;//META for something needing it
                //with right gaming logic this should make easy pause exit
            }
            if(downEvent) {
                if (c == INFO) {//only key down
                    if (lastButton != k) {
                        if (lastButton != null && keyCode == PAUSE) {
                            c = PAUSE;//a pause mechanism
                        }
                        lastButton = k;//new event
                    }
                } else {
                    lastButton = null;
                }
            }
            if(c == keyCode)  {
                return true;
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
    static void initCheapGenericHID1() {//SNES USB
        A        = KeyEvent.KEYCODE_BUTTON_4;//primary
        B        = KeyEvent.KEYCODE_BUTTON_3;//exit/back
        X        = KeyEvent.KEYCODE_BUTTON_2;
        Y        = KeyEvent.KEYCODE_BUTTON_1;
        L1       = KeyEvent.KEYCODE_BUTTON_5;
        R1       = KeyEvent.KEYCODE_BUTTON_6;
        L2       = KeyEvent.KEYCODE_BUTTON_5;//duplicate
        R2       = KeyEvent.KEYCODE_BUTTON_6;

        //special action buttons (for menus and pause)
        PAUSE    = KeyEvent.KEYCODE_BUTTON_8;
        MENU     = KeyEvent.KEYCODE_BUTTON_7;//not all controllers?
        //NB. paused and ACTION is MENU?
        fixButtons();
    }

    static void initCheapGenericHID2() {//PS1 USB / Logitech USB
        A        = KeyEvent.KEYCODE_BUTTON_3;//primary
        B        = KeyEvent.KEYCODE_BUTTON_2;//exit/back
        X        = KeyEvent.KEYCODE_BUTTON_4;
        Y        = KeyEvent.KEYCODE_BUTTON_1;
        L1       = KeyEvent.KEYCODE_BUTTON_5;
        R1       = KeyEvent.KEYCODE_BUTTON_6;
        L2       = KeyEvent.KEYCODE_BUTTON_7;
        R2       = KeyEvent.KEYCODE_BUTTON_8;

        //special action buttons (for menus and pause)
        PAUSE    = KeyEvent.KEYCODE_BUTTON_10;
        MENU     = KeyEvent.KEYCODE_BUTTON_9;//not all controllers?
        //NB. paused and ACTION is MENU?
        fixButtons();
    }

    static void initCheapGenericHID3() {//PS with adapter generic USB
        A        = KeyEvent.KEYCODE_BUTTON_2;//primary
        B        = KeyEvent.KEYCODE_BUTTON_3;//exit/back
        X        = KeyEvent.KEYCODE_BUTTON_1;
        Y        = KeyEvent.KEYCODE_BUTTON_4;
        L1       = KeyEvent.KEYCODE_BUTTON_7;
        R1       = KeyEvent.KEYCODE_BUTTON_8;
        L2       = KeyEvent.KEYCODE_BUTTON_5;
        R2       = KeyEvent.KEYCODE_BUTTON_6;

        //special action buttons (for menus and pause)
        PAUSE    = KeyEvent.KEYCODE_BUTTON_10;
        MENU     = KeyEvent.KEYCODE_BUTTON_9;//not all controllers?
        //NB. paused and ACTION is MENU?
        fixButtons();
    }

    static void initCheapGenericHID4() {//NES USB
        A        = KeyEvent.KEYCODE_BUTTON_2;//primary
        B        = KeyEvent.KEYCODE_BUTTON_1;//exit/back
        X        = KeyEvent.KEYCODE_BUTTON_3;
        Y        = KeyEvent.KEYCODE_BUTTON_4;
        L1       = KeyEvent.KEYCODE_BUTTON_5;//invalid TODO:
        R1       = KeyEvent.KEYCODE_BUTTON_6;
        L2       = KeyEvent.KEYCODE_BUTTON_5;
        R2       = KeyEvent.KEYCODE_BUTTON_6;

        //special action buttons (for menus and pause)
        PAUSE    = KeyEvent.KEYCODE_BUTTON_7;
        MENU     = KeyEvent.KEYCODE_BUTTON_8;//not all controllers?
        //NB. paused and ACTION is MENU?
        fixButtons();
    }

    static void fixButtons() {
        BACK     = B;//easy check back
        ACTION   = A;
        SCAN     = X;
        INFO     = Y;
    }

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
