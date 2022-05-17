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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.processors.PublishProcessor;

public class OtherReasonForActivity extends AppCompatActivity {

    private String eventUid;
    private String programUid;
    private String selectedChild;
    private OtherReasonForActivity.FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private CheckBox checkbox_Severe_Acute;
    private CheckBox checkbox_Moderate_Acute;
    private CheckBox checkbox_Long_standing;
    private CheckBox checkbox_Underweight;
    private CheckBox checkbox_Overweight;
    private CheckBox checkbox_Stunting;


    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;

    private TrackedEntityAttributeValue birthday;

    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               OtherReasonForActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, OtherReasonForActivity.class);
        intent.putExtra(OtherReasonForActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(OtherReasonForActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(OtherReasonForActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(OtherReasonForActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(OtherReasonForActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_reason_for_enrollment);

        textView_Date = findViewById(R.id.reasonForEnrollmentDate);
        datePicker = findViewById(R.id.reason_for_enroll_date_pick);
        checkbox_Severe_Acute = findViewById(R.id.Severe_acute_Checkbox);
        checkbox_Moderate_Acute = findViewById(R.id.Moderate_acute_Checkbox);
        checkbox_Long_standing = findViewById(R.id.Long_standing_Checkbox);
        checkbox_Underweight = findViewById(R.id.Underweight_Checkbox);
        checkbox_Overweight = findViewById(R.id.Overweight_Checkbox);
        checkbox_Stunting = findViewById(R.id.Stunting_Checkbox);
        saveButton = findViewById(R.id.reasonForEnrollSave);

        context = this;

        eventUid = getIntent().getStringExtra(OtherReasonForActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(OtherReasonForActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(OtherReasonForActivity.IntentExtra.TEI_ID.name());
        formType = OtherReasonForActivity.FormType.valueOf(getIntent().getStringExtra(OtherReasonForActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(OtherReasonForActivity.IntentExtra.OU_UID.name());


        engineInitialization = PublishProcessor.create();

        // Get the birthday of the child
        birthday = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(selectedChild)
                .byTrackedEntityAttribute().eq("qNH202ChkV3")
                .one().blockingGet();

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
                System.out.println("Clicked et date");
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

        datePicker.setOnClickListener(new View.OnClickListener() {
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
                textView_Date.setText(date);
            }
        };

        if (formType == OtherReasonForActivity.FormType.CHECK) {
            System.out.println(getDataElement("Dpw5YPM1CFj")); // reason for enrollment date
            System.out.println(getDataElement("AOKp3oQPyYP")); // SAM
            System.out.println(getDataElement("QNV3Qb2kjx8")); // MAM
            System.out.println(getDataElement("Sw98c8KAEmr")); // long standing growth
            System.out.println(getDataElement("xkhQxmJ8X24")); // underweight
            System.out.println(getDataElement("dnLak5wmEzT")); // overweight
            System.out.println(getDataElement("paM0QZaZMTO")); // stunting


            // set date
            try {
                String prev_date = getDataElement("Dpw5YPM1CFj");
                if (!prev_date.isEmpty()) {
                    textView_Date.setText(prev_date);
                }
            } catch (Exception e) {
                textView_Date.setText("");
            }

            // SAM
            try {
                if (getDataElement("AOKp3oQPyYP").equals("true")) {
                    checkbox_Severe_Acute.setChecked(true);
                }
            } catch (Exception e) {
                checkbox_Severe_Acute.setChecked(false);
            }

            // MAM
            try {
                if (getDataElement("QNV3Qb2kjx8").equals("true")) {
                    checkbox_Moderate_Acute.setChecked(true);
                }
            } catch (Exception e) {
                checkbox_Moderate_Acute.setChecked(false);
            }

            // long standing growth
            try {
                if (getDataElement("Sw98c8KAEmr").equals("true")) {
                    checkbox_Long_standing.setChecked(true);
                }
            } catch (Exception e) {
                checkbox_Long_standing.setChecked(false);
            }

            // checkbox_Underweight
            try {
                if (getDataElement("xkhQxmJ8X24").equals("true")) {
                    checkbox_Underweight.setChecked(true);
                }
            } catch (Exception e) {
                checkbox_Underweight.setChecked(false);
            }

            // checkbox_Overweight
            try {
                if (getDataElement("dnLak5wmEzT").equals("true")) {
                    checkbox_Overweight.setChecked(true);
                }
            } catch (Exception e) {
                checkbox_Overweight.setChecked(false);
            }

            // checkbox_Stunting
            try {
                if (getDataElement("paM0QZaZMTO").equals("true")) {
                    checkbox_Stunting.setChecked(true);
                }
            } catch (Exception e) {
                checkbox_Stunting.setChecked(false);
            }

        } else {
            textView_Date.setText("Click here to set Date");
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
                getIntent().getStringExtra(OtherReasonForActivity.IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();

    }

    @Override
    protected void onResume(){
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
        if (formType == OtherReasonForActivity.FormType.CREATE)
            EventFormService.getInstance().delete();
        setResult(RESULT_CANCELED);
        finish();
    }

    private String getDataElement(String dataElement){
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

    private void saveDataElement(String dataElement, String value){
        TrackedEntityDataValueObjectRepository valueRepository;
        try {
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                    .value(
                            EventFormService.getInstance().getEventUid(),
                            dataElement
                    );
        }catch (Exception e)
        {
            EventFormService.getInstance().init(
                    Sdk.d2(),
                    eventUid,
                    programUid,
                    getIntent().getStringExtra(OtherReasonForActivity.IntentExtra.OU_UID.name()));
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
        if(textView_Date.getText().toString().equals("Click here to set Date")||
                textView_Date.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Date Not Selected");
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

        System.out.println(getDataElement("Dpw5YPM1CFj")); // reason for enrollment date
        System.out.println(getDataElement("AOKp3oQPyYP")); // SAM
        System.out.println(getDataElement("QNV3Qb2kjx8")); // MAM
        System.out.println(getDataElement("Sw98c8KAEmr")); // long standing growth
        System.out.println(getDataElement("xkhQxmJ8X24")); // underweight
        System.out.println(getDataElement("dnLak5wmEzT")); // overweight
        System.out.println(getDataElement("paM0QZaZMTO")); // stunting


        saveDataElement("Dpw5YPM1CFj", textView_Date.getText().toString());
        saveDataElement("AOKp3oQPyYP", checkbox_Severe_Acute.isChecked() ? "true" : "");
        saveDataElement("QNV3Qb2kjx8", checkbox_Moderate_Acute.isChecked() ? "true" : "");
        saveDataElement("Sw98c8KAEmr", checkbox_Long_standing.isChecked() ? "true" : "");
        saveDataElement("xkhQxmJ8X24", checkbox_Underweight.isChecked() ? "true" : "");
        saveDataElement("dnLak5wmEzT", checkbox_Overweight.isChecked() ? "true" : "");
        saveDataElement("paM0QZaZMTO", checkbox_Stunting.isChecked() ? "true" : "");


        finishEnrollment();
    }

    private void selectDate(int year, int month, int day)
    {
        System.out.println("Clicked at date");
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








