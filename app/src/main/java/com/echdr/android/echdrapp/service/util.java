package com.echdr.android.echdrapp.service;

import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.ui.event_form.OverweightOutcomeActivity;

import static android.text.TextUtils.isEmpty;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueObjectRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import io.reactivex.processors.PublishProcessor;


public class util {

    public static String getDataTEIElement(String dataElement, String TrackedEntityInstance) {
        TrackedEntityAttributeValueObjectRepository valueRepository =
                Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                        .value(dataElement, TrackedEntityInstance);
        String currentValue = valueRepository.blockingExists() ?
                valueRepository.blockingGet().value() : "";
        return currentValue;
    }

    public static void saveTEIDataElement(String dataElement, String value, String TrackedEntityInstance,
                                          PublishProcessor<Boolean> engineInitialization){
        TrackedEntityAttributeValueObjectRepository valueRepository;
        try {
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                    .value(dataElement, TrackedEntityInstance);
        }catch (Exception e)
        {
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                    .value(TrackedEntityInstance, dataElement);
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

    public static String getDataElement(String dataElement, String eventUid) {
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

    public static void saveDataElement(String dataElement, String value, String eventUid, String programUid,
                                 String oUid, PublishProcessor<Boolean> engineInitialization) {
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
                    oUid);
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

    static class EnrollmentTypeSpinnerClass implements AdapterView.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public static void setSpinner(Context context, Spinner spinner, Object object){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                (Integer) object,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new EnrollmentTypeSpinnerClass());
    }

    public static void setTextView(TextView textView, String dataElement, String eventUid){
        try {
            String element = util.getDataElement(dataElement, eventUid);
            if (!element.isEmpty()) {
                textView.setText(element);
            }
        } catch (Exception e) {
            textView.setText("");
        }
    }

    public static int getSpinnerSelection(String eventUid, String dataElement, String [] array)
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

    public static int getTEISpinnerSelection(String dataElement, String [] array, String teiUid)
    {
        int itemPosition = -1;
        String stringElement = util.getDataTEIElement(dataElement, teiUid);
        for(int i =0; i<array.length; i++)
        {
            if(array[i].equals(stringElement))
            {
                itemPosition = i;
            }
        }
        return itemPosition;
    }

    public static void setTEITextview(TextView textView, String dataElement, String teiUid){
        try {
            String element = util.getDataTEIElement(dataElement, teiUid);
            if (!element.isEmpty()) {
                textView.setText(element);
            }
        } catch (Exception e) {
            textView.setText("");
        }
    }


}
