package me.vittorio_io.openhostseditor.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.io.File;

import me.vittorio_io.openhostseditor.R;
import me.vittorio_io.openhostseditor.fragments.backup.Fragment;
import me.vittorio_io.openhostseditor.fragments.backup.RecyclerViewAdapter;
import me.vittorio_io.openhostseditor.model.HostsManager;

public class RestoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);

        RecyclerView viewById = (RecyclerView) findViewById(R.id.backup_list);

        try {
            viewById.setAdapter(iCanHazAdapter());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RecyclerViewAdapter iCanHazAdapter() {
        return new RecyclerViewAdapter(HostsManager.getBackupList(), new Fragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(File item, int position) {
                switch (position) {
                    case 0: // row pressed

                        return;
                    case 1: // restore pressed
                        HostsManager.writeFromFile(item);
                        return;
                    case 2: // delete pressed
                        if (item.delete()) {
                            haveASnack("Backup file deleted.");
                            RecyclerView viewById = (RecyclerView) findViewById(R.id.backup_list);
                            viewById.setAdapter(iCanHazAdapter());
                        } else {
                            haveASnack("Failed to remove file.");
                        }
                        return;
                    default:
                        Log.e("err", "Unhandled code returned.");
                }
            }
        });
    }


    public void haveASnack(String message) {
        System.out.println(message);
        View view = getWindow().getDecorView().getRootView();

        try {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
