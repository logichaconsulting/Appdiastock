package com.diastock.app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.diastock.app.InputArea.TYPE_DATE;
import static com.diastock.app.InputArea.TYPE_EANUCC;
import static com.diastock.app.InputArea.TYPE_LOCATION;
import static com.diastock.app.InputArea.TYPE_LOGUNIT;
import static com.diastock.app.InputArea.TYPE_QTY;
import static com.diastock.app.InputArea.TYPE_TEXT;

public class ManualLoad extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    /*ActivityItem container = new ActivityItem(1, "Container", 20, TYPE_TEXT, "", false, DataExchange.PROP_CONTAINER, true);
    ActivityItem eanucc = new ActivityItem(2, "EAN/UCC", 200, TYPE_EANUCC, "", false, DataExchange.PROP_EANUCC, true);
    ActivityItem unit = new ActivityItem(3, "Unità Logistica", 40, TYPE_LOGUNIT, "", false, DataExchange.PROP_UNIT, true);
    ActivityItem sku = new ActivityItem(4, "Articolo", 50, TYPE_TEXT, "", false, DataExchange.PROP_CURRENTARTICLE, true);
    ActivityItem skuDescr = new ActivityItem(5, "Descrizione Articolo", 50, TYPE_TEXT, "", false, null, false);
    ActivityItem batchnr = new ActivityItem(6, "Lotto", 50, TYPE_TEXT, "", false, DataExchange.PROP_BATCH, true);
    ActivityItem expiry = new ActivityItem(7, "Scadenza", 10, TYPE_DATE, "", false, DataExchange.PROP_EXPIRE, false);
    ActivityItem variantid1 = new ActivityItem(8, "Variante1", 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID1, true);
    ActivityItem variantid2 = new ActivityItem(9, "Variante2", 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID2, true);
    ActivityItem variantid3 = new ActivityItem(10, "Variante3", 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID3, true);
    ActivityItem qty = new ActivityItem(11, "Quantità", 6, TYPE_QTY, "", false, DataExchange.PROP_QTY, false);
    ActivityItem location = new ActivityItem(12, "Posizione", 23, TYPE_LOCATION, "", false, DataExchange.PROP_TOLOCATION, true);*/

    ActivityItem container = null;
    ActivityItem eanucc = null;
    ActivityItem unit = null;
    ActivityItem sku = null;
    ActivityItem skuDescr = null;
    ActivityItem batchnr = null;
    ActivityItem expiry = null;
    ActivityItem variantid1 = null;
    ActivityItem variantid2 = null;
    ActivityItem variantid3 = null;
    ActivityItem qty = null;
    ActivityItem location = null;

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    DisplayFragment base;
    MenuItem menuItem = null;
    CloudConnector cloudConnector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manual_load);

        base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));

        try {

            container = new ActivityItem(1, getResources().getString(R.string.ID000004), 20, TYPE_TEXT, "", false, DataExchange.PROP_CONTAINER, true);
            eanucc = new ActivityItem(2, getResources().getString(R.string.ID000059), 200, TYPE_EANUCC, "", false, DataExchange.PROP_EANUCC, true);
            unit = new ActivityItem(3, getResources().getString(R.string.ID000005), 40, TYPE_LOGUNIT, "", false, DataExchange.PROP_UNIT, true);
            sku = new ActivityItem(4, getResources().getString(R.string.ID000003), 50, TYPE_TEXT, "", false, DataExchange.PROP_CURRENTARTICLE, true);
            skuDescr = new ActivityItem(5, getResources().getString(R.string.ID000013), 50, TYPE_TEXT, "", false, null, false);
            batchnr = new ActivityItem(6, getResources().getString(R.string.ID000007), 50, TYPE_TEXT, "", false, DataExchange.PROP_BATCH, true);
            expiry = new ActivityItem(7, getResources().getString(R.string.ID000008), 10, TYPE_DATE, "", false, DataExchange.PROP_EXPIRE, false);
            variantid1 = new ActivityItem(8, getResources().getString(R.string.ID000009), 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID1, true);
            variantid2 = new ActivityItem(9, getResources().getString(R.string.ID000010), 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID2, true);
            variantid3 = new ActivityItem(10, getResources().getString(R.string.ID000011), 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID3, true);
            qty = new ActivityItem(11, getResources().getString(R.string.ID000012), 6, TYPE_QTY, "", false, DataExchange.PROP_QTY, false);
            location = new ActivityItem(12, getResources().getString(R.string.ID000006), 23, TYPE_LOCATION, "", false, DataExchange.PROP_FROMLOCATION, true);

            base.ClearAllDisplay();
            Bundle params = getIntent().getExtras();
            int position = -1; // or other values
            if (params != null)
                position = params.getInt("menuPosition");

            menuItem = UserMenu.getInstance().getMenuItem(position);
            setTitle(menuItem.getFunctionDescription());

            DataExchange.getInstance().format();
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F2002); // MANUAL LOAD
            DataExchange.getInstance().setLoadCausal(menuItem.getDefLoadCausal());
            DataExchange.getInstance().getToLocation().setPositionCode(menuItem.getDefToLocation());

            if (SetupFlags.getInstance().getUsescontainer())
                base.InitializeItem(container);
            else if (SetupFlags.getInstance().getUsesmaxibarcode())
                base.InitializeItem(eanucc);
            else if (SetupFlags.getInstance().getUseslogisticunit())
                base.InitializeItem(unit);
            else
                base.InitializeItem(sku);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AcceptText(String data, int responseFromServer) {
        try {
            String outMesaage = "";

            if (base.GetCurrentItem() == container) {

                DataExchange.getInstance().setStart(Calendar.getInstance().getTime());
                DataExchange.getInstance().setContainer(data);

                if (!DataFunctions.isNullOrEmpty(data)) {
                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);
                } else {
                    if (SetupFlags.getInstance().getUsesmaxibarcode())
                        base.InitializeItem(eanucc);
                    else if (SetupFlags.getInstance().getUseslogisticunit())
                        base.InitializeItem(unit);
                    else
                        base.InitializeItem(sku);
                    return;
                }

            } else if (base.GetCurrentItem() == eanucc) {

                if (responseFromServer == 0) {

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(eanucc);
                    } else {

                        if (SetupFlags.getInstance().getUseslogisticunit()) {
                            if (!DataFunctions.isNullOrEmpty(data) && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getUnit())) {
                                DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0001);
                                cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                                synchronized (cloudConnector) {
                                    cloudConnector.execute();
                                }

                                return;
                            } else
                                base.InitializeItem(unit);
                        } else {
                            if (!DataFunctions.isNullOrEmpty(data) && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku())) {
                                DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0002);

                                cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                                synchronized (cloudConnector) {
                                    String res = cloudConnector.execute().get();
                                }
                                return;
                            } else
                                base.InitializeItem(sku);

                        }
                    }
                } else {

                    if (SetupFlags.getInstance().getUseslogisticunit()) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getUnit()))
                            base.AddDisplayRow(DataExchange.getInstance().getUnit(), getResources().getString(R.string.ID000005), false);
                        else {
                            base.InitializeItem(unit);
                            return;
                        }
                    }

                    if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku()) &&
                            !DataExchange.getInstance().getCurrentArticle().getSku().equals("")) {
                        base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getSku(), sku.label, false);
                        base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), getResources().getString(R.string.ID000013), false);
                    } else {
                        base.InitializeItem(sku, DataExchange.getInstance().getCurrentArticle().getSku());
                        return;
                    }

                    DataExchange.getInstance().setStart(Calendar.getInstance().getTime());


                    if (DataExchange.getInstance().getCurrentArticle().getUsesbatch()) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getBatch()))
                            base.AddDisplayRow(DataExchange.getInstance().getBatch(), getResources().getString(R.string.ID000007), false);
                        else {
                            base.InitializeItem(batchnr);
                            return;
                        }
                    } else
                        DataExchange.getInstance().setBatch(null);

                    if (DataExchange.getInstance().getCurrentArticle().getUsesexpire()) {
                        if (!DataExchange.getInstance().getExpire().equals((DataFunctions.getEmptyDate()))) {
                            base.AddDisplayRow(format.format(DataExchange.getInstance().getExpire()), getResources().getString(R.string.ID000008), false);
                        } else {
                            base.InitializeItem(expiry);
                            return;
                        }
                    } else
                        DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());

                    if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId1()))
                            base.AddDisplayRow(DataExchange.getInstance().getVariantId1(), getResources().getString(R.string.ID000009), false);
                        else
                            base.InitializeItem(variantid1);
                    } else
                        DataExchange.getInstance().setVariantId1(null);

                    if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId2()))
                            base.AddDisplayRow(DataExchange.getInstance().getVariantId1(), getResources().getString(R.string.ID000010), false);
                        else {
                            base.InitializeItem(variantid2);
                            return;
                        }
                    } else
                        DataExchange.getInstance().setVariantId2(null);

                    if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId3()))
                            base.AddDisplayRow(DataExchange.getInstance().getVariantId1(), getResources().getString(R.string.ID000011), false);
                        else {
                            base.InitializeItem(variantid3);
                            return;
                        }
                    } else
                        DataExchange.getInstance().setVariantId3(null);

                    Functions.InitializeWithDefaultQty(base, qty);
                    //base.InitializeItem(qty);
                }


                return;
            } else if (base.GetCurrentItem() == unit) {
                if (!DataFunctions.isNullOrEmpty(data)) {

                    BooleanMessage result = Functions.invalidLenght(data, SetupFlags.getInstance().getLenghtManager(), InputType.Unit, this);

                    if (result.getResult() == true) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), result.getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(unit);
                        return;
                    }

                    DataExchange.getInstance().setStart(Calendar.getInstance().getTime());
                    DataExchange.getInstance().setUnit(data);

                    if (DataFunctions.isNullOrEmpty(data)) {
                        base.InitializeItem(sku, DataExchange.getInstance().getCurrentArticle().getSku());
                        return;
                    }

                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0001);

