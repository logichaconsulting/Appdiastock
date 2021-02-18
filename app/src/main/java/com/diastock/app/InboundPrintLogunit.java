package com.diastock.app;

import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.diastock.app.InputArea.TYPE_SKU;

public class InboundPrintLogunit extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    ActivityItem sku = null;

    String order, docnr, company;
    DisplayFragment base;
    Menu actionMenu;
    MenuItem menuItem = null;
    CloudConnector cloudConnector = null;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbound_print_logunit);

        try {

            sku = new ActivityItem(4, this.getResources().getString(R.string.ID000003), 30, TYPE_SKU, "", false, DataExchange.PROP_CURRENTARTICLE, true);

            base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));
            base.ClearAllDisplay();
            Bundle b = getIntent().getExtras();

            setTitle(R.string.PrintLabelLogunit);

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
        menu.findItem(R.id.menu_item_new_quote).setIcon(R.drawable.ic_action_activity_back);
        //menu.findItem(R.id.menu_item_options).setIcon(R.drawable.ic_action_login);

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
            if (base.GetCurrentItem() == sku) {
                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    base.InitializeItem(sku);
                    return;
                }

                if (goodResponse == 0) {
                    DataExchange.getInstance().setStart(Calendar.getInstance().getTime());
                    DataExchange.getInstance().getCurrentArticle().setBarcode(data);

                    if (DataFunctions.isNullOrEmpty(data)) {
                        base.InitializeItem(sku);
                        return;
                    }

                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0002);

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String, int)");

                    cloudConnector.setPostStep(1);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                    return;
                } else if (goodResponse == 1) {
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001);

                    DataExchange.getInstance().setStep('A');

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String, int)");

                    cloudConnector.setPostStep(2);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                    return;
                } else if (goodResponse == 2)
                    FinalStep();

                return;
            }
            //else
            //  base.InitializeItem(sku);
        } catch (Exception e) {
        }
    }

    private void FinalStep() {
        try {
            //DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001);
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001);
            DataExchange.getInstance().setStep('P');
            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FinalStep()");

            cloudConnector.setPostStep(3);
            synchronized (cloudConnector) {
                cloudConnector.execute();
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnBackAction() {

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

    @Override
    public void taskCompletionResult(String result, int step) throws Exception {
        if (!cloudConnector.getPostExceuteMethod().equals("mobile.logicha.wcftester.InboundPrintLogunit.FinalStep()"))
        //this.getClass().getMethod(cloudConnector.getPostExceuteMethod()).invoke (this, (result.equals("OK") ? true :false));
        {
            Object[] params = {null, result.equals("OK") ? step : 0};
            this.getClass().getMethods()[0].invoke(this, params);
        } else {
            if (result.equals("OK")) {
                try {
                    cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FinalStep()");

                    cloudConnector.setPostStep(3);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
                return;

            } else {
                AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.INFO, this);
                alertMessageBuilder.Show();
                base.InitializeItem(sku);
            }
        }
    }
}
