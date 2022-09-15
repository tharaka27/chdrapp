package com.echdr.android.echdrapp.service.Validator;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventCollectionRepository;

import java.util.ArrayList;
import java.util.List;

public class EditAccessValidator extends Validator{

    private String TAG = "EditAccessValidator";
    private Context context;
    private String selectedChild;
    private String anthropometryProgram = "hM6Yt9FQL0n";
    private String enrollmentID;

    public void setSelectedChild(String selectedChild) {
        this.selectedChild = selectedChild;
    }

    public void setEnrollmentID(String enrollmentID) {
        this.enrollmentID = enrollmentID;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean validate(){
        super.setContext(context);
        super.setTAG(TAG);

        List<String> j = new ArrayList<>();
        j.add(selectedChild);

        List<Event> events = Sdk.d2().eventModule().events()
                .withTrackedEntityDataValues().byTrackedEntityInstanceUids(j)
                .byProgramUid().eq(anthropometryProgram)
                .byEnrollmentUid().eq(enrollmentID).blockingGet();

        if(events.size() == 0){
            Log.i(TAG, "No anthropometry events are there");
            return true;
        }
        Log.i(TAG, "Anthropometry events are present");
        return false;
    }

}
