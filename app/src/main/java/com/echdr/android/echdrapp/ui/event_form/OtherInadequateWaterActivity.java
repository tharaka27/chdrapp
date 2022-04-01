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
import com.echdr.android.echdrapp.data.service.DateFormatHelper;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventModule;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.util.Date;

import io.reactivex.processors.PublishProcessor;

public class OtherInadequateWaterActivity extends AppCompatActivity {

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


    private RadioGroup oth_indequate_hand_wash_radio;
    private RadioButton oth_indequate_hand_wash_radio_yes;
    private RadioButton oth_indequate_hand_wash_radio_no;
    private RadioGroup oth_inadequate_toilet_facility_radio;
    private RadioButton oth_inadequate_toilet_facility_radio_yes;
    private RadioButton oth_inadequate_toilet_facility_radio_no;

    private RadioGroup oth_inadequate_awareness_drinking_radio;
    private RadioButton oth_inadequate_awareness_drinking_radio_yes;
    private RadioButton oth_inadequate_awareness_drinking_radio_no;
    private RadioGroup oth_inadequate_awareness_wash_radio;
    private RadioButton oth_inadequate_awareness_wash_radio_yes;
    private RadioButton oth_inadequate_awareness_wash_radio_no;

    private RadioGroup oth_inadequate_enrolment_house_radio;
    private RadioButton oth_inadequate_enrolment_house_radio_yes;
    private RadioButton oth_inadequate_enrolment_house_radio_no;
    private RadioGroup oth_indequate_establish_water_radio;
    private RadioButton oth_indequate_establish_water_radio_yes;
    private RadioButton oth_indequate_establish_water_radio_no;

    private RadioGroup oth_indequate_imporve_water_radio;
    private RadioButton oth_indequate_imporve_water_radio_yes;
    private RadioButton oth_indequate_imporve_water_radio_no;
    private RadioGroup oth_indequate_alternative_water_radio;
    private RadioButton oth_indequate_alternative_water_radio_yes;
    private RadioButton oth_indequate_alternative_water_radio_no;

    private RadioGroup oth_inadequate_household_water_radio;
    private RadioButton oth_inadequate_household_water_radio_yes;
    private RadioButton oth_inadequate_household_water_radio_no;
    private RadioGroup oth_inadequate_monitor_sanitation_radio;
    private RadioButton oth_inadequate_monitor_sanitation_radio_yes;
    private RadioButton oth_inadequate_monitor_sanitation_radio_no;

    private RadioGroup oth_inadequate_provide_safe_water_radio;
    private RadioButton oth_inadequate_provide_safe_water_radio_yes;
    private RadioButton oth_inadequate_provide_safe_water_radio_no;
    private RadioGroup oth_inadequate_provide_improved_radio;
    private RadioButton oth_inadequate_provide_improved_radio_yes;
    private RadioButton oth_inadequate_provide_improved_radio_no;

    private RadioGroup oth_inadequate_renovation_radio;
    private RadioButton oth_inadequate_renovation_radio_yes;
    private RadioButton oth_inadequate_renovation_radio_no;
    private RadioGroup oth_inadequate_water_quality_radio;
    private RadioButton oth_inadequate_water_quality_radio_yes;
    private RadioButton oth_inadequate_water_quality_radio_no;

