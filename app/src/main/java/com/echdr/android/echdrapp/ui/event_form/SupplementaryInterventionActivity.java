package com.echdr.android.echdrapp.ui.event_form;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.echdr.android.echdrapp.service.Validator.SupplementInterventionValidator;
import com.echdr.android.echdrapp.service.util;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.Date;

import io.reactivex.processors.PublishProcessor;

public class SupplementaryInterventionActivity extends AppCompatActivity {
    private static final String TAG = "SupplementaryInter";
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

    private TrackedEntityAttributeValue birthday;


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

        if(formType == SupplementaryInterventionActivity.FormType.CHECK)
        {
            // set date
            util.setTextView(textView_Date, "WRjdfCRNhnU", eventUid);

            // on triposha
            try{
                if(util.getDataElement("pAxHi8Zd5ZD", eventUid).equals("true"))
                {
                    radioButtonTriposhaYes.setChecked(true);
                    radioButtonTriposhaNo.setChecked(false);
                }else if(util.getDataElement("pAxHi8Zd5ZD", eventUid).equals("false"))
                {
                    radioButtonTriposhaYes.setChecked(false);
                    radioButtonTriposhaNo.setChecked(true);
                }
            }
            catch (Exception e)
            {
                radioGroupCounselling.clearCheck();
            }

            // counselling given
            try{
                if(util.getDataElement("FoAmHt6CnGU", eventUid).equals("true"))
                {
                    radioButtonCounsellingYes.setChecked(true);
                    radioButtonCounsellingNo.setChecked(false);
                }else if(util.getDataElement("FoAmHt6CnGU", eventUid).equals("false"))
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
                String triposhaPackets = util.getDataElement("h9Sv7i87Ks1", eventUid);
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
            textView_Date.setText(getString(R.string.date_button_text));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveElements();
            }
        });

        radioButtonTriposhaNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberOfTriposha.setText("");
                numberOfTriposha.setEnabled(false);
            }
        });

        radioButtonTriposhaYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numberOfTriposha.setEnabled(true);
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

    private boolean saveElements() {

        SupplementInterventionValidator supplimentInterventionValidator = new SupplementInterventionValidator();
        supplimentInterventionValidator.setContext(context);
        supplimentInterventionValidator.setTextView_Date(textView_Date);
        supplimentInterventionValidator.setTextView_Packets(numberOfTriposha);


        if(!supplimentInterventionValidator.validate()){
            Log.e(TAG, "Validation failure" );
            return false;
        }

        util.saveDataElement("WRjdfCRNhnU", textView_Date.getText().toString(),
                eventUid, programUid, orgUnit ,engineInitialization );

        util.saveDataElement("h9Sv7i87Ks1", numberOfTriposha.getText().toString(),
                eventUid, programUid, orgUnit ,engineInitialization );

        String onTriposha = "";
        if(radioButtonTriposhaYes.isChecked())
        {
            onTriposha = "true";
        }else if(radioButtonTriposhaNo.isChecked())
        {
            onTriposha = "false";
        }

        util.saveDataElement("pAxHi8Zd5ZD", onTriposha, eventUid,
                programUid, orgUnit ,engineInitialization );

        String counsilling = "";
        if(radioButtonCounsellingYes.isChecked())
        {
            counsilling = "true";
        }else if(radioButtonCounsellingNo.isChecked())
        {
            counsilling = "false";
        }

        util.saveDataElement("FoAmHt6CnGU", counsilling, eventUid,
                programUid, orgUnit ,engineInitialization );


        finishEnrollment();
        return false;
    }


}
