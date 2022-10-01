package com.echdr.android.echdrapp.service.Validator;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.echdr.android.echdrapp.R;

public class TherapeuticInterventionValidator extends Validator{
    private String TAG = "TherapeuticInterventionValidator";
    private TextView textView_Date;
    private Context context;

    public void setTextView_Date(TextView textView_Date) {
        this.textView_Date = textView_Date;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    public boolean validate() {
        super.setContext(context);
        super.setTAG(TAG);

        if (textView_Date.getText().toString().equals(context.getString(R.string.date_button_text)) ||
                textView_Date.getText().toString().isEmpty()) {
            CreateAlertDialog(context.getString(R.string.date));
            return false;
        }
        return true;
    }
}
