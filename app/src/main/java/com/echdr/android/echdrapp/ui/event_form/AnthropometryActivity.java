package com.echdr.android.echdrapp.ui.event_form;

import static android.text.TextUtils.isEmpty;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventCollectionRepository;
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
    private FormType formType;
    private GraphView heightGraph;
    private GraphView weightGraph;
    private String selectedChild;
    private String sex;

    private String orgUnit;
    private TextView AgeInWeeksTxt;

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

        EditText heightTxt = findViewById(R.id.anthropometryHeight);
        EditText weightTxt = findViewById(R.id.anthropometryWeight);
        FloatingActionButton saveButton = findViewById(R.id.anthropometrySave);
        heightGraph = findViewById(R.id.heightforageAnthropometry);
        weightGraph = findViewById(R.id.weightforageAnthropometry);
        AgeInWeeksTxt  = findViewById(R.id.ageInWeeks);
        Button plotGraphButton = findViewById(R.id.plotGraph);


        eventUid = getIntent().getStringExtra(AnthropometryActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(AnthropometryActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(AnthropometryActivity.IntentExtra.TEI_ID.name());
        formType = FormType.valueOf(getIntent().getStringExtra(AnthropometryActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(IntentExtra.OU_UID.name());

        System.out.println("Org Unit " + orgUnit);
        engineInitialization = PublishProcessor.create();

        TrackedEntityAttributeValue birthday = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(selectedChild)
                .byTrackedEntityAttribute().eq("qNH202ChkV3")
                .one().blockingGet();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        final int currentAge;
        int currentAge1;
        sex = "Male";
        Date date = new Date();
        try {
            Date dob = formatter.parse(birthday.value());



            long diffInMillies = date.getTime() - dob.getTime();

            currentAge1 = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 10;

            System.out.println("Current age in weeks is " + currentAge1);

        }
        catch (Exception error)
        {
            currentAge1 = 0;
            System.out.print( "Error in parsing date field: " +  error.toString());
        }


        currentAge = currentAge1;
        AgeInWeeksTxt.setText(String.valueOf(currentAge));

        saveButton.setOnClickListener(v -> {
            String heightTextValue = heightTxt.getText().toString();
            String weightTextValue = weightTxt.getText().toString() ;


            saveDataElement("cDXlUgg1WiZ", heightTextValue); // save height value
            saveDataElement("rBRI27lvfY5", weightTextValue); // save weight value
            System.out.println( formatter.format(new Date()).toString());
            saveDataElement("YB21tVtxZ0z", formatter.format(new Date()).toString());
            //String ageString = String.valueOf((int)Math.ceil(currentAge/4));
            saveDataElement("b4Gpl5ayBe3", "");

        });

        dataValuesWHO d =  dataValuesWHO.getInstance();
        Map<Integer, double[]> heightDataWHO;
        Map<Integer, double[]> weightDataWHO;

        setupCharts(d);
        fillChart();

        if(sex.equals("Male"))
        {
            d.initializeweightForAgeBoys();
            d.initializeheightForAgeBoys();
            heightDataWHO = d.getHeightForAgeBoys();
            weightDataWHO = d.getWeightForAgeBoys();

        }else{
            d.initializeheightForAgeGirls();
            d.initializeweightForAgeGirls();
            heightDataWHO = d.getHeightForAgeGirls();
            weightDataWHO = d.getWeightForAgeGirls();
        }

        if(formType == FormType.CHECK)
        {
            heightTxt.setText(getDataElement("cDXlUgg1WiZ"));
            weightTxt.setText(getDataElement("rBRI27lvfY5"));

            float currentWeightInt = 0;
            if(weightTxt.getText().toString().isEmpty()){
                currentWeightInt = 0;
            }else
            {
                currentWeightInt = Float.parseFloat(weightTxt.getText().toString())/1000f;
            }

            String Currentweight = String.valueOf(currentWeightInt);

            ChangeColor(heightTxt, heightTxt.getText().toString(),
                    currentAge, heightDataWHO, true);
            ChangeColor(weightTxt, Currentweight, currentAge, weightDataWHO, false);

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
                saveDataElement("rBRI27lvfY5",  weightTxt.getText().toString()); // save height value
                weightGraph.removeAllSeries();
                heightGraph.removeAllSeries();
                setupCharts(d);
                fillChart();
            }
        });

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
        EventFormService.clear();
        setResult(RESULT_OK);
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

    private void saveDataElement(String dataElement, String value){
        TrackedEntityDataValueObjectRepository valueRepository;
        try {
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                    .value(
                            EventFormService.getInstance().getEventUid(),
                            dataElement
                    );
        }catch (Exception e)
        {
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

        try{
            if(!isEmpty(value))
            {
                valueRepository.blockingSet(value);
            }else
            {
                valueRepository.blockingDeleteIfExist();
            }
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }finally {
            if (!value.equals(currentValue)) {
                engineInitialization.onNext(true);
            }
        }
    }

    private void setupCharts(dataValuesWHO e)
    {
        if(sex.equals("Male"))
        {
            e.initializeheightForAgeBoys();
            e.initializeweightForAgeBoys();

            for(int i=6; i > -1; i--)
            {
                heightGraph.addSeries(e.heightForAgeBoysValues(i, 60));
                weightGraph.addSeries(e.weightForAgeBoys(i, 60));
            }

        }else {
            e.initializeheightForAgeGirls();
            e.initializeweightForAgeGirls();

            for(int i=6; i > -1; i--)
            {
                heightGraph.addSeries(e.heightForAgeGirlsValues(i, 60));
                weightGraph.addSeries(e.weightForAgeGirlsValues(i, 60));
            }

        }

        heightGraph.getViewport().setMaxX(60);
        weightGraph.getViewport().setMaxX(60);
    }

    private void ChangeColor(EditText text, String s, int currentAge,
                             Map<Integer, double[]> data , boolean height)
    {
        float currentValue;
        if(s.isEmpty())
        {
            currentValue = 0;
        }else{
            if(height)
            {
                currentValue = Float.parseFloat(s) ;
            }else
            {
                currentValue = Float.parseFloat(s) / 1000f;
            }
        }

        int category = 0;
        try{
            double [] array = data.get(currentAge);
            for(category=0; category< 7;)
            {

                assert array != null;
                if (array[category] < currentValue)
                {
                    category++;
                }else
                {
                    break;
                }
            }
            category = category-1;
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Suitable Category is " + category);

        switch (category){
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

    }

    public void fillChart()
    {
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
        heightGraph.addSeries(getData(eventRepository, "cDXlUgg1WiZ", dob , 1));

        // weight graph
        weightGraph.addSeries(getData(eventRepository, "rBRI27lvfY5", dob , 1000));

    }



    public LineGraphSeries getData(List<Event> eventRepository, String dataElement,
                                   TrackedEntityAttributeValue data, float factor ){
        float[] data_list = new float[60];
        List<TrackedEntityDataValue> event_Array = new ArrayList<>();

        for(int i =0; i < eventRepository.size();i++)
        {
            TrackedEntityDataValue d = Sdk.d2().trackedEntityModule()
                    .trackedEntityDataValues()
                    .byDataElement().eq(dataElement)
                    .byEvent().eq(eventRepository.get(i).uid())
                    .one().blockingGet();
            if(d != null)
            {
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
        }
        catch (Exception error)
        {
            System.out.print( "Error in parsing date field: " +  error.toString());
        }

        LineGraphSeries<DataPoint> line_series  = new LineGraphSeries<DataPoint>();

        for(int i=0; i< 60; i++)
        {
            line_series.appendData(
                    new DataPoint(i, data_list[i]/factor ), true, 60);
        }

        line_series.setColor(Color.BLACK);
        line_series.setThickness(2);
        return line_series;
    }


}