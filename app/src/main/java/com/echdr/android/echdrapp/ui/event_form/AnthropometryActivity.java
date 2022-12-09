package com.echdr.android.echdrapp.ui.event_form;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.echdr.android.echdrapp.service.Service.AnthropometryChartService;
import com.echdr.android.echdrapp.service.Service.AnthropometryService;
import com.echdr.android.echdrapp.service.Validator.AnthropometryValidator;
import com.echdr.android.echdrapp.service.util;
import com.jjoe64.graphview.GraphView;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.processors.PublishProcessor;

public class AnthropometryActivity extends AppCompatActivity {

        private static final String TAG = "AnthropometryActivity";
        private String eventUid;
        private String programUid;
        private PublishProcessor<Boolean> engineInitialization;
        private RuleEngineService engineService;
        private FormType formType;
        private GraphView heightGraph;
        private GraphView weightGraph;
        private GraphView weightHeightGraph;
        private String selectedChild;
        private String sex;
        private TextView textView_Date;
        private ImageView datePicker;

        private String orgUnit;
        private TextView AgeInWeeksTxt;
        private TextView AgeInMonthsTxt;
        private TextView weightTxtKG;
        private Context context;
        private DatePickerDialog.OnDateSetListener setListener;
        private EditText heightTxt;
        private EditText weightTxt;
        private Button saveButton;
        private Button plotGraphButton;

        private TrackedEntityAttributeValue birthday;
        private TrackedEntityAttributeValue sex_d;

        Map<Integer, Integer> heightValues;
        Map<Integer, Integer> weightValues;

        Map<Integer, double[]> heightDataWHO;
        Map<Integer, double[]> weightDataWHO;
        Map<Double, double[]> weightForHeightDataWHO;

        private AnthropometryChartService anthropometryChartService;

        private enum IntentExtra {
            EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
        }

        public enum FormType {
            CREATE, CHECK
        }

        public static Intent getFormActivityIntent(Context context, String eventUid,
                                                   String programUid, String orgUnitUid,
                                                   FormType type, String teiID) {
            Intent intent = new Intent(context, AnthropometryActivity.class);
            intent.putExtra(IntentExtra.EVENT_UID.name(), eventUid);
            intent.putExtra(IntentExtra.PROGRAM_UID.name(), programUid);
            intent.putExtra(IntentExtra.OU_UID.name(), orgUnitUid);
            intent.putExtra(IntentExtra.TYPE.name(), type.name());
            intent.putExtra(IntentExtra.TEI_ID.name(), teiID);
            return intent;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_anthropometry_new);



            textView_Date       = findViewById(R.id.anthropometryDate);
            datePicker          = findViewById(R.id.anthropometry_date_pick);
            heightTxt           = findViewById(R.id.anthropometryHeight);
            weightTxt           = findViewById(R.id.anthropometryWeight);
            saveButton          = findViewById(R.id.anthropometrySave);
            heightGraph         = findViewById(R.id.heightforageAnthropometry);
            weightGraph         = findViewById(R.id.weightforageAnthropometry);
            weightHeightGraph   = findViewById(R.id.weightforheightAnthropometry);
            AgeInWeeksTxt       = findViewById(R.id.ageInWeeks);
            AgeInMonthsTxt      = findViewById(R.id.ageInMonths);
            plotGraphButton     = findViewById(R.id.plotGraph);
            weightTxtKG         = findViewById(R.id.anthropometryWeightInKg);

