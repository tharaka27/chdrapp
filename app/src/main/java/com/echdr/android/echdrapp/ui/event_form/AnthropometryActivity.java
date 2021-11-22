package com.echdr.android.echdrapp.ui.event_form;

import static android.text.TextUtils.isEmpty;

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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.processors.PublishProcessor;

public class AnthropometryActivity extends AppCompatActivity {

    private String eventUid;
    private String programUid;
    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private FormType formType;
    private GraphView heightGraph;
    private GraphView weightGraph;
    private String selectedChild;
    private String sex;
    private TextView textView_Date;
    private ImageView datePicker;

    private String orgUnit;
    private TextView AgeInWeeksTxt;
    private Context context;
    private DatePickerDialog.OnDateSetListener setListener;
    private EditText heightTxt;
    private EditText weightTxt;
    private Button saveButton;
    private Button plotGraphButton;
    private TrackedEntityAttributeValue birthday;

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
        setContentView(R.layout.activity_anthropometry);

        textView_Date      = findViewById(R.id.anthropometryDate);
        datePicker         = findViewById(R.id.anthropometry_date_pick);
        heightTxt          = findViewById(R.id.anthropometryHeight);
        weightTxt          = findViewById(R.id.anthropometryWeight);
        saveButton         = findViewById(R.id.anthropometrySave);
        heightGraph        = findViewById(R.id.heightforageAnthropometry);
        weightGraph        = findViewById(R.id.weightforageAnthropometry);
        AgeInWeeksTxt      = findViewById(R.id.ageInWeeks);
        plotGraphButton    = findViewById(R.id.plotGraph);

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

