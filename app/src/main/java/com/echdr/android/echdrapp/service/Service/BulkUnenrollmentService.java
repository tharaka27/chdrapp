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
import org.hisp.dhis.android.core.event.EventCreateProjection;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.processors.PublishProcessor;

public class BulkUnenrollmentService {
    private static final String TAG = "BulkUnenrollmentService";
    private Context context;
    private String selectedChild;
    private Map<String, List<String>> ProgramList;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setSelectedChild(String selectedChild) {
        this.selectedChild = selectedChild;
    }

    BulkUnenrollmentService(){
        ProgramList = new HashMap<String, List<String>>();

        //program , programStage, date , outcome
        ProgramList.put("hM6Yt9FQL0n",  Arrays.asList("pI5JAmTcjE4"));
        ProgramList.put("JsfNVX0hdq9",  Arrays.asList("ctwLm9rn8gr","E5rWDjnuN6M","x9iPS2RiOKO"));
        ProgramList.put("lSSNwBMiwrK",  Arrays.asList("L4MJKSCcUof","SgPDQhOWB7f", "BIeXJ9fThq6"));
        ProgramList.put("tc6RsYbgGzm",  Arrays.asList("QKsx9TfOJ3m","lSy6bC6y3UD", "rTHNOXde3wr"));
        ProgramList.put("CoGsKgEG4O0",  Arrays.asList("RtC4CcoEs4J","HXin8cvKgVq", "xi20olIPIsb"));
    }

    public void unenroll(String programID, String orgID, String date, String reasonForUnenrollment,
                         PublishProcessor<Boolean> engineInitialization){

        for (String program: ProgramList.keySet()) {
            if(!program.equals(programID)){

                if(program.equals("hM6Yt9FQL0n")){ // anthropometry program does not have reason for un-enrollment
                    unenrollFromProgram(program, ProgramList.get(program).get(0), orgID, null, engineInitialization);
                    continue;
                }
                List<String> programData = ProgramList.get(program);
                Map<String, String > dataElements = new HashMap<>();
                dataElements.put(ProgramList.get(program).get(1), date);
                dataElements.put(ProgramList.get(program).get(2), reasonForUnenrollment);
                unenrollFromProgram(program, programData.get(0), orgID,
                        dataElements, engineInitialization);
            }
        }
    }

    public void unenrollFromProgram(String programID, String stageSelected, String orgID,
                                    Map<String, String > dataElements, PublishProcessor<Boolean> engineInitialization){

        String enrollmentID = isEnrolled(programID);

        if(!enrollmentID.isEmpty()) {
            try {
                String eventID  = createOutcomeEvent(enrollmentID, programID, stageSelected );
                unenrollFromProgram(programID,enrollmentID, eventID, dataElements, orgID, engineInitialization);
                Log.i(TAG, String.format("Unenrolled from program: {}, stage: {}", programID, stageSelected ));
            } catch (D2Error d2Error) {
                d2Error.printStackTrace();
                Log.i(TAG, String.format("Unenrollment unsuccessful from program: {}, stage: {}", programID, stageSelected ));
            }
        }

    }

    private String isEnrolled(String programID){

        // get latest enrollment
        List<Enrollment> enrollmentStatus = Sdk.d2().enrollmentModule().enrollments()
                .byTrackedEntityInstance().eq(selectedChild)
                .byProgram().eq(programID)
                .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        String enrollmentID = "";
        if(!enrollmentStatus.isEmpty())
        {
            if ( enrollmentStatus.get(0).status().equals(EnrollmentStatus.ACTIVE)) {
                enrollmentID = enrollmentStatus.get(0).uid();
            }
        }

        return enrollmentID;
    }


    private String createOutcomeEvent(String enrollmentID, String selectedProgram, String stageSelected) throws D2Error {
        Program program = Sdk.d2().programModule().programs().uid(selectedProgram).blockingGet();
        String orgUnit = Sdk.d2().organisationUnitModule().organisationUnits()
                                    .byProgramUids(Collections.singletonList(selectedProgram))
                                    .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                                    .one().blockingGet().uid();

        String attrOptionCombo = program.categoryCombo() != null ?
                Sdk.d2().categoryModule().categoryOptionCombos().byCategoryComboUid().eq(program.categoryComboUid())
                        .one().blockingGet().uid() : null;
        return Sdk.d2().eventModule().events().blockingAdd(
                EventCreateProjection.builder().organisationUnit(orgUnit).program(program.uid()).enrollment(enrollmentID)
                        .programStage(stageSelected).attributeOptionCombo(attrOptionCombo).build());
    }


    private void unenrollFromProgram(String programID,  String enrollmentID, String eventID,
                                    Map<String, String> dataElements, String orgID,
                                    PublishProcessor<Boolean> engineInitialization){

        EnrollmentObjectRepository repository = Sdk.d2().enrollmentModule().enrollments()
                .uid(enrollmentID);

        try {
            repository.setStatus(EnrollmentStatus.COMPLETED);
            if(dataElements != null){
                for (String key: dataElements.keySet()) {
                    util.saveDataElement(key, dataElements.get(key), eventID,
                            programID, orgID, engineInitialization );
                }
            }
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
            Toast.makeText(context, "Un-enrolling unsuccessful ",
                    Toast.LENGTH_LONG).show();
        }
    }

}
