package me.vittorio_io.openhostseditor.activities;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;

import me.vittorio_io.openhostseditor.R;
import me.vittorio_io.openhostseditor.fragments.backup.Fragment;
import me.vittorio_io.openhostseditor.fragments.backup.RecyclerViewAdapter;
import me.vittorio_io.openhostseditor.model.HostsManager;

public class RestoreActivity extends BaseActivity {

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
}
