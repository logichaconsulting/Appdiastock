package com.diastock.app;

import android.util.Log;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataFunctions {
    static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static float readDecimal(String o) throws Exception {
        NumberFormat nfi = NumberFormat.getNumberInstance(Locale.getDefault());

        o = o.replace(',', '.');

        if (!isNullOrEmpty(o) && o.indexOf(".") > 0)
            nfi = NumberFormat.getNumberInstance(Locale.UK);
        else if (!isNullOrEmpty(o) && o.indexOf(",") > 0)
            nfi = NumberFormat.getNumberInstance(Locale.ITALY);

        if (isNullOrEmpty(o))
            return 0;
        else
            return Float.parseFloat(o);
    }

    public static double readDouble(String o) throws Exception {
        NumberFormat nfi = NumberFormat.getNumberInstance(Locale.getDefault());

        o = o.replace(',', '.');

        if (!isNullOrEmpty(o) && o.indexOf(".") > 0)
            nfi = NumberFormat.getNumberInstance(Locale.UK);
        else if (!isNullOrEmpty(o) && o.indexOf(",") > 0)
            nfi = NumberFormat.getNumberInstance(Locale.ITALY);

        if (isNullOrEmpty(o))
            return 0;
        else
            return Float.parseFloat(o);
    }

    public static int readInt(String o) throws Exception {
        NumberFormat nfi = NumberFormat.getNumberInstance(Locale.getDefault());

        o = o.replace(',', '.');

        if (!isNullOrEmpty(o) && o.indexOf(".") > 0)
            nfi = NumberFormat.getNumberInstance(Locale.UK);
        else if (!isNullOrEmpty(o) && o.indexOf(",") > 0)
            nfi = NumberFormat.getNumberInstance(Locale.ITALY);

        if (isNullOrEmpty(o))
            return 0;
        else
            return Integer.parseInt(o);
    }

    public static boolean isNullOrEmpty(Object o) throws Exception {
        boolean result = false;
        if (o == null || o.toString().equals("") || isNewDateTime(o))
            result = true;

        return result;
    }

    public static boolean isStringNewDateTime(String obj) throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date d = sdf.parse(obj);
            Date t = DataFunctions.getEmptyDate();

            boolean res = d.toString().equals(t.toString());

            return res;
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
            return false;
        }

    }


    public static boolean isNewDateTime(Object obj) throws Exception {
        if (obj instanceof Date) {
            if (((Date) obj).equals(DataFunctions.getEmptyDate()))
                return true;
            else
                return false;
        } else
            return false;
    }

    public static Date getEmptyDate() {
        /*Calendar cal = Calendar.getInstance();
        cal.set(1, 0, 1, 0, 0, 0);
        return cal.getTime();*/
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse("0001-01-01 00:00:00");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static Date readDateTime(String o) throws Exception {
        if (isNullOrEmpty(o))
            return getEmptyDate();
        else
            return dateFormat.parse(o);
    }

    public static boolean CheckSerialNrPrefix(String serialnr, String[] prefixes) {
        for (String prefix : prefixes) {
            if (!prefix.equals("") && serialnr.toUpperCase().substring(0, prefix.length()).equals(prefix.toUpperCase()))
                return true;
        }

        return false;
    }

    public static boolean CheckSerialNrLength(String serialnr, int length) {
        if (length > 0) {
            if (serialnr.length() == length)
                return true;
            else
                return false;
        } else
            return true;
    }
}


