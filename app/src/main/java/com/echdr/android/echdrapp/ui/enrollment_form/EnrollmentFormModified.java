package com.echdr.android.echdrapp.ui.enrollment_form;


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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.ActivityStarter;
import com.echdr.android.echdrapp.data.service.forms.EnrollmentFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.echdr.android.echdrapp.service.Validator.EnrollmentFormValidator;
import com.echdr.android.echdrapp.service.util;
import com.echdr.android.echdrapp.ui.tracked_entity_instances.ChildDetailsActivityNew;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.rules.RuleEngine;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

public class EnrollmentFormModified extends AppCompatActivity {

    private static final String TAG = "EnrollmentFormActivity";
    private CompositeDisposable disposable;
    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private RuleEngine ruleEngine;
    private static boolean isValidated = false;

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
    private Button saveButton;

    private enum IntentExtra {
        TEI_UID, PROGRAM_UID, OU_UID
    }

    public static Intent getModifiedFormActivityIntent(Context context, String teiUid, String programUid,
                                                       String orgUnitUid) {
        Intent intent = new Intent(context, EnrollmentFormModified.class);
        intent.putExtra(EnrollmentFormModified.IntentExtra.TEI_UID.name(), teiUid);
        intent.putExtra(EnrollmentFormModified.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(EnrollmentFormModified.IntentExtra.OU_UID.name(), orgUnitUid);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_custom);

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

        saveButton = findViewById(R.id.childSave);

        context = this;

        teiUid = getIntent().getStringExtra(IntentExtra.TEI_UID.name());

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

        engineInitialization = PublishProcessor.create();

        if (EnrollmentFormService.getInstance().init(
                Sdk.d2(),
                getIntent().getStringExtra(EnrollmentFormModified.IntentExtra.TEI_UID.name()),
                getIntent().getStringExtra(EnrollmentFormModified.IntentExtra.PROGRAM_UID.name()),
                getIntent().getStringExtra(EnrollmentFormModified.IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();


        //Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        String s_day = (String) DateFormat.format("dd", date); // 20
        String s_monthNumber = (String) DateFormat.format("MM", date); // 06
        String s_year = (String) DateFormat.format("yyyy", date); // 2013

        final int year = Integer.parseInt(s_year);
        final int month = Integer.parseInt(s_monthNumber) - 1;
        final int day = Integer.parseInt(s_day);


        datePicker_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateRegistration(year, month, day);
            }
        });

        setListenerRegistration = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth);
                textView_date_of_registration.setText(date);
            }
        };


        datePicker_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(year, month, day);
            }
        });


        setListenerDob = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               month = month  + 1;
               String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth);
               textView_dob.setText(date);
            }
        };

        datePicker_mother_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDateMotherDOB(year, month, day);
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

        //setting spinners
        util.setSpinner(context, sex, R.array.sex);
        util.setSpinner(context, ethnicity, R.array.ethnicity);
        util.setSpinner(context, eduLevel, R.array.highestEdu);
        util.setSpinner(context, sector, R.array.sector);
        util.setSpinner(context, occupation, R.array.occupation);
        util.setSpinner(context, relationship, R.array.relationship);

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
        sex.setSelection(util.getTEISpinnerSelection("lmtzQrlHMYF", sexArray, teiUid));
        ethnicity.setSelection(util.getTEISpinnerSelection("b9CoAneYYys", ethinicityArray, teiUid));
        sector.setSelection(util.getTEISpinnerSelection("igjlkmMF81X", sectorArray, teiUid));
        eduLevel.setSelection(util.getTEISpinnerSelection("GMNSaaq4xST", eduLevelArray, teiUid));
        occupation.setSelection(util.getTEISpinnerSelection("Srxv0vniOnf", occupationArray, teiUid));
        relationship.setSelection(util.getTEISpinnerSelection("ghN8XfnlU5V", relationshipArray, teiUid));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start of the validating this should be false
                isValidated = false;
                EnrollmentFormValidator enrollmentFormValidator = new EnrollmentFormValidator();
                enrollmentFormValidator.setEnrollement(true);
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

                saveElements();

            }
        });

    }

    private void saveElements()
    {
        if(!isValidated){
            Log.e(TAG, "Error occured while trying to save tracked entity instance" );
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String message = String.format(context.getString(R.string.enr_form_bi),
                immuneNum.getText().toString());
        builder.setMessage(message);
        builder.setCancelable(true);

        builder.setNegativeButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //saveTEIDataElement("KuMTUOY6X3L", textView_date_of_registration.getText().toString());
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
                        EnrollmentFormModified.this,
                        ChildDetailsActivityNew.getTrackedEntityInstancesActivityIntent(
                                context,
                                teiUid
                        ),true
                );
            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }

    private void selectDateRegistration(int year, int month, int day)
    {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -7*4); // subtract 1 month from now
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListenerRegistration, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
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

    private String makeDate(int year, int month, int DayofMonth)
    {
        return DayofMonth + "/" + month + "/" + year;
    }

    /*
    private int getSpinnerSelection(String dataElement, String [] array)
    {
        int itemPosition = -1;
        String stringElement = util.getDataTEIElement(dataElement, teiUid);
        for(int i =0; i<array.length; i++)
        {
            if(array[i].equals(stringElement))
            {
                itemPosition = i;
            }
        }
        return itemPosition;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        disposable = new CompositeDisposable();

        disposable.add(
                engineService.configure(Sdk.d2(),
                        getIntent().getStringExtra(EnrollmentFormModified.IntentExtra.PROGRAM_UID.name()),
                        EnrollmentFormService.getInstance().getEnrollmentUid(),
                        null
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                ruleEngine -> {
                                    this.ruleEngine = ruleEngine;
                                    engineInitialization.onNext(true);
                                },
                                Throwable::printStackTrace
                        )
        );

    }


    @Override
    protected void onPause() {
        super.onPause();
        disposable.clear();
    }

    @Override
    protected void onDestroy() {
        EnrollmentFormService.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        EnrollmentFormService.getInstance().delete();

        try {
            Sdk.d2().trackedEntityModule().trackedEntityInstances().uid(teiUid).blockingDelete();

        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
            System.out.println("[INFO] Tracked identity delete failed");
        }


        setResult(RESULT_CANCELED);
        finish();
    }

    private void finishEnrollment() {
        setResult(RESULT_OK);
        finish();
    }



}
