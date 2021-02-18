package com.diastock.app;

public class SetupFlags {
    private static SetupFlags setupFlags;
    private boolean usesLogisticUnit;
    private boolean usesContainer;
    private boolean skuOnLogisticUnit;
    private boolean markshippingpackages;
    private boolean usesmaxibarcode;
    private int logunitAIStart;
    private int skuAIStart;
    private int batchAIStart;
    private int variantAIStart;
    private int serialnrAIStart;
    private int expireAIStart;
    private int logunitAILen;
    private int skuAILen;
    private int batchAILen;
    private int variantAILen;
    private int serialnrAILen;
    private int expireAILen;
    private boolean receivemaxdocqty;
    private boolean pickingswitchposition;
    private boolean weightonrflistclosing;
    private boolean volumeonrflistclosing;
    private boolean stockonlastlocation;
    private boolean verifybatch;
    private boolean verifybbe;
    private int serialnrlength;
    private String serialnrprefixes = new String();
    private boolean inventoryusesserialnr;
    private boolean autoparcellabels;
    private boolean pallettypescounting;
    private boolean doubleCountOnInventory;
    private boolean fastinventorybylogunit;
    private boolean inboundcreatearticle;
    private boolean oneshotunloading;
    //AO20200103
    private String lenghtmanager = "0;0;0;0;0;0;0;0;0;0;0;0";
    private  boolean printinboundseriallabel;
    private  boolean rfusesbarcodedefaultqty;
    private  boolean rfautoinsertarrivalorder;

    public static SetupFlags getInstance() throws Exception {
        if (setupFlags == null)
            setupFlags = new SetupFlags();
        return setupFlags;
    }

    public static void set(SetupFlags setup) throws Exception {

        setupFlags = setup;
    }

    public SetupFlags() throws Exception {
        logunitAIStart = 0;
        skuAIStart = 0;
        batchAIStart = 0;
        variantAIStart = 0;
        serialnrAIStart = 0;
        expireAIStart = 0;
        logunitAILen = 0;
        skuAILen = 0;
        batchAILen = 0;
        variantAILen = 0;
        serialnrAILen = 0;
        expireAILen = 0;
        serialnrlength = 0;
    }

    public boolean getSkuOnLogisticUnit() throws Exception {
        return skuOnLogisticUnit;
    }

    public void setSkuOnLogisticUnit(boolean value) throws Exception {
        skuOnLogisticUnit = value;
    }

    public boolean getUseslogisticunit() throws Exception {
        return usesLogisticUnit;
    }

    public void setUseslogisticunit(boolean value) throws Exception {
        usesLogisticUnit = value;
    }

    public boolean getUsescontainer() throws Exception {
        return usesContainer;
    }

    public void setUsescontainer(boolean value) throws Exception {
        usesContainer = value;
    }

    public boolean getMarkshippingpackages() throws Exception {
        return markshippingpackages;
    }

    public void setMarkshippingpackages(boolean value) throws Exception {
        markshippingpackages = value;
    }

    public boolean getUsesmaxibarcode() throws Exception {
        return usesmaxibarcode;
    }

    public void setUsesmaxibarcode(boolean value) throws Exception {
        usesmaxibarcode = value;
    }

    public int getLogunitAIStart() throws Exception {
        return logunitAIStart;
    }

    public void setLogunitAIStart(int value) throws Exception {
        logunitAIStart = value;
    }

    public int getSkuAIStart() throws Exception {
        return skuAIStart;
    }

    public void setSkuAIStart(int value) throws Exception {
        skuAIStart = value;
    }

    public int getBatchAIStart() throws Exception {
        return batchAIStart;
    }

    public void setBatchAIStart(int value) throws Exception {
        batchAIStart = value;
    }

    public int getVariantAIStart() throws Exception {
        return variantAIStart;
    }

    public void setVariantAIStart(int value) throws Exception {
        variantAIStart = value;
    }

    public int getSerialnrAIStart() throws Exception {
        return serialnrAIStart;
    }

    public void setSerialnrAIStart(int value) throws Exception {
        serialnrAIStart = value;
    }

    public int getExpireAIStart() throws Exception {
        return expireAIStart;
    }

    public void setExpireAIStart(int value) throws Exception {
        expireAIStart = value;
    }

    public int getLogunitAILen() throws Exception {
        return logunitAILen;
    }

    public void setLogunitAILen(int value) throws Exception {
        logunitAILen = value;
    }

    public int getSkuAILen() throws Exception {
        return skuAILen;
    }

    public void setSkuAILen(int value) throws Exception {
        skuAILen = value;
    }

    public int getBatchAILen() throws Exception {
        return batchAILen;
    }

    public void setBatchAILen(int value) throws Exception {
        batchAILen = value;
    }

