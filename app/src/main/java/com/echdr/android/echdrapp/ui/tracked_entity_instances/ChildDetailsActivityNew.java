package com.echdr.android.echdrapp.ui.tracked_entity_instances;

import static android.text.TextUtils.isEmpty;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.echdr.android.echdrapp.ui.enrollment_form.EnrollmentFormActivity;
import com.echdr.android.echdrapp.ui.enrollment_form.EnrollmentFormModified;
import com.echdr.android.echdrapp.ui.event_form.SupplementaryIndicationActivity;
import com.echdr.android.echdrapp.ui.events.EventsActivity;
import com.echdr.android.echdrapp.ui.tracked_entity_instances.ChildDetailsActivity;

import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.rules.RuleEngine;
import org.hisp.dhis.rules.models.RuleAction;
import org.hisp.dhis.rules.models.RuleActionHideField;
import org.hisp.dhis.rules.models.RuleEffect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

public class ChildDetailsActivityNew extends AppCompatActivity {


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

    private ImageButton edit_button;

    private Button submitButton;

    private String anthropometryEnrollmentID;
    private String otherEnrollmentID;
    private String overweightEnrollmentID;
    private String stuntingEnrollmentID;
    private String supplementaryEnrollmentID;
    private String therapeuticEnrollmentID;

    private String orgUnit;

    private enum IntentExtra {
        TEI_UID
    }

    public static Intent getTrackedEntityInstancesActivityIntent(Context context, String uid) {
        Intent intent = new Intent(context, ChildDetailsActivityNew.class);
        intent.putExtra(ChildDetailsActivityNew.IntentExtra.TEI_UID.name(), uid);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details_new);

        engineInitialization = PublishProcessor.create();

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
        occu_specification = findViewById(R.id.occu_specifcation);
        relationship = findViewById(R.id.relationship);
        caregiver = findViewById(R.id.caregiverName);
        weight = findViewById(R.id.weight);
        length = findViewById(R.id.length);

        edit_button = findViewById(R.id.edit_btn);
        submitButton = findViewById(R.id.submit);

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

        ArrayAdapter<CharSequence> sexadapter = ArrayAdapter.createFromResource(context,
                R.array.sex,
                android.R.layout.simple_spinner_item);
        sexadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sex.setAdapter(sexadapter);
        sex.setOnItemSelectedListener(new ChildDetailsActivityNew.EnrollmentTypeSpinnerClass());
        sex.setEnabled(false);

        ArrayAdapter<CharSequence> ethinicityadapter = ArrayAdapter.createFromResource(context,
                R.array.ethnicity,
                android.R.layout.simple_spinner_item);
        ethinicityadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ethnicity.setAdapter(ethinicityadapter);
        ethnicity.setOnItemSelectedListener(new ChildDetailsActivityNew.EnrollmentTypeSpinnerClass());
        ethnicity.setEnabled(false);

        ArrayAdapter<CharSequence> eduadapter = ArrayAdapter.createFromResource(context,
                R.array.highestEdu,
                android.R.layout.simple_spinner_item);
        eduadapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        eduLevel.setAdapter(eduadapter);
        eduLevel.setOnItemSelectedListener(new ChildDetailsActivityNew.EnrollmentTypeSpinnerClass());
        eduLevel.setEnabled(false);

        ArrayAdapter<CharSequence> sectoradapter = ArrayAdapter.createFromResource(context,
                R.array.sector,
                android.R.layout.simple_spinner_item);
        sectoradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sector.setAdapter(sectoradapter);
        sector.setOnItemSelectedListener(new ChildDetailsActivityNew.EnrollmentTypeSpinnerClass());
        sector.setEnabled(false);

        ArrayAdapter<CharSequence> occuadapter = ArrayAdapter.createFromResource(context,
                R.array.occupation,
                android.R.layout.simple_spinner_item);
        occuadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occupation.setAdapter(occuadapter);
        occupation.setOnItemSelectedListener(new ChildDetailsActivityNew.EnrollmentTypeSpinnerClass());
        occupation.setEnabled(false);

        ArrayAdapter<CharSequence> relationadapter = ArrayAdapter.createFromResource(context,
                R.array.relationship,
                android.R.layout.simple_spinner_item);
        relationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relationship.setAdapter(relationadapter);
        relationship.setOnItemSelectedListener(new ChildDetailsActivityNew.EnrollmentTypeSpinnerClass());
        relationship.setEnabled(false);

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

