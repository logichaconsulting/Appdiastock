package com.diastock.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.diastock.app.InputArea.TYPE_DATE;
import static com.diastock.app.InputArea.TYPE_EANUCC;
import static com.diastock.app.InputArea.TYPE_LOCATION;
import static com.diastock.app.InputArea.TYPE_LOGUNIT;
import static com.diastock.app.InputArea.TYPE_QTY;
import static com.diastock.app.InputArea.TYPE_SERIAL;
import static com.diastock.app.InputArea.TYPE_TEXT;

public class Inbound extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    ActivityItem barcode = null;
    ActivityItem container = null;
    ActivityItem eanucc = null;
    ActivityItem newsku = null;
    ActivityItem newskuDescr = null;
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
    ActivityItem serialnr = null;
    ActivityItem optionalserialnr = null;
    ActivityItem palletcode = null;


    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    DisplayFragment base;
    Menu actionMenu;
    MenuItem menuItem = null;
    CloudConnector cloudConnector = null;

    double requiredQty;
    double orderRequiredQty;
    double partialQty;
    double arrivedQty;
    double suggestedQty;
    boolean topUpIteration;
    int shootedSerials = 0;
    String suggestedSerialnr = null;
    WarehouseLocation topUpLocation = null;
    WarehouseLocation suggestedStorageLocation = null;
    int position = -1; // or other values

    Barcodes barcodes = null;

    String order, docnr, company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbound);

        base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));

        try {
            base.ClearAllDisplay();
            Bundle params = getIntent().getExtras();

            if (params != null)
                position = params.getInt("menuPosition");

            barcode = new ActivityItem(1, getResources().getString(R.string.ID000115), 200, TYPE_TEXT, "", false, DataExchange.PROP_ORDER, true);
            container = new ActivityItem(2, getResources().getString(R.string.ID000004), 20, TYPE_TEXT, "", false, DataExchange.PROP_CONTAINER, true);
            eanucc = new ActivityItem(3, getResources().getString(R.string.ID000059), 200, TYPE_EANUCC, "", false, DataExchange.PROP_EANUCC, true);
            newsku = new ActivityItem(4, getResources().getString(R.string.ID000200), 50, TYPE_TEXT, "", false, DataExchange.PROP_CURRENTARTICLE, true);
            newskuDescr = new ActivityItem(5, getResources().getString(R.string.ID000201), 50, TYPE_TEXT, "", false, null, false);
            unit = new ActivityItem(6, getResources().getString(R.string.ID000005), 40, TYPE_LOGUNIT, "", false, DataExchange.PROP_UNIT, true);
            sku = new ActivityItem(7, getResources().getString(R.string.ID000003), 50, TYPE_TEXT, "", false, DataExchange.PROP_CURRENTARTICLE, true);
            skuDescr = new ActivityItem(8, getResources().getString(R.string.ID000013), 50, TYPE_TEXT, "", false, null, false);
            batchnr = new ActivityItem(9, getResources().getString(R.string.ID000007), 50, TYPE_TEXT, "", false, DataExchange.PROP_BATCH, true);
            expiry = new ActivityItem(10, getResources().getString(R.string.ID000008), 10, TYPE_DATE, "", false, DataExchange.PROP_EXPIRE, false);
            variantid1 = new ActivityItem(11, getResources().getString(R.string.ID000009), 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID1, true);
            variantid2 = new ActivityItem(12, getResources().getString(R.string.ID000010), 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID2, true);
            variantid3 = new ActivityItem(13, getResources().getString(R.string.ID000011), 50, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID3, true);
            qty = new ActivityItem(14, getResources().getString(R.string.ID000012), 6, TYPE_QTY, "", false, DataExchange.PROP_QTY, false);
            location = new ActivityItem(15, getResources().getString(R.string.ID000006), 23, TYPE_LOCATION, "", false, DataExchange.PROP_FROMLOCATION, true);
            serialnr = new ActivityItem(16, getResources().getString(R.string.ID000088), 40, TYPE_SERIAL, "", false, DataExchange.PROP_SERIALNR, true);
            optionalserialnr = new ActivityItem(17, getResources().getString(R.string.ID000133), 40, TYPE_SERIAL, "", false, DataExchange.PROP_OPTIONAL_SERIALNR, true);
            palletcode = new ActivityItem(18, getResources().getString(R.string.ID000056), 40, TYPE_TEXT, "", false, DataExchange.PROP_PALLETCODE, false);


            menuItem = UserMenu.getInstance().getMenuItem(position);
            setTitle(menuItem.getFunctionDescription());

            barcodes = new Barcodes();

            DataExchange.getInstance().format();
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001); // INBOUND
            DataExchange.getInstance().setLoadCausal(menuItem.getDefLoadCausal());
            DataExchange.getInstance().getToLocation().setPositionCode(menuItem.getDefToLocation());

            base.InitializeItem(barcode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fragment, menu);
        menu.findItem(R.id.menu_item_options).setVisible(true);
        menu.findItem(R.id.menu_item_new_quote).setIcon(R.drawable.ic_action_menu_main);
        //menu.findItem(R.id.menu_item_options).setIcon(R.drawable.ic_action_login);
        actionMenu = menu;
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

                if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getOrder())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Inbound.this);
                    builder.setTitle("Opzioni");

                    String[] arrayChoices = new String[]{
                            "Chiudi Ordine", "Situazione Ordine", "Modifica Attributi", "Stampa Etichette"
                    };

                    builder.setItems(arrayChoices, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            switch (position) {
                                case 0:
                                    try {

                                        Intent intent = new Intent(getApplicationContext(), InboundClose.class);

                                        Bundle b = new Bundle();
                                        b.putString("ORDER", order); //Your id
                                        b.putString("DOCUMENT", docnr); //Your id

                                        intent.putExtras(b);
                                        //intent.putExtra("COMPANY", company);
                                        startActivity(intent);

                                        base.ClearAllDisplay();

                                        base.InitializeItem(barcode);

                                        return;
                                    } catch (Exception e) {
                                        return;
                                    }
                                case 1:

                                    break;
                                case 2:

                                    break;
                                case 3:
                                    try {

                                        Intent intent = new Intent(getApplicationContext(), InboundPrintLogunit.class);

                                        startActivity(intent);

                                        base.ClearAllDisplay();

                                        base.InitializeItem(barcode);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void AcceptText(String data, int responseFromServer) {
        try {

            if (base.GetCurrentItem() == barcode) {

                if (responseFromServer == 0) {
                    DataExchange.getInstance().setStart(Calendar.getInstance().getTime());

                    if (data == null) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(barcode);
                        return;
                    }

                    String[] values = data.split(Pattern.quote("."), 3);

                    DataExchange.getInstance().setOrder(values[0]);
                    DataExchange.getInstance().setDocnr(values[1]);

                    Supplier supplier = new Supplier();
                    supplier.setBarcode(values[2]);

                    DataExchange.getInstance().setSupplier(supplier);

                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001);
                    DataExchange.getInstance().setStep('C');

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    cloudConnector.setPostStep(1);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }

                    return;
                } else {
                    base.ClearAllDisplay();

                    base.AddDisplayRow(DataExchange.getInstance().getOrder() + " / " + DataExchange.getInstance().getDocnr(), this.getResources().getString(R.string.ID000115), false);
                    //base.AddDisplayRow(DataExchange.getInstance().getDocnr(), "Documento", false);
                    base.AddDisplayRow(DataExchange.getInstance().getSupplier().getDescription(), this.getResources().getString(R.string.ID000014), false);

                    if (SetupFlags.getInstance().getUsescontainer())
                        base.InitializeItem(container);
                    else if (SetupFlags.getInstance().getUsesmaxibarcode())
                        base.InitializeItem(eanucc);
                    else if (SetupFlags.getInstance().getUseslogisticunit())
                        base.InitializeItem(unit);
                    else
                        base.InitializeItem(sku);
                    return;
                }
            } else if (base.GetCurrentItem() == container) {
                shootedSerials = 0;
                suggestedSerialnr = null;

                //DataExchange.getInstance().getun = false;

                DataExchange.getInstance().setSerialNumbers(null);
                DataExchange.getInstance().setOptionalserialNumbers(null);
                DataExchange.getInstance().setBatches(null);

                DataExchange.getInstance().setStart(Calendar.getInstance().getTime());
                DataExchange.getInstance().setContainer(data);

                if (!Functions.isNullOrEmpty(data)) {
                    base.AddDisplayRow(DataExchange.getInstance().getContainer(), this.getResources().getString(R.string.ID000004), false);
                }
                if (SetupFlags.getInstance().getUsesmaxibarcode())
                    base.InitializeItem(eanucc);
                else if (SetupFlags.getInstance().getUseslogisticunit())
                    base.InitializeItem(unit);
                else
                    base.InitializeItem(sku);
                return;

            } else if (base.GetCurrentItem() == eanucc) {


                if (responseFromServer == 0) {

                    shootedSerials = 0;
                    suggestedSerialnr = null;

                    DataExchange.getInstance().setSerialNumbers(null);
                    DataExchange.getInstance().setOptionalserialNumbers(null);
                    DataExchange.getInstance().setBatches(null);

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
                                DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0008);

                                cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                                synchronized (cloudConnector) {
                                    String res = cloudConnector.execute().get();
                                }
                                return;
                            } else
                                base.InitializeItem(sku);

                        }

                    }
                } else if (responseFromServer == 1) {

                    if (SetupFlags.getInstance().getInboundCreateArticle() &&
                            !SetupFlags.getInstance().getUseslogisticunit() &&
                            DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.QUESTION) {

                        DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();
                        //Item does not exist! Do you want to create it?
                        if (dialogMessageBuilder.getYesNoWithExecutionStop(getResources().getString(R.string.warning), getResources().getString(R.string.ID000202), this) == 1) {
                            if (Functions.isNullOrEmpty(DataExchange.getInstance().getGenericString2()))
                                base.InitializeItem(newsku);
                            else {
                                base.AddDisplayRow(DataExchange.getInstance().getGenericString2(), getResources().getString(R.string.ID000200), false);
                                //DataExchange.getInstance().getCurrentArticle().setBarcode(DataExchange.getInstance().getCurrentArticle().getSku());
                                DataExchange.getInstance().getCurrentArticle().setBarcode(DataExchange.getInstance().getGenericString2());
                                base.InitializeItem(newskuDescr);
                            }
                        } else {
                            base.InitializeItem(eanucc);
                        }
                        return;
                    }

                    if (SetupFlags.getInstance().getUseslogisticunit()) {
                        if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getUnit()))
                            base.AddDisplayRow(DataExchange.getInstance().getUnit(), this.getResources().getString(R.string.ID000005), false);
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

                    qtyStep(eanucc);
                } else {
                    arrivedQty = DataExchange.getInstance().getPartialQty();
                    base.AddDisplayRow(String.format("%.0f", arrivedQty), getResources().getString(R.string.ID000040), false);

                    //SetVideoRowLabel(localizer.GetString(40)); // Arrived Quantity
                    //SetVideoRowData(DataExchange.GetInstance().PartialQty.ToString("#########0"));

                    requiredQty = DataExchange.getInstance().getRequiredQty();
                    orderRequiredQty = DataExchange.getInstance().getRequiredQty();
                    qty.label = getResources().getString(R.string.ID000012) + (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");
                    suggestedSerialnr = DataExchange.getInstance().getSerialNumbers();

                    if (suggestedQty > 0)
                        base.InitializeItem(qty, String.format("%.0f", suggestedQty));
                    else
                        base.InitializeItem(qty, String.format("%.0f", DataExchange.getInstance().getQty()));
                }
                return;
            } else if (base.GetCurrentItem() == newsku) {
                if (!DataFunctions.isNullOrEmpty(data)) {

                    if (Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getBarcode()))
                        DataExchange.getInstance().getCurrentArticle().setBarcode(DataExchange.getInstance().getCurrentArticle().getSku());

                    DataExchange.getInstance().getCurrentArticle().setSku(data);
                    base.AddDisplayRow(data, getResources().getString(R.string.ID000200), false);
                    base.InitializeItem(newskuDescr);
                }
            } else if (base.GetCurrentItem() == newskuDescr) {

                if (responseFromServer == 0) {
                    if (DataFunctions.isNullOrEmpty(data)) {
                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.WARNING, this);
                            alertMessageBuilder.Show();
                            base.InitializeItem(newskuDescr);
                        }
                        return;
                    }

                    DataExchange.getInstance().getCurrentArticle().setDescription(data);
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F0006);
                    DataExchange.getInstance().setStep('Z');

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
                    //base.ClearAllDisplay();
                    base.InitializeItem(eanucc);
                }

            } else if (base.GetCurrentItem() == unit) {
                if (responseFromServer == 0) {

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(unit);
                        return;
                    } else if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.MAPPED) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), getResources().getString(R.string.ID000030), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(unit);
                        return;
                    }

                    shootedSerials = 0;
                    suggestedSerialnr = null;
                    DataExchange.getInstance().setLogunitEdited(false);

                    DataExchange.getInstance().setSerialNumbers(null);
                    DataExchange.getInstance().setOptionalserialNumbers(null);
                    DataExchange.getInstance().setBatches(null);
                    DataExchange.getInstance().setStart(Calendar.getInstance().getTime());


                    if (DataExchange.getInstance().getDoctype().getReadhplogunit())
                        DataExchange.getInstance().setUnit(barcodes.getHPSerianNr(data));
                    else
                        DataExchange.getInstance().setUnit(data);

                    if (DataFunctions.isNullOrEmpty(data)) {
                        base.InitializeItem(sku, DataExchange.getInstance().getCurrentArticle().getSku());
                        return;
                    } else {
                        BooleanMessage result = Functions.invalidLenght(data, SetupFlags.getInstance().getLenghtManager(), InputType.Unit, this);

                        if (result.getResult() == true) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), result.getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();
                            base.InitializeItem(unit);
                            return;
                        }

                        base.AddDisplayRow(DataExchange.getInstance().getUnit(), base.GetCurrentItem().label, false);
                    }
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0001);

