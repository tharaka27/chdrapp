package com.echdr.android.echdrapp.service.Setter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateSetter {
    private static TextView textView;
    private static ImageView imageView;
    private static TrackedEntityAttributeValue birthday;
    private static Context context;
    private static DatePickerDialog.OnDateSetListener setListener;

    public static void setImageView(ImageView imageView) {
        DateSetter.imageView = imageView;
    }
    public static void setTextView(TextView textView) {
        DateSetter.textView = textView;
    }

    public static void setBirthday(TrackedEntityAttributeValue birthday) {
        DateSetter.birthday = birthday;
    }

    public static void setContext(Context context) {
        DateSetter.context = context;
    }

    public static void setSetListener(DatePickerDialog.OnDateSetListener setListener) {
        DateSetter.setListener = setListener;
    }

    public static void setDate(int year, int month, int day, int maxDuration){
        setDate(textView, year, month, day, maxDuration);
        setDate(imageView, year, month, day, maxDuration);
        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth) ;
                textView.setText(date);
            }
        };
    }


    private static void setDate(View view, int year, int month, int day, int maxDuration){
        view.setOnClickListener(new View.OnClickListener() {
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
                    c.add(Calendar.DATE, maxDuration);
                    long minimum_value = Math.min(c.getTimeInMillis(), System.currentTimeMillis());

                    datePickerDialog.getDatePicker().setMaxDate(minimum_value);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                datePickerDialog.show();
            }
        });
    }
}