//                    cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." +
//                                                                                                                    Thread.currentThread().getStackTrace()[2].getMethodName());
                    cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                } else {
                    if (responseFromServer == 1) {
                        base.AddDisplayRow(DataExchange.getInstance().getUnit(), base.GetCurrentItem().label, false);

                        if (DataExchange.getInstance().getMessagetype().equals(DataExchange.MessageType.NEW)) {
                            base.InitializeItem(sku);
                            return;
                        } else if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku()))
                            base.InitializeItem(sku);
                        else {
                            base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getSku(), sku.label, false);
                            base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), getResources().getString(R.string.ID000013), false);

                            Presentation.ShowAttributes(base, DataExchange.getInstance(), true, false, false);
                            Functions.InitializeWithDefaultQty(base, qty);
                            //base.InitializeItem(qty);
                            return;
                        }
                    } else {
                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();
                            base.InitializeItem(unit);
                        } else
                            base.InitializeItem(sku);
                    }
                }
            } else if (base.GetCurrentItem() == sku) {

                if (!DataFunctions.isNullOrEmpty(data)) {
                    DataExchange.getInstance().setStart(Calendar.getInstance().getTime());
                    DataExchange.getInstance().getCurrentArticle().setBarcode(data);

                    if (DataFunctions.isNullOrEmpty(data)) {
                        base.InitializeItem(sku, "");
                        return;
                    }

                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0002);

                    cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                } else {
                    if (responseFromServer == 1) {
                        base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getSku(), sku.label, false);
                        base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), getResources().getString(R.string.ID000013), false);

                        if (DataExchange.getInstance().getCurrentArticle().getUsesbatch())
                            base.InitializeItem(batchnr, DataExchange.getInstance().getBatch());
                        else if (DataExchange.getInstance().getCurrentArticle().getUsesexpire())
                            base.InitializeItem(expiry, format.format(DataExchange.getInstance().getExpire()));
                        else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1)
                            base.InitializeItem(variantid1);
                        else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2)
                            base.InitializeItem(variantid2);
                        else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                            base.InitializeItem(variantid3);
                        else
                            Functions.InitializeWithDefaultQty(base, qty);
                            //base.InitializeItem(qty);
                    } else {
                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();
                        }
                        base.InitializeItem(sku);
                    }
                }
                return;

            } else if (base.GetCurrentItem() == batchnr) {

                DataExchange.getInstance().setBatch(data);

                if (DataFunctions.isNullOrEmpty(data)) {
                    if (SetupFlags.getInstance().getVerifybatch()) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), getResources().getString(R.string.ID000141), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(batchnr, "");
                        return;
                    }
                } else {
                    BooleanMessage result = Functions.invalidLenght(data, SetupFlags.getInstance().getLenghtManager(), InputType.Batch, this);

                    if (result.getResult() == true) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), result.getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(batchnr);
                        return;
                    }
                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);
                }

                if (DataExchange.getInstance().getCurrentArticle().getUsesexpire())
                    base.InitializeItem(expiry, format.format(DataExchange.getInstance().getExpire()));
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1)
                    base.InitializeItem(variantid1);
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2)
                    base.InitializeItem(variantid2);
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                    base.InitializeItem(variantid3);
                else
                    Functions.InitializeWithDefaultQty(base, qty);
                    //base.InitializeItem(qty);

                return;

            } else if (base.GetCurrentItem() == expiry) {

                // SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                //format.set2DigitYearStart((format.parse("01/01/2000")));
                DataExchange.getInstance().setExpire(format.parse(data));

                if (data.equals("  /  /    ")) {
                    if (SetupFlags.getInstance().getVerifybbe()) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), getResources().getString(R.string.ID000142), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(expiry, "");
                        return;
                    }
                } else
                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);

                if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1)
                    base.InitializeItem(variantid1);
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2)
                    base.InitializeItem(variantid2);
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                    base.InitializeItem(variantid3);
                else
                    Functions.InitializeWithDefaultQty(base, qty);
                    //base.InitializeItem(qty);

                return;

            } else if (base.GetCurrentItem() == variantid1) {

                DataExchange.getInstance().setVariantId1(data);

                if (!DataFunctions.isNullOrEmpty(data)) {
                    BooleanMessage result = Functions.invalidLenght(data, SetupFlags.getInstance().getLenghtManager(), InputType.Variantid1, this);

                    if (result.getResult() == true) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), result.getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(variantid1);
                        return;
                    }
                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);
                }
                if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2)
                    base.InitializeItem(variantid2);
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                    base.InitializeItem(variantid3);
                else
                    Functions.InitializeWithDefaultQty(base, qty);
                    //base.InitializeItem(qty);

                return;

            } else if (base.GetCurrentItem() == variantid2) {

                DataExchange.getInstance().setVariantId2(data);

                if (!DataFunctions.isNullOrEmpty(data)) {
                    BooleanMessage result = Functions.invalidLenght(data, SetupFlags.getInstance().getLenghtManager(), InputType.Variantid2, this);

                    if (result.getResult() == true) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), result.getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(variantid2);
                        return;
                    }

                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);
                }
                if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                    base.InitializeItem(variantid3);
                else
                    Functions.InitializeWithDefaultQty(base, qty);
                    //base.InitializeItem(qty);

                return;
            } else if (base.GetCurrentItem() == variantid3) {

                DataExchange.getInstance().setVariantId3(data);

                if (!DataFunctions.isNullOrEmpty(data)) {
                    BooleanMessage result = Functions.invalidLenght(data, SetupFlags.getInstance().getLenghtManager(), InputType.Variantid3, this);

                    if (result.getResult() == true) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), result.getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(variantid3);
                        return;
                    }

                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);
                }

                Functions.InitializeWithDefaultQty(base, qty);
                //base.InitializeItem(qty);

                return;

            } else if (base.GetCurrentItem() == location) {

                if (!DataFunctions.isNullOrEmpty(data)) {
                    if (DataFunctions.isNullOrEmpty(data)) {
                        base.InitializeItem(location);
                        return;
                    }

                    DataExchange.getInstance().getToLocation().setEntirePosition(data);
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0004);

                    cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                } else {
                    if (responseFromServer == 1) {
                        base.AddDisplayRow(DataExchange.getInstance().getToLocation().getEntirePosition(), base.GetCurrentItem().label, false);
                        FinalStep();
                    } else {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(location);
                    }
                }
                return;

            } else if (base.GetCurrentItem() == qty) {

                DataExchange.getInstance().setQty(DataFunctions.readDecimal(data));

                base.AddDisplayRow(Double.toString(DataExchange.getInstance().getQty()), qty.label, false);

                DataExchange.getInstance().setToLocation(new WarehouseLocation());

                if (menuItem.getDefToLocation() != 0 && DataFunctions.isNullOrEmpty(DataExchange.getInstance().getUnit()))
                    DataExchange.getInstance().getToLocation().setPositionCode(menuItem.getDefToLocation());

                if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getUnit())) {
                    DataExchange.getInstance().getToLocation().setEntirePosition(DataExchange.getInstance().getFromLocation().getEntirePosition());
                    DataExchange.getInstance().getToLocation().setPositionCode(DataExchange.getInstance().getFromLocation().getPositionCode());
                }

                if ((!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getToLocation()) && DataExchange.getInstance().getToLocation().getPositionCode() != 0)) // || !Functions.IsNullOrEmpty(DataExchange.getInstance().Unit))
                    FinalStep();
                else
                    base.InitializeItem(location);

                return;
            }
        } catch (Exception ex) {
            Log.e("", ex.getMessage());
        }
    }

    private void FinalStep() {
        try {
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F2002);
            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FinalStep()");
            synchronized (cloudConnector) {
                cloudConnector.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void OnBackAction() {
        try {
            ActivityItem previousItem = base.GetPreviousItem();

            if (previousItem == null)
                finish();
            else {
                if (previousItem == qty)
                    Functions.InitializeWithDefaultQty(base, qty);
                else
                    base.InitializeItem(previousItem);
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

    public void Test(String data, boolean ok) {

    }

    public void taskCompletionResult(String result, int step) throws Exception {

        if (!cloudConnector.getPostExceuteMethod().equals("mobile.logicha.wcftester.ManualLoad.FinalStep()")) {
            Object[] params = {null, result.equals("OK") ? step : 0};
            this.getClass().getMethods()[0].invoke(this, params);
        } else {
            // IF FINAL STEP ....

            if (result.equals("OK")) {

                Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), getResources().getString(R.string.operation_successfully_completed), Snackbar.LENGTH_LONG)
                        .setAction("No action", null);

                sbw.getView().setBackgroundColor(Color.parseColor("#108a1f"));
                sbw.show();

                // Re-Initialize and restart...
                base.ClearAllDisplay();

                DataExchange.getInstance().format();
                DataExchange.getInstance().setFunctionName(DataExchange.Operations.F2002); // MANUAL LOAD
                DataExchange.getInstance().setLoadCausal(menuItem.getDefLoadCausal());
                DataExchange.getInstance().getToLocation().setPositionCode(menuItem.getDefToLocation());

                if (SetupFlags.getInstance().getUsesmaxibarcode())
                    base.InitializeItem(eanucc);
                else if (SetupFlags.getInstance().getSkuOnLogisticUnit() && SetupFlags.getInstance().getUseslogisticunit())
                    base.InitializeItem(unit); //Logistic Unit is mono-sku
                else
                    base.InitializeItem(sku);   //Logistic Unit has many sku
            } else {
                AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.INFO, this);
                alertMessageBuilder.Show();
                base.InitializeItem(location);
            }
        }
        //AcceptText(null, (result.equals("OK") ? true :false));
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
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
}
