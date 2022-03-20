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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
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

public class SupplementaryInterventionActivity extends AppCompatActivity {

    private String eventUid;
    private String programUid;
    private String selectedChild;
    private SupplementaryInterventionActivity.FormType formType;
    private String orgUnit;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;

    private TextView textView_Date;
    private EditText numberOfTriposha;
    private Button saveButton;
    private ImageView datePicker;

    private RadioGroup radioGroupTriposha;
    private RadioButton radioButtonTriposhaYes;
    private RadioButton radioButtonTriposhaNo;
    private RadioGroup radioGroupCounselling;
    private RadioButton radioButtonCounsellingYes;
    private RadioButton radioButtonCounsellingNo;


    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               SupplementaryInterventionActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, SupplementaryInterventionActivity.class);
        intent.putExtra(SupplementaryInterventionActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(SupplementaryInterventionActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(SupplementaryInterventionActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(SupplementaryInterventionActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(SupplementaryInterventionActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplementary_interventions);

        textView_Date = findViewById(R.id.supp_intervensionDate);
        saveButton         = findViewById(R.id.SupplementarySave);
        datePicker         = findViewById(R.id.supp_intervension_date_pick);
        radioGroupTriposha = findViewById(R.id.radioGroupTriposha);
        radioButtonTriposhaYes = findViewById(R.id.radioButtonTriposhaYes);
        radioButtonTriposhaNo = findViewById(R.id.radioButtonTriposhaNo);
        radioGroupCounselling      = findViewById(R.id.radioGroupCounselling);
        radioButtonCounsellingYes = findViewById(R.id.radioButtonCounsellingYes);
        radioButtonCounsellingNo = findViewById(R.id.radioButtonCounsellingNo);
        numberOfTriposha = findViewById(R.id.supp_NumberOfTriposha);

        context = this;

        eventUid = getIntent().getStringExtra(SupplementaryInterventionActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(SupplementaryInterventionActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(SupplementaryInterventionActivity.IntentExtra.TEI_ID.name());
        formType = SupplementaryInterventionActivity.FormType.valueOf(getIntent().getStringExtra(SupplementaryInterventionActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(SupplementaryInterventionActivity.IntentExtra.OU_UID.name());


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

        // Load the existing values - form.CHECK

        if(formType == SupplementaryInterventionActivity.FormType.CHECK)
        {
            System.out.println(getDataElement("WRjdfCRNhnU")); // date
            System.out.println(getDataElement("pAxHi8Zd5ZD")); // on triposha
            System.out.println(getDataElement("h9Sv7i87Ks1")); // number of triposha packets given
            System.out.println(getDataElement("FoAmHt6CnGU")); // counsilling given

            // set date
            try{
                String prev_date = getDataElement("WRjdfCRNhnU");
                if(!prev_date.isEmpty())
                {
                    textView_Date.setText(prev_date);
                }
            }
            catch (Exception e)
            {
                textView_Date.setText("");
            }

            // on triposha
            try{
                if(getDataElement("pAxHi8Zd5ZD").equals("true"))
                {
                    radioButtonTriposhaYes.setChecked(true);
                    radioButtonTriposhaNo.setChecked(false);
                }else if(getDataElement("pAxHi8Zd5ZD").equals("false"))
                {
                    radioButtonTriposhaYes.setChecked(false);
                    radioButtonTriposhaNo.setChecked(true);
                }
            }
            catch (Exception e)
            {
                radioGroupTriposha.clearCheck();
            }

            // counselling given
            try{
                if(getDataElement("FoAmHt6CnGU").equals("true"))
                {
                    radioButtonCounsellingYes.setChecked(true);
                    radioButtonCounsellingNo.setChecked(false);
                }else if(getDataElement("FoAmHt6CnGU").equals("false"))
                {
                    radioButtonCounsellingYes.setChecked(false);
                    radioButtonCounsellingNo.setChecked(true);
                }
            }
            catch (Exception e)
            {
                radioGroupCounselling.clearCheck();
            }

            // Number of triposha
            try{
                String triposhaPackets = getDataElement("h9Sv7i87Ks1");
                System.out.println(getDataElement("h9Sv7i87Ks1"));
                if(!triposhaPackets.isEmpty())
                {
                    numberOfTriposha.setText(triposhaPackets);
                }
            }
            catch (Exception e)
            {
                numberOfTriposha.setText("");
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
                getIntent().getStringExtra(SupplementaryInterventionActivity.IntentExtra.OU_UID.name())))
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
        if (formType == SupplementaryInterventionActivity.FormType.CREATE)
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
                    getIntent().getStringExtra(SupplementaryInterventionActivity.IntentExtra.OU_UID.name()));
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

        if( numberOfTriposha.getText().toString().isEmpty() ||
                Integer.parseInt(numberOfTriposha.getText().toString()) < 0
                || Integer.parseInt(numberOfTriposha.getText().toString()) > 5)
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Number of Thriposha packets given should be between 0-5");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            //return;
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }

        saveDataElement("WRjdfCRNhnU", textView_Date.getText().toString());

        String onTriposha = "";
        if(radioButtonTriposhaYes.isChecked())
        {
            onTriposha = "true";
        }else if(radioButtonTriposhaNo.isChecked())
        {
            onTriposha = "false";
        }

        String counsilling = "";
        if(radioButtonCounsellingYes.isChecked())
        {
            counsilling = "true";
        }else if(radioButtonCounsellingNo.isChecked())
        {
            counsilling = "false";
        }



        saveDataElement("pAxHi8Zd5ZD", onTriposha);
        saveDataElement("FoAmHt6CnGU", counsilling);
        saveDataElement("h9Sv7i87Ks1", numberOfTriposha.getText().toString());


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
