package me.vittorio_io.openhostseditor.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import me.vittorio_io.openhostseditor.R;
import me.vittorio_io.openhostseditor.fragments.hostrule.Fragment;
import me.vittorio_io.openhostseditor.fragments.hostrule.HidingScrollListener;
import me.vittorio_io.openhostseditor.fragments.hostrule.RecyclerViewAdapter;
import me.vittorio_io.openhostseditor.model.ExecuteAsRootBase;
import me.vittorio_io.openhostseditor.model.HostRule;
import me.vittorio_io.openhostseditor.model.HostsManager;

public class MainActivity extends BaseActivity {

    private static final int WRITE_BACKUP_ACTION = 0;
    public static List<HostRule> rules;
    private FloatingActionButton fab;
    private String searchString = "";

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

        if (!ExecuteAsRootBase.isRootAvailable()) {
            findViewById(R.id.no_root_warning).setVisibility(View.VISIBLE);
        }

        refreshList(false);
    }

    private void refreshList(boolean searching) {
        try {
            if (rules == null) {
                rules = HostsManager.readFromFile();
            } else if (!rules.equals(HostsManager.readFromFile()) && !searching) {
                rules = HostsManager.readFromFile();
            }
        } catch (IOException e) {
            // TODO error message
            haveASnack(getString(R.string.message_failed_to_read_file));
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
        listView.addOnScrollListener(new HidingScrollListener() {
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

    private void searchDialog() {
        // Opens a Dialog with an EditText to search the host list

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.action_search);
        builder.setCancelable(false);

        final EditText edTxtSearchString = new EditText(this);
        edTxtSearchString.setText(searchString);
        edTxtSearchString.setSelection(edTxtSearchString.getText().length());
        builder.setView(edTxtSearchString);

        if(searchString.length() != 0) {
            builder.setNeutralButton(R.string.action_reset, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doSearch("");
                }
            });
        }
            builder.setPositiveButton(R.string.action_search, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (edTxtSearchString.length() != 0) {
                        doSearch(edTxtSearchString.getText().toString());
                    }
                }
            });

        builder.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void doSearch(String searchString) {
        // Change Visibility of the SearchBar
        final LinearLayout lin = (LinearLayout) findViewById(R.id.search_layout);

        if(searchString.length() != 0) {
            this.searchString = searchString;
            lin.setVisibility(View.VISIBLE);
            searchFilterList(searchString);
        } else {
            this.searchString = "";
            lin.setVisibility(View.GONE);
            refreshList(false);
        }

    }

    private void searchFilterList(String searchString) {
        // Handle the result of the searchDialog()

        // Handle the button action
        ImageButton action_search_cancel = (ImageButton) findViewById(R.id.action_search_cancel);
        action_search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch("");
            }
        });


        // Change TextView to show the current SearchString
        TextView t = (TextView) findViewById(R.id.search_view_text);
        t.setText(getString(R.string.action_searching, searchString));

        if(searchString.endsWith(" ")) {
            searchString = searchString.substring(0,searchString.length()-1);
        }
        this.searchString = searchString;

        // reset list before new search
        refreshList(false);

        for(int i=0;i<rules.size();i++) {
            if(!rules.get(i).getUrl().contains(searchString) && !rules.get(i).getIp().getHostAddress().contains(searchString)) {
                rules.remove(i);
                i -= 1;
            }
        }
        refreshList(true);
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
                // There is no way of launching the callback
                // without asking for the permission at the moment,
                // so it's pointless to check if the permission has been granted.
                // int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

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
            case R.id.action_search: {
                searchDialog();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        refreshList(false);
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
                        haveASnack(getString(R.string.message_backup_saved));
                    } catch (IOException e) {
                        haveASnack(getString(R.string.message_backup_not_saved));
                        e.printStackTrace();
                    }

                } else {
                    haveASnack(getString(R.string.message_permission_denied));
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
