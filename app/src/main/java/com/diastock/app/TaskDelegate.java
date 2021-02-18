package com.diastock.app;

public interface TaskDelegate
{
    public void taskCompletionResult(String result, int step) throws Exception;
}
