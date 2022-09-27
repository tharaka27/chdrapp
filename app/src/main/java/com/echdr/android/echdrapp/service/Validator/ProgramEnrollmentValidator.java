package com.echdr.android.echdrapp.service.Validator;

import android.content.Context;
import android.widget.Spinner;
import android.widget.TextView;

import com.echdr.android.echdrapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Setter;


public class ProgramEnrollmentValidator extends Validator{
    private String TAG = "ProgramEnrollmentValidator";
    private Context context;
    private boolean IsSupplementaryEnrolled;
    private boolean IsTherapeuticalEnrolled;
    private boolean IsOtherNonHealthEnrolled;
    private boolean IsStuntingEnrolled;
    private boolean IsOverweightEnrolled;

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    public void setSupplementaryEnrolled(boolean supplementaryEnrolled) {
        IsSupplementaryEnrolled = supplementaryEnrolled;
    }

    public void setTherapeuticalEnrolled(boolean therapeuticalEnrolled) {
        IsTherapeuticalEnrolled = therapeuticalEnrolled;
    }

    public void setOtherNonHealthEnrolled(boolean otherNonHealthEnrolled) {
        IsOtherNonHealthEnrolled = otherNonHealthEnrolled;
    }

    public void setStuntingEnrolled(boolean stuntingEnrolled) {
        IsStuntingEnrolled = stuntingEnrolled;
    }

    public void setOverweightEnrolled(boolean overweightEnrolled) {
        IsOverweightEnrolled = overweightEnrolled;
    }

    public boolean validate(String programID){
        super.setContext(context);
        super.setTAG(TAG);

        if(programID.equals("OVERWEIGHT"))
        {
            if(IsSupplementaryEnrolled || IsTherapeuticalEnrolled){
                CreateAlertDialog("Already enrolled to either one or more programs of " +
                        "\nTherapeutical, Supplementary");
                return false;
            }
        }
        if(programID.equals("SUPPLEMENTARY"))
        {
            if(IsTherapeuticalEnrolled || IsOverweightEnrolled){
                CreateAlertDialog("Already enrolled to either one or more programs of " +
                        "\nTherapeutical, Overweight/Obesity");
                return false;
            }
        }
        if(programID.equals("THERAPEUTICAL"))
        {
            if(IsSupplementaryEnrolled || IsOverweightEnrolled){
                CreateAlertDialog("Already enrolled to either one or more programs of " +
                        "\nSupplementary, Overweight/Obesity");
                return false;
            }
        }

        return true;
    }
}
