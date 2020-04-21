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
    int codes[] = new int[1024];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        genCodes();
        setContentView(R.layout.activity_dcs);
        ((ListView)findViewById(R.id.dcs_list)).setAdapter(la);
        Intent intent = new Intent(this, MyService.class);
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

    //CODE GENERATION
    void genCodes() {
        for(int i = 0; i < 512; ++i) {
            codes[i] = i + (4 << 9);//base code
            boolean C1 = (i & 1) != 0;
            boolean C2 = (i & 2) != 0;
            boolean C3 = (i & 4) != 0;
            boolean C4 = (i & 8) != 0;
            boolean C5 = (i & 16) != 0;
            boolean C6 = (i & 32) != 0;
            boolean C7 = (i & 64) != 0;
            boolean C8 = (i & 128) != 0;
            boolean C9 = (i & 256) != 0;
            boolean P[] = new boolean[11];
            P[0] = C1 ^ C2 ^ C3 ^ C4 ^ C5 ^ C8;
            P[1] = !(C2 ^ C3 ^ C4 ^ C5 ^ C6 ^ C9);
            P[2] = C1 ^ C2 ^ C6 ^ C7 ^ C8;
            P[3] = !(C2 ^ C3 ^ C7 ^ C8 ^ C9);
            P[4] = !(C1 ^ C2 ^ C5 ^ C9);
            P[5] = !(C1 ^ C4 ^ C5 ^ C6 ^ C8);
            P[6] = C1 ^ C3 ^ C4 ^ C6 ^ C7 ^ C8 ^ C9;
            P[7] = C2 ^ C4 ^ C5 ^ C7 ^ C8 ^ C9;
            P[8] = C3 ^ C5 ^ C6 ^ C8 ^ C9;
            P[9] = !(C4 ^ C6 ^ C7 ^ C9);
            P[10] = !(C1 ^ C2 ^ C3 ^ C4 ^ C7);
            int c = 0;
            for(int j = 0; j < 11; ++j) {
                if(P[j]) c+= (1 << j);
            }
            c <<= 12;//SHIFT
            codes[i] += c;
            codes[i + 512] = (~codes[i]) & 0x8FFFFF;//23 BIT INVERSE
        }
        int c = 0;//BASE GROUP
        for(int i = 0; i < 1024; ++i) {
            if(codes[i] >> 23 != 0) {
                continue;//already assigned group
            }
            int s = codes[i];//current code
            codes[i] += ((++c) << 23);//assign new group
            for(int j = 0; j < 22; ++j) {
                s = (s << 1) + (s >> 22);//rotate
                if(codes[s & 1023] >> 23 != 0) {
                    continue;//already assigned group
                }
                //check same code
                if(codes[s & 1023] != s) {
                    continue;//is not same code
                }
                codes[i] += (c << 23);//assign same group
            }
        }
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
