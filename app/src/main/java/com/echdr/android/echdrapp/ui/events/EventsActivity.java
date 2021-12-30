package com.echdr.android.echdrapp.ui.events;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.ActivityStarter;
import com.echdr.android.echdrapp.ui.base.ListActivity;
import com.echdr.android.echdrapp.ui.event_form.AnthropometryActivity;
import com.echdr.android.echdrapp.ui.event_form.AnthropometryActivityNew;
import com.echdr.android.echdrapp.ui.event_form.EventFormActivity;
import com.echdr.android.echdrapp.ui.event_form.OtherEvaluationActivity;
import com.echdr.android.echdrapp.ui.event_form.OtherReasonForActivity;
import com.echdr.android.echdrapp.ui.event_form.OtherReferredForInterventionActivity;
import com.echdr.android.echdrapp.ui.event_form.OverweightIntervensionActivity;
import com.echdr.android.echdrapp.ui.event_form.OverweightManagementActivity;
import com.echdr.android.echdrapp.ui.event_form.OverweightOutcomeActivity;
import com.echdr.android.echdrapp.ui.event_form.StuntingInterventionActivity;
import com.echdr.android.echdrapp.ui.event_form.StuntingManagementActivity;
import com.echdr.android.echdrapp.ui.event_form.StuntingOutcomeActivity;
import com.echdr.android.echdrapp.ui.event_form.SupplementaryIndicationActivity;
import com.echdr.android.echdrapp.ui.event_form.SupplementaryInterventionActivity;
import com.echdr.android.echdrapp.ui.event_form.SupplementaryOutcomeActivity;
import com.echdr.android.echdrapp.ui.event_form.TherapeuticInterventionActivity;
import com.echdr.android.echdrapp.ui.event_form.TherapeuticManagementActivity;
import com.echdr.android.echdrapp.ui.event_form.TherapeuticOutcomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventCollectionRepository;
import org.hisp.dhis.android.core.event.EventCreateProjection;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.ProgramStage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.text.TextUtils.isEmpty;

public class EventsActivity extends ListActivity {
    private String selectedProgram;
    private String selectedChild;
    private CompositeDisposable compositeDisposable;
    private EventAdapter adapter;
    private final int EVENT_RQ = 1210;
    private Context context;
    private String stageSelected;
    private FloatingActionButton anthUnenroll;
    private String programEnrollmentID;

    private enum IntentExtra {
        PROGRAM, TEI_ID, ENROLLMENT_ID
    }


    /**
     * This method can be used with startActivity() to start the intent.
     * @param context context of the calling intent
     * @param programUid The program ID from DHIS2 side
     * @param teiUid Tracked Entity Instance ID of the child
     * @param enrollmentID Enrollment ID of the particular TEI and program
     * @return intent
     *
     */
    public static Intent getIntent(Context context, String programUid, String teiUid, String enrollmentID) {
        Bundle bundle = new Bundle();
        if (!isEmpty(programUid))
            bundle.putString(IntentExtra.PROGRAM.name(), programUid);
        if(!isEmpty(teiUid))
            bundle.putString(IntentExtra.TEI_ID.name(), teiUid);
        if(!isEmpty(enrollmentID))
            bundle.putString(IntentExtra.ENROLLMENT_ID.name(), enrollmentID);
        Intent intent = new Intent(context, EventsActivity.class);
        intent.putExtras(bundle);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_events_new, R.id.eventsRecyclerView);

        // Extract string from the passed intent parameters
        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        selectedChild = getIntent().getStringExtra(IntentExtra.TEI_ID.name());
        programEnrollmentID = getIntent().getStringExtra(IntentExtra.ENROLLMENT_ID.name());

        // Composite Disposable instance is required for creating new events in the
        // background
        compositeDisposable = new CompositeDisposable();

        // populate the list view
        observeEvents();

        context = this;

