package uk.co.kring.android.dcs;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class DCSActivity extends AppCompatActivity {

    int id;
    int codes[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcs_detail);
        Bundle b = getIntent().getExtras();
        id = b.getInt("id");
        codes = b.getIntArray("codes");
        //show code
    }
}
