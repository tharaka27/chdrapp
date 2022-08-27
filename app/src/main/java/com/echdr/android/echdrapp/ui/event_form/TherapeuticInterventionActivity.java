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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class TherapeuticInterventionActivity extends AppCompatActivity {
    private String eventUid;
    private String programUid;
    private String selectedChild;
    private TherapeuticInterventionActivity.FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;
    private RadioGroup counsellingGroup;
    private RadioButton counsellingButtonYes;
    private RadioButton counsellingButtonNo;
    private RadioGroup onBPGroup;
    private RadioButton onBPButtonYes;
    private RadioButton onBPButtonNo;

    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    private TrackedEntityAttributeValue birthday;


    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               TherapeuticInterventionActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, TherapeuticInterventionActivity.class);
        intent.putExtra(TherapeuticInterventionActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(TherapeuticInterventionActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(TherapeuticInterventionActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(TherapeuticInterventionActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(TherapeuticInterventionActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapeutical_intervention);

        textView_Date = findViewById(R.id.editTextDateTherapeuticIntervention);
        saveButton         = findViewById(R.id.therapeuticInterventionSave);
        datePicker         = findViewById(R.id.therapeutic_inter_date_pick);
        onBPGroup = findViewById(R.id.radioGroupBP);
        onBPButtonYes = findViewById(R.id.radioButtonBPYes);
        onBPButtonNo = findViewById(R.id.radioButtonBPNo);
        counsellingGroup = findViewById(R.id.radioGroupCounselling);
        counsellingButtonYes = findViewById(R.id.radioButtonCounsellingYesT);
        counsellingButtonNo = findViewById(R.id.radioButtonCounsellingNoT);

        context = this;

        eventUid = getIntent().getStringExtra(TherapeuticInterventionActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(TherapeuticInterventionActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(TherapeuticInterventionActivity.IntentExtra.TEI_ID.name());
        formType = TherapeuticInterventionActivity.FormType.valueOf(getIntent().getStringExtra(TherapeuticInterventionActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(TherapeuticInterventionActivity.IntentExtra.OU_UID.name());

        engineInitialization = PublishProcessor.create();

        birthday = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(selectedChild)
                .byTrackedEntityAttribute().eq("qNH202ChkV3")
                .one().blockingGet();

        //Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        String s_day          = (String) DateFormat.format("dd",   date); // 20
        String s_monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String s_year         = (String) DateFormat.format("yyyy", date); // 2013

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
                month = month+1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth) ;
                textView_Date.setText(date);
            }
        };


        // Load the existing values - form.CHECK
        if(formType == TherapeuticInterventionActivity.FormType.CHECK)
        {
            System.out.println(getDataElement("I4ttdIcJ9Pn")); // Date
            System.out.println(getDataElement("UYR4jf1kU0T")); // counselling
            System.out.println(getDataElement("jlzzNX043Yg")); // onBP

            // set date
            try{
                String prev_date = getDataElement("I4ttdIcJ9Pn");
                if(!prev_date.isEmpty())
                {
                    textView_Date.setText(prev_date);
                }
            }
            catch (Exception e)
            {
                textView_Date.setText("");
            }

            // set counselling given
            try{
                if(getDataElement("UYR4jf1kU0T").equals("true"))
                {
                    counsellingButtonYes.setChecked(true);
                    counsellingButtonNo.setChecked(false);
                }else if(getDataElement("UYR4jf1kU0T").equals("false"))
                {
                    counsellingButtonYes.setChecked(false);
                    counsellingButtonNo.setChecked(true);
                }
            }
            catch (Exception e)
            {
                counsellingGroup.clearCheck();
            }

            // set on BP
            try{
                if(getDataElement("jlzzNX043Yg").equals("true"))
                {
                    onBPButtonYes.setChecked(true);
                    onBPButtonNo.setChecked(false);
                }else if(getDataElement("jlzzNX043Yg").equals("false"))
                {
                    onBPButtonYes.setChecked(false);
                    onBPButtonNo.setChecked(true);
                }
            }
            catch (Exception e)
            {
                onBPGroup.clearCheck();
            }

        }
        else{
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
                getIntent().getStringExtra(TherapeuticInterventionActivity.IntentExtra.OU_UID.name())))
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
        if (formType == TherapeuticInterventionActivity.FormType.CREATE)
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
                    getIntent().getStringExtra(TherapeuticInterventionActivity.IntentExtra.OU_UID.name()));
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
        System.out.println(getDataElement("I4ttdIcJ9Pn")); // Date
        System.out.println(getDataElement("UYR4jf1kU0T")); // counselling
        System.out.println(getDataElement("jlzzNX043Yg")); // onBP

        saveDataElement("I4ttdIcJ9Pn", textView_Date.getText().toString());

        String councellingSelection = "";
        if(counsellingButtonYes.isChecked())
        {
            councellingSelection = "true";
        }else if(counsellingButtonNo.isChecked())
        {
            councellingSelection = "false";
        }

        saveDataElement("UYR4jf1kU0T", councellingSelection);

        String onBPSelection = "";
        if(onBPButtonYes.isChecked())
        {
            onBPSelection = "true";
        }else if(onBPButtonNo.isChecked())
        {
            onBPSelection = "false";
        }

        saveDataElement("jlzzNX043Yg", onBPSelection);

        finishEnrollment();
    }

    private void selectDate(int year, int month, int day)
    {
        System.out.println("Clicked et date");
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

}
