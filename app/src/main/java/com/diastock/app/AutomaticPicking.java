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
import java.util.ArrayList;
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
import static com.diastock.app.InputArea.TYPE_SKU;
import static com.diastock.app.InputArea.TYPE_TEXT;

public class AutomaticPicking extends AppCompatActivity implements TaskDelegate, BaseActivityInterface, BaseFragment.OnFragmentInteractionListener {

    private static final int  PICKING_SPLITTER_INTENT_REQUEST_CODE = 323;
    ActivityItem listid = null;
    ActivityItem fromlocation = null;
    ActivityItem tolocation = null;
    ActivityItem eanucc = null;
    ActivityItem container = null;
    ActivityItem unit = null;
    ActivityItem sku = null;
    ActivityItem decodedsku = null;
    ActivityItem batch = null;
    ActivityItem expire = null;
    ActivityItem variantid1 = null;
    ActivityItem variantid2 = null;
    ActivityItem variantid3 = null;
    ActivityItem qty = null;
    ActivityItem finalunit = null;
    ActivityItem palletcode = null;
    ActivityItem cardboard = null;
    ActivityItem serialnr = null;
    ActivityItem optionalserialnr = null;

    ActivityItem returnTo = null;

    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    DisplayFragment base;
    Menu actionMenu;
    MenuItem menuItem = null;

    CloudConnector cloudConnector = null;

    WarehouseLocation suggestedFromLocation;
    WarehouseLocation suggestedToLocation;
    String suggestedContainer = "";
    String suggestedUnit = "";
    Article suggestedArticle;
    String suggestedBatch = "";
    Date suggestedExpire = DataFunctions.getEmptyDate();
    String suggestedVariantid1 = "";
    String suggestedVariantid2 = "";
    String suggestedVariantid3 = "";
    String suggestedReserved = "";
    double suggestedQty = 0;
    int suggestedSplit = 0;
    int shootedSerials = 0;
    Boolean switchLogUnit = false;
    Boolean switchBatchExpire = false;
    Boolean switchVariantId = false;

    Barcodes barcodes = null;

    WarehouseLocation previousLocation;

    String saveUnit = "";
    String lastListId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_automatic_picking);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        double requiredQty;
        double orderRequiredQty;
        double suggestedQty;
        boolean topUpIteration;
        int shootedSerials = 0;
        String suggestedSerialnr = "";
        WarehouseLocation topUpLocation = null;
        WarehouseLocation suggestedStorageLocation = null;
        int position = -1; // or other values

        String order, docnr, company;

        base = ((DisplayFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().findFragmentById(R.id.display_fragment)); //getSupportFragmentManager().findFragmentById(R.id.display_fragment));

        try {
            base.ClearAllDisplay();
            Bundle params = getIntent().getExtras();

            if (params != null)
                position = params.getInt("menuPosition");

            listid = new ActivityItem(1, getResources().getString(R.string.ID000074), 30, InputArea.TYPE_TEXT, "", false, DataExchange.PROP_LISTID, true);
            fromlocation = new ActivityItem(2, getResources().getString(R.string.ID000017), 30, TYPE_LOCATION, "", false, DataExchange.PROP_FROMLOCATION, true);
            container = new ActivityItem(3, getResources().getString(R.string.ID000004), 30, TYPE_TEXT, "", false, DataExchange.PROP_CONTAINER, true);
            eanucc = new ActivityItem(4, getResources().getString(R.string.ID000059), 100, TYPE_EANUCC, "", false, DataExchange.PROP_EANUCC, true);
            unit = new ActivityItem(5, getResources().getString(R.string.ID000005), 40, TYPE_LOGUNIT, "", false, DataExchange.PROP_UNIT, true);
            sku = new ActivityItem(6, getResources().getString(R.string.ID000003), 30, TYPE_SKU, "", false, DataExchange.PROP_CURRENTARTICLE, true);
            decodedsku = new ActivityItem(7, getResources().getString(R.string.ID000013), 50, TYPE_TEXT, "", true, "", false);
            batch = new ActivityItem(8, getResources().getString(R.string.ID000007), 30, TYPE_TEXT, "", false, DataExchange.PROP_BATCH, true);
            expire = new ActivityItem(9, getResources().getString(R.string.ID000008), 30, TYPE_DATE, "", false, DataExchange.PROP_EXPIRE, false);
            variantid1 = new ActivityItem(10, getResources().getString(R.string.ID000009), 30, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID1, true);
            variantid2 = new ActivityItem(11, getResources().getString(R.string.ID000010), 30, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID2, true);
            variantid3 = new ActivityItem(12, getResources().getString(R.string.ID000011), 30, TYPE_TEXT, "", false, DataExchange.PROP_VARIANTID3, true);
            qty = new ActivityItem(13, getResources().getString(R.string.ID000012), 14, TYPE_QTY, "", false, DataExchange.PROP_QTY, false);
            finalunit = new ActivityItem(14, getResources().getString(R.string.ID000071), 40, TYPE_LOGUNIT, "", false, DataExchange.PROP_FINALUNIT, true);
            palletcode = new ActivityItem(15, getResources().getString(R.string.ID000056), 3, TYPE_TEXT, "", false, DataExchange.PROP_PALLETCODE, true);
            tolocation = new ActivityItem(16, getResources().getString(R.string.ID000006), 30, TYPE_LOCATION, "", false, DataExchange.PROP_TOLOCATION, true);
            serialnr = new ActivityItem(17, getResources().getString(R.string.ID000088), 50, TYPE_SERIAL, "", false, DataExchange.PROP_SERIALNR, true);
            optionalserialnr = new ActivityItem(18, getResources().getString(R.string.ID000133), 50, TYPE_TEXT, "", false, DataExchange.PROP_OPTIONAL_SERIALNR, true);
            cardboard = new ActivityItem(19, getResources().getString(R.string.ID000051), 20, TYPE_TEXT, "", false, DataExchange.PROP_CARDBOARD, true);

            suggestedFromLocation = new WarehouseLocation();
            suggestedToLocation = new WarehouseLocation();
            suggestedArticle = new Article();

            previousLocation = new WarehouseLocation();

            menuItem = UserMenu.getInstance().getMenuItem(position);
            setTitle(menuItem.getFunctionDescription());

            barcodes = new Barcodes();

            DataExchange.getInstance().format();

            DataExchange.getInstance().setStep('P');
            DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003); // AUTOMATIC PICKING
            DataExchange.getInstance().setDownloadCausal(menuItem.getDefDownloadCausal());
            DataExchange.getInstance().setLoadCausal(menuItem.getDefLoadCausal());
            DataExchange.getInstance().setPaperless(menuItem.isPaperless());
            DataExchange.getInstance().getToLocation().setPositionCode(menuItem.getDefToLocation());

            if (!DataExchange.getInstance().getPaperless())
                base.InitializeItem(listid, true);
            else {
                if (!FindFirstMission()) {
                    if (DataExchange.getInstance().getPaperless())
                        base.InitializeItem(fromlocation, true);
                    else
                        base.InitializeItem(listid, true);
                }
            }

            return;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean FindFirstMission() {
        try {
            CopyToSuggestedData();
            ResetCollectedData();
            //base.ClearAllDisplay();

            if (!DataExchange.getInstance().getPaperless()) {
                base.AddDisplayRow(DataExchange.getInstance().getListid(), listid.label, false);
            }
            //InitializeItem(ref fromlocation, suggestedFromLocation.InputPosition);
            base.InitializeItem(fromlocation, true);
            Presentation.ShowFromLocation(base, suggestedFromLocation, true);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void CopyToSuggestedData() {
        try {
            suggestedFromLocation.setEntirePosition(DataExchange.getInstance().getFromLocation().getEntirePosition());
            suggestedFromLocation.setPositionCode(DataExchange.getInstance().getFromLocation().getPositionCode());
            fromlocation.lastValue = suggestedFromLocation.getEntirePosition();

            suggestedToLocation.setEntirePosition(DataExchange.getInstance().getToLocation().getEntirePosition());
            suggestedToLocation.setPositionCode(DataExchange.getInstance().getToLocation().getPositionCode());
            tolocation.lastValue = suggestedToLocation.getEntirePosition();

            suggestedContainer = DataExchange.getInstance().getContainer();
            container.lastValue = suggestedContainer;

            suggestedUnit = DataExchange.getInstance().getUnit();
            unit.lastValue = suggestedUnit;

            suggestedArticle = DataExchange.getInstance().getCurrentArticle();
            sku.lastValue = suggestedArticle.getSku();

            suggestedBatch = DataExchange.getInstance().getBatch();
            batch.lastValue = suggestedBatch;

            if (!DataExchange.getInstance().getExpire().equals((DataFunctions.getEmptyDate()))) {
                suggestedExpire = DataExchange.getInstance().getExpire();
                expire.lastValue = format.format(suggestedExpire);
            }

            suggestedVariantid1 = DataExchange.getInstance().getVariantId1();
            variantid1.lastValue = suggestedVariantid1;

            suggestedVariantid2 = DataExchange.getInstance().getVariantId2();
            variantid2.lastValue = suggestedVariantid2;

            suggestedVariantid3 = DataExchange.getInstance().getVariantId3();
            variantid3.lastValue = suggestedVariantid3;

            suggestedQty = DataExchange.getInstance().getRequiredQty() - DataExchange.getInstance().getPartialQty();
            suggestedFromLocation.setMaxsku(DataExchange.getInstance().getFromLocation().getMaxsku());
            suggestedSplit = DataExchange.getInstance().getRow();

            suggestedReserved = DataExchange.getInstance().getReserved();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CopyFromSuggestedData() {
        try {
            DataExchange.getInstance().getFromLocation().setEntirePosition(suggestedFromLocation.getEntirePosition());
            DataExchange.getInstance().getFromLocation().setPositionCode(suggestedFromLocation.getPositionCode());
            DataExchange.getInstance().getToLocation().setEntirePosition(suggestedToLocation.getEntirePosition());
            DataExchange.getInstance().getToLocation().setPositionCode(suggestedToLocation.getPositionCode());
            DataExchange.getInstance().setContainer(suggestedContainer);
            DataExchange.getInstance().setUnit(suggestedUnit);
            DataExchange.getInstance().setCurrentArticle(suggestedArticle);
            DataExchange.getInstance().setBatch(suggestedBatch);
            DataExchange.getInstance().setExpire(suggestedExpire);
            DataExchange.getInstance().setVariantId1(suggestedVariantid1);
            DataExchange.getInstance().setVariantId2(suggestedVariantid2);
            DataExchange.getInstance().setVariantId3(suggestedVariantid3);
            DataExchange.getInstance().setReserved(suggestedReserved);
            DataExchange.getInstance().setRequiredQty(suggestedQty);
            DataExchange.getInstance().setRow(suggestedSplit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ResetCollectedData() {
        try {
            DataExchange.getInstance().setFromLocation(new WarehouseLocation());
            DataExchange.getInstance().setToLocation(new WarehouseLocation());
            if (menuItem.getDefToLocation() > 0)
                DataExchange.getInstance().getToLocation().setPositionCode(menuItem.getDefToLocation());
            DataExchange.getInstance().setContainer("");
            DataExchange.getInstance().setUnit("");
            DataExchange.getInstance().setCurrentArticle(new Article());
            DataExchange.getInstance().setBatch("");
            DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());
            DataExchange.getInstance().setVariantId1("");
            DataExchange.getInstance().setVariantId2("");
            DataExchange.getInstance().setVariantId3("");
            DataExchange.getInstance().setQty(0); //Qty!!! not RequiredQty!!!
            DataExchange.getInstance().setSerialUccNumber("");
            DataExchange.getInstance().setReserved("");
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

                if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getListid())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AutomaticPicking.this);
                    builder.setTitle("Opzioni");

                    ArrayList<String> choices = new ArrayList<String>();

                    choices.add("Chiudi Lista");
                    choices.add("Scambio Unità Logistica");

                    if (!Functions.isNullOrEmpty(DataExchange.getInstance().getUnit()))
                        choices.add("Chiusura Cartone");


                    builder.setItems((CharSequence[]) (choices.toArray(new String[choices.size()])), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            switch (position) {
                                case 0:
                                    try {

                                        Intent intent = new Intent(getApplicationContext(), MarkShippingPackages.class);

                                        Bundle b = new Bundle();
                                        b.putString("LISTID", DataExchange.getInstance().getListid()); //Your id
                                        b.putString("LISTNR", DataExchange.getInstance().getListnr()); //Your nr

                                        intent.putExtras(b);
                                        //intent.putExtra("COMPANY", company);
                                        startActivity(intent);

                                        base.ClearAllDisplay();

                                        base.InitializeItem(listid);

                                        return;
                                    } catch (Exception e) {
                                        return;
                                    }
                                case 1:
                                    switchLogUnit = true;
                                    break;
                                case 2:
                                    Intent intent = new Intent(getApplicationContext(), EndCardboard.class);
                                    startActivity(intent);
                                    base.InitializeItem(cardboard);
                                    break;

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
    public void AcceptText(String data, int responseFromServer) {
        try {

            if (base.GetCurrentItem() == listid) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    String savedListId = DataExchange.getInstance().getListid();
                    base.InitializeItem(listid, savedListId);
                    DataExchange.getInstance().setListid(savedListId);
                    return;
                } else if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.CLOSED) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.warning), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.WARNING, this);
                    alertMessageBuilder.Show();
                    base.InitializeItem(listid, data, false, false, false, true);
                    DataExchange.getInstance().setListid(data);
                    return;
                }

                if (responseFromServer == 0) {
                    if (!Functions.isNullOrEmpty(data)) {
                        try {
                            Integer.parseInt(data);
                        } catch (NumberFormatException nfe) {
                            base.InitializeItem(listid, true);
                            return;
                        }
                    } else {
                        base.InitializeItem(listid, true);
                        return;
                    }

                    DataExchange.getInstance().setRow(0);
                    DataExchange.getInstance().setGenericInt1(0);
                    DataExchange.getInstance().setGenericInt2(0);
                    DataExchange.getInstance().setListid(data);
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003);
                    DataExchange.getInstance().setStep('P'); // Find a Position

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                    cloudConnector.setPostStep(2);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                    return;
                } else if (responseFromServer == 2) {
                    if (!FindFirstMission()) {
                        base.InitializeItem(listid, data, false, false, false, true);
                        DataExchange.getInstance().setListid(data);
                    }
                }
                return;
            } else if (base.GetCurrentItem() == fromlocation) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    base.InitializeItem(listid, true);
                    return;
                }

                if (responseFromServer == 0) {

                    shootedSerials = 0;

                    DataExchange.getInstance().setSerialNumbers("");
                    DataExchange.getInstance().setOptionalserialNumbers("");

                    if (Functions.isNullOrEmpty(data)) {
                        // Search another mission ...
                        FindNextMission(0);
                        return;
                    } else {
                        //DataExchange.getInstance().setStep('F');
                        DataExchange.getInstance().getFromLocation().setEntirePosition(data);

                        if (!suggestedFromLocation.getEntirePosition().toUpperCase().equals(DataExchange.getInstance().getFromLocation().getEntirePosition().toUpperCase())) {
                            // This is a different location.
                            // Search a mission?

                            DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();

                            if (dialogMessageBuilder.getYesNoWithExecutionStop(
                                    getResources().getString(R.string.warning), getResources().getString(R.string.ID000067), this) == 1) {

                                FindNextMission(1);
                                return;
                            }

                            base.BackToItem(fromlocation);
                            return;
                        }

                        FindNextMission(2);
                        return;
                    }
                } else if (responseFromServer == 1) { //Ho inserito posizione vuota --> Vado alla successiva
                    FindNextMission(3);
                    return;
                } else if (responseFromServer == 2) { //Ho cercato una posizione diversa
                    if (FindNextMission(3)) {
                        WhatObject();
                        return;
                    }
                    base.BackToItem(fromlocation);
                    return;
                } else {
                    if (!FindNextMission(3)) //Ho inserito la posizione suggerita
                        base.BackToItem(fromlocation);
                }
                return;
            } else if (base.GetCurrentItem() == container) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    base.BackToItem(container);
                    return;
                } else if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.NEW) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    base.BackToItem(container);
                    return;
                }

                if (responseFromServer == 0) {

                    DataExchange.getInstance().setStart(Calendar.getInstance().getTime());
                    shootedSerials = 0;
                    DataExchange.getInstance().setSerialNumbers("");
                    DataExchange.getInstance().setOptionalserialNumbers("");

                    if (DataFunctions.isNullOrEmpty(data)) {
                        // Search another mission ...
                        FindNextMission(0);
                        return;
                    }

                    if (!suggestedContainer.toUpperCase().equals(DataExchange.getInstance().getContainer().toUpperCase())) {
                        // This is a different Container.
                        // Search a mission?

                        DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();

                        if (dialogMessageBuilder.getYesNoWithExecutionStop(
                                getResources().getString(R.string.warning), getResources().getString(R.string.ID000069), this) == 1) {

                            FindSimilarMission(1);
                            return;
                        } else {
                            base.BackToItem(container);
                            return;
                        }
                    }
                } else if (responseFromServer == 1) {
                    FindNextMission(3);
                    return;
                } else if (responseFromServer == 2) {
                    if (!FindSimilarMission(responseFromServer)) {
                        base.BackToItem(container);
                        return;
                    } else
                        CopyFromSuggestedData();
                } else if (responseFromServer == 3) {
                    base.AddDisplayRow(suggestedContainer, getResources().getString(R.string.ID000004), false); // Container

                    DataExchange.getInstance().setQty(0); // Move all Container

                    if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getDescription())) {
                        base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), decodedsku.label, false);
                    }

                    base.InitializeItem(tolocation);
                    base.AddDisplayRow("", getResources().getString(R.string.ID000018), false); // End Location
                    Presentation.ShowToLocation(base, suggestedToLocation, false);

                    return;
                }

                DataExchange.getInstance().setContainer(data);
                DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0009);

                cloudConnector = new CloudConnector(this, this, this,
                        Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String, int)");

                cloudConnector.setPostStep(3);
                synchronized (cloudConnector) {
                    cloudConnector.execute();
                }
                return;
            } else if (base.GetCurrentItem() == eanucc) {
                DataExchange.getInstance().setStart(Calendar.getInstance().getTime());
                shootedSerials = 0;
                DataExchange.getInstance().setSerialNumbers("");
                DataExchange.getInstance().setOptionalserialNumbers("");

                if (SetupFlags.getInstance().getUseslogisticunit() && !DataFunctions.isNullOrEmpty(suggestedUnit))
                    base.InitializeItem(unit, DataExchange.getInstance().getUnit(), false, false, false, true);
                else
                    base.InitializeItem(sku, DataExchange.getInstance().getCurrentArticle().getSku(), false, false, false, true);

                if (!DataFunctions.isNullOrEmpty(suggestedUnit))

                    base.AddDisplayRow(suggestedUnit, base.GetCurrentItem().label, false); // Log Unit
                else
                    base.AddDisplayRow(suggestedArticle.getSku(), getResources().getString(R.string.ID000003), false); // Sku

                return;
            } else if (base.GetCurrentItem() == unit) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    base.BackToItem(unit);
                    return;
                }

                if (responseFromServer == 0) {

                    DataExchange.getInstance().setStart(Calendar.getInstance().getTime());
                    shootedSerials = 0;
                    DataExchange.getInstance().setSerialNumbers("");
                    DataExchange.getInstance().setOptionalserialNumbers("");

                    if (Functions.isNullOrEmpty(data)) {
                        FindNextMission(0);
                        return;
                    }

                    if (DataExchange.getInstance().getDoctype().getReadhplogunit())
                        DataExchange.getInstance().setUnit(barcodes.getHPSerianNr(data));
                    else
                        DataExchange.getInstance().setUnit(data);

                    if (switchLogUnit) {
                        DataExchange.getInstance().setSwitchedUnit(suggestedUnit);
                        DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003);
                        DataExchange.getInstance().setStep('S'); //Switch

                        cloudConnector = new CloudConnector(this, this, this,
                                Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String, int)");

                        cloudConnector.setPostStep(2);
                        synchronized (cloudConnector) {
                            cloudConnector.execute();
                        }
                        return;
                    } else if (!suggestedUnit.toUpperCase().equals(DataExchange.getInstance().getUnit().toUpperCase())) {
                        // This is a different Logistic Unit.u
                        // Search a mission?

                        previousLocation = new WarehouseLocation();
                        previousLocation.setEntirePosition(suggestedFromLocation.getEntirePosition());

                        DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003);

                        if (Functions.isNullOrEmpty(DataExchange.getInstance().getFromLocation().getEntirePosition()))
                            DataExchange.getInstance().setStep('P');
                        else
                            DataExchange.getInstance().setStep('R');

                        cloudConnector = new CloudConnector(this, this, this,
                                Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                        cloudConnector.setPostStep(3);
                        synchronized (cloudConnector) {
                            cloudConnector.execute();
                        }
                        return;
                    }

                } else if (responseFromServer == 1) {
                    FindNextMission(3);
                    return;
                } else if (responseFromServer == 2) {

                    DataExchange.getInstance().setSwitchedBatch(suggestedBatch);
                    DataExchange.getInstance().setSwitchedExpire(suggestedExpire);
                    DataExchange.getInstance().setSwitchedVariant1(suggestedVariantid1);
                    DataExchange.getInstance().setSwitchedVariant2(suggestedVariantid2);
                    DataExchange.getInstance().setSwitchedVariant3(suggestedVariantid3);
                    DataExchange.getInstance().setReserved(suggestedReserved);
                } else if (responseFromServer == 3) {
                    if (!FindSimilarMission(responseFromServer)) {
                        base.BackToItem(unit);
                        return;
                    } else
                        CopyFromSuggestedData();

                } else if (responseFromServer == 4) {
                    base.AddDisplayRow(DataExchange.getInstance().getUnit(), getResources().getString(R.string.ID000005), false); // Log Unit

                    if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku())) {
                        base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getSku(), sku.label, false);
                        base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), decodedsku.label, false);

                        if (!DataFunctions.isNullOrEmpty(suggestedBatch)) {
                            base.AddDisplayRow(suggestedBatch, getResources().getString(R.string.ID000007), false);
                        }

                        if (!suggestedExpire.equals(DataFunctions.getEmptyDate())) {
                            base.AddDisplayRow(String.format("dd/MM/yy", suggestedExpire), getResources().getString(R.string.ID000008), false);
                        }

                        if (!DataFunctions.isNullOrEmpty(suggestedVariantid1)) {
                            base.AddDisplayRow(suggestedVariantid1, variantid1.label, false);
                        }

                        if (!DataFunctions.isNullOrEmpty(suggestedVariantid2)) {
                            base.AddDisplayRow(suggestedVariantid2, variantid2.label, false);
                        }

                        if (!DataFunctions.isNullOrEmpty(suggestedVariantid3)) {
                            base.AddDisplayRow(suggestedVariantid3, variantid3.label, false);
                        }

                        if (DataExchange.getInstance().getDoctype().getRfsinglepiecedirectpick() && suggestedQty == 1) {
                            DataExchange.getInstance().setQty(1);
                            base.AddDisplayRow("1", qty.label, false);

                            if (DataExchange.getInstance().getCurrentArticle().getUsesserialnumber().equals("O") ||
                                    DataExchange.getInstance().getCurrentArticle().getUsesserialnumber().equals("A"))
                                base.InitializeItem(serialnr, DataExchange.getInstance().getSerialUccNumber(), false, false, false, true);
                            else if (DataExchange.getInstance().getDoctype().getUsescardboard())
                                base.InitializeItem(cardboard, true);
                            else {
                                returnTo = qty;
                                FinalStep();
                            }

                            return;
                        } else {
                            qty.label = getResources().getString(R.string.ID000012) + (!DataFunctions.isNullOrEmpty(suggestedArticle.getMeasure()) ? "  (" + suggestedArticle.getMeasure() + ")" : "");
                            base.InitializeItem(qty, String.format("%.0f", suggestedQty), false, false, false, true);
                        }
                    } else {
                        base.InitializeItem(sku, true);
                        base.AddDisplayRow(suggestedArticle.getSku(), getResources().getString(R.string.ID000003), false); // Sku
                    }
                    return;
                }

                DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0001);

                cloudConnector = new CloudConnector(this, this, this,
                        Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String, int)");

                cloudConnector.setPostStep(4);
                synchronized (cloudConnector) {
                    cloudConnector.execute();
                }
                return;
            } else if (base.GetCurrentItem() == sku) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    base.BackToItem(sku);
                    return;
                }

                if (responseFromServer == 0) {
                    ConfirmSku(data, responseFromServer);
                    return;
                } else if (responseFromServer == 1) {
                    ConfirmSku(data, responseFromServer);
                    return;
                } else if (responseFromServer == 2) {
                    if (!FindSimilarMission(responseFromServer)) {
                        CopyFromSuggestedData();
                    }
                    base.InitializeItem(sku, true);
                    base.AddDisplayRow(suggestedArticle.getSku(), getResources().getString(R.string.ID000003), false); // Sku
                    return;
                }
                return;
            } else if (base.GetCurrentItem() == batch) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    return;
                }

                if (responseFromServer == 0) {
                    DataExchange.getInstance().setBatch(data);

                    if (!DataFunctions.isNullOrEmpty(data)) {
                        base.AddDisplayRow(data, batch.label, false);
                    }

                    if (!DataFunctions.isNullOrEmpty(suggestedBatch) && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getBatch()) &&
                            suggestedBatch.toUpperCase() != DataExchange.getInstance().getBatch().toUpperCase()) {
                        // This is a different batch.
                        // Search a mission
                        FindSimilarMission(1);
                        return;
                    }
                } else {
                    if (!FindSimilarMission(responseFromServer))
                        CopyFromSuggestedData();

                    if (!switchBatchExpire) {
                        base.InitializeItem(batch, true);
                        base.AddDisplayRow(suggestedBatch, batch.label, false);
                        return;
                    }
                }

                WhatAttribute(batch);

                return;
            } else if (base.GetCurrentItem() == expire) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    return;
                }

                if (responseFromServer == 0) {
                    DataExchange.getInstance().tryParseExpire(data);

                    if (!DataFunctions.isNullOrEmpty(data)) {
                        base.AddDisplayRow(data, expire.label, false);
                    }

                    if (!suggestedExpire.equals((DataFunctions.getEmptyDate())) && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getExpire()) &&
                            suggestedExpire.compareTo(DataExchange.getInstance().getExpire()) != 0) {
                        // This is a different expire.
                        // Search a mission
                        FindSimilarMission(1);
                        return;
                    }
                } else {
                    if (!FindSimilarMission(responseFromServer))
                        CopyFromSuggestedData();

                    if (!switchBatchExpire) {
                        base.InitializeItem(expire, true);
                        base.AddDisplayRow(String.format("dd/MM/yy", suggestedExpire), expire.label, false);
                        return;
                    }
                }
                WhatAttribute(expire);

                return;
            } else if (base.GetCurrentItem() == variantid1) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    return;
                }

                if (responseFromServer == 0) {
                    DataExchange.getInstance().setVariantId1(data);

                    if (!DataFunctions.isNullOrEmpty(data)) {
                        base.AddDisplayRow(data, variantid1.label, false);
                    }

                    if (!DataFunctions.isNullOrEmpty(suggestedVariantid1) && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId1()) &&
                            !suggestedVariantid1.toUpperCase().equals(DataExchange.getInstance().getVariantId1().toUpperCase())) {
                        // This is a different variantid1.
                        // Search a mission
                        FindSimilarMission(1);
                        return;
                    }
                } else {
                    if (!FindSimilarMission(responseFromServer)) {
                        CopyFromSuggestedData();
                    }

                    if (!switchBatchExpire) {
                        base.InitializeItem(variantid1, true);
                        base.AddDisplayRow(suggestedVariantid1, variantid1.label, false);
                        return;
                    }
                }

                WhatAttribute(variantid1);

                return;
            } else if (base.GetCurrentItem() == variantid2) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    return;
                }

                if (responseFromServer == 0) {
                    DataExchange.getInstance().setVariantId2(data);

                    if (!DataFunctions.isNullOrEmpty(data)) {
                        base.AddDisplayRow(data, variantid2.label, false);
                    }

                    if (!DataFunctions.isNullOrEmpty(suggestedVariantid2) &&
                            !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId2()) && suggestedVariantid2.toUpperCase().equals(DataExchange.getInstance().getVariantId2().toUpperCase())) {
                        // This is a different variantid2.
                        // Search a mission
                        FindSimilarMission(1);
                        return;
                    }
                } else {
                    if (!FindSimilarMission(responseFromServer))
                        CopyFromSuggestedData();

                    base.InitializeItem(variantid2, true);
                    base.AddDisplayRow(suggestedVariantid2, variantid2.label, false);
                    return;
                }

                WhatAttribute(variantid2);

                return;
            } else if (base.GetCurrentItem() == variantid3) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    return;
                }

                if (responseFromServer == 0) {
                    DataExchange.getInstance().setVariantId3(data);

                    if (!DataFunctions.isNullOrEmpty(data)) {
                        base.AddDisplayRow(data, variantid3.label, false);
                    }

                    if (!DataFunctions.isNullOrEmpty(suggestedVariantid3) && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId3()) &&
                            !suggestedVariantid3.toUpperCase().equals(DataExchange.getInstance().getVariantId3().toUpperCase())) {
                        // This is a different variantid3.
                        // Search a mission
                        FindSimilarMission(1);
                        return;
                    }
                } else {
                    if (!FindSimilarMission(responseFromServer))
                        CopyFromSuggestedData();

                    base.InitializeItem(variantid3, true);
                    base.AddDisplayRow(suggestedVariantid3, variantid3.label, false);
                    return;
                }

                qty.label = getResources().getString(R.string.ID000012) + (!DataFunctions.isNullOrEmpty(suggestedArticle.getMeasure()) ? "  (" + suggestedArticle.getMeasure() + ")" : "");
                base.InitializeItem(qty, suggestedQty > 0 ? String.format("%.0f", suggestedQty) : String.format("#########0.00", suggestedQty), false, false, false, true);

                return;
            } else if (base.GetCurrentItem() == qty) {
                shootedSerials = 0;
                DataExchange.getInstance().setSerialNumbers("");
                DataExchange.getInstance().setOptionalserialNumbers("");

                DataExchange.getInstance().setQty(DataFunctions.readDecimal(data));

                if (DataExchange.getInstance().getQty() <= 0) {
                    qty.label = getResources().getString(R.string.ID000012) + (!DataFunctions.isNullOrEmpty(suggestedArticle.getMeasure()) ? "  (" + suggestedArticle.getMeasure() + ")" : "");
                    base.InitializeItem(qty, String.format("%.0f", suggestedQty), false, false, false, true);
                    return;
                }

                if (DataExchange.getInstance().getQty() > suggestedQty) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog("Warning",
                            getResources().getString(R.string.ID000127) + ": " + String.format("%.0f", suggestedQty), AlertMessageBuilder.Severity.WARNING, this);
                    alertMessageBuilder.Show();

                    qty.label = getResources().getString(R.string.ID000012) + (!DataFunctions.isNullOrEmpty(suggestedArticle.getMeasure()) ? "  (" + suggestedArticle.getMeasure() + ")" : "");
                    base.InitializeItem(qty, String.format("%.0f", suggestedQty), false, false, false, true);
                    return;
                }

                base.AddDisplayRow(data, qty.label, false);

                if (DataExchange.getInstance().getCurrentArticle().getUsesserialnumber().equals("O") ||
                        DataExchange.getInstance().getCurrentArticle().getUsesserialnumber().equals("A"))
                    base.InitializeItem(serialnr, DataExchange.getInstance().getSerialUccNumber(), false, false, false, true);
                else if (DataExchange.getInstance().getDoctype().getUsescardboard())
                    base.InitializeItem(cardboard, true);
                else {
                    returnTo = qty;
                    FinalStep();
                }

                return;
            } else if (base.GetCurrentItem() == serialnr) {
                if (!DataFunctions.isNullOrEmpty(data)) {
                    if (shootedSerials == 0) {
                        DataExchange.getInstance().setSerialNumbers("");
                        DataExchange.getInstance().setOptionalserialNumbers("");
                    } else {
                        String[] serials = DataExchange.getInstance().getSerialNumbers().split(Pattern.quote("|"));

                        for (String serial : serials) {
                            if (serial.equals(data)) {
                                AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                                alertMessageBuilder.BuildDialog("Warning",
                                        getResources().getString(R.string.ID000095), AlertMessageBuilder.Severity.WARNING, this);
                                alertMessageBuilder.Show();

                                base.InitializeItem(serialnr, "", false, false, false, true);
                                return;
                            }
                        }
                    }

                    DataExchange.getInstance().setSerialNumbers(DataExchange.getInstance().getSerialNumbers() + data + "|");

                    //base.ClearDisplayArea(base.GetCurrentItem().row + 1, base.GetCurrentItem().row + 2);

                    base.AddDisplayRow(data, getResources().getString(R.string.ID000096), false);

                    shootedSerials++;
                    if (shootedSerials < DataExchange.getInstance().getQty())
                        if (shootedSerials == 0)
                            base.InitializeItem(serialnr, "", false, false, false, true);
                        else {
                            if (DataExchange.getInstance().getCurrentArticle().getUsesserialnumber().equals("O") &&
                                    DataExchange.getInstance().getCurrentArticle().getUsesoptionalserialnumber())
                                base.InitializeItem(optionalserialnr, "", false, true, false, true);
                            else
                                base.InitializeItem(serialnr, "", false, true, false, true);
                        }
                    else {
                        if (DataExchange.getInstance().getCurrentArticle().getUsesoptionalserialnumber())
                            base.InitializeItem(optionalserialnr, "", false, true, false, true);
                        else {
                            if (DataExchange.getInstance().getDoctype().getUsescardboard())
                                base.InitializeItem(cardboard, true);
                            else {
                                returnTo = serialnr;
                                FinalStep();
                            }
                        }
                    }
                } else
                    base.BackToItem(serialnr);

                return;
            } else if (base.GetCurrentItem() == optionalserialnr) {
                if (!DataFunctions.isNullOrEmpty(data)) {
                    if (shootedSerials == 1) {
                        DataExchange.getInstance().setOptionalserialNumbers("");
                    } else {
                        String[] optionalserials = DataExchange.getInstance().getOptionalserialNumbers().split(Pattern.quote("|"));

                        for (String serial : optionalserials) {
                            if (serial.equals(data)) {
                                AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                                alertMessageBuilder.BuildDialog("Warning",
                                        getResources().getString(R.string.ID000095), AlertMessageBuilder.Severity.WARNING, this);
                                alertMessageBuilder.Show();

                                base.InitializeItem(optionalserialnr, "", false, false, false, true);

                                return;
                            }
                        }
                    }

                    DataExchange.getInstance().setOptionalserialNumbers(DataExchange.getInstance().getOptionalserialNumbers() + data + "|");

                    //SetVideoRowLabel(localizer.GetString(96));
                    //ClearArea(GetCurrentItem().row + 1, GetCurrentItem().row + 2);
                    //SetVideoRowData(data);
                    //shootedSerials++;
                    if (shootedSerials < DataExchange.getInstance().getQty())
                        if (shootedSerials == 1)
                            base.InitializeItem(serialnr, "", false, false, false, true);
                        else
                            base.InitializeItem(serialnr, "", false, true, false, true);
                    else {
                        if (DataExchange.getInstance().getDoctype().getUsescardboard())
                            base.InitializeItem(cardboard, true);
                        else {
                            returnTo = serialnr;
                            FinalStep();
                        }
                    }
                } else
                    base.BackToItem(optionalserialnr);

                return;
            } else if (base.GetCurrentItem() == cardboard) {
                DataExchange.getInstance().setCardboard(data);

                base.AddDisplayRow(data, cardboard.label, false);

                returnTo = cardboard;
                FinalStep();
                return;
            } else if (base.GetCurrentItem() == finalunit) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    base.InitializeItem(finalunit, true);
                    return;
                }

                if (responseFromServer == 0) {

                    if (DataFunctions.isNullOrEmpty(data)) {
                        base.InitializeItem(tolocation, true);
                        return;
                    }

                    DataExchange.getInstance().setFinalUnit(data);
                    saveUnit = DataExchange.getInstance().getFinalUnit();
                    DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0010);

                    cloudConnector = new CloudConnector(this, this, this,
                            Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String, int)");

                    cloudConnector.setPostStep(1);
                    synchronized (cloudConnector) {
                        cloudConnector.execute();
                    }
                    return;
                } else {
                    base.AddDisplayRow(data, finalunit.label, false);

                    if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getPalletcode()))
                        base.InitializeItem(palletcode, true);
                    else {
                        base.AddDisplayRow("", getResources().getString(R.string.ID000018), false); // End Location
                        Presentation.ShowToLocation(base, suggestedToLocation, false);
                        base.InitializeItem(tolocation, true);
                    }
                }

                DataExchange.getInstance().setFinalUnit(saveUnit);

                return;
            } else if (base.GetCurrentItem() == palletcode) {

                DataExchange.getInstance().setPalletcode(data);

                if (!DataFunctions.isNullOrEmpty(data)) {
                    base.AddDisplayRow(data, palletcode.label, false);
                }

                base.AddDisplayRow("", getResources().getString(R.string.ID000018), false); // End Location
                Presentation.ShowToLocation(base, suggestedToLocation, false);
                base.InitializeItem(tolocation, true);

                return;
            } else if (base.GetCurrentItem() == tolocation) {

                if (DataExchange.getInstance().getMessagetype() == DataExchange.MessageType.ERROR) {
                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                    alertMessageBuilder.Show();
                    base.BackToItem(tolocation);
                    return;
                }

                if (responseFromServer == 0) {

                    if (DataFunctions.isNullOrEmpty(data)) {
                        base.BackToItem(tolocation);
                        return;
                    }

                    DataExchange.getInstance().getToLocation().setEntirePosition(data);

                    if (!Functions.isNullOrEmpty(suggestedToLocation.getEntirePosition()) &&
                            !suggestedToLocation.getEntirePosition().toUpperCase().equals(DataExchange.getInstance().getToLocation().getEntirePosition().toUpperCase())) {
                        // This is a different location.
                        // Confirm ?

                        DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();

                        if (dialogMessageBuilder.getYesNoWithExecutionStop(
                                getResources().getString(R.string.warning), getResources().getString(R.string.ID000068), this) == 1) {

                            DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0004);

                            cloudConnector = new CloudConnector(this, this, this,
                                    Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                            cloudConnector.setPostStep(1);
                            synchronized (cloudConnector) {
                                cloudConnector.execute();
                            }
                            return;
                        } else {
                            base.BackToItem(tolocation);
                            return;
                        }

                    }
                }

                returnTo = tolocation;
                FinalStep();

                return;
            }
        } catch (
                Exception e) {
            Log.e("", e.getMessage());
        }

    }

    public Boolean FindNextMission(int responseFromServer) {
        try {
            if (responseFromServer < 3) {
                previousLocation = new WarehouseLocation();
                previousLocation.setEntirePosition(suggestedFromLocation.getEntirePosition());

                DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003);

                if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getFromLocation().getEntirePosition()))
                    DataExchange.getInstance().setStep('P');
                else
                    DataExchange.getInstance().setStep('F');

                //cloudConnector = new CloudConnector(this, this, this,
                //        Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                cloudConnector = new CloudConnector(this, this, this,
                        Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FindNextMission(int)");

                cloudConnector.setPostStep(responseFromServer + 1);
                synchronized (cloudConnector) {
                    cloudConnector.execute();
                }
                return false;
            } else {
                CopyToSuggestedData();
                ResetCollectedData();

                base.ClearDisplayArea(listid.row, cardboard.row);

                if (!DataExchange.getInstance().getPaperless())
                    base.AddDisplayRow(DataExchange.getInstance().getListid(), listid.label, false);

                if (previousLocation.getEntirePosition().toUpperCase().equals(suggestedFromLocation.getEntirePosition().toUpperCase())) {
                    base.ResetFields(fromlocation.sequence + 1, base.itemsList);
                    Presentation.ShowFromLocation(base, suggestedFromLocation, true);
                    WhatObject();
                } else {
                    base.InitializeItem(fromlocation, true);
                    Presentation.ShowFromLocation(base, suggestedFromLocation, true);
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean FinalStep() {
        try {
            DataExchange.getInstance().setReserved(suggestedReserved);

            if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getLoadCausal()) && suggestedToLocation.getPositionCode() == 0) {
                base.AddDisplayRow("", getResources().getString(R.string.ID000018), false); // End Location
                Presentation.ShowToLocation(base, suggestedToLocation, false);
                base.InitializeItem(tolocation, true);
                return true;
            } else {
                if (suggestedToLocation.getPositionCode() > 0)
                    DataExchange.getInstance().getToLocation().setPositionCode(suggestedToLocation.getPositionCode());

                DataExchange.getInstance().setStep('W');
                DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003);
                lastListId = DataExchange.getInstance().getListid();

                cloudConnector = new CloudConnector(this, this, this, Thread.currentThread().getStackTrace()[2].getClassName() + "." + "FinalStep()");

                cloudConnector.setPostStep(3);
                synchronized (cloudConnector) {
                    cloudConnector.execute();
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void WhatObject() {
        try {
            switchLogUnit = false;

            if (!Functions.isNullOrEmpty(suggestedContainer) && Functions.isNullOrEmpty(DataExchange.getInstance().getContainer())) {
                base.InitializeItem(container, suggestedContainer, false, false, false, true);
                base.AddDisplayRow(suggestedContainer, getResources().getString(R.string.ID000004), false); // Container
            } else if (SetupFlags.getInstance().getUsesmaxibarcode() && Functions.isNullOrEmpty(DataExchange.getInstance().getUnit()) && (!Functions.isNullOrEmpty(suggestedUnit) || !Functions.isNullOrEmpty(suggestedArticle.getSku()))) {
                DataExchange.getInstance().getFromLocation().setEntirePosition(suggestedFromLocation.getEntirePosition());
                DataExchange.getInstance().setContainer(suggestedContainer);
                base.InitializeItem(eanucc, true);
                if (!Functions.isNullOrEmpty(suggestedUnit)) {
                    base.AddDisplayRow(suggestedUnit, getResources().getString(R.string.ID000005), false); // Log Unit
                } else {
                    base.AddDisplayRow(suggestedArticle.getSku(), getResources().getString(R.string.ID000003), false); // Sku
                }
            } else if (!Functions.isNullOrEmpty(suggestedUnit) && Functions.isNullOrEmpty(DataExchange.getInstance().getUnit())) {
                DataExchange.getInstance().getFromLocation().setEntirePosition(suggestedFromLocation.getEntirePosition());
                DataExchange.getInstance().setContainer(suggestedContainer);
                base.InitializeItem(unit, true);
                base.AddDisplayRow(suggestedUnit, getResources().getString(R.string.ID000005), false); // Log Unit
            } else if (!Functions.isNullOrEmpty(suggestedArticle.getSku()) && Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku())) {
                DataExchange.getInstance().getFromLocation().setEntirePosition(suggestedFromLocation.getEntirePosition());
                DataExchange.getInstance().setContainer(suggestedContainer);
                DataExchange.getInstance().setUnit(suggestedUnit);

                if (suggestedFromLocation.getMaxsku() == 1) {
                    DataExchange.getInstance().setCurrentArticle(suggestedArticle);
                    ConfirmSku(suggestedArticle.getSku(), 0);
                } else {
                    base.InitializeItem(sku, true);
                    base.AddDisplayRow(suggestedArticle.getSku(), getResources().getString(R.string.ID000003), false); // Sku
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ConfirmSku(String data, int responseFromServer) {
        try {
            if (responseFromServer == 0) {

                DataExchange.getInstance().setStart(Calendar.getInstance().getTime());
                DataExchange.getInstance().getCurrentArticle().setBarcode(data);

                if (Functions.isNullOrEmpty(data)) {
                    base.BackToItem(sku);
                    return;
                }

                DataExchange.getInstance().setFunctionName(DataExchange.Operations.I0002);

                cloudConnector = new CloudConnector(this, this, this,
                        Thread.currentThread().getStackTrace()[2].getClassName() + "." + "ConfirmSku(java.lang.String,int)");

                cloudConnector.setPostStep(responseFromServer + 1);
                synchronized (cloudConnector) {
                    cloudConnector.execute();
                }
                return;
            } else if (responseFromServer == 1) {
                if (!suggestedArticle.getSku().toUpperCase().equals(DataExchange.getInstance().getCurrentArticle().getSku().toUpperCase()) &&
                        !suggestedArticle.getBarcode().toUpperCase().equals(DataExchange.getInstance().getCurrentArticle().getBarcode().toUpperCase())) {
                    // This is a different sku.
                    // Search a mission

                    FindSimilarMission(responseFromServer);
                    return;
                }

                base.AddDisplayRow(DataExchange.getInstance().getCurrentArticle().getDescription(), decodedsku.label, false); // Sku
                WhatAttribute(sku);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void WhatAttribute(ActivityItem fromItem) {
        try {
            switchBatchExpire = false;
            switchVariantId = false;

            if (!DataFunctions.isNullOrEmpty(suggestedBatch) && fromItem.sequence < batch.sequence) {
                base.InitializeItem(batch, true);
                base.AddDisplayRow(suggestedBatch, getResources().getString(R.string.ID000007), false);
            } else if ((!suggestedExpire.equals((DataFunctions.getEmptyDate()))) && fromItem.sequence < expire.sequence) {
                base.InitializeItem(expire, true);
                if (!suggestedExpire.equals((DataFunctions.getEmptyDate()))) {
                    base.AddDisplayRow(String.format("dd/MM/yy", suggestedExpire), getResources().getString(R.string.ID000008), false);
                }
            } else if (!DataFunctions.isNullOrEmpty(suggestedVariantid1) && fromItem.sequence < variantid1.sequence) {
                base.InitializeItem(variantid1, true);
                base.AddDisplayRow(suggestedVariantid1, variantid1.label, false);
            }
            //else if (DataExchange.GetInstance().CurrentArticle.Usesvariant >= 2 && fromItem.sequence < variantid2.sequence)
            else if (!DataFunctions.isNullOrEmpty(suggestedVariantid2) && fromItem.sequence < variantid2.sequence) {
                base.InitializeItem(variantid2, true);
                base.AddDisplayRow(suggestedVariantid2, variantid2.label, false);
            }
            //else if (DataExchange.GetInstance().CurrentArticle.Usesvariant == 3 && fromItem.sequence < variantid3.sequence)
            else if (!DataFunctions.isNullOrEmpty(suggestedVariantid3) && fromItem.sequence < variantid3.sequence) {
                base.InitializeItem(variantid3, true);
                base.AddDisplayRow(suggestedVariantid3, variantid3.label, false);
            }
            else if (DataExchange.getInstance().getDoctype().isSplitpickingwithcardboards()) {

                // SPLIT TO ORDERS
                try {
                    Intent intent = new Intent(getApplicationContext(), PickingSplitter.class);
                    startActivityForResult(intent, PICKING_SPLITTER_INTENT_REQUEST_CODE);
                    return;
                } catch (Exception e) {
                    return;
                }

            } else {
                qty.label = getResources().getString(R.string.ID000012) + (!DataFunctions.isNullOrEmpty(suggestedArticle.getMeasure()) ? "  (" +
                        suggestedArticle.getMeasure() + ")" : "");
                base.InitializeItem(qty, String.format("%.0f", suggestedQty), false, false, false, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean FindSimilarMission(int responseFromServer) {
        try {
            if (responseFromServer < 2) {
                previousLocation = new WarehouseLocation();
                previousLocation.setEntirePosition(suggestedFromLocation.getEntirePosition());

                DataExchange.getInstance().setFunctionName(DataExchange.Operations.F5003);

                if (Functions.isNullOrEmpty(DataExchange.getInstance().getFromLocation().getEntirePosition()))
                    DataExchange.getInstance().setStep('P');
                else
                    DataExchange.getInstance().setStep('R');

                cloudConnector = new CloudConnector(this, this, this,
                        Thread.currentThread().getStackTrace()[2].getClassName() + "." + "AcceptText(java.lang.String,int)");

                cloudConnector.setPostStep(responseFromServer + 1);
                synchronized (cloudConnector) {
                    cloudConnector.execute();
                }
                return false;
            } else {
                if (!Functions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku())) {
                    CopyToSuggestedData();
                    return true;
                } else {
                    DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();

                    if (base.GetCurrentItem() == batch && menuItem.getBatchexpireswitch() && !DataFunctions.isNullOrEmpty(suggestedBatch)) {

                        if (dialogMessageBuilder.getYesNoWithExecutionStop(
                                getResources().getString(R.string.warning), getResources().getString(R.string.ID000138) + " " + suggestedBatch + " " +
                                        getResources().getString(R.string.ID000137) + DataExchange.getInstance().getBatch() + " ?", this) == 1) {

                            switchBatchExpire = true;
                            DataExchange.getInstance().setSwitchedBatch(suggestedBatch);
                            return true;
                        } else {
                            switchBatchExpire = false;
                            return false;
                        }
                    } else if (base.GetCurrentItem() == expire && menuItem.getBatchexpireswitch() && !suggestedExpire.equals((DataFunctions.getEmptyDate())) && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getExpire())) {

                        if (dialogMessageBuilder.getYesNoWithExecutionStop(
                                getResources().getString(R.string.warning), getResources().getString(R.string.ID000138) + " " + String.format("dd/MM/yy", suggestedExpire) +
                                        getResources().getString(R.string.ID000137) + " " + String.format("dd/MM/yy", DataExchange.getInstance().getExpire()) + " ?", this) == 1) {
                            switchBatchExpire = true;
                            DataExchange.getInstance().setSwitchedExpire(suggestedExpire);
                            return true;
                        } else {
                            switchBatchExpire = false;
                            return false;
                        }
                    } else {

                        AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                        alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                        alertMessageBuilder.Show(); //No Mission With These Features
                        return false;
                    }
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICKING_SPLITTER_INTENT_REQUEST_CODE)
        {
            // JUMP FROM PICKING SPLITTER TO NEXT MISSION
            try {
                if (!DataFunctions.isNullOrEmpty(DataExchange.getInstance().getListid()))
                    FindNextMission(2);
                else
                    base.InitializeItem(listid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
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
    }

    @Override
    public void OnBackAction() {
        try {

            if ((base.GetCurrentItem() == fromlocation && DataExchange.getInstance().getPaperless()) || base.GetCurrentItem() == listid)
                finish();
            else {
                ActivityItem previousItem = base.GetPreviousItem();

                if (previousItem == null)
                    finish();
                else {
                    base.BackToItem(previousItem);

                    if (previousItem == serialnr) {
                        DialogMessageBuilder dialogMessageBuilder = new DialogMessageBuilder();

                        // Undo Serials Numbers?
                        if (dialogMessageBuilder.getYesNoWithExecutionStop(
                                getResources().getString(R.string.warning), getResources().getString(R.string.ID000134), this) == 1) {

                            shootedSerials = 0;
                            DataExchange.getInstance().setSerialNumbers("");
                            DataExchange.getInstance().setOptionalserialNumbers("");
                        } else {
                            base.InitializeItem(serialnr, DataExchange.getInstance().getSerialUccNumber(), false, false, false, true);
                            return;
                        }
                    } else if (base.GetCurrentItem() == serialnr && shootedSerials > 1) {
                        shootedSerials--;
                        DataExchange.getInstance().setSerialNumbers(DataExchange.getInstance().getSerialNumbers().substring(
                                DataExchange.getInstance().getSerialNumbers().length() - 1, 1));
                        DataExchange.getInstance().setSerialNumbers(DataExchange.getInstance().getSerialNumbers().substring(
                                DataExchange.getInstance().getSerialNumbers().lastIndexOf('|') + 1,
                                DataExchange.getInstance().getSerialNumbers().length() - DataExchange.getInstance().getSerialNumbers().lastIndexOf('|') - 1));

                        base.InitializeItem(serialnr, DataExchange.getInstance().getSerialUccNumber(), false, false, false, true);
                        String lastSerial = DataExchange.getInstance().getSerialNumbers().substring(DataExchange.getInstance().getSerialNumbers().lastIndexOf('|'), 1);
                        lastSerial = lastSerial.substring(lastSerial.lastIndexOf('|') + 1);
                        base.AddDisplayRow(lastSerial, "", false);
                    } else if (base.GetCurrentItem() == optionalserialnr && shootedSerials > 1) {
                        DataExchange.getInstance().setOptionalserialNumbers(DataExchange.getInstance().getOptionalserialNumbers().substring(
                                DataExchange.getInstance().getOptionalserialNumbers().length() - 1, 1));
                        DataExchange.getInstance().setOptionalserialNumbers(DataExchange.getInstance().getOptionalserialNumbers().substring(
                                DataExchange.getInstance().getOptionalserialNumbers().lastIndexOf('|') + 1,
                                DataExchange.getInstance().getOptionalserialNumbers().length() - DataExchange.getInstance().getOptionalserialNumbers().lastIndexOf('|') - 1));
                    } else if (previousItem == listid)
                        base.ClearAllDisplay();
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
        if (!cloudConnector.getPostExceuteMethod().equals("mobile.logicha.wcftester.AutomaticPicking.FinalStep()")) {
            Object[] params = {null, result.equals("OK") ? step : 0};

            if (!cloudConnector.getPostExceuteMethod().equals("mobile.logicha.wcftester.AutomaticPicking.FindNextMission(int)"))
                this.getClass().getMethods()[0].invoke(this, params);
            else
                this.getClass().getMethod("FindNextMission", int.class).invoke(this, new Object[] {step});//FINDNEXTMISSION
        } else {
            if (result.equals("OK")) {
                DataExchange.getInstance().setStep('F');

                if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getListid())) {

                    AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                    alertMessageBuilder.BuildDialog(getResources().getString(R.string.info), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.INFO, this);
                    alertMessageBuilder.Show();

                    base.InitializeItem(listid, true);
                    return;
                }

                Snackbar sbw = Snackbar.make(findViewById(R.id.testMainLayout), getResources().getString(R.string.operation_successfully_completed), Snackbar.LENGTH_LONG)
                        .setAction("No action", null);
                sbw.getView().setBackgroundColor(Color.parseColor("#66a3ff"));
                sbw.show();

                base.ClearAllDisplay();

                DataExchange.getInstance().setContainer("");
                DataExchange.getInstance().setUnit("");
                DataExchange.getInstance().setCurrentArticle(new Article());
                DataExchange.getInstance().setBatch("");
                DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());
                DataExchange.getInstance().setVariantId1("");
                DataExchange.getInstance().setVariantId2("");
                DataExchange.getInstance().setVariantId3("");
                DataExchange.getInstance().setQty(0);

                if (!FindNextMission(2)) {
                    if (DataExchange.getInstance().getPaperless())
                        finish();
                    else
                        base.InitializeItem(listid);
                }
            } else {
                DataExchange.getInstance().setStep('F');

                AlertMessageBuilder alertMessageBuilder = new AlertMessageBuilder();
                alertMessageBuilder.BuildDialog(getResources().getString(R.string.error), DataExchange.getInstance().getMessage(), AlertMessageBuilder.Severity.ERROR, this);
                alertMessageBuilder.Show();

                if (returnTo.equals(serialnr)) {
                    shootedSerials = 0;
                    DataExchange.getInstance().setSerialNumbers("");
                    DataExchange.getInstance().setOptionalserialNumbers("");
                }

                base.InitializeItem(returnTo, true);
            }
        }
    }
}
