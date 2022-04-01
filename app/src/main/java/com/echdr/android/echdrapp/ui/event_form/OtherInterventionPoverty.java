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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.DateFormatHelper;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.processors.PublishProcessor;

public class OtherInterventionPoverty extends AppCompatActivity {

    private String eventUid;
    private String programUid;
    private String selectedChild;
    private OtherInterventionPoverty.FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;


    private RadioGroup oth_inter_povert_training_radio;
    private RadioButton oth_inter_povert_training_radio_yes;
    private RadioButton oth_inter_povert_training_radio_no;
    private RadioGroup oth_inter_povert_support_radio;
    private RadioButton oth_inter_povert_support_radio_yes;
    private RadioButton oth_inter_povert_support_radio_no;

    private RadioGroup oth_inter_povert_livelihood_radio;
    private RadioButton oth_inter_povert_livelihood_radio_yes;
    private RadioButton oth_inter_povert_livelihood_radio_no;
    private RadioGroup oth_inter_povert_polu_support_radio;
    private RadioButton oth_inter_povert_polu_support_radio_yes;
    private RadioButton oth_inter_povert_polu_support_radio_no;

    private RadioGroup oth_inter_povert_empower_training_radio;
    private RadioButton oth_inter_povert_empower_training_radio_yes;
    private RadioButton oth_inter_povert_empower_training_radio_no;
    private RadioGroup oth_inter_povert_financial_radio;
    private RadioButton oth_inter_povert_financial_radio_yes;
    private RadioButton oth_inter_povert_financial_radio_no;

    private RadioGroup oth_inter_povert_samurdhi_radio;
    private RadioButton oth_inter_povert_samurdhi_radio_yes;
    private RadioButton oth_inter_povert_samurdhi_radio_no;
    private RadioGroup oth_inter_povert_samurdhi_bank_radio;
    private RadioButton oth_inter_povert_samurdhi_bank_radio_yes;
    private RadioButton oth_inter_povert_samurdhi_bank_radio_no;

    private RadioGroup oth_inter_povert_comm_cultivation_radio;
    private RadioButton oth_inter_povert_comm_cultivation_radio_yes;
    private RadioButton oth_inter_povert_comm_cultivation_radio_no;
    private RadioGroup oth_inter_povert_market_access_radio;
    private RadioButton oth_inter_povert_market_access_radio_yes;
    private RadioButton oth_inter_povert_market_access_radio_no;

