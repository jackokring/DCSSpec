package uk.co.kring.android.dcs;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import uk.co.kring.android.dcs.statics.CodeStatic;
import uk.co.kring.android.dcs.statics.UtilStatic;

public class DSPActivity extends AppCompatActivity {

    MyAdapter la = new MyAdapter();
    //TODO: DSP algorithm selects and sliders
    CodeStatic dcs = CodeStatic.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs_list);
        ((ListView)findViewById(R.id.dcs_list)).setAdapter(la);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_dsp_list, container,
                        false);
            }

            convertView.setOnClickListener(new AdapterView.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DSPActivity.this,
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
        getMenuInflater().inflate(R.menu.dsp_menu, menu);
        return true;
    }
}
