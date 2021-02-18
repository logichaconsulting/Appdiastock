package com.diastock.app;

public interface BaseActivityInterface {
    abstract void AcceptText(String data, int goodResponse);
    //abstract void AcceptText(String data, boolean goodResponse);
    abstract void OnBackAction();

    abstract void CreateAction1();
    abstract void CreateAction2();
}
