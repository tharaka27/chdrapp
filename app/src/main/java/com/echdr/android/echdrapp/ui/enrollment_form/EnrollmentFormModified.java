package com.echdr.android.echdrapp.ui.enrollment_form;

import static android.text.TextUtils.isEmpty;

import android.app.DatePickerDialog;
import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EnrollmentFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.echdr.android.echdrapp.ui.event_form.SupplementaryIndicationActivity;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;
import org.hisp.dhis.rules.RuleEngine;

import java.util.Date;
import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.PublishProcessor;

public class EnrollmentFormModified extends AppCompatActivity {


    private CompositeDisposable disposable;
    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private RuleEngine ruleEngine;

    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;
    protected String[] sexArray;
    protected String[] ethinicityArray;
    protected String[] sectorArray;
    protected String[] eduLevelArray;
    protected String[] occupationArray;
    protected String[] relationshipArray;

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

        textView_date_of_registration = findViewById(R.id.editTextDateofRegistration);
        datePicker_registration = findViewById(R.id.date_pick_registration);
        GNArea = findViewById(R.id.gn_area);
        immuneNum = findViewById(R.id.immuneNum);
        name = findViewById(R.id.name);
        sex = findViewById(R.id.sex);
        textView_dob = findViewById(R.id.editTextDateofBirth);
        datePicker_dob = findViewById(R.id.date_pick_birth);
        ethnicity = findViewById(R.id.ethnicity);
        address = findViewById(R.id.address);
        sector = findViewById(R.id.sector);
        landNumber = findViewById(R.id.lNumber);
        mobileNumber = findViewById(R.id.mNumber);
        motherName = findViewById(R.id.motherName);
        nic = findViewById(R.id.nic);
        textView_mother_dob = findViewById(R.id.editTextMomDateofBirth);
        datePicker_mother_dob = findViewById(R.id.date_pick_mom_birth);
        numberOfChildren = findViewById(R.id.number_of_children);
        eduLevel = findViewById(R.id.edu_level);
        occupation = findViewById(R.id.occupation);
        relationship = findViewById(R.id.relationship);
        caregiver = findViewById(R.id.caregiverName);
        weight = findViewById(R.id.weight);
        length = findViewById(R.id.length);

        saveButton = findViewById(R.id.childSave);
        //saveButton.setOnClickListener(this::finishEnrollment);

        context = this;

        teiUid = getIntent().getStringExtra(IntentExtra.TEI_UID.name());

        sexArray = getResources().getStringArray(R.array.sex);
        ethinicityArray = getResources().getStringArray(R.array.ethnicity);
        sectorArray = getResources().getStringArray(R.array.sector);
        eduLevelArray = getResources().getStringArray(R.array.highestEdu);
        occupationArray = getResources().getStringArray(R.array.occupation);
        relationshipArray = getResources().getStringArray(R.array.relationship);


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
        final int month = Integer.parseInt(s_monthNumber);
        final int day = Integer.parseInt(s_day);

