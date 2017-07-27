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
                .setDescription("Free and open source application to edit the /etc/hosts file for android devices.\n[Root required]")
                .addItem(new Element().setTitle("Version 0.10.0-alpha").setGravity(Gravity.CENTER))
                .addGroup("Connect with us")
                .addGitHub("SirPryderi/OpenHostsEditor", "GitHub Repository")
                .addPlayStore("me.vittorio_io.openhostseditor")
                .addWebsite("http://vittorio-io.me/", "Developer Website")
                .addEmail("pryderi.mail@gmail.com")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }


    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right) + " " + getString(R.string.dev_name), Calendar.getInstance().get(Calendar.YEAR));
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