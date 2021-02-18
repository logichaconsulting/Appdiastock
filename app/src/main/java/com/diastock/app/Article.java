package com.diastock.app;

public class Article
{
    private String barcode= "";
    private String sku= "";
    private String description= "";
    private double unitqty= 0;
    private boolean usesbatch= false;
    private boolean usesexpire= false;
    private int usesvariant= 0;
    private String palletcode= "";
    private String usesserialnumber= "";
    private boolean usesoptionalserialnumber= false;
    private String measure= "";
    private boolean consumptionmaterial= false;
    private int shelflifein= 0;
    private boolean usequalitycontrol= false;

    private double defaultqty= 0;

    public Article() throws Exception {
        barcode = null;
        sku = null;
        description = null;
        unitqty = 0;
        usesbatch = false;
        usesexpire = false;
        usesvariant = 0;
        palletcode = null;
        usesserialnumber = null;
        usesoptionalserialnumber = false;
        measure = null;
        consumptionmaterial = false;
        shelflifein = 0;
        usequalitycontrol = false;
        defaultqty = 0;
    }

    public boolean getUsequalitycontrol() throws Exception {
        return usequalitycontrol;
    }

    public void setUsequalitycontrol(boolean value) throws Exception {
        usequalitycontrol = value;
    }

    public String getPalletcode() throws Exception {
        return palletcode;
    }

    public void setPalletcode(String value) throws Exception {
        palletcode = value;
    }

    public String getBarcode() throws Exception {
        return barcode;
    }

    public void setBarcode(String value) throws Exception {
        barcode = value;
    }

    public String getSku() throws Exception {
        return sku;
    }

    public void setSku(String value) throws Exception {
        sku = value;
    }

    public String getDescription() throws Exception {
        return description;
    }

    public void setDescription(String value) throws Exception {
        description = value;
    }

    public double getUnitqty() throws Exception {
        return unitqty;
    }

    public void setUnitqty(double value) throws Exception {
        unitqty = value;
    }

    public boolean getUsesbatch() throws Exception {
        return usesbatch;
    }

    public void setUsesbatch(boolean value) throws Exception {
        usesbatch = value;
    }

    public boolean getUsesexpire() throws Exception {
        return usesexpire;
    }

    public void setUsesexpire(boolean value) throws Exception {
        usesexpire = value;
    }

    public int getUsesvariant() throws Exception {
        return usesvariant;
    }

    public void setUsesvariant(int value) throws Exception {
        usesvariant = value;
    }

    public String getUsesserialnumber() throws Exception {
        return usesserialnumber;
    }

    public void setUsesserialnumber(String value) throws Exception {
        usesserialnumber = value;
    }

    public boolean getUsesoptionalserialnumber() throws Exception {
        return usesoptionalserialnumber;
    }

    public void setUsesoptionalserialnumber(boolean value) throws Exception {
        usesoptionalserialnumber = value;
    }

    public String getMeasure() throws Exception {
        return measure;
    }

    public void setMeasure(String value) throws Exception {
        measure = value;
    }

    public boolean getConsumptionmaterial() throws Exception {
        return consumptionmaterial;
    }

    public void setConsumptionmaterial(boolean value) throws Exception {
        consumptionmaterial = value;
    }

    public int getShelflifein() throws Exception {
        return shelflifein;
    }

    public void setShelflifein(int value) throws Exception {
        shelflifein = value;
    }

    public double getDefaultqty() {
        return defaultqty;
    }

    public void setDefaultqty(double defaultqty) {
        this.defaultqty = defaultqty;
    }
}


