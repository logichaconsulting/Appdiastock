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
import static com.diastock.app.InputArea.TYPE_TEXT;

public class PickingSplitter extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    DisplayFragment base;
    Menu actionMenu;
    MenuItem menuItem = null;
    CloudConnector cloudConnector = null;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    String suggestedCardboard;

    ActivityItem qty = null;
    ActivityItem cardboard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking_splitter);
        setTitle("Smistamento Picking");

        try {
            cardboard = new ActivityItem(1, getResources().getString(R.string.ID000051), 20, TYPE_TEXT, "", false, DataExchange.PROP_CARDBOARD, true);
            qty = new ActivityItem(2, getResources().getString(R.string.ID000012), 14, TYPE_QTY, "", false, DataExchange.PROP_SPLITQTY, false);

            base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));
            base.ClearAllDisplay();
            Bundle b = getIntent().getExtras();

            findNextOrder();


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
            if (base.GetCurrentItem() == cardboard) {
                if (!data.equals(suggestedCardboard))
                {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.warning), "Cartone Diverso!", AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    return;
                }
                DataExchange.getInstance().setCardboard(data);
                /*DataExchange.getInstance().setSplitqty(0);
                DataExchange.getInstance().setCardboard(data);
                DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003);
                DataExchange.getInstance().setStep('X');
                cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String, int)");
                cloudConnector.setPostStep(1);
                synchronized (cloudConnector) {
                    cloudConnector.execute();
                }*/

                base.InitializeItem(qty, String.format("%.0f", DataExchange.getInstance().getSplitqty()));
            } else if (base.GetCurrentItem() == qty) {
                DataExchange.getInstance().setQty(DataFunctions.readDecimal(data));
                FinalStep();

            }
        } catch (Exception e) {
        }
    }

    private void FinalStep() {
        try {
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003);
            DataExchange.getInstance().setStep('W');
            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FinalStep()");

            synchronized (cloudConnector) {
                cloudConnector.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnBackAction() {
        try {
            if (base.GetCurrentItem() == cardboard)
                finish();
            else {
                ActivityItem previousItem = base.GetPreviousItem();

                if (previousItem == null)
                    finish();
                else {
                    if (previousItem.equals(cardboard)) {
                        findNextOrder();
                    }
                    else
                        base.BackToItem(previousItem);
                }
            }
            return;
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
        if (!cloudConnector.getPostExceuteMethod().equals("mobile.logicha.wcftester.PickingSplitter.FinalStep()")) {
            if (!Functions.isNullOrEmpty(DataExchange.getInstance().getCardboard())) {
                //findNextOrder();
                //base.InitializeItem(qty, String.format("%.0f", DataExchange.getInstance().getSplitqty()));
                suggestedCardboard = DataExchange.getInstance().getCardboard();
                base.InitializeItem(cardboard);//, DataExchange.getInstance().getCardboard());
                base.AddDisplayRow(suggestedCardboard, getResources().getString(R.string.ID000051), false);
                base.AddDisplayRow(DataExchange.getInstance().getOrder() + "/" + DataExchange.getInstance().getDocnr(), getResources().getString(R.string.ID000115), false);
                base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getSku(), getResources().getString(R.string.ID000003), false);
                base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), getResources().getString(R.string.ID000013), false);
                return;
            }
            else
                finish();
        }
        else {
            if (result.equals("OK")) {

                if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getListid())) {

                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.info), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.INFO, this);
                    alertMessageBuilder.Show();

                    finish();
                    return;
                }

                Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), R.string.operation_successfully_completed, Snackbar.LENGTH_LONG)
                        .setAction("No action", null);
                sbw.getView().setBackgroundColor(Color.parseColor("#66a3ff"));
                sbw.show();
                findNextOrder();
            } else {
                AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                alertMessageBuilder.Show();
                base.InitializeItem(base.GetPreviousItem());
            }
        }
    }

    public void findNextOrder()
    {
        try {
            DataExchange.getInstance().setQty(0);
            DataExchange.getInstance().setCardboard(null);
            DataExchange.getInstance().setOrder(null);
            DataExchange.getInstance().setDocnr(null);
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003);
            DataExchange.getInstance().setStep('X');
            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String, int)");

            synchronized (cloudConnector) {
                cloudConnector.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
