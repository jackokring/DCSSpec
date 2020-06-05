package uk.co.kring.android.dcs;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SurfaceActivity extends AppCompatActivity {

    //====================== PUBLIC INTERFACE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back
    }
}
