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

import static com.diastock.app.InputArea.TYPE_LOCATION;
import static com.diastock.app.InputArea.TYPE_TEXT;

public class LocationInquiry extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    DisplayFragment base;
    MenuItem menuItem = null;
    CloudConnector cloudConnector = null;

    ActivityItem location = null;
    ActivityItem wait = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_inquiry);

        base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));

        try {
            location = new ActivityItem(1, getResources().getString(R.string.ID000006), 23, TYPE_LOCATION, "", false, DataExchange.PROP_FROMLOCATION, true);
            wait = new ActivityItem(2, getResources().getString(R.string.ID000048), 50, TYPE_TEXT, "", false, null, false);

            base.ClearAllDisplay();
            Bundle params = getIntent().getExtras();
            int position = -1; // or other values
            if (params != null)
                position = params.getInt("menuPosition");

            menuItem = UserMenu.getInstance().getMenuItem(position);
            setTitle(menuItem.getFunctionDescription());

            DataExchange.getInstance().format();

            base.InitializeItem(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String re = scanResult.getContents();
            //InputArea input = (InputArea) findViewById(R.id.inputarea);
            //input.setText(re);

            try {
                if (Functions.ValidateBarcode(re, base.GetCurrentItem(), null))
                    AcceptText(re.trim(), 0);

            } catch (Exception e) {
                e.printStackTrace();
            }

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
            if (base.GetCurrentItem() == location || base.GetCurrentItem() == wait) {
                if (goodResponse == 0) {
                    if (DataFunctions.isNullOrEmpty(data)) {
                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();

                            base.InitializeItem(location);
                            return;
                        }

                    }

                    if (base.GetCurrentItem() == location) {
                        DataExchange.getInstance().getFromLocation().setEntirePosition(data);

                        DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0003);
                        DataExchange.getInstance().setStep('\0');

                        cloudConnector = new CloudConnector(this, this, this,
                                Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                        cloudConnector.setPostStep(2);
                        synchronized (cloudConnector) {
                            cloudConnector.execute();
                        }
                        return;
                    } else {

                        DataExchange.getInstance().setFunctionName(DataExchange.Operations.F0002);
                        DataExchange.getInstance().setStep('\0');

                        cloudConnector = new CloudConnector(this, this, this,
                                Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                        cloudConnector.setPostStep(3);
                        synchronized (cloudConnector) {
                            cloudConnector.execute();
                        }
                        return;
                    }
                } else if (goodResponse == 2) {
                    if (DataFunctions.isNullOrEmpty(data)) {
                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();

                            base.InitializeItem(location);
                            return;
                        }
                        //return;
                    }

                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F0002);
                    DataExchange.getInstance().setStep('\0');

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    cloudConnector.setPostStep(3);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                    return;
                } else if (goodResponse == 3) {
                    base.ClearAllDisplay();

                    base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getSku(), getResources().getString(R.string.ID000003), false);
                    base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), getResources().getString(R.string.ID000013), false);

                    Presentation.showGenericCoordinates(base, DataExchange.getInstance(), true);

                    base.AddDisplayRow(Double.toString(DataExchange.getInstance().getQty()), getResources().getString(R.string.ID000012), false);
                    Presentation.ShowAttributes(base, DataExchange.getInstance(), true, true, true);

                    if (DataExchange.getInstance().getFromLocation().getPositionCode() > 0)
                        base.InitializeItem(wait, "", true);
                    else {
                        Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), "Articoli terminati", Snackbar.LENGTH_LONG)
                                .setAction("No action", null);

                        sbw.getView().setBackgroundColor(Color.parseColor("#66a3ff"));
                        sbw.show();

                        base.InitializeItem(location, "", true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnBackAction() {
        try {
            if (base.GetCurrentItem() == location)
                finish();
            else {
                //ActivityItem ai = base.GetPreviousItem();
                base.InitializeItem(location);
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

    @Override
    public void taskCompletionResult(String result, int step) throws Exception {
        if (!cloudConnector.getPostExceuteMethod().equals("mobile.logicha.wcftester.LocationInquiry.FinalStep()")) {
            Object[] params = {null, result.equals("OK") ? step : 0};
            this.getClass().getMethods()[0].invoke(this, params);
        }
    }
}
