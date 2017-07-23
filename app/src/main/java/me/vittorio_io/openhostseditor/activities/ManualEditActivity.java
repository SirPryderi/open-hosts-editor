package me.vittorio_io.openhostseditor.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import me.vittorio_io.openhostseditor.R;
import me.vittorio_io.openhostseditor.model.HostRule;
import me.vittorio_io.openhostseditor.model.HostsManager;

public class ManualEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_edit);

        loadRules();
    }

    private void loadRules() {
        EditText editor = (EditText) findViewById(R.id.editText);

        editor.setHorizontallyScrolling(true);
        editor.setHorizontalFadingEdgeEnabled(true);
        editor.setHorizontalScrollBarEnabled(true);
        editor.setVerticalScrollBarEnabled(true);

        try {
            editor.setText(HostsManager.writeToString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateRules() {
        EditText editor = (EditText) findViewById(R.id.editText);

        String[] lines = editor.getText().toString().split("\n");
        ArrayList<HostRule> rules = new ArrayList<>();

        for (String line : lines) {
            try {
                HostRule rule = HostRule.fromHostLine(line);

                if (rule != null) {
                    rules.add(rule);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        MainActivity.rules = rules;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                this.updateRules();
                finish();
                return true;
            case R.id.action_restore:
                this.loadRules();
                return true;
        }
        return false;
    }
}
