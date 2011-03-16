package com.folone.replcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class History extends Activity {

    private static final String REPL = "repl";

    private TableLayout table;
    private Button delBtn;
    private DBHelper db;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        table = (TableLayout) findViewById(R.id.history);
        delBtn = (Button) findViewById(R.id.history_del);

        db = new DBHelper(getApplicationContext());
        final Map<String, String> history = db.getHistory();
        Set<String> scripts = history.keySet();
        final Map<CheckBox, List<String>> checksMap = new HashMap<CheckBox, List<String>>();

        for (final String script : scripts) {
            View view;
            LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.element, null);

            CheckBox deleteChk = (CheckBox) view.findViewById(R.id.elem_del);
            List<String> params = new ArrayList<String>();
            params.add(script);
            params.add(history.get(script));
            checksMap.put(deleteChk, params);

            TableRow tr = (TableRow) view.findViewById(R.id.row);
            TextView srcTxt = (TextView) view.findViewById(R.id.left);
            TextView envTxt = (TextView) view.findViewById(R.id.right);

            srcTxt.setText(script);
            envTxt.setText(history.get(script));

            tr.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    SharedPreferences prefs = getSharedPreferences(REPL, 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("script", script);
                    editor.putString("environment", history.get(script));
                    editor.commit();

                    Intent repl = new Intent(getApplicationContext(),
                            REPL.class);
                    startActivity(repl);
                }

            });
            table.addView(view);
        }

        delBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Set<CheckBox> checkboxes = checksMap.keySet();
                if (!checkboxes.isEmpty()) {
                    for (CheckBox checkbox : checkboxes) {
                        if (checkbox.isChecked()) {
                            db.removeScript(checksMap.get(checkbox).get(0),
                                    checksMap.get(checkbox).get(1));
                        }
                    }
                    onCreate(savedInstanceState);
                }
            }
        });

    }

    /**
     * Menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);

        // Clear preferences
        SharedPreferences prefs = getSharedPreferences(REPL, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        return result;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_clear:
            // clear
            db.clearDB();

            // Go back
            Intent repl = new Intent(getApplicationContext(), REPL.class);
            startActivity(repl);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
