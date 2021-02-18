package com.diastock.app;

import java.util.ArrayList;

public class ActivityItem {

    public String label ="";
    public int row = 0;
    public int maxlen = 0;
    public int datatype = 0;
    public String defaultValue = "";
    public boolean showonly;
    public String dataFieldName = null;
    public boolean allowScanner = true;
    public int sequence = 0;
    public String lastValue = "";
    public ArrayList<SelectionItem> selectionItems;

    public ActivityItem(int sequence, String label, int maxlen, int dataType, String defaultValue, boolean showOnly, String dataFieldName, boolean allowScanner)
    {
        this.label = label;
        this.maxlen = maxlen;
        this.datatype = dataType;
        this.defaultValue = defaultValue;
        this.showonly = showOnly;
        this.dataFieldName = dataFieldName;
        this.allowScanner = allowScanner;
        this.sequence = sequence;
    }

    class SelectionItem
    {
        public String code;
        public String description;

        public SelectionItem(String code, String description)
        {
            this.code = code;
            this.description = description;
        }
    }

}
   

