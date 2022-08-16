package com.echdr.android.echdrapp.ui.enrollment_form;

import static android.text.TextUtils.isEmpty;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.ActivityStarter;
import com.echdr.android.echdrapp.data.service.forms.EnrollmentFormService;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.FormField;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.echdr.android.echdrapp.ui.event_form.SupplementaryIndicationActivity;
import com.echdr.android.echdrapp.ui.events.EventsActivity;
import com.echdr.android.echdrapp.ui.tracked_entity_instances.ChildDetailsActivity;

import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;
import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionHideField;
import org.hisp.dhis.rules.models.RuleEffect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

public class EnrollmentFormModified extends AppCompatActivity {


    private CompositeDisposable disposable;
    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private RuleEngine ruleEngine;
    private static boolean isError = false;

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

        /*
        textView_date_of_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR, -5); // subtract 5 years from now
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, android.R.style.Theme_Holo_Light_Dialog, setListenerDob, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                datePickerDialog.show();
            }
        });
         */

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

        /*
        textView_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                c.add(Calendar.YEAR, -5); // subtract 5 years from now
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, android.R.style.Theme_Holo_Light_Dialog, setListenerDob, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                datePickerDialog.show();
            }
        });
         */

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

        /*
        textView_mother_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicked et date");
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, android.R.style.Theme_Holo_Light_Dialog, setListenerMotherDob, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
         */

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
        setSpinner(sex, R.array.sex);
        setSpinner(ethnicity, R.array.ethnicity);
        setSpinner(eduLevel, R.array.highestEdu);
        setSpinner(sector, R.array.sector);
        setSpinner(occupation, R.array.occupation);
        setSpinner(relationship, R.array.relationship);

        setEditText(textView_mother_dob, "kYfIkz2M6En");
        setEditText(GNArea, "upQGjAHBjzu");
        setEditText(immuneNum, "h2ATdtJguMq");
        setEditText(name, "zh4hiarsSD5");
        setEditText(textView_dob, "qNH202ChkV3");
        setEditText(address, "D9aC5K6C6ne");
        setEditText(landNumber, "cpcMXDhQouL");
        setEditText(mobileNumber, "LYRf4eIUVuN");
        setEditText(motherName, "K7Fxa2wv2Rx");
        setEditText(nic, "Gzjb3fp9FSe");
        setEditText(numberOfChildren, "Gy4bCBxNuo4");
        setEditText(occu_specification, "s7Rde0kFOFb");
        setEditText(caregiver, "hxCXbI5J2YS");
        setEditText(weight, "Fs89NLB2FrA");
        setEditText(length, "LpvdWM4YuRq");
        sex.setSelection(getSpinnerSelection("lmtzQrlHMYF", sexArray));
        ethnicity.setSelection(getSpinnerSelection("b9CoAneYYys", ethinicityArray));
        sector.setSelection(getSpinnerSelection("igjlkmMF81X", sectorArray));
        eduLevel.setSelection(getSpinnerSelection("GMNSaaq4xST", eduLevelArray));
        occupation.setSelection(getSpinnerSelection("Srxv0vniOnf", occupationArray));
        relationship.setSelection(getSpinnerSelection("ghN8XfnlU5V", relationshipArray));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Immunization number validation
                String pattern = "[0-9][0-9]\\/[0-1][0-9]\\/[0-3][0-9]";
                Matcher m1 = null;
                Matcher m2 = null;
                Pattern r = Pattern.compile(pattern);

                String patternLPhone = "[0-9]{10}";
                Pattern q = Pattern.compile(patternLPhone);

