package com.diastock.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class AlertMessageBuilder {

	AlertDialog.Builder builder;

	public ResponseType getResponseType() {
		return responseType;
	}

	ResponseType responseType = ResponseType.OK;

	public static enum Severity
	{
		ERROR,
		WARNING,
		INFO,
		QUESTION
	}

	public static enum ResponseType
	{
		OK,
		NO
	}


	public AlertMessageBuilder()
	{

	}

	public AlertDialog.Builder BuildDialog(String headerMessage, String message, Severity severity, Context context) throws Exception
	{
		// make a handler that throws a runtime exception when a message is received
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message mesg) {
				throw new RuntimeException();
			}
		};

		builder = new AlertDialog.Builder(context);
		builder.setTitle(headerMessage);
		builder.setMessage(message);
		builder.setCancelable(false);

		if (severity == Severity.ERROR)
			builder.setIcon(R.drawable.ic_error);
		else
			builder.setIcon(R.drawable.ic_warning);

		builder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						responseType = ResponseType.OK;
						handler.sendMessage(handler.obtainMessage());
					}
				});

		if (severity == Severity.QUESTION) {
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					responseType = ResponseType.NO;
					handler.sendMessage(handler.obtainMessage());
				}
			});
		}
		builder.setCancelable(false);

		return builder;
	}

	public void Show()
	{

		builder.show();
		// loop till a runtime exception is triggered.
		try { Looper.loop(); }
		catch(RuntimeException e2) {}
	}
}


