package uk.co.kring.android.dcs;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;

public class DCSListActivity extends AppCompatActivity {

    MyAdapter la = new MyAdapter();
    int codes[] = new int[512];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs);
        ((ListView)findViewById(R.id.dcs_list)).setAdapter(la);
        //TODO: make codes

        Intent intent =new Intent(this, MyService.class);
        intent.putExtra("codes", codes);//valid code set
        startService(intent);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.dcs_list_item, container,
                        false);
            }

            ((TextView) convertView.findViewById(R.id.dcs_list_text))
                    .setText((String)getItem(position));
            convertView.setClickable(true);
            convertView.setOnClickListener(new AdapterView.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(DCSListActivity.this,
                            DCSActivity.class);
                    intent.putExtra("id", position);
                    intent.putExtra("codes", codes);//valid code set
                    startActivity(intent);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return codes.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return codes[i];
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
}