    private TrackedEntityAttributeValue birthday;


    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               OtherInterventionPoverty.FormType type, String teiID) {
        Intent intent = new Intent(context, OtherInterventionPoverty.class);
        intent.putExtra(OtherInterventionPoverty.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(OtherInterventionPoverty.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(OtherInterventionPoverty.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(OtherInterventionPoverty.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(OtherInterventionPoverty.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_intervention_poverty);

        textView_Date = findViewById(R.id.editTextDateOtherInterventionPoverty);
        //datePicker         = findViewById(R.id.other_intervention_poverty_date_pick);
        oth_inter_povert_training_radio = findViewById(R.id.oth_inter_povert_training_radio);
        oth_inter_povert_training_radio_yes = findViewById(R.id.oth_inter_povert_training_radio_yes);
        oth_inter_povert_training_radio_no = findViewById(R.id.oth_inter_povert_training_radio_no);
        oth_inter_povert_support_radio = findViewById(R.id.oth_inter_povert_support_radio);
        oth_inter_povert_support_radio_yes = findViewById(R.id.oth_inter_povert_support_radio_yes);
        oth_inter_povert_support_radio_no = findViewById(R.id.oth_inter_povert_support_radio_no);

        oth_inter_povert_livelihood_radio = findViewById(R.id.oth_inter_povert_livelihood_radio);
        oth_inter_povert_livelihood_radio_yes = findViewById(R.id.oth_inter_povert_livelihood_radio_yes);
        oth_inter_povert_livelihood_radio_no = findViewById(R.id.oth_inter_povert_livelihood_radio_no);
        oth_inter_povert_polu_support_radio = findViewById(R.id.oth_inter_povert_polu_support_radio);
        oth_inter_povert_polu_support_radio_yes = findViewById(R.id.oth_inter_povert_polu_support_radio_yes);
        oth_inter_povert_polu_support_radio_no = findViewById(R.id.oth_inter_povert_polu_support_radio_no);

        oth_inter_povert_empower_training_radio = findViewById(R.id.oth_inter_povert_empower_training_radio);
        oth_inter_povert_empower_training_radio_yes = findViewById(R.id.oth_inter_povert_empower_training_radio_yes);
        oth_inter_povert_empower_training_radio_no = findViewById(R.id.oth_inter_povert_empower_training_radio_no);
        oth_inter_povert_financial_radio = findViewById(R.id.oth_inter_povert_financial_radio);
        oth_inter_povert_financial_radio_yes = findViewById(R.id.oth_inter_povert_financial_radio_yes);
        oth_inter_povert_financial_radio_no = findViewById(R.id.oth_inter_povert_financial_radio_no);

        oth_inter_povert_samurdhi_radio = findViewById(R.id.oth_inter_povert_samurdhi_radio);
        oth_inter_povert_samurdhi_radio_yes = findViewById(R.id.oth_inter_povert_samurdhi_radio_yes);
        oth_inter_povert_samurdhi_radio_no = findViewById(R.id.oth_inter_povert_samurdhi_radio_no);
        oth_inter_povert_samurdhi_bank_radio = findViewById(R.id.oth_inter_povert_samurdhi_bank_radio);
        oth_inter_povert_samurdhi_bank_radio_yes = findViewById(R.id.oth_inter_povert_samurdhi_bank_radio_yes);
        oth_inter_povert_samurdhi_bank_radio_no = findViewById(R.id.oth_inter_povert_samurdhi_bank_radio_no);

        oth_inter_povert_comm_cultivation_radio = findViewById(R.id.oth_inter_povert_comm_cultivation_radio);
        oth_inter_povert_comm_cultivation_radio_yes = findViewById(R.id.oth_inter_povert_comm_cultivation_radio_yes);
        oth_inter_povert_comm_cultivation_radio_no = findViewById(R.id.oth_inter_povert_comm_cultivation_radio_no);
        oth_inter_povert_market_access_radio = findViewById(R.id.oth_inter_povert_market_access_radio);
        oth_inter_povert_market_access_radio_yes = findViewById(R.id.oth_inter_povert_market_access_radio_yes);
        oth_inter_povert_market_access_radio_no = findViewById(R.id.oth_inter_povert_market_access_radio_no);

        context = this;

        eventUid = getIntent().getStringExtra(OtherInterventionPoverty.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(OtherInterventionPoverty.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(OtherInterventionPoverty.IntentExtra.TEI_ID.name());
        formType = OtherInterventionPoverty.FormType.valueOf(getIntent().getStringExtra(OtherInterventionPoverty.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(OtherInterventionPoverty.IntentExtra.OU_UID.name());


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

        if(formType == OtherInterventionPoverty.FormType.CHECK)
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

            // set Agriculture based entrepreneur training
            try{
                if(getDataElement("d9SXXlGKOEm").equals("true"))
                {
                    oth_inter_povert_training_radio_yes.setChecked(true);
                    oth_inter_povert_training_radio_no.setChecked(false);
                }else if(getDataElement("d9SXXlGKOEm").equals("false"))
                {
                    oth_inter_povert_training_radio_yes.setChecked(false);
                    oth_inter_povert_training_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inter_povert_training_radio.clearCheck();
            }

            // Agriculture based livelihood support
            try{
                if(getDataElement("woDz1zS45gM").equals("true"))
                {
                    oth_inter_povert_support_radio_yes.setChecked(true);
                    oth_inter_povert_support_radio_no.setChecked(false);
                }else if(getDataElement("woDz1zS45gM").equals("false"))
                {
                    oth_inter_povert_support_radio_yes.setChecked(false);
                    oth_inter_povert_support_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inter_povert_support_radio.clearCheck();
            }

            // Asset transfer to establish livelihood
            try{
                if(getDataElement("LNSkdGpTAS3").equals("true"))
                {
                    oth_inter_povert_livelihood_radio_yes.setChecked(true);
                    oth_inter_povert_livelihood_radio_no.setChecked(false);
                }else if(getDataElement("LNSkdGpTAS3").equals("false"))
                {
                    oth_inter_povert_livelihood_radio_yes.setChecked(false);
                    oth_inter_povert_livelihood_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inter_povert_livelihood_radio.clearCheck();
            }

            // Commercial livestock and poultry support
            try{
                if(getDataElement("pAK1zskmBtv").equals("true"))
                {
                    oth_inter_povert_polu_support_radio_yes.setChecked(true);
                    oth_inter_povert_polu_support_radio_no.setChecked(false);
                }else if(getDataElement("pAK1zskmBtv").equals("false"))
                {
                    oth_inter_povert_polu_support_radio_yes.setChecked(false);
                    oth_inter_povert_polu_support_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inter_povert_polu_support_radio.clearCheck();
            }

            // Empowerment training
            try{
                if(getDataElement("JDY9XpKsT8R").equals("true"))
                {
                    oth_inter_povert_empower_training_radio_yes.setChecked(true);
                    oth_inter_povert_empower_training_radio_no.setChecked(false);
                }else if(getDataElement("JDY9XpKsT8R").equals("false"))
                {
                    oth_inter_povert_empower_training_radio_yes.setChecked(false);
                    oth_inter_povert_empower_training_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inter_povert_empower_training_radio.clearCheck();
            }

            // Financial literacy Training
            try{
                if(getDataElement("C2l3nzHxUsF").equals("true"))
                {
                    oth_inter_povert_financial_radio_yes.setChecked(true);
                    oth_inter_povert_financial_radio_no.setChecked(false);
                }else if(getDataElement("C2l3nzHxUsF").equals("false"))
                {
                    oth_inter_povert_financial_radio_yes.setChecked(false);
                    oth_inter_povert_financial_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inter_povert_financial_radio.clearCheck();
            }

            // Provision of Samurdhi
            try{
                if(getDataElement("zEboWO8OicF").equals("true"))
                {
                    oth_inter_povert_samurdhi_radio_yes.setChecked(true);
                    oth_inter_povert_samurdhi_radio_no.setChecked(false);
                }else if(getDataElement("zEboWO8OicF").equals("false"))
                {
                    oth_inter_povert_samurdhi_radio_yes.setChecked(false);
                    oth_inter_povert_samurdhi_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inter_povert_samurdhi_radio.clearCheck();
            }

            // Provision of Samurdhi bank loan
            try{
                if(getDataElement("D6igixSguKT").equals("true"))
                {
                    oth_inter_povert_samurdhi_bank_radio_yes.setChecked(true);
                    oth_inter_povert_samurdhi_bank_radio_no.setChecked(false);
                }else if(getDataElement("D6igixSguKT").equals("false"))
                {
                    oth_inter_povert_samurdhi_bank_radio_yes.setChecked(false);
                    oth_inter_povert_samurdhi_bank_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inter_povert_samurdhi_bank_radio.clearCheck();
            }

            // Support community water harvesting
            try{
                if(getDataElement("Wd8fR0wRW71").equals("true"))
                {
                    oth_inter_povert_comm_cultivation_radio_yes.setChecked(true);
                    oth_inter_povert_comm_cultivation_radio_no.setChecked(false);
                }else if(getDataElement("Wd8fR0wRW71").equals("false"))
                {
                    oth_inter_povert_comm_cultivation_radio_yes.setChecked(false);
                    oth_inter_povert_comm_cultivation_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inter_povert_comm_cultivation_radio.clearCheck();
            }

            // Value chain support and  increased market
            try{
                if(getDataElement("KjX4j1qI3W6").equals("true"))
                {
                    oth_inter_povert_market_access_radio_yes.setChecked(true);
                    oth_inter_povert_market_access_radio_no.setChecked(false);
                }else if(getDataElement("KjX4j1qI3W6").equals("false"))
                {
                    oth_inter_povert_market_access_radio_yes.setChecked(false);
                    oth_inter_povert_market_access_radio_no.setChecked(true);
                }
            }
            catch (Exception e)
            {
                oth_inter_povert_market_access_radio.clearCheck();
            }


        }
        else{
            textView_Date.setText("Click here to set Date");
        }


        if (EventFormService.getInstance().init(
                Sdk.d2(),
                eventUid,
                programUid,
                getIntent().getStringExtra(OtherInterventionPoverty.IntentExtra.OU_UID.name())))
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
        if (formType == OtherInterventionPoverty.FormType.CREATE)
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
