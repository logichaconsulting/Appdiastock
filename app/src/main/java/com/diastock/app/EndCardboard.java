package com.diastock.app;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static com.diastock.app.InputArea.TYPE_QTY;
import static com.diastock.app.InputArea.TYPE_TEXT;

public class EndCardboard extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    DisplayFragment base;
    MenuItem menuItem = null;
    CloudConnector cloudConnector = null;

    ActivityItem cardboard = null;
    ActivityItem packageType = null;
    ActivityItem grossweight = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_cardboard);

        base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));

        try {
            cardboard = new ActivityItem(0, getResources().getString(R.string.ID000051), 20, TYPE_TEXT, "", false, DataExchange.PROP_CARDBOARD, true);
            packageType = new ActivityItem(1, getResources().getString(R.string.ID000083), 20, TYPE_TEXT, "", false, DataExchange.PROP_CARDBOARD, true);
            grossweight = new ActivityItem(2, getResources().getString(R.string.ID000086), 20, TYPE_QTY, "", false, DataExchange.PROP_REQ_QTY, true);

            base.ClearAllDisplay();
            Bundle params = getIntent().getExtras();
            int position = -1; // or other values
            if (params != null) {
                position = params.getInt("menuPosition");

                menuItem = UserMenu.getInstance().getMenuItem(position);
                setTitle(menuItem.getFunctionDescription());
            }
            else
                setTitle(R.string.ID000085);

            DataExchange.getInstance().setStep('\0');
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5004);

            base.InitializeItem(cardboard, DataExchange.getInstance().getCardboard());
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void AcceptText(String data, int goodResponse) {
        try {
            if (base.GetCurrentItem() == cardboard) {
                if (goodResponse == 0) {
                    if (DataFunctions.isNullOrEmpty(data)) {
                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();

                            base.InitializeItem(cardboard, DataExchange.getInstance().getCardboard());
                        }
                        return;
                    }

                    DataExchange.getInstance().setCardboard(data);
                    DataExchange.getInstance().setStep('C');

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    cloudConnector.setPostStep(1);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                    return;
                } else if (goodResponse == 1) {
                    base.ClearAllDisplay();

                    base.AddDisplayRow(DataExchange.getInstance().getCardboard(), cardboard.label, false);

                    //cloudConnector = new CloudConnector(this, this, this,
                    //        Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    //cloudConnector.setPostStep(2);
                    //synchronized (cloudConnector) {
                    //    cloudConnector.execute();
                    //}
                    base.InitializeItem(grossweight, String.format("%.0f", DataExchange.getInstance().getRequiredQty()));
                    //return;
                //} else {
                //    base.InitializeItem(grossweight, String.format("%.0f", DataExchange.getInstance().getRequiredQty()));
                }
                return;
            }

            if (base.GetCurrentItem() == packageType) {
                if (goodResponse == 0) {
                    if (DataFunctions.isNullOrEmpty(data)) {
                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();

                            base.InitializeItem(packageType);
                        }
                        return;
                    }

                    DataExchange.getInstance().setpackages(data);
                    DataExchange.getInstance().setStep('I');    // Check for cardboard prefix

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    cloudConnector.setPostStep(1);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                    return;
                } else if (goodResponse == 1) {
                    StepWrite();
                    //return;
                    //} else {
                    base.ClearAllDisplay();
                    base.InitializeItem(cardboard);
                }
                return;
            }

            if (base.GetCurrentItem() == grossweight) {
                if (goodResponse == 0) {
                    if (DataFunctions.isNullOrEmpty(data)) {
                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();

                            base.InitializeItem(packageType);
                        }
                        return;
                    }

                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5004);
                    DataExchange.getInstance().setGrossweight(DataFunctions.readDecimal(data));
                    DataExchange.getInstance().setStep('W');

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    cloudConnector.setPostStep(1);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                    return;
                } else if (goodResponse == 1) {
                    if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getpackages()))
                        base.InitializeItem(packageType);
                    else {
                        StepWrite();
                        //return;
                        //} else {
                        base.ClearAllDisplay();
                        base.InitializeItem(cardboard);
                    }
                    return;
                }
            }
        } catch (
                Exception e) {
        }

    }

    private void StepWrite() {
        try {
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5004); // END CARDBOARD
            DataExchange.getInstance().setStep('W');

            cloudConnector = new CloudConnector(this, this, this,
                    Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FinalStep()");

            //cloudConnector.setPostStep(2);
            synchronized (cloudConnector) {
                cloudConnector.execute();
            }

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
    public void OnBackAction() {
        try {
            if (base.GetCurrentItem() == cardboard)
                finish();
            else if (base.GetCurrentItem() == packageType) {
                base.ClearAllDisplay();
                base.InitializeItem(grossweight);
            } else {
                //wait.label = getResources().getString(R.string.ID000019);
                base.ClearAllDisplay();
                base.InitializeItem(cardboard);
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void taskCompletionResult(String result, int step) throws Exception {
        if (!cloudConnector.getPostExceuteMethod().equals("mobile.logicha.wcftester.EndCardboard.FinalStep()"))
        //this.getClass().getMethod(cloudConnector.getPostExceuteMethod()).invoke (this, (result.equals("OK") ? true :false));
        {
            Object[] params = {null, result.equals("OK") ? step : 0};
            this.getClass().getMethods()[0].invoke(this, params);
        }
    }
}