    public int getVariantAILen() throws Exception {
        return variantAILen;
    }

    public void setVariantAILen(int value) throws Exception {
        variantAILen = value;
    }

    public int getSerialnrAILen() throws Exception {
        return serialnrAILen;
    }

    public void setSerialnrAILen(int value) throws Exception {
        serialnrAILen = value;
    }

    public int getExpireAILen() throws Exception {
        return expireAILen;
    }

    public void setExpireAILen(int value) throws Exception {
        expireAILen = value;
    }

    public boolean getReceivemaxdocqty() throws Exception {
        return receivemaxdocqty;
    }

    public void setReceivemaxdocqty(boolean value) throws Exception {
        receivemaxdocqty = value;
    }

    public boolean getPickingswitchposition() throws Exception {
        return pickingswitchposition;
    }

    public void setPickingswitchposition(boolean value) throws Exception {
        pickingswitchposition = value;
    }

    public boolean getWeightonrflistclosing() throws Exception {
        return weightonrflistclosing;
    }

    public void setWeightonrflistclosing(boolean value) throws Exception {
        weightonrflistclosing = value;
    }

    public boolean getVolumeonrflistclosing() throws Exception {
        return volumeonrflistclosing;
    }

    public void setVolumeonrflistclosing(boolean value) throws Exception {
        volumeonrflistclosing = value;
    }

    public boolean getStockonlastlocation() throws Exception {
        return stockonlastlocation;
    }

    public void setStockonlastlocation(boolean value) throws Exception {
        stockonlastlocation = value;
    }

    public boolean getVerifybatch() throws Exception {
        return verifybatch;
    }

    public void setVerifybatch(boolean value) throws Exception {
        verifybatch = value;
    }

    public boolean getVerifybbe() throws Exception {
        return verifybbe;
    }

    public void setVerifybbe(boolean value) throws Exception {
        verifybbe = value;
    }

    public int getSerialnrlength() throws Exception {
        return serialnrlength;
    }

    public void setSerialnrlength(int value) throws Exception {
        serialnrlength = value;
    }

    public String getSerialnrprefixes() throws Exception {
        return serialnrprefixes;
    }

    public void setSerialnrprefixes(String value) throws Exception {
        serialnrprefixes = value;
    }

    public boolean getInventoryusesserialnr() throws Exception {
        return inventoryusesserialnr;
    }

    public void setInventoryusesserialnr(boolean value) throws Exception {
        inventoryusesserialnr = value;
    }

    public boolean getAutoparcellabels() throws Exception {
        return autoparcellabels;
    }

    public void setAutoparcellabels(boolean value) throws Exception {
        autoparcellabels = value;
    }

    public boolean getPallettypescounting() throws Exception {
        return pallettypescounting;
    }

    public void setPallettypescounting(boolean value) throws Exception {
        pallettypescounting = value;
    }

    public boolean getDoubleCountOnInventory() throws Exception {
        return doubleCountOnInventory;
    }

    public void setDoubleCountOnInventory(boolean value) throws Exception {
        doubleCountOnInventory = value;
    }

    public boolean getFastinventorybylogunit() throws Exception {
        return fastinventorybylogunit;
    }

    public void setFastinventorybylogunit(boolean value) throws Exception {
        fastinventorybylogunit = value;
    }

    public boolean getInboundCreateArticle() throws Exception {
        return inboundcreatearticle;
    }

    public void setInboundCreateArticle(boolean value) throws Exception {
        inboundcreatearticle = value;
    }

    public boolean getOneShotUnloading() throws Exception {
        return oneshotunloading;
    }

    public void setOneShotUnloading(boolean value) throws Exception {
        oneshotunloading = value;
    }

    public String getLenghtManager() throws Exception {
        return lenghtmanager;
    }

    public void setLenghtManager(String value) throws Exception {
        lenghtmanager = value;
    }

    public boolean isPrintinboundseriallabel() {
        return printinboundseriallabel;
    }

    public void setPrintinboundseriallabel(boolean printinboundseriallabel) {
        this.printinboundseriallabel = printinboundseriallabel;
    }

    public boolean isRfusesbarcodedefaultqty() {
        return rfusesbarcodedefaultqty;
    }

    public void setRfusesbarcodedefaultqty(boolean rfusesbarcodedefaultqty) {
        this.rfusesbarcodedefaultqty = rfusesbarcodedefaultqty;
    }

    public boolean isRfautoinsertarrivalorder() {
        return rfautoinsertarrivalorder;
    }

    public void setRfautoinsertarrivalorder(boolean rfautoinsertarrivalorder) {
        this.rfautoinsertarrivalorder = rfautoinsertarrivalorder;
    }
}



