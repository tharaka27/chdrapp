package com.echdr.android.echdrapp.service.Service;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.service.Validator.ProgramEnrollmentValidator;
import com.echdr.android.echdrapp.ui.enrollment_form.EnrollmentFormActivity;
import com.echdr.android.echdrapp.ui.events.EventsActivity;
import com.echdr.android.echdrapp.ui.tracked_entity_instances.ChildDetailsActivityNew;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

public class ProgramEnrollmentService {
    private final String TAG = "ProgramEnrollmentService";
    private  String selectedChild;
    private  String orgUnit;
    private  Context context;

    private  ImageView overweightNotEnrolled;
    private  ImageView overweightEnrolled;
    private  ImageView antopoNotEnrolled;
    private  ImageView antopoEnrolled;
    private  ImageView otherHealthNotEnrolled;
    private  ImageView otherHealthEnrolled;
    private  ImageView stuntingNotEnrolled;
    private  ImageView stuntingEnrolled;
    private  ImageView supplementaryNotEnrolled;
    private  ImageView supplementaryEnrolled;
    private  ImageView therapeuticNotEnrolled;
    private  ImageView therapeuticEnrolled;

    private  String overweightEnrollmentID;
    private  String anthropometryEnrollmentID;
    private  String otherEnrollmentID;
    private  String stuntingEnrollmentID;
    private  String supplementaryEnrollmentID;
    private  String therapeuticEnrollmentID;

    private  ProgramEnrollmentValidator programEnrollmentValidator;

    public ProgramEnrollmentService(String selectedChild, String orgUnit, Context context,
                                    ImageView overweightNotEnrolled, ImageView overweightEnrolled,
                                    ImageView antopoNotEnrolled, ImageView antopoEnrolled,
                                    ImageView otherHealthNotEnrolled, ImageView otherHealthEnrolled,
                                    ImageView stuntingNotEnrolled, ImageView stuntingEnrolled,
                                    ImageView supplementaryNotEnrolled, ImageView supplementaryEnrolled,
                                    ImageView therapeuticNotEnrolled, ImageView therapeuticEnrolled,
                                    String overweightEnrollmentID, String anthropometryEnrollmentID,
                                    String otherEnrollmentID, String stuntingEnrollmentID,
                                    String supplementaryEnrollmentID, String therapeuticEnrollmentID,
                                    ProgramEnrollmentValidator programEnrollmentValidator) {
        this.selectedChild = selectedChild;
        this.orgUnit = orgUnit;
        this.context = context;
        this.overweightNotEnrolled = overweightNotEnrolled;
        this.overweightEnrolled = overweightEnrolled;
        this.antopoNotEnrolled = antopoNotEnrolled;
        this.antopoEnrolled = antopoEnrolled;
        this.otherHealthNotEnrolled = otherHealthNotEnrolled;
        this.otherHealthEnrolled = otherHealthEnrolled;
        this.stuntingNotEnrolled = stuntingNotEnrolled;
        this.stuntingEnrolled = stuntingEnrolled;
        this.supplementaryNotEnrolled = supplementaryNotEnrolled;
        this.supplementaryEnrolled = supplementaryEnrolled;
        this.therapeuticNotEnrolled = therapeuticNotEnrolled;
        this.therapeuticEnrolled = therapeuticEnrolled;
        this.overweightEnrollmentID = overweightEnrollmentID;
        this.anthropometryEnrollmentID = anthropometryEnrollmentID;
        this.otherEnrollmentID = otherEnrollmentID;
        this.stuntingEnrollmentID = stuntingEnrollmentID;
        this.supplementaryEnrollmentID = supplementaryEnrollmentID;
        this.therapeuticEnrollmentID = therapeuticEnrollmentID;
        this.programEnrollmentValidator = programEnrollmentValidator;
    }

