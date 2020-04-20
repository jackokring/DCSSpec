package uk.co.kring.android.dcs;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MessageActivity extends AppCompatActivity {

    int id;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        Bundle b = getIntent().getExtras();
        id = b.getInt("id");
        title = b.getString("title");
    }
}
