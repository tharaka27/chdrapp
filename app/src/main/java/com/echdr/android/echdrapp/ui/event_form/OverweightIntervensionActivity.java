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
import com.echdr.android.echdrapp.service.Validator.OverweightInterventionValidator;
import com.echdr.android.echdrapp.service.util;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.Date;

import io.reactivex.processors.PublishProcessor;

public class OverweightIntervensionActivity extends AppCompatActivity {
    private static final String TAG = "OverweightIntervension";
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

        textView_Date               = findViewById(R.id.intervensionDate);
        saveButton                  = findViewById(R.id.IntervensionSave);
        datePicker                  = findViewById(R.id.intervension_date_pick);
        radioGroupCounselling       = findViewById(R.id.radioGroupCounselling);
        radioButtonCounsellingYes   = findViewById(R.id.radioButtonCounsellingYes);
        radioButtonCounsellingNo    = findViewById(R.id.radioButtonCounsellingNo);

        context = this;

        eventUid        = getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.EVENT_UID.name());
        programUid      = getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild   = getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.TEI_ID.name());
        formType        = OverweightIntervensionActivity.FormType.valueOf(getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.TYPE.name()));
        orgUnit         = getIntent().getStringExtra(OverweightIntervensionActivity.IntentExtra.OU_UID.name());

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

        if(formType == OverweightIntervensionActivity.FormType.CHECK)
        {

            // set date
            util.setTextView(textView_Date, "kCYwMXTkeAE", eventUid);

            // set counselling give
            try{
                if(util.getDataElement("J8qFRBqjDfE", eventUid).equals("true"))
                {
                    radioButtonCounsellingYes.setChecked(true);
                    radioButtonCounsellingNo.setChecked(false);
                }else if(util.getDataElement("J8qFRBqjDfE", eventUid).equals("false"))
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

    private boolean saveElements()

    {
        OverweightInterventionValidator overweightInterventionValidator = new OverweightInterventionValidator();
        overweightInterventionValidator.setContext(context);
        overweightInterventionValidator.setTextView_Date(textView_Date);

        if(!overweightInterventionValidator.validate()){
            Log.e(TAG, "Validation failure" );
            return false;
        }


        util.saveDataElement("kCYwMXTkeAE", textView_Date.getText().toString(),
                eventUid, programUid, orgUnit ,engineInitialization );

        String counsellingGiven = "";
        if(radioButtonCounsellingYes.isChecked())
        {
            counsellingGiven = "true";
        }else if(radioButtonCounsellingNo.isChecked())
        {
            counsellingGiven = "false";
        }

        util.saveDataElement("J8qFRBqjDfE", counsellingGiven, eventUid,
                programUid, orgUnit ,engineInitialization );


        finishEnrollment();
        return false;
    }

}