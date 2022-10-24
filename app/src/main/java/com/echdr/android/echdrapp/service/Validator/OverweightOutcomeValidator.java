package com.echdr.android.echdrapp.service.Validator;

import static com.echdr.android.echdrapp.service.Constants.AGE_COMPLETED_FIVE_YEARS_OVERWEIGHT;

import android.content.Context;
import android.widget.Spinner;
import android.widget.TextView;

import com.echdr.android.echdrapp.R;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;


public class OverweightOutcomeValidator extends Validator{
    private String TAG = "OverweightOutcomeValidator";
    private TextView textView_Date;
    private Spinner spinner_Enrollment;
    private Context context;

    public void setBirthday(TrackedEntityAttributeValue birthday) {
        this.birthday = birthday;
    }

    private TrackedEntityAttributeValue birthday;

    @Override
    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public void setTextView_Date(TextView textView_Date) {
        this.textView_Date = textView_Date;
    }

    public void setSpinner_Enrollment(Spinner spinner_Enrollment) {
        this.spinner_Enrollment = spinner_Enrollment;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    public boolean validate(){
        super.setContext(context);
        super.setTAG(TAG);

        if(textView_Date.getText().toString().equals(context.getString(R.string.date_button_text))||
                textView_Date.getText().toString().isEmpty())
        {
            CreateAlertDialog(context.getString(R.string.date));
            return false;
        }

        if (spinner_Enrollment.getSelectedItemPosition() == AGE_COMPLETED_FIVE_YEARS_OVERWEIGHT){
            return checkIfAgeIsFive(textView_Date, birthday);
        }

        return true;
    }
}
