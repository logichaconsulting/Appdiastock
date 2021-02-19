package com.diastock.app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static com.diastock.app.InputArea.TYPE_EANUCC;
import static com.diastock.app.InputArea.TYPE_SKU;
import static com.diastock.app.InputArea.TYPE_TEXT;

public class CreateArticle extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    DisplayFragment base;
    MenuItem menuItem = null;
    CloudConnector cloudConnector = null;

    ActivityItem eanucc = null;
    ActivityItem sku = null;
    ActivityItem descr = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String re = scanResult.getContents();
            InputArea input = (InputArea) findViewById(R.id.inputarea);
            input.setText(re);

            try {
                if (Functions.ValidateBarcode(input.getText().toString(), base.GetCurrentItem(), input))
                    AcceptText(input.getText().toString().trim(), 0);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);

        base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));

        try {
            eanucc = new ActivityItem(0, getResources().getString(R.string.ID000059), 200, TYPE_EANUCC, "", false, DataExchange.PROP_EANUCC, true);
            sku = new ActivityItem(1, getResources().getString(R.string.ID000003), 50, TYPE_SKU, "", false, DataExchange.PROP_CURRENTARTICLE, true);
            descr = new ActivityItem(2, getResources().getString(R.string.ID000013), 50, TYPE_TEXT, "", false, null, false);

            base.ClearAllDisplay();
            Bundle params = getIntent().getExtras();
            int position = -1; // or other values
            if (params != null)
                position = params.getInt("menuPosition");

            menuItem = UserMenu.getInstance().getMenuItem(position);
            setTitle(menuItem.getFunctionDescription());

            DataExchange.getInstance().format();
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F0006); // CREATE ARTICLE

            if (SetupFlags.getInstance().getUsesmaxibarcode())
                base.InitializeItem(eanucc);
            else
                base.InitializeItem(sku);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fragment, menu);
        menu.findItem(R.id.menu_item_options).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.menu_item_new_quote) {// TODO put your code here to respond to the button tap
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void AcceptText(String data, int goodResponse) {
        try {
            if (base.GetCurrentItem() == eanucc) {
                if (goodResponse == 0) {
                    if (DataFunctions.isNullOrEmpty(data)) {
                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();
                            base.ClearAllDisplay();

                            DataExchange.getInstance().format();
                            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F0006); // CREATE ARTICLE

                        } else
                            base.InitializeItem(sku);
                        return;
                    }

                    DataExchange.getInstance().getCurrentArticle().setBarcode(data);
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F0006);
                    DataExchange.getInstance().setStep('\0');

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    cloudConnector.setPostStep(1);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }

                    return;
                } else {
                    base.ClearAllDisplay();

                    String barcode = DataExchange.getInstance().getGenericString2();

                    if (Functions.isNullOrEmpty(barcode))
                        barcode = DataExchange.getInstance().getCurrentArticle().getBarcode();

                    if (Functions.isNullOrEmpty(barcode))
                        barcode = DataExchange.getInstance().getCurrentArticle().getSku();

                    DataExchange.getInstance().getCurrentArticle().setBarcode(barcode);

                    base.AddDisplayRow(barcode, getResources().getString(R.string.barcode), false);
                    base.InitializeItem(sku, DataExchange.getInstance().getCurrentArticle().getSku());
                }
            } else if (base.GetCurrentItem() == sku) {
                if (!DataFunctions.isNullOrEmpty(data)) {
                    DataExchange.getInstance().getCurrentArticle().setSku(data);
                    base.AddDisplayRow(data, getResources().getString(R.string.ID000003), false);
                    base.InitializeItem(descr);
                }
            } else if (base.GetCurrentItem() == descr) {
                if (goodResponse == 0) {
                    if (DataFunctions.isNullOrEmpty(data)) {
                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.WARNING, this);
                            alertMessageBuilder.Show();
                            //base.InitializeItem(descr);

                            base.ClearAllDisplay();

                            DataExchange.getInstance().format();
                            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F0006); // CREATE ARTICLE

                            if (SetupFlags.getInstance().getUsesmaxibarcode())
                                base.InitializeItem(eanucc);
                            else
                                base.InitializeItem(sku);
                        }
                        return;
                    }

                    DataExchange.getInstance().getCurrentArticle().setDescription(data);
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F0006);
                    DataExchange.getInstance().setStep('K');

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    cloudConnector.setPostStep(1);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }

                    return;
                } else {
                    Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), DataExchange.getInstance().getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("No action", null);

                    sbw.getView().setBackgroundColor(Color.parseColor("#66a3ff"));
                    sbw.show();
                    base.ClearAllDisplay();

                    DataExchange.getInstance().format();
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F0006); // CREATE ARTICLE

                    if (SetupFlags.getInstance().getUsesmaxibarcode())
                        base.InitializeItem(eanucc);
                    else
                        base.InitializeItem(sku);
                }
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void OnBackAction() {
        try {
            if (base.GetCurrentItem() == eanucc)
                finish();
            else if (base.GetCurrentItem() == sku)
                if (SetupFlags.getInstance().getUsesmaxibarcode()) {
                    base.ClearAllDisplay();
                    base.InitializeItem(eanucc);
                } else
                    finish();
            else {
                base.InitializeItem(sku);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void CreateAction1() {

    }

    @Override
    public void CreateAction2() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void FinalStep() {


    }

    @Override
    public void taskCompletionResult(String result, int step) throws Exception {
        if (!cloudConnector.getPostExceuteMethod().equals("com.diastock.app.CreateArticle.FinalStep()"))
        //this.getClass().getMethod(cloudConnector.getPostExceuteMethod()).invoke (this, (result.equals("OK") ? true :false));
        {
            Object[] params = {null, result.equals("OK") ? step : 0};
            this.getClass().getMethods()[0].invoke(this, params);
        }
    }
}
