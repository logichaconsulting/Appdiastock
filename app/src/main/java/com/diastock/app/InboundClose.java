package com.diastock.app;

import android.graphics.Color;
import android.net.Uri;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.diastock.app.InputArea.TYPE_QTY;

public class InboundClose extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    ActivityItem pieces = null;
    ActivityItem standardpallets = null;
    ActivityItem oversizepallets = null;

    String order, docnr, company;
    int suggestedPieces = 0;
    DisplayFragment base;
    Menu actionMenu;
    MenuItem menuItem = null;
    CloudConnector cloudConnector = null;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbound_close);

        try {

            pieces = new ActivityItem(1, this.getResources().getString(R.string.ID000205), 20, TYPE_QTY, "", false, DataExchange.PROP_PIECES, false);
            standardpallets = new ActivityItem(2, this.getResources().getString(R.string.ID000152), 20, TYPE_QTY, "", false, DataExchange.PROP_STANDARDPALLETS, false);
            oversizepallets = new ActivityItem(3, this.getResources().getString(R.string.ID000153), 20, TYPE_QTY, "", false, DataExchange.PROP_OUTSIZEDPALLETS, false);


            base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));
            base.ClearAllDisplay();
            Bundle b = getIntent().getExtras();


            //order = b.getString("ORDER");
            //docnr = b.getString("DOCUMENT");
            setTitle(R.string.CloseIbound);

            //DataExchange.getInstance().format();

            if (DataExchange.getInstance().getDoctype().isPiecesonrfinboundclosing())
                base.InitializeItem(pieces);
            else
                base.InitializeItem(standardpallets);

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
            if (base.GetCurrentItem() == pieces) {
                int typedQty = Integer.parseInt(data);

                if (typedQty <= 0) {
                    base.InitializeItem(pieces, Integer.toString(suggestedPieces));
                    return;
                }

                if (suggestedPieces != typedQty) {
                    DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();
                    //Confirm pieces?
                    if (dialogMessageBuilder.getYesNoWithExecutionStop(this.getResources().getString(R.string.warning), String.format(getResources().getString(R.string.ID000206), typedQty), this) == 1) {
                        DataExchange.getInstance().setPieces(typedQty);
                    } else {
                        base.InitializeItem(pieces, Integer.toString(suggestedPieces));
                        return;
                    }
                } else
                    DataExchange.getInstance().setPieces(typedQty);

                if (SetupFlags.getInstance().getPallettypescounting())
                    base.InitializeItem(standardpallets);
                else
                    FinalStep();

                return;
            } else if (base.GetCurrentItem() == standardpallets) {
                DataExchange.getInstance().setStandardpallets(Integer.parseInt(data));
                base.InitializeItem(oversizepallets);
                return;
            } else if (base.GetCurrentItem() == oversizepallets) {
                DataExchange.getInstance().setStandardpallets(Integer.parseInt(data));
                FinalStep();
            }
        } catch (Exception e) {
        }
    }

    private void FinalStep() {
        try {
            //DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001);
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001);
            DataExchange.getInstance().setStep('K');
            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FinalStep()");

            cloudConnector.setPostStep(3);
            synchronized (cloudConnector) {
                cloudConnector.execute();
            }

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
        if (result.equals("OK")) {
            if (step == 3) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.QUESTION) {
                    DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();
                    if (dialogMessageBuilder.getYesNoWithExecutionStop(this.getResources().getString(R.string.warning), DataExchange.getInstance().getMessage(), this) == 1) {
                        DataExchange.getInstance().setMessagetype(DataExchange.MessageType.QUESTION);
                        DataExchange.getInstance().setMessage(null);
                        cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FinalStep()");

                        cloudConnector.setPostStep(3);
                        synchronized (cloudConnector) {
                            cloudConnector.execute();
                        }
                        return;
                    } else {
                        base.InitializeItem(base.GetPreviousItem());
                        return;
                    }
                } else {
                    Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), DataExchange.getInstance().getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("No action", null);

                    sbw.getView().setBackgroundColor(Color.parseColor("#66a3ff"));
                    sbw.show();


                    finish();
                }

                return;
            } else {
                AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                alertMessageBuilder.Show();
                base.InitializeItem(base.GetPreviousItem());
            }
        } else {
            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
            alertMessageBuilder.Show();
        }
    }
}
