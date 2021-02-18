package com.diastock.app;

public class DocType
{
    private String company= "";
    private String doctype= "";
    private boolean usescardboard= false;
    private boolean usesboxes= false;
    private short pickingmode= 0;
    private short quantitymode= 0;
    private short cellstatemode= 0;
    private boolean multishootbatches= false;
    private boolean closewithdeliverydate= false;
    private short relatingdoc= 0;
    private boolean incomingconsumptionmaterial= false;
    private boolean pickingrecountrequired= false;
    private boolean packagescountingrequired= false;
    private boolean editincominglogunit= false;
    private boolean rfsinglepiecedirectpick= false;
    private boolean readhplogunit= false;
    private boolean piecesonrfinboundclosing = false;
    private boolean piecesonrfpickingclosing = false;
    private WarehouseLocation qualityparkingposition;
    private boolean usesinboundcontainer;
    private boolean splitpickingwithcardboards;


    public DocType() throws Exception {
        company= "";
        doctype= "";
        usescardboard = false;
        usesboxes = false;
        pickingmode = 0;
        quantitymode = 0;
        cellstatemode = 0;
        multishootbatches = false;
        closewithdeliverydate = false;
        relatingdoc = 0;
        pickingrecountrequired = false;
        packagescountingrequired = false;
        editincominglogunit = false;
        readhplogunit = false;
        piecesonrfinboundclosing = false;
        piecesonrfpickingclosing = false;
        qualityparkingposition = new WarehouseLocation();
        splitpickingwithcardboards = false;
    }

    public WarehouseLocation getQualityparkingposition() throws Exception {
        return qualityparkingposition;
    }

    public void setQualityparkingposition(WarehouseLocation value) throws Exception {
        qualityparkingposition = value;
    }

    public String getCompany() throws Exception {
        return company;
    }

    public void setCompany(String value) throws Exception {
        company = value;
    }

    public String getDoctype() throws Exception {
        return doctype;
    }

    public void setDoctype(String value) throws Exception {
        doctype = value;
    }

    public boolean getUsescardboard() throws Exception {
        return usescardboard;
    }

    public void setUsescardboard(boolean value) throws Exception {
        usescardboard = value;
    }

    public boolean getUsesboxes() throws Exception {
        return usesboxes;
    }

    public void setUsesboxes(boolean value) throws Exception {
        usesboxes = value;
    }

    public short getPickingmode() throws Exception {
        return pickingmode;
    }

    public void setPickingmode(short value) throws Exception {
        pickingmode = value;
    }

    public short getQuantitymode() throws Exception {
        return quantitymode;
    }

    public void setQuantitymode(short value) throws Exception {
        quantitymode = value;
    }

    public short getCellstatemode() throws Exception {
        return cellstatemode;
    }

    public void setCellstatemode(short value) throws Exception {
        cellstatemode = value;
    }

    public boolean getMultishootbatches() throws Exception {
        return multishootbatches;
    }

    public void setMultishootbatches(boolean value) throws Exception {
        multishootbatches = value;
    }

    public boolean getClosewithdeliverydate() throws Exception {
        return closewithdeliverydate;
    }

    public void setClosewithdeliverydate(boolean value) throws Exception {
        closewithdeliverydate = value;
    }

    public short getRelatingdoc() throws Exception {
        return relatingdoc;
    }

    public void setRelatingdoc(short value) throws Exception {
        relatingdoc = value;
    }

    public boolean getIncomingconsumptionmaterial() throws Exception {
        return incomingconsumptionmaterial;
    }

    public void setIncomingconsumptionmaterial(boolean value) throws Exception {
        incomingconsumptionmaterial = value;
    }

    public boolean getPiecesonrfinboundclosing() throws Exception{
        return  piecesonrfinboundclosing;
    }

    public boolean getPickingrecountrequired() throws Exception {
        return pickingrecountrequired;
    }

    public void setPickingrecountrequired(boolean value) throws Exception {
        pickingrecountrequired = value;
    }

    public boolean getPackagescountingrequired() throws Exception {
        return packagescountingrequired;
    }

    public void setPackagescountingrequired(boolean value) throws Exception {
        packagescountingrequired = value;
    }

    public boolean getEditincominglogunit() throws Exception {
        return editincominglogunit;
    }

    public void setEditincominglogunit(boolean value) throws Exception {
        editincominglogunit = value;
    }

    public boolean getRfsinglepiecedirectpick() throws Exception {
        return rfsinglepiecedirectpick;
    }

    public void setRfsinglepiecedirectpick(boolean value) throws Exception {
        rfsinglepiecedirectpick = value;
    }

    public boolean getReadhplogunit() throws Exception {
        return readhplogunit;
    }

    public void setReadhplogunit(boolean value) throws Exception {
        readhplogunit = value;
    }

    public boolean isPiecesonrfinboundclosing() {
        return piecesonrfinboundclosing;
    }

    public void setPiecesonrfinboundclosing(boolean piecesonrfinboundclosing) {
        this.piecesonrfinboundclosing = piecesonrfinboundclosing;
    }

    public boolean isPiecesonrfpickingclosing() {
        return piecesonrfpickingclosing;
    }

    public void setPiecesonrfpickingclosing(boolean piecesonrfpickingclosing) {
        this.piecesonrfpickingclosing = piecesonrfpickingclosing;
    }

    public boolean isUsesinboundcontainer() {
        return usesinboundcontainer;
    }

    public void setUsesinboundcontainer(boolean usesinboundcontainer) {
        this.usesinboundcontainer = usesinboundcontainer;
    }

    public boolean isSplitpickingwithcardboards() {
        return splitpickingwithcardboards;
    }

    public void setSplitpickingwithcardboards(boolean splitpickingwithcardboards) {
        this.splitpickingwithcardboards = splitpickingwithcardboards;
    }
}


