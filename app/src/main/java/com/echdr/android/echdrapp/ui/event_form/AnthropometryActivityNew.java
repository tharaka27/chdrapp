package com.echdr.android.echdrapp.ui.event_form;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.echdr.android.echdrapp.service.AnthropometryChartService;
import com.echdr.android.echdrapp.service.Validator.AnthropometryValidator;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;
import org.hisp.dhis.rules.parser.expression.function.ScalarFunctionToEvaluate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.processors.PublishProcessor;

public class AnthropometryActivityNew extends AppCompatActivity {

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
        private Context context;
        private DatePickerDialog.OnDateSetListener setListener;
        private EditText heightTxt;
        private EditText weightTxt;
        private Button saveButton;
        private Button plotGraphButton;
//        private ImageView backButton;

        private TrackedEntityAttributeValue birthday;

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
            Intent intent = new Intent(context, AnthropometryActivityNew.class);
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

            textView_Date = findViewById(R.id.anthropometryDate);
            datePicker = findViewById(R.id.anthropometry_date_pick);
            heightTxt = findViewById(R.id.anthropometryHeight);
            weightTxt = findViewById(R.id.anthropometryWeight);
            saveButton = findViewById(R.id.anthropometrySave);
            heightGraph = findViewById(R.id.heightforageAnthropometry);
            weightGraph = findViewById(R.id.weightforageAnthropometry);
            weightHeightGraph = findViewById(R.id.weightforheightAnthropometry);
            AgeInWeeksTxt = findViewById(R.id.ageInWeeks);
            AgeInMonthsTxt  =  findViewById(R.id.ageInMonths);
            plotGraphButton = findViewById(R.id.plotGraph);
//            backButton = findViewById(R.id.back_btn_anthopo);

