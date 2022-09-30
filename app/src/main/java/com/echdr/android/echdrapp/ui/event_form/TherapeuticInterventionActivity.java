package com.echdr.android.echdrapp.ui.event_form;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.echdr.android.echdrapp.service.Validator.TherapeuticInterventionValidator;
import com.echdr.android.echdrapp.service.util;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.Date;

import io.reactivex.processors.PublishProcessor;

public class TherapeuticInterventionActivity extends AppCompatActivity {
    private static final String TAG = "TherapeuticIntervention";
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

        DateSetter.setContext(context);
        DateSetter.setBirthday(birthday);
        DateSetter.setSetListener(setListener);
        DateSetter.setTextView(textView_Date);
        DateSetter.setImageView(datePicker);
        DateSetter.setDate(year, month, day, 365*5+2);

        // Load the existing values - form.CHECK
        if(formType == TherapeuticInterventionActivity.FormType.CHECK)
        {

            // set date
            util.setTextView(textView_Date, "I4ttdIcJ9Pn", eventUid);

            // set counselling given
            try{
                if(util.getDataElement("UYR4jf1kU0T", eventUid).equals("true"))
                {
                    counsellingButtonYes.setChecked(true);
                    counsellingButtonNo.setChecked(false);
                }else if(util.getDataElement("UYR4jf1kU0T", eventUid).equals("false"))
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
                if(util.getDataElement("jlzzNX043Yg", eventUid).equals("true"))
                {
                    onBPButtonYes.setChecked(true);
                    onBPButtonNo.setChecked(false);
                }else if(util.getDataElement("jlzzNX043Yg", eventUid).equals("false"))
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

    private boolean saveElements()
    {
        TherapeuticInterventionValidator therapeuticInterventionValidator = new TherapeuticInterventionValidator();
        therapeuticInterventionValidator.setContext(context);
        therapeuticInterventionValidator.setTextView_Date(textView_Date);

        if(!therapeuticInterventionValidator.validate()){
            Log.e(TAG, "Validation failure" );
            return false;
        }

        util.saveDataElement("I4ttdIcJ9Pn", textView_Date.getText().toString(),
                eventUid, programUid, orgUnit ,engineInitialization );


        String onBP100 = "";
        if(onBPButtonYes.isChecked())
        {
            onBP100 = "true";
        }else if(onBPButtonNo.isChecked())
        {
            onBP100 = "false";
        }

        util.saveDataElement("jlzzNX043Yg", onBP100, eventUid,
                programUid, orgUnit ,engineInitialization );

        String counsilling = "";
        if(counsellingButtonYes.isChecked())
        {
            counsilling = "true";
        }else if(counsellingButtonNo.isChecked())
        {
            counsilling = "false";
        }

        util.saveDataElement("UYR4jf1kU0T", counsilling, eventUid,
                programUid, orgUnit ,engineInitialization );


        finishEnrollment();
        return false;
    }


}
