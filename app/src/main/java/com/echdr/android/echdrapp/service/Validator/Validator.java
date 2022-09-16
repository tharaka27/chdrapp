package com.echdr.android.echdrapp.service.Validator;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

public class Validator {
    private  String TAG = "";
    private Context context;

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected void CreateAlertDialog(String ErrorMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(ErrorMessage);
        builder.setCancelable(true);
        Log.e(TAG, ErrorMessage);

        builder.setNegativeButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
