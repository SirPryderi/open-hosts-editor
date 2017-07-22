package me.vittorio_io.openhostseditor.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        updateRules();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                updateRules();
                finish();
                return true;
        }
        return false;
    }
}
