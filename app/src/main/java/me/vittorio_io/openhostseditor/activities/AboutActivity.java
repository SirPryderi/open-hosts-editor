package me.vittorio_io.openhostseditor.activities;

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
                .addGitHub("SirPryderi/OpenHostsEditor", "GitHub Repository")
                .addPlayStore("me.vittorio_io.openhostseditor")
                .addWebsite("https://sirpryderi.github.io/", "Developer Website")
                .addEmail("pryderi.mail@gmail.com")
                .addGroup(getString(R.string.app_version))
                .addItem(new Element().setTitle("0.10.0-alpha"))
                .addGroup(getString(R.string.title_credits))
                .addItem(new Element().setTitle(getString(R.string.app_credits) + " " + getString(R.string.app_developer)).setGravity(Gravity.CENTER))
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
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
}