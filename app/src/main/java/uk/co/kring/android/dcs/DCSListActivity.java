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
    CodeStatic dcs = CodeStatic.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs);
        ((ListView)findViewById(R.id.dcs_list)).setAdapter(la);
        Intent intent = new Intent(this, MyService.class);
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
        public Object getItem(int i) {
            //TODO: other data?
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
}
