package me.vittorio_io.openhostseditor.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

/**
 * Created by vittorio on 23/07/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected void openActivityForResult(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, 1);
    }

    protected void getPermissionAndExecute(String permission, int code) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                haveASnack("We need additional permissions.");

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

    protected void askPermissionBase(String permission, int code) {
        ActivityCompat.requestPermissions(this,
                new String[]{permission},
                code);
    }

    protected void haveASnack(String message) {
        System.out.println(message);
        View view = getWindow().getDecorView().getRootView();

        try {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
