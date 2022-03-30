package com.echdr.android.echdrapp.ui.event_form;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.util.Date;

import io.reactivex.processors.PublishProcessor;

public class OtherFoodInsecurityActivity extends AppCompatActivity {

    private String eventUid;
    private String programUid;
    private String selectedChild;
    private FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;


    private RadioGroup oth_food_agri_training_radio;
    private RadioButton oth_food_agri_training_radio_yes;
    private RadioButton oth_food_agri_training_radio_no;
    private RadioGroup oth_food_live_stock_radio;
    private RadioButton oth_food_live_stock_radio_yes;
    private RadioButton oth_food_live_stock_radio_no;

    private RadioGroup oth_food_support_radio;
    private RadioButton oth_food_support_radio_yes;
    private RadioButton oth_food_support_radio_no;
    private RadioGroup oth_food_support_household_radio;
    private RadioButton oth_food_support_household_radio_yes;
    private RadioButton oth_food_support_household_radio_no;

    private RadioGroup oth_food_training_radio;
    private RadioButton oth_food_training_radio_yes;
    private RadioButton oth_food_training_radio_no;


    private TrackedEntityAttributeValue birthday;


    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               OtherFoodInsecurityActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, OtherFoodInsecurityActivity.class);
        intent.putExtra(OtherFoodInsecurityActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(OtherFoodInsecurityActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(OtherFoodInsecurityActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(OtherFoodInsecurityActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(OtherFoodInsecurityActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_food_insecurity);

        textView_Date = findViewById(R.id.editTextDateOtherFoodInsecurity);
        //datePicker         = findViewById(R.id.other_intervention_poverty_date_pick);


        oth_food_agri_training_radio = findViewById(R.id.oth_food_agri_training_radio);
        oth_food_agri_training_radio_yes = findViewById(R.id.oth_food_agri_training_radio_yes);
        oth_food_agri_training_radio_no = findViewById(R.id.oth_food_agri_training_radio_no);
        oth_food_live_stock_radio = findViewById(R.id.oth_food_live_stock_radio);
        oth_food_live_stock_radio_yes = findViewById(R.id.oth_food_live_stock_radio_yes);
        oth_food_live_stock_radio_no = findViewById(R.id.oth_food_live_stock_radio_no);

        oth_food_support_radio = findViewById(R.id.oth_food_support_radio);
        oth_food_support_radio_yes = findViewById(R.id.oth_food_support_radio_yes);
        oth_food_support_radio_no = findViewById(R.id.oth_food_support_radio_no);
        oth_food_support_household_radio = findViewById(R.id.oth_food_support_household_radio);
        oth_food_support_household_radio_yes = findViewById(R.id.oth_food_support_household_radio_yes);
        oth_food_support_household_radio_no = findViewById(R.id.oth_food_support_household_radio_no);

        oth_food_training_radio = findViewById(R.id.oth_food_training_radio);
        oth_food_training_radio_yes = findViewById(R.id.oth_food_training_radio_yes);
        oth_food_training_radio_no = findViewById(R.id.oth_food_training_radio_no);

        context = this;

        eventUid = getIntent().getStringExtra(OtherFoodInsecurityActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(OtherFoodInsecurityActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(OtherFoodInsecurityActivity.IntentExtra.TEI_ID.name());
        formType = OtherFoodInsecurityActivity.FormType.valueOf(getIntent().getStringExtra(OtherFoodInsecurityActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(OtherFoodInsecurityActivity.IntentExtra.OU_UID.name());


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



        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth) ;
                textView_Date.setText(date);
            }
        };


        // Load the existing values - form.CHECK

        if(formType == OtherFoodInsecurityActivity.FormType.CHECK)
        {

            /*
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

             */

            // set Agriculture skills training/ technical support
            try{
                if(getDataElement("zSMbJlZUVbz").equals("true"))
                {
                    oth_food_agri_training_radio_yes.setChecked(true);
                    oth_food_agri_training_radio_no.setChecked(false);
                }else if(getDataElement("zSMbJlZUVbz").equals("false"))
                {
                    oth_food_agri_training_radio_yes.setChecked(false);
                    oth_food_agri_training_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_food_agri_training_radio.clearCheck();
            }

            // Live stock and poultry support projects for households
            try{
                if(getDataElement("SAji7ivbksI").equals("true"))
                {
                    oth_food_live_stock_radio_yes.setChecked(true);
                    oth_food_live_stock_radio_no.setChecked(false);
                }else if(getDataElement("SAji7ivbksI").equals("false"))
                {
                    oth_food_live_stock_radio_yes.setChecked(false);
                    oth_food_live_stock_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_food_live_stock_radio.clearCheck();
            }

            // Support home gardening
            try{
                if(getDataElement("lfDklH49PJq").equals("true"))
                {
                    oth_food_support_radio_yes.setChecked(true);
                    oth_food_support_radio_no.setChecked(false);
                }else if(getDataElement("lfDklH49PJq").equals("false"))
                {
                    oth_food_support_radio_yes.setChecked(false);
                    oth_food_support_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_food_support_radio.clearCheck();
            }

            // Support household water harvesting /water irrigation projects
            try{
                if(getDataElement("jKT3vVgdMJn").equals("true"))
                {
                    oth_food_support_household_radio_yes.setChecked(true);
                    oth_food_support_household_radio_no.setChecked(false);
                }else if(getDataElement("jKT3vVgdMJn").equals("false"))
                {
                    oth_food_support_household_radio_yes.setChecked(false);
                    oth_food_support_household_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_food_support_household_radio.clearCheck();
            }

            // Training to reduce post harvest loss
            try{
                if(getDataElement("l0ENNYAx5Np").equals("true"))
                {
                    oth_food_training_radio_yes.setChecked(true);
                    oth_food_training_radio_no.setChecked(false);
                }else if(getDataElement("l0ENNYAx5Np").equals("false"))
                {
                    oth_food_training_radio_yes.setChecked(false);
                    oth_food_training_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_food_training_radio.clearCheck();
            }

        }
        else{
            textView_Date.setText("Click here to set Date");
        }


        if (EventFormService.getInstance().init(
                Sdk.d2(),
                eventUid,
                programUid,
                getIntent().getStringExtra(OtherFoodInsecurityActivity.IntentExtra.OU_UID.name())))
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

    @Override
    public void onBackPressed() {
        if (formType == OtherFoodInsecurityActivity.FormType.CREATE)
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
}