//                    cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." +
//                                                                                                                    Thread.currentThread().getStackTrace()[2].getMethodName());
                    cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");
                    cloudConnector.setPostStep(1);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                } else if (responseFromServer == 1) {

                        /*
                        if (DataExchange.getInstance().getMessagetype().equals(DataExchange.MessageType.NEW)) {
                            base.InitializeItem(sku);
                            return;
                        } else if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku()))
                            base.InitializeItem(sku);
                        else {
                            base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getSku(), sku.label, false);
                            base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), "Descrizione", false);

                            Presentation.ShowAttributes(base, DataExchange.getInstance(), true, false, false);
                            base.InitializeItem(qty);
                            return;
                        }
                        */

                    if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku())) {
                        if (DataExchange.getInstance().getDoctype().getEditincominglogunit()) {
                            base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getSku(), sku.label, false);
                            base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), getResources().getString(R.string.ID000013), false);

                            DataExchange.getInstance().setLogunitEdited(true);

                            if (DataExchange.getInstance().getCurrentArticle().getUsesbatch())// && !Functions.IsNullOrEmpty(DataExchange.GetInstance().Batch))
                                base.InitializeItem(batchnr, DataExchange.getInstance().getBatch());
                            else if (DataExchange.getInstance().getCurrentArticle().getUsesexpire()) // && !DataExchange.GetInstance().Expire.Date.Equals(new DateTime().Date))
                                base.InitializeItem(expiry, DataExchange.getInstance().getExpire().toString());
                            else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1)// && !Functions.IsNullOrEmpty(DataExchange.GetInstance().VariantId1))
                                base.InitializeItem(variantid1, DataExchange.getInstance().getVariantId1());
                            else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2)// && !Functions.IsNullOrEmpty(DataExchange.GetInstance().VariantId2))
                                base.InitializeItem(variantid2, DataExchange.getInstance().getVariantId2());
                            else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)// && !Functions.IsNullOrEmpty(DataExchange.GetInstance().VariantId3))
                                base.InitializeItem(variantid3, DataExchange.getInstance().getVariantId3());
                            else
                                qtyStep(sku);
                        } else {
                            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001);
                            DataExchange.getInstance().setStep('L');

                            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");
                            cloudConnector.setPostStep(2);
                            synchronized (cloudConnector) {
                                cloudConnector.execute();
                            }
                        }
                    }
                }
                if (responseFromServer == 2) {
                    base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getSku(), sku.label, false);
                    base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), getResources().getString(R.string.ID000013), false);

                    Presentation.ShowAttributes(base, DataExchange.getInstance(), false, false, false);

                    //qty.label = localizer.GetString(40);
                    //InitializeItem(ref qty, DataExchange.GetInstance().Qty.ToString("#########0"));
                    qtyStep(unit);

                } else {
                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(unit);
                    } else
                        base.InitializeItem(sku);
                }

            } else if (base.GetCurrentItem() == sku) {

                if (responseFromServer == 0) {

                    shootedSerials = 0;
                    suggestedSerialnr = null;
                    topUpIteration = false;
                    topUpLocation = new WarehouseLocation();
                    suggestedStorageLocation = new WarehouseLocation();

                    DataExchange.getInstance().setSerialNumbers(null);
                    DataExchange.getInstance().setOptionalserialNumbers(null);
                    DataExchange.getInstance().setBatches(null);

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(sku);
                        {
                        }
                    } else if (!DataFunctions.isNullOrEmpty(data)) {
                        DataExchange.getInstance().setStart(Calendar.getInstance().getTime());
                        DataExchange.getInstance().getCurrentArticle().setBarcode(data);

                        if (DataFunctions.isNullOrEmpty(data)) {
                            base.InitializeItem(sku, "");
                            return;
                        }

                        DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0002);

                        cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");
                        cloudConnector.setPostStep(1);
                        synchronized (cloudConnector) {
                            cloudConnector.execute();
                        }
                    }
                } else if (responseFromServer == 1) {

                    if (SetupFlags.getInstance().getInboundCreateArticle() &&
                            !SetupFlags.getInstance().getUseslogisticunit() &&
                            DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.QUESTION) {

                        DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();
                        //Item does not exist! Do you want to create it?
                        if (dialogMessageBuilder.getYesNoWithExecutionStop(getResources().getString(R.string.warning), getResources().getString(R.string.ID000202), this) == 1) {
                            base.InitializeItem(newsku);
                        } else {
                            base.InitializeItem(sku);
                        }
                        return;
                    }

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();

                        base.InitializeItem(sku);
                    } else {
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
                        else if (DataExchange.getInstance().getDoctype().getMultishootbatches() && DataExchange.getInstance().getQty() > 0) {
                            suggestedQty = DataExchange.getInstance().getQty();
                            qtyStep(sku, DataExchange.getInstance().getQty());
                        } else
                            qtyStep(sku);
                    }
                } else {
                    arrivedQty = DataExchange.getInstance().getPartialQty();
                    base.AddDisplayRow(String.format("%.0f", arrivedQty), getResources().getString(R.string.ID000040), false);

                    //SetVideoRowLabel(localizer.GetString(40)); // Arrived Quantity
                    //SetVideoRowData(DataExchange.GetInstance().PartialQty.ToString("#########0"));

                    requiredQty = DataExchange.getInstance().getRequiredQty();
                    orderRequiredQty = DataExchange.getInstance().getRequiredQty();
                    qty.label = "Quantità" + (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");
                    suggestedSerialnr = DataExchange.getInstance().getSerialNumbers();

                    if (suggestedQty > 0)
                        base.InitializeItem(qty, String.format("%.0f", suggestedQty));
                    else
                        base.InitializeItem(qty, String.format("%.0f", DataExchange.getInstance().getQty()));
                }
                return;

            } else if (base.GetCurrentItem() == batchnr) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    base.InitializeItem(batchnr);
                    return;
                }

                if (DataExchange.getInstance().getDoctype().getMultishootbatches() && !DataFunctions.isNullOrEmpty(data)) {
                    if (DataExchange.getInstance().getQty() == 0)
                        DataExchange.getInstance().setBatches(null);
                    else {
                        String[] batches = DataExchange.getInstance().getBatches().split(Pattern.quote("|"));

                        for (String currentBatch : batches) {
                            if (currentBatch == data) {
                                AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                                alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), getResources().getString(R.string.ID000203), AlertMessageBuilder.Severity.ERROR, this);
                                alertMessageBuilder.Show();
                                base.InitializeItem(batchnr, "");
                                return;
                            }
                        }

                        DataExchange.getInstance().setBatches(DataExchange.getInstance().getBatches() + data + "|");
                        DataExchange.getInstance().setQty(DataExchange.getInstance().getQty() + 1);
                        base.AddDisplayRow(data, base.GetCurrentItem().label, false);
                        base.InitializeItem(batchnr, "");
                        return;
                    }
                } else {

                    if (responseFromServer == 0) {

                        if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();
                            base.InitializeItem(batchnr);
                            return;
                        }

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
                            qtyStep(batchnr);

                        return;
                    } else {
                        arrivedQty = DataExchange.getInstance().getPartialQty();
                        base.AddDisplayRow(Double.toString(arrivedQty), getResources().getString(R.string.ID000040), false);

                        //SetVideoRowLabel(localizer.GetString(40)); // Arrived Quantity
                        //SetVideoRowData(DataExchange.GetInstance().PartialQty.ToString("#########0"));

                        requiredQty = DataExchange.getInstance().getRequiredQty();
                        orderRequiredQty = DataExchange.getInstance().getRequiredQty();
                        qty.label = "Quantità" + (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");
                        suggestedSerialnr = DataExchange.getInstance().getSerialNumbers();

                        if (suggestedQty > 0)
                            base.InitializeItem(qty, Double.toString(suggestedQty));
                        else
                            base.InitializeItem(qty, Double.toString(DataExchange.getInstance().getQty()));
                    }
                }

            } else if (base.GetCurrentItem() == expiry) {

                if (responseFromServer == 0) {

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(expiry);
                        return;
                    }

                    DataExchange.getInstance().setLockedforshelflife(false);
                    DataExchange.getInstance().setExpire(format.parse(data));


                    if (data.equals("  /  /    ")) {
                        if (SetupFlags.getInstance().getVerifybbe()) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), getResources().getString(R.string.ID000142), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();
                            base.InitializeItem(expiry, "");
                            return;
                        }
                    } else {

                        Calendar c = Calendar.getInstance();
                        c.setTime(Calendar.getInstance().getTime());
                        c.add(c.DATE, DataExchange.getInstance().getCurrentArticle().getShelflifein());

                        if (DataExchange.getInstance().getExpire() != new Date() &&
                                (DataExchange.getInstance().getExpire().before(c.getTime()))) {
                            DataExchange.getInstance().setLockedforshelflife(true);
                            // Warning! Expiring goods to stock in locked position.
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog("Warning", getResources().getString(R.string.ID000204), AlertMessageBuilder.Severity.WARNING, this);
                            alertMessageBuilder.Show();
                        }


                        base.AddDisplayRow(data, base.GetCurrentItem().label, false);
                    }
                    if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1)
                        base.InitializeItem(variantid1);
                    else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2)
                        base.InitializeItem(variantid2);
                    else if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3)
                        base.InitializeItem(variantid3);
                    else
                        qtyStep(expiry);
                    //base.InitializeItem(qty);
                } else {
                    arrivedQty = DataExchange.getInstance().getPartialQty();
                    base.AddDisplayRow(Double.toString(arrivedQty), getResources().getString(R.string.ID000040), false);

                    //SetVideoRowLabel(localizer.GetString(40)); // Arrived Quantity
                    //SetVideoRowData(DataExchange.GetInstance().PartialQty.ToString("#########0"));

                    requiredQty = DataExchange.getInstance().getRequiredQty();
                    orderRequiredQty = DataExchange.getInstance().getRequiredQty();
                    qty.label = getResources().getString(R.string.ID000012) + (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");
                    suggestedSerialnr = DataExchange.getInstance().getSerialNumbers();

                    if (suggestedQty > 0)
                        base.InitializeItem(qty, Double.toString(suggestedQty));
                    else
                        base.InitializeItem(qty, Double.toString(DataExchange.getInstance().getQty()));
                }

                return;

            } else if (base.GetCurrentItem() == variantid1) {

                if (responseFromServer == 0) {

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(variantid1);
                        return;
                    }
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
                        qtyStep(variantid1);

                    return;
                } else {
                    arrivedQty = DataExchange.getInstance().getPartialQty();
                    base.AddDisplayRow(Double.toString(arrivedQty), getResources().getString(R.string.ID000040), false);

                    //SetVideoRowLabel(localizer.GetString(40)); // Arrived Quantity
                    //SetVideoRowData(DataExchange.GetInstance().PartialQty.ToString("#########0"));

                    requiredQty = DataExchange.getInstance().getRequiredQty();
                    orderRequiredQty = DataExchange.getInstance().getRequiredQty();
                    qty.label = getResources().getString(R.string.ID000012) + (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");
                    suggestedSerialnr = DataExchange.getInstance().getSerialNumbers();

                    if (suggestedQty > 0)
                        base.InitializeItem(qty, Double.toString(suggestedQty));
                    else
                        base.InitializeItem(qty, Double.toString(DataExchange.getInstance().getQty()));
                }
            } else if (base.GetCurrentItem() == variantid2) {

                if (responseFromServer == 0) {

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(variantid2);
                        return;
                    }
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
                        qtyStep(variantid2);

                    return;
                } else {
                    arrivedQty = DataExchange.getInstance().getPartialQty();
                    base.AddDisplayRow(Double.toString(arrivedQty), getResources().getString(R.string.ID000040), false);

                    //SetVideoRowLabel(localizer.GetString(40)); // Arrived Quantity
                    //SetVideoRowData(DataExchange.GetInstance().PartialQty.ToString("#########0"));

                    requiredQty = DataExchange.getInstance().getRequiredQty();
                    orderRequiredQty = DataExchange.getInstance().getRequiredQty();
                    qty.label = getResources().getString(R.string.ID000012) + (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");
                    suggestedSerialnr = DataExchange.getInstance().getSerialNumbers();

                    if (suggestedQty > 0)
                        base.InitializeItem(qty, Double.toString(suggestedQty));
                    else
                        base.InitializeItem(qty, Double.toString(DataExchange.getInstance().getQty()));
                }
            } else if (base.GetCurrentItem() == variantid3) {

                if (responseFromServer == 0) {

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(variantid3);
                        return;
                    }
                    DataExchange.getInstance().setVariantId3(data);

                    if (!DataFunctions.isNullOrEmpty(data)) {
                        BooleanMessage result = Functions.invalidLenght(data, SetupFlags.getInstance().getLenghtManager(), InputType.Variantid2, this);

                        if (result.getResult() == true) {
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), result.getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();
                            base.InitializeItem(variantid3);
                            return;
                        }

                        base.AddDisplayRow(data, base.GetCurrentItem().label, false);
                    }
                    //base.InitializeItem(qty);
                    qtyStep(variantid3);

                    return;
                } else {
                    arrivedQty = DataExchange.getInstance().getPartialQty();
                    base.AddDisplayRow(Double.toString(arrivedQty), getResources().getString(R.string.ID000040), false);

                    //SetVideoRowLabel(localizer.GetString(40)); // Arrived Quantity
                    //SetVideoRowData(DataExchange.GetInstance().PartialQty.ToString("#########0"));

                    requiredQty = DataExchange.getInstance().getRequiredQty();
                    orderRequiredQty = DataExchange.getInstance().getRequiredQty();
                    qty.label = getResources().getString(R.string.ID000012) + (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");
                    suggestedSerialnr = DataExchange.getInstance().getSerialNumbers();

                    if (suggestedQty > 0)
                        base.InitializeItem(qty, Double.toString(suggestedQty));
                    else
                        base.InitializeItem(qty, Double.toString(DataExchange.getInstance().getQty()));
                }

            } else if (base.GetCurrentItem() == qty) {

                if (responseFromServer == 0) {

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(qty);
                        return;
                    }
                    shootedSerials = 0;
                    DataExchange.getInstance().setSerialNumbers(null);
                    DataExchange.getInstance().setOptionalserialNumbers(null);

                    DataExchange.getInstance().setQty(DataFunctions.readDecimal(data));

                    if (DataExchange.getInstance().getQty() <= 0) return;

                    if ((DataExchange.getInstance().getQty() + arrivedQty) > orderRequiredQty) {
                        if (SetupFlags.getInstance().getReceivemaxdocqty()) {
                            // Max Quantity Allowed:
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.warning), getResources().getString(R.string.ID000126) + " " + (orderRequiredQty - arrivedQty), AlertMessageBuilder.Severity.WARNING, this);
                            alertMessageBuilder.Show();

                            qty.label = getResources().getString(R.string.ID000080) + " " + (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");

                            base.InitializeItem(qty, Double.toString(orderRequiredQty - arrivedQty));
                            return;
                        } else {

                            DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();

                            if (dialogMessageBuilder.getYesNoWithExecutionStop(getResources().getString(R.string.warning), getResources().getString(R.string.ID000076), this) == 0) {
                                qty.label = getResources().getString(R.string.ID000012) + (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");

                                base.InitializeItem(qty, Double.toString(orderRequiredQty - arrivedQty));
                                return;
                            } else {
                                requiredQty = DataExchange.getInstance().getQty();
                            }
                        }
                    }

                    // TopUp Cycle
                    ///////////////////////////////////////////////////////////////////////////////
                    if (topUpIteration && DataExchange.getInstance().getQty() > Math.min(DataExchange.getInstance().getPartialQty(), (requiredQty - partialQty))) {
                        DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();
                        //Quantità superiore alla capacità cella. Confermare?
                        if (dialogMessageBuilder.getYesNoWithExecutionStop(getResources().getString(R.string.warning), getResources().getString(R.string.ID000081), this) == 1) {
                            qty.label = getResources().getString(R.string.ID000012) + (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");

                            base.InitializeItem(qty, Double.toString(orderRequiredQty - arrivedQty));
                            return;
                        }
                    }

                    if (topUpIteration && partialQty + DataExchange.getInstance().getQty() < requiredQty) {
                        if (partialQty < requiredQty) {

                            DataExchange.getInstance().setToLocation(Functions.locationCopy(topUpLocation));
                            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001);
                            DataExchange.getInstance().setStep('W');

                            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");
                            cloudConnector.setPostStep(2);
                            synchronized (cloudConnector) {
                                cloudConnector.execute();
                            }
                        } else {
                            FinalStep();
                            return;
                        }
                    } else if (partialQty == 0) {
                        requiredQty = DataExchange.getInstance().getQty(); // Standard Cycle, no TopUp
                    }

                    if (menuItem.getDefToLocation() != 0)
                        DataExchange.getInstance().getToLocation().setPositionCode(menuItem.getDefToLocation());
                    else
                        DataExchange.getInstance().setToLocation(new WarehouseLocation());

                    // TopUp: set last TopUp Location
                    if (partialQty > 0)
                        DataExchange.getInstance().setToLocation(Functions.locationCopy(topUpLocation)); // Set last topup position

                    if (DataExchange.getInstance().getCurrentArticle().getUsesserialnumber().equals("I") ||
                            DataExchange.getInstance().getCurrentArticle().getUsesserialnumber().equals("A")) {
                        base.InitializeItem(serialnr, DataExchange.getInstance().getSerialUccNumber());
                        return;
                    }

                    if (!Functions.isNullOrEmpty(DataExchange.getInstance().getToLocation()) && DataExchange.getInstance().getToLocation().getPositionCode() != 0) {
                        if (Functions.isNullOrEmpty(DataExchange.getInstance().getPalletcode()) &&
                                Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getPalletcode()) &&
                                !Functions.isNullOrEmpty(DataExchange.getInstance().getUnit()))
                            base.InitializeItem(palletcode);
                        else
                            FinalStep();
                    } else {
                        if (DataExchange.getInstance().getCurrentArticle().getUsequalitycontrol() &&
                                !Functions.isNullOrEmpty(DataExchange.getInstance().getDoctype().getQualityparkingposition().getEntirePosition())) {
                                DataExchange.getInstance().getDoctype().setQualityparkingposition(Functions.locationCopy(suggestedStorageLocation));
                                Presentation.ShowToLocation(base, DataExchange.getInstance().getDoctype().getQualityparkingposition(), true);
                        } else if (SetupFlags.getInstance().getStockonlastlocation() && !DataExchange.getInstance().getLockedforshelflife()) // Search for a Storage Position
                        {
                            DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0011);

                            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");
                            cloudConnector.setPostStep(4);
                            synchronized (cloudConnector) {
                                cloudConnector.execute();
                            }
                            return;
                        } else {
                            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5001);
                            DataExchange.getInstance().setStep('F'); //Find Location

                            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");
                            cloudConnector.setPostStep(5);
                            synchronized (cloudConnector) {
                                cloudConnector.execute();
                            }
                            return;
                        }

                        base.InitializeItem(location);
                    }

                } else if (responseFromServer == 2) {

                    if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getOrder())) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog("Attenzione", DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(barcode, "");
                        return;
                    }

                    partialQty += DataExchange.getInstance().getQty();

                    base.ClearDisplayArea(8, 9);

                    base.AddDisplayRow(Double.toString(arrivedQty + partialQty), getResources().getString(R.string.ID000040), false);

                    DataExchange.getInstance().setQty(requiredQty - partialQty);
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5001);
                    DataExchange.getInstance().setStep('F'); //Find Location
                    DataExchange.getInstance().setToLocation(new WarehouseLocation());

                    cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");
                    cloudConnector.setPostStep(3);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                } else if (responseFromServer == 3) {

                    suggestedStorageLocation = DataExchange.getInstance().getToLocation();
                    topUpLocation = DataExchange.getInstance().getToLocation();

                    Presentation.ShowToLocation(base, DataExchange.getInstance().getToLocation(), false, getResources().getString(R.string.ID000053));

                    base.InitializeItem(location);
                } else if (responseFromServer == 4) {

                    Presentation.showSuggestedLocations(base, DataExchange.getInstance());
                    /*suggestedStorageLocation = DataExchange.getInstance().getToLocation();
                    topUpLocation = DataExchange.getInstance().getToLocation();

                    Presentation.ShowToLocation(base, DataExchange.getInstance().getToLocation(), false, getResources().getString(R.string.ID000053));
*/
                    base.InitializeItem(location);
                } else if (responseFromServer == 5) {

                    suggestedStorageLocation = DataExchange.getInstance().getToLocation();
                    topUpLocation = DataExchange.getInstance().getToLocation();

                    Presentation.ShowToLocation(base, DataExchange.getInstance().getToLocation(), false, getResources().getString(R.string.ID000053));

                    base.InitializeItem(location);
                }
                return;

            } else if (base.GetCurrentItem() == location) {
                if (responseFromServer == 0) {

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(location);
                        return;
                    }
                    if (DataFunctions.isNullOrEmpty(data)) {
                        base.InitializeItem(location);
                        return;
                    }

                    WarehouseLocation readLocation = new WarehouseLocation();

                    if (DataExchange.getInstance().getLockedforshelflife()) {
                        readLocation.setEntirePosition(data);

                        if (readLocation.getEntirePosition() != suggestedStorageLocation.getEntirePosition()) {
                            //Different expire lock position. Cannot continue.
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.warning), getResources().getString(R.string.ID000140), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();

                            base.InitializeItem(location);
                            return;
                        } else
                            DataExchange.getInstance().setToLocation(Functions.locationCopy(suggestedStorageLocation));
                    }

                    if (DataExchange.getInstance().getCurrentArticle().getUsequalitycontrol() &&
                            !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getDoctype().getQualityparkingposition().getEntirePosition())) {
                        readLocation.setEntirePosition(data);

                        if (readLocation.getEntirePosition() != suggestedStorageLocation.getEntirePosition()) {
                            //Different quality control position. Cannot continue.
                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.warning), getResources().getString(R.string.ID000159), AlertMessageBuilder.Severity.ERROR, this);
                            alertMessageBuilder.Show();

                            base.InitializeItem(location);
                            return;
                        } else
                            DataExchange.getInstance().setToLocation(Functions.locationCopy(suggestedStorageLocation));
                    }


                    // TopUp Iteration...
                    if (DataExchange.getInstance().getPartialQty() > 0 && partialQty < requiredQty) {
                        readLocation.setEntirePosition(data);

                        topUpIteration = true;

                        if (readLocation.getEntirePosition() == suggestedStorageLocation.getEntirePosition()) {
                            topUpLocation = Functions.locationCopy(suggestedStorageLocation);

                            if (DataExchange.getInstance().getPartialQty() >= requiredQty) {
                                if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getPalletcode()) &&
                                        DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getPalletcode()) &&
                                        !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getUnit()))
                                    base.InitializeItem(palletcode);
                                else
                                    FinalStep();
                                return;
                            }

                            qty.label = getResources().getString(R.string.ID000077) +
                                    (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" :
                                            "");
                            base.InitializeItem(qty, String.format("%.2f", Math.min(DataExchange.getInstance().getPartialQty(), (requiredQty - partialQty))));
                        } else {

                            DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();
                            //Posizione Diversa. Confermare?
                            if (dialogMessageBuilder.getYesNoWithExecutionStop(getResources().getString(R.string.warning), getResources().getString(R.string.ID000078), this) == 0)
                                base.InitializeItem(location);
                            else {
                                // TopUp: user has read a different storage location...

                                DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5001);
                                DataExchange.getInstance().getToLocation().setEntirePosition(data);
                                DataExchange.getInstance().setStep('U'); //Find Location TopUp Properties

                                cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");
                                cloudConnector.setPostStep(2);
                                synchronized (cloudConnector) {
                                    cloudConnector.execute();
                                }
                                return;
                            }
                        }
                        return;
                    }

                    topUpIteration = false;
                    topUpLocation = new WarehouseLocation();

                    DataExchange.getInstance().setPartialQty(0);
                    DataExchange.getInstance().getToLocation().setEntirePosition(data);
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0004);

                    cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");
                    cloudConnector.setPostStep(3);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                    return;

                } else if (responseFromServer == 2) {

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(location);
                        return;
                    }

                    suggestedStorageLocation = Functions.locationCopy(DataExchange.getInstance().getToLocation());
                    topUpLocation = Functions.locationCopy(DataExchange.getInstance().getToLocation());

                    if (DataExchange.getInstance().getPartialQty() >= requiredQty) {
                        if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getPalletcode()) &&
                                DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getPalletcode()) &&
                                !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getUnit()))
                            base.InitializeItem(palletcode);
                        else
                            FinalStep();

                        return;
                    }
                    qty.label = getResources().getString(R.string.ID000077) +
                            (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getMeasure()) ? "  (" + DataExchange.getInstance().getCurrentArticle().getMeasure() + ")" : "");

                    base.InitializeItem(qty, String.format("%.2f", Math.min(DataExchange.getInstance().getPartialQty(), (requiredQty - partialQty))));
                } else if (responseFromServer == 3) {

                    if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show();
                        base.InitializeItem(location);
                        return;
                    }

                    base.AddDisplayRow(data, location.label, false);

                    if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getPalletcode()) &&
                            DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getPalletcode()) &&
                            !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getUnit()))
                        base.InitializeItem(palletcode);
                    else
                        FinalStep();
                }

            } else if (base.GetCurrentItem() == serialnr) {

            } else if (base.GetCurrentItem() == optionalserialnr) {

            } else if (base.GetCurrentItem() == palletcode) {
                DataExchange.getInstance().setPalletcode(data);

                if (!DataFunctions.isNullOrEmpty(data)) {
                    base.AddDisplayRow(data, palletcode.label, false);
                    FinalStep();
                } else
                    base.InitializeItem(palletcode);

                return;
            }

        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
    }

    private void qtyStep(ActivityItem returnTo) throws Exception {
        qtyStep(returnTo, 0);
    }

    private void qtyStep(ActivityItem returnTo, double suggestedQty) throws Exception {
        DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001);
        DataExchange.getInstance().setStep('L');
        DataExchange.getInstance().setWarehouse(null);

        requiredQty = 0;
        partialQty = 0;

        cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");
        cloudConnector.setPostStep(99);
        synchronized (cloudConnector) {
            cloudConnector.execute();
        }
    }

    private void FinalStep() {
        try {
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001);
            DataExchange.getInstance().setStep('W');
            cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FinalStep()");

            cloudConnector.setPostStep(3);
            synchronized (cloudConnector) {
                cloudConnector.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    public void OnBackAction() {
        try {
            ActivityItem previousItem = base.GetPreviousItem();

            if (previousItem == null)
                finish();
            else
                base.InitializeItem(previousItem);

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

    public void taskCompletionResult(String result, int step) throws Exception {
        if (!cloudConnector.getPostExceuteMethod().equals("com.diastock.app.Inbound.FinalStep()")) {
            Object[] params = {null, result.equals("OK") ? step : 0};
            this.getClass().getMethods()[0].invoke(this, params);
        } else {

            if (result.equals("OK")) {
                if (step == 3) {
                    /*
                       if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                                            AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                                            alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                                            alertMessageBuilder.Show();
                                            base.InitializeItem(eanucc);
                                        }
                    * */

                    if (DataExchange.getInstance().getStep() == 'K' &&
                            DataExchange.getInstance().getDoctype().isPiecesonrfinboundclosing()) {

                        Intent intent = new Intent(getApplicationContext(), InboundClose.class);

                        Bundle b = new Bundle();
                        b.putString("ORDER", order); //Your id
                        b.putString("DOCUMENT", docnr); //Your id

                        intent.putExtras(b);
                        //intent.putExtra("COMPANY", company);
                        startActivity(intent);
                        base.InitializeItem(barcode);
                        return;
                    }

                   /* if (DataExchange.getInstance().getStep() == 'P') {

                        Intent intent = new Intent(getApplicationContext(), InboundClose.class);

                        Bundle b = new Bundle();
                        b.putString("ORDER", order); //Your id
                        b.putString("DOCUMENT", docnr); //Your id

                        intent.putExtras(b);
                        //intent.putExtra("COMPANY", company);
                        startActivity(intent);
                        base.InitializeItem(barcode);
                        return;
                    }*/

                    if (Functions.isNullOrEmpty(DataExchange.getInstance().getOrder())) {
                        Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), DataExchange.getInstance().getMessage(), Snackbar.LENGTH_LONG)
                                .setAction("No action", null);

                        sbw.getView().setBackgroundColor(Color.parseColor("#66a3ff"));
                        sbw.show();
                        base.InitializeItem(barcode);
                        actionMenu.findItem(R.id.menu_item_options).setVisible(true);

                        return;
                    }

                    // Exit TopUp Iteration (if any...)
                    topUpIteration = false;

                    DataExchange.getInstance().setSerialUccNumber(null);

                    if (SetupFlags.getInstance().getUsesmaxibarcode() && DataExchange.getInstance().getSupplier().getUseseanucc())
                        base.InitializeItem(eanucc);
                    else if (SetupFlags.getInstance().getSkuOnLogisticUnit() && SetupFlags.getInstance().getUseslogisticunit())
                        base.InitializeItem(unit); //Logistic Unit is mono-sku
                    else
                        base.InitializeItem(sku);   //Logistic Unit has many sku
                } else {
                    Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), getResources().getString(R.string.operation_successfully_completed), Snackbar.LENGTH_LONG)
                            .setAction("No action", null);

                    sbw.getView().setBackgroundColor(Color.parseColor("#108a1f"));
                    sbw.show();
                    // Re-Initialize and restart...
                    base.ClearAllDisplay();

                    String docnr = null, ordernr = null, supplierCode = null;

                    if (step == 1) {
                        base.AddDisplayRow(DataExchange.getInstance().getOrder() + " / " + DataExchange.getInstance().getDocnr(), getResources().getString(R.string.ID000115), false);
                        base.AddDisplayRow(DataExchange.getInstance().getSupplier().getDescription(), getResources().getString(R.string.ID000014), false);
                        docnr = DataExchange.getInstance().getDocnr();
                        ordernr = DataExchange.getInstance().getOrder();
                        supplierCode = DataExchange.getInstance().getSupplier().getBarcode();
                    }

                    menuItem = UserMenu.getInstance().getMenuItem(position);

                    DataExchange.getInstance().format();
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F1001); // INBOUND
                    DataExchange.getInstance().setLoadCausal(menuItem.getDefLoadCausal());
                    DataExchange.getInstance().getToLocation().setPositionCode(menuItem.getDefToLocation());


                    if (step == 1) {
                        DataExchange.getInstance().setDocnr(docnr);
                        DataExchange.getInstance().setOrder(ordernr);
                        DataExchange.getInstance().setStep('C');
                        Supplier supplier = new Supplier();
                        supplier.setBarcode(supplierCode);

                        DataExchange.getInstance().setSupplier(supplier);


                        if (SetupFlags.getInstance().getUsesmaxibarcode())
                            base.InitializeItem(eanucc);
                        else if (SetupFlags.getInstance().getSkuOnLogisticUnit() && SetupFlags.getInstance().getUseslogisticunit())
                            base.InitializeItem(unit); //Logistic Unit is mono-sku
                        else
                            base.InitializeItem(sku);   //Logistic Unit has many sku
                    } else
                        base.InitializeItem(barcode);
                }
                return;

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

}
