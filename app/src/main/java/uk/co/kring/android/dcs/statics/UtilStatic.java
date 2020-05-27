package uk.co.kring.android.dcs.statics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.widget.Toast;
import uk.co.kring.android.dcs.R;

import java.io.IOException;
import java.io.InputStream;

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

    public static Bitmap getBitmap(Context here, String res) throws IOException {
        InputStream i = here.getAssets().open(res);
        f = BitmapFactory.decodeStream(i);
        i.close();
        return f;
    }

    static Bitmap f;
    final static int width = 8;
    final static int height = 10;

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
}
