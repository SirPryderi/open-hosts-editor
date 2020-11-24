package me.vittorio_io.openhostseditor.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import me.vittorio_io.openhostseditor.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(getString(R.string.app_description))
                .addGroup(getString(R.string.title_connect))
                .addItem(koFiElement())
                .addGitHub("SirPryderi/OpenHostsEditor", "GitHub Repository")
                .addPlayStore("me.vittorio_io.openhostseditor")
                .addWebsite("https://sirpryderi.github.io/", "Developer Website")
                .addGroup(getString(R.string.app_version))
                .addItem(textElement(getVersion(), false))
                .addGroup(getString(R.string.title_credits))
                .addItem(textElement(getString(R.string.app_credits) + " " + getString(R.string.app_developer), true))
                .addItem(textElement(getString(R.string.app_contributors) + "  Phil Roggenbuck", true))
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }

    Element textElement(String text, boolean center) {
        return new Element().setTitle(text).setGravity(center ? Gravity.CENTER : Gravity.START);
    }

    Element koFiElement() {
        Uri uri = Uri.parse("https://ko-fi.com/sirpryderi");
        String text = getString(R.string.app_coffee);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
        return textElement(text, false).setIconDrawable(R.drawable.ic_ko_fi).setIntent(browserIntent);
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.app_copyright), Calendar.getInstance().get(Calendar.YEAR));
        final String copyrightNotice = "Licensed under the Apache License, Version 2.0";
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, copyrightNotice, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    String getVersion() {
        try {
            return getApplicationContext()
                .getPackageManager()
                .getPackageInfo(getApplicationContext().getPackageName(), 0)
                .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown version";
        }
    }
}