            eventUid = getIntent().getStringExtra(IntentExtra.EVENT_UID.name());
            programUid = getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name());
            selectedChild = getIntent().getStringExtra(IntentExtra.TEI_ID.name());
            formType = FormType.valueOf(getIntent().getStringExtra(IntentExtra.TYPE.name()));
            orgUnit = getIntent().getStringExtra(IntentExtra.OU_UID.name());

            engineInitialization = PublishProcessor.create();

            // Get the birthday of the child
            birthday = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                    .byTrackedEntityInstance().eq(selectedChild)
                    .byTrackedEntityAttribute().eq("qNH202ChkV3")
                    .one().blockingGet();

            // Get the sex of the child
            TrackedEntityAttributeValue sex_d = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                    .byTrackedEntityInstance().eq(selectedChild)
                    .byTrackedEntityAttribute().eq("lmtzQrlHMYF")
                    .one().blockingGet();

            if (sex_d == null)
                System.out.println("Sex is null");

            sex = sex_d.value();

            context = this;

            heightValues = new HashMap<>();
            weightValues = new HashMap<>();
            selectDataSets();

            anthropometryChartService = new AnthropometryChartService();


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
                    setAgeInWeeks();

                    // make sure weight and height is set after date is picked
                    // unless color changes will not work properly
                    weightTxt.setEnabled(true);
                    heightTxt.setEnabled(true);
                }
            };


            // Load the existing values - form.CHECK
            if(formType == AnthropometryActivityNew.FormType.CHECK)
            {
                // set date
                try{
                    String prev_date = getDataElement("YB21tVtxZ0z");
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

                heightTxt.setText(getDataElement("cDXlUgg1WiZ"));
                weightTxt.setText(getDataElement("rBRI27lvfY5"));

                String Currentweight;
                if (weightTxt.getText().toString().isEmpty()) {
                    Currentweight = "";
                } else {
                    Currentweight = String.valueOf(
                            Float.parseFloat(weightTxt.getText().toString()) / 1000f);
                }

                // First set age in weeks because change color uses its value
                setAgeInWeeks();

                ChangeColor(heightTxt, heightTxt.getText().toString(), heightDataWHO, true);
                ChangeColor(weightTxt, Currentweight, weightDataWHO, true);



            }else{
                textView_Date.setText("Click here to set Date");
            }

            changeListeners(heightTxt, heightDataWHO, true);
            changeListeners(weightTxt, weightDataWHO, false);

            saveButton.setOnClickListener(v -> {
                boolean wasSuccessful = saveElements();
                if(wasSuccessful) {
                    finishEnrollment();
                }
            });

            anthropometryChartService.setHeightGraph(heightGraph);
            anthropometryChartService.setWeightGraph(weightGraph);
            anthropometryChartService.setWeightHeightGraph(weightHeightGraph);
            anthropometryChartService.setSex(sex);
            anthropometryChartService.setContext(context);
            anthropometryChartService.setSelectedChild(selectedChild);
            anthropometryChartService.setBirthday(birthday);
            anthropometryChartService.setHeightValues(heightValues);
            anthropometryChartService.setWeightValues(weightValues);


            plotGraphButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveElements();
                    weightGraph.removeAllSeries();
                    heightGraph.removeAllSeries();

                    anthropometryChartService.plotGraph();
                    anthropometryChartService.plotDataElements();
                    anthropometryChartService.drawLineGraph();
                }
            });

            anthropometryChartService.plotGraph();
            anthropometryChartService.plotDataElements();
            anthropometryChartService.drawLineGraph();


            if (EventFormService.getInstance().init(
                    Sdk.d2(),
                    eventUid,
                    programUid,
                    getIntent().getStringExtra(IntentExtra.OU_UID.name())))
                this.engineService = new RuleEngineService();
        }

        private void changeListeners(EditText editText, Map<Integer, double[]> dataWHO, boolean isHeight){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    ChangeColor(editText, s.toString(), dataWHO, isHeight);
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

            //EventFormService.clear();
            //setResult(RESULT_OK);
            //finish();
        }



        private boolean saveElements() {
            AnthropometryValidator anthropometryValidator = new AnthropometryValidator();

            anthropometryValidator.setHeightTxt(heightTxt);
            anthropometryValidator.setWeightTxt(weightTxt);
            anthropometryValidator.setTextView_Date(textView_Date);
            anthropometryValidator.setTAG(TAG);
            anthropometryValidator.setContext(context);

            if(!anthropometryValidator.validate()){
                Log.e(TAG, "Error occured while trying to save tracked entity instance" );
                return false;
            }

            saveDataElement("YB21tVtxZ0z", textView_Date.getText().toString());
            saveDataElement("cDXlUgg1WiZ", heightTxt.getText().toString());
            saveDataElement("rBRI27lvfY5", weightTxt.getText().toString());

            //finishEnrollment();
            return true;
        }

        private void ChangeColor(EditText text, String s,
                                 Map<Integer, double[]> data, boolean height) {
            int currentAge = 0;
            if(!(AgeInWeeksTxt.getText().toString().isEmpty() ||
                    AgeInWeeksTxt.getText().toString().equals("Age in weeks")))
                currentAge = Integer.parseInt(AgeInWeeksTxt.getText().toString());

            float currentValue;
            if (s.isEmpty()) {
                currentValue = 0;
                text.setBackgroundColor(Color.WHITE);
                return;
            } else {
                if (height) {
                    currentValue = Float.parseFloat(s);
                } else {
                    currentValue = Float.parseFloat(s) / 1000f;
                }
            }

            int category = 0;
            try {
                System.out.println("Change color : " + currentAge +" currentValue" + currentValue);

                // divide by 4 to covert to months
                double[] array = data.get( currentAge/4 );
                System.out.println("month : " + String.valueOf(currentAge/4) +" currentValue " + currentValue);
                for (category = 0; category < 4; ) {

                    assert array != null;
                    if (array[category] < currentValue) {
                        category++;
                    } else {
                        break;
                    }
                }
                category = category - 1;
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Suitable Category is " + category);

            switch (category) {
                case -1:
                    //text.setBackgroundColor(Color.RED);
                    //text.setBackgroundColor(Color.parseColor("#e6653b"));
                    text.setBackgroundColor(Color.parseColor("#a60c0c"));
                    break;
                case 0:
                    //text.setBackgroundColor(Color.rgb(255, 165, 0)); // orange
                    //text.setBackgroundColor(Color.parseColor("#ccc971"));
                    text.setBackgroundColor(Color.parseColor("#F6A21E"));
                    break;
                case 1:
                    //text.setBackgroundColor(Color.YELLOW);
                    //text.setBackgroundColor(Color.parseColor("#afe1bb"));
                    text.setBackgroundColor(Color.parseColor("#afe1bb"));
                    break;
                case 2:
                    //text.setBackgroundColor(Color.GREEN);
                    //text.setBackgroundColor(Color.parseColor("#a3ccae"));
                    text.setBackgroundColor(Color.parseColor("#a3ccae"));
                    break;
                case 3:
                    //text.setBackgroundColor(Color.rgb(215, 31, 226)); // purple color
                    //text.setBackgroundColor(Color.parseColor("#f3e5f6"));
                    text.setBackgroundColor(Color.parseColor("#f3e5f6"));
                    break;

            }

        }

    private void setAgeInWeeks()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


            try {
                // Parse event date
                Date dob = formatter.parse(birthday.value());
                Date eventDate = formatter.parse(textView_Date.getText().toString());

                // Calculate age in weeks
                long diffInMillies = Math.abs(eventDate.getTime() - dob.getTime());

                int diff = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 7;
                double diffWeeks = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 7 / 52.143;

                int yrs = diff/52;
                //if(yrs==0){
                AgeInWeeksTxt.setText(String.valueOf(diff));

                String doubleAsString = String.valueOf(diffWeeks);
                int indexOfDecimal = doubleAsString.indexOf(".");
                //System.out.println("Double Number: " + diff);
                //System.out.println("Integer Part: " + doubleAsString.substring(0, indexOfDecimal));
                //System.out.println("Decimal Part: " + doubleAsString.substring(indexOfDecimal));

                //double months = Double.valueOf(doubleAsString.substring(indexOfDecimal)) * 12;
                //AgeInMonthsTxt.setText(String.valueOf(diff));
                double months = Math.round(Double.valueOf(doubleAsString.substring(indexOfDecimal)) * 12 *  100)/100;

                AgeInMonthsTxt.setText(doubleAsString.substring(0, indexOfDecimal) + " years " + months+  " months");
                //}else
                //{
                //    String display = String.valueOf(yrs) +"yrs " + String.valueOf(diff%52);
                //    AgeInWeeksTxt.setText(display);
                //}


            } catch (Exception error) {
                System.out.print("Error in parsing date field: " + error.toString());
            }
    }

    private void setAgeInYearsAndMonths()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Parse event date
            Date dob = formatter.parse(birthday.value());
            Date eventDate = formatter.parse(textView_Date.getText().toString());

            // Calculate age in weeks
            long diffInMillies = Math.abs(eventDate.getTime() - dob.getTime());
            double diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 7 / 52.143;

            String doubleAsString = String.valueOf(diff);
            int indexOfDecimal = doubleAsString.indexOf(".");
            //System.out.println("Double Number: " + diff);
            //System.out.println("Integer Part: " + doubleAsString.substring(0, indexOfDecimal));
            //System.out.println("Decimal Part: " + doubleAsString.substring(indexOfDecimal));

            //double months = Double.valueOf(doubleAsString.substring(indexOfDecimal)) * 12;
            //AgeInMonthsTxt.setText(String.valueOf(diff));
            double months = Math.round(Double.valueOf(doubleAsString.substring(indexOfDecimal)) * 12 *  100)/100;

            AgeInMonthsTxt.setText(doubleAsString.substring(0, indexOfDecimal) + " years " + months+  " months");

        } catch (Exception error) {
            System.out.print("Error in parsing date field: " + error.toString());
        }
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


    private void selectDate(int year, int month, int day)
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private String getDataElement(String dataElement) {
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


    private void saveDataElement(String dataElement, String value) {
        TrackedEntityDataValueObjectRepository valueRepository;
        try {
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                    .value(
                            EventFormService.getInstance().getEventUid(),
                            dataElement
                    );
        } catch (Exception e) {
            EventFormService.getInstance().init(
                    Sdk.d2(),
                    eventUid,
                    programUid,
                    getIntent().getStringExtra(AnthropometryActivityNew.IntentExtra.OU_UID.name()));
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

        try {
            if (!isEmpty(value)) {
                valueRepository.blockingSet(value);
            } else {
                valueRepository.blockingDeleteIfExist();
            }
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        } finally {
            if (!value.equals(currentValue)) {
                engineInitialization.onNext(true);
            }
        }
    }

    private void finishEnrollment() {
        setResult(RESULT_OK);
        finish();
    }

}