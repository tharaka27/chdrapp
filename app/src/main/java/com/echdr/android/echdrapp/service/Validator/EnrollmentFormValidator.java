package com.echdr.android.echdrapp.service.Validator;

import androidx.appcompat.app.AlertDialog;
import android.content.res.Resources;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Setter;

public class  EnrollmentFormValidator extends Validator {

    private  String TAG = "";
    Context context;
    EditText GNArea;
    EditText name;
    TextView birthday;
    EditText address;
    EditText motherName;
    TextView mother_birthday;
    EditText immuneNum;
    EditText landNumber;
    EditText mobileNumber;
    EditText numberOfChildren;
    EditText weight;
    EditText length;
    Spinner relationship;
    Spinner occupation;
    EditText nic;
    EditText caregiver;
    EditText occu_specification;
    String[] relationship_english_only;
    String[] occupation_english_only;
    boolean isEnrollement = false;

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setGNArea(EditText GNArea) {
        this.GNArea = GNArea;
    }

    public void setName(EditText name) {
        this.name = name;
    }

    public void setBirthday(TextView birthday) {
        this.birthday = birthday;
    }

    public void setAddress(EditText address) {
        this.address = address;
    }

    public void setMotherName(EditText motherName) {
        this.motherName = motherName;
    }

    public void setMother_birthday(TextView mother_birthday) {
        this.mother_birthday = mother_birthday;
    }

    public void setImmuneNum(EditText immuneNum) {
        this.immuneNum = immuneNum;
    }

    public void setLandNumber(EditText landNumber) {
        this.landNumber = landNumber;
    }

