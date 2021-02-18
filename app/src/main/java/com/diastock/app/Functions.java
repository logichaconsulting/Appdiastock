package com.diastock.app;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import static com.diastock.app.InputArea.TYPE_EANUCC;
import static com.diastock.app.InputArea.TYPE_LOCATION;
import static com.diastock.app.InputArea.TYPE_LOGUNIT;
import static com.diastock.app.InputArea.TYPE_SERIAL;

public class Functions {


    public static BooleanMessage invalidLenght(String value, String pattern, InputType type, Context context) {
        String message = "";

        BooleanMessage result = null;
        try {
            int min = 0, max = 0;
            result = new BooleanMessage(false,"");

            String[] lenghts = pattern.split(";");

            switch (type) {
                case Batch:
                    max = Integer.parseInt(lenghts[0]);
                    min = Integer.parseInt(lenghts[1]);
                    break;
                case Variantid1:
                    max = Integer.parseInt(lenghts[2]);
                    min = Integer.parseInt(lenghts[3]);
                    break;
                case Variantid2:
                    max = Integer.parseInt(lenghts[4]);
                    min = Integer.parseInt(lenghts[5]);
                    break;
                case Variantid3:
                    max = Integer.parseInt(lenghts[6]);
                    min = Integer.parseInt(lenghts[7]);
                    break;
                case Reserved:
                    max = Integer.parseInt(lenghts[8]);
                    min = Integer.parseInt(lenghts[8]);
                    break;
                case Unit:
                    max = Integer.parseInt(lenghts[10]);
                    min = Integer.parseInt(lenghts[11]);
                    break;
            }

            if (min > 0 && value.length() < min) {
                //Lunghezza minima per {0} --> {1} caratteri
                message = String.format(context.getResources().getString(R.string.ID000207), getInputTypeName(type, context), min);
                result = new BooleanMessage(true,message);
            }
            if (max > 0 && value.length() > max) {
                //Lunghezza massima per {0} --> {1} caratteri
                message = String.format(context.getResources().getString(R.string.ID000208), getInputTypeName(type, context), max);
                result = new BooleanMessage(true,message);
            }
        } catch (Exception e) {
            message = e.getMessage();
            result = new BooleanMessage(true,message);
        }

        return result;
    }

    private static String getInputTypeName(InputType type, Context context) {
        switch (type) {
            case Batch:
                return context.getResources().getString(R.string.ID000007);
            case Reserved:
                return context.getResources().getString(R.string.ID000161);
            case Unit:
                return context.getResources().getString(R.string.ID000005);
            case Variantid1:
                return context.getResources().getString(R.string.ID000009);
            case Variantid2:
                return context.getResources().getString(R.string.ID000010);
            case Variantid3:
                return context.getResources().getString(R.string.ID000011);
            default:
                return "";
        }
    }


    public static boolean ValidateBarcode(String barcode, ActivityItem currentItem, InputArea input) throws Exception {
        barcode = barcode.indexOf('\r') > 0 ? StringUtils.remove(barcode, barcode.substring(barcode.indexOf('\r'))) : barcode;
        barcode = barcode.indexOf(']') == 0 ? barcode.substring(3) : barcode;

        char[] spaceChar = {' '};
        barcode = StringUtils.stripEnd(barcode, " ");

        if (currentItem.datatype == TYPE_LOGUNIT &&
                barcode.length() > Barcodes.BarcodeLenghts.LOGUNIT &&
                barcode.substring(0, 2) == Barcodes.ApplicationIdentifiers.SSCC18) {
            barcode = barcode.substring(2);
        }

        if (currentItem.datatype == TYPE_EANUCC) {
            Barcodes barcodes = new Barcodes();

            if (!barcodes.EANUCCSplitter(barcode)) {
                return false;
            }

            if (barcode.indexOf((char) Barcodes.UCC_SEPARATOR) < 0 &&
                    (DataExchange.getInstance().getCurrentArticle() == null || DataFunctions.isNullOrEmpty(DataExchange.getInstance().getCurrentArticle().getSku()))) {
                DataExchange.getInstance().setCurrentArticle(new Article());
                DataExchange.getInstance().getCurrentArticle().setSku(barcode);
            }
        }

        if (currentItem.datatype == TYPE_SERIAL) {

            // TODO
//            Barcodes barcodes = new Barcodes();
//
//            if (barcodes.SerialSplitter(barcode))
//                barcode = DataExchange.getInstance().getSerialNumbers();

            return true;
        }

        // Compatibility with older system
        if (currentItem.datatype == TYPE_LOCATION && barcode.length() == 12) {
            String MF = StringUtils.leftPad(barcode.substring(0, 1), 5, '0');
            String ML = StringUtils.leftPad(barcode.substring(2, 3), 5, '0');
            String SC = StringUtils.leftPad(barcode.substring(4, 6), 3, '0');
            String PO = StringUtils.leftPad(barcode.substring(7, 9), 3, '0');
            String PI = StringUtils.leftPad(barcode.substring(10, 11), 3, '0');
            barcode = MF + ML + SC + PO + PI;
        }
        return true;
    }

    public static boolean isNullOrEmpty(Object val) throws Exception {
        return DataFunctions.isNullOrEmpty(val);
    }

    public static WarehouseLocation locationCopy(WarehouseLocation fromLocation) throws Exception {
        WarehouseLocation location = new WarehouseLocation();

        location.setBuilding(fromLocation.getBuilding());
        location.setDepartment(fromLocation.getDepartment());
        location.setEntirePosition(fromLocation.getEntirePosition());
        location.setFloor(fromLocation.getFloor());
        location.setPosition(fromLocation.getPosition());
        location.setPositionCode(fromLocation.getPositionCode());
        location.setShelf(fromLocation.getShelf());

        return location;
    }

    public static void InitializeWithDefaultQty(DisplayFragment base, ActivityItem qtyItem)
    {
        try {
            base.InitializeItem(qtyItem, (SetupFlags.getInstance().isRfusesbarcodedefaultqty() ? String.format("%.0f", DataExchange.getInstance().getCurrentArticle().getDefaultqty()) : ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    public static String Encrypt(String clearword)
    {
        String Password = "ahcigol";

        byte[] clearBytes = new byte[0];
        try {
            clearBytes = clearword.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        PasswordDeriveBytes pdb = new PasswordDeriveBytes(Password,
                new byte[] {0x49, 0x76, 0x61, 0x6e, 0x20, 0x4d,
                        0x65, 0x64, 0x76, 0x65, 0x64, 0x65, 0x76});

        byte[] encryptedData = Encrypt(clearBytes, pdb.getBytes(32), pdb.getBytes(16));

        return Convert.ToBase64String(encryptedData);
    }

    public static byte[] Encrypt(byte[] clearData, byte[] Key, byte[] IV)
    {
        Stre ms = new MemoryStream();

        Rijndael alg = Rijndael.Create();

        alg.Key = Key;
        alg.IV = IV;

        CryptoStream cs = new CryptoStream(ms, alg.CreateEncryptor(), CryptoStreamMode.Write);

        cs.Write(clearData, 0, clearData.Length);

        cs.Close();

        byte[] encryptedData = ms.ToArray();

        return encryptedData;
    }*/
}
