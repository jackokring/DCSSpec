package uk.co.kring.android.dcs.statics;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import uk.co.kring.android.dcs.R;

public class UtilStatic {

    public static void dialog(Context here) {
        AlertDialog.Builder builder = new AlertDialog.Builder(here);
        builder.setTitle("Test dialog");
        builder.setIcon(R.drawable.ic_launcher_foreground);
        builder.setMessage("Content");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Do something
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        //alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }
}
