package me.vittorio_io.openhostseditor.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.File;

import me.vittorio_io.openhostseditor.R;
import me.vittorio_io.openhostseditor.fragments.backup.Fragment;
import me.vittorio_io.openhostseditor.fragments.backup.RecyclerViewAdapter;
import me.vittorio_io.openhostseditor.model.HostsManager;

public class RestoreActivity extends BaseActivity {

    public static final int ACTION_LOAD_BACKUPS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore);

        getPermissionAndExecute(Manifest.permission.WRITE_EXTERNAL_STORAGE, ACTION_LOAD_BACKUPS);
    }

    private RecyclerViewAdapter iCanHazAdapter() {
        return new RecyclerViewAdapter(HostsManager.getBackupList(), new Fragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(File item, int position) {
                switch (position) {
                    case 0: // row pressed

                        return;
                    case ACTION_LOAD_BACKUPS: // restore pressed
                        if (HostsManager.writeFromFile(item)) {
                            finish();
                        } else {
                            haveASnack("Backup not restored.");
                        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case ACTION_LOAD_BACKUPS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    RecyclerView viewById = (RecyclerView) findViewById(R.id.backup_list);

                    try {
                        viewById.setAdapter(iCanHazAdapter());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    haveASnack("Permission denied. Nothing to do here.");
                    finish();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
