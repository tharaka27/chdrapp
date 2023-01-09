package com.echdr.android.echdrapp.ui.tracked_entity_instances;

import static android.text.TextUtils.isEmpty;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.LocaleHelper;
import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.ActivityStarter;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.echdr.android.echdrapp.service.Service.ProgramEnrollmentService;
import com.echdr.android.echdrapp.service.Validator.EditAccessValidator;
import com.echdr.android.echdrapp.service.Validator.EnrollmentFormValidator;
import com.echdr.android.echdrapp.service.Validator.ProgramEnrollmentValidator;
import com.echdr.android.echdrapp.service.util;
import com.echdr.android.echdrapp.ui.enrollment_form.EnrollmentFormActivity;
import com.echdr.android.echdrapp.ui.events.EventsActivity;
import com.echdr.android.echdrapp.ui.splash.LanguageContext;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.rules.RuleEngine;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.PublishProcessor;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ChildDetailsActivityNew extends AppCompatActivity {

    private static final String TAG = "ChildDetailsActivity";
    private static boolean isValidated = true;
    private CompositeDisposable disposable;
    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private RuleEngine ruleEngine;

    private DatePickerDialog.OnDateSetListener setListenerRegistration;
    private DatePickerDialog.OnDateSetListener setListenerDob;
    private DatePickerDialog.OnDateSetListener setListenerMotherDob;


    private Context context;
    protected String[] sexArray;
    protected String[] sex_english_only;

    protected String[] ethinicityArray;
    protected String[] ethinicity_english_only;

    protected String[] sectorArray;
    protected String[] sector_english_only;

    protected String[] eduLevelArray;
    protected String[] eduLevel_english_only;

    protected String[] occupationArray;
    protected String[] occupation_english_only;


    protected String[] relationshipArray;
    protected String[] relationship_english_only;


    private String teiUid;

    private TextView textView_date_of_registration;
    private ImageView datePicker_registration;
    private EditText GNArea;
    private EditText immuneNum;
    private EditText name;
    private Spinner sex;
    private TextView textView_dob;
    private ImageView datePicker_dob;
    private Spinner ethnicity;
    private EditText address;
    private Spinner sector;
    private EditText landNumber;
    private EditText mobileNumber;
    private EditText motherName;
    private EditText nic;
    private TextView textView_mother_dob;
    private ImageView datePicker_mother_dob;
    private EditText numberOfChildren;
    private Spinner eduLevel;
    private Spinner occupation;
    private EditText occu_specification;
    private Spinner relationship;
    private EditText caregiver;
    private EditText weight;
    private EditText length;

    private ImageView overweightNotEnrolled;
    private ImageView overweightEnrolled;
    private ImageView antopoNotEnrolled;
    private ImageView antopoEnrolled;
    private ImageView supplementaryNotEnrolled;
    private ImageView supplementaryEnrolled;
    private ImageView therapeuticNotEnrolled;
    private ImageView therapeuticEnrolled;
    private ImageView otherHealthNotEnrolled;
    private ImageView otherHealthEnrolled;
    private ImageView stuntingEnrolled;
    private ImageView stuntingNotEnrolled;

    private boolean IsAnthropometryEnrolled = false;
    private boolean IsSupplementaryEnrolled= false;
    private boolean IsTherapeuticalEnrolled= false;
    private boolean IsOtherNonHealthEnrolled= false;
    private boolean IsStuntingEnrolled= false;
    private boolean IsOverweightEnrolled= false;


    private Button edit_button;

    private Button submitButton;

    private String anthropometryEnrollmentID;
    private String otherEnrollmentID;
    private String overweightEnrollmentID;
    private String stuntingEnrollmentID;
    private String supplementaryEnrollmentID;
    private String therapeuticEnrollmentID;

    private String orgUnit;

    private static class ReturnPair{
        String enrollmentID;
        boolean isEnrolled;
    }

    private enum IntentExtra {
        TEI_UID
    }

    public static Intent getTrackedEntityInstancesActivityIntent(Context context, String uid) {
        context = LocaleHelper.setLocale(context, LanguageContext.getLanguageContext().getLanguage());
        Intent intent = new Intent(context, ChildDetailsActivityNew.class);
        intent.putExtra(ChildDetailsActivityNew.IntentExtra.TEI_UID.name(), uid);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details_new);

        engineInitialization            = PublishProcessor.create();

        textView_date_of_registration   = findViewById(R.id.editTextDateofRegistration);
        datePicker_registration         = findViewById(R.id.date_pick_registration);
        GNArea                          = findViewById(R.id.gn_area);
        immuneNum                       = findViewById(R.id.immuneNum);
        name                            = findViewById(R.id.name);
        sex                             = findViewById(R.id.sex);
        textView_dob                    = findViewById(R.id.editTextDateofBirth);
        datePicker_dob                  = findViewById(R.id.date_pick_birth);
        ethnicity                       = findViewById(R.id.ethnicity);
        address                         = findViewById(R.id.address);
        sector                          = findViewById(R.id.sector);
        landNumber                      = findViewById(R.id.lNumber);
        mobileNumber                    = findViewById(R.id.mNumber);
        motherName                      = findViewById(R.id.motherName);
        nic                             = findViewById(R.id.nic);
        textView_mother_dob             = findViewById(R.id.editTextMomDateofBirth);
        datePicker_mother_dob           = findViewById(R.id.date_pick_mom_birth);
        numberOfChildren                = findViewById(R.id.number_of_children);
        eduLevel                        = findViewById(R.id.edu_level);
        occupation                      = findViewById(R.id.occupation);
        occu_specification              = findViewById(R.id.occu_specifcation);
        relationship                    = findViewById(R.id.relationship);
        caregiver                       = findViewById(R.id.caregiverName);
        weight                          = findViewById(R.id.weight);
        length                          = findViewById(R.id.length);

        edit_button     = findViewById(R.id.edit_btn);
        submitButton    = findViewById(R.id.submit);

        context = this;

        teiUid = getIntent().getStringExtra(ChildDetailsActivityNew.IntentExtra.TEI_UID.name());

        sexArray = getResources().getStringArray(R.array.sex);
        sex_english_only = getResources().getStringArray(R.array.sex_english_only);

        ethinicityArray = getResources().getStringArray(R.array.ethnicity);
        ethinicity_english_only = getResources().getStringArray(R.array.ethinicity_english_only);

        sectorArray = getResources().getStringArray(R.array.sector);
        sector_english_only = getResources().getStringArray(R.array.sector_english_only);

        eduLevelArray = getResources().getStringArray(R.array.highestEdu);
        eduLevel_english_only = getResources().getStringArray(R.array.eduLevel_english_only);

        occupationArray = getResources().getStringArray(R.array.occupation);
        occupation_english_only = getResources().getStringArray(R.array.occupation_english_only);

        relationshipArray = getResources().getStringArray(R.array.relationship);
        relationship_english_only = getResources().getStringArray(R.array.relationship_english_only);

        overweightNotEnrolled = findViewById(R.id.NotEnOverWeight);
        overweightEnrolled = findViewById(R.id.EnOverWeight);
        antopoNotEnrolled = findViewById(R.id.NotEnAntopo);
        antopoEnrolled = findViewById(R.id.EnAntopo);
        supplementaryNotEnrolled = findViewById(R.id.NotEnSupplementary);
        supplementaryEnrolled = findViewById(R.id.EnSupplementary);
        therapeuticNotEnrolled = findViewById(R.id.NotEnTera);
        therapeuticEnrolled = findViewById(R.id.EnTera);
        otherHealthNotEnrolled = findViewById(R.id.NotEnOtherHealth);
        otherHealthEnrolled = findViewById(R.id.EnOtherHealth);
        stuntingEnrolled = findViewById(R.id.EnStunting);
        stuntingNotEnrolled = findViewById(R.id.NotEnStunting);

        //setting spinners
        util.setSpinner(context, sex, R.array.sex);
        util.setSpinner(context, ethnicity, R.array.ethnicity);
        util.setSpinner(context, eduLevel, R.array.highestEdu);
        util.setSpinner(context, sector, R.array.sector);
        util.setSpinner(context, occupation, R.array.occupation);
        util.setSpinner(context, relationship, R.array.relationship);

        Date date = new Date();
        String s_day = (String) DateFormat.format("dd", date); // 20
        String s_monthNumber = (String) DateFormat.format("MM", date); // 06
        String s_year = (String) DateFormat.format("yyyy", date); // 2013

        final int year = Integer.parseInt(s_year);
        final int month = Integer.parseInt(s_monthNumber) - 1;
        final int day = Integer.parseInt(s_day);

        submitButton.setClickable(false);
        submitButton.setBackgroundColor(Color.GRAY);

        setListenerRegistration = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth);
                textView_date_of_registration.setText(date);
            }
        };

        setListenerDob = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month  + 1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth);
                textView_dob.setText(date);
            }
        };

        datePicker_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(year, month, day);
            }
        });

        setListenerMotherDob = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth);
                textView_mother_dob.setText(date);
            }
        };

        datePicker_mother_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateMotherDOB(year, month, day);
            }
        });


        // setting date of registration
        // Get the latest enrollment

        List<Enrollment> AnthropometryStatus = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(teiUid)
                .byProgram().eq("hM6Yt9FQL0n")
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();
        String anthropometryEnrollmentID = "";

        // The child should have at least one enrollment
        if(!AnthropometryStatus.isEmpty())
        {
            anthropometryEnrollmentID = AnthropometryStatus.get(0).uid();
        }

        // set the enrollment status to active based on the enrollment ID
        try {
            Date date_of_reg = Sdk.d2().enrollmentModule().enrollments()
                    .uid(anthropometryEnrollmentID).blockingGet().enrollmentDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String prev_date = formatter.format(date_of_reg);
            if (!prev_date.isEmpty()) {
                textView_date_of_registration.setText(prev_date);
            }
        } catch (Exception e) {
            textView_date_of_registration.setText("");
        }

        // setting mother DOB date
        util.setTEITextview(textView_mother_dob, "kYfIkz2M6En", teiUid);
        util.setTEITextview(GNArea, "upQGjAHBjzu", teiUid);
        util.setTEITextview(immuneNum, "h2ATdtJguMq", teiUid);
        util.setTEITextview(name, "zh4hiarsSD5", teiUid);
        util.setTEITextview(textView_dob, "qNH202ChkV3", teiUid);
        util.setTEITextview(address, "D9aC5K6C6ne", teiUid);
        util.setTEITextview(landNumber, "cpcMXDhQouL", teiUid);
        util.setTEITextview(mobileNumber, "LYRf4eIUVuN", teiUid);
        util.setTEITextview(motherName, "K7Fxa2wv2Rx", teiUid);
        util.setTEITextview(nic, "Gzjb3fp9FSe", teiUid);
        util.setTEITextview(numberOfChildren, "Gy4bCBxNuo4", teiUid);
        util.setTEITextview(occu_specification, "s7Rde0kFOFb", teiUid);
        util.setTEITextview(caregiver, "hxCXbI5J2YS", teiUid);
        util.setTEITextview(weight, "Fs89NLB2FrA", teiUid);
        util.setTEITextview(length, "LpvdWM4YuRq", teiUid);

        sex.setSelection(util.getTEISpinnerSelection("lmtzQrlHMYF", sex_english_only , teiUid));
        ethnicity.setSelection(util.getTEISpinnerSelection("b9CoAneYYys", ethinicity_english_only, teiUid));
        sector.setSelection(util.getTEISpinnerSelection("igjlkmMF81X", sector_english_only, teiUid));
        eduLevel.setSelection(util.getTEISpinnerSelection("GMNSaaq4xST", eduLevel_english_only, teiUid));
        occupation.setSelection(util.getTEISpinnerSelection("Srxv0vniOnf", occupation_english_only, teiUid));
        relationship.setSelection(util.getTEISpinnerSelection("ghN8XfnlU5V", relationship_english_only, teiUid));


        //make spinners not clickable on startup
        sex.setClickable(false);
        sex.setEnabled(false);
        ethnicity.setClickable(false);
        ethnicity.setEnabled(false);
        sector.setClickable(false);
        sector.setEnabled(false);
        eduLevel.setClickable(false);
        eduLevel.setEnabled(false);
        occupation.setClickable(false);
        occupation.setEnabled(false);
        relationship.setClickable(false);
        relationship.setEnabled(false);

        getEnrollment();

        ProgramEnrollmentValidator programEnrollmentValidator = new ProgramEnrollmentValidator();
        programEnrollmentValidator.setContext(context);
        programEnrollmentValidator.setOtherNonHealthEnrolled(IsOtherNonHealthEnrolled);
        programEnrollmentValidator.setOverweightEnrolled(IsOverweightEnrolled);
        programEnrollmentValidator.setStuntingEnrolled(IsStuntingEnrolled);
        programEnrollmentValidator.setSupplementaryEnrolled(IsSupplementaryEnrolled);
        programEnrollmentValidator.setTherapeuticalEnrolled(IsTherapeuticalEnrolled);

        ProgramEnrollmentService programEnrollmentService = new ProgramEnrollmentService(
                teiUid, orgUnit, context, overweightNotEnrolled, overweightEnrolled,
                antopoNotEnrolled, antopoEnrolled, otherHealthNotEnrolled, otherHealthEnrolled,
                stuntingNotEnrolled, stuntingEnrolled, supplementaryNotEnrolled, supplementaryEnrolled,
                therapeuticNotEnrolled, therapeuticEnrolled,
                overweightEnrollmentID, anthropometryEnrollmentID, otherEnrollmentID,
                stuntingEnrollmentID, supplementaryEnrollmentID, therapeuticEnrollmentID, programEnrollmentValidator);

        programEnrollmentService.EnrollToPrograms();

        submitButton.setEnabled(false);

        EditAccessValidator editAccessValidator = new EditAccessValidator();
        editAccessValidator.setContext(context);
        editAccessValidator.setSelectedChild(teiUid);
        editAccessValidator.setEnrollmentID(anthropometryEnrollmentID);

        edit_button.setOnClickListener(view ->{
            //edit_button.setBackgroundResource(R.drawable.button_edit_child_details_editing);
            edit_button.setVisibility(View.GONE);

            if(editAccessValidator.validate()){
                sex.setEnabled(true);
                textView_dob.setEnabled(true);
                datePicker_dob.setEnabled(true);
            }else{
                sex.setEnabled(false);
                textView_dob.setEnabled(false);
                datePicker_dob.setEnabled(false);
            }

            immuneNum.setEnabled(false);
            GNArea.setEnabled(true);
            name.setEnabled(true);
            ethnicity.setEnabled(true);
            address.setEnabled(true);
            sector.setEnabled(true);
            landNumber.setEnabled(true);
            mobileNumber.setEnabled(true);
            motherName.setEnabled(true);
            nic.setEnabled(true);
            textView_mother_dob.setEnabled(true);
            datePicker_mother_dob.setEnabled(true);
            numberOfChildren.setEnabled(true);
            eduLevel.setEnabled(true);
            occupation.setEnabled(true);
            occu_specification.setEnabled(true);
            relationship.setEnabled(true);
            caregiver.setEnabled(true);
            weight.setEnabled(true);
            length.setEnabled(true);

            sex.setEnabled(true);
            ethnicity.setEnabled(true);
            sector.setEnabled(true);
            eduLevel.setEnabled(true);
            occupation.setEnabled(true);
            relationship.setEnabled(true);
            sex.setClickable(true);
            ethnicity.setClickable(true);
            sector.setClickable(true);
            eduLevel.setClickable(true);
            occupation.setClickable(true);
            relationship.setClickable(true);


            //submitButton.setClickable(true);
            submitButton.setEnabled(true);
            submitButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_button));
        });


        submitButton.setOnClickListener(view -> {

            isValidated = true;
            EnrollmentFormValidator enrollmentFormValidator = new EnrollmentFormValidator();
            enrollmentFormValidator.setEnrollement(false);
            enrollmentFormValidator.setGNArea(GNArea);
            enrollmentFormValidator.setName(name);
            enrollmentFormValidator.setBirthday(textView_dob);
            enrollmentFormValidator.setAddress(address);
            enrollmentFormValidator.setMotherName(motherName);
            enrollmentFormValidator.setMother_birthday(textView_mother_dob);
            enrollmentFormValidator.setImmuneNum(immuneNum);
            enrollmentFormValidator.setLandNumber(landNumber);
            enrollmentFormValidator.setMobileNumber(mobileNumber);
            enrollmentFormValidator.setNumberOfChildren(numberOfChildren);
            enrollmentFormValidator.setWeight(weight);
            enrollmentFormValidator.setLength(length);
            enrollmentFormValidator.setRelationship_english_only(relationship_english_only);
            enrollmentFormValidator.setRelationship(relationship);
            enrollmentFormValidator.setCaregiver(caregiver);
            enrollmentFormValidator.setOccu_specification(occu_specification);
            enrollmentFormValidator.setOccupation(occupation);
            enrollmentFormValidator.setOccupation_english_only(occupation_english_only);
            enrollmentFormValidator.setNic(nic);
            enrollmentFormValidator.setContext(context);
            enrollmentFormValidator.setTAG(TAG);

            isValidated = enrollmentFormValidator.validate();

            if(!isValidated){
                Log.e(TAG, "Error occured while trying to save tracked entity instance" );
                return;
            }

            saveElements();
        });

    }


    private void saveElements()
    {
        util.saveTEIDataElement("upQGjAHBjzu", GNArea.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("h2ATdtJguMq", immuneNum.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("zh4hiarsSD5", name.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("lmtzQrlHMYF",
                sex_english_only[sex.getSelectedItemPosition()], teiUid, engineInitialization);
        util.saveTEIDataElement("qNH202ChkV3", textView_dob.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("b9CoAneYYys",
                ethinicity_english_only[ethnicity.getSelectedItemPosition()], teiUid, engineInitialization);
        util.saveTEIDataElement("D9aC5K6C6ne", address.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("igjlkmMF81X",
                sector_english_only[sector.getSelectedItemPosition()], teiUid, engineInitialization);
        util.saveTEIDataElement("cpcMXDhQouL", landNumber.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("LYRf4eIUVuN", mobileNumber.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("K7Fxa2wv2Rx", motherName.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("Gzjb3fp9FSe", nic.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("kYfIkz2M6En", textView_mother_dob.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("Gy4bCBxNuo4", numberOfChildren.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("GMNSaaq4xST",
                eduLevel_english_only[eduLevel.getSelectedItemPosition()], teiUid, engineInitialization);
        util.saveTEIDataElement("Srxv0vniOnf",
                occupation_english_only[occupation.getSelectedItemPosition()], teiUid, engineInitialization);
        util.saveTEIDataElement("s7Rde0kFOFb", occu_specification.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("ghN8XfnlU5V",
                relationship_english_only[relationship.getSelectedItemPosition()], teiUid, engineInitialization);
        util.saveTEIDataElement("hxCXbI5J2YS", caregiver.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("Fs89NLB2FrA", weight.getText().toString(), teiUid, engineInitialization);
        util.saveTEIDataElement("LpvdWM4YuRq", length.getText().toString(), teiUid, engineInitialization);

        ActivityStarter.startActivity(
                this,
                ChildDetailsActivityNew.getTrackedEntityInstancesActivityIntent(
                        this,
                        teiUid
                ),true
        );
    }

    private void selectDate(int year, int month, int day)
    {
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DATE, -365*5); // subtract 5 years from now
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListenerDob, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.getDatePicker().setMinDate(c2.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void selectDateMotherDOB(int year, int month, int day)
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListenerMotherDob, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private ReturnPair setProgramEnrollment(String programID, ImageView enrolled, ImageView notEnrolled){
        List<Enrollment> programStatus = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(teiUid)
                .byProgram().eq(programID)
                //.orderByLastUpdated(RepositoryScope.OrderByDirection.DESC)
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        ReturnPair returnPair = new ReturnPair();
        if(!programStatus.isEmpty())
        {
            if ( programStatus.get(0).status().equals(EnrollmentStatus.ACTIVE)) {
                returnPair.isEnrolled = true;
                enrolled.setVisibility(View.VISIBLE);
                notEnrolled.setVisibility(View.GONE);
            }
            returnPair.enrollmentID = programStatus.get(0).uid();
            return returnPair;

        }
        return returnPair;
    }

    private void getEnrollment(){

        // get anthropometry latest enrollment
        ReturnPair anthropometryPair = setProgramEnrollment("hM6Yt9FQL0n",
                antopoEnrolled, antopoNotEnrolled);
        anthropometryEnrollmentID = anthropometryPair.enrollmentID;
        IsAnthropometryEnrolled = anthropometryPair.isEnrolled;

        // get other health/non health latest enrollment
        ReturnPair otherPair = setProgramEnrollment("iUgzznPsePB",
                otherHealthEnrolled, otherHealthNotEnrolled);
        otherEnrollmentID = otherPair.enrollmentID;
        IsOtherNonHealthEnrolled = otherPair.isEnrolled;

        // get other overweight/obesity latest enrollment
        ReturnPair overweightPair = setProgramEnrollment("JsfNVX0hdq9",
                overweightEnrolled, overweightNotEnrolled);
        overweightEnrollmentID = overweightPair.enrollmentID;
        IsOverweightEnrolled = overweightPair.isEnrolled;

        // get other stunting latest enrollment
        ReturnPair stuntingPair = setProgramEnrollment("lSSNwBMiwrK",
                stuntingEnrolled, stuntingNotEnrolled);
        stuntingEnrollmentID = stuntingPair.enrollmentID ;
        IsStuntingEnrolled = stuntingPair.isEnrolled;

        // get supplementary latest enrollment
        ReturnPair supplementaryPair = setProgramEnrollment("tc6RsYbgGzm",
                supplementaryEnrolled, supplementaryNotEnrolled);
        supplementaryEnrollmentID = supplementaryPair.enrollmentID;
        IsSupplementaryEnrolled = supplementaryPair.isEnrolled;

        // get therapeutic latest enrollment
        ReturnPair therapeuticPair = setProgramEnrollment("CoGsKgEG4O0",
                therapeuticEnrolled, therapeuticNotEnrolled);
        therapeuticEnrollmentID = therapeuticPair.enrollmentID;
        IsTherapeuticalEnrolled = therapeuticPair.isEnrolled;

    }

}
