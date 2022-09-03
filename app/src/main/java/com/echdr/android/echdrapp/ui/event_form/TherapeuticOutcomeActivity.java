package com.echdr.android.echdrapp.ui.event_form;

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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.processors.PublishProcessor;

public class TherapeuticOutcomeActivity extends AppCompatActivity {

    private String eventUid;
    private String programUid;
    private String selectedChild;
    private TherapeuticOutcomeActivity.FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private Spinner spinner_Enrollment;
    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;
    protected String[] other_type_array;
    protected String[] other_type_array_english;

    private TrackedEntityAttributeValue birthday;


    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               TherapeuticOutcomeActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, TherapeuticOutcomeActivity.class);
        intent.putExtra(TherapeuticOutcomeActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(TherapeuticOutcomeActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(TherapeuticOutcomeActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(TherapeuticOutcomeActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(TherapeuticOutcomeActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theraputic_outcome);

        textView_Date = findViewById(R.id.editTextDateTeraOutcome);
        spinner_Enrollment = findViewById(R.id.tera_outcome_Enrollment_spinner);
        saveButton = findViewById(R.id.teraOutcomeSave);
        datePicker = findViewById(R.id.TeraOutcome_date_pick);

        context = this;

        eventUid = getIntent().getStringExtra(TherapeuticOutcomeActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(TherapeuticOutcomeActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(TherapeuticOutcomeActivity.IntentExtra.TEI_ID.name());
        formType = TherapeuticOutcomeActivity.FormType.valueOf(getIntent().getStringExtra(TherapeuticOutcomeActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(TherapeuticOutcomeActivity.IntentExtra.OU_UID.name());

        other_type_array = getResources().getStringArray(R.array.tera_outcome_type);
        other_type_array_english = getResources().getStringArray(R.array.tera_outcome_type_english);

        engineInitialization = PublishProcessor.create();

        birthday = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(selectedChild)
                .byTrackedEntityAttribute().eq("qNH202ChkV3")
                .one().blockingGet();
        //Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        String s_day = (String) DateFormat.format("dd", date); // 20
        String s_monthNumber = (String) DateFormat.format("MM", date); // 06
        String s_year = (String) DateFormat.format("yyyy", date); // 2013

        final int year = Integer.parseInt(s_year);
        final int month = Integer.parseInt(s_monthNumber);
        final int day = Integer.parseInt(s_day);

        textView_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, android.R.style.Theme_Holo_Light_Dialog, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = null;
                try {
                    dob = formatter.parse(birthday.value());
                    datePickerDialog.getDatePicker().setMinDate(dob.getTime());

                    Calendar c = Calendar.getInstance();
                    c.setTime(dob);
                    c.add(Calendar.DATE, 365*5+2);
                    long minimum_value = Math.min(c.getTimeInMillis(), System.currentTimeMillis());

                    datePickerDialog.getDatePicker().setMaxDate(minimum_value);
                    //datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datePickerDialog.show();
            }
        });

        datePicker.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, android.R.style.Theme_Holo_Light_Dialog, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = null;
                try {
                    dob = formatter.parse(birthday.value());
                    datePickerDialog.getDatePicker().setMinDate(dob.getTime());

                    Calendar c = Calendar.getInstance();
                    c.setTime(dob);
                    c.add(Calendar.DATE, 365*5+2);
                    long minimum_value = Math.min(c.getTimeInMillis(), System.currentTimeMillis());

                    datePickerDialog.getDatePicker().setMaxDate(minimum_value);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth);
                textView_Date.setText(date);
            }
        };

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.tera_outcome_type,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        spinner_Enrollment.setAdapter(adapter);
        spinner_Enrollment.setOnItemSelectedListener(new TherapeuticOutcomeActivity.EnrollmentTypeSpinnerClass());


        // Load the existing values - form.CHECK
        if (formType == TherapeuticOutcomeActivity.FormType.CHECK) {
            System.out.println(getDataElement("xi20olIPIsb")); // type
            System.out.println(getDataElement("HXin8cvKgVq")); // date

            // set date
            try {
                String prev_date = getDataElement("HXin8cvKgVq");
                if (!prev_date.isEmpty()) {
                    textView_Date.setText(prev_date);
                }
            } catch (Exception e) {
                textView_Date.setText("");
            }


            // set enrollment type
            spinner_Enrollment.setSelection(
                    getSpinnerSelection("xi20olIPIsb", other_type_array));

        } else {
            textView_Date.setText(getString(R.string.date_button_text));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveElements();
            }
        });


        if (EventFormService.getInstance().init(
                Sdk.d2(),
                eventUid,
                programUid,
                getIntent().getStringExtra(TherapeuticOutcomeActivity.IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();


    }

    class EnrollmentTypeSpinnerClass implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            Toast.makeText(v.getContext(), "Your choose :" +
                    other_type_array[position], Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        EventFormService.clear();
        super.onDestroy();
    }

    private void finishEnrollment() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (formType == TherapeuticOutcomeActivity.FormType.CREATE)
            EventFormService.getInstance().delete();
        setResult(RESULT_CANCELED);
        finish();
    }

    private String getDataElement(String dataElement) {
        TrackedEntityDataValueObjectRepository valueRepository =
                Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                        .value(
                                eventUid,
                                dataElement
                        );

        String currentValue = valueRepository.blockingExists() ?
                valueRepository.blockingGet().value() : "";

        return currentValue;
    }

    private void saveDataElement(String dataElement, String value) {
        TrackedEntityDataValueObjectRepository valueRepository;
        try {
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                    .value(
                            EventFormService.getInstance().getEventUid(),
                            dataElement
                    );
        } catch (Exception e) {
            EventFormService.getInstance().init(
                    Sdk.d2(),
                    eventUid,
                    programUid,
                    getIntent().getStringExtra(TherapeuticOutcomeActivity.IntentExtra.OU_UID.name()));
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                    .value(
                            EventFormService.getInstance().getEventUid(),
                            dataElement
                    );
        }

        String currentValue = valueRepository.blockingExists() ?
                valueRepository.blockingGet().value() : "";

        if (currentValue == null)
            currentValue = "";

        try {
            if (!isEmpty(value)) {
                valueRepository.blockingSet(value);
            } else {
                valueRepository.blockingDeleteIfExist();
            }
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        } finally {
            if (!value.equals(currentValue)) {
                engineInitialization.onNext(true);
            }
        }
    }

    private void saveElements() {
        if (textView_Date.getText().toString().equals(getString(R.string.date_button_text)) ||
                textView_Date.getText().toString().isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage(getString(R.string.date));
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

        if (spinner_Enrollment.getSelectedItemPosition() == 4){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = null;
            try {
                dob = formatter.parse(birthday.value());
                Calendar c = Calendar.getInstance();
                c.setTime(dob);
                long minimum_value =  System.currentTimeMillis() - c.getTimeInMillis();

                if (minimum_value < 157784630000L){
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                    builder2.setMessage("The Child is not 5 years old");
                    builder2.setCancelable(true);

                    builder2.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert12 = builder2.create();
                    alert12.show();
                    return;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // un-enroll from the program
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.baby_girl);
        builderSingle.setTitle(getString(R.string.unenroll));
        builderSingle.setMessage(getString(R.string.unenroll_thera));

        builderSingle.setNegativeButton("un-enroll", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                // get latest enrollment
                List<Enrollment> enrollmentStatus = Sdk.d2().enrollmentModule().enrollments()
                        .byTrackedEntityInstance().eq(selectedChild)
                        .byProgram().eq("CoGsKgEG4O0")
                        .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                        .blockingGet();

                String enrollmentID = "";

                if(!enrollmentStatus.isEmpty())
                {
                    enrollmentID = enrollmentStatus.get(0).uid();
                }

                EnrollmentObjectRepository rep = Sdk.d2().enrollmentModule().enrollments()
                        .uid(enrollmentID);
                try {
                    rep.setStatus(EnrollmentStatus.COMPLETED);

                    saveDataElement("HXin8cvKgVq", textView_Date.getText().toString());
                    saveDataElement("xi20olIPIsb",
                            other_type_array_english[spinner_Enrollment.getSelectedItemPosition()]);

                    finishEnrollment();

                } catch (D2Error d2Error) {
                    d2Error.printStackTrace();
                    Toast.makeText(context, "Un-enrolling unsuccessful",
                            Toast.LENGTH_LONG).show();
                }


                dialog.dismiss();
                return;
            }
        });

        builderSingle.show();


    }

    private void selectDate(int year, int month, int day) {
        System.out.println("Clicked et date");
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private int getSpinnerSelection(String dataElement, String[] array) {
        int itemPosition = -1;
        String stringElement = getDataElement(dataElement);
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(stringElement)) {
                itemPosition = i;
            }
        }
        return itemPosition;
    }
}


