package com.diastock.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public class Presentation {

    public static void ShowAttributes(DisplayFragment frm, DataExchange dataExchange, boolean showContainer, boolean showUnit, boolean noautoscroll) throws Exception {
        //Localizer localizer = Localizer.getInstance();

        if (SetupFlags.getInstance().getUsescontainer() && showContainer && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getContainer())) {
            frm.AddDisplayRow(DataExchange.getInstance().getContainer(), "Container", false);
        }

        if (SetupFlags.getInstance().getUseslogisticunit() && showUnit && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getUnit())) {
            frm.AddDisplayRow(DataExchange.getInstance().getUnit(), "Unità Logistica", false);
        }

        if (DataExchange.getInstance().getCurrentArticle().getUsesbatch() && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getBatch())) {
            frm.AddDisplayRow(DataExchange.getInstance().getBatch(), "Lotto", false);
        }

        if (DataExchange.getInstance().getCurrentArticle().getUsesexpire() && !DataExchange.getInstance().getExpire().toString().equals((DataFunctions.getEmptyDate().toString()))) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            frm.AddDisplayRow(dateFormat.format(DataExchange.getInstance().getExpire()), "Scadenza", false);
        }

        if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 1 && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId1())) {
            frm.AddDisplayRow(DataExchange.getInstance().getVariantId1(), "Variante 1", false);
        }

        if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 2 && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId2())) {
            frm.AddDisplayRow(DataExchange.getInstance().getVariantId1(), "Variante 2", false);
        }

        if (DataExchange.getInstance().getCurrentArticle().getUsesvariant() >= 3 && !DataFunctions.isNullOrEmpty(DataExchange.getInstance().getVariantId3())) {
            frm.AddDisplayRow(DataExchange.getInstance().getVariantId1(), "Variante 3", false);
        }

        return;
    }

    public static void ShowFromLocation(DisplayFragment frm, WarehouseLocation location, boolean showStandardLabel) throws Exception {
        ShowFromLocation(frm, location, showStandardLabel, "Posizione");
    }

    public static void ShowToLocation(DisplayFragment frm, WarehouseLocation location, boolean showStandardLabel) throws Exception {
        ShowToLocation(frm, location, showStandardLabel, "Posizione");
    }

    public static void ShowFromLocation(DisplayFragment frm, WarehouseLocation location, boolean showStandardLabel, String label) throws Exception {
        label = showStandardLabel ? "Posizione Partenza" : label;
        frm.AddDisplayRow(location.getInputPosition(), label, false);
    }

    public static void ShowToLocation(DisplayFragment frm, WarehouseLocation location, boolean showStandardLabel, String label) throws Exception {
        label = showStandardLabel ? "Posizione Arrivo" : label;
        frm.AddDisplayRow(location.getInputPosition(), label, false);
    }

    public static void showGenericCoordinates(DisplayFragment frm, DataExchange instance, boolean b) throws Exception {
        if (instance.getGenericCoordinates().trim().length() >= 17) {
            String s =
                    instance.getGenericCoordinates().trim().substring(0, 5) + " " +
                            instance.getGenericCoordinates().trim().substring(5, 10) + " " +
                            instance.getGenericCoordinates().trim().substring(10, 13) + " " +
                            instance.getGenericCoordinates().trim().substring(13, 16) + " " +
                            instance.getGenericCoordinates().trim().substring(16, 19);


            frm.AddDisplayRow(s, frm.getResources().getString(R.string.ID000006), false);
        }
    }

    public static void showSuggestedLocations(DisplayFragment frm, DataExchange instance) throws Exception {
        if (instance.getSuggestedToLocations().trim().length() >= 17) {

            String[] positions = instance.getSuggestedToLocations().split(";");
            String s = null;

            for (String position : positions) {
                if (position.length() > 0) {
                    String[] data = position.split(Pattern.quote("|"));

                    String location = data[0];
                    double qty = DataFunctions.readDecimal(data[1]);

                    if (location.trim().length() >= 17) {
                        s =
                                location.trim().substring(0, 5) + " " +
                                        location.trim().substring(5, 10) + " " +
                                        location.trim().substring(10, 13) + " " +
                                        location.trim().substring(13, 16) + " " +
                                        location.trim().substring(16, 19) + " -> " + String.format("%.0f", qty);
                    }
                }
            }

            frm.AddDisplayRow(s, frm.getResources().getString(R.string.ID000130) + " -> " + frm.getResources().getString(R.string.ID000131), false);
        }
    }

    public static void showAttributes(ArticleInquiry articleInquiry, DataExchange instance, boolean b, boolean b1, boolean b2) {
    }
}
