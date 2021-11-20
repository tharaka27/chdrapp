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
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.util.Date;

import io.reactivex.processors.PublishProcessor;

public class StuntingManagementActivity extends AppCompatActivity {

    private String eventUid;
    private String programUid;
    private String selectedChild;
    private StuntingManagementActivity.FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private Spinner stunting_management_Enrollment_spinner;
    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;
    protected String[] other_type_array ;
    private RadioGroup radioGroupHospitalStunting;
    private RadioButton radioButtonHospitalYesStunting;
    private RadioButton radioButtonHospitalNoStunting;
    private RadioGroup radioGroupPaediatricianStunting;
    private RadioButton radioButtonPaediatricianYesStunting;
    private RadioButton radioButtonPaediatricianNoStunting;

    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               StuntingManagementActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, StuntingManagementActivity.class);
        intent.putExtra(StuntingManagementActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(StuntingManagementActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(StuntingManagementActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(StuntingManagementActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(StuntingManagementActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stunting_management);

        textView_Date = findViewById(R.id.editTextDateStuntingManagement);
        stunting_management_Enrollment_spinner = findViewById(R.id.stunting_management_Enrollment_spinner);
        saveButton         = findViewById(R.id.stuntingManagementSave);
        datePicker         = findViewById(R.id.stunting_date_pick);
        radioGroupPaediatricianStunting = findViewById(R.id.radioGroupPaediatricianStunting);
        radioButtonPaediatricianYesStunting = findViewById(R.id.radioButtonPaediatricianYesStunting);
        radioButtonPaediatricianNoStunting = findViewById(R.id.radioButtonPaediatricianNoStunting);
        radioGroupHospitalStunting      = findViewById(R.id.radioGroupHospitalStunting);
        radioButtonHospitalYesStunting = findViewById(R.id.radioButtonHospitalYesStunting);
        radioButtonHospitalNoStunting = findViewById(R.id.radioButtonHospitalNoStunting);

        context = this;

        eventUid = getIntent().getStringExtra(StuntingManagementActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(StuntingManagementActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(StuntingManagementActivity.IntentExtra.TEI_ID.name());
        formType = StuntingManagementActivity.FormType.valueOf(getIntent().getStringExtra(StuntingManagementActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(StuntingManagementActivity.IntentExtra.OU_UID.name());

        other_type_array = getResources().getStringArray(R.array.other_management_type);

        engineInitialization = PublishProcessor.create();

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
                R.array.stunting_management_type,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stunting_management_Enrollment_spinner.setAdapter(adapter);
        stunting_management_Enrollment_spinner.setOnItemSelectedListener(new StuntingManagementActivity.EnrollmentTypeSpinnerClass());

        // Load the existing values - form.CHECK
        if(formType == StuntingManagementActivity.FormType.CHECK)
        {
            System.out.println(getDataElement("KdN0rkDaYLD")); // Paediatrician seen
            System.out.println(getDataElement("kynpSFSeQZa")); // type
            System.out.println(getDataElement("GDF0Ms05QaS")); // date
            System.out.println(getDataElement("Pvi4qR3ZmOy")); // referred to hospital

            // set date
            try{
                String prev_date = getDataElement("GDF0Ms05QaS");
                if(!prev_date.isEmpty())
                {
                    textView_Date.setText(prev_date);
                }
            }
            catch (Exception e)
            {
                textView_Date.setText("");
            }

            // set paediatrician seen
            try{
                if(getDataElement("KdN0rkDaYLD").equals("true"))
                {
                    radioButtonPaediatricianYesStunting.setChecked(true);
                    radioButtonPaediatricianNoStunting.setChecked(false);
                }else if(getDataElement("KdN0rkDaYLD").equals("false"))
                {
                    radioButtonPaediatricianYesStunting.setChecked(false);
                    radioButtonPaediatricianNoStunting.setChecked(true);
                }
            }
            catch (Exception e)
            {
                radioGroupPaediatricianStunting.clearCheck();
            }

            // set referred to hospital
            try{
                if(getDataElement("Pvi4qR3ZmOy").equals("true"))
                {
                    radioButtonHospitalYesStunting.setChecked(true);
                    radioButtonHospitalNoStunting.setChecked(false);
                }else if(getDataElement("Pvi4qR3ZmOy").equals("false"))
                {
                    radioButtonHospitalYesStunting.setChecked(false);
                    radioButtonHospitalNoStunting.setChecked(true);
                }
            }
            catch (Exception e)
            {
                radioGroupHospitalStunting.clearCheck();
            }

            // set enrollment type
            stunting_management_Enrollment_spinner.setSelection(
                    getSpinnerSelection("kynpSFSeQZa", other_type_array));

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
                getIntent().getStringExtra(StuntingManagementActivity.IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();
    }

    class EnrollmentTypeSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            Toast.makeText(v.getContext(), "Your choose :" +
                    other_type_array[position],Toast.LENGTH_SHORT).show();
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
        if (formType == StuntingManagementActivity.FormType.CREATE)
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
                    getIntent().getStringExtra(StuntingManagementActivity.IntentExtra.OU_UID.name()));
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

        saveDataElement("GDF0Ms05QaS", textView_Date.getText().toString());

        String paediatricianSelection = "";
        if(radioButtonPaediatricianYesStunting.isChecked())
        {
            paediatricianSelection = "true";
        }else if(radioButtonPaediatricianNoStunting.isChecked())
        {
            paediatricianSelection = "false";
        }

        String hospitalSelection = "";
        if(radioButtonHospitalYesStunting.isChecked())
        {
            hospitalSelection = "true";
        }else if(radioButtonHospitalYesStunting.isChecked())
        {
            hospitalSelection = "false";
        }

        saveDataElement("KdN0rkDaYLD", paediatricianSelection);
        saveDataElement("Pvi4qR3ZmOy", hospitalSelection);
        saveDataElement("kynpSFSeQZa",
                other_type_array[stunting_management_Enrollment_spinner.getSelectedItemPosition()]);

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
