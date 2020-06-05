package uk.co.kring.android.dcs;

import android.Manifest;
import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;
import androidx.core.app.ActivityCompat;
import uk.co.kring.android.dcs.statics.CodeStatic;
import uk.co.kring.android.dcs.statics.UtilStatic;

public class DCSListActivity extends AppCompatActivity {

    MyAdapter la = new MyAdapter();
    CodeStatic dcs = CodeStatic.getInstance();

    // Requesting permission to RECORD_AUDIO
    boolean permissionToRecordAccepted = false;
    static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    String permissions[] = { Manifest.permission.RECORD_AUDIO };
    AudioService audio;

    ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            audio = ((AudioService.MyBinder)service).getService();
            audio.setPermission(permissionToRecordAccepted);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            audio = null;
        }
    };

    //==================== PUBLIC INTERFACE
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        //if(!permissionToRecordAccepted) finish();
        Intent intent = new Intent(this, AudioService.class);
        //intent.putExtra("record", permissionToRecordAccepted);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        //startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs_list);
        ((ListView)findViewById(R.id.dcs_list)).setAdapter(la);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back
        UtilStatic.googleAPICheck(this);
        UtilStatic.postAnalyticContext(this);//init analytics
        ActivityCompat.requestPermissions(this, permissions,
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    /* protected void onResume() {
        super.onResume();
        UtilStatic.googleAPICheck(this);
    } */

    @Override
    protected void onDestroy() {
        if(audio != null) audio.stopAudioAll();//end processing on exit
        unbindService(connection);
        super.onDestroy();
    }

    //================================== MENU ITEMS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dcs_list_menu, menu);
        return true;
    }

    public void onShowMessagesAction(MenuItem mi) {
        Intent intent = new Intent(DCSListActivity.this,
                MessageListActivity.class);
        startActivity(intent);
    }

    public void onShowCommunicateAction(MenuItem item) {
        Intent intent = new Intent(DCSListActivity.this,
                SurfaceActivity.class);
        startActivity(intent);
    }

    public void onShowDSPAction(MenuItem item) {
        Intent intent = new Intent(DCSListActivity.this,
                Activity.class);
        startActivity(intent);
    }

    public void onShowSettingsAction(MenuItem item) {
        Intent intent = new Intent(DCSListActivity.this,
                SettingsActivity.class);
        startActivity(intent);
    }

    public void onShowAboutAction(MenuItem item) {
        UtilStatic.dialog(this, R.string.about_title,
                R.drawable.ic_about,
                getString(R.string.about) + getString(R.string.version),
                new DialogInterface.OnClickListener() {//ok
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }, null, null);
    }

    //============================== PACKAGED
    class MyAdapter extends BaseAdapter {

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_dcs_list, container,
                        false);
            }

            ((TextView) convertView.findViewById(R.id.dcs_group))
                    .setText(String.valueOf(position));
            ((TextView) convertView.findViewById(R.id.dcs_code))
                    .setText(dcs.signed((int)getItemId(position)));
            ((TextView) convertView.findViewById(R.id.dcs_letter))
                    .setText(dcs.humanStringOfRX((int)getItemId(position)));
            ((TextView) convertView.findViewById(R.id.dcs_members))
                    .setText(dcs.alternates(position));
            convertView.setClickable(true);
            convertView.setOnClickListener(new AdapterView.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DCSListActivity.this,
                            DCSActivity.class);
                    intent.putExtra("id", position);
                    startActivity(intent);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return dcs.primaries.length;
        }

        @Override
        public Object getItem(int i) {//not used
            return null;
        }

        @Override
        public long getItemId(int i) {
            return dcs.primaries[i];
        }
    }
}
