package com.echdr.android.echdrapp.ui.event_form;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.echdr.android.echdrapp.service.Service.UnenrollmentService;
import com.echdr.android.echdrapp.service.Setter.DateSetter;
import com.echdr.android.echdrapp.service.Validator.OverweightOutcomeValidator;
import com.echdr.android.echdrapp.service.util;
import com.echdr.android.echdrapp.ui.enrollment_form.EnrollmentFormModified;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.processors.PublishProcessor;

public class OverweightOutcomeActivity extends AppCompatActivity {
    private static final String TAG = "OverweightOutcomeActivity";
    private String eventUid;
    private String programUid;
    private String selectedChild;
    private OverweightOutcomeActivity.FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private Spinner spinner_Enrollment;
    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;
    protected String[] other_type_array ;
    protected String[] english_other_type_array ;

    private TrackedEntityAttributeValue birthday;


    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               OverweightOutcomeActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, OverweightOutcomeActivity.class);
        intent.putExtra(OverweightOutcomeActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(OverweightOutcomeActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(OverweightOutcomeActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(OverweightOutcomeActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(OverweightOutcomeActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overweight_outcome);

        textView_Date       = findViewById(R.id.editTextDateOverweightOutcome);
        spinner_Enrollment  = findViewById(R.id.overweight_outcome_Enrollment_spinner);
        saveButton          = findViewById(R.id.overweightOutcomeSave);
        datePicker          = findViewById(R.id.over_out_date_pick);

        context = this;

        eventUid = getIntent().getStringExtra(OverweightOutcomeActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(OverweightOutcomeActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(OverweightOutcomeActivity.IntentExtra.TEI_ID.name());
        formType = OverweightOutcomeActivity.FormType.valueOf(getIntent().getStringExtra(OverweightOutcomeActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(OverweightOutcomeActivity.IntentExtra.OU_UID.name());

        other_type_array = getResources().getStringArray(R.array.overweight_outcome_type);
        english_other_type_array = getResources().getStringArray(R.array.overweight_outcome_type_english);


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

        setSpinner(spinner_Enrollment, R.array.overweight_outcome_type);

        // Load the existing values - form.CHECK
        if(formType == OverweightOutcomeActivity.FormType.CHECK)
        {
            // set date
            setTextView(textView_Date, "E5rWDjnuN6M", eventUid);

            // set enrollment type
            spinner_Enrollment.setSelection(
                    getSpinnerSelection("x9iPS2RiOKO", other_type_array));

        }
        else{
            textView_Date.setText(getString(R.string.date_button_text));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean wasSuccessful = saveElements();
                if(wasSuccessful) {
                    finishEnrollment();
                }
            }
        });

        if (EventFormService.getInstance().init(
                Sdk.d2(),
                eventUid,
                programUid,
                getIntent().getStringExtra(OverweightOutcomeActivity.IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();
    }

    @SuppressLint("LongLogTag")
    private boolean saveElements()
    {
        OverweightOutcomeValidator overweightOutcomeValidator = new OverweightOutcomeValidator();
        overweightOutcomeValidator.setTextView_Date(textView_Date);
        overweightOutcomeValidator.setContext(context);
        overweightOutcomeValidator.setSpinner_Enrollment(spinner_Enrollment);
        overweightOutcomeValidator.setBirthday(birthday);

        if(!overweightOutcomeValidator.validate()){
            Log.e(TAG, "Validation failure" );
            return false;
        }

        Map<String, String> dataElements = new HashMap<>();
        dataElements.put("E5rWDjnuN6M", textView_Date.getText().toString());
        dataElements.put("x9iPS2RiOKO", english_other_type_array[spinner_Enrollment.getSelectedItemPosition()]);

        UnenrollmentService.setSelectedChild(selectedChild);
        UnenrollmentService.setContext(context);

        UnenrollmentService.unenroll( textView_Date.getText().toString(),
                english_other_type_array[spinner_Enrollment.getSelectedItemPosition()]
                ,getString(R.string.unenroll_overWeight), dataElements, "JsfNVX0hdq9",
                eventUid, orgUnit, engineInitialization,
                ()->{
                    finishEnrollment();
                    return null;
                });

        return UnenrollmentService.isSuccessfulUnenrollment();
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
        if (formType == OverweightOutcomeActivity.FormType.CREATE)
            EventFormService.getInstance().delete();
        setResult(RESULT_CANCELED);
        finish();
    }

    private void setSpinner(Spinner spinner, Object object){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                (Integer) object,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new EnrollmentTypeSpinnerClass());
    }

    private void setTextView(TextView textView, String dataElement, String eventUid){
        try {
            String element = util.getDataElement(dataElement, eventUid);
            if (!element.isEmpty()) {
                textView.setText(dataElement);
            }
        } catch (Exception e) {
            textView.setText("");
        }
    }


    private int getSpinnerSelection(String dataElement, String [] array)
    {
        int itemPosition = -1;
        String stringElement = util.getDataElement(dataElement, eventUid);
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
