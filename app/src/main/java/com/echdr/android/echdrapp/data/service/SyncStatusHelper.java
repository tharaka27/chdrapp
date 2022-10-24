package com.echdr.android.echdrapp.data.service;

import com.echdr.android.echdrapp.data.Sdk;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SyncStatusHelper {

    public static int programCount() {
        return Sdk.d2().programModule().programs().blockingCount();
    }

    public static int dataSetCount() {
        return Sdk.d2().dataSetModule().dataSets().blockingCount();
    }

    public static int trackedEntityInstanceCount() {
        return Sdk.d2().trackedEntityModule().trackedEntityInstances()
                .byState().neq(State.RELATIONSHIP).blockingCount();
    }

    public static int singleEventCount() {
        return Sdk.d2().eventModule().events().byEnrollmentUid().isNull().blockingCount();
    }

    public static int dataValueCount() {
        return Sdk.d2().dataValueModule().dataValues().blockingCount();
    }



    public static boolean isThereDataToUpload() {
        return Sdk.d2().trackedEntityModule().trackedEntityInstances().byState()
                .notIn(Collections.singletonList(State.SYNCED)).blockingCount() > 0 ||
                Sdk.d2().dataValueModule().dataValues().byState()
                        .notIn(Collections.singletonList(State.SYNCED)).blockingCount() > 0;
    }

    public static int therapeuticalCount(){
        List<String> l = new ArrayList<>();
        l.add("CoGsKgEG4O0");

        return Sdk.d2().enrollmentModule().enrollments().byProgram().eq("CoGsKgEG4O0")
                .byStatus().eq(EnrollmentStatus.ACTIVE).blockingCount();
    }

    public static int supplementaryCount(){
        List<String> l = new ArrayList<>();
        l.add("tc6RsYbgGzm");

        return Sdk.d2().enrollmentModule().enrollments().byProgram().eq("tc6RsYbgGzm")
                .byStatus().eq(EnrollmentStatus.ACTIVE).blockingCount();
    }

    public static int stuntingCount(){
        List<String> l = new ArrayList<>();
        l.add("lSSNwBMiwrK");

        return Sdk.d2().enrollmentModule().enrollments().byProgram().eq("lSSNwBMiwrK")
                .byStatus().eq(EnrollmentStatus.ACTIVE).blockingCount();
    }

    public static int overweightCount(){
        List<String> l = new ArrayList<>();
        l.add("JsfNVX0hdq9");

        return Sdk.d2().enrollmentModule().enrollments().byProgram().eq("JsfNVX0hdq9")
                .byStatus().eq(EnrollmentStatus.ACTIVE).blockingCount();
    }

    public static int otherCount(){
        List<String> l = new ArrayList<>();
        l.add("iUgzznPsePB");

        return Sdk.d2().enrollmentModule().enrollments().byProgram().eq("iUgzznPsePB")
                .byStatus().eq(EnrollmentStatus.ACTIVE).blockingCount();
    }


}
