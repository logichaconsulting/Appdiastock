package com.diastock.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements TaskDelegate, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.ic_action_pack);
        ab.setTitle("  " + getResources().getString(R.string.app_name));
        setContentView(R.layout.activity_login);

        TextView textViewLoginTitle = (TextView) findViewById(R.id.textViewLoginTitle);
        textViewLoginTitle.setText("Accedi a " + getResources().getString(R.string.app_name));
        getAppLocale();
        /*
        TextView textView = (TextView) findViewById(R.id.appVersion);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            textView.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        */
        UserLoggedDbHelper dbHelper = new UserLoggedDbHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                UserLoggedContract.UserEntry.COLUMN_NAME_USER
        };

        Cursor cursor = db.query(
                UserLoggedContract.UserEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null// The sort order
        );

        if (cursor.getCount() > 0 && projection.length > 0) {
            cursor.moveToFirst();
            EditText txtUser = (EditText) findViewById(R.id.user);
            txtUser.setText(cursor.getString(cursor.getColumnIndex(UserLoggedContract.UserEntry.COLUMN_NAME_USER)));
            EditText txtPwd = (EditText) findViewById(R.id.password);
            txtPwd.requestFocus();
        }

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        EditText txtCompany = (EditText) findViewById(R.id.company);

        SharedPreferences prefs = getSharedPreferences("SETTINGS", MODE_PRIVATE);

        if (prefs != null) {
            boolean restoredCompanyReq = prefs.getBoolean("COMPANY_REQUEST", false);

            if (restoredCompanyReq)
                txtCompany.setVisibility(View.VISIBLE);
            else
                txtCompany.setVisibility(View.INVISIBLE);
        }

        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        //setContentView(R.layout.main);

        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);

    }

    public void onLangItClick(View v) {
        setAppLocale("it");
    }

    public void onLangEnClick(View v) {
        setAppLocale("en");
    }

    private void getAppLocale() {

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String langId = sharedPref.getString("LightWareLangID", "IT");

        Resources res = getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        Configuration configuration = res.getConfiguration();

        configuration.setLocale(new Locale(langId));
    }

    private void setAppLocale(String localeCode) {
        Resources res = getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        Configuration configuration = res.getConfiguration();

        configuration.setLocale(new Locale(localeCode.toLowerCase()));

        res.updateConfiguration(configuration, displayMetrics);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("LightWareLangID", localeCode);
        editor.commit();

        recreate();
    }

    @Override
    public void onClick(View v) {

        final EditText txtUser = (EditText) findViewById(R.id.user);
        final EditText txtPassword = (EditText) findViewById(R.id.password);
        final EditText txtCompany = (EditText) findViewById(R.id.company);

        boolean companyRequest = false;
        String restoredDB = null;

        SharedPreferences prefs = getSharedPreferences("SETTINGS", MODE_PRIVATE);

        if (prefs != null) {
            companyRequest = prefs.getBoolean("COMPANY_REQUEST", false);
            restoredDB = prefs.getString("DEFAULT_DB", "");
        }

        if (!txtUser.getText().toString().equals("") &&
                !txtPassword.getText().toString().equals("")) {
            CloudConnector cloudConnector = new CloudConnector(getApplicationContext(), this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "onClick(View v)");

            synchronized (cloudConnector) {
                try {
                    DataExchange.getInstance().setUserId(txtUser.getText().toString());
                    DataExchange.getInstance().setPassword(txtPassword.getText().toString());
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.FMENU);

                    SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                    String langId = sharedPref.getString("LightWareLangID", "IT");

                    DataExchange.getInstance().setLangId(langId);
                    DataExchange.getInstance().setGenericString1(restoredDB);

                    if (companyRequest) {
                        DataExchange.getInstance().setCompany(txtCompany.getText().toString());
                    }

                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void taskCompletionResult(String result, int step) throws Exception {

        if (result.equals("OK") && UserMenu.getInstance().getMessage().equals("OK")) {

            UserLoggedDbHelper dbHelper = new UserLoggedDbHelper(this);

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] projection = {
                    BaseColumns._ID,
                    UserLoggedContract.UserEntry.COLUMN_NAME_USER
            };

            Cursor cursor = db.query(
                    UserLoggedContract.UserEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    null,              // The columns for the WHERE clause
                    null,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    null// The sort order
            );

            String user = ((EditText) findViewById(R.id.user)).getText().toString();

            ContentValues values = new ContentValues();
            values.put(UserLoggedContract.UserEntry.COLUMN_NAME_USER, user);

            long count = -1;

            if (cursor.getCount() > 0) {
                count = db.update(
                        UserLoggedContract.UserEntry.TABLE_NAME,
                        values,
                        null,
                        null);
            } else {
                // Insert the new row, returning the primary key value of the new row
                count = db.insert(UserLoggedContract.UserEntry.TABLE_NAME, null, values);
            }

            if (count > 0) {

            }

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else {
            String message = "";

            if (UserMenu.getInstance().getMessage() != null) {
                message = UserMenu.getInstance().getMessage();

                if (UserMenu.getInstance().getMessage().contains("BODY"))
                    //Errore di connessione. Controllare la configurazione
                    message = getResources().getString(R.string.connection_error);
            } else
                //Errore di connessione. Controllare la configurazione
                message = message = getResources().getString(R.string.connection_error);

            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), message, AlertMessageBuilder.Severity.ERROR, this);
            alertMessageBuilder.Show();
        }
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_fragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_new_quote) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
