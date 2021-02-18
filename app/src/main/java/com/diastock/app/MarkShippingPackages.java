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

public class MarkShippingPackages extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    ActivityItem pieces = null;
    ActivityItem firstpackage = null;
    ActivityItem lastpackage = null;
    ActivityItem packages = null;
    ActivityItem weight = null;
    ActivityItem volume = null;
    ActivityItem standardpallets = null;
    ActivityItem oversizepallets = null;

    String listid, listnr, company, suggestedweight, suggestedvolume, suggestedpieces;
    DisplayFragment base;
    Menu actionMenu;
    MenuItem menuItem = null;
    CloudConnector cloudConnector = null;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


    Boolean weightonrflistclosing;
    Boolean volumeonrflistclosing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_shipping_packages);
        try {

            pieces = new ActivityItem(1, this.getResources().getString(R.string.ID000205), 20, TYPE_QTY, "", false, DataExchange.PROP_PIECES, false);
            firstpackage = new ActivityItem(2, "Primo segnacollo", 20, TYPE_TEXT, "", false, DataExchange.PROP_CARDBOARD, false);
            lastpackage = new ActivityItem(3, "Ultimo segnacollo", 20, TYPE_TEXT, "", false, DataExchange.PROP_CARDBOARD, false);
            packages = new ActivityItem(4, "Colli", 20, TYPE_TEXT, "", false, DataExchange.PROP_GEN_INT_1, false);
            weight = new ActivityItem(5, "Peso", 20, TYPE_TEXT, "", false, DataExchange.PROP_GROSSWEIGHT, false);
            volume = new ActivityItem(6, "Volume", 20, TYPE_TEXT, "", false, DataExchange.PROP_VOLUME, false);
            standardpallets = new ActivityItem(7, this.getResources().getString(R.string.ID000152), 20, TYPE_QTY, "", false, DataExchange.PROP_STANDARDPALLETS, false);
            oversizepallets = new ActivityItem(8, this.getResources().getString(R.string.ID000153), 20, TYPE_QTY, "", false, DataExchange.PROP_OUTSIZEDPALLETS, false);


            base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));
            base.ClearAllDisplay();
            Bundle b = getIntent().getExtras();


            listid = b.getString("LISTID");
            listnr = b.getString("LISTNR");
            setTitle(R.string.ClosePicking);


            weightonrflistclosing = DataExchange.getInstance().getRequestWeightOnPicking();
            volumeonrflistclosing = DataExchange.getInstance().getRequestVolumeOnPicking();
            suggestedweight = Double.toString(DataExchange.getInstance().getGrossweight());
            suggestedvolume = Double.toString(DataExchange.getInstance().getVolume());
            suggestedpieces = Integer.toString(DataExchange.getInstance().getPieces());

            DataExchange.getInstance().setGenericString1("");
            DataExchange.getInstance().setGenericString2("");
            DataExchange.getInstance().setGrossweight(0);
            DataExchange.getInstance().setVolume(0);
            DataExchange.getInstance().setGenericInt1(0);
            DataExchange.getInstance().setGenericInt2(0);
            DataExchange.getInstance().setStandardpallets(0);
            DataExchange.getInstance().setOutsizedpallets(0);

            if (DataExchange.getInstance().getDoctype().isPiecesonrfpickingclosing())
                base.InitializeItem(pieces);
            else if (SetupFlags.getInstance().getAutoparcellabels() && DataExchange.getInstance().getCarrierAutoparcellabels())
                base.InitializeItem(packages);
            else if (SetupFlags.getInstance().getMarkshippingpackages() && !DataExchange.getInstance().getBypassmarkshippingpackagess())
                base.InitializeItem(firstpackage);
            else if (weightonrflistclosing)
                base.InitializeItem(weight, suggestedweight);
            else if (volumeonrflistclosing)
                base.InitializeItem(volume, suggestedvolume);
            else if (SetupFlags.getInstance().getPallettypescounting())
                base.InitializeItem(standardpallets);
            else
                finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void AcceptText(String data, int goodResponse) {
        try {
            DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();

            if (base.GetCurrentItem() == pieces) {
                int typedQty = Integer.parseInt(data);

                if (typedQty <= 0) {
                    base.InitializeItem(pieces, suggestedpieces);
                    return;
                }

                if (DataFunctions.readInt(suggestedpieces) != typedQty) {

                    //Confirm pieces?
                    if (dialogMessageBuilder.getYesNoWithExecutionStop(this.getResources().getString(R.string.warning), String.format(getResources().getString(R.string.ID000206), typedQty), this) == 1) {
                        DataExchange.getInstance().setPieces(typedQty);
                    } else {
                        base.InitializeItem(pieces, suggestedpieces);
                        return;
                    }
                } else
                    DataExchange.getInstance().setPieces(typedQty);

                if (SetupFlags.getInstance().getAutoparcellabels() && DataExchange.getInstance().getCarrierAutoparcellabels())
                    base.InitializeItem(packages);
                else if (SetupFlags.getInstance().getMarkshippingpackages() && !DataExchange.getInstance().getBypassmarkshippingpackagess())
                    base.InitializeItem(firstpackage);
                else if (weightonrflistclosing)
                    base.InitializeItem(weight, suggestedweight);
                else if (volumeonrflistclosing)
                    base.InitializeItem(volume, suggestedvolume);
                else if (SetupFlags.getInstance().getPallettypescounting())
                    base.InitializeItem(standardpallets);
                else
                    FinalStep();

                return;

            } else if (base.GetCurrentItem() == firstpackage) {
                if (Functions.isNullOrEmpty(data) || data.length() < 12)
                    base.InitializeItem(firstpackage);
                else {
                    DataExchange.getInstance().setGenericString1(data);
                    base.InitializeItem(lastpackage);
                }
                return;
            } else if (base.GetCurrentItem() == lastpackage) {

                if (Functions.isNullOrEmpty(data) || data.length() < 12)
                    base.InitializeItem(lastpackage);
                else {
                    DataExchange.getInstance().setGenericString2(data);

                    if (DataExchange.getInstance().getGenericString1().length() > 11 &&
                            DataExchange.getInstance().getGenericString2().length() > 11 &&
                            DataFunctions.readInt(DataExchange.getInstance().getGenericString1().substring(5, 5+6)) >
                                    DataFunctions.readInt(DataExchange.getInstance().getGenericString2().substring(5, 5+6))) {
                        dialogMessageBuilder.getYesNoWithExecutionStop(this.getResources().getString(R.string.warning), getResources().getString(R.string.ID000112), this);
                        base.InitializeItem(firstpackage);
                        return;
                    } else if (weightonrflistclosing)
                        base.InitializeItem(weight, suggestedweight);
                    else if (volumeonrflistclosing)
                        base.InitializeItem(volume, suggestedvolume);
                    else {
                        if (SetupFlags.getInstance().getPallettypescounting())
                            base.InitializeItem(standardpallets);
                        else
                            FinalStep();
                    }
                    return;
                }
            }else if (base.GetCurrentItem() == packages) {

                DataExchange.getInstance().setGenericInt1(DataFunctions.readInt(data));

                if (DataExchange.getInstance().getGenericInt1() <= 0)
                    return;

                if (weightonrflistclosing)
                    base.InitializeItem(weight, suggestedweight);
                else if (volumeonrflistclosing)
                    base.InitializeItem(volume, suggestedvolume);
                else {
                    if (SetupFlags.getInstance().getPallettypescounting())
                        base.InitializeItem(standardpallets);
                    else
                        FinalStep();
                }

                return;


            } else if (base.GetCurrentItem() == weight) {

                DataExchange.getInstance().setGrossweight(DataFunctions.readDecimal(data));

                if (volumeonrflistclosing)
                    base.InitializeItem(volume, suggestedvolume);
                else
                {
                    if (SetupFlags.getInstance().getPallettypescounting())
                        base.InitializeItem(standardpallets);
                    else
                        FinalStep();
                }
                return;
            } else if (base.GetCurrentItem() == volume) {

                    DataExchange.getInstance().setVolume(DataFunctions.readDecimal(data));

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
            //DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003);
            DataExchange.getInstance().setStep('K');
            DataExchange.getInstance().setListid(listid);
            DataExchange.getInstance().setListnr(listnr);

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

        ActivityItem currentItem = base.GetCurrentItem();

        try {
            if (currentItem == pieces)
                finish();
            if (currentItem == firstpackage && !DataExchange.getInstance().getDoctype().isPiecesonrfpickingclosing())
                finish();
            else if (currentItem == packages && !DataExchange.getInstance().getDoctype().isPiecesonrfinboundclosing())
                finish();
            else if (currentItem == weight && !(SetupFlags.getInstance().getMarkshippingpackages() && !DataExchange.getInstance().getBypassmarkshippingpackagess()) &&
                    !(SetupFlags.getInstance().getAutoparcellabels() && DataExchange.getInstance().getCarrierAutoparcellabels()))
                finish();
            else if (currentItem == volume && !(SetupFlags.getInstance().getMarkshippingpackages() && !DataExchange.getInstance().getBypassmarkshippingpackagess()) &&
                    !weightonrflistclosing &&
                    !(SetupFlags.getInstance().getAutoparcellabels() && DataExchange.getInstance().getCarrierAutoparcellabels()))
                finish();
            else if (currentItem == standardpallets && !(SetupFlags.getInstance().getMarkshippingpackages() && !DataExchange.getInstance().getBypassmarkshippingpackagess()) &&
                    !weightonrflistclosing &&
                    !(SetupFlags.getInstance().getAutoparcellabels() && DataExchange.getInstance().getCarrierAutoparcellabels()))
                finish();
            else {
                ActivityItem prevItem = base.GetPreviousItem();

                if (prevItem == weight)
                    base.InitializeItem(weight, Double.toString(DataExchange.getInstance().getGrossweight()));
                else if (prevItem == volume)
                    base.InitializeItem(volume, Double.toString(DataExchange.getInstance().getVolume()));
                else if (prevItem == packages)
                    base.InitializeItem(packages, Integer.toString(DataExchange.getInstance().getGenericInt1()));
                else
                    base.InitializeItem(prevItem, "");
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
        if (result.equals("OK")) {
            Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), getResources().getString(R.string.operation_successfully_completed), Snackbar.LENGTH_LONG)
                    .setAction("No action", null);

            sbw.getView().setBackgroundColor(Color.parseColor("#108a1f"));
            sbw.show();
            DataExchange.getInstance().setListid(null);
            DataExchange.getInstance().setListnr(null);
            finish();
        }
        else
        {
            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
            alertMessageBuilder.Show();
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
}