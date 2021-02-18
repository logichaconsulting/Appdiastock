package com.diastock.app;

public class MenuItem {
    private int functionNumber;
    private String functionName;
    private String functionDescription;
    private String defDownloadCausal;
    private String defLoadCausal;
    private int defToLocation;
    private int defFromLocation;
    private boolean isPaperless;
    private boolean logunitswitch;
    private boolean batchexpireswitch;
    private boolean variantIdswitch;
    private boolean askreservedgoods;
    private boolean suggestexpirationdate;
    private boolean oneshotunloading;
    private String lenghtmanager;

    public MenuItem() throws Exception {
        functionNumber = 0;
        functionName = null;
        functionDescription = null;
        defDownloadCausal = null;
        defLoadCausal = null;
        defToLocation = 0;
        defFromLocation = 0;
        isPaperless = false;
        logunitswitch = false;
        batchexpireswitch = false;
        variantIdswitch = false;
        askreservedgoods = false;
        suggestexpirationdate = false;
        oneshotunloading = false;
        lenghtmanager = ("0;0;0;0;0;0;0;0;0;0;0;0");
    }

    public boolean isSuggestexpirationdate() {
        return suggestexpirationdate;
    }

    public void setSuggestexpirationdate(boolean suggestexpirationdate) {
        this.suggestexpirationdate = suggestexpirationdate;
    }

    public boolean isAskreservedgoods() {
        return askreservedgoods;
    }

    public void setAskreservedgoods(boolean askreservedgoods) {
        this.askreservedgoods = askreservedgoods;
    }

    public void setOneshotunloading(boolean oneshotunloading) {
        this.oneshotunloading = oneshotunloading;
    }

    public boolean getOneshotUnloading() throws Exception {
        return oneshotunloading;
    }

    public boolean getVariantIdSwitch() throws Exception {
        return variantIdswitch;
    }

    public void setBatchExpireSwitch(boolean batchexpireswitch) throws Exception {
        this.batchexpireswitch = batchexpireswitch;
    }

    public void setVariantIdSwitch(boolean variantidswitch) throws Exception {
        this.variantIdswitch = variantidswitch;
    }

    public void setLogUnitSwitch(boolean logunitswitch) throws Exception {
        this.logunitswitch = logunitswitch;
    }

    public void setPaperless(boolean paperless) throws Exception {
        isPaperless = paperless;
    }

    public boolean isPaperless() throws Exception {
        return isPaperless;
    }

    public void setFuncionNumber(int number) throws Exception {
        functionNumber = number;
    }

    public void setFunctionName(String name) throws Exception {
        functionName = name;
    }

    public void setFunctionDescription(String description) throws Exception {
        functionDescription = description;
    }

    public int getFunctionNumber() throws Exception {
        return functionNumber;
    }

    public String getFunctionName() throws Exception {
        return functionName;
    }

    public String getFunctionDescription() throws Exception {
        return functionDescription;
    }

    public String getDefDownloadCausal() throws Exception {
        return defDownloadCausal;
    }

    public String getDefLoadCausal() throws Exception {
        return defLoadCausal;
    }

    public void setDefDownloadCausal(String causal) throws Exception {
        this.defDownloadCausal = causal;
    }

    public void setDefLoadCausal(String causal) throws Exception {
        this.defLoadCausal = causal;
    }

    public int getDefToLocation() throws Exception {
        return defToLocation;
    }

    public void setDefToLocation(int value) throws Exception {
        defToLocation = value;
    }

    public int getDefFromLocation() throws Exception {
        return defFromLocation;
    }

    public void setDefFromLocation(int value) throws Exception {
        defFromLocation = value;
    }

    public boolean getLogunitSwitch() throws Exception {
        return logunitswitch;
    }

    public void setLogunitSwitch(boolean value) throws Exception {
        logunitswitch = value;
    }

    public boolean getBatchexpireswitch() throws Exception {
        return batchexpireswitch;
    }

    public void setBatchexpireswitch(boolean value) throws Exception {
        batchexpireswitch = value;
    }

    public String getLenghtmanager() {
        return lenghtmanager;
    }

    public void setLenghtmanager(String lenghtmanager) {
        this.lenghtmanager = lenghtmanager;
    }
}
