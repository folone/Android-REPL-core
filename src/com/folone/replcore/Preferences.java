package com.folone.replcore;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class Preferences extends Activity {

    private static final String PREFS = "repl.prefs";

    private Button ok;
    private CheckBox save;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        SharedPreferences prefs = getSharedPreferences(PREFS, 0);

        save = (CheckBox) findViewById(R.id.prefs_save);
        save.setChecked(prefs.getBoolean("save_history", true));
        ok = (Button) findViewById(R.id.prefs_ok);
        ok.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(PREFS, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("save_history", save.isChecked());
                editor.commit();

                Intent repl = new Intent(getApplicationContext(), REPL.class);
                startActivity(repl);
            }
        });
    }
}
