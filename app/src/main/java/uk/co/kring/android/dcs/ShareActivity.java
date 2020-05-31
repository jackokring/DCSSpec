package uk.co.kring.android.dcs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import uk.co.kring.android.dcs.statics.UtilStatic;

import java.io.File;
import java.io.InputStream;

import static java.security.AccessController.getContext;

public class ShareActivity extends AppCompatActivity {

    Uri current;
    String processedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type != null) {
            current = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (current != null) {
                try {
                    handleFile(getContentResolver().openInputStream(current));
                } catch(Exception e) {
                    error();
                    finish();
                }
            } else {
                error();
                finish();//no file
            }
        }
    }

    public void handleFile(InputStream in) {
        //TODO: handle file
    }

    //MENU ITEMS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dsp_menu, menu);
        return true;
    }

    public void error() {
        UtilStatic.dialog(this, R.string.share_title,
                R.drawable.ic_share,
                getString(R.string.share),
                new DialogInterface.OnClickListener() {//ok
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }, null, null);
    }

    public void onShowShareAction(MenuItem item) {
        if(processedName == null) {
            error();
            return;
        }
        File path = new File(getExternalFilesDir(null), "processed");
        File newFile = new File(path, processedName);
        Uri contentUri = FileProvider.getUriForFile(this,
                "uk.co.kring.android.dcs.fileprovider", newFile);
        Intent i = new Intent();
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_STREAM, contentUri);
        i.setType(getContentResolver().getType(contentUri));
        startActivity(Intent.createChooser(i, null));
    }
}
