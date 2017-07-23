package me.vittorio_io.openhostseditor.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.vittorio_io.openhostseditor.R;
import me.vittorio_io.openhostseditor.fragments.hostrule.Fragment;
import me.vittorio_io.openhostseditor.fragments.hostrule.HidingScrollListener;
import me.vittorio_io.openhostseditor.fragments.hostrule.RecyclerViewAdapter;
import me.vittorio_io.openhostseditor.model.HostRule;
import me.vittorio_io.openhostseditor.model.HostsManager;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_BACKUP_ACTION = 0;
    public static List<HostRule> rules;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewRuleActivity();
            }
        });

        refreshList();
    }

    private void refreshList() {
        try {
            if (rules == null) {
                rules = HostsManager.readFromFile();
            } else if (!rules.equals(HostsManager.readFromFile())) {
                File file = HostsManager.saveListToTempFile(getApplicationContext(), rules);
                HostsManager.writeFromFile(file);
                rules = HostsManager.readFromFile();
                haveASnack("Changes saved to disk.");
            }
        } catch (IOException e) {
            // TODO error message
            haveASnack("Failed to read original hosts file.");
            e.printStackTrace();
        }

        RecyclerViewAdapter listViewAdapter = new RecyclerViewAdapter(rules, new Fragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(HostRule item) {
                openEditRuleActivity(rules.indexOf(item));
            }
        });

        RecyclerView listView = (RecyclerView) findViewById(R.id.list);

        listView.swapAdapter(listViewAdapter, true);

        // source: https://stackoverflow.com/a/39145431
        listView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                fab.animate().translationY(fab.getHeight() + lp.bottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
            }

            @Override
            public void onShow() {
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });
    }

    private void openEditRuleActivity(int index) {
        Intent intent = new Intent(MainActivity.this, EditRuleActivity.class);
        Bundle b = new Bundle();
        b.putInt("key", index); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.push_right_out, R.anim.push_right_in);
    }

    private void openNewRuleActivity() {
        openEditRuleActivity(-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings: {
                return true;
            }
            case R.id.action_backup: {
                int permissionCheck = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);

                getPermissionAndExecute(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_BACKUP_ACTION);
                return true;
            }
            case R.id.action_edit: {
                openActivityForResult(ManualEditActivity.class);
                return true;
            }
            case R.id.action_restore: {
                openActivityForResult(RestoreActivity.class);
                return true;
            }
            case R.id.action_about: {
                openActivityForResult(AboutActivity.class);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void openActivityForResult(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(MainActivity.this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onResume() {
        //refreshList();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshList();
    }

    public void getPermissionAndExecute(String permission, int code) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permission)) {

                haveASnack("I need to explain permission.");

                askPermissionBase(permission, code);

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                askPermissionBase(permission, code);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            askPermissionBase(permission, code);
        }

    }

    private void askPermissionBase(String permission, int code) {
        ActivityCompat.requestPermissions(this,
                new String[]{permission},
                code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case WRITE_BACKUP_ACTION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    try {
                        HostsManager.saveBackup();
                        haveASnack("Backup saved!");
                    } catch (IOException e) {
                        haveASnack("Failed to save a backup");
                        e.printStackTrace();
                    }

                } else {
                    haveASnack("Permission denied. No changes made.");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
