package uk.co.kring.android.dcs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import uk.co.kring.android.dcs.room.AppDatabase;

public class MessageListActivity extends AppCompatActivity {

    MyAdapter la = new MyAdapter();
    AppDatabase db = AppDatabase.getInstance(getApplicationContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs);
        ((ListView)findViewById(R.id.message_list)).setAdapter(la);
        //TODO: ?
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public View getView(final int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.dcs_list_item, container, false);
            }

            ((TextView) convertView.findViewById(R.id.message_list_text))
                    .setText((String)getItem(position));
            convertView.setClickable(true);
            convertView.setOnClickListener(new AdapterView.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MessageListActivity.this,
                            MessageActivity.class);
                    intent.putExtra("id", position);
                    //TODO: title index
                    startActivity(intent);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }
}