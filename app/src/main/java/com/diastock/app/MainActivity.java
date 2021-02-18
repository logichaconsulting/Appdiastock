package com.diastock.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn;
    private String values = "";
    ArrayList<MenuDataModel> dataModels;
    ListView theListView;
    private static final int SPEECH_REQUEST_CODE = 0;
    private static CustomMenuAdapter adapter;

    public MainActivity() {

    }

    int getImageId(String functionName) {

        switch (functionName) {
            case "2003":
                return R.drawable.ic_action_arrow_left;
            case "2002":
            case "1001":
                return R.drawable.ic_action_trolley;
            case "0001":
            case "0002":
            case "0003":
            case "0004":
            case "0005":
                return R.drawable.ic_action_question_popup;
            case "0006":
                return R.drawable.ic_action_new_item;
            case "2001":
                return R.drawable.ic_action_arrow_move;
            case "3001":
                return R.drawable.ic_action_inventory;

        }
        return R.drawable.ic_action_arrow_move;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setIcon(R.drawable.ic_action_user);


        try {
            theListView = findViewById(R.id.mainListView);

            dataModels = new ArrayList<>();

            List<com.diastock.app.MenuItem> menuItems = UserMenu.getInstance().getMenuItem();
            Iterator<com.diastock.app.MenuItem> it = menuItems.iterator();

            if (it.hasNext()) {
                setTitle(" " + it.next().getFunctionDescription());
            }
            while (it.hasNext()) {

                com.diastock.app.MenuItem menuItem = it.next();
                int j = getImageId(menuItem.getFunctionName());

                dataModels.add(new MenuDataModel(
                        menuItem.getFunctionDescription(),
                        String.format("%1$2s", String.valueOf(menuItem.getFunctionNumber())).replace(' ', '0'), getAttributes(menuItem.getDefDownloadCausal(), menuItem.getDefLoadCausal()), j));
            }

            adapter = new CustomMenuAdapter(dataModels, getApplicationContext());

            theListView.setAdapter(adapter);
            theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    StartSelectedActivity(position);

                }
            });


/*
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            startActivityForResult(intent, SPEECH_REQUEST_CODE);
*/

        } catch (Exception e) {
            Log.e("Click Exception ", e.getMessage());
        }
    }

    private String getAttributes(String defDownloadCausal, String defLoadCausal) {
        String result = "", download = "", load = "";

        if (!defLoadCausal.equals(null) && !defLoadCausal.equals(""))
            load += getResources().getString(R.string.causal_load) + ": " + defLoadCausal;

        if (!defDownloadCausal.equals(null) && !defDownloadCausal.equals(""))
            download += getResources().getString(R.string.causal_unload) + ": " + defDownloadCausal;

        if (!load.equals(null) && !load.equals("") && !download.equals(null) && !download.equals(""))
            result = load + " - " + download;
        else
            result = load + download;

        return result;
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    @Override
    public void onClick(View arg0) {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fragment, menu);
        menu.findItem(R.id.menu_item_options).setVisible(false);

        menu.findItem(R.id.menu_item_new_quote).setIcon(R.drawable.ic_action_key);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_new_quote) {// TODO put your code here to respond to the button tap
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            StartSelectedActivity(Integer.parseInt(spokenText));
            // Do something with spokenText
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void StartSelectedActivity(int position) {
        Intent intent = null;
        int menuPosition = position + 1;
        int adapterPosition = position;

        try {
            MenuDataModel dataModel = dataModels.get(adapterPosition);

            if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("2002")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), ManualLoad.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("2003")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), ManualDownload.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("0001")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), ArticleInquiry.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("0003")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), LogUnitInquiry.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("0002")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), LocationInquiry.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("2001")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), ManualHandling.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("3001")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), ClosedInventory.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("1002")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), FreeInbound.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("1001")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), Inbound.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("5004")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), EndCardboard.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("0006")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), CreateArticle.class);
                intent.putExtras(param); //Put your id to your next Intent
            } else if (UserMenu.getInstance().getMenuItem(menuPosition).getFunctionName().equals("5003")) {
                Bundle param = new Bundle();
                param.putInt("menuPosition", menuPosition); //Your id
                intent = new Intent(getApplicationContext(), AutomaticPicking.class);
                intent.putExtras(param); //Put your id to your next Intent
            }
            if (intent != null) startActivity(intent);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