            eventUid        = getIntent().getStringExtra(IntentExtra.EVENT_UID.name());
            programUid      = getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name());
            selectedChild   = getIntent().getStringExtra(IntentExtra.TEI_ID.name());
            formType        = FormType.valueOf(getIntent().getStringExtra(IntentExtra.TYPE.name()));
            orgUnit         = getIntent().getStringExtra(IntentExtra.OU_UID.name());

            engineInitialization = PublishProcessor.create();

            AnthropometryService anthropometryService = new AnthropometryService();


            // Get the birthday of the child
            birthday = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                    .byTrackedEntityInstance().eq(selectedChild)
                    .byTrackedEntityAttribute().eq("qNH202ChkV3")
                    .one().blockingGet();

            // Get the sex of the child
            sex_d = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                    .byTrackedEntityInstance().eq(selectedChild)
                    .byTrackedEntityAttribute().eq("lmtzQrlHMYF")
                    .one().blockingGet();

            Log.i(TAG, String.format("Birthday : %s and sex : %s", birthday.value(), sex_d.value()));

            if (sex_d == null) {
                Log.e(TAG, "TrackedEntityAttributeValue for sex is NULL");
            }

            sex = sex_d.value();
            context = this;
            anthropometryService.setContext(context);

            heightValues = new HashMap<>();
            weightValues = new HashMap<>();
            selectDataSets();

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

            datePicker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    datePickerDialog.show();
                }
            });



            String date_string = "";
            setListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month = month+1;

                    String date_string = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth) ;
                    textView_Date.setText(date_string);
                    anthropometryService.setAgeInWeeks( birthday, textView_Date, AgeInWeeksTxt, AgeInMonthsTxt);

                    // make sure weight and height is set after date is picked
                    // unless color changes will not work properly
                    weightTxt.setEnabled(true);
                    heightTxt.setEnabled(true);
                }
            };


            // Load the existing values - form.CHECK
            if(formType == AnthropometryActivity.FormType.CHECK)
            {
                // set date
                try{
                    String prev_date = util.getDataElement("YB21tVtxZ0z", eventUid);
                    if(!prev_date.isEmpty())
                    {
                        textView_Date.setText(prev_date);

                        weightTxt.setEnabled(true);
                        heightTxt.setEnabled(true);
                    }
                }
                catch (Exception e)
                {
                    textView_Date.setText("");
                }

                heightTxt.setText(util.getDataElement("cDXlUgg1WiZ", eventUid));
                weightTxt.setText(util.getDataElement("rBRI27lvfY5", eventUid));

                String Currentweight;
                if (weightTxt.getText().toString().isEmpty()) {
                    Currentweight = "";
                } else {
                    Currentweight = String.valueOf(
                            Float.parseFloat(weightTxt.getText().toString()) / 1000f);
                    weightTxtKG.setText(Currentweight);
                }

                // First set age in weeks because change color uses its value
                anthropometryService.setAgeInWeeks( birthday, textView_Date, AgeInWeeksTxt, AgeInMonthsTxt);

                anthropometryService.ChangeColor(heightTxt, heightTxt.getText().toString(),
                        heightDataWHO, true, AgeInWeeksTxt);
                anthropometryService.ChangeColor(weightTxt, Currentweight,
                        weightDataWHO, true, AgeInWeeksTxt);

            }else{
                textView_Date.setText(R.string.date_button_text);
            }

            changeListeners(heightTxt, heightDataWHO, true, anthropometryService);
            changeListeners(weightTxt, weightDataWHO, false, anthropometryService);

            saveButton.setOnClickListener(v -> {
                boolean wasSuccessful = saveElements();
                if(wasSuccessful) {
                    finishEnrollment();
                }
            });


            anthropometryChartService = new AnthropometryChartService(heightGraph, weightGraph,
                    weightHeightGraph, sex, context, selectedChild, birthday, heightValues, weightValues);

            plotGraphButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveElements();
                    weightGraph.removeAllSeries();
                    heightGraph.removeAllSeries();
                    weightHeightGraph.removeAllSeries();

                    anthropometryChartService.plotGraph();
                }
            });

            anthropometryChartService.plotGraph();

            if (EventFormService.getInstance().init(
                    Sdk.d2(),
                    eventUid,
                    programUid,
                    getIntent().getStringExtra(IntentExtra.OU_UID.name())))
                this.engineService = new RuleEngineService();
        }

        private void changeListeners(EditText editText, Map<Integer, double[]> dataWHO,
                                     boolean isHeight, AnthropometryService anthropometryService){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    anthropometryService.ChangeColor(editText, s.toString(), dataWHO, isHeight,
                        AgeInWeeksTxt );

                    if(!isHeight){
                        String Currentweight;
                        if (editText.getText().toString().isEmpty()) {
                            Currentweight = "";
                        } else {
                            Currentweight = String.valueOf(
                                    Float.parseFloat(weightTxt.getText().toString()) / 1000f);
                            weightTxtKG.setText(Currentweight);
                        }
                    }
                }
            });
        }

        @Override
        protected void onResume() {
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
            if (formType == FormType.CREATE)
                EventFormService.getInstance().delete();
            setResult(RESULT_CANCELED);
            finish();
        }


        private boolean saveElements() {
            AnthropometryValidator anthropometryValidator = new AnthropometryValidator();

            anthropometryValidator.setHeightTxt(heightTxt);
            anthropometryValidator.setWeightTxt(weightTxt);
            anthropometryValidator.setTextView_Date(textView_Date);
            anthropometryValidator.setTAG(TAG);
            anthropometryValidator.setContext(context);

            if(!anthropometryValidator.validate()){
                Log.e(TAG, "Error occurred while trying to save tracked entity instance" );
                return false;
            }

            util.saveDataElement("YB21tVtxZ0z", textView_Date.getText().toString(),
                    eventUid, programUid, orgUnit, engineInitialization);
            util.saveDataElement("cDXlUgg1WiZ", heightTxt.getText().toString(),
                    eventUid, programUid, orgUnit, engineInitialization);
            util.saveDataElement("rBRI27lvfY5", weightTxt.getText().toString(),
                    eventUid, programUid, orgUnit, engineInitialization);

            //finishEnrollment();
            return true;
        }

    private void selectDataSets()
    {
        DataValuesWHO d = DataValuesWHO.getInstance();
        if(sex.equals("Male"))
        {
            d.initializeheightForAgeBoys();
            d.initializeweightForAgeBoys();
            d.initializeweightForHeightBoys();
            heightDataWHO = d.getHeightForAgeBoys();
            weightDataWHO = d.getWeightForAgeBoys();
            weightForHeightDataWHO = d.getWeightForHeightBoys();
        }else
        {
            d.initializeweightForAgeGirls();
            d.initializeheightForAgeGirls();
            d.initializeweightForHeightGirls();
            heightDataWHO = d.getHeightForAgeGirls();
            weightDataWHO = d.getWeightForAgeGirls();
            weightForHeightDataWHO = d.getWeightForHeightGirls();
        }
    }

    private void finishEnrollment() {
        setResult(RESULT_OK);
        finish();
    }

}