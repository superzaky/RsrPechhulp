package com.example.yomac_000.rsrpechhulp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yomac_000 on 26-1-2016.
 */

public class AboutRsr extends AppCompatActivity {
    private View vLinkToRehabMenu;
    private View imgBtnAboutScreen;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_rsr);

        TextView tv = (TextView) findViewById(R.id.about_text);
        tv.setText(Html.fromHtml(getString(R.string.about_text)));
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        vLinkToRehabMenu = findViewById(R.id.tvLinkToRehabMenu);
        imgBtnAboutScreen = findViewById(R.id.LinkToAboutScreen);
        View.OnClickListener myOnlyhandler = new View.OnClickListener() {
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.tvLinkToRehabMenu:
                        intent = new Intent(getApplicationContext(),
                                RehabilitationMenu.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        };
        vLinkToRehabMenu.setOnClickListener(myOnlyhandler);
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(getApplicationContext(),
                RehabilitationMenu.class);
        startActivity(intent);
        finish();
    }
}
