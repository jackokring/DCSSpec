package uk.co.kring.android.dcs;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import uk.co.kring.android.dcs.statics.CodeStatic;

public class DCSActivity extends AppCompatActivity {

    int position;
    int codes[];
    CodeStatic dcs = CodeStatic.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back
        Bundle b = getIntent().getExtras();
        position = b.getInt("id");

        ((TextView) findViewById(R.id.dcs_group))
                .setText(String.valueOf(position));
        ((TextView) findViewById(R.id.dcs_code))
                .setText(dcs.signed((int)getItemId(position)));
        ((TextView) findViewById(R.id.dcs_letter))
                .setText(dcs.humanString((int)getItemId(position)));
        ((TextView) findViewById(R.id.dcs_members))
                .setText(dcs.alternates(position));
        //data per code
        ((TextView) findViewById(R.id.dcs_binary))
                .setText(dcs.asBinary((int)getItemId(position)));
    }

    public long getItemId(int i) {
        return dcs.primaries[i];
    }
}
