package com.echdr.android.echdrapp.ui.event_form;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.echdr.android.echdrapp.service.Setter.DateSetter;
import com.echdr.android.echdrapp.service.Validator.StuntingInterventionValidator;
import com.echdr.android.echdrapp.service.util;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.Date;

import io.reactivex.processors.PublishProcessor;

public class StuntingInterventionActivity extends AppCompatActivity {
    private String TAG = "StuntingInterventionActivity";
    private String eventUid;
    private String programUid;
    private String selectedChild;
    private StuntingInterventionActivity.FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;
    private RadioGroup councellingGroup;
    private RadioButton councellingButtonYes;
    private RadioButton councellingButtonNo;

    private TrackedEntityAttributeValue birthday;


    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               StuntingInterventionActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, StuntingInterventionActivity.class);
        intent.putExtra(StuntingInterventionActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(StuntingInterventionActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(StuntingInterventionActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(StuntingInterventionActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(StuntingInterventionActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stunting_intervention);

        textView_Date = findViewById(R.id.editTextDateStuntingIntervention);
        saveButton         = findViewById(R.id.stuntingInterventionSave);
        datePicker         = findViewById(R.id.stunting_inter_date_pick);
        councellingGroup = findViewById(R.id.radioGroupCounselling);
        councellingButtonYes = findViewById(R.id.radioButtonCounsellingYes);
        councellingButtonNo = findViewById(R.id.radioButtonCounsellingNo);

        context = this;

        eventUid = getIntent().getStringExtra(StuntingInterventionActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(StuntingInterventionActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(StuntingInterventionActivity.IntentExtra.TEI_ID.name());
        formType = StuntingInterventionActivity.FormType.valueOf(getIntent().getStringExtra(StuntingInterventionActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(StuntingInterventionActivity.IntentExtra.OU_UID.name());

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

        DateSetter.setContext(context);
        DateSetter.setBirthday(birthday);
        DateSetter.setSetListener(setListener);
        DateSetter.setTextView(textView_Date);
        DateSetter.setImageView(datePicker);
        DateSetter.setDate(year, month, day, 365*5+2);



        // Load the existing values - form.CHECK
        if(formType == StuntingInterventionActivity.FormType.CHECK)
        {

            // set date
            util.setTextView(textView_Date, "RuOyWXMpWHs", eventUid);


            // set paediatrician seen
            try{
                if(util.getDataElement("Xpf2G3fhTUb", eventUid).equals("true"))
                {
                    councellingButtonYes.setChecked(true);
                    councellingButtonNo.setChecked(false);
                }else if(util.getDataElement("Xpf2G3fhTUb", eventUid).equals("false"))
                {
                    councellingButtonYes.setChecked(false);
                    councellingButtonNo.setChecked(true);
                }
            }
            catch (Exception e)
            {
                councellingGroup.clearCheck();
            }

        }
        else{
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
                getIntent().getStringExtra(StuntingInterventionActivity.IntentExtra.OU_UID.name())))
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
        if (formType == StuntingInterventionActivity.FormType.CREATE)
            EventFormService.getInstance().delete();
        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean saveElements()
    {

        StuntingInterventionValidator stuntingInterventionValidator = new StuntingInterventionValidator();
        stuntingInterventionValidator.setContext(context);
        stuntingInterventionValidator.setTextView_Date(textView_Date);

        if(!stuntingInterventionValidator.validate()){
            Log.e(TAG, "Validation failure" );
            return false;
        }
        util.saveDataElement("RuOyWXMpWHs", textView_Date.getText().toString(),
                eventUid, programUid, orgUnit ,engineInitialization );

        String counsellingGiven = "";
        if(councellingButtonYes.isChecked())
        {
            counsellingGiven = "true";
        }else if(councellingButtonNo.isChecked())
        {
            counsellingGiven = "false";
        }

        util.saveDataElement("Xpf2G3fhTUb", counsellingGiven, eventUid,
                programUid, orgUnit ,engineInitialization );
        finishEnrollment();
        return false;
    }

}
