package com.echdr.android.echdrapp.service.Service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.service.util;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.List;
import java.util.Map;

import io.reactivex.processors.PublishProcessor;

public class BulkUnenrollmentService {
    private static final String TAG = "BulkUnenrollmentService";
    private Context context;

    public void unenroll(String selectedChild, String programID,
                         Map<String, String> dataElements, String orgID,
                         PublishProcessor<Boolean> engineInitialization){
        //TODO first check if the child is enrolled in the program

        //TODO if enrolled create a event using eventCreationModule and get a eventID

        //TODO then unenroll from the event module

    }

    private void unenrollFromProgram(String selectedChild, String programID,
                                    Map<String, String> dataElements, String orgID,
                                    PublishProcessor<Boolean> engineInitialization){

        // get latest enrollment
        List<Enrollment> enrollmentStatus = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(selectedChild)
                .byProgram().eq(programID)
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        String enrollmentID = "";

        if(!enrollmentStatus.isEmpty())
        {
            enrollmentID = enrollmentStatus.get(0).uid();
        }

        EnrollmentObjectRepository rep = Sdk.d2().enrollmentModule().enrollments()
                .uid(enrollmentID);

        try {
            rep.setStatus(EnrollmentStatus.COMPLETED);
            //for (String key: dataElements.keySet()) {
            //    util.saveDataElement(key, dataElements.get(key), eventID,
            //            programID, orgID, engineInitialization );
            //}

        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
            Toast.makeText(context, "Un-enrolling unsuccessful",
                    Toast.LENGTH_LONG).show();
        }
    }

}
