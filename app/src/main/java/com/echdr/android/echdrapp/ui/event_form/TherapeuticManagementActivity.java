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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class TherapeuticManagementActivity extends AppCompatActivity {


    private String eventUid;
    private String programUid;
    private String selectedChild;
    private TherapeuticManagementActivity.FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private Spinner spinner_Enrollment;
    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;

    protected String[] other_type_therapeutic_array ;
    protected String[] other_type_therapeutic_array_english ;

    private RadioGroup radioGroupTherapeuticHospital;
    private RadioButton radioButtonTherapeuticHospitalYes;
    private RadioButton radioButtonTherapeuticHospitalNo;
    private RadioGroup radioGroupTherapeuticPaediatrician;
    private RadioButton radioButtonTherapeuticPaediatricianYes;
    private RadioButton radioButtonTherapeuticPaediatricianNo;
    private RadioGroup radioGroupTherapeuticBP100;
    private RadioButton radioButtonTherapeuticBP100Yes;
    private RadioButton radioButtonTherapeuticBP100No;

    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    private TrackedEntityAttributeValue birthday;


    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               TherapeuticManagementActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, TherapeuticManagementActivity.class);
        intent.putExtra(TherapeuticManagementActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(TherapeuticManagementActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(TherapeuticManagementActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(TherapeuticManagementActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(TherapeuticManagementActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapeutical_management);

        textView_Date = findViewById(R.id.editTextDateTherapeuticManagement);
        spinner_Enrollment = findViewById(R.id.therapeutic_management_Enrollment_spinner);
        saveButton         = findViewById(R.id.therapeuticManagementSave);
        datePicker         = findViewById(R.id.therapeutic_date_pick);
        radioGroupTherapeuticHospital = findViewById(R.id.radioGroupTherapeuticHospital);
        radioButtonTherapeuticHospitalYes = findViewById(R.id.radioButtonTherapeuticHospitalYes);
        radioButtonTherapeuticHospitalNo = findViewById(R.id.radioButtonTherapeuticHospitalNo);
        radioGroupTherapeuticPaediatrician = findViewById(R.id.radioGroupTherapeuticPaediatrician);
        radioButtonTherapeuticPaediatricianYes = findViewById(R.id.radioButtonTherapeuticPaediatricianYes);
        radioButtonTherapeuticPaediatricianNo = findViewById(R.id.radioButtonTherapeuticPaediatricianNo);
        radioGroupTherapeuticBP100 = findViewById(R.id.radioGroupTherapeuticBP100);
        radioButtonTherapeuticBP100Yes = findViewById(R.id.radioButtonTherapeuticBP100Yes);
        radioButtonTherapeuticBP100No = findViewById(R.id.radioButtonTherapeuticBP100No);

        context = this;

        eventUid = getIntent().getStringExtra(TherapeuticManagementActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(TherapeuticManagementActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(TherapeuticManagementActivity.IntentExtra.TEI_ID.name());
        formType = TherapeuticManagementActivity.FormType.valueOf(getIntent().getStringExtra(TherapeuticManagementActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(TherapeuticManagementActivity.IntentExtra.OU_UID.name());

        other_type_therapeutic_array = getResources().getStringArray(R.array.therapeutical_management_type);
        other_type_therapeutic_array_english = getResources().getStringArray(R.array.therapeutical_management_type_english);

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.therapeutical_management_type,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Enrollment.setAdapter(adapter);
        spinner_Enrollment.setOnItemSelectedListener(new TherapeuticManagementActivity.EnrollmentTypeSpinnerClass());

        // Load the existing values - form.CHECK
        if(formType == TherapeuticManagementActivity.FormType.CHECK)
        {
            System.out.println(getDataElement("TUqp4MzW8SF")); // date
            System.out.println(getDataElement("ZUcO0D98xSN")); // enrollment type
            System.out.println(getDataElement("U266Gc85oQr")); // referred to hospital
            System.out.println(getDataElement("ous3DfCJdmJ")); // nutrition seen
            System.out.println(getDataElement("sYmcJNBVsXA")); // bp100


            // set date
            try{
                String prev_date = getDataElement("TUqp4MzW8SF");
                if(!prev_date.isEmpty())
                {
                    textView_Date.setText(prev_date);
                }
            }
            catch (Exception e)
            {
                textView_Date.setText("");
            }

            // set referred to hospital
            try{
                if(getDataElement("U266Gc85oQr").equals("true"))
                {
                    radioButtonTherapeuticHospitalYes.setChecked(true);
                    radioButtonTherapeuticHospitalNo.setChecked(false);
                }else if(getDataElement("U266Gc85oQr").equals("false"))
                {
                    radioButtonTherapeuticHospitalYes.setChecked(false);
                    radioButtonTherapeuticHospitalNo.setChecked(true);
                }
            }
            catch (Exception e)
            {
                radioGroupTherapeuticHospital.clearCheck();
            }

            // set bp100
            try{
                if(getDataElement("sYmcJNBVsXA").equals("true"))
                {
                    radioButtonTherapeuticBP100Yes.setChecked(true);
                    radioButtonTherapeuticBP100No.setChecked(false);
                }else if(getDataElement("sYmcJNBVsXA").equals("false"))
                {
                    radioButtonTherapeuticBP100Yes.setChecked(false);
                    radioButtonTherapeuticBP100No.setChecked(true);
                }
            }
            catch (Exception e)
            {
                radioGroupTherapeuticBP100.clearCheck();
            }

            // set nutrition seen
            try{
                if(getDataElement("ous3DfCJdmJ").equals("true"))
                {
                    radioButtonTherapeuticPaediatricianYes.setChecked(true);
                    radioButtonTherapeuticPaediatricianNo.setChecked(false);
                }else if(getDataElement("ous3DfCJdmJ").equals("false"))
                {
                    radioButtonTherapeuticPaediatricianYes.setChecked(false);
                    radioButtonTherapeuticPaediatricianNo.setChecked(true);
                }
            }
            catch (Exception e)
            {
                radioGroupTherapeuticPaediatrician.clearCheck();
            }

            // set enrollment type
            spinner_Enrollment.setSelection(
                    getSpinnerSelection("ZUcO0D98xSN", other_type_therapeutic_array));

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
                getIntent().getStringExtra(TherapeuticManagementActivity.IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();
    }

    class EnrollmentTypeSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            Toast.makeText(v.getContext(), "Your choose :" +
                    other_type_therapeutic_array[position],Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
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
        if (formType == TherapeuticManagementActivity.FormType.CREATE)
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
                    getIntent().getStringExtra(TherapeuticManagementActivity.IntentExtra.OU_UID.name()));
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


        saveDataElement("TUqp4MzW8SF", textView_Date.getText().toString());

        String ReferredToHostital = "";
        if(radioButtonTherapeuticHospitalYes.isChecked())
        {
            ReferredToHostital = "true";
        }else if(radioButtonTherapeuticHospitalNo.isChecked())
        {
            ReferredToHostital = "false";
        }

        String paediatricianSelection = "";
        if(radioButtonTherapeuticPaediatricianYes.isChecked())
        {
            paediatricianSelection = "true";
        }else if(radioButtonTherapeuticPaediatricianNo.isChecked())
        {
            paediatricianSelection = "false";
        }

        String BP100 = "";
        if(radioButtonTherapeuticBP100Yes.isChecked())
        {
            BP100 = "true";
        }else if(radioButtonTherapeuticBP100No.isChecked())
        {
            BP100 = "false";
        }


        saveDataElement("ous3DfCJdmJ", paediatricianSelection);
        saveDataElement("U266Gc85oQr", ReferredToHostital);
        saveDataElement("sYmcJNBVsXA", BP100);
        saveDataElement("ZUcO0D98xSN",
                other_type_therapeutic_array_english[spinner_Enrollment.getSelectedItemPosition()]);

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







    }
