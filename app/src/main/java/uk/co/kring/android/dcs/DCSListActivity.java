package uk.co.kring.android.dcs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class DCSListActivity extends AppCompatActivity {

    MyAdapter la = new MyAdapter();
    CodeStatic dcs = CodeStatic.getInstance();

    // Requesting permission to RECORD_AUDIO
    public boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String permissions[] = { Manifest.permission.RECORD_AUDIO };

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
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("record", permissionToRecordAccepted);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs_list);
        ((ListView)findViewById(R.id.dcs_list)).setAdapter(la);
        ActivityCompat.requestPermissions(this, permissions,
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

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

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, MyService.class));
        super.onDestroy();
    }

    //MENU ITEMS
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
}
