package uk.co.kring.android.dcs;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import uk.co.kring.android.dcs.mvm.FileProcessor;
import uk.co.kring.android.dcs.statics.UtilStatic;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

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
                    //i.close();
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

    static LinkedBlockingQueue<Thread> lock = new LinkedBlockingQueue<Thread>();

    public void process(InputStream is, OutputStream os, String oldName, FileProcessor fp) {
        Thread onError = new Thread() {
            public void run() {
                error();
            }
        };
        Thread background = new Thread() {
            public void run() {
                try {
                    lock.put(this);
                    while (lock.peek() != this) Thread.yield();
                    fp.headers(is, os, oldName);//anything simple before - headers?
                    fp.background(is, os);
                    os.flush();
                    os.close();
                    is.close();
                    lock.take();
                } catch (Exception e) {
                    //error();
                    runOnUiThread(onError);//thread safe
                    while(lock.peek() != null) {
                        try {
                            lock.take().interrupt();
                        } catch(Exception f) {
                            //fine as expected
                        }
                    }
                }
            }
        };
        background.start();
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
        if(lock.peek() != null) {
            waitFor();
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

    void waitFor() {
        UtilStatic.dialog(this, R.string.share_title,
                R.drawable.ic_share,
                getString(R.string.share_wait),
                new DialogInterface.OnClickListener() {//ok
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }, null, null);
    }

    void handleFile(InputStream in, String ext, FileProcessor fp) {
        String old = processedName;
        processedName = fp.newName(old, ext);
        File path = new File(getExternalFilesDir(null), "processed");
        File newFile = new File(path, processedName);
        try {
            OutputStream os = new FileOutputStream(newFile);
            process(in, os, old, fp);//use old name as useful maybe
            //os.flush();
            //os.close();
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
}
