package com.echdr.android.echdrapp.service.Service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.service.Setter.AnthropometryMissingHeightSetter;
import com.echdr.android.echdrapp.ui.event_form.DataValuesWHO;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Setter;

public class AnthropometryChartService {
    private static final String TAG = "AnthropometryChartService";
    private GraphView heightGraph;
    private GraphView weightGraph;
    private GraphView weightHeightGraph;
    private String sex;
    private Context context;
    private String selectedChild;
    private TrackedEntityAttributeValue birthday;
    private Map<Integer, Integer> heightValues;
    private Map<Integer, Integer> weightValues;

    AnthropometryMissingHeightSetter anthropometryMissingHeightSetter;

    public void setHeightGraph(GraphView heightGraph) {
        this.heightGraph = heightGraph;
    }

    public void setWeightGraph(GraphView weightGraph) {
        this.weightGraph = weightGraph;
    }

    public void setWeightHeightGraph(GraphView weightHeightGraph) {
        this.weightHeightGraph = weightHeightGraph;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setSelectedChild(String selectedChild) {
        this.selectedChild = selectedChild;
    }

    public void setBirthday(TrackedEntityAttributeValue birthday) {
        this.birthday = birthday;
    }

    public void setHeightValues(Map<Integer, Integer> heightValues) {
        this.heightValues = heightValues;
    }

    public void setWeightValues(Map<Integer, Integer> weightValues) {
        this.weightValues = weightValues;
    }

    public AnthropometryChartService(){
        anthropometryMissingHeightSetter = new AnthropometryMissingHeightSetter();
    }

    public void plotGraph()
    {
        //setup charts and background
        DataValuesWHO d = DataValuesWHO.getInstance();

        if (sex.equals("Male")) {
            for (int i = 3; i > -1; i--) {
                heightGraph.addSeries(d.heightForAgeBoysValues(i, 60));
                weightGraph.addSeries(d.weightForAgeBoys(i, 60));
                weightHeightGraph.addSeries(d.weightForHeightBoys(i, 60));
            }

        } else {
            for (int i = 3; i > -1; i--) {
                heightGraph.addSeries(d.heightForAgeGirlsValues(i, 60));
                weightGraph.addSeries(d.weightForAgeGirlsValues(i, 60));
                weightHeightGraph.addSeries(d.weightForHeightGirls(i, 60));
            }

        }

        heightGraph.getViewport().setBackgroundColor(Color.parseColor("#f3e5f6"));
        weightGraph.getViewport().setBackgroundColor(Color.parseColor("#f3e5f6"));
        weightHeightGraph.getViewport().setBackgroundColor(Color.parseColor("#f3e5f6"));

        heightGraph.getViewport().setMaxX(60);
        heightGraph.getViewport().setMaxY(130);

        weightGraph.getViewport().setMaxX(60);
        weightGraph.getViewport().setMaxY(30);

        weightHeightGraph.getViewport().setMaxX(120);
        weightHeightGraph.getViewport().setMaxY(32);

        // don't show anomalies ( might be redundant when zooming is enabled)
        weightGraph.getViewport().setYAxisBoundsManual(true);
        heightGraph.getViewport().setYAxisBoundsManual(true);
        weightGraph.getViewport().setXAxisBoundsManual(true);
        heightGraph.getViewport().setXAxisBoundsManual(true);
        weightHeightGraph.getViewport().setXAxisBoundsManual(true);
        weightHeightGraph.getViewport().setYAxisBoundsManual(true);

        weightGraph.getGridLabelRenderer().setHorizontalAxisTitle(context.getString(R.string.month));
        weightGraph.getGridLabelRenderer().setVerticalAxisTitle(context.getString(R.string.weight));

        heightGraph.getGridLabelRenderer().setHorizontalAxisTitle(context.getString(R.string.month));
        heightGraph.getGridLabelRenderer().setVerticalAxisTitle(context.getString(R.string.height));

        weightHeightGraph.getGridLabelRenderer().setHorizontalAxisTitle(context.getString(R.string.height));
        weightHeightGraph.getGridLabelRenderer().setVerticalAxisTitle(context.getString(R.string.weight));

        // enable zooming
        weightGraph.getViewport().setScalable(true);
        heightGraph.getViewport().setScalable(true);
        weightGraph.getViewport().setScalableY(true);
        heightGraph.getViewport().setScalableY(true);
        weightHeightGraph.getViewport().setScalable(true);
        weightHeightGraph.getViewport().setScalableY(true);

        heightValues = new HashMap<>();
        weightValues = new HashMap<>();

    }

    public void plotDataElements()
    {
        List<String> j = new ArrayList<>();
        j.add(selectedChild);

        // get all anthropometry data of the selected child
        List<Event> eventRepository = Sdk.d2().eventModule().events()
                .byTrackedEntityInstanceUids(j)
                .byProgramUid().eq("hM6Yt9FQL0n")
                .blockingGet();

        for(int i=0; i < eventRepository.size(); i++)
        {
            prepareDataPoints(
                    getDataElementFromEvent(
                            "YB21tVtxZ0z", eventRepository.get(i).uid()),
                    getDataElementFromEvent(
                            "cDXlUgg1WiZ", eventRepository.get(i).uid()),
                    getDataElementFromEvent(
                            "rBRI27lvfY5", eventRepository.get(i).uid()));
        }

    }
    public String getDataElementFromEvent(String dataElement, String captureEvent)
    {
        TrackedEntityDataValueObjectRepository valueRepository =
                Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                        .value(
                                captureEvent,
                                dataElement
                        );

        String currentValue = valueRepository.blockingExists() ?
                valueRepository.blockingGet().value() : "";

        return currentValue;
    }

    @SuppressLint("LongLogTag")
    private void prepareDataPoints(String date, String height, String weight)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Parse event date
            Date dob = formatter.parse(birthday.value());
            Date eventDate = formatter.parse(date);

            // Calculate age in months
            long diffInMillies = Math.abs(eventDate.getTime() - dob.getTime());
            int diff = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 30;

            // Enter to the data values
            if(height.isEmpty() || height == ""){
                // if fall in to same week
                if(heightValues.containsKey(diff) && heightValues.get(diff) != 0){
                }else{
                    heightValues.put(diff, 0);
                }
            }else{
                heightValues.put(diff, Integer.parseInt(height));
            }
            weightValues.put(diff, Integer.parseInt(weight));

        } catch (ParseException error) {
            Log.e(TAG, String.format("Birthday : %s and event day : %s", birthday.value(), date));
        } catch (Exception error){
            Log.e(TAG, error.toString());
        }
    }

    @SuppressLint("LongLogTag")
    public void drawLineGraph()
    {

        LineGraphSeries<DataPoint> height_series = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> weight_series = new LineGraphSeries<DataPoint>();
        LineGraphSeries<DataPoint> weight_for_height_series = new LineGraphSeries<DataPoint>();

        int birthWeight = Integer.parseInt(getValueListener("Fs89NLB2FrA"));
        int birthHeight = Integer.parseInt(getValueListener("LpvdWM4YuRq"));

        height_series.appendData(
                new DataPoint(0,birthHeight), true, 61
        );

        Log.i(TAG, String.format("Birth Height is %s", String.valueOf(birthHeight)));

        weight_series.appendData(
                new DataPoint(0,birthWeight/1000), true, 61
        );
        weight_for_height_series.appendData(
                new DataPoint(birthHeight, birthWeight/1000) , true, 61
        );

        int heightLastFilledElement = 0;
        for(int i=0; i< 60; i++)
        {
            if(heightValues.containsKey(i) && !heightValues.get(i).equals(0))
            {
                heightLastFilledElement = i;
                height_series.appendData(
                        new DataPoint(i, heightValues.get(i)), true, 61);
            }else{
                // fill for missing height setter
                heightValues.put(i, 0);
            }
        }

        // Fill the missing values
        int heightLastFilledElementWeight = 0;
        for(int i=0; i< 60; i++)
        {
            if(weightValues.containsKey(i))
            {
                weight_series.appendData(
                        new DataPoint(i, weightValues.get(i)/1000f), true, 61);
            } else {
                // fill for missing height setter
                weightValues.put(i, 0);
            }
        }

        heightValues.put(0, birthHeight);
        weightValues.put(0, birthWeight);

        Log.i(TAG, String.format("Before Height values %s", heightValues.toString()));
        Log.i(TAG, String.format("Before Weight values %s", weightValues.toString()));
        Log.i(TAG, String.format("Before Last filled element is %d", heightLastFilledElement));

        try {
            anthropometryMissingHeightSetter.setDataElements(heightValues);
            anthropometryMissingHeightSetter.setMissingElements();
            heightValues = anthropometryMissingHeightSetter.getDataElements();
        }catch (IllegalArgumentException e)
        {
            Log.e(TAG, e.toString());
        }

        try {
            anthropometryMissingHeightSetter.setDataElements(weightValues);
            anthropometryMissingHeightSetter.setMissingElements();
            weightValues = anthropometryMissingHeightSetter.getDataElements();
        } catch (IllegalArgumentException e)
        {
            Log.e(TAG, e.toString());
        }

        Log.i(TAG, String.format("After Height values %s", heightValues.toString()));
        Log.i(TAG, String.format("After Weight values %s", weightValues.toString()));
        Log.i(TAG, String.format("After Last filled element is %d", heightLastFilledElement));

        // fill only till the last filled height value
        for(int i=0; i<= heightLastFilledElement; i++)
        {
            if(heightValues.containsKey(i))
            {
                try {
                    weight_for_height_series.appendData(
                            new DataPoint( heightValues.get(i),
                                        weightValues.get(i) / 1000f),
                                 true, 61);
                } catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                    Toast t = Toast.makeText(context,
                            "Height values should be in increasing order", Toast.LENGTH_LONG);
                    t.show();
                }
            }
        }


        height_series.setColor(Color.BLACK);
        height_series.setThickness(5);
        weight_series.setColor(Color.BLACK);
        weight_series.setThickness(5);
        weight_for_height_series.setThickness(5);
        weight_for_height_series.setColor(Color.BLACK);

        heightGraph.addSeries(height_series);
        weightGraph.addSeries(weight_series);
        weightHeightGraph.addSeries(weight_for_height_series);

    }

    private String getValueListener(String dataElement) {

        String currentValue = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(selectedChild)
                .byTrackedEntityAttribute().eq(dataElement)
                .one().blockingGet().value();

        return currentValue;
    }
}
