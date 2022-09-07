package com.echdr.android.echdrapp.service.Service;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.service.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import io.reactivex.processors.PublishProcessor;

public class UnenrollmentService {
    private static final String TAG = "UnenrollmentService";
    private static Context context;
    private static String selectedChild;
    private static boolean successfulUnenrollment = false;
    private static BulkUnenrollmentService bulkUnenrollmentService = new BulkUnenrollmentService();

    public static void setContext(Context context) {
        UnenrollmentService.context = context;
    }

    public static void setSelectedChild(String selectedChild) {
        UnenrollmentService.selectedChild = selectedChild;
    }

    public static boolean isSuccessfulUnenrollment() {
        return successfulUnenrollment;
    }

    public static void unenroll(String date, String reasonForUnenrollment, String message, Map<String, String> dataElements,
                                String programID, String eventID, String orgID,
                                PublishProcessor<Boolean> engineInitialization, Callable function){

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.baby_girl);
        builderSingle.setTitle(context.getString(R.string.unenroll));
        builderSingle.setMessage(message);

        builderSingle.setNegativeButton("un-enroll", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
                    for (String key: dataElements.keySet()) {
                        util.saveDataElement(key, dataElements.get(key), eventID,
                                programID, orgID, engineInitialization );
                    }

                    // bulk un-enroll if the Left the area, left due to completion of age five, Died
                    if(reasonForUnenrollment.equals("Left the area")||
                            reasonForUnenrollment.equals("Left due to completion of age 5")||
                            reasonForUnenrollment.equals("Died")){
                        bulkUnenrollmentService.unenroll(programID, orgID, date, reasonForUnenrollment, engineInitialization);
                    }
                    function.call();

                } catch (D2Error d2Error) {
                    d2Error.printStackTrace();
                    Toast.makeText(context, "Un-enrolling unsuccessful",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e( TAG, "Function call failure");
                }

                dialog.dismiss();
                return;
            }
        });

        builderSingle.show();
    }
}
