package com.diastock.app;

import android.provider.BaseColumns;

public final class UserLoggedContract {
    private UserLoggedContract() {}

    /* Inner class that defines the table contents */
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_USER = "userid";
    }
}