        setListenerMotherDob = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth);
                textView_mother_dob.setText(date);
            }
        };

        // setting date of registration
        try {
            String prev_date = getDataElement("zmCxHpWgOOv");
            System.out.println("results out " + prev_date );
            if (!prev_date.isEmpty()) {
                textView_date_of_registration.setText(prev_date);
            }
        } catch (Exception e) {
            textView_date_of_registration.setText("");
        }

        // setting mother DOB date
        try {
            String prev_date = getDataElement("kYfIkz2M6En");
            if (!prev_date.isEmpty()) {
                textView_mother_dob.setText(prev_date);
            }
        } catch (Exception e) {
            textView_mother_dob.setText("");
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
        //select sex
        sex.setSelection(
                getSpinnerSelection("lmtzQrlHMYF", sex_english_only));

        System.out.println("sex is " + getDataElement("lmtzQrlHMYF"));

        // setting child dob
        try {
            String prev_child_dob = getDataElement("qNH202ChkV3");
            if (!prev_child_dob.isEmpty()) {
                textView_dob.setText(prev_child_dob);
            }
        } catch (Exception e) {
            textView_dob.setText("");
        }

        //select ethnicity
        ethnicity.setSelection(
                getSpinnerSelection("b9CoAneYYys", ethinicity_english_only));

        System.out.println("ethnicity is " + getDataElement("b9CoAneYYys"));

        // setting address
        try {
            String prev_address = getDataElement("D9aC5K6C6ne");
            if (!prev_address.isEmpty()) {
                address.setText(prev_address);
            }
        } catch (Exception e) {
            address.setText("");
        }

        //select sector
        sector.setSelection(
                getSpinnerSelection("igjlkmMF81X", sector_english_only));
        System.out.println("sector is " + getDataElement("igjlkmMF81X"));

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
            String prev_nic = getDataElement("Gzjb3fp9FSe");
            if (!prev_nic.isEmpty()) {
                nic.setText(prev_nic);
            }
        } catch (Exception e) {
            nic.setText("");
        }

        // setting number of children
        try {
            String prev_number_of_children = getDataElement("Gy4bCBxNuo4");
            if (!prev_number_of_children.isEmpty()) {
                numberOfChildren.setText(prev_number_of_children);
            }
        } catch (Exception e) {
            numberOfChildren.setText("");
        }

        // setting occupation specification
        try {
            String o_specification = getDataElement("s7Rde0kFOFb");
            if (!o_specification.isEmpty()) {
                occu_specification.setText(o_specification);
            }
        } catch (Exception e) {
            occu_specification.setText("");
        }

        // setting caregiver name
        try {
            String caregiverName = getDataElement("hxCXbI5J2YS");
            if (!caregiverName.isEmpty()) {
                caregiver.setText(caregiverName);
            }
        } catch (Exception e) {
            caregiver.setText("");
        }

        //select education
        eduLevel.setSelection(
                getSpinnerSelection("GMNSaaq4xST", eduLevel_english_only));
        System.out.println("eduLevel is " + getDataElement("GMNSaaq4xST"));

        //select occupation
        occupation.setSelection(
                getSpinnerSelection("Srxv0vniOnf", occupation_english_only));
        System.out.println("occupation is " + getDataElement("Srxv0vniOnf"));

        //select relationship
        relationship.setSelection(
                getSpinnerSelection("ghN8XfnlU5V", relationship_english_only));
        System.out.println("relationship is " + getDataElement("ghN8XfnlU5V"));

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

        getEnrollment();
        EnrollToPrograms();
        submitButton.setEnabled(false);

        edit_button.setOnClickListener(view ->{
            textView_date_of_registration.setEnabled(false);
            datePicker_registration.setEnabled(false);
            GNArea.setEnabled(true);
            immuneNum.setEnabled(false);
            name.setEnabled(true);
            sex.setEnabled(false);
            textView_dob.setEnabled(false);
            datePicker_dob.setEnabled(false);
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

            //submitButton.setClickable(true);
            submitButton.setEnabled(true);
            submitButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_button));
        });


            submitButton.setOnClickListener(view -> {

                // Immunization number validation
                String pattern = "[0-9][0-9]\\/[0-1][0-9]\\/[0-3][0-9]";
                Matcher m1 = null;
                Matcher m2 = null;
                Pattern r = Pattern.compile(pattern);

                String patternLPhone = "[0-9]{10}";
                Pattern q = Pattern.compile(patternLPhone);

                if (immuneNum.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                    builder2.setMessage("Birth and Immunization Number is not filled");
                    builder2.setCancelable(true);

                    builder2.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //return;
                                }
                            });

                    AlertDialog alert12 = builder2.create();
                    alert12.show();
                    return;
                    //Toast.makeText(EnrollmentFormModified.this, "Birth and Immunization Number is not filled ", Toast.LENGTH_LONG).show();
                } else {
                    m1 = r.matcher(immuneNum.getText().toString().trim());
                    if (m1.find()) {
                        //Toast.makeText(EnrollmentFormModified.this, "Birth and Immunization Number matched", Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog.Builder builder3 = new AlertDialog.Builder(context);
                        builder3.setMessage("Birth and Immunization Number not matched");
                        builder3.setCancelable(true);

                        builder3.setNegativeButton(
                                "Close",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        //return;
                                    }
                                });

                        AlertDialog alert13 = builder3.create();
                        alert13.show();
                        return;
                        //Toast.makeText(EnrollmentFormModified.this, "NO MATCH", Toast.LENGTH_LONG).show();
                    }
                }

                if(landNumber.getText().toString().isEmpty()){
                    AlertDialog.Builder builder3 = new AlertDialog.Builder(context);
                    builder3.setMessage("Land Number is not filled");
                    builder3.setCancelable(true);

                    builder3.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //return;
                                }
                            });

                    AlertDialog alert13 = builder3.create();
                    alert13.show();
                    return;
                    //Toast.makeText(EnrollmentFormModified.this, "Birth and Immunization Number is not filled ", Toast.LENGTH_LONG).show();
                } else {
                    m2 = q.matcher(landNumber.getText().toString().trim());
                    if (m2.find()) {
                        //Toast.makeText(EnrollmentFormModified.this, "Land Number matched", Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog.Builder builder4 = new AlertDialog.Builder(context);
                        builder4.setMessage("Land Number not matched");
                        builder4.setCancelable(true);

                        builder4.setNegativeButton(
                                "Close",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        //return;
                                    }
                                });

                        AlertDialog alert14 = builder4.create();
                        alert14.show();
                        return;
                        //Toast.makeText(EnrollmentFormModified.this, "NO MATCH", Toast.LENGTH_LONG).show();
                    }
                }

                if(mobileNumber.getText().toString().isEmpty()){
                    AlertDialog.Builder builder8 = new AlertDialog.Builder(context);
                    builder8.setMessage("Mobile Number is not filled");
                    builder8.setCancelable(true);

                    builder8.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //return;
                                }
                            });

                    AlertDialog alert18 = builder8.create();
                    builder8.show();
                    return;
                    //Toast.makeText(EnrollmentFormModified.this, "Birth and Immunization Number is not filled ", Toast.LENGTH_LONG).show();
                } else {
                    m2 = q.matcher(mobileNumber.getText().toString().trim());
                    if (m2.find()) {
                        //Toast.makeText(EnrollmentFormModified.this, "Land Number matched", Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog.Builder builder8 = new AlertDialog.Builder(context);
                        builder8.setMessage("Mobile Number not matched");
                        builder8.setCancelable(true);

                        builder8.setNegativeButton(
                                "Close",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        //return;
                                    }
                                });

                        AlertDialog alert18 = builder8.create();
                        builder8.show();
                        return;
                        //Toast.makeText(EnrollmentFormModified.this, "NO MATCH", Toast.LENGTH_LONG).show();
                    }
                }
                if( numberOfChildren.getText().toString().isEmpty() ||
                        Integer.parseInt(numberOfChildren.getText().toString()) < 0
                        || Integer.parseInt(numberOfChildren.getText().toString()) >= 20)
                {
                    AlertDialog.Builder builder5 = new AlertDialog.Builder(context);
                    builder5.setMessage("Number of Children is allowed up to 20");
                    builder5.setCancelable(true);

                    builder5.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //return;
                                }
                            });

                    AlertDialog alert15 = builder5.create();
                    alert15.show();
                    return;
                }

                if(weight.getText().toString().isEmpty() ||
                        Integer.parseInt(weight.getText().toString()) < 500
                        || Integer.parseInt(weight.getText().toString()) >= 9999)
                {
                    AlertDialog.Builder builder6 = new AlertDialog.Builder(context);
                    builder6.setMessage("Weight in Grams is allowed 500-9999");
                    builder6.setCancelable(true);

                    builder6.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //return;
                                }
                            });

                    AlertDialog alert16 = builder6.create();
                    alert16.show();
                    return;
                }
                if(length.getText().toString().isEmpty() ||
                        Integer.parseInt(length.getText().toString()) < 10
                        || Integer.parseInt(length.getText().toString()) >= 99)
                {
                    AlertDialog.Builder builder7 = new AlertDialog.Builder(context);
                    builder7.setMessage("Length in Centimeters is allowed 10-99");
                    builder7.setCancelable(true);

                    builder7.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //return;
                                }
                            });

                    AlertDialog alert17 = builder7.create();
                    alert17.show();
                    return;
                }

                // Caregiver is not mother
                if( !relationship_english_only[relationship.getSelectedItemPosition()].equals("Mother")
                        && caregiver.getText().toString().isEmpty() )
                {
                    AlertDialog.Builder builder7 = new AlertDialog.Builder(context);
                    builder7.setMessage("If caregiver is not mother, caregiver name is mandatory.");
                    builder7.setCancelable(true);

                    builder7.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //return;
                                }
                            });

                    AlertDialog alert17 = builder7.create();
                    alert17.show();
                    return;
                }
                // Occupation specification is mandatory is self employed/retired/paid employement
                //<item>Self employment</item>
                //<item>Paid employment</item>
                //<item>Retired</item>
                if( occu_specification.getText().toString().isEmpty() &&
                        (occupation_english_only[occupation.getSelectedItemPosition()].equals("Retired") ||
                                occupation_english_only[occupation.getSelectedItemPosition()].equals("Self employment") ||
                                occupation_english_only[occupation.getSelectedItemPosition()].equals("Paid employment")) )
                {
                    AlertDialog.Builder builder7 = new AlertDialog.Builder(context);
                    builder7.setMessage("Occupation specification is mandatory is self employed/retired/paid.");
                    builder7.setCancelable(true);

                    builder7.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    //return;
                                }
                            });

                    AlertDialog alert17 = builder7.create();
                    alert17.show();
                    return;
                }

                saveElements();
            });

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
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                    .value(
                            teiUid,
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
        }
        /*
        finally {
            if (!value.equals(currentValue)) {
                engineInitialization.onNext(true);
            }
        }*/
    }

    private void saveElements()
    {
        if(GNArea.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("GN Area is not filled");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        if(immuneNum.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Birth and Immunization Number is not filled");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        if(name.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Name of the child is not filled");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        if(textView_dob.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Date of Birth is not filled");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        if(address.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Address is not filled");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        if(motherName.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Name of Mother is not filled");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        if(textView_mother_dob.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Mother's date of birth is not filled");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        if(weight.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Birth weight (in grams) is not filled");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }
        if(length.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Length at birth (in cm) is not filled");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
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
        System.out.println("selected item " +  ethnicity.getSelectedItemPosition());
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


        ActivityStarter.startActivity(
                this,
                ChildDetailsActivityNew.getTrackedEntityInstancesActivityIntent(
                        this,
                        teiUid
                ),true
        );
    }

    private void selectDateRegistration(int year, int month, int day)
    {
        System.out.println("Clicked et date");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -7); // subtract 5 years from now
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListenerDob, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

    private void getEnrollment(){

        // get anthropometry latest enrollment
        List<Enrollment> AnthropometryStatus = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(teiUid)
                .byProgram().eq("hM6Yt9FQL0n")
                //.orderByLastUpdated(RepositoryScope.OrderByDirection.DESC)
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        for(int i=0;i<AnthropometryStatus.size(); i++)
        {
            System.out.print("Date :");
            System.out.print(AnthropometryStatus.get(i).created());
            System.out.print(" uid :");
            System.out.print(AnthropometryStatus.get(i).uid());
            System.out.print(" status :");
            System.out.println(AnthropometryStatus.get(i).status().toString());
        }

        System.out.println("Anthropometry size " + String.valueOf(AnthropometryStatus.size()) );

        if(!AnthropometryStatus.isEmpty())
        {
            System.out.println("Anthropometry is " + AnthropometryStatus.get(0).status().toString() );
            anthropometryEnrollmentID = AnthropometryStatus.get(0).uid();
            if ( AnthropometryStatus.get(0).status().equals(EnrollmentStatus.ACTIVE)) {
                antopoEnrolled.setVisibility(View.VISIBLE);
                antopoNotEnrolled.setVisibility(View.GONE);
            }
        }

        // get other health/non health latest enrollment
        List<Enrollment> otherStatus = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(teiUid)
                .byProgram().eq("iUgzznPsePB")
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        if(!otherStatus.isEmpty())
        {
            otherEnrollmentID =  otherStatus.get(0).uid();
            if ( otherStatus.get(0).status().equals(EnrollmentStatus.ACTIVE)) {
                otherHealthEnrolled.setVisibility(View.VISIBLE);
                otherHealthNotEnrolled.setVisibility(View.GONE);
            }
        }

        // get other overweight/obesity latest enrollment
        List<Enrollment> overweightStatus = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(teiUid)
                .byProgram().eq("JsfNVX0hdq9")
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        if(!overweightStatus.isEmpty())
        {
            overweightEnrollmentID =  overweightStatus.get(0).uid();
            if ( overweightStatus.get(0).status().equals(EnrollmentStatus.ACTIVE)) {
                overweightEnrolled.setVisibility(View.VISIBLE);
                overweightNotEnrolled.setVisibility(View.GONE);
            }
        }

        // get other stunting latest enrollment
        List<Enrollment> stuntingStatus = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(teiUid)
                .byProgram().eq("lSSNwBMiwrK")
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        if(!stuntingStatus.isEmpty())
        {
            stuntingEnrollmentID = stuntingStatus.get(0).uid();
            if ( stuntingStatus.get(0).status().equals(EnrollmentStatus.ACTIVE)) {
                stuntingEnrolled.setVisibility(View.VISIBLE);
                stuntingNotEnrolled.setVisibility(View.GONE);
            }
        }

        // get supplementary latest enrollment
        List<Enrollment> supplementaryStatus = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(teiUid)
                .byProgram().eq("tc6RsYbgGzm")
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        if(!supplementaryStatus.isEmpty())
        {
            supplementaryEnrollmentID = supplementaryStatus.get(0).uid();
            if ( supplementaryStatus.get(0).status().equals(EnrollmentStatus.ACTIVE)) {
                supplementaryEnrolled.setVisibility(View.VISIBLE);
                supplementaryNotEnrolled.setVisibility(View.GONE);
            }
        }

        // get therapeutic latest enrollment
        List<Enrollment> therapeuticStatus = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(teiUid)
                .byProgram().eq("CoGsKgEG4O0")
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        if(!therapeuticStatus.isEmpty())
        {
            therapeuticEnrollmentID = therapeuticStatus.get(0).uid();
            if ( therapeuticStatus.get(0).status().equals(EnrollmentStatus.ACTIVE)) {
                therapeuticEnrolled.setVisibility(View.VISIBLE);
                therapeuticNotEnrolled.setVisibility(View.GONE);
            }
        }
    }

    private void EnrollToPrograms(){

        List<TrackedEntityInstance> s = Sdk.d2().trackedEntityModule()
                .trackedEntityInstances().byUid().eq(teiUid).blockingGet();

        for (TrackedEntityInstance v: s) {
            orgUnit = v.organisationUnit();
            System.out.println("Organization Unit: " + orgUnit);
        }

        String orgUnit2 = Sdk.d2().organisationUnitModule().organisationUnits()
                .byProgramUids(Collections.singletonList("hM6Yt9FQL0n"))
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .one().blockingGet().uid();
        System.out.println("Organization Unit 2 : " + orgUnit2);

        orgUnit = orgUnit2;


        overweightNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ActivityStarter.startActivity(ChildDetailsActivityNew.this,
                //        EnrollmentFormActivity.getFormActivityIntent(getApplicationContext(), teiUid, "JsfNVX0hdq9", orgUnit), false);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Please confirm overweight program enrollment");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = EnrollmentFormActivity.getFormActivityIntent(ChildDetailsActivityNew.this, teiUid, "JsfNVX0hdq9", orgUnit);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                //return;
                            }
                        });

                AlertDialog alert12 = builder.create();
                alert12.show();
                return;

            }
        });

        overweightEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EventsActivity.getIntent(getApplicationContext(), "JsfNVX0hdq9",
                        teiUid, overweightEnrollmentID);
                startActivity(intent);
            }
        });

        antopoNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                ActivityStarter.startActivity(ChildDetailsActivity.this,
                        EnrollmentFormActivity.getFormActivityIntent(getApplicationContext(), trackedEntityInstanceUid, "hM6Yt9FQL0n", orgUnit), false);
                 */
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
                else
                {
                    return;
                }

                // set the enrollment status to active based on the enrollment ID
                EnrollmentObjectRepository rep = Sdk.d2().enrollmentModule().enrollments()
                        .uid(anthropometryEnrollmentID);
                try {
                    rep.setStatus(EnrollmentStatus.ACTIVE);
                } catch (D2Error d2Error) {
                    d2Error.printStackTrace();
                    Toast.makeText(context, "re-enrolling unsuccessful",
                            Toast.LENGTH_LONG).show();
                }

                Intent intent = EventsActivity.getIntent(getApplicationContext(), "hM6Yt9FQL0n",
                        teiUid, anthropometryEnrollmentID);
                startActivity(intent);
                finish();

            }
        });

        antopoEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EventsActivity.getIntent(getApplicationContext(), "hM6Yt9FQL0n",
                        teiUid, anthropometryEnrollmentID);
                startActivity(intent);

            }
        });

        otherHealthNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ActivityStarter.startActivity(ChildDetailsActivityNew.this,
                //        EnrollmentFormActivity.getFormActivityIntent(getApplicationContext(), teiUid, "iUgzznPsePB", orgUnit), true);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Please confirm other health/non health program enrollment");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = EnrollmentFormActivity.getFormActivityIntent(ChildDetailsActivityNew.this, teiUid, "iUgzznPsePB", orgUnit);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                //return;
                            }
                        });

                AlertDialog alert12 = builder.create();
                alert12.show();
                return;
            }
        });

        otherHealthEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EventsActivity.getIntent(getApplicationContext(), "iUgzznPsePB",
                        teiUid, otherEnrollmentID);
                startActivity(intent);

            }
        });

        stuntingNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ActivityStarter.startActivity(ChildDetailsActivityNew.this,
                //        EnrollmentFormActivity.getFormActivityIntent(getApplicationContext(), teiUid, "lSSNwBMiwrK", orgUnit), true);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Please confirm stunting program enrollment");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = EnrollmentFormActivity.getFormActivityIntent(ChildDetailsActivityNew.this, teiUid, "lSSNwBMiwrK", orgUnit);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                //return;
                            }
                        });

                AlertDialog alert12 = builder.create();
                alert12.show();
                return;

            }
        });

        stuntingEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EventsActivity.getIntent(getApplicationContext(), "lSSNwBMiwrK",
                        teiUid, stuntingEnrollmentID);
                startActivity(intent);

            }
        });

        supplementaryNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ActivityStarter.startActivity(ChildDetailsActivityNew.this,
                 //       EnrollmentFormActivity.getFormActivityIntent(getApplicationContext(), teiUid, "tc6RsYbgGzm", orgUnit), true);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Please confirm supplementary program enrollment");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = EnrollmentFormActivity.getFormActivityIntent(ChildDetailsActivityNew.this, teiUid, "tc6RsYbgGzm", orgUnit);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                //return;
                            }
                        });

                AlertDialog alert12 = builder.create();
                alert12.show();
                return;

            }
        });

        supplementaryEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EventsActivity.getIntent(getApplicationContext(), "tc6RsYbgGzm",
                        teiUid, supplementaryEnrollmentID);
                startActivity(intent);

            }
        });

        therapeuticNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ActivityStarter.startActivity(ChildDetailsActivityNew.this,
                //        EnrollmentFormActivity.getFormActivityIntent(getApplicationContext(), teiUid, "CoGsKgEG4O0", orgUnit), true);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Please confirm therapeutical feeding program enrollment");
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = EnrollmentFormActivity.getFormActivityIntent(ChildDetailsActivityNew.this, teiUid, "CoGsKgEG4O0", orgUnit);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                //return;
                            }
                        });

                AlertDialog alert12 = builder.create();
                alert12.show();
                return;
            }
        });

        therapeuticEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = EventsActivity.getIntent(getApplicationContext(), "CoGsKgEG4O0",
                        teiUid, therapeuticEnrollmentID);
                startActivity(intent);

            }
        });

    }



}
