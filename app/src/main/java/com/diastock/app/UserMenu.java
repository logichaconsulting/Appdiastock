package com.diastock.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//[Serializable]
public class UserMenu {
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static UserMenu userMenu;
    private List<MenuItem> menuItem;
    private String message;
    private SetupFlags setupFlags;
    private Date sysdate = new Date();
    private static final String splitter = "\u0126";

    public static UserMenu getInstance() throws Exception {
        if (userMenu == null) userMenu = new UserMenu();
        return userMenu;
    }

    public static void set(UserMenu menu) throws Exception {
        userMenu = menu;
    }

    public String getMessage() throws Exception {
        return message;
    }

    public void setMessage(String value) throws Exception {
        message = value;
    }

    public List<MenuItem> getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(List<MenuItem> menuItem) {
        this.menuItem = menuItem;
    }

    public UserMenu() throws Exception {
        menuItem = new ArrayList<MenuItem>();
        setupFlags = SetupFlags.getInstance();
    }

    public void format() throws Exception {
        menuItem = new ArrayList<MenuItem>();
        setupFlags = SetupFlags.getInstance();
    }

    public void addItem(MenuItem item) throws Exception {
        menuItem.add(item);
    }

    public MenuItem getMenuItem(int menuItemIndex) throws Exception {
        if (menuItemIndex < menuItem.size())
            return menuItem.get(menuItemIndex);
        else
            return null;
    }

    public int getMenuItems() throws Exception {
        return menuItem.size();
    }

    public SetupFlags getSetupFlags() throws Exception {
        return setupFlags;
    }

    public Date getSysdate() throws Exception {
        return sysdate;
    }

    public void setSysdate(Date value) throws Exception {
        sysdate = value;
    }

    private String FieldConvert(Object o) {
        if (o == null)
            return "" + splitter;
        else if (o.getClass() == String.class)
            return (String) o + splitter;
        else if (o.getClass() == Date.class)
            return format.format((Date) o) + splitter;
        else
            return o.toString() + splitter;
    }

    public void DeSerialize(String msg) throws Exception {
        this.format();
        String[] values = msg.split(splitter);
        int index = 0;

        //private SetupFlags setupFlags;

        message = values[index++];
        setupFlags.setUseslogisticunit(Boolean.parseBoolean(values[index++]));
        setupFlags.setUsescontainer(Boolean.parseBoolean(values[index++]));
        setupFlags.setSkuOnLogisticUnit(Boolean.parseBoolean(values[index++]));
        setupFlags.setMarkshippingpackages(Boolean.parseBoolean(values[index++]));
        setupFlags.setUsesmaxibarcode(Boolean.parseBoolean(values[index++]));
        setupFlags.setLogunitAIStart(Integer.parseInt((values[index++])));
        setupFlags.setSkuAIStart(Integer.parseInt(values[index++]));
        setupFlags.setBatchAIStart(Integer.parseInt(values[index++]));
        setupFlags.setVariantAIStart(Integer.parseInt(values[index++]));
        setupFlags.setSerialnrAIStart(Integer.parseInt(values[index++]));
        setupFlags.setExpireAIStart(Integer.parseInt(values[index++]));
        setupFlags.setLogunitAILen(Integer.parseInt(values[index++]));
        setupFlags.setSkuAILen(Integer.parseInt(values[index++]));
        setupFlags.setBatchAILen(Integer.parseInt(values[index++]));
        setupFlags.setVariantAILen(Integer.parseInt(values[index++]));
        setupFlags.setSerialnrAILen(Integer.parseInt(values[index++]));
        setupFlags.setExpireAILen(Integer.parseInt(values[index++]));
        setupFlags.setReceivemaxdocqty(Boolean.parseBoolean(values[index++]));
        setupFlags.setPickingswitchposition(Boolean.parseBoolean(values[index++]));
        setupFlags.setWeightonrflistclosing(Boolean.parseBoolean(values[index++]));
        setupFlags.setVolumeonrflistclosing(Boolean.parseBoolean(values[index++]));
        setupFlags.setStockonlastlocation(Boolean.parseBoolean(values[index++]));
        setupFlags.setVerifybatch(Boolean.parseBoolean(values[index++]));
        setupFlags.setVerifybbe(Boolean.parseBoolean(values[index++]));
        setupFlags.setSerialnrlength(Integer.parseInt(values[index++]));
        setupFlags.setSerialnrprefixes(values[index++]);
        setupFlags.setInventoryusesserialnr(Boolean.parseBoolean(values[index++]));
        setupFlags.setAutoparcellabels(Boolean.parseBoolean(values[index++]));
        setupFlags.setPallettypescounting(Boolean.parseBoolean(values[index++]));
        setupFlags.setDoubleCountOnInventory(Boolean.parseBoolean(values[index++]));
        setupFlags.setFastinventorybylogunit(Boolean.parseBoolean(values[index++]));
        setupFlags.setInboundCreateArticle(Boolean.parseBoolean(values[index++]));
        setupFlags.setOneShotUnloading(Boolean.parseBoolean(values[index++]));
        setupFlags.setLenghtManager(values[index++]);
        setupFlags.setPrintinboundseriallabel(Boolean.parseBoolean(values[index++]));
        setupFlags.setRfusesbarcodedefaultqty(Boolean.parseBoolean(values[index++]));
        setupFlags.setRfautoinsertarrivalorder(Boolean.parseBoolean(values[index++]));

        sysdate = format.parse(values[index++]);

        //private List<MenuItem> menuItem;
        for (int i = index; i < values.length - 1; i += 14) {
            MenuItem item = new MenuItem();

            item.setDefDownloadCausal(values[i]);
            item.setDefLoadCausal(values[i + 1]);
            item.setFunctionDescription(values[i + 2]);
            item.setFunctionName(values[i + 3]);
            item.setFuncionNumber(Integer.parseInt(values[i + 4]));
            item.setPaperless(Boolean.parseBoolean(values[i + 5]));
            item.setDefFromLocation(Integer.parseInt(values[i + 6]));
            item.setDefToLocation(Integer.parseInt(values[i + 7]));
            item.setLogunitSwitch(Boolean.parseBoolean(values[i + 8]));
            item.setBatchexpireswitch(Boolean.parseBoolean(values[i + 9]));
            item.setVariantIdSwitch(Boolean.parseBoolean(values[i + 10]));
            item.setAskreservedgoods(Boolean.parseBoolean(values[i + 11]));
            item.setSuggestexpirationdate(Boolean.parseBoolean(values[i + 12]));
            item.setOneshotunloading(Boolean.parseBoolean(values[i + 13]));
            menuItem.add(item);
        }

    }

}


