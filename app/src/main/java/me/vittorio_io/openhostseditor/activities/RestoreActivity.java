package me.vittorio_io.openhostseditor.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
                        Intent intent = new Intent(RestoreActivity.this, PreviewActivity.class);
                        Bundle b = new Bundle();
                        b.putSerializable("file", item); //Your id
                        intent.putExtras(b); //Put your id to your next Intent
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivityForResult(intent, 0);
                        return;
                    case ACTION_LOAD_BACKUPS: // restore pressed
                        if (HostsManager.writeFromFile(item)) {
                            finish();
                        } else {
                            haveASnack(getString(R.string.message_backup_not_restored));
                        }
                        return;
                    case 2: // delete pressed
                        if (item.delete()) {
                            haveASnack(getString(R.string.message_backup_deleted));
                            RecyclerView viewById = (RecyclerView) findViewById(R.id.backup_list);
                            viewById.setAdapter(iCanHazAdapter());
                        } else {
                            haveASnack(getString(R.string.message_backup_not_deleted));
                        }
                        return;
                    default:
                        Log.e("err", "Unhandled code returned.");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getPermissionAndExecute(Manifest.permission.WRITE_EXTERNAL_STORAGE, ACTION_LOAD_BACKUPS);
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
                    haveASnack(getString(R.string.message_permission_denied));
                    finish();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
