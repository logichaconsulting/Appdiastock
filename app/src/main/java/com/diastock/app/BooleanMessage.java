package com.diastock.app;

public class BooleanMessage{
    private final boolean result;
    private final String message;

    public BooleanMessage(boolean result, String message) {
        this.result = result;
        this.message= message;
    }

    public boolean getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
