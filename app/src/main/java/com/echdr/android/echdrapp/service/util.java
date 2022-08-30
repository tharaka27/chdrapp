package com.echdr.android.echdrapp.service;

import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.echdr.android.echdrapp.ui.event_form.AnthropometryActivity;

import static android.text.TextUtils.isEmpty;

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


}