        textView_date_of_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicked et date");
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, android.R.style.Theme_Holo_Light_Dialog, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        datePicker_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(year, month, day);
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth);
                textView_date_of_registration.setText(date);
            }
        };

        //setting spinners

        ArrayAdapter<CharSequence> sexadapter = ArrayAdapter.createFromResource(context,
                R.array.sex,
                android.R.layout.simple_spinner_item);
        sexadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sex.setAdapter(sexadapter);
        sex.setOnItemSelectedListener(new EnrollmentFormModified.EnrollmentTypeSpinnerClass());

        ArrayAdapter<CharSequence> ethinicityadapter = ArrayAdapter.createFromResource(context,
                R.array.ethnicity,
                android.R.layout.simple_spinner_item);
        ethinicityadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ethnicity.setAdapter(ethinicityadapter);
        ethnicity.setOnItemSelectedListener(new EnrollmentFormModified.EnrollmentTypeSpinnerClass());

        ArrayAdapter<CharSequence> eduadapter = ArrayAdapter.createFromResource(context,
                R.array.highestEdu,
                android.R.layout.simple_spinner_item);
        eduadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eduLevel.setAdapter(eduadapter);
        eduLevel.setOnItemSelectedListener(new EnrollmentFormModified.EnrollmentTypeSpinnerClass());

        ArrayAdapter<CharSequence> sectoradapter = ArrayAdapter.createFromResource(context,
                R.array.sector,
                android.R.layout.simple_spinner_item);
        sectoradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sector.setAdapter(sectoradapter);
        sector.setOnItemSelectedListener(new EnrollmentFormModified.EnrollmentTypeSpinnerClass());

        ArrayAdapter<CharSequence> occuadapter = ArrayAdapter.createFromResource(context,
                R.array.occupation,
                android.R.layout.simple_spinner_item);
        occuadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occupation.setAdapter(occuadapter);
        occupation.setOnItemSelectedListener(new EnrollmentFormModified.EnrollmentTypeSpinnerClass());

        ArrayAdapter<CharSequence> relationadapter = ArrayAdapter.createFromResource(context,
                R.array.relationship,
                android.R.layout.simple_spinner_item);
        relationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relationship.setAdapter(relationadapter);
        relationship.setOnItemSelectedListener(new EnrollmentFormModified.EnrollmentTypeSpinnerClass());


        // setting registration date
        try {
            String prev_date = getDataElement("kYfIkz2M6En");
            if (!prev_date.isEmpty()) {
                textView_date_of_registration.setText(prev_date);
            }
        } catch (Exception e) {
            textView_date_of_registration.setText("");
        }

        // setting GN area
        try {
            String prev_gn_area = getDataElement("upQGjAHBjzu");
            if (!prev_gn_area.isEmpty()) {
                GNArea.setText(prev_gn_area);
            }
        } catch (Exception e) {
            GNArea.setText("");
        }

        // setting birth and immun number
        try {
            String prev_birth_num = getDataElement("h2ATdtJguMq");
            if (!prev_birth_num.isEmpty()) {
                immuneNum.setText(prev_birth_num);
            }
        } catch (Exception e) {
            immuneNum.setText("");
        }

        // setting child name
        try {
            String prev_name = getDataElement("zh4hiarsSD5");
            if (!prev_name.isEmpty()) {
                name.setText(prev_name);
            }
        } catch (Exception e) {
            name.setText("");
        }

        // setting child dob
        try {
            String prev_child_dob = getDataElement("qNH202ChkV3");
            if (!prev_child_dob.isEmpty()) {
                textView_dob.setText(prev_child_dob);
            }
        } catch (Exception e) {
            textView_dob.setText("");
        }

        // setting address
        try {
            String prev_address = getDataElement("D9aC5K6C6ne");
            if (!prev_address.isEmpty()) {
                address.setText(prev_address);
            }
        } catch (Exception e) {
            address.setText("");
        }

        // setting land number
        try {
            String prev_land_number = getDataElement("cpcMXDhQouL");
            if (!prev_land_number.isEmpty()) {
                landNumber.setText(prev_land_number);
            }
        } catch (Exception e) {
            landNumber.setText("");
        }

        // setting mobile number
        try {
            String prev_mobile_number = getDataElement("LYRf4eIUVuN");
            if (!prev_mobile_number.isEmpty()) {
                mobileNumber.setText(prev_mobile_number);
            }
        } catch (Exception e) {
            mobileNumber.setText("");
        }

        // setting mother name
        try {
            String prev_mom_name = getDataElement("K7Fxa2wv2Rx");
            if (!prev_mom_name.isEmpty()) {
                motherName.setText(prev_mom_name);
            }
        } catch (Exception e) {
            motherName.setText("");
        }

        // setting nic
        try {
            String prev_nic = getDataElement("K7Fxa2wv2Rx");
            if (!prev_nic.isEmpty()) {
                nic.setText(prev_nic);
            }
        } catch (Exception e) {
            nic.setText("");
        }

        // setting number of childrean
        try {
            String prev_number_of_children = getDataElement("Gy4bCBxNuo4");
            if (!prev_number_of_children.isEmpty()) {
                numberOfChildren.setText(prev_number_of_children);
            }
        } catch (Exception e) {
            numberOfChildren.setText("");
        }

        // setting birth weight
        try {
            String prev_birth_weight = getDataElement("Fs89NLB2FrA");
            if (!prev_birth_weight.isEmpty()) {
                weight.setText(prev_birth_weight);
            }
        } catch (Exception e) {
            weight.setText("");
        }

        // setting birth length
        try {
            String prev_birth_length = getDataElement("LpvdWM4YuRq");
            if (!prev_birth_length.isEmpty()) {
                length.setText(prev_birth_length);
            }
        } catch (Exception e) {
            length.setText("");
        }

    }

    class EnrollmentTypeSpinnerClass implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            Toast.makeText(v.getContext(), "Your choose :" +
                    sexArray[position], Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private String getDataElement(String dataElement) {
        TrackedEntityAttributeValueObjectRepository valueRepository =
                Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                        .value(
                                teiUid,
                                dataElement
                                //getIntent().getStringExtra(EnrollmentFormModified.IntentExtra.TEI_UID.name()
                                //)
                        );
        String currentValue = valueRepository.blockingExists() ?
                valueRepository.blockingGet().value() : "";

        return currentValue;
    }

    private void selectDate(int year, int month, int day)
    {
        System.out.println("Clicked et date");
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private String makeDate(int year, int month, int DayofMonth)
    {
        return DayofMonth + "/" + month + "/" + year;
    }
}
