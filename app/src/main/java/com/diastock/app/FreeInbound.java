package com.diastock.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.view.Menu;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.diastock.app.InputArea.TYPE_DATE;
import static com.diastock.app.InputArea.TYPE_EANUCC;
import static com.diastock.app.InputArea.TYPE_LOCATION;
import static com.diastock.app.InputArea.TYPE_LOGUNIT;
import static com.diastock.app.InputArea.TYPE_QTY;
import static com.diastock.app.InputArea.TYPE_TEXT;


public class FreeInbound extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    ActivityItem order = new ActivityItem(0, "Ordine", 20, TYPE_TEXT, "", false, DataExchange.PROP_ORDER, false);
    ActivityItem supplier = new ActivityItem(1, "Fornitore", 20, TYPE_TEXT, "", false, DataExchange.PROP_SUPPLIER, false);
    ActivityItem decodedsupplier = new ActivityItem(2, "Descrizione Fornitore", 50, TYPE_TEXT, "", false, null, false);
    ActivityItem docnr = new ActivityItem(3, "N° Documento", 30, TYPE_TEXT, "", false, DataExchange.PROP_DOCNR, false);
    ActivityItem docdate = new ActivityItem(4, "Data Documento", 10, TYPE_DATE, "", false, DataExchange.PROP_DOCDATE, false);
    ActivityItem container = new ActivityItem(5, "Container", 20, TYPE_TEXT, "", false, DataExchange.PROP_CONTAINER, false);
    ActivityItem eanucc = new ActivityItem(6, "EAN/UCC", 200, TYPE_EANUCC, "", false, DataExchange.PROP_EANUCC, false);
    ActivityItem unit = new ActivityItem(7, "Unità Logistica", 40, TYPE_LOGUNIT, "", false, DataExchange.PROP_UNIT, false);
    ActivityItem sku = new ActivityItem(8, "Articolo", 50, TYPE_TEXT, "", false, DataExchange.PROP_CURRENTARTICLE, false);
    ActivityItem skuDescr = new ActivityItem(9, "Descrizione Articolo", 50, TYPE_TEXT, "", false, null, false);
    ActivityItem batchnr = new ActivityItem(10, "Lotto", 50, TYPE_TEXT, "", false, DataExchange.PROP_BATCH, false);
    ActivityItem expiry = new ActivityItem(11, "Scadenza", 10, TYPE_DATE, "", false, DataExchange.PROP_EXPIRE, false);
    ActivityItem variantid1 = new ActivityItem(12, "Variante1", 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID1, false);
    ActivityItem variantid2 = new ActivityItem(13, "Variante2", 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID2, false);
    ActivityItem variantid3 = new ActivityItem(14, "Variante3", 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID3, false);
    ActivityItem qty = new ActivityItem(15, "Quantità", 6, TYPE_QTY, "", false, DataExchange.PROP_QTY, false);
    ActivityItem location = new ActivityItem(16, "Posizione", 23, TYPE_LOCATION, "", false, DataExchange.PROP_TOLOCATION, false);
    ActivityItem palletcode = new ActivityItem(17, "Codice Pallet", 3, TYPE_TEXT, "", false, DataExchange.PROP_PALLETCODE, false);

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    DisplayFragment base;
    MenuItem menuItem = null;
    CloudConnector cloudConnector = null;

    String currentorder, currentsupplier, currentdocnr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manual_download);

        base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));

        try {
            base.ClearAllDisplay();
            Bundle params = getIntent().getExtras();
            int position = -1; // or other values
            if (params != null)
                position = params.getInt("menuPosition");

            menuItem = UserMenu.getInstance().getMenuItem(position);
            setTitle(menuItem.getFunctionDescription());

            DataExchange.getInstance().format();
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1002); // FREE INBOUND
            DataExchange.getInstance().setLoadCausal(menuItem.getDefLoadCausal());
            DataExchange.getInstance().getToLocation().setPositionCode(menuItem.getDefToLocation());

            base.InitializeItem(supplier);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void AcceptText(String data, int responseFromServer) {
        try {

            if (base.GetCurrentItem() == supplier) {

                if (responseFromServer == 1) {
                    //base.AddDisplayRow(DataExchange.getInstance().getSupplier().getBarcode(), base.GetCurrentItem().label, false);
                    base.AddDisplayRow(DataExchange.getInstance().getSupplier().getDescription(), base.GetCurrentItem().label, false);

                    if (SetupFlags.getInstance().isRfautoinsertarrivalorder())
                        base.InitializeItem(order);
                    else
                        base.InitializeItem(docnr);

                    return;
                } else {
                    if (!DataFunctions.isNullOrEmpty(data)) {
                        DataExchange.getInstance().getSupplier().setBarcode(data);
                        DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0005); // GET SUPPLIER DATA
                        cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                        synchronized (cloudConnector) {
                            cloudConnector.execute();
                        }

                        return;

                    } else {

                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();
                        } else {

                            if (SetupFlags.getInstance().getUsesmaxibarcode())
                                base.InitializeItem(eanucc);
                            else if (SetupFlags.getInstance().getUseslogisticunit())
                                base.InitializeItem(unit);
                            else
                                base.InitializeItem(sku);
                        }
                        return;
                    }
                }
            } else if (base.GetCurrentItem() == order) {
                if (responseFromServer == 1) {
                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                    } else {
                        base.AddDisplayRow(DataExchange.getInstance().getOrder(), base.GetCurrentItem().label, false);
                        base.InitializeItem(docnr);
                    }
                } else {
                    if (!DataFunctions.isNullOrEmpty(data)) {
                        DataExchange.getInstance().setOrder(data);
                        DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1002);
                        DataExchange.getInstance().setStep('C');
                        cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                        synchronized (cloudConnector) {
                            cloudConnector.execute();
                        }
                    }
                    else if(DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                    }
                }
                return;
           } else if (base.GetCurrentItem() == docnr) {

                if (!DataFunctions.isNullOrEmpty(data)) {
                    DataExchange.getInstance().setDocnr(data);
                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);
                }

                base.InitializeItem(docdate);
                return;

            } else if (base.GetCurrentItem() == docdate) {

                if (!DataFunctions.isNullOrEmpty(data)) {
                    DataExchange.getInstance().setDocdate(format.parse(data));
                    base.AddDisplayRow(format.format(DataExchange.getInstance().getDocdate()), base.GetCurrentItem().label, false);
                }

                if (SetupFlags.getInstance().getUsescontainer())
                    base.InitializeItem(container);
                else if (SetupFlags.getInstance().getUsesmaxibarcode())
                    base.InitializeItem(eanucc);
                else if (SetupFlags.getInstance().getUseslogisticunit())
                    base.InitializeItem(unit);
                else
                    base.InitializeItem(sku);
            } else if (base.GetCurrentItem() == container) {

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
                                DataExchange.getInstance().setStep('\0');

                                cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                                synchronized (cloudConnector) {
                                    cloudConnector.execute();
                                }

                                return;
                            } else
                                base.InitializeItem(sku);

                        }
                    }
                } else {

                    if (SetupFlags.getInstance().getUseslogisticunit()) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getUnit()))
                            base.AddDisplayRow(DataExchange.getInstance().getUnit(), "Unità Logistica", false);
                        else {
                            base.InitializeItem(unit);
                            return;
                        }
                    }

                    if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku()) &&
                            !DataExchange.getInstance().getCurrentArticle().getSku().equals("")) {
                        base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getSku(), sku.label, false);
                        base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), "Descrizione", false);
                    } else {
                        base.InitializeItem(sku, DataExchange.getInstance().getCurrentArticle().getSku());
                        return;
                    }

                    DataExchange.getInstance().setStart(Calendar.getInstance().getTime());


                    if (DataExchange.getInstance().getCurrentArticle().getUsesbatch()) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getBatch()))
                            base.AddDisplayRow(DataExchange.getInstance().getBatch(), "Lotto", false);
                        else {
                            base.InitializeItem(batchnr);
                            return;
                        }
                    } else
                        DataExchange.getInstance().setBatch(null);

                    if (DataExchange.getInstance().getCurrentArticle().getUsesexpire()) {
                        if (!DataExchange.getInstance().getExpire().equals((DataFunctions.getEmptyDate()))) {
                            base.AddDisplayRow(format.format(DataExchange.getInstance().getExpire()), "Scadenza", false);
                        } else {
                            base.InitializeItem(expiry);
                            return;
                        }
                    } else
                        DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());

                    if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId1()))
                            base.AddDisplayRow(DataExchange.getInstance().getVariantId1(), "Variante 1", false);
                        else
                            base.InitializeItem(variantid1);
                    } else
                        DataExchange.getInstance().setVariantId1(null);

                    if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId2()))
                            base.AddDisplayRow(DataExchange.getInstance().getVariantId1(), "Variante 2", false);
                        else {
                            base.InitializeItem(variantid2);
                            return;
                        }
                    } else
                        DataExchange.getInstance().setVariantId2(null);

                    if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId3()))
                            base.AddDisplayRow(DataExchange.getInstance().getVariantId1(), "Variante 3", false);
                        else {
                            base.InitializeItem(variantid3);
                            return;
                        }
                    } else
                        DataExchange.getInstance().setVariantId3(null);

                    //base.InitializeItem(qty);
                    Functions.InitializeWithDefaultQty(base, qty);
                }


                return;

            } else if (base.GetCurrentItem() == unit) {
                if (!DataFunctions.isNullOrEmpty(data)) {
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
                            base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), "Descrizione", false);

                            Presentation.ShowAttributes(base, DataExchange.getInstance(), true, false, false);
                            //base.InitializeItem(qty);
                            Functions.InitializeWithDefaultQty(base, qty);
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
                        base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), "Descrizione Articolo", false);

                        if (DataExchange.getInstance().getCurrentArticle().getUsesbatch())
                            base.InitializeItem(batchnr, DataExchange.getInstance().getBatch());
                        else if (DataExchange.getInstance().getCurrentArticle().getUsesexpire())
                            base.InitializeItem(expiry, format.format(DataExchange.getInstance().getExpire()));
                        else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1)
                            base.InitializeItem(variantid1);
                        else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2)
                            base.InitializeItem(variantid3);
                        else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                            base.InitializeItem(variantid3);
                        else
                            //base.InitializeItem(qty);
                            Functions.InitializeWithDefaultQty(base, qty);
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
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), "Lotto obbligatorio", AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(batchnr, "");
                        return;
                    }
                } else
                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);

                if (DataExchange.getInstance().getCurrentArticle().getUsesexpire())
                    base.InitializeItem(expiry, format.format(DataExchange.getInstance().getExpire()));
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1)
                    base.InitializeItem(variantid1);
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2)
                    base.InitializeItem(variantid3);
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                    base.InitializeItem(variantid3);
                else
                    //base.InitializeItem(qty);
                    Functions.InitializeWithDefaultQty(base, qty);

                return;

            } else if (base.GetCurrentItem() == expiry) {

                // SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                //format.set2DigitYearStart((format.parse("01/01/2000")));
                DataExchange.getInstance().setExpire(format.parse(data));

                if (data.equals("  /  /    ")) {
                    if (SetupFlags.getInstance().getVerifybbe()) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), "Scadenza obbligatoria", AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(expiry, "");
                        return;
                    }
                } else
                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);

                if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1)
                    base.InitializeItem(variantid1);
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2)
                    base.InitializeItem(variantid3);
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                    base.InitializeItem(variantid3);
                else
                    //base.InitializeItem(qty);
                    Functions.InitializeWithDefaultQty(base, qty);

                return;

            } else if (base.GetCurrentItem() == variantid1) {

                DataExchange.getInstance().setVariantId1(data);

                if (!DataFunctions.isNullOrEmpty(data))
                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);

                if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2)
                    base.InitializeItem(variantid3);
                else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                    base.InitializeItem(variantid3);
                else
                    //base.InitializeItem(qty);
                    Functions.InitializeWithDefaultQty(base, qty);

                return;

            } else if (base.GetCurrentItem() == variantid2) {

                DataExchange.getInstance().setVariantId2(data);

                if (!DataFunctions.isNullOrEmpty(data))
                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);

                if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                    base.InitializeItem(variantid3);
                else
                    //base.InitializeItem(qty);
                    Functions.InitializeWithDefaultQty(base, qty);

                return;
            } else if (base.GetCurrentItem() == variantid3) {

                DataExchange.getInstance().setVariantId3(data);

                if (!DataFunctions.isNullOrEmpty(data))
                    base.AddDisplayRow(data, base.GetCurrentItem().label, false);

                //base.InitializeItem(qty);
                Functions.InitializeWithDefaultQty(base, qty);

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
                    DataExchange.getInstance().getToLocation().setEntirePosition(DataExchange.getInstance().getToLocation().getEntirePosition());
                    DataExchange.getInstance().getToLocation().setPositionCode(DataExchange.getInstance().getToLocation().getPositionCode());
                }

                if ((DataExchange.getInstance().getToLocation().getPositionCode() != 0)) // || !Functions.IsNullOrEmpty(DataExchange.getInstance().Unit))
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
            DataExchange.getInstance().setStep('\0');
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1002);
            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FinalStep()");

            synchronized (cloudConnector) {
                cloudConnector.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void OnClosed() {
        try {
            if (DataExchange.getInstance().getMessagetype().equals(DataExchange.MessageType.OK)) {
                Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), getResources().getString(R.string.operation_successfully_completed), Snackbar.LENGTH_LONG)
                        .setAction("No action", null);

                sbw.getView().setBackgroundColor(Color.parseColor("#108a1f"));
                sbw.show();
                base.InitializeItem(supplier);
                base.ClearAllDisplay();
                DataExchange.getInstance().setStep('\0');
            } else {
                AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                alertMessageBuilder.Show();
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


        if (!cloudConnector.getPostExceuteMethod().equals("mobile.logicha.wcftester.FreeInbound.FinalStep()"))
        //this.getClass().getMethod(cloudConnector.getPostExceuteMethod()).invoke (this, (result.equals("OK") ? true :false));
        {
            if (DataExchange.getInstance().getStep() == 'K')
            {
                OnClosed();
            }
            else {
                Object[] params = {null, result.equals("OK") ? step : 0};
                this.getClass().getMethods()[0].invoke(this, params);
            }
        } else {

            if (result.equals("OK")) {

                Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), "Operazione Completata Correttamente", Snackbar.LENGTH_LONG)
                        .setAction("No action", null);

                sbw.getView().setBackgroundColor(Color.parseColor("#108a1f"));
                sbw.show();

                // Re-Initialize and restart...
                base.ClearAllDisplay();

                String savedSupplier = DataExchange.getInstance().getSupplier().getBarcode();
                String savedSupplierDescription = DataExchange.getInstance().getSupplier().getDescription();
                String savedDocnr = DataExchange.getInstance().getDocnr();
                Date savedDocDate = DataExchange.getInstance().getDocdate();


                //DataExchange.getInstance().format();

//                DataExchange.getInstance().getSupplier().setBarcode(savedSupplier);
//                DataExchange.getInstance().getSupplier().setDescription(savedSupplierDescription);
//                DataExchange.getInstance().setDocnr(savedDocnr);
//                DataExchange.getInstance().setDocdate(savedDocDate);

                DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1002); // FREE INBOUND
//                DataExchange.getInstance().setLoadCausal(menuItem.getDefLoadCausal());
//                DataExchange.getInstance().getToLocation().setPositionCode(menuItem.getDefToLocation());

                base.AddDisplayRow(DataExchange.getInstance().getSupplier().getDescription(), supplier.label, false);
                base.AddDisplayRow(DataExchange.getInstance().getDocnr(), docnr.label, false);
                base.AddDisplayRow(format.format(DataExchange.getInstance().getDocdate()), docdate.label, false);

                if (SetupFlags.getInstance().getUsesmaxibarcode())
                    base.InitializeItem(eanucc);
                else if (SetupFlags.getInstance().getSkuOnLogisticUnit() && SetupFlags.getInstance().getUseslogisticunit())
                    base.InitializeItem(unit); //Logistic Unit is mono-sku
                else
                    base.InitializeItem(sku);   //Logistic Unit has many sku


                DataExchange.getInstance().setCurrentArticle(new Article());
                DataExchange.getInstance().setBatch(null);
                DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());
                DataExchange.getInstance().setVariantId1(null);
                DataExchange.getInstance().setVariantId2(null);
                DataExchange.getInstance().setVariantId3(null);
                DataExchange.getInstance().setQty(0);
            } else {
                AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.INFO, this);
                alertMessageBuilder.Show();
                base.InitializeItem(base.GetPreviousItem());
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.menu_item_new_quote) {// TODO put your code here to respond to the button tap
            finish();
            return true;
        } else if (itemId == R.id.menu_item_options) {
            try {

                if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getOrder()) &&
                        !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getDocnr()) &&
                        !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getSupplier().getBarcode())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FreeInbound.this);
                    builder.setTitle("Opzioni");

                    String[] arrayChoices = new String[]{
                            "Chiudi Ordine"
                    };

                    builder.setItems(arrayChoices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            switch (position) {
                                case 0:
                                    try {

                                        try {
                                            DataExchange.getInstance().setStep('K');
                                            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1002);
                                            cloudConnector = new CloudConnector(FreeInbound.this, FreeInbound.this, FreeInbound.this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "OnClosed()");

                                            synchronized (cloudConnector) {
                                                cloudConnector.execute();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        return;
                                    } catch (Exception e) {
                                        return;
                                    }
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                return true;
            } catch (Exception e) {
                return false;
            }
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