        context = this;
        sex = sex_d.value();

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
                datePickerDialog.show();

            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(year, month, day);
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
            }
        };

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        final int currentAge;
        int currentAge1;

        try{
            System.out.println("Date of birth " + birthday.value());
            System.out.println("Selected date is " + getDataElement("YB21tVtxZ0z"));
            Date dob = formatter.parse(birthday.value());
            Date today = formatter.parse(textView_Date.getText().toString());
            long diffInMillies = today.getTime() - dob.getTime();

            currentAge1 = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 10;

            System.out.println("Current age in weeks is " + currentAge1);
        }
        catch (Exception error)
        {
            currentAge1 = 0;
            System.out.print("Error in parsing date field: " + error.toString());
        }

        currentAge = currentAge1;
        AgeInWeeksTxt.setText(String.valueOf(currentAge));

        saveButton.setOnClickListener(v -> {
            saveElements();
        });

        if (EventFormService.getInstance().init(
                Sdk.d2(),
                eventUid,
                programUid,
                getIntent().getStringExtra(IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();


        DataValuesWHO d = DataValuesWHO.getInstance();
        Map<Integer, double[]> heightDataWHO;
        Map<Integer, double[]> weightDataWHO;

        setupCharts(d);
        fillChart();

        if (sex.equals("Male")) {
            d.initializeweightForAgeBoys();
            d.initializeheightForAgeBoys();
            heightDataWHO = d.getHeightForAgeBoys();
            weightDataWHO = d.getWeightForAgeBoys();

        } else {
            d.initializeheightForAgeGirls();
            d.initializeweightForAgeGirls();
            heightDataWHO = d.getHeightForAgeGirls();
            weightDataWHO = d.getWeightForAgeGirls();
        }

        // Load the existing values - form.CHECK
        if(formType == FormType.CHECK)
        {
            // set date
            try{
                String prev_date = getDataElement("YB21tVtxZ0z");
                if(!prev_date.isEmpty())
                {
                    textView_Date.setText(prev_date);
                }
            }
            catch (Exception e)
            {
                textView_Date.setText("");
            }

            heightTxt.setText(getDataElement("cDXlUgg1WiZ"));
            weightTxt.setText(getDataElement("rBRI27lvfY5"));

            float currentWeightInt = 0;
            if (weightTxt.getText().toString().isEmpty()) {
                currentWeightInt = 0;
            } else {
                currentWeightInt = Float.parseFloat(weightTxt.getText().toString()) / 1000f;
            }

            String Currentweight = String.valueOf(currentWeightInt);

            ChangeColor(heightTxt, heightTxt.getText().toString(),
                    currentAge, heightDataWHO, true);
            ChangeColor(weightTxt, Currentweight, currentAge, weightDataWHO, false);

            setAgeInWeeks();

        }else{
            textView_Date.setText("Click here to set Date");
        }


        heightTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeColor(heightTxt, s.toString(), currentAge, heightDataWHO, true);
            }
        });

        weightTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                ChangeColor(weightTxt, s.toString(), currentAge, weightDataWHO, false);
            }
        });

        plotGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataElement("cDXlUgg1WiZ", heightTxt.getText().toString()); // save height value
                saveDataElement("rBRI27lvfY5", weightTxt.getText().toString()); // save height value
                weightGraph.removeAllSeries();
                heightGraph.removeAllSeries();
                setupCharts(d);
                fillChart();
            }
        });


    }

    private void setAgeInWeeks()
    {
        AgeInWeeksTxt.setText(String.valueOf(calculateAgeInWeeks()));
    }

    private int calculateAgeInWeeks()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        final int currentAge;
        int currentAge1;

        try{
            System.out.println("Date of birth " + birthday.value());
            System.out.println("Selected date is " + textView_Date.getText().toString());
            Date dob = formatter.parse(birthday.value());
            Date today = formatter.parse(textView_Date.getText().toString());
            long diffInMillies = today.getTime() - dob.getTime();

            currentAge1 = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 10;

            System.out.println("Current age in weeks is " + currentAge1);
        }
        catch (Exception error)
        {
            currentAge1 = 0;
            System.out.print("Error in parsing date field: " + error.toString());
        }

        currentAge = currentAge1;
        return currentAge;
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
        EventFormService.clear();
        setResult(RESULT_OK);
        finish();
    }

    private void setupCharts(DataValuesWHO e) {
        if (sex.equals("Male")) {
            e.initializeheightForAgeBoys();
            e.initializeweightForAgeBoys();

            for (int i = 3; i > -1; i--) {
                heightGraph.addSeries(e.heightForAgeBoysValues(i, 60));
                weightGraph.addSeries(e.weightForAgeBoys(i, 60));
            }

        } else {
            e.initializeheightForAgeGirls();
            e.initializeweightForAgeGirls();

            for (int i = 3; i > -1; i--) {
                heightGraph.addSeries(e.heightForAgeGirlsValues(i, 60));
                weightGraph.addSeries(e.weightForAgeGirlsValues(i, 60));
            }

        }

        heightGraph.getViewport().setBackgroundColor(Color.rgb(215, 31, 226));
        weightGraph.getViewport().setBackgroundColor(Color.rgb(215, 31, 226));

        heightGraph.getViewport().setMaxX(60);
        heightGraph.getViewport().setMaxY(130);

        weightGraph.getViewport().setMaxX(60);
        weightGraph.getViewport().setMaxY(30);

        // don't show anomalies ( might be redundant when zooming is enabled)
        weightGraph.getViewport().setYAxisBoundsManual(true);
        heightGraph.getViewport().setYAxisBoundsManual(true);
        weightGraph.getViewport().setXAxisBoundsManual(true);
        heightGraph.getViewport().setXAxisBoundsManual(true);

        // enable zooming
        weightGraph.getViewport().setScalable(true);
        heightGraph.getViewport().setScalable(true);
        weightGraph.getViewport().setScalableY(true);
        heightGraph.getViewport().setScalableY(true);
    }

    private void ChangeColor(EditText text, String s, int currentAge,
                             Map<Integer, double[]> data, boolean height) {
        currentAge = calculateAgeInWeeks();
        float currentValue;
        if (s.isEmpty()) {
            currentValue = 0;
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
            double[] array = data.get(currentAge);
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

        /*
        switch (category) {
            case -1:
            case 5:
                text.setBackgroundColor(Color.RED);
                break;
            case 0:
            case 4:
                text.setBackgroundColor(Color.rgb(255, 165, 0));
                break;
            case 1:
            case 3:
                text.setBackgroundColor(Color.YELLOW);
                break;
            case 2:
                text.setBackgroundColor(Color.GREEN);
                break;

        }

         */

        switch (category) {
            case -1:
                text.setBackgroundColor(Color.RED);
                break;
            case 0:
                text.setBackgroundColor(Color.rgb(255, 165, 0)); // orange
                break;
            case 1:
                text.setBackgroundColor(Color.YELLOW);
                break;
            case 2:
                text.setBackgroundColor(Color.GREEN);
                break;
            case 3:
                text.setBackgroundColor(Color.rgb(215, 31, 226)); // purple color
                break;

        }

    }

    public void fillChart() {
        List<String> j = new ArrayList<>();
        j.add(selectedChild);

        List<Event> eventRepository = Sdk.d2().eventModule().events()
                .byTrackedEntityInstanceUids(j)
                .blockingGet();

        List<TrackedEntityDataValue> height = new ArrayList<>();

        // Get birthday of the child
        TrackedEntityAttributeValue dob = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(selectedChild)
                .byTrackedEntityAttribute().eq("qNH202ChkV3")
                .one().blockingGet();

        // height graph
        heightGraph.addSeries(getData(eventRepository, "cDXlUgg1WiZ", dob, 1));

        // weight graph
        weightGraph.addSeries(getData(eventRepository, "rBRI27lvfY5", dob, 1000));

    }

    public LineGraphSeries getData(List<Event> eventRepository, String dataElement,
                                   TrackedEntityAttributeValue data, float factor) {
        float[] data_list = new float[60];
        List<TrackedEntityDataValue> event_Array = new ArrayList<>();

        for (int i = 0; i < eventRepository.size(); i++) {
            TrackedEntityDataValue d = Sdk.d2().trackedEntityModule()
                    .trackedEntityDataValues()
                    .byDataElement().eq(dataElement)
                    .byEvent().eq(eventRepository.get(i).uid())
                    .one().blockingGet();
            if (d != null) {
                event_Array.add(d);
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date dob = formatter.parse(data.value());

            for (int i = 0; i < event_Array.size(); i++) {

                long diffInMillies = Math.abs(event_Array.get(i).created().getTime()
                        - dob.getTime());

                int diff = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 10;

                System.out.println("Week number is " + diff);
                data_list[diff] = Float.parseFloat(event_Array.get(i).value());
            }
        } catch (Exception error) {
            System.out.print("Error in parsing date field: " + error.toString());
        }

        LineGraphSeries<DataPoint> line_series = new LineGraphSeries<DataPoint>();

        for (int i = 0; i < 60; i++) {
            line_series.appendData(
                    new DataPoint(i, data_list[i] / factor), true, 60);
        }

        line_series.setColor(Color.BLACK);
        line_series.setThickness(2);
        return line_series;
    }



    private void saveElements()
    {
        if(textView_Date.getText().toString().equals("Click here to set Date")||
                textView_Date.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Date Not Selected");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }

        System.out.println(getDataElement("YB21tVtxZ0z")); // Date
        System.out.println(getDataElement("cDXlUgg1WiZ")); // height
        //System.out.println(getDataElement("SOAtQfInRoy")); // length for age
        //System.out.println(getDataElement("b4Gpl5ayBe3")); // age in months
        System.out.println(getDataElement("rBRI27lvfY5")); // weight
        //System.out.println(getDataElement("bJHCnjX02PN")); // weight for age
        //System.out.println(getDataElement("jnzg5BvOj5T")); // weight for lenght

        saveDataElement("YB21tVtxZ0z", textView_Date.getText().toString());
        saveDataElement("cDXlUgg1WiZ", heightTxt.getText().toString());
        saveDataElement("rBRI27lvfY5", weightTxt.getText().toString());

        finishEnrollment();
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
                    getIntent().getStringExtra(AnthropometryActivity.IntentExtra.OU_UID.name()));
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

    private void selectDate(int year, int month, int day)
    {
        System.out.println("Clicked et date");
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }
}