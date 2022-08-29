package com.echdr.android.echdrapp.service.Validator;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.echdr.android.echdrapp.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnthropometryValidator extends Validator {
    private  String TAG = "";
    private TextView textView_Date;
    private EditText heightTxt;
    private EditText weightTxt;
    private Context context;

    public void setTextView_Date(TextView textView_Date) {
        this.textView_Date = textView_Date;
    }

    public void setHeightTxt(EditText heightTxt) {
        this.heightTxt = heightTxt;
    }

    public void setWeightTxt(EditText weightTxt) {
        this.weightTxt = weightTxt;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean validate(){
        super.setContext(context);
        super.setTAG(TAG);

        if(textView_Date.getText().toString().equals("Click here to set Date")||
                textView_Date.getText().toString().isEmpty()){
            CreateAlertDialog(context.getString(R.string.date));
            return false;
        }

        if( heightTxt.getText().toString().isEmpty() ||
                Integer.parseInt(heightTxt.getText().toString()) < 15
                || Integer.parseInt(heightTxt.getText().toString()) > 150){
            CreateAlertDialog(context.getString(R.string.anthro_height));
            return false;
        }

        if( weightTxt.getText().toString().isEmpty() ||
                Integer.parseInt(weightTxt.getText().toString()) < 100
                || Integer.parseInt(weightTxt.getText().toString()) > 50000){
            CreateAlertDialog(context.getString(R.string.anthro_weight));
            return false;
        }
        return true;

    }

}
