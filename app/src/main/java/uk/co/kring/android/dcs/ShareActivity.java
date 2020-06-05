package uk.co.kring.android.dcs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import uk.co.kring.android.dcs.statics.UtilStatic;

import java.io.*;
import java.util.UUID;

import static java.security.AccessController.getContext;

public class ShareActivity extends AppCompatActivity {

    Uri current;
    String processedName;

    //============================= PUBLIC INTERFACE
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
                    InputStream i;
                    String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
                    handleFile(i = getContentResolver().openInputStream(current),
                            ext, new FileProcessor());
                    i.close();
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

    //================================ MENU ITEMS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
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

    //=========================== PACKAGED
    void error() {
        UtilStatic.dialog(this, R.string.share_title,
                R.drawable.ic_share,
                getString(R.string.share),
                new DialogInterface.OnClickListener() {//ok
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }, null, null);
    }

    void handleFile(InputStream in, String ext, FileProcessor fp) {
        String old = processedName;
        processedName = UUID.randomUUID().toString() + ext;
        File path = new File(getExternalFilesDir(null), "processed");
        File newFile = new File(path, processedName);
        try {
            OutputStream os = new FileOutputStream(newFile);
            fp.process(in, os, old);//use old name as useful maybe
            os.flush();
            os.close();
        } catch(Exception e) {
            error();
        }
    }

    InputStream openFile() {
        if(processedName == null) {
            error();
            return null;
        }
        File path = new File(getExternalFilesDir(null), "processed");
        File newFile = new File(path, processedName);
        newFile.deleteOnExit();//keeping clean
        try {
            return new FileInputStream(newFile);
        } catch(Exception e) {
            error();
            return null;
        }
    }

    void renameFile(String name) {
        if(processedName == null || name == null) {
            error();
        }
        File path = new File(getExternalFilesDir(null), "processed");
        File newFile = new File(path, processedName);
        File newerFile = new File(path, name);
        try {
            if(newFile.renameTo(newerFile)) {
                processedName = name;
            }
        } catch(Exception e) {
            error();
        }
    }

    class FileProcessor {
        public void process(InputStream is, OutputStream os, String oldName)
                throws IOException {
            int i;
            while((i = is.read()) != -1) {
                os.write(i);//copy
            }
        }
    }
}