        // All the programs except the Anthropometry has a outcome section. Hence program
        // un-enrollment happens in the outcome section. For the anthropometry program we
        // have created a un-enroll button in the event list activity.
        // This button is only visible for the Anthropometry program
        anthUnenroll = findViewById(R.id.unenrollAnthropometry);
        if (selectedProgram.equals("hM6Yt9FQL0n")) {
            anthUnenroll.setVisibility(View.VISIBLE);
            anthUnenroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                    builderSingle.setIcon(R.drawable.baby_girl);
                    builderSingle.setTitle("Are you sure to un-enroll from Anthropometry Program");

                    builderSingle.setNegativeButton("un-enroll", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // ToDo: Remove following enrollment ID section
                            //  pass the value from the previous intent.

                            // get anthropometry latest enrollment (descending order)
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

                            // set the enrollment status based on the enrollment ID
                            EnrollmentObjectRepository rep = Sdk.d2().enrollmentModule().enrollments()
                                    .uid(anthropometryEnrollmentID);
                            try {
                                rep.setStatus(EnrollmentStatus.COMPLETED);
                            } catch (D2Error d2Error) {
                                d2Error.printStackTrace();
                                Toast.makeText(context, "Un-enrolling unsuccessful",
                                        Toast.LENGTH_LONG).show();
                            }

                            dialog.dismiss();

                            // once the enrollment is completed close the activity.
                            finish();
                            return;
                        }
                    });

                    builderSingle.show();

                }
            });
        }

        // as a backup remove add new event button if program is not selected properly
        if (isEmpty(selectedProgram))
            findViewById(R.id.eventButton).setVisibility(View.GONE);

        findViewById(R.id.eventButton).setOnClickListener(view ->
                {
                    // first create a alert dialog box to select program stage
                    List<ProgramStage> stages = Sdk.d2().programModule().programStages()
                            .byProgramUid().eq(selectedProgram)
                            .blockingGet();

                    ArrayAdapter<String> stages_names = new ArrayAdapter<String>
                            (this, android.R.layout.select_dialog_singlechoice);

                    // The order of the overweight or stunting or therapeutic programs should be
                    // in following order. Other programs are shown in the sane order as in
                    // the database.
                    if(selectedProgram.equals("JsfNVX0hdq9") || selectedProgram.equals("lSSNwBMiwrK")
                            || selectedProgram.equals("CoGsKgEG4O0") ) {
                        stages_names.add("Management");
                        stages_names.add("Intervention");
                        stages_names.add("Outcome");
                    }else
                    {
                        for (ProgramStage stageItem : stages) {
                            stages_names.add(stageItem.name());
                        }
                    }


                    // Show a dialog box to select the program stage. Once the program is
                    // launched we will call the activity corresponding to the selected
                    // program
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
                    builderSingle.setIcon(R.drawable.baby_girl);
                    builderSingle.setTitle("Select program stage");

                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            return;
                        }
                    });

                    builderSingle.setAdapter(stages_names, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(int i=0; i<stages.size();i++)
                            {
                                if(stages.get(i).name().equals(stages_names.getItem(which)))
                                {
                                    stageSelected = stages.get(i).uid();
                                }else if(stages.get(which).name().equals("Out come"))
                                {
                                    stageSelected = "L4MJKSCcUof";
                                }
                            }
                            //stageSelected = stages.get(which).uid();

                            List<String> j = new ArrayList<>();
                            System.out.println("Came here");
                            compositeDisposable.add(
                                    Sdk.d2().programModule().programs()
                                            .uid(selectedProgram).get()
                                            .map(program -> {
                                                /*
                                                String orgUnit = Sdk.d2().trackedEntityModule().trackedEntityInstances()
                                                        .uid(selectedChild).blockingGet().organisationUnit();

                                                 */

                                                String orgUnit = Sdk.d2().organisationUnitModule().organisationUnits()
                                                        .byProgramUids(Collections.singletonList(selectedProgram))
                                                        .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                                                        .one().blockingGet().uid();
                                                //System.out.println("org" + orgUnit);
                                                /*
                                                String enrollmentID = Sdk.d2().enrollmentModule().enrollments()
                                                        .byProgram().eq(selectedProgram)
                                                        .byTrackedEntityInstance().eq(selectedChild)
                                                        //.byOrganisationUnit().eq(orgUnit)
                                                        .one().blockingGet().uid();

                                                 */
                                                String enrollmentID = "";
                                                List<Enrollment> enrollmentList = Sdk.d2().enrollmentModule().enrollments()
                                                        .byTrackedEntityInstance().eq(selectedChild)
                                                        .byProgram().eq(selectedProgram)
                                                        //.orderByLastUpdated(RepositoryScope.OrderByDirection.DESC)
                                                        .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                                                        .blockingGet();

                                                if(!enrollmentList.isEmpty())
                                                {
                                                    enrollmentID =  enrollmentList.get(0).uid();
                                                }
                                                //System.out.println("OrgUnit: " + orgUnit + " EnrollementID " + enrollmentID);
                                                //String stage = Sdk.d2().programModule().programStages()
                                                //        .byProgramUid().eq(program.uid())
                                                //        .one().blockingGet().uid();
                                                String attrOptionCombo = program.categoryCombo() != null ?
                                                        Sdk.d2().categoryModule().categoryOptionCombos()
                                                                .byCategoryComboUid().eq(program.categoryComboUid())
                                                                .one().blockingGet().uid() : null;
                                                return Sdk.d2().eventModule().events()
                                                        //.withTrackedEntityDataValues().byTrackedEntityInstanceUids(j)
                                                        .blockingAdd(
                                                                EventCreateProjection.builder()
                                                                        .organisationUnit(orgUnit)
                                                                        .program(program.uid())
                                                                        .enrollment(enrollmentID)
                                                                        .programStage(stageSelected)
                                                                        .attributeOptionCombo(attrOptionCombo)
                                                                        .build()
                                                        );
                                            })
                                            .map(eventUid ->
                                                    {
                                                        System.out.println("Valencia Came here");
                                                        System.out.println(selectedProgram);
                                                        if(selectedProgram.equals("hM6Yt9FQL0n"))
                                                        {
                                                            return AnthropometryActivityNew.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    AnthropometryActivityNew.FormType.CREATE,
                                                                    selectedChild);
                                                        }else if(stageSelected.equals("KN0o3H6x8IH"))
                                                        {
                                                            return SupplementaryIndicationActivity.getFormActivityIntent( // supplementary - indication for thriposha
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    SupplementaryIndicationActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("O9FEeIYqGRH")) // other - evaluation
                                                        {
                                                            return OtherEvaluationActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    OtherEvaluationActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("iWycCg6C2gd")) // other - reason for enrollment
                                                        {
                                                            return OtherReasonForActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    OtherReasonForActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }

                                                        else if(stageSelected.equals("y2imfIjE4zt")) // other - referred for intervention
                                                        {
                                                            return OtherReferredForInterventionActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    OtherReferredForInterventionActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }

                                                        else if(stageSelected.equals("TC7YSoNEUag")) // overweight - management
                                                        {
                                                            return OverweightManagementActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    OverweightManagementActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("S4DegY3OjJv")) // overweight - intervention
                                                        {
                                                            return OverweightIntervensionActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    OverweightIntervensionActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("ctwLm9rn8gr")) // overweight - outcome
                                                        {
                                                            return OverweightOutcomeActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    OverweightOutcomeActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("mjjxR9aGJ4P")) // stunting - intervention
                                                        {
                                                            return StuntingInterventionActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    StuntingInterventionActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("iEylwjAa5Cq")) // stunting - management
                                                        {
                                                            return StuntingManagementActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    StuntingManagementActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("L4MJKSCcUof")) // stunting - outcome
                                                        {
                                                            return StuntingOutcomeActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    StuntingOutcomeActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("QKsx9TfOJ3m")) // supplementary - outcome
                                                        {
                                                            return SupplementaryOutcomeActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    SupplementaryOutcomeActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("du2KnwyeL32")) // supplementary - intervention
                                                        {
                                                            return SupplementaryInterventionActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    SupplementaryInterventionActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("YweAFncBjUm")) // therapeutic - intervention
                                                        {
                                                            return TherapeuticInterventionActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    TherapeuticInterventionActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("B8Jbdgg7Ut1")) // therapeutic - management
                                                        {
                                                            return TherapeuticManagementActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    TherapeuticManagementActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else if(stageSelected.equals("RtC4CcoEs4J")) // therapeutic - outcome
                                                        {
                                                            return TherapeuticOutcomeActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    TherapeuticOutcomeActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                        else{
                                                            return EventFormActivity.getFormActivityIntent(EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    EventFormActivity.FormType.CREATE,
                                                                    selectedChild);
                                                        }
                                                    }
                                            )
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    activityIntent -> {
                                                        if(selectedProgram.equals("pI5JAmTcjE4"))
                                                        {
                                                            Toast t = Toast.makeText(getApplicationContext(),
                                                                    "Anthropometry clicked",
                                                                    Toast.LENGTH_LONG);
                                                            t.show();
                                                        }else
                                                        {
                                                            ActivityStarter.startActivityForResult(
                                                                    EventsActivity.this, activityIntent, EVENT_RQ);
                                                        }
                                                    }, Throwable::printStackTrace ));
                        }
                    });
                    builderSingle.show();
                }

        );
    }

    /**
     * This method will set adapter for the list view using the event repository data
     */
    private void observeEvents() {
        adapter = new EventAdapter(this, selectedChild);
        recyclerView.setAdapter(adapter);

        getEventRepository().getPaged(20).observe(this, eventsPagedList -> {
            adapter.setSource(eventsPagedList.getDataSource());
            adapter.submitList(eventsPagedList);
            findViewById(R.id.eventsNotificator).setVisibility(
                    eventsPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    /**
     *
     * @return
     */
    private EventCollectionRepository getEventRepository() {
        List<String> j = new ArrayList<>();
        j.add(selectedChild);
        /*
        EventCollectionRepository eventRepository =
                Sdk.d2().eventModule().events()
                        .withTrackedEntityDataValues().byTrackedEntityInstanceUids(j);
         */
        EventCollectionRepository eventRepository = Sdk.d2().eventModule().events()
                .withTrackedEntityDataValues().byTrackedEntityInstanceUids(j);

        if (!isEmpty(selectedProgram)) {
            return eventRepository.byProgramUid().eq(selectedProgram)
                    .byEnrollmentUid().eq(programEnrollmentID);
        } else {
            return eventRepository;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == EVENT_RQ && resultCode == RESULT_OK) {
            adapter.invalidateSource();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}