                if (GNArea.getText().toString().isEmpty()) {
                    CreateAlertDialog("GN Area is not filled");
                }
                if (name.getText().toString().isEmpty()) {
                    CreateAlertDialog("Name of the child is not filled");
                }
                if (textView_dob.getText().toString().isEmpty()) {
                    CreateAlertDialog("Date of Birth is not filled");
                }
                if (address.getText().toString().isEmpty()) {
                    CreateAlertDialog("Address is not filled");
                }
                if (motherName.getText().toString().isEmpty()) {
                    CreateAlertDialog("Name of Mother is not filled");
                }
                if (textView_mother_dob.getText().toString().isEmpty()) {
                    CreateAlertDialog("Mother's date of birth is not filled");
                }
                if (immuneNum.getText().toString().isEmpty()) {
                    CreateAlertDialog("Birth and Immunization Number is not filled");
                } else {
                    m1 = r.matcher(immuneNum.getText().toString().trim());
                    if (!m1.find()) {
                        CreateAlertDialog("Birth and Immunization Number not matched");
                    }
                }
                if(landNumber.getText().toString().isEmpty() && mobileNumber.getText().toString().isEmpty() ){
                    CreateAlertDialog("Land Number is not filled");
                } else {
                    m2 = q.matcher(landNumber.getText().toString().trim());
                    if (!m2.find()) {
                        CreateAlertDialog("Land Number not matched");
                    }
                }
                if(mobileNumber.getText().toString().isEmpty()){
                    CreateAlertDialog("Mobile Number is not filled");
                } else {
                    m2 = q.matcher(mobileNumber.getText().toString().trim());
                    if (m2.find()) {
                        //Toast.makeText(EnrollmentFormModified.this, "Land Number matched", Toast.LENGTH_LONG).show();
                    } else {
                        CreateAlertDialog("Mobile Number not matched");
                    }
                }
                if( numberOfChildren.getText().toString().isEmpty() ||
                        Integer.parseInt(numberOfChildren.getText().toString()) < 0
                        || Integer.parseInt(numberOfChildren.getText().toString()) >= 20)
                {
                    CreateAlertDialog("Number of Children is allowed up to 20");
                }

                if(weight.getText().toString().isEmpty() ||
                        Integer.parseInt(weight.getText().toString()) < 500
                        || Integer.parseInt(weight.getText().toString()) >= 9999)
                {
                    CreateAlertDialog("Weight in Grams is allowed 500-9999");
                }
                if(length.getText().toString().isEmpty() ||
                        Integer.parseInt(length.getText().toString()) < 10
                        || Integer.parseInt(length.getText().toString()) >= 99)
                {
                    CreateAlertDialog("Length in Centimeters is allowed 10-99");
                }

                if( !relationship_english_only[relationship.getSelectedItemPosition()].equals("Mother")
                && caregiver.getText().toString().isEmpty() )
                {
                    CreateAlertDialog("If caregiver is not mother, caregiver name is mandatory.");
                }
                if(occu_specification.getText().toString().isEmpty() &&
                        (occupation_english_only[occupation.getSelectedItemPosition()].equals("Retired") ||
                                occupation_english_only[occupation.getSelectedItemPosition()].equals("Self employment") ||
                                occupation_english_only[occupation.getSelectedItemPosition()].equals("Paid employment")) )
                {
                    CreateAlertDialog("Occupation specification is mandatory is self employed/retired/paid.");
                }

