package com.echdr.android.echdrapp.service.Service;

import android.graphics.Color;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.echdr.android.echdrapp.ui.event_form.DataValuesWHO;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AnthropometryService {

    private static final String TAG = "AnthropometryService";

    public void ChangeColor(EditText text, String s, Map<Integer, double[]> data,
                            boolean height, TextView AgeInWeeksTxt) {
        int currentAge = 0;
        if(!(AgeInWeeksTxt.getText().toString().isEmpty() ||
                AgeInWeeksTxt.getText().toString().equals("Age in weeks"))) {
            currentAge = Integer.parseInt(AgeInWeeksTxt.getText().toString());
        }

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
            // divide by 4 to covert to months
            double[] array = data.get( currentAge/4 );

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


        switch (category) {
            case -1:
                text.setBackgroundColor(Color.parseColor("#a60c0c"));
                break;
            case 0:
                text.setBackgroundColor(Color.parseColor("#F6A21E"));
                break;
            case 1:
                text.setBackgroundColor(Color.parseColor("#afe1bb"));
                break;
            case 2:
                text.setBackgroundColor(Color.parseColor("#a3ccae"));
                break;
            case 3:
                text.setBackgroundColor(Color.parseColor("#f3e5f6"));
                break;
        }
    }

    public void setAgeInWeeks(TrackedEntityAttributeValue birthday,
                              TextView textView_Date, TextView AgeInWeeksTxt, TextView AgeInMonthsTxt)
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

                AgeInWeeksTxt.setText(String.valueOf(diff));

                String doubleAsString = String.valueOf(diffWeeks);
                int indexOfDecimal = doubleAsString.indexOf(".");

                double months = Math.round(Double.valueOf(doubleAsString.substring(indexOfDecimal)) * 12 *  100)/100;

                AgeInMonthsTxt.setText(doubleAsString.substring(0, indexOfDecimal) + " years " + months+  " months");

            } catch (Exception error) {
                Log.e(TAG, String.format("Error parsing the date filed %s", birthday.value()));
            }
    }

}
