package com.diastock.app;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class Settings extends AppCompatActivity {

    static String uri = "", defDb = "";
    static boolean companyRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle(getTitle() + " " + BuildConfig.VERSION_NAME);

        Button saveButton = (Button) findViewById(R.id.saveSettingsButton);
        Button exitAppButton = (Button) findViewById(R.id.exitAppButton);
        final EditText txtUri = (EditText) findViewById(R.id.txtCloudServer);
        final EditText txtDb = (EditText) findViewById(R.id.txtDb);
        final CheckBox chkMultiComp = (CheckBox) findViewById(R.id.chkMultiComp);

        uri = txtUri.getText().toString();
        defDb = txtDb.getText().toString();

        SharedPreferences prefs = getSharedPreferences("SETTINGS", MODE_PRIVATE);

        if (prefs != null) {
            String restoredUri = prefs.getString("CLOUD_URI", "");

            if (restoredUri != null) {
                uri = restoredUri;
                txtUri.setText(uri);
            }

            String restoredDB = prefs.getString("DEFAULT_DB", "");

            if (restoredDB != null) {
                defDb = restoredDB;
                txtDb.setText(defDb);
            }

            boolean restoredCompanyReq = prefs.getBoolean("COMPANY_REQUEST", false);

            companyRequest = restoredCompanyReq;
            chkMultiComp.setChecked(companyRequest);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("SETTINGS", MODE_PRIVATE).edit();
                editor.putString("CLOUD_URI", txtUri.getText().toString());
                editor.putString("DEFAULT_DB", txtDb.getText().toString());
                editor.putBoolean("COMPANY_REQUEST", chkMultiComp.isChecked());

                editor.apply();
                finish();
            }
        });

        exitAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }
}
