package com.diastock.app;

import android.content.Context;

import androidx.appcompat.widget.AppCompatEditText;

import android.util.AttributeSet;

import java.util.ArrayList;

public class InputArea extends AppCompatEditText {

    private short dataType;
    private int maxLength;
    private boolean edited;
    private ArrayList<ActivityItem.SelectionItem> selectionItems;

    public static final short TYPE_TEXT = 0;
    public static final short TYPE_QTY = 1;
    public static final short TYPE_LOCATION = 2;
    public static final short TYPE_PASSWORD = 3;
    public static final short TYPE_DATE = 4;
    public static final short TYPE_LOGUNIT = 5;
    public static final short TYPE_SKU = 6;
    public static final short TYPE_EANUCC = 7;
    public static final short TYPE_COMPANY = 8;
    public static final short TYPE_SKIN = 9;
    public static final short TYPE_NUMERIC = 10;
    public static final short TYPE_SERIAL_STATE = 11;
    public static final short TYPE_ADD_REMOVE = 12;
    public static final short TYPE_UNIT_DESTINATION_CHECK = 13;
    public static final short TYPE_CAUSAL = 14;
    public static final short TYPE_SERIAL = 15;


    public final int KEY_ESC = '\u001b';
    public final int KEY_BACK_SPACE = '\u0008';

    public short getDataType() {
        return dataType;
    }

    public void setDataType(short dataType) {
        this.dataType = dataType;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public ArrayList<ActivityItem.SelectionItem> getSelectionItems() {
        return selectionItems;
    }

    public void setSelectionItems(ArrayList<ActivityItem.SelectionItem> selectionItems) {
        this.selectionItems = selectionItems;
    }


    public InputArea(Context context) {
        super(context);
        init();

    }

    public InputArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InputArea(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setAttribute(Context context,AttributeSet attrs)
    {


    }
//    public InputArea(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    private  void init()
    {
        this.requestFocus();
    }



}
