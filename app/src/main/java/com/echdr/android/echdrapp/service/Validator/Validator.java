package com.echdr.android.echdrapp.service.Validator;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.echdr.android.echdrapp.R;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    protected boolean checkIfAgeIsFive(TextView textView_Date,
                                       TrackedEntityAttributeValue birthday){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dob = null;
        try {
            dob = formatter.parse(birthday.value());
            Calendar c = Calendar.getInstance();
            c.setTime(dob);
            Date event_date = formatter.parse(textView_Date.getText().toString());
            Calendar td = Calendar.getInstance();
            td.setTime(event_date);
            long minimum_value =  td.getTimeInMillis() - c.getTimeInMillis();
            if (minimum_value < 157784630000L){
                //CreateAlertDialog("The Child is not 5 years old");
                CreateAlertDialog(context.getString(R.string.age_less_than_5));
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }
}