    private TrackedEntityAttributeValue birthday;


    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }
    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               OtherInadequateWaterActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, OtherInadequateWaterActivity.class);
        intent.putExtra(OtherInadequateWaterActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(OtherInadequateWaterActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(OtherInadequateWaterActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(OtherInadequateWaterActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(OtherInadequateWaterActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_inadequate_water);

        textView_Date = findViewById(R.id.editTextDateOtherInadequateWater);
        //datePicker         = findViewById(R.id.other_intervention_poverty_date_pick);

        oth_indequate_hand_wash_radio = findViewById(R.id.oth_indequate_hand_wash_radio);
        oth_indequate_hand_wash_radio_yes = findViewById(R.id.oth_indequate_hand_wash_radio_yes);
        oth_indequate_hand_wash_radio_no = findViewById(R.id.oth_indequate_hand_wash_radio_no);
        oth_inadequate_toilet_facility_radio = findViewById(R.id.oth_inadequate_toilet_facility_radio);
        oth_inadequate_toilet_facility_radio_yes = findViewById(R.id.oth_inadequate_toilet_facility_radio_yes);
        oth_inadequate_toilet_facility_radio_no = findViewById(R.id.oth_inadequate_toilet_facility_radio_no);

        oth_inadequate_awareness_drinking_radio = findViewById(R.id.oth_inadequate_awareness_drinking_radio);
        oth_inadequate_awareness_drinking_radio_yes = findViewById(R.id.oth_inadequate_awareness_drinking_radio_yes);
        oth_inadequate_awareness_drinking_radio_no = findViewById(R.id.oth_inadequate_awareness_drinking_radio_no);
        oth_inadequate_awareness_wash_radio = findViewById(R.id.oth_inadequate_awareness_wash_radio);
        oth_inadequate_awareness_wash_radio_yes = findViewById(R.id.oth_inadequate_awareness_wash_radio_yes);
        oth_inadequate_awareness_wash_radio_no = findViewById(R.id.oth_inadequate_awareness_wash_radio_no);

        oth_inadequate_enrolment_house_radio = findViewById(R.id.oth_inadequate_enrolment_house_radio);
        oth_inadequate_enrolment_house_radio_yes = findViewById(R.id.oth_inadequate_enrolment_house_radio_yes);
        oth_inadequate_enrolment_house_radio_no = findViewById(R.id.oth_inadequate_enrolment_house_radio_no);
        oth_indequate_establish_water_radio = findViewById(R.id.oth_indequate_establish_water_radio);
        oth_indequate_establish_water_radio_yes = findViewById(R.id.oth_indequate_establish_water_radio_yes);
        oth_indequate_establish_water_radio_no = findViewById(R.id.oth_indequate_establish_water_radio_no);

        oth_indequate_imporve_water_radio = findViewById(R.id.oth_indequate_imporve_water_radio);
        oth_indequate_imporve_water_radio_yes = findViewById(R.id.oth_indequate_imporve_water_radio_yes);
        oth_indequate_imporve_water_radio_no = findViewById(R.id.oth_indequate_imporve_water_radio_no);
        oth_indequate_alternative_water_radio = findViewById(R.id.oth_indequate_alternative_water_radio);
        oth_indequate_alternative_water_radio_yes = findViewById(R.id.oth_indequate_alternative_water_radio_yes);
        oth_indequate_alternative_water_radio_no = findViewById(R.id.oth_indequate_alternative_water_radio_no);

        oth_inadequate_household_water_radio = findViewById(R.id.oth_inadequate_household_water_radio);
        oth_inadequate_household_water_radio_yes = findViewById(R.id.oth_inadequate_household_water_radio_yes);
        oth_inadequate_household_water_radio_no = findViewById(R.id.oth_inadequate_household_water_radio_no);
        oth_inadequate_monitor_sanitation_radio = findViewById(R.id.oth_inadequate_monitor_sanitation_radio);
        oth_inadequate_monitor_sanitation_radio_yes = findViewById(R.id.oth_inadequate_monitor_sanitation_radio_yes);
        oth_inadequate_monitor_sanitation_radio_no = findViewById(R.id.oth_inadequate_monitor_sanitation_radio_no);

        oth_inadequate_provide_safe_water_radio = findViewById(R.id.oth_inadequate_provide_safe_water_radio);
        oth_inadequate_provide_safe_water_radio_yes = findViewById(R.id.oth_inadequate_provide_safe_water_radio_yes);
        oth_inadequate_provide_safe_water_radio_no = findViewById(R.id.oth_inadequate_provide_safe_water_radio_no);
        oth_inadequate_provide_improved_radio = findViewById(R.id.oth_inadequate_provide_improved_radio);
        oth_inadequate_provide_improved_radio_yes = findViewById(R.id.oth_inadequate_provide_improved_radio_yes);
        oth_inadequate_provide_improved_radio_no = findViewById(R.id.oth_inadequate_provide_improved_radio_no);

        oth_inadequate_renovation_radio = findViewById(R.id.oth_inadequate_renovation_radio);
        oth_inadequate_renovation_radio_yes = findViewById(R.id.oth_inadequate_renovation_radio_yes);
        oth_inadequate_renovation_radio_no = findViewById(R.id.oth_inadequate_renovation_radio_no);
        oth_inadequate_water_quality_radio = findViewById(R.id.oth_inadequate_water_quality_radio);
        oth_inadequate_water_quality_radio_yes = findViewById(R.id.oth_inadequate_water_quality_radio_yes);
        oth_inadequate_water_quality_radio_no = findViewById(R.id.oth_inadequate_water_quality_radio_no);

        context = this;

        eventUid = getIntent().getStringExtra(OtherInadequateWaterActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(OtherInadequateWaterActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(OtherInadequateWaterActivity.IntentExtra.TEI_ID.name());
        formType = OtherInadequateWaterActivity.FormType.valueOf(getIntent().getStringExtra(OtherInadequateWaterActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(OtherInadequateWaterActivity.IntentExtra.OU_UID.name());


        engineInitialization = PublishProcessor.create();

        /*
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
         */

        try{
            Event e = Sdk.d2().eventModule().events().byUid().eq(eventUid).one().blockingGet();
            textView_Date.setText(DateFormatHelper.formatSimpleDate(e.eventDate()));
            System.out.println("Event Date is " + e.eventDate());
        }
        catch (Exception e)
        {
            System.out.println("[Error] Error occured while retrieving event date");
        }


        // Load the existing values - form.CHECK

        if(formType == OtherInadequateWaterActivity.FormType.CHECK)
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

            // Assess hand washing facilities
            try{
                if(getDataElement("LnnYFKOkyLe").equals("true"))
                {
                    oth_indequate_hand_wash_radio_yes.setChecked(true);
                    oth_indequate_hand_wash_radio_no.setChecked(false);
                }else if(getDataElement("LnnYFKOkyLe").equals("false"))
                {
                    oth_indequate_hand_wash_radio_yes.setChecked(false);
                    oth_indequate_hand_wash_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_indequate_hand_wash_radio.clearCheck();
            }

            // Assess toilet facility against local standards
            try{
                if(getDataElement("RnLR81DgY6n").equals("true"))
                {
                    oth_inadequate_toilet_facility_radio_yes.setChecked(true);
                    oth_inadequate_toilet_facility_radio_no.setChecked(false);
                }else if(getDataElement("RnLR81DgY6n").equals("false"))
                {
                    oth_inadequate_toilet_facility_radio_yes.setChecked(false);
                    oth_inadequate_toilet_facility_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inadequate_toilet_facility_radio.clearCheck();
            }

            // Awareness on safely managed drinking water
            try{
                if(getDataElement("ndCSQG3lAIF").equals("true"))
                {
                    oth_inadequate_awareness_drinking_radio_yes.setChecked(true);
                    oth_inadequate_awareness_drinking_radio_no.setChecked(false);
                }else if(getDataElement("ndCSQG3lAIF").equals("false"))
                {
                    oth_inadequate_awareness_drinking_radio_yes.setChecked(false);
                    oth_inadequate_awareness_drinking_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inadequate_awareness_drinking_radio.clearCheck();
            }

            // Awareness on WASH practices
            try{
                if(getDataElement("ueZA61nCvel").equals("true"))
                {
                    oth_inadequate_awareness_wash_radio_yes.setChecked(true);
                    oth_inadequate_awareness_wash_radio_no.setChecked(false);
                }else if(getDataElement("ueZA61nCvel").equals("false"))
                {
                    oth_inadequate_awareness_wash_radio_yes.setChecked(false);
                    oth_inadequate_awareness_wash_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inadequate_awareness_wash_radio.clearCheck();
            }

            // Enrolment to house construction/renovation programme
            try{
                if(getDataElement("gooWsSVbGMo").equals("true"))
                {
                    oth_inadequate_enrolment_house_radio_yes.setChecked(true);
                    oth_inadequate_enrolment_house_radio_no.setChecked(false);
                }else if(getDataElement("gooWsSVbGMo").equals("false"))
                {
                    oth_inadequate_enrolment_house_radio_yes.setChecked(false);
                    oth_inadequate_enrolment_house_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inadequate_enrolment_house_radio.clearCheck();
            }

            // Establish water treatment method to community  water supply scheme
            try{
                if(getDataElement("TJxuf5n6Sh8").equals("true"))
                {
                    oth_indequate_establish_water_radio_yes.setChecked(true);
                    oth_indequate_establish_water_radio_no.setChecked(false);
                }else if(getDataElement("TJxuf5n6Sh8").equals("false"))
                {
                    oth_indequate_establish_water_radio_yes.setChecked(false);
                    oth_indequate_establish_water_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_indequate_establish_water_radio.clearCheck();
            }

            // Improve existing water source(from unprotected to protected)
            try{
                if(getDataElement("d3Ddo3UodPp").equals("true"))
                {
                    oth_indequate_imporve_water_radio_yes.setChecked(true);
                    oth_indequate_imporve_water_radio_no.setChecked(false);
                }else if(getDataElement("d3Ddo3UodPp").equals("false"))
                {
                    oth_indequate_imporve_water_radio_yes.setChecked(false);
                    oth_indequate_imporve_water_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_indequate_imporve_water_radio.clearCheck();
            }

            // Introduce alternative water sources (rain water harvesting)
            try{
                if(getDataElement("dRfB9wDGjkV").equals("true"))
                {
                    oth_indequate_alternative_water_radio_yes.setChecked(true);
                    oth_indequate_alternative_water_radio_no.setChecked(false);
                }else if(getDataElement("dRfB9wDGjkV").equals("false"))
                {
                    oth_indequate_alternative_water_radio_yes.setChecked(false);
                    oth_indequate_alternative_water_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_indequate_alternative_water_radio.clearCheck();
            }

            // Introduce household water treatment methods
            try{
                if(getDataElement("OahxhG02NZW").equals("true"))
                {
                    oth_inadequate_household_water_radio_yes.setChecked(true);
                    oth_inadequate_household_water_radio_no.setChecked(false);
                }else if(getDataElement("OahxhG02NZW").equals("false"))
                {
                    oth_inadequate_household_water_radio_yes.setChecked(false);
                    oth_inadequate_household_water_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inadequate_household_water_radio.clearCheck();
            }

            // Monitor safely managed sanitation
            try{
                if(getDataElement("p1f8x33HE3R").equals("true"))
                {
                    oth_inadequate_monitor_sanitation_radio_yes.setChecked(true);
                    oth_inadequate_monitor_sanitation_radio_no.setChecked(false);
                }else if(getDataElement("p1f8x33HE3R").equals("false"))
                {
                    oth_inadequate_monitor_sanitation_radio_yes.setChecked(false);
                    oth_inadequate_monitor_sanitation_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inadequate_monitor_sanitation_radio.clearCheck();
            }

            // Provide access to safe water supply systems
            try{
                if(getDataElement("LAA4JitW0KY").equals("true"))
                {
                    oth_inadequate_provide_safe_water_radio_yes.setChecked(true);
                    oth_inadequate_provide_safe_water_radio_no.setChecked(false);
                }else if(getDataElement("LAA4JitW0KY").equals("false"))
                {
                    oth_inadequate_provide_safe_water_radio_yes.setChecked(false);
                    oth_inadequate_provide_safe_water_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inadequate_provide_safe_water_radio.clearCheck();
            }

            // Provide improved sanitation/latrine facilities
            try{
                if(getDataElement("RuNUs7ojSv8").equals("true"))
                {
                    oth_inadequate_provide_improved_radio_yes.setChecked(true);
                    oth_inadequate_provide_improved_radio_no.setChecked(false);
                }else if(getDataElement("RuNUs7ojSv8").equals("false"))
                {
                    oth_inadequate_provide_improved_radio_yes.setChecked(false);
                    oth_inadequate_provide_improved_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inadequate_provide_improved_radio.clearCheck();
            }

            // Renovation of tube well
            try{
                if(getDataElement("umK1U1V3Bac").equals("true"))
                {
                    oth_inadequate_renovation_radio_yes.setChecked(true);
                    oth_inadequate_renovation_radio_no.setChecked(false);
                }else if(getDataElement("umK1U1V3Bac").equals("false"))
                {
                    oth_inadequate_renovation_radio_yes.setChecked(false);
                    oth_inadequate_renovation_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inadequate_renovation_radio.clearCheck();
            }

            // Water quality monitoring
            try{
                if(getDataElement("VQHhTFq6pyq").equals("true"))
                {
                    oth_inadequate_water_quality_radio_yes.setChecked(true);
                    oth_inadequate_water_quality_radio_no.setChecked(false);
                }else if(getDataElement("VQHhTFq6pyq").equals("false"))
                {
                    oth_inadequate_water_quality_radio_yes.setChecked(false);
                    oth_inadequate_water_quality_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inadequate_water_quality_radio.clearCheck();
            }


        }
        else{
            textView_Date.setText("Click here to set Date");
        }


        if (EventFormService.getInstance().init(
                Sdk.d2(),
                eventUid,
                programUid,
                getIntent().getStringExtra(OtherInadequateWaterActivity.IntentExtra.OU_UID.name())))
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
        if (formType == OtherInadequateWaterActivity.FormType.CREATE)
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