    public void EnrollToPrograms(){

        List<TrackedEntityInstance> s = Sdk.d2().trackedEntityModule()
                .trackedEntityInstances().byUid().eq(selectedChild).blockingGet();

        String orgUnit1 = Sdk.d2().organisationUnitModule().organisationUnits()
                .byProgramUids(Collections.singletonList("hM6Yt9FQL0n"))
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .one().blockingGet().uid();
        orgUnit = orgUnit1;

        overweightNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!programEnrollmentValidator.validate("OVERWEIGHT")){
                    return ;
                }
                CreateConfirmationDialog(context.getString(R.string.confirm_overweight), "JsfNVX0hdq9");
            }
        });

        overweightEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartEventActivity("JsfNVX0hdq9", overweightEnrollmentID);
            }
        });

        antopoNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the latest enrollment

                List<Enrollment> AnthropometryStatus = Sdk.d2().enrollmentModule().enrollments()
                        .byTrackedEntityInstance().eq(selectedChild)
                        .byProgram().eq("hM6Yt9FQL0n")
                        .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                        .blockingGet();

                String anthropometryEnrollmentID = "";

                // The child should have at least one enrollment
                if(!AnthropometryStatus.isEmpty())
                {
                    anthropometryEnrollmentID = AnthropometryStatus.get(0).uid();
                }
                else
                {
                    return;
                }

                // set the enrollment status to active based on the enrollment ID
                EnrollmentObjectRepository rep = Sdk.d2().enrollmentModule().enrollments()
                        .uid(anthropometryEnrollmentID);
                try {
                    rep.setStatus(EnrollmentStatus.ACTIVE);
                } catch (D2Error d2Error) {
                    d2Error.printStackTrace();
                    Toast.makeText(context, "re-enrolling unsuccessful",
                            Toast.LENGTH_LONG).show();
                }

                StartEventActivity("hM6Yt9FQL0n", anthropometryEnrollmentID);

            }
        });

        antopoEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartEventActivity("hM6Yt9FQL0n", anthropometryEnrollmentID);
            }
        });

        otherHealthNotEnrolled.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CreateConfirmationDialog(
                        context.getString(R.string.confirm_other),
                        "iUgzznPsePB");
            }
        });

        otherHealthEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartEventActivity("iUgzznPsePB", otherEnrollmentID);
            }
        });

        stuntingNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateConfirmationDialog(context.getString(R.string.confirm_stunting), "lSSNwBMiwrK");
            }
        });

        stuntingEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartEventActivity("lSSNwBMiwrK", stuntingEnrollmentID);

            }
        });

        supplementaryNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!programEnrollmentValidator.validate("SUPPLEMENTARY")){
                    return ;
                }
                CreateConfirmationDialog(context.getString(R.string.confirm_supplementary), "tc6RsYbgGzm");
            }
        });

        supplementaryEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartEventActivity("tc6RsYbgGzm", supplementaryEnrollmentID);
            }
        });

        therapeuticNotEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!programEnrollmentValidator.validate("THERAPEUTICAL")){
                    return ;
                }

                CreateConfirmationDialog(context.getString(R.string.confirm_therapeutical), "CoGsKgEG4O0");
            }
        });

        therapeuticEnrolled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartEventActivity("CoGsKgEG4O0", therapeuticEnrollmentID);

            }
        });

    }

    protected void StartEventActivity(String programID, String enrollmentID){
        Intent intent = EventsActivity.getIntent(context, programID,
                selectedChild, enrollmentID);
        ((Activity) context).startActivity(intent);
        ((Activity) context).finish();
    }

    protected void CreateConfirmationDialog(String confirmationMessage, String programID){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(confirmationMessage);
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = EnrollmentFormActivity.getFormActivityIntent(context,
                        selectedChild, programID, orgUnit);
                ((Activity) context).startActivity(intent);
                ((Activity) context).finish();
            }
        });

        builder.setNegativeButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //return;
                    }
                });

        AlertDialog alert12 = builder.create();
        alert12.show();
        return;
    }

}
