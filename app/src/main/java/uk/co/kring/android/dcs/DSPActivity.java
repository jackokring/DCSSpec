package uk.co.kring.android.dcs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class DSPActivity extends AppCompatActivity {

    MyAdapter la = new MyAdapter();

    String[][] labels = {
            { "Centre Frequency", "Resonant Q", "Invert", "Focus", "Low High Mix" }
    };

    int algorithm = 0;
    AudioService audio;

    ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            audio = ((AudioService.MyBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            audio = null;
        }
    };

    //============================ PUBLIC INTERFACE
    public static final int DSP_FIVE_FILTER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs_list);
        la.makeControls();
        ((ListView)findViewById(R.id.dcs_list)).setAdapter(la);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back
        Intent intent = new Intent(this, AudioService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    //=========================== MENU ITEMS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dsp_menu, menu);
        return true;
    }

    public void onShowMuteAction(MenuItem item) {
        if(audio != null) audio.setMute(true);
    }

    public void onShowPassThroughAction(MenuItem item) {
        if(audio != null) audio.setMute(false);
    }

    public void onShowFilterAction(MenuItem item) {
        algorithm = (algorithm + 1) % labels.length;
        ((ListView)findViewById(R.id.dcs_list)).invalidate();
        if(audio != null) audio.setDSPAlg(algorithm, la.controls);
    }

    //============================= PACKAGED
    class MyAdapter extends BaseAdapter {

        public void makeControls() {
            int m = 0;
            for(int i = 0; i < labels.length; ++i) {
                if(m < labels[i].length) m = labels[i].length;
            }
            controls = new int[m];
        }

        int controls[];

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_dsp_list, container,
                        false);
            }

            ((TextView) convertView.findViewById(R.id.name))
                    .setText(labels[algorithm][position]);
            SeekBar sb = ((SeekBar) convertView.findViewById(R.id.control));
            sb.setMax(Integer.MAX_VALUE);
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    controls[position] = i;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return labels[algorithm].length;//number of labels
        }

        @Override
        public Object getItem(int i) {//not used
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }
}