    public void setMobileNumber(EditText mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setNumberOfChildren(EditText numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public void setWeight(EditText weight) {
        this.weight = weight;
    }

    public void setLength(EditText length) {
        this.length = length;
    }

    public void setRelationship(Spinner relationship) {
        this.relationship = relationship;
    }

    public void setOccupation(Spinner occupation) {
        this.occupation = occupation;
    }

    public void setNic(EditText nic) {
        this.nic = nic;
    }

    public void setCaregiver(EditText caregiver) {
        this.caregiver = caregiver;
    }

    public void setOccu_specification(EditText occu_specification) {
        this.occu_specification = occu_specification;
    }

    public void setRelationship_english_only(String[] relationship_english_only) {
        this.relationship_english_only = relationship_english_only;
    }

    public void setOccupation_english_only(String[] occupation_english_only) {
        this.occupation_english_only = occupation_english_only;
    }

    public void setEnrollement(boolean enrollement) {
        isEnrollement = enrollement;
    }

    public boolean validate(){
        super.setContext(context);
        super.setTAG(TAG);

        // Immunization number validation
        String pattern = "^[0-9][0-9]\\/[0-1][0-9]\\/[0-3][0-9]$";
        Matcher m1 = null;
        Matcher m2 = null;
        Pattern r = Pattern.compile(pattern);

        String nicPattern = "^([0-9]{9}[x|X|v|V]|[0-9]{12})$";
        Matcher mNICPattern = null;
        Pattern pNICPattern = Pattern.compile(nicPattern);

        String patternLPhone = "^[0-9]{10}$";
        Pattern q = Pattern.compile(patternLPhone);

        if (GNArea.getText().toString().isEmpty()) {
            CreateAlertDialog(context.getString(R.string.anthro_gn));
            return false;
        }
        if (name.getText().toString().isEmpty()) {
            CreateAlertDialog(context.getString(R.string.anthro_name));
            return false;
        }
        if (birthday.getText().toString().isEmpty()) {
            CreateAlertDialog(context.getString(R.string.anthro_dob));
            return false;
        }
        if (address.getText().toString().isEmpty()) {
            CreateAlertDialog(context.getString(R.string.anthro_address));
            return false;
        }
        if (motherName.getText().toString().isEmpty()) {
            CreateAlertDialog(context.getString(R.string.anthro_mom_name));
            return false;
        }
        if (mother_birthday.getText().toString().isEmpty()) {
            CreateAlertDialog(context.getString(R.string.anthro_mom_dob));
            return false;
        }
        if (immuneNum.getText().toString().isEmpty()) {
            CreateAlertDialog(context.getString(R.string.anthro_immune));
            return false;
        } else {
            m1 = r.matcher(immuneNum.getText().toString().trim());
            if (!m1.find()) {
                CreateAlertDialog(context.getString(R.string.anthro_immune_not_matching));
                return false;
            }
            if(isEnrollement){
                String immun = immuneNum.getText().toString().trim();
                try {
                    List<TrackedEntityAttributeValue> values =
                            Sdk.d2().trackedEntityModule()
                                    .trackedEntityAttributeValues().byValue()
                                    .eq(immun).blockingGet();
                    if(!values.isEmpty()){
                        CreateAlertDialog("Child with same Immunization number" +
                                "already exists in PHM area");
                        return false;
                    }
                }catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                    return false;
                }
            }

        }
        if(landNumber.getText().toString().isEmpty() && mobileNumber.getText().toString().isEmpty() ){
            CreateAlertDialog(context.getString(R.string.anthro_regis_land_empty));
            return false;
        } else {
            m2 = q.matcher(landNumber.getText().toString().trim());
            if (!m2.find()) {
                CreateAlertDialog(context.getString(R.string.anthro_regis_land));
                return false;
            }
        }
        if(mobileNumber.getText().toString().isEmpty()){
            CreateAlertDialog(context.getString(R.string.anthro_regis_mobile_empty));
            return false;
        } else {
            m2 = q.matcher(mobileNumber.getText().toString().trim());
            if (m2.find()) {
                //Toast.makeText(EnrollmentFormModified.this, "Land Number matched", Toast.LENGTH_LONG).show();
            } else {
                CreateAlertDialog(context.getString(R.string.anthro_regis_mobile));
                return false;
            }
        }
        if( numberOfChildren.getText().toString().isEmpty() ||
                Integer.parseInt(numberOfChildren.getText().toString()) < 1
                || Integer.parseInt(numberOfChildren.getText().toString()) >= 20)
        {
            CreateAlertDialog(context.getString(R.string.anthro_chirdren));
            return false;
        }

        if(weight.getText().toString().isEmpty() ||
                Integer.parseInt(weight.getText().toString()) < 500
                || Integer.parseInt(weight.getText().toString()) >= 9999)
        {
            CreateAlertDialog(context.getString(R.string.anthro_regis_weight));
            return false;
        }
        if(length.getText().toString().isEmpty() ||
                Integer.parseInt(length.getText().toString()) < 10
                || Integer.parseInt(length.getText().toString()) >= 99)
        {
            CreateAlertDialog(context.getString(R.string.anthro_regis_length));
            return false;
        }
        if( !relationship_english_only[relationship.getSelectedItemPosition()].equals("Mother")
                && caregiver.getText().toString().isEmpty() )
        {
            CreateAlertDialog(context.getString(R.string.anthro_not_mot));
            return false;
        }
        if(occu_specification.getText().toString().isEmpty() &&
                (occupation_english_only[occupation.getSelectedItemPosition()].equals("Retired") ||
                        occupation_english_only[occupation.getSelectedItemPosition()].equals("Self employment") ||
                        occupation_english_only[occupation.getSelectedItemPosition()].equals("Paid employment")) )
        {
            CreateAlertDialog(context.getString(R.string.anthro_occu));
            return false;
        }

        if(!nic.getText().toString().isEmpty()){
            mNICPattern = pNICPattern.matcher(nic.getText().toString().trim());
            if (mNICPattern.find()) {
                //Toast.makeText(EnrollmentFormModified.this, "Land Number matched", Toast.LENGTH_LONG).show();
            } else {
                CreateAlertDialog("NIC validation failure");
                return false;
            }
        }
        return true;
    }



}
