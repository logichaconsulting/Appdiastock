package com.diastock.app;

public class Customer
{
    private String company = "";
    private String barcode = "";
    private String description = "";
    private boolean useseanucc = false;

    public Customer() throws Exception {
        company = "";
        barcode = "";
        description = "";
        useseanucc = false;
    }

    public String getCompany() throws Exception {
        return company;
    }

    public void setCompany(String value) throws Exception {
        company = value;
    }

    public String getBarcode() throws Exception {
        return barcode;
    }

    public void setBarcode(String value) throws Exception {
        barcode = value;
    }

    public String getDescription() throws Exception {
        return description;
    }

    public void setDescription(String value) throws Exception {
        description = value;
    }

    public boolean getUseseanucc() throws Exception {
        return useseanucc;
    }

    public void setUseseanucc(boolean value) throws Exception {
        useseanucc = value;
    }

}