                saveElements();
            }
        });

    }

    private void CreateAlertDialog(String ErrorMessage){
        isError = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(ErrorMessage);
        builder.setCancelable(true);

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

    class EnrollmentTypeSpinnerClass implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            //Toast.makeText(v.getContext(), "Your choose :" +
                    //sexArray[position], Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void setEditText(TextView textView, String dataElement){
        try {
            String element = getDataElement(dataElement);
            if (!element.isEmpty()) {
                textView.setText(dataElement);
            }
        } catch (Exception e) {
            textView.setText("");
        }
    }

    private void setSpinner(Spinner spinner, Object object){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                (Integer) object,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new EnrollmentTypeSpinnerClass());
    }

    private String getDataElement(String dataElement) {
        TrackedEntityAttributeValueObjectRepository valueRepository =
                Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                        .value(

                                dataElement,
                                teiUid
                                //getIntent().getStringExtra(EnrollmentFormModified.IntentExtra.TEI_UID.name()
                                //)
                        );
        String currentValue = valueRepository.blockingExists() ?
                valueRepository.blockingGet().value() : "";

        return currentValue;
    }

    private void saveDataElement(String dataElement, String value){
        TrackedEntityAttributeValueObjectRepository valueRepository;
        try {
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                    .value(
                            dataElement,
                            teiUid
                    );
        }catch (Exception e)
        {
            //EnrollmentFormService.getInstance().init(
                    //Sdk.d2(),
                    //teiUid,
                    //"hM6Yt9FQL0n",
                    //getIntent().getStringExtra(EnrollmentFormModified.IntentExtra.OU_UID.name()));
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                    .value(
                            teiUid,
                            //EnrollmentFormService.getInstance().getEnrollmentUid(),
                            dataElement
                    );
        }

        String currentValue = valueRepository.blockingExists() ?
                valueRepository.blockingGet().value() : "";

        if (currentValue == null)
            currentValue = "";

        try{
            if(!isEmpty(value))
            {
                valueRepository.blockingSet(value);
            }else
            {
                valueRepository.blockingDeleteIfExist();
            }
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }finally {
            if (!value.equals(currentValue)) {
                engineInitialization.onNext(true);
            }
        }
    }

    private void saveElements()
    {
        if(isError){
            return;
        }

        //saveDataElement("KuMTUOY6X3L", textView_date_of_registration.getText().toString());
        saveDataElement("upQGjAHBjzu", GNArea.getText().toString());
        saveDataElement("h2ATdtJguMq", immuneNum.getText().toString());
        saveDataElement("zh4hiarsSD5", name.getText().toString());
        saveDataElement("lmtzQrlHMYF",
                sex_english_only[sex.getSelectedItemPosition()]);
        saveDataElement("qNH202ChkV3", textView_dob.getText().toString());
        saveDataElement("b9CoAneYYys",
                ethinicity_english_only[ethnicity.getSelectedItemPosition()]);
        saveDataElement("D9aC5K6C6ne", address.getText().toString());
        saveDataElement("igjlkmMF81X",
                sector_english_only[sector.getSelectedItemPosition()]);
        saveDataElement("cpcMXDhQouL", landNumber.getText().toString());
        saveDataElement("LYRf4eIUVuN", mobileNumber.getText().toString());
        saveDataElement("K7Fxa2wv2Rx", motherName.getText().toString());
        saveDataElement("Gzjb3fp9FSe", nic.getText().toString());
        saveDataElement("kYfIkz2M6En", textView_mother_dob.getText().toString());
        saveDataElement("Gy4bCBxNuo4", numberOfChildren.getText().toString());
        saveDataElement("GMNSaaq4xST",
                eduLevel_english_only[eduLevel.getSelectedItemPosition()]);
        saveDataElement("Srxv0vniOnf",
                occupation_english_only[occupation.getSelectedItemPosition()]);
        saveDataElement("s7Rde0kFOFb", occu_specification.getText().toString());
        saveDataElement("ghN8XfnlU5V",
                relationship_english_only[relationship.getSelectedItemPosition()]);
        saveDataElement("hxCXbI5J2YS", caregiver.getText().toString());
        saveDataElement("Fs89NLB2FrA", weight.getText().toString());
        saveDataElement("LpvdWM4YuRq", length.getText().toString());


        //finishEnrollment();
        /*
        ActivityStarter.startActivity(
                this,
                ChildDetailsActivity.getTrackedEntityInstancesActivityIntent(
                        this,
                        trackedEntityInstance.uid()
                ),true
        );
         */
        ActivityStarter.startActivity(
                this,
                ChildDetailsActivity.getTrackedEntityInstancesActivityIntent(
                        this,
                        teiUid
                ),true
        );
    }

    private void selectDateRegistration(int year, int month, int day)
    {
        System.out.println("Clicked et date");
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
        System.out.println("Clicked et date");
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.DATE, -7*4*12*5); // subtract 5 years from now
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListenerDob, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.getDatePicker().setMinDate(c2.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void selectDateMotherDOB(int year, int month, int day)
    {
        System.out.println("Clicked et date");
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

    private int getSpinnerSelection(String dataElement, String [] array)
    {
        int itemPosition = -1;
        String stringElement = getDataElement(dataElement);
        for(int i =0; i<array.length; i++)
        {
            if(array[i].equals(stringElement))
            {
                itemPosition = i;
            }
        }
        return itemPosition;
    }

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
