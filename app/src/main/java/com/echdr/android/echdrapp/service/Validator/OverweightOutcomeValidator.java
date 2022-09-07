package com.echdr.android.echdrapp.service.Validator;

import android.content.Context;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.GuardedBy;

import com.echdr.android.echdrapp.R;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if (spinner_Enrollment.getSelectedItemPosition() == 3){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = null;
            try {
                dob = formatter.parse(birthday.value());
                Calendar c = Calendar.getInstance();
                c.setTime(dob);
                long minimum_value =  System.currentTimeMillis() - c.getTimeInMillis();

                if (minimum_value < 157784630000L){
                    CreateAlertDialog("The Child is not 5 years old");
                    return false;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
