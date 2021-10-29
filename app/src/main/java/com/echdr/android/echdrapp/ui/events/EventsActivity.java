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
import com.echdr.android.echdrapp.ui.event_form.EventFormActivity;

import org.hisp.dhis.android.core.event.EventCollectionRepository;
import org.hisp.dhis.android.core.event.EventCreateProjection;
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

    private enum IntentExtra {
        PROGRAM, TEI_ID
    }

    public static Intent getIntent(Context context, String programUid, String teiUid) {
        Bundle bundle = new Bundle();
        if (!isEmpty(programUid))
            bundle.putString(IntentExtra.PROGRAM.name(), programUid);
        if(!isEmpty(teiUid))
            bundle.putString(IntentExtra.TEI_ID.name(), teiUid);
        Intent intent = new Intent(context, EventsActivity.class);
        intent.putExtras(bundle);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_events, R.id.eventsRecyclerView);
        selectedProgram = getIntent().getStringExtra(IntentExtra.PROGRAM.name());
        selectedChild = getIntent().getStringExtra(IntentExtra.TEI_ID.name());
        compositeDisposable = new CompositeDisposable();
        observeEvents();
        context = this;
        //Sdk.d2().programModule().programs().byTrackedEntityTypeUid().eq(selectedChild)
        System.out.println("Selected child is @ eventActivity " + selectedChild);

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
                    for (ProgramStage stageItem : stages) {
                        stages_names.add(stageItem.name());
                    }

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
                            stageSelected = stages.get(which).uid();

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
                                                String enrollmentID = Sdk.d2().enrollmentModule().enrollments()
                                                        .byProgram().eq(selectedProgram)
                                                        .byTrackedEntityInstance().eq(selectedChild)
                                                        .byOrganisationUnit().eq(orgUnit)
                                                        .one().blockingGet().uid();
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
                                                        if(selectedProgram.equals("hM6Yt9FQL0n"))
                                                        {
                                                            return AnthropometryActivity.getFormActivityIntent(
                                                                    EventsActivity.this,
                                                                    eventUid,
                                                                    selectedProgram,
                                                                    Sdk.d2().organisationUnitModule().organisationUnits()
                                                                            .one().blockingGet().uid(),
                                                                    AnthropometryActivity.FormType.CREATE,
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
            return eventRepository.byProgramUid().eq(selectedProgram);
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