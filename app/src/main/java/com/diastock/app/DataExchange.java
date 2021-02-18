package com.diastock.app;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataExchange {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static DataExchange dataExchange;
    private static DataExchange dataMessageExchange;
    private static final String splitter = "\u0126";

    public static DataExchange getInstance() throws Exception {
        if (dataExchange == null) dataExchange = new DataExchange();
        return dataExchange;
    }

    public static DataExchange getMessagesInstance() throws Exception {
        if (dataMessageExchange == null) dataMessageExchange = new DataExchange();
        return dataMessageExchange;
    }

    public static void set(DataExchange data) throws Exception {
        dataExchange = data;
    }

    public enum MessageType {
        VOID,
        OK,
        ERROR,
        NEW,
        MAPPED,
        QUESTION,
        CLOSED
    }

    public enum Operations {
        FMENU,
        F2001,
        // Manual Handling
        F2002,
        // Manual Load
        F2003,
        // Manual Download
        F2004,
        // Company Move
        F2005,
        // Move To Position
        F2006,
        // Logistic Unit Destruction
        F2007,
        // Move Logistic Unit
        F2008,
        // Washing
        F2009,
        // Workings
        F2010,
        // Company Move With Document
        F2011,
        // Container Move
        F2012,
        // Container Compacting
        F1002,
        // Simple Inbound
        I0001,
        // Get Logistic Unit
        I0002,
        // Get Sku
        I0003,
        // Get From Location
        I0004,
        // Get To Location
        I0005,
        // Get Supplier
        F3001,
        // Closed Inventory
        F3002,
        // Cycle Count Inventory
        F3003,
        // FastInventory
        F3004,
        // Detailed FastInventory
        F0001,
        // Article Inquiry
        F0003,
        // Logistic Unit Inquiry
        F0004,
        // Container Inquiry
        F0002,
        // Location Inquiry
        F0005,
        // Create Article
        F0006,
        // Box-ID Destination
        F1001,
        // Inbound
        F1003,
        // Create Arrival List
        F1004,
        // Box-ID Unload
        F1005,
        // Lock/Unlock Box-ID
        F1006,
        // Box-ID Sorting
        F1007,
        // Box-ID Rectification
        F1008,
        // Edit Logunit Attributes
        F1009,
        // Edit Palletcode
        I0006,
        // Inbound Situation
        F4001,
        // Order Shipment
        I0007,
        // Order Situation
        F5001,
        // Automatic Storage
        I0009,
        // Check Container
        F5002,
        // Automatic Handling
        I0010,
        // Check Final Unit
        F5003,
        // Automatic Shipping
        F5004,
        // End Cardboard
        F5005,
        // Packing
        F5006,
        // CardBoard Rectification
        F5007,
        // Picking Rectification
        F5008,
        // Returned for Replacement
        F5009,
        // One Shot Picking
        F5010,
        // FCDN Shipment
        F5011,
        // Picking Recount
        F5012,
        // packagess Counting
        F5013,
        // Borderò Checklist
        F5014,
        // Driven Storage
        F5015,
        // Container Pairing
        F6001,
        // Print Label Sku Batch
        F6002,
        // Print Label Sku
        F6003,
        // Print Logunit From Batch
        F6004,
        // RePrint Logunit
        F9000,
        // Messages
        I0011,
        // Get Article Inbound
        I0008
    }

    //Stock Positions
    private String version = "";
    private Date start = new Date();
    private char option = '\0';
    private String userId = "";
    private String password = "";
    private String company = "";
    private String langId = "";
    private Operations functionName = Operations.FMENU;
    private double qty = 0;
    private double requiredqty = 0;
    private double partialqty = 0;
    private String batch = "";
    private String variantId1 = "";
    private String variantId2 = "";
    private String variantId3 = "";
    private String unit = "";
    private String container = "";
    private String message = "";
    private MessageType messagetype = MessageType.VOID;
    private String downloadCausal = "";
    private String loadCausal = "";
    private Customer customer;
    private String order = "";
    private WarehouseLocation fromLocation;
    private WarehouseLocation toLocation;
    private Article currentArticle;
    private Supplier supplier;
    private Date expire = DataFunctions.getEmptyDate();
    private String docnr = "";
    private Date docdate = DataFunctions.getEmptyDate();
    private String genericString1 = "";
    private String genericString2 = "";
    private int genericInt1 = 0;
    private int genericInt2 = 0;
    private String genericCoordinates = "";
    private int row = 0;
    private DocType doctype;
    private String cardboard = "";
    private String box = "";
    private String palletcode = "";
    private String reserved = "";
    private String eanucc = "";
    private String warehouse = "";
    private String listnr = "";
    private String listid = "";
    private boolean paperless = false;
    private String finalContainer = "";
    private String finalUnit = "";
    private String packages = "";
    private String oemid = "";
    private String serialNumbers = "";
    private String batches = "";
    private String switchedUnit = "";
    private double netweight = 0;
    private double grossweight = 0;
    private double volume = 0;
    private String suggestedToLocations = "";
    private String optionalserialNumbers = "";
    private String switchedBatch = "";
    private Date switchedExpire = DataFunctions.getEmptyDate();
    private boolean lockedforshelflife = false;
    private String serialUccNumber = "";
    private boolean logunitEdited = false;
    private boolean carrierAutoparcellabels = false;
    private int standardpallets = 0;
    private int outsizedpallets = 0;
    private String billingCausals = "";
    private String instantMessage = "";
    private boolean requestWeightOnPicking = false;
    private boolean requestVolumeOnPicking = false;
    private boolean bypassmarkshippingpackagess = false;
    private String switchedVariant1 = "";
    private String switchedVariant2 = "";
    private String switchedVariant3 = "";
    private int pieces = 0;
    private int functionNumber = 0;
    private double splitqty = 0;

    public static final String PROP_STEP = "Step";
    public static final String PROP_USERID = "UserId";
    public static final String PROP_PASSWORD = "Password";
    public static final String PROP_COMPANY = "Company";
    public static final String PROP_LANGID = "LangId";
    public static final String PROP_TERMID = "TermId";
    public static final String PROP_FUNCTIONNAME = "FunctionName";
    public static final String PROP_FUNCTIONNUMBER = "FunctionNumber";
    public static final String PROP_QTY = "Qty";
    public static final String PROP_REQ_QTY = "RequiredQty";
    public static final String PROP_PAR_QTY = "PartialQty";
    public static final String PROP_BATCH = "Batch";
    public static final String PROP_VARIANTID1 = "VariantId1";
    public static final String PROP_VARIANTID2 = "VariantId2";
    public static final String PROP_VARIANTID3 = "VariantId3";
    public static final String PROP_UNIT = "Unit";
    public static final String PROP_CONTAINER = "Container";
    public static final String PROP_MESSAGE = "Message";
    public static final String PROP_MESSAGE_TYPE = "Messagetype";
    public static final String PROP_DOWNLOADCAUSAL = "DownloadCausal";
    public static final String PROP_LOADCAUSAL = "LoadCausal";
    public static final String PROP_CUSTOMER = "Customer";
    public static final String PROP_ORDER = "Order";
    public static final String PROP_FROMLOCATION = "FromLocation";
    public static final String PROP_TOLOCATION = "ToLocation";
    public static final String PROP_CURRENTARTICLE = "CurrentArticle";
    public static final String PROP_SUPPLIER = "Supplier";
    public static final String PROP_EXPIRE = "Expire";
    public static final String PROP_DOCNR = "Docnr";
    public static final String PROP_DOCDATE = "Docdate";
    public static final String PROP_GEN_STR_1 = "GenericString1";
    public static final String PROP_GEN_STR_2 = "GenericString2";
    public static final String PROP_GEN_INT_1 = "GenericInt1";
    public static final String PROP_GEN_INT_2 = "GenericInt2";
    public static final String PROP_GEN_COORDINATES = "GenericCoordinates";
    public static final String PROP_ROW = "Row";
    public static final String PROP_DOCTYPE = "Doctype";
    public static final String PROP_CARDBOARD = "Cardboard";
    public static final String PROP_BOX = "Box";
    public static final String PROP_PALLETCODE = "Palletcode";
    public static final String PROP_RESERVED = "Reserved";
    public static final String PROP_EANUCC = "Eanucc";
    public static final String PROP_WAREHOUSE = "Warehouse";
    public static final String PROP_LISTNR = "Listnr";
    public static final String PROP_LISTID = "Listid";
    public static final String PROP_FINALCONTAINER = "FinalContainer";
    public static final String PROP_FINALUNIT = "FinalUnit";
    public static final String PROP_packages = "packages";
    public static final String PROP_SERIALNR = "SerialNumbers";
    public static final String PROP_OPTIONAL_SERIALNR = "OptionalserialNumbers";
    public static final String PROP_BATCHES = "Batches";
    public static final String PROP_SWITCHEDUNIT = "SwitchedUnit";
    public static final String PROP_NETWEIGHT = "Netweight";
    public static final String PROP_GROSSWEIGHT = "Grossweight";
    public static final String PROP_VOLUME = "Volume";
    public static final String PROP_SUGGESTEDTOLOCATIONS = "SuggestedToLocations";
    public static final String PROP_SWITCHEDBATCH = "SwitchedBatch";
    public static final String PROP_SWITCHEDEXPIRE = "SwitchedExpire";
    public static final String PROP_LOCKEDFORSHELFLIFE = "Lockedforshelflife";
    public static final String PROP_LOGUNITEDITED = "LogunitEdited";
    public static final String PROP_STANDARDPALLETS = "Standardpallets";
    public static final String PROP_OUTSIZEDPALLETS = "Outsizedpallets";
    public static final String PROP_SWITCHEDVARIANT1 = "SwitchedVariant1";
    public static final String PROP_SWITCHEDVARIANT2 = "SwitchedVariant2";
    public static final String PROP_SWITCHEDVARIANT3 = "SwitchedVariant3";
    public static final String PROP_PIECES = "Pieces";
    public static final String PROP_SPLITQTY = "SplitQty";

    public DataExchange() throws Exception {
        this.format();
    }

    //Format();
    public void format() throws Exception {
        version = "";
        start = new Date();
        start = Calendar.getInstance().getTime();
        fromLocation = new WarehouseLocation();
        toLocation = new WarehouseLocation();
        currentArticle = new Article();
        supplier = new Supplier();
        expire = DataFunctions.getEmptyDate();
        docnr = null;
        docdate = DataFunctions.getEmptyDate();
        option = '\0';
        functionName = Operations.FMENU;
        qty = 0;
        requiredqty = 0;
        partialqty = 0;
        batch = null;
        variantId1 = null;
        variantId2 = null;
        variantId3 = null;
        unit = null;
        container = null;
        downloadCausal = null;
        loadCausal = null;
        customer = new Customer();
        order = null;
        genericString1 = null;
        genericString2 = null;
        genericInt1 = 0;
        genericInt2 = 0;
        genericCoordinates = null;
        row = 0;
        doctype = new DocType();
        cardboard = null;
        box = null;
        palletcode = null;
        reserved = null;
        eanucc = null;
        warehouse = null;
        listnr = null;
        listid = null;
        paperless = false;
        finalContainer = null;
        finalUnit = null;
        packages = null;
        oemid = null;
        serialNumbers = null;
        batches = null;
        message = null;
        messagetype = MessageType.OK;
        switchedUnit = null;
        netweight = 0;
        grossweight = 0;
        volume = 0;
        suggestedToLocations = null;
        optionalserialNumbers = null;
        switchedBatch = null;
        switchedExpire = DataFunctions.getEmptyDate();
        lockedforshelflife = false;
        serialUccNumber = null;
        logunitEdited = false;
        carrierAutoparcellabels = false;
        standardpallets = 0;
        outsizedpallets = 0;
        billingCausals = null;
        instantMessage = null;
        bypassmarkshippingpackagess = false;
        switchedVariant1 = null;
        switchedVariant2 = null;
        switchedVariant3 = null;
        pieces = 0;
        functionNumber = 0;
        splitqty = 0;
    }

    public void tryParseExpire(String value) throws Exception {
        try {
            Locale ci = Locale.getDefault();
            //CultureInfo.CurrentCulture.Calendar.TwoDigitYearMax : 2099;


            DataExchange.getInstance().setExpire(dateFormat.parse(value));
        } catch (Exception __dummyCatchVar0) {
            DataExchange.getInstance().setExpire(DataFunctions.getEmptyDate());
        }

    }

    public void tryParseDocdate(String value) throws Exception {
        try {
            Locale ci = Locale.getDefault();
            //CultureInfo.CurrentCulture.Calendar.TwoDigitYearMax : 2099;


            DataExchange.getInstance().setDocdate(dateFormat.parse(value));
        } catch (Exception __dummyCatchVar0) {
            DataExchange.getInstance().setDocdate(DataFunctions.getEmptyDate());
        }

    }

    public String getVersion() throws Exception {
        return version;
    }

    public void setVersion(String value) throws Exception {
        version = value;
    }

    public Article getCurrentArticle() throws Exception {
        return currentArticle;
    }

    public void setCurrentArticle(Article value) throws Exception {
        currentArticle = value;
    }

    public char getStep() throws Exception {
        return option;
    }

    public void setStep(char value) throws Exception {
        option = value;
    }

    public String getBatch() throws Exception {
        return batch;
    }

    public void setBatch(String value) throws Exception {
        batch = value;
    }

    public String getMessage() throws Exception {
        return message;
    }

    public void setMessage(String value) throws Exception {
        message = value;
    }

    public String getUnit() throws Exception {
        return unit;
    }

    public void setUnit(String value) throws Exception {
        unit = value;
    }

    public String getContainer() throws Exception {
        return container;
    }

    public void setContainer(String value) throws Exception {
        container = value;
    }

    public WarehouseLocation getToLocation() throws Exception {
        return toLocation;
    }

    public void setToLocation(WarehouseLocation value) throws Exception {
        toLocation = value;
    }

    public WarehouseLocation getFromLocation() throws Exception {
        return fromLocation;
    }

    public void setFromLocation(WarehouseLocation value) throws Exception {
        fromLocation = value;
    }

    public Operations getFunctionName() throws Exception {
        return functionName;
    }

    public void setFunctionName(Operations value) throws Exception {
        functionName = value;
    }

    public String getUserId() throws Exception {
        return userId;
    }

    public void setUserId(String value) throws Exception {
        userId = value;
    }

    public String getPassword() throws Exception {
        return password;
    }

    public void setPassword(String value) throws Exception {
        password = value;
    }

    public String getCompany() throws Exception {
        return company;
    }

    public void setCompany(String value) throws Exception {
        company = value;
    }

    public String getLangId() throws Exception {
        return langId;
    }

    public void setLangId(String value) throws Exception {
        langId = value;
    }

    public double getQty() throws Exception {
        return qty;
    }

    public void setQty(double value) throws Exception {
        qty = value;
    }

    public double getRequiredQty() throws Exception {
        return requiredqty;
    }

    public void setRequiredQty(double value) throws Exception {
        requiredqty = value;
    }

    public Date getExpire() throws Exception {
        return expire;
    }

    public void setExpire(Date value) throws Exception {
        expire = value;
    }

    public String getDownloadCausal() throws Exception {
        return downloadCausal;
    }

    public void setDownloadCausal(String value) throws Exception {
        downloadCausal = value;
    }

    public String getLoadCausal() throws Exception {
        return loadCausal;
    }

    public void setLoadCausal(String value) throws Exception {
        loadCausal = value;
    }

    public String getVariantId1() throws Exception {
        return variantId1;
    }

    public void setVariantId1(String value) throws Exception {
        variantId1 = value;
    }

    public String getVariantId2() throws Exception {
        return variantId2;
    }

    public void setVariantId2(String value) throws Exception {
        variantId2 = value;
    }

    public String getVariantId3() throws Exception {
        return variantId3;
    }

    public void setVariantId3(String value) throws Exception {
        variantId3 = value;
    }

    public Supplier getSupplier() throws Exception {
        return supplier;
    }

    public void setSupplier(Supplier value) throws Exception {
        supplier = value;
    }

    public Customer getCustomer() throws Exception {
        return customer;
    }

    public void setCustomer(Customer value) throws Exception {
        customer = value;
    }

    public String getOrder() throws Exception {
        return order;
    }

    public void setOrder(String value) throws Exception {
        order = value;
    }

    public String getDocnr() throws Exception {
        return docnr;
    }

    public void setDocnr(String value) throws Exception {
        docnr = value;
    }

    public Date getDocdate() throws Exception {
        return docdate;
    }

    public void setDocdate(Date value) throws Exception {
        docdate = value;
    }

    public int getGenericInt1() throws Exception {
        return genericInt1;
    }

    public void setGenericInt1(int value) throws Exception {
        genericInt1 = value;
    }

    public int getGenericInt2() throws Exception {
        return genericInt2;
    }

    public void setGenericInt2(int value) throws Exception {
        genericInt2 = value;
    }

    public String getGenericCoordinates() throws Exception {
        return genericCoordinates;
    }

    public void setGenericCoordinates(String value) throws Exception {
        genericCoordinates = value;
    }

    public int getRow() throws Exception {
        return row;
    }

    public void setRow(int value) throws Exception {
        row = value;
    }

    public double getPartialQty() throws Exception {
        return partialqty;
    }

    public void setPartialQty(double value) throws Exception {
        partialqty = value;
    }

    public MessageType getMessagetype() throws Exception {
        return messagetype;
    }

    public void setMessagetype(MessageType value) throws Exception {
        messagetype = value;
    }

    public DocType getDoctype() throws Exception {
        return doctype;
    }

    public void setDoctype(DocType value) throws Exception {
        doctype = value;
    }

    public String getGenericString1() throws Exception {
        return genericString1;
    }

    public void setGenericString1(String value) throws Exception {
        genericString1 = value;
    }

    public String getGenericString2() throws Exception {
        return genericString2;
    }

    public void setGenericString2(String value) throws Exception {
        genericString2 = value;
    }

    public String getCardboard() throws Exception {
        return cardboard;
    }

    public void setCardboard(String value) throws Exception {
        cardboard = value;
    }

    public String getBox() throws Exception {
        return box;
    }

    public void setBox(String value) throws Exception {
        box = value;
    }

    public String getPalletcode() throws Exception {
        return palletcode;
    }

    public void setPalletcode(String value) throws Exception {
        palletcode = value;
    }

    public Date getStart() throws Exception {
        return start;
    }

    public void setStart(Date value) throws Exception {
        start = value;
    }

    public String getReserved() throws Exception {
        return reserved;
    }

    public void setReserved(String value) throws Exception {
        reserved = value;
    }

    public String getEanucc() throws Exception {
        return eanucc;
    }

    public void setEanucc(String value) throws Exception {
        eanucc = value;
    }

    public String getWarehouse() throws Exception {
        return warehouse;
    }

    public void setWarehouse(String value) throws Exception {
        warehouse = value;
    }

    public String getListnr() throws Exception {
        return listnr;
    }

    public void setListnr(String value) throws Exception {
        listnr = value;
    }

    public String getListid() throws Exception {
        return listid;
    }

    public void setListid(String value) throws Exception {
        listid = value;
    }

    public boolean getPaperless() throws Exception {
        return paperless;
    }

    public void setPaperless(boolean value) throws Exception {
        paperless = value;
    }

    public String getFinalContainer() throws Exception {
        return finalContainer;
    }

    public void setFinalContainer(String value) throws Exception {
        finalContainer = value;
    }

    public String getFinalUnit() throws Exception {
        return finalUnit;
    }

    public void setFinalUnit(String value) throws Exception {
        finalUnit = value;
    }

    public String getpackages() throws Exception {
        return packages;
    }

    public void setpackages(String value) throws Exception {
        packages = value;
    }

    public String getOemid() throws Exception {
        return oemid;
    }

    public void setOemid(String value) throws Exception {
        oemid = value;
    }

    public String getSerialNumbers() throws Exception {
        return serialNumbers;
    }

    public void setSerialNumbers(String value) throws Exception {
        serialNumbers = value;
    }

    public String getBatches() throws Exception {
        return batches;
    }

    public void setBatches(String value) throws Exception {
        batches = value;
    }

    public String getSwitchedUnit() throws Exception {
        return switchedUnit;
    }

    public void setSwitchedUnit(String value) throws Exception {
        switchedUnit = value;
    }

    public double getNetweight() throws Exception {
        return netweight;
    }

    public void setNetweight(double value) throws Exception {
        netweight = value;
    }

    public double getGrossweight() throws Exception {
        return grossweight;
    }

    public void setGrossweight(double value) throws Exception {
        grossweight = value;
    }

    public double getVolume() throws Exception {
        return volume;
    }

    public void setVolume(double value) throws Exception {
        volume = value;
    }

    public String getSuggestedToLocations() throws Exception {
        return suggestedToLocations;
    }

    public void setSuggestedToLocations(String value) throws Exception {
        suggestedToLocations = value;
    }

    public String getOptionalserialNumbers() throws Exception {
        return optionalserialNumbers;
    }

    public void setPieces(Integer value) throws Exception {
        pieces = value;
    }

    public Integer getPieces() throws Exception {
        return pieces;
    }

    public void setOptionalserialNumbers(String value) throws Exception {
        optionalserialNumbers = value;
    }

    public String getSwitchedBatch() throws Exception {
        return switchedBatch;
    }

    public void setSwitchedBatch(String value) throws Exception {
        switchedBatch = value;
    }

    public String getSwitchedVariant1() throws Exception {
        return switchedVariant1;
    }

    public void setSwitchedVariant1(String value) throws Exception {
        switchedVariant1 = value;
    }

    public Date getSwitchedExpire() throws Exception {
        return switchedExpire;
    }

    public void setSwitchedExpire(Date value) throws Exception {
        switchedExpire = value;
    }

    public boolean getLockedforshelflife() throws Exception {
        return lockedforshelflife;
    }

    public void setLockedforshelflife(boolean value) throws Exception {
        lockedforshelflife = value;
    }

    public String getSerialUccNumber() throws Exception {
        return serialUccNumber;
    }

    public void setSerialUccNumber(String value) throws Exception {
        serialUccNumber = value;
    }

    public boolean getLogunitEdited() throws Exception {
        return logunitEdited;
    }

    public void setLogunitEdited(boolean value) throws Exception {
        logunitEdited = value;
    }

    public boolean getCarrierAutoparcellabels() throws Exception {
        return carrierAutoparcellabels;
    }

    public void setCarrierAutoparcellabels(boolean value) throws Exception {
        carrierAutoparcellabels = value;
    }

    public int getOutsizedpallets() throws Exception {
        return outsizedpallets;
    }

    public void setOutsizedpallets(int value) throws Exception {
        outsizedpallets = value;
    }

    public int getStandardpallets() throws Exception {
        return standardpallets;
    }

    public void setStandardpallets(int value) throws Exception {
        standardpallets = value;
    }

    public String getBillingCausals() throws Exception {
        return billingCausals;
    }

    public void setBillingCausals(String value) throws Exception {
        billingCausals = value;
    }

    public boolean getRequestWeightOnPicking() throws Exception {
        return requestWeightOnPicking;
    }

    public void setRequestWeightOnPicking(boolean value) throws Exception {
        requestWeightOnPicking = value;
    }

    public boolean getRequestVolumeOnPicking() throws Exception {
        return requestVolumeOnPicking;
    }

    public void setRequestVolumeOnPicking(boolean value) throws Exception {
        requestVolumeOnPicking = value;
    }

    public String getInstantMessage() throws Exception {
        return instantMessage;
    }

    public void setInstantMessage(String value) throws Exception {
        instantMessage = value;
    }

    public boolean getBypassmarkshippingpackagess() throws Exception {
        return bypassmarkshippingpackagess;
    }

    public void setBypassmarkshippingpackagess(boolean value) throws Exception {
        bypassmarkshippingpackagess = value;
    }

    public String getSwitchedVariant2() throws Exception {
        return switchedVariant2;
    }

    public void setSwitchedVariant2(String value) throws Exception {
        switchedVariant2 = value;
    }

    public String getSwitchedVariant3() throws Exception {
        return switchedVariant3;
    }

    public void setSwitchedVariant3(String value) throws Exception {
        switchedVariant3 = value;
    }

    //Function Number
    public int getFunctionNumber() throws Exception {
        return functionNumber;
    }

    public void setFunctionNumber(int value) throws Exception {
        functionNumber = value;
    }


    public double getSplitqty() {
        return splitqty;
    }

    public void setSplitqty(double splitqty) {
        this.splitqty = splitqty;
    }


    private String fieldConvert(Object o) throws Exception {
        if (o == null)
            return "" + splitter;
        else if (o.getClass() == String.class)
            return (String) o + splitter;
        else if (o.getClass() == Date.class)
            return dateFormat.format((Date) o) + splitter;
        else if (o.getClass() == Character.class)
            return String.valueOf((Character) o) + splitter;
        else
            return o.toString() + splitter;
    }

    public void deSerialize(String msg) throws Exception {
        deSerialize(msg, BuildConfig.VERSION_NAME);
    }

    public void deSerialize(String msg, String serverVersion) throws Exception, VersionException {
        this.format();
        String[] values = msg.split(splitter);
        int index = 0;
        start = dateFormat.parse(values[index++]);
        option = values[index++].charAt(0);
        userId = values[index++];
        password = values[index++];
        company = values[index++];
        langId = values[index++];
        functionName = Enum.valueOf(Operations.class, values[index++]);
        // DONT MOVE FROM HERE (OR KEEP AFTER functionName)!
        version = values[index++];
        if (serverVersion != null && !(version.equals(serverVersion))) {
            throw new VersionException();
        }

        qty = DataFunctions.readDecimal(values[index++]);
        requiredqty = DataFunctions.readDecimal(values[index++]);
        partialqty = DataFunctions.readDecimal(values[index++]);
        batch = values[index++];
        variantId1 = values[index++];
        variantId2 = values[index++];
        variantId3 = values[index++];
        unit = values[index++];
        container = values[index++];
        message = values[index++];
        messagetype = Enum.valueOf(MessageType.class, values[index++]);
        downloadCausal = values[index++];
        loadCausal = values[index++];
        // private Customer customer;
        customer.setCompany(values[index++]);
        customer.setBarcode(values[index++]);
        customer.setDescription(values[index++]);
        customer.setUseseanucc(Boolean.parseBoolean(values[index++]));
        order = values[index++];
        // private WarehouseLocation fromLocation;
        fromLocation.setEntirePosition(values[index++]);
        // DO NOT MOVE: LEAVE FIRST
        fromLocation.setBuilding(values[index++]);
        fromLocation.setDepartment(values[index++]);
        fromLocation.setShelf(values[index++]);
        fromLocation.setPosition(values[index++]);
        fromLocation.setFloor(values[index++]);
        fromLocation.setPositionCode(Integer.parseInt(values[index++]));
        fromLocation.setMaxsku(Integer.parseInt(values[index++]));
        // private WarehouseLocation toLocation;
        toLocation.setEntirePosition(values[index++]);
        // DO NOT MOVE: LEAVE FIRST
        toLocation.setBuilding(values[index++]);
        toLocation.setDepartment(values[index++]);
        toLocation.setShelf(values[index++]);
        toLocation.setPosition(values[index++]);
        toLocation.setFloor(values[index++]);
        toLocation.setPositionCode(Integer.parseInt(values[index++]));
        toLocation.setMaxsku(Integer.parseInt(values[index++]));
        //private Article currentArticle;
        currentArticle.setBarcode(values[index++]);
        currentArticle.setSku(values[index++]);
        currentArticle.setDescription(values[index++]);
        currentArticle.setUnitqty(DataFunctions.readDecimal(values[index++]));
        currentArticle.setUsesbatch(Boolean.parseBoolean(values[index++]));
        currentArticle.setUsesexpire(Boolean.parseBoolean(values[index++]));
        currentArticle.setUsesvariant(Integer.parseInt(values[index++]));
        currentArticle.setPalletcode(values[index++]);
        currentArticle.setUsesserialnumber(values[index++]);
        currentArticle.setUsesoptionalserialnumber(Boolean.parseBoolean(values[index++]));
        currentArticle.setMeasure(values[index++]);
        currentArticle.setConsumptionmaterial(Boolean.parseBoolean(values[index++]));
        currentArticle.setShelflifein(Integer.parseInt(values[index++]));
        currentArticle.setUsequalitycontrol(Boolean.parseBoolean(values[index++]));
        currentArticle.setDefaultqty(DataFunctions.readDecimal(values[index++]));

        // private Supplier supplier;
        supplier.setCompany(values[index++]);
        supplier.setBarcode(values[index++]);
        supplier.setDescription(values[index++]);
        supplier.setUseseanucc(Boolean.parseBoolean(values[index++]));
        supplier.setLogunitAIStart(Integer.parseInt(values[index++]));
        supplier.setSkuAIStart(Integer.parseInt(values[index++]));
        supplier.setBatchAIStart(Integer.parseInt(values[index++]));
        supplier.setVariantAIStart(Integer.parseInt(values[index++]));
        supplier.setSerialnrAIStart(Integer.parseInt(values[index++]));
        supplier.setExpireAIStart(Integer.parseInt(values[index++]));
        supplier.setLogunitAILen(Integer.parseInt(values[index++]));
        supplier.setSkuAILen(Integer.parseInt(values[index++]));
        supplier.setBatchAILen(Integer.parseInt(values[index++]));
        supplier.setVariantAILen(Integer.parseInt(values[index++]));
        supplier.setSerialnrAILen(Integer.parseInt(values[index++]));
        supplier.setExpireAILen(Integer.parseInt(values[index++]));
        supplier.setDynamiceanucc(Boolean.parseBoolean(values[index++]));
        expire = dateFormat.parse(values[index++]);
        docnr = values[index++];
        docdate = dateFormat.parse(values[index++]);
        genericString1 = values[index++];
        genericString2 = values[index++];
        genericInt1 = Integer.parseInt(values[index++]);
        genericInt2 = Integer.parseInt(values[index++]);
        genericCoordinates = values[index++];
        row = Integer.parseInt(values[index++]);
        // private DocType doctype;
        doctype.setCompany(values[index++]);
        doctype.setDoctype(values[index++]);
        doctype.setUsescardboard(Boolean.parseBoolean(values[index++]));
        doctype.setUsesboxes(Boolean.parseBoolean(values[index++]));
        doctype.setPickingmode(Short.parseShort(values[index++]));
        doctype.setQuantitymode(Short.parseShort(values[index++]));
        doctype.setCellstatemode(Short.parseShort(values[index++]));
        doctype.setMultishootbatches(Boolean.parseBoolean(values[index++]));
        doctype.setClosewithdeliverydate(Boolean.parseBoolean(values[index++]));
        doctype.setRelatingdoc(Short.parseShort(values[index++]));
        doctype.setIncomingconsumptionmaterial(Boolean.parseBoolean(values[index++]));
        doctype.setPickingrecountrequired(Boolean.parseBoolean(values[index++]));
        doctype.setPackagescountingrequired(Boolean.parseBoolean(values[index++]));
        doctype.setEditincominglogunit(Boolean.parseBoolean(values[index++]));
        doctype.setRfsinglepiecedirectpick(Boolean.parseBoolean(values[index++]));
        doctype.setReadhplogunit(Boolean.parseBoolean(values[index++]));
        doctype.setPiecesonrfinboundclosing(Boolean.parseBoolean(values[index++]));
        doctype.setPiecesonrfpickingclosing(Boolean.parseBoolean(values[index++]));
        doctype.getQualityparkingposition().setEntirePosition(values[index++]);
        doctype.setUsesinboundcontainer(Boolean.parseBoolean((values[index++])));
        doctype.setSplitpickingwithcardboards(Boolean.parseBoolean((values[index++])));

        // DO NOT MOVE
        cardboard = values[index++];
        box = values[index++];
        palletcode = values[index++];
        reserved = values[index++];
        eanucc = values[index++];
        warehouse = values[index++];
        listnr = values[index++];
        listid = values[index++];
        paperless = Boolean.parseBoolean(values[index++]);
        finalContainer = values[index++];
        finalUnit = values[index++];
        packages = values[index++];
        oemid = values[index++];
        serialNumbers = values[index++];
        batches = values[index++];
        switchedUnit = values[index++];
        netweight = DataFunctions.readDecimal(values[index++]);
        grossweight = DataFunctions.readDecimal(values[index++]);
        volume = DataFunctions.readDecimal(values[index++]);
        suggestedToLocations = values[index++];
        optionalserialNumbers = values[index++];
        switchedBatch = values[index++];
        switchedExpire = dateFormat.parse(values[index++]);
        lockedforshelflife = Boolean.parseBoolean(values[index++]);
        serialUccNumber = values[index++];
        logunitEdited = Boolean.parseBoolean(values[index++]);
        carrierAutoparcellabels = Boolean.parseBoolean(values[index++]);
        standardpallets = Integer.parseInt(values[index++]);
        outsizedpallets = Integer.parseInt(values[index++]);
        billingCausals = values[index++];
        instantMessage = values[index++];
        requestWeightOnPicking = Boolean.parseBoolean(values[index++]);
        requestVolumeOnPicking = Boolean.parseBoolean(values[index++]);
        bypassmarkshippingpackagess = Boolean.parseBoolean(values[index++]);
        switchedVariant1 = values[index++];
        switchedVariant2 = values[index++];
        switchedVariant3 = values[index++];
        pieces = Integer.parseInt(values[index++]);
        functionNumber = Integer.parseInt(values[index++]);
        splitqty = DataFunctions.readDecimal(values[index++]);

        String end = values[index++];
    }

    //99
    public String serialize() throws Exception {
        String msg = "";
        msg += fieldConvert(start);
        msg += fieldConvert(option);
        msg += fieldConvert(userId);
        msg += fieldConvert(password);
        msg += fieldConvert(company);
        msg += fieldConvert(langId);
        msg += fieldConvert(functionName);
        msg += fieldConvert(version);
        msg += fieldConvert(qty);
        msg += fieldConvert(requiredqty);
        msg += fieldConvert(partialqty);
        msg += fieldConvert(batch);
        msg += fieldConvert(variantId1);
        msg += fieldConvert(variantId2);
        msg += fieldConvert(variantId3);
        msg += fieldConvert(unit);
        msg += fieldConvert(container);
        msg += fieldConvert(message);
        msg += fieldConvert(messagetype);
        msg += fieldConvert(downloadCausal);
        msg += fieldConvert(loadCausal);
        //private Customer customer);
        msg += fieldConvert(customer.getCompany());
        msg += fieldConvert(customer.getBarcode());
        msg += fieldConvert(customer.getDescription());
        msg += fieldConvert(customer.getUseseanucc());
        msg += fieldConvert(order);
        //private WarehouseLocation fromLocation;
        msg += fieldConvert(fromLocation.getEntirePosition());
        // DO NOT MOVE: LEAVE FIRST
        msg += fieldConvert(fromLocation.getBuilding());
        msg += fieldConvert(fromLocation.getDepartment());
        msg += fieldConvert(fromLocation.getShelf());
        msg += fieldConvert(fromLocation.getPosition());
        msg += fieldConvert(fromLocation.getFloor());
        msg += fieldConvert(fromLocation.getPositionCode());
        msg += fieldConvert(fromLocation.getMaxsku());
        //private WarehouseLocation toLocation;
        msg += fieldConvert(toLocation.getEntirePosition());
        // DO NOT MOVE: LEAVE FIRST
        msg += fieldConvert(toLocation.getBuilding());
        msg += fieldConvert(toLocation.getDepartment());
        msg += fieldConvert(toLocation.getShelf());
        msg += fieldConvert(toLocation.getPosition());
        msg += fieldConvert(toLocation.getFloor());
        msg += fieldConvert(toLocation.getPositionCode());
        msg += fieldConvert(toLocation.getMaxsku());
        //private Article currentArticle;
        msg += fieldConvert(currentArticle.getBarcode());
        msg += fieldConvert(currentArticle.getSku());
        msg += fieldConvert(currentArticle.getDescription());
        msg += fieldConvert(currentArticle.getUnitqty());
        msg += fieldConvert(currentArticle.getUsesbatch());
        msg += fieldConvert(currentArticle.getUsesexpire());
        msg += fieldConvert(currentArticle.getUsesvariant());
        msg += fieldConvert(currentArticle.getPalletcode());
        msg += fieldConvert(currentArticle.getUsesserialnumber());
        msg += fieldConvert(currentArticle.getUsesoptionalserialnumber());
        msg += fieldConvert(currentArticle.getMeasure());
        msg += fieldConvert(currentArticle.getConsumptionmaterial());
        msg += fieldConvert(currentArticle.getShelflifein());
        msg += fieldConvert(currentArticle.getUsequalitycontrol());
        msg += fieldConvert(currentArticle.getDefaultqty());

        //private Supplier supplier;
        msg += fieldConvert(supplier.getCompany());
        msg += fieldConvert(supplier.getBarcode());
        msg += fieldConvert(supplier.getDescription());
        msg += fieldConvert(supplier.getUseseanucc());
        msg += fieldConvert(supplier.getLogunitAIStart());
        msg += fieldConvert(supplier.getSkuAIStart());
        msg += fieldConvert(supplier.getBatchAIStart());
        msg += fieldConvert(supplier.getVariantAIStart());
        msg += fieldConvert(supplier.getSerialnrAIStart());
        msg += fieldConvert(supplier.getExpireAIStart());
        msg += fieldConvert(supplier.getLogunitAILen());
        msg += fieldConvert(supplier.getSkuAILen());
        msg += fieldConvert(supplier.getBatchAILen());
        msg += fieldConvert(supplier.getVariantAILen());
        msg += fieldConvert(supplier.getSerialnrAILen());
        msg += fieldConvert(supplier.getExpireAILen());
        msg += fieldConvert(supplier.getDynamiceanucc());
        msg += fieldConvert(expire);
        msg += fieldConvert(docnr);
        msg += fieldConvert(docdate);
        msg += fieldConvert(genericString1);
        msg += fieldConvert(genericString2);
        msg += fieldConvert(genericInt1);
        msg += fieldConvert(genericInt2);
        msg += fieldConvert(genericCoordinates);
        msg += fieldConvert(row);
        //private DocType doctype;
        msg += fieldConvert(doctype.getCompany());
        msg += fieldConvert(doctype.getDoctype());
        msg += fieldConvert(doctype.getUsescardboard());
        msg += fieldConvert(doctype.getUsesboxes());
        msg += fieldConvert(doctype.getPickingmode());
        msg += fieldConvert(doctype.getQuantitymode());
        msg += fieldConvert(doctype.getCellstatemode());
        msg += fieldConvert(doctype.getMultishootbatches());
        msg += fieldConvert(doctype.getClosewithdeliverydate());
        msg += fieldConvert(doctype.getRelatingdoc());
        msg += fieldConvert(doctype.getIncomingconsumptionmaterial());
        msg += fieldConvert(doctype.getPickingrecountrequired());
        msg += fieldConvert(doctype.getPackagescountingrequired());
        msg += fieldConvert(doctype.getEditincominglogunit());
        msg += fieldConvert(doctype.getRfsinglepiecedirectpick());
        msg += fieldConvert(doctype.getReadhplogunit());
        msg += fieldConvert(doctype.isPiecesonrfinboundclosing());
        msg += fieldConvert(doctype.isPiecesonrfpickingclosing());
        msg += fieldConvert(doctype.getQualityparkingposition().getEntirePosition());
        msg += fieldConvert(doctype.isUsesinboundcontainer());
        msg += fieldConvert(doctype.isSplitpickingwithcardboards());
        // DO NOT MOVE
        msg += fieldConvert(cardboard);
        msg += fieldConvert(box);
        msg += fieldConvert(palletcode);
        msg += fieldConvert(reserved);
        msg += fieldConvert(eanucc);
        msg += fieldConvert(warehouse);
        msg += fieldConvert(listnr);
        msg += fieldConvert(listid);
        msg += fieldConvert(paperless);
        msg += fieldConvert(finalContainer);
        msg += fieldConvert(finalUnit);
        msg += fieldConvert(packages);
        msg += fieldConvert(oemid);
        msg += fieldConvert(serialNumbers);
        msg += fieldConvert(batches);
        msg += fieldConvert(switchedUnit);
        msg += fieldConvert(netweight);
        msg += fieldConvert(grossweight);
        msg += fieldConvert(volume);
        msg += fieldConvert(suggestedToLocations);
        msg += fieldConvert(optionalserialNumbers);
        msg += fieldConvert(switchedBatch);
        msg += fieldConvert(switchedExpire);
        msg += fieldConvert(lockedforshelflife);
        msg += fieldConvert(serialUccNumber);
        msg += fieldConvert(logunitEdited);
        msg += fieldConvert(carrierAutoparcellabels);
        msg += fieldConvert(standardpallets);
        msg += fieldConvert(outsizedpallets);
        msg += fieldConvert(billingCausals);
        msg += fieldConvert(instantMessage);
        msg += fieldConvert(requestWeightOnPicking);
        msg += fieldConvert(requestVolumeOnPicking);
        msg += fieldConvert(bypassmarkshippingpackagess);
        msg += fieldConvert(switchedVariant1);
        msg += fieldConvert(switchedVariant2);
        msg += fieldConvert(switchedVariant3);
        msg += fieldConvert(pieces);
        msg += fieldConvert(functionNumber);
        msg += fieldConvert(splitqty);

        msg += fieldConvert("END");
        return StringUtils.leftPad(String.valueOf(msg.length()), 4, "0") + msg;
    }

}


