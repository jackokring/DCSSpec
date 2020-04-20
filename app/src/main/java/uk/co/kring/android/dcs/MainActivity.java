package uk.co.kring.android.dcs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {

    MyAdapter la = new MyAdapter();
    int codes[] = new int[512];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((ListView)findViewById(R.id.list_view)).setAdapter(la);
        //TODO: make codes

        Intent intent =new Intent(this, MyService.class);
        intent.putExtra("codes", codes);//valid code set
        startService(intent);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
            }

            ((TextView) convertView.findViewById(R.id.text_view_id))
                    .setText((String)getItem(position));
            convertView.setClickable(true);
            convertView.setOnClickListener(new AdapterView.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, DCSSpecActivity.class);
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
}
