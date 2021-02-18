package com.diastock.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class Barcodes {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd", Locale.getDefault());
    List<Identifier> identifiersList = new ArrayList<Identifier>();
    static final int UCC_SEPARATOR = '\u001D';

    public List<Identifier> getIdentifiersList() throws Exception {
        return identifiersList;
    }

    public Barcodes() throws Exception {
        Identifier ai = new Identifier();
        ai.name = "SSCC18";
        ai.AI = ApplicationIdentifiers.SSCC18;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 18;
        identifiersList.add(ai);
        ai = new Identifier();
        ai.name = "SSCC14";
        ai.AI = ApplicationIdentifiers.SSCC14;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 14;
        identifiersList.add(ai);
        ai = new Identifier();
        ai.name = "ARTICLE";
        ai.AI = ApplicationIdentifiers.ARTICLE;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 14;
        identifiersList.add(ai);

        ai = new Identifier();
        ai.name = "PRODUCTION_DATE";
        ai.AI = ApplicationIdentifiers.PRODUCTION_DATE;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 6;
        identifiersList.add(ai);

        ai = new Identifier();
        ai.name = "BATCH";
        ai.AI = ApplicationIdentifiers.BATCH;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 20;
        identifiersList.add(ai);
        ai = new Identifier();
        ai.name = "BBE";
        ai.AI = ApplicationIdentifiers.BBE;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 6;
        identifiersList.add(ai);
        ai = new Identifier();
        ai.name = "EXPIRY";
        ai.AI = ApplicationIdentifiers.EXPIRY;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 6;
        identifiersList.add(ai);
        ai = new Identifier();
        ai.name = "VARIANT";
        ai.AI = ApplicationIdentifiers.VARIANT;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 2;
        identifiersList.add(ai);
        ai = new Identifier();
        ai.name = "SERIALNR";
        ai.AI = ApplicationIdentifiers.SERIALNR;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 20;
        identifiersList.add(ai);
        ai = new Identifier();
        ai.name = "QTY";
        ai.AI = ApplicationIdentifiers.QTY;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 8;
        identifiersList.add(ai);
        ai = new Identifier();
        ai.name = "VCOUNT";
        ai.AI = ApplicationIdentifiers.VCOUNT;
        ai.length = 2;
        ai.standardDataLength = 0;
        ai.maxDataLength = 8;
        identifiersList.add(ai);
        ai = new Identifier();
        ai.name = "ADDITIONAL_PRODUCT";
        ai.AI = ApplicationIdentifiers.ADDITIONAL_PRODUCT;
        ai.length = 3;
        ai.standardDataLength = 0;
        ai.maxDataLength = 30;
        identifiersList.add(ai);
    }

    public static class ApplicationIdentifiers {
        public ApplicationIdentifiers() {
        }

        public static final String SSCC18 = "00";
        public static final String SSCC14 = "02";
        public static final String ARTICLE = "01";
        public static final String BATCH = "10";
        public static final String PRODUCTION_DATE = "11";
        public static final String BBE = "15";
        public static final String EXPIRY = "17";
        public static final String VARIANT = "20";
        public static final String SERIALNR = "21";
        public static final String VCOUNT = "30";
        public static final String QTY = "37";
        public static final String ADDITIONAL_PRODUCT = "240";
    }

    public static class Identifier {
        public Identifier() {
        }

        public String name = "";
        public String AI = "";
        public short length = 0;
        public short standardDataLength = 0;
        public short maxDataLength = 0;
    }

    public static class BarcodeLenghts {
        public BarcodeLenghts() {
        }

        public static final short LOGUNIT = 18;
    }

    public boolean EANUCCSplitter(String barcode) throws Exception {

        if (barcode.indexOf("(") > 0) {
            barcode = barcode.replace("(", (char) UCC_SEPARATOR + "(");
            barcode = "01" + barcode;
            barcode = barcode.replace("(", "").replace(")", "");

            if (barcode.indexOf((char) UCC_SEPARATOR + "17") > 0) {
                String oldDate = barcode.substring(barcode.indexOf((char) UCC_SEPARATOR + "17") + 3, barcode.indexOf((char) UCC_SEPARATOR + "17") + 3 + 8);
                String newDate = oldDate.substring(6, 8) + oldDate.substring(2, 4) + oldDate.substring(0, 2);
                barcode = barcode.replace((char) UCC_SEPARATOR + "17" + oldDate, (char) UCC_SEPARATOR + "17" + newDate);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.set2DigitYearStart((sdf.parse("01/01/2000")));

        boolean staticDecode = (SetupFlags.getInstance().getLogunitAIStart() > 0 || SetupFlags.getInstance().getSkuAIStart() > 0 || SetupFlags.getInstance().getBatchAIStart() > 0 || SetupFlags.getInstance().getVariantAIStart() > 0 || SetupFlags.getInstance().getExpireAIStart() > 0 || SetupFlags.getInstance().getSerialnrAIStart() > 0);
        if (DataExchange.getInstance().getSupplier().getDynamiceanucc() ||
                (SetupFlags.getInstance().getUsesmaxibarcode() &&
                        //DataFunctions.isNullOrEmpty(DataExchange.getInstance().getSupplier().getBarcode()) &&
                        !staticDecode)) {
            while (barcode.length() > 0) {
                if (barcode.indexOf((char) UCC_SEPARATOR) == 0)
                    barcode = barcode.substring(1);

                Identifier id = null;
                Iterator aiIterator = getIdentifiersList().listIterator();

                while (aiIterator.hasNext()) {
                    Identifier dummyIdentifier = (Identifier) aiIterator.next();
                    if (dummyIdentifier.AI.equals(barcode.substring(0, dummyIdentifier.AI.length()))) {
                        id = dummyIdentifier;
                        break;
                    }
                }

                short splitterPosition = 0;

                if (id != null) {
                    if (id.name != null) {
                        splitterPosition = (short) barcode.substring(id.length, Math.min(id.maxDataLength, (barcode.length() - id.length))).indexOf((char) UCC_SEPARATOR);
                        //if (id.standardDataLength > barcode.length()) id.standardDataLength = (short)(barcode.length() - id.length);
                        //string data = barcode.substring(id.length, Math.min((splitterPosition > 0 ? splitterPosition : (id.standardDataLength > 0 ? id.standardDataLength : barcode.length() - id.length)), id.maxDataLength));
                        int dataLen = Math.min((splitterPosition > 0 ? splitterPosition : Math.min((barcode.length() - id.length), id.maxDataLength)), id.maxDataLength);
                        String data = barcode.substring(id.length, id.length + dataLen);
                        barcode = barcode.substring(id.length + data.length() + (splitterPosition > 0 ? 1 : 0)); // .Remove(0, id.length + data.length() + (splitterPosition > 0 ? 1 : 0));
                        String __dummyScrutVar0 = id.name;
                        if (__dummyScrutVar0.equals("SSCC18")) {
                            DataExchange.getInstance().setUnit(data);
                        } else if (__dummyScrutVar0.equals("SSCC14")) {
                            if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle()))
                                DataExchange.getInstance().setCurrentArticle(new Article());

                            DataExchange.getInstance().getCurrentArticle().setSku(data);
                        } else if (__dummyScrutVar0.equals("ARTICLE")) {
                            //if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle())) {
                            DataExchange.getInstance().setCurrentArticle(new Article());
                            DataExchange.getInstance().getCurrentArticle().setSku(data);
                            //AP20190621 --> Save barcode in Generic String 2 for future use in Article Creation
                            DataExchange.getInstance().setGenericString2(data);
                            //}
                        } else if (__dummyScrutVar0.equals("BATCH")) {
                            DataExchange.getInstance().setBatch(data);
                        } else if (__dummyScrutVar0.equals("BBE")) {
                            DataExchange.getInstance().setExpire(dateFormat.parse(data));
                        } else if (__dummyScrutVar0.equals("EXPIRY")) {
                            DataExchange.getInstance().setExpire(dateFormat.parse(data));
                        } else if (__dummyScrutVar0.equals("VARIANT")) {
                            DataExchange.getInstance().setVariantId1(data);
                        } else if (__dummyScrutVar0.equals("SERIALNR")) {
                            DataExchange.getInstance().setSerialUccNumber(data);
                        } else if (__dummyScrutVar0.equals("QTY")) {
                            DataExchange.getInstance().setQty(DataFunctions.readDecimal(data));
                        } else if (__dummyScrutVar0.equals("ADDITIONAL_PRODUCT")) {
                            //if (DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle())) {
                            DataExchange.getInstance().setCurrentArticle(new Article());
                            DataExchange.getInstance().getCurrentArticle().setSku(data);
                            //}
                        }
                    } else
                        return false;
                } else
                    break;
            }
        } else if (barcode.length() > 0 && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getSupplier().getBarcode()) && staticDecode) {
            if (DataExchange.getInstance().getSupplier().getLogunitAIStart() - 1 > 0 && barcode.length() >= DataExchange.getInstance().getSupplier().getLogunitAIStart() && barcode.length() >= DataExchange.getInstance().getSupplier().getLogunitAIStart() - 1 + Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getLogunitAIStart() - 1), DataExchange.getInstance().getSupplier().getLogunitAILen()))
                DataExchange.getInstance().setUnit(barcode.substring(DataExchange.getInstance().getSupplier().getLogunitAIStart() - 1, Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getLogunitAIStart() - 1), DataExchange.getInstance().getSupplier().getLogunitAILen())));

            if (DataExchange.getInstance().getSupplier().getSkuAIStart() - 1 > 0 && barcode.length() >= DataExchange.getInstance().getSupplier().getSkuAIStart() && barcode.length() >= DataExchange.getInstance().getSupplier().getSkuAIStart() - 1 + Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getSkuAIStart() - 1), DataExchange.getInstance().getSupplier().getSkuAILen()))
                DataExchange.getInstance().getCurrentArticle().setSku(barcode.substring(DataExchange.getInstance().getSupplier().getSkuAIStart() - 1, Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getSkuAIStart() - 1), DataExchange.getInstance().getSupplier().getSkuAILen())));

            if (DataExchange.getInstance().getSupplier().getBatchAIStart() - 1 > 0 && barcode.length() >= DataExchange.getInstance().getSupplier().getBatchAIStart() && barcode.length() >= DataExchange.getInstance().getSupplier().getBatchAIStart() - 1 + Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getBatchAIStart() - 1), DataExchange.getInstance().getSupplier().getBatchAILen()))
                DataExchange.getInstance().setBatch(barcode.substring(DataExchange.getInstance().getSupplier().getBatchAIStart() - 1, Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getBatchAIStart() - 1), DataExchange.getInstance().getSupplier().getBatchAILen())));

            if (DataExchange.getInstance().getSupplier().getVariantAIStart() - 1 > 0 && barcode.length() >= DataExchange.getInstance().getSupplier().getVariantAIStart() && barcode.length() >= DataExchange.getInstance().getSupplier().getVariantAIStart() - 1 + Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getVariantAIStart() - 1), DataExchange.getInstance().getSupplier().getVariantAILen()))
                DataExchange.getInstance().setVariantId1(barcode.substring(DataExchange.getInstance().getSupplier().getVariantAIStart() - 1, Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getVariantAIStart() - 1), DataExchange.getInstance().getSupplier().getVariantAILen())));

            if (DataExchange.getInstance().getSupplier().getSerialnrAIStart() - 1 > 0 && barcode.length() >= DataExchange.getInstance().getSupplier().getSerialnrAIStart() && barcode.length() >= DataExchange.getInstance().getSupplier().getSerialnrAIStart() - 1 + Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getSerialnrAIStart() - 1), DataExchange.getInstance().getSupplier().getSerialnrAILen()))
                DataExchange.getInstance().setSerialNumbers(barcode.substring(DataExchange.getInstance().getSupplier().getSerialnrAIStart() - 1, Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getSerialnrAIStart() - 1), DataExchange.getInstance().getSupplier().getSerialnrAILen())) + "|");

            if (DataExchange.getInstance().getSupplier().getExpireAIStart() - 1 > 0 && barcode.length() >= DataExchange.getInstance().getSupplier().getExpireAIStart() && barcode.length() >= DataExchange.getInstance().getSupplier().getExpireAIStart() - 1 + Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getExpireAIStart() - 1), DataExchange.getInstance().getSupplier().getExpireAILen()))
                DataExchange.getInstance().setExpire(dateFormat.parse(barcode.substring(DataExchange.getInstance().getSupplier().getExpireAIStart() - 1, Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getExpireAIStart() - 1), DataExchange.getInstance().getSupplier().getExpireAILen()))));

        } else if (barcode.length() > 0 && SetupFlags.getInstance().getLogunitAIStart() > 0 || SetupFlags.getInstance().getSkuAIStart() > 0 || SetupFlags.getInstance().getBatchAIStart() > 0 || SetupFlags.getInstance().getVariantAIStart() > 0 || SetupFlags.getInstance().getExpireAIStart() > 0 || SetupFlags.getInstance().getSerialnrAIStart() > 0) {
            if (SetupFlags.getInstance().getLogunitAIStart() - 1 > 0 && barcode.length() >= SetupFlags.getInstance().getLogunitAIStart() && barcode.length() >= SetupFlags.getInstance().getLogunitAIStart() - 1 + Math.min(barcode.length() - (SetupFlags.getInstance().getLogunitAIStart() - 1), SetupFlags.getInstance().getLogunitAILen()))
                DataExchange.getInstance().setUnit(barcode.substring(SetupFlags.getInstance().getLogunitAIStart() - 1, Math.min(barcode.length() - (SetupFlags.getInstance().getLogunitAIStart() - 1), SetupFlags.getInstance().getLogunitAILen())));

            if (SetupFlags.getInstance().getSkuAIStart() - 1 > 0 && barcode.length() >= SetupFlags.getInstance().getSkuAIStart() && barcode.length() >= SetupFlags.getInstance().getSkuAIStart() - 1 + Math.min(barcode.length() - (SetupFlags.getInstance().getSkuAIStart() - 1), SetupFlags.getInstance().getSkuAILen()))
                DataExchange.getInstance().getCurrentArticle().setSku(barcode.substring(SetupFlags.getInstance().getSkuAIStart() - 1, Math.min(barcode.length() - (SetupFlags.getInstance().getSkuAIStart() - 1), SetupFlags.getInstance().getSkuAILen())));

            if (SetupFlags.getInstance().getBatchAIStart() - 1 > 0 && barcode.length() >= SetupFlags.getInstance().getBatchAIStart() && barcode.length() >= SetupFlags.getInstance().getBatchAIStart() - 1 + Math.min(barcode.length() - (SetupFlags.getInstance().getBatchAIStart() - 1), SetupFlags.getInstance().getBatchAILen()))
                DataExchange.getInstance().setBatch(barcode.substring(SetupFlags.getInstance().getBatchAIStart() - 1, Math.min(barcode.length() - (SetupFlags.getInstance().getBatchAIStart() - 1), SetupFlags.getInstance().getBatchAILen())));

            if (SetupFlags.getInstance().getVariantAIStart() - 1 > 0 && barcode.length() >= SetupFlags.getInstance().getVariantAIStart() && barcode.length() >= SetupFlags.getInstance().getVariantAIStart() - 1 + Math.min(barcode.length() - (SetupFlags.getInstance().getVariantAIStart() - 1), SetupFlags.getInstance().getVariantAILen()))
                DataExchange.getInstance().setVariantId1(barcode.substring(SetupFlags.getInstance().getVariantAIStart() - 1, Math.min(barcode.length() - (SetupFlags.getInstance().getVariantAIStart() - 1), SetupFlags.getInstance().getVariantAILen())));

            if (SetupFlags.getInstance().getSerialnrAIStart() - 1 > 0 && barcode.length() >= SetupFlags.getInstance().getSerialnrAIStart() && barcode.length() >= SetupFlags.getInstance().getSerialnrAIStart() - 1 + Math.min(barcode.length() - (SetupFlags.getInstance().getSerialnrAIStart() - 1), SetupFlags.getInstance().getSerialnrAILen()))
                DataExchange.getInstance().setSerialNumbers(barcode.substring(SetupFlags.getInstance().getSerialnrAIStart() - 1, Math.min(barcode.length() - (SetupFlags.getInstance().getSerialnrAIStart() - 1), SetupFlags.getInstance().getSerialnrAILen())) + "|");

            if (SetupFlags.getInstance().getExpireAIStart() - 1 > 0 && barcode.length() >= SetupFlags.getInstance().getExpireAIStart() && barcode.length() >= SetupFlags.getInstance().getExpireAIStart() - 1 + Math.min(barcode.length() - (SetupFlags.getInstance().getExpireAIStart() - 1), SetupFlags.getInstance().getExpireAILen()))
                DataExchange.getInstance().setExpire(dateFormat.parse(barcode.substring(SetupFlags.getInstance().getExpireAIStart() - 1, Math.min(barcode.length() - (SetupFlags.getInstance().getExpireAIStart() - 1), SetupFlags.getInstance().getExpireAILen()))));

        }

        return true;
    }

    public String getHPSerianNr(String barcode) throws Exception {
        String outValue = barcode;
        int len = barcode.length();
        if (len == 9) {
            outValue = barcode;
        } else if (len == 10) {
            if (barcode.indexOf("-") > 0)
                outValue = barcode.replace("-", "");
            else
                outValue = barcode.substring(0, 9);
        } else if (len == 11) {
            if (barcode.indexOf("-") > 0)
                outValue = (barcode.replace("-", "")).substring(0, 9);
            else
                outValue = barcode.substring(2, 9);
        } else if (len == 12) {
            if (barcode.indexOf("-") > 0)
                outValue = (barcode.replace("-", "")).substring(2, 9);
            else
                outValue = barcode.substring(2, 9);
        } else if (len == 13) {
            outValue = barcode.substring(2, 4) + barcode.substring(7, 5);
        }

        return outValue;
    }

    public boolean serialSplitter(String barcode) throws Exception {
        if (barcode.length() > 0 && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getSupplier().getBarcode())) {
            if (DataExchange.getInstance().getSupplier().getSerialnrAIStart() - 1 > 0 && barcode.length() >= DataExchange.getInstance().getSupplier().getSerialnrAIStart() && barcode.length() >= DataExchange.getInstance().getSupplier().getSerialnrAIStart() - 1 + Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getSerialnrAIStart() - 1), DataExchange.getInstance().getSupplier().getSerialnrAILen())) {
                DataExchange.getInstance().setSerialNumbers(barcode.substring(DataExchange.getInstance().getSupplier().getSerialnrAIStart() - 1, Math.min(barcode.length() - (DataExchange.getInstance().getSupplier().getSerialnrAIStart() - 1), DataExchange.getInstance().getSupplier().getSerialnrAILen())));
                return true;
            } else
                return false;
        } else if (barcode.length() > 0 && SetupFlags.getInstance().getSerialnrAIStart() > 0) {
            if (SetupFlags.getInstance().getSerialnrAIStart() - 1 > 0 && barcode.length() >= SetupFlags.getInstance().getSerialnrAIStart() && barcode.length() >= SetupFlags.getInstance().getSerialnrAIStart() - 1 + Math.min(barcode.length() - (SetupFlags.getInstance().getSerialnrAIStart() - 1), SetupFlags.getInstance().getSerialnrAILen())) {
                DataExchange.getInstance().setSerialNumbers(barcode.substring(SetupFlags.getInstance().getSerialnrAIStart() - 1, Math.min(barcode.length() - (SetupFlags.getInstance().getSerialnrAIStart() - 1), SetupFlags.getInstance().getSerialnrAILen())));
                return true;
            } else
                return false;
        } else
            return false;
    }

}
