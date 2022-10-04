package com.echdr.android.echdrapp.service.Validator;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.echdr.android.echdrapp.R;

public class SupplementInterventionValidator extends Validator{
    private String TAG = "SupplementInterventionValidator";
    private TextView textView_Date;
    private EditText numberOfPackets;
    private Context context;

    public void setTextView_Date(TextView textView_Date) {
        this.textView_Date = textView_Date;
    }
    public void setTextView_Packets(EditText textView_Packets) {
        this.numberOfPackets = textView_Packets;

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

        if (numberOfPackets.getText().toString().isEmpty() ||
                Integer.parseInt(numberOfPackets.getText().toString()) > 4 ||
                    Integer.parseInt(numberOfPackets.getText().toString()) < 1) {
                CreateAlertDialog(context.getString(R.string.supp_intervention_alert));
                return false;
            }
        return true;
    }
}
