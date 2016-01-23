package com.example.yomac_000.rsrpechhulp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by yomac_000 on 2-1-2016.
 */
public class RehabilitationMenu extends AppCompatActivity {
    Button btnBreakDownMapScreen;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rehabilitation_menu);
        btnBreakDownMapScreen = (Button) findViewById(R.id.LinkToBreakDownMapScreen);
        View.OnClickListener myOnlyhandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.LinkToBreakDownMapScreen:
                        intent = new Intent(getApplicationContext(),
                                BreakDownOnMaps.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        };
        btnBreakDownMapScreen.setOnClickListener(myOnlyhandler);
    }
}
