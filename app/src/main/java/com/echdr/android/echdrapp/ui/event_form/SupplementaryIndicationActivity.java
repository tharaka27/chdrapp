package com.echdr.android.echdrapp.ui.event_form;

import static android.text.TextUtils.isEmpty;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.processors.PublishProcessor;


public class SupplementaryIndicationActivity extends AppCompatActivity {
    private String eventUid;
    private String programUid;
    private String selectedChild;
    private FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private CheckBox checkbox_MAM;
    private CheckBox checkbox_Green;
    private CheckBox checkbox_Underweight;
    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;
    protected String[] supp_type_array ;
    protected String[] supp_type_array_english ;

    private TrackedEntityAttributeValue birthday;


    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               SupplementaryIndicationActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, SupplementaryIndicationActivity.class);
        intent.putExtra(SupplementaryIndicationActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(SupplementaryIndicationActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(SupplementaryIndicationActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(SupplementaryIndicationActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(SupplementaryIndicationActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplementary_indication);

        textView_Date = findViewById(R.id.editTextDate);
        checkbox_MAM  = findViewById(R.id.MAM_Checkbox);
        checkbox_Green = findViewById(R.id.Green_Checkbox);
        saveButton         = findViewById(R.id.supplementaryIndicationSave);
        datePicker         = findViewById(R.id.supp_date_pick);
        checkbox_Underweight = findViewById(R.id.underweight_Checkbox);

        context = this;

        eventUid = getIntent().getStringExtra(SupplementaryIndicationActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(SupplementaryIndicationActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(SupplementaryIndicationActivity.IntentExtra.TEI_ID.name());
        formType = FormType.valueOf(getIntent().getStringExtra(SupplementaryIndicationActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(SupplementaryIndicationActivity.IntentExtra.OU_UID.name());

        supp_type_array = getResources().getStringArray(R.array.supp_intervention_type);
        supp_type_array_english = getResources().getStringArray(R.array.supp_intervention_type_english);

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
                    //datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

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
        if(formType == FormType.CHECK)
        {
            System.out.println(getDataElement("KuMTUOY6X3L")); // date
            System.out.println(getDataElement("o4ltT56H9QV")); // MAM
            System.out.println(getDataElement("tSnrbDU0cJA")); // green zone
            System.out.println(getDataElement("Fzl8qpcjcwV")); // type

            // set date
            try{
                String prev_date = getDataElement("KuMTUOY6X3L");
                if(!prev_date.isEmpty())
                {
                    textView_Date.setText(prev_date);
                }
            }
            catch (Exception e)
            {
                textView_Date.setText("");
            }

            // set MAM
            try{
                if(getDataElement("o4ltT56H9QV").equals("true"))
                {
                    checkbox_MAM.setChecked(true);
                }
            }
            catch (Exception e)
            {
                checkbox_MAM.setChecked(false);
            }

            // set underweight
            try{
                if(getDataElement("B0RrjNXLZ6z").equals("true"))
                {
                    checkbox_Underweight.setChecked(true);
                }
            }
            catch (Exception e)
            {
                checkbox_Underweight.setChecked(false);
            }

            // set Green zone
            try{
                if(getDataElement("tSnrbDU0cJA").equals("true"))
                {
                    checkbox_Green.setChecked(true);
                }
            }
            catch (Exception e)
            {
                checkbox_Green.setChecked(false);
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
                getIntent().getStringExtra(SupplementaryIndicationActivity.IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();
    }

    class EnrollmentTypeSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            Toast.makeText(v.getContext(), "Your choose :" +
                    supp_type_array[position],Toast.LENGTH_SHORT).show();
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
        if (formType == SupplementaryIndicationActivity.FormType.CREATE)
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
                    getIntent().getStringExtra(SupplementaryIndicationActivity.IntentExtra.OU_UID.name()));
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

        if(checkbox_Green.isChecked() && checkbox_MAM.isChecked()
                && checkbox_Underweight.isChecked() || checkbox_Underweight.isChecked() &&
                    checkbox_Green.isChecked())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("The selection combination is invalid. Possible combinations" +
                    " are \n1. MAM\n2. Underweight\n3. Green Zone" +
                    "\n4. MAM + Underweight\n5. MAM + Longstanding Growth Faltering Zone");
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

        saveDataElement("KuMTUOY6X3L", textView_Date.getText().toString());
        saveDataElement("o4ltT56H9QV", checkbox_MAM.isChecked() ? "true" : "");
        saveDataElement("tSnrbDU0cJA", checkbox_Green.isChecked() ? "true" : "");
        saveDataElement("B0RrjNXLZ6z", checkbox_Underweight.isChecked() ? "true" : "");

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




}
