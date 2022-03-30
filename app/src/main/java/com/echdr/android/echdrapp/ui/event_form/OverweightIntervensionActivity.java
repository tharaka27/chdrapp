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
import android.widget.Spinner;
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

public class OverweightIntervensionActivity extends AppCompatActivity {
    private String eventUid;
    private String programUid;
    private String selectedChild;
    private OverweightIntervensionActivity.FormType formType;
    private String orgUnit;


    private TextView textView_Date;
    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;
    private RadioGroup radioGroupCounselling;
    private RadioButton radioButtonCounsellingYes;
    private RadioButton radioButtonCounsellingNo;

    private TrackedEntityAttributeValue birthday;


    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               OverweightIntervensionActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, OverweightIntervensionActivity.class);
        intent.putExtra(OverweightIntervensionActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(OverweightIntervensionActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(OverweightIntervensionActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(OverweightIntervensionActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(OverweightIntervensionActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overweight_intervention);

        textView_Date = findViewById(R.id.intervensionDate);
        saveButton         = findViewById(R.id.IntervensionSave);
        datePicker         = findViewById(R.id.intervension_date_pick);
        radioGroupCounselling = findViewById(R.id.radioGroupCounselling);
        radioButtonCounsellingYes = findViewById(R.id.radioButtonCounsellingYes);
        radioButtonCounsellingNo = findViewById(R.id.radioButtonCounsellingNo);


        context = this;

        eventUid = getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.TEI_ID.name());
        formType = OverweightIntervensionActivity.FormType.valueOf(getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.OU_UID.name());

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
                month = month+1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth) ;
                textView_Date.setText(date);
            }
        };

        if(formType == OverweightIntervensionActivity.FormType.CHECK)
        {
            System.out.println(getDataElement("kCYwMXTkeAE")); // intervension date
            System.out.println(getDataElement("J8qFRBqjDfE")); // counselling given


            // set date
            try{
                String prev_date = getDataElement("kCYwMXTkeAE");
                if(!prev_date.isEmpty())
                {
                    textView_Date.setText(prev_date);
                }
            }
            catch (Exception e)
            {
                textView_Date.setText("");
            }

            // set counselling give
            try{
                if(getDataElement("J8qFRBqjDfE").equals("true"))
                {
                    radioButtonCounsellingYes.setChecked(true);
                    radioButtonCounsellingNo.setChecked(false);
                }else if(getDataElement("J8qFRBqjDfE").equals("false"))
                {
                    radioButtonCounsellingYes.setChecked(false);
                    radioButtonCounsellingNo.setChecked(true);
                }
            }
            catch (Exception e)
            {
                radioGroupCounselling.clearCheck();
            }

        }else{
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
                getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.OU_UID.name())))
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
        if (formType == OverweightIntervensionActivity.FormType.CREATE)
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
                    getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.OU_UID.name()));
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


        saveDataElement("kCYwMXTkeAE", textView_Date.getText().toString());

        String counsellingGiven = "";
        if(radioButtonCounsellingYes.isChecked())
        {
            counsellingGiven = "true";
        }else if(radioButtonCounsellingNo.isChecked())
        {
            counsellingGiven = "false";
        }


        saveDataElement("J8qFRBqjDfE", counsellingGiven);


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
