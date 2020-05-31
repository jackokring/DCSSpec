package uk.co.kring.android.dcs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class DSPActivity extends AppCompatActivity {

    MyAdapter la = new MyAdapter();

    public int controlCount() {
        return 32;//number of controls
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs_list);
        la.makeControls();
        ((ListView)findViewById(R.id.dcs_list)).setAdapter(la);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back
    }

    class MyAdapter extends BaseAdapter {

        public int getValue(int idx) {
            return controls[idx];
        }

        public void setName(String s, int idx) {
            names[idx].setText(s);
        }

        public void makeControls() {
            controls = new int[getCount()];
            names = new TextView[getCount()];
            //TODO: initial control names and values
        }

        int controls[];
        TextView names[];

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_dsp_list, container,
                        false);
            }

            names[position] = ((TextView) convertView.findViewById(R.id.name));
            SeekBar sb = ((SeekBar) convertView.findViewById(R.id.control));
            sb.setMax(Integer.MAX_VALUE);
            sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    controls[position] = i;
                    //TODO: update service
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
            return controlCount();
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

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, AudioService.class));
        super.onDestroy();
    }

    //MENU ITEMS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dsp_menu, menu);
        return true;
    }

    public void onShowMuteAction(MenuItem item) {

    }

    public void onShowPassThroughAction(MenuItem item) {

    }

    public void onShowFilterAction(MenuItem item) {

    }
}
