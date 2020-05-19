package uk.co.kring.android.dcs;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DCSActivity extends AppCompatActivity {

    int position;
    int codes[];
    CodeStatic dcs = CodeStatic.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs);
        Bundle b = getIntent().getExtras();
        position = b.getInt("id");

        ((TextView) findViewById(R.id.dcs_group))
                .setText(String.valueOf(position));
        ((TextView) findViewById(R.id.dcs_code))
                .setText(String.valueOf(dcs.signed((int)getItemId(position))));
        ((TextView) findViewById(R.id.dcs_letter))
                .setText(dcs.humanString((int)getItemId(position)));
        ((TextView) findViewById(R.id.dcs_members))
                .setText(dcs.alternates(position));
    }

    public long getItemId(int i) {
        return dcs.primaries[i];
    }
}
