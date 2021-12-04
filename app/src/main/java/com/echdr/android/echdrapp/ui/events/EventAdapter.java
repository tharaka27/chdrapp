package com.echdr.android.echdrapp.ui.events;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.DataSource;
import androidx.paging.PagedListAdapter;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.ActivityStarter;
import com.echdr.android.echdrapp.data.service.DateFormatHelper;
import com.echdr.android.echdrapp.ui.base.DiffByIdItemCallback;
import com.echdr.android.echdrapp.ui.base.ListItemWithSyncHolder;
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
import com.echdr.android.echdrapp.ui.tracker_import_conflicts.TrackerImportConflictsAdapter;

import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.echdr.android.echdrapp.data.service.StyleBinderHelper.setBackgroundColor;
import static com.echdr.android.echdrapp.data.service.StyleBinderHelper.setState;

public class EventAdapter extends PagedListAdapter<Event, ListItemWithSyncHolder> {

    private final AppCompatActivity activity;
    private DataSource<?, Event> source;
    private String selectedChild;
    private HashMap<String, String> programStageNames;
    private HashMap<String, String> anthropometryElements;
    private HashMap<String, String> otherHealth_reasonForEnrollment;
    private HashMap<String, String> otherHealth_riskFactorEvaluation;
    private HashMap<String, String> otherHealth_referredForIntervention;




    public EventAdapter(AppCompatActivity activity, String selectedChild) {
        super(new DiffByIdItemCallback<>());
        this.activity = activity;
        this.selectedChild = selectedChild;


        programStageNames = new HashMap<>();
        programStageNames.put("pI5JAmTcjE4", "Intervention");
        programStageNames.put("iWycCg6C2gd", "Reason for enrollment");
        programStageNames.put("O9FEeIYqGRH", "Risk factor evaluation");
        programStageNames.put("y2imfIjE4zt", "Referred for intervention");
        programStageNames.put("TC7YSoNEUag", "Management");
        programStageNames.put("S4DegY3OjJv", "Intervention");
        programStageNames.put("ctwLm9rn8gr", "Outcome");
        programStageNames.put("iEylwjAa5Cq", "Management");
        programStageNames.put("mjjxR9aGJ4P", "Intervention");
        programStageNames.put("L4MJKSCcUof", "Out come");
        programStageNames.put("KN0o3H6x8IH", "Indication for Thriposha");
        programStageNames.put("du2KnwyeL32", "Interventions");
        programStageNames.put("QKsx9TfOJ3m", "Outcome");
        programStageNames.put("B8Jbdgg7Ut1", "Management");
        programStageNames.put("YweAFncBjUm", "Intervention");
        programStageNames.put("RtC4CcoEs4J", "Outcome");

        anthropometryElements = new HashMap<>();
        anthropometryElements.put("YB21tVtxZ0z", "Anthropometry date");
        anthropometryElements.put("cDXlUgg1WiZ", "Length/Height");
        anthropometryElements.put("SOAtQfInRoy", "Length/Height for Age");
        anthropometryElements.put("b4Gpl5ayBe3", "Age in months");
        anthropometryElements.put("rBRI27lvfY5", "Weight");
        anthropometryElements.put("bJHCnjX02PN", "Weight for Age");
        anthropometryElements.put("jnzg5BvOj5T", "Weight for Length/Height");

        otherHealth_reasonForEnrollment = new HashMap<>();
        otherHealth_reasonForEnrollment.put("Sw98c8KAEmr", "Reason Long-standing Growth faltering in green zone date");
        otherHealth_reasonForEnrollment.put("Dpw5YPM1CFj", "Reason for enrollment date");
        otherHealth_reasonForEnrollment.put("QNV3Qb2kjx8", "Reason MAM");
        otherHealth_reasonForEnrollment.put("dnLak5wmEzT", "Reason Overweight_Obesity");
        otherHealth_reasonForEnrollment.put("AOKp3oQPyYP", "Reason SAM");
        otherHealth_reasonForEnrollment.put("paM0QZaZMTO", "Reason Stunting");
        otherHealth_reasonForEnrollment.put("xkhQxmJ8X24", "Reason Underweight");

        otherHealth_riskFactorEvaluation = new HashMap<>();
        otherHealth_riskFactorEvaluation.put("cLNSXKlqjqA", "Food insecurity");
        otherHealth_riskFactorEvaluation.put("Zr5SvpMT2y0", "High prevalence of communicable diseases");
        otherHealth_riskFactorEvaluation.put("nXJSGsaPznl", "Inadequate child care");
        otherHealth_riskFactorEvaluation.put("r1YtZtTBbKZ", "Inadequate water sanitation");
        otherHealth_riskFactorEvaluation.put("riZnnab24ef", "Poverty and poor income management");
        otherHealth_riskFactorEvaluation.put("cmqwQ5zk66F", "Risk factor evaluation date");






    }

    @NonNull
    @Override
    public ListItemWithSyncHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListItemWithSyncHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemWithSyncHolder holder, int position) {
        Event event = getItem(position);
        List<TrackedEntityDataValue> values = new ArrayList<>(event.trackedEntityDataValues());

        /*
        holder.title.setText(orgUnit(event.organisationUnit()).displayName());
        holder.subtitle1.setText(valueAt(values, event.programStage()));
        holder.subtitle2.setText(optionCombo(event.attributeOptionCombo()).displayName());
        holder.rightText.setText(DateFormatHelper.formatDate(event.eventDate()));
        holder.icon.setImageResource(R.drawable.ic_programs_black_24dp);
        holder.delete.setVisibility(View.VISIBLE);
         */
        holder.title.setText(programStageNames.get(event.programStage()));
        holder.subtitle1.setText(valueAt(values, event.programStage()));
        holder.subtitle2.setText(optionCombo(event.attributeOptionCombo()).displayName());
        holder.rightText.setText(DateFormatHelper.formatDate(event.eventDate()));
        holder.icon.setImageResource(R.drawable.ic_programs_black_24dp);
        holder.delete.setVisibility(View.VISIBLE);

        holder.delete.setOnClickListener(view -> {
            try {
                Sdk.d2().eventModule().events().uid(event.uid()).blockingDelete();
                invalidateSource();
                notifyDataSetChanged();
            } catch (D2Error d2Error) {
                d2Error.printStackTrace();
            }
        });
        setBackgroundColor(R.color.colorAccentDark, holder.icon);
        setState(event.state(), holder.syncIcon);
        setConflicts(event.uid(), holder);

        holder.itemView.setOnClickListener(view->{

            if(event.program().equals("hM6Yt9FQL0n"))
            {
                ActivityStarter.startActivity(activity,  // anthropometry
                        AnthropometryActivityNew.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                AnthropometryActivityNew.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }else if(event.programStage().equals("KN0o3H6x8IH")) // supplementary - indication for thriposha
            {
                ActivityStarter.startActivity(activity,
                        SupplementaryIndicationActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                SupplementaryIndicationActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("O9FEeIYqGRH")) // other - evaluation
            {
                ActivityStarter.startActivity(activity,
                        OtherEvaluationActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                OtherEvaluationActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("iWycCg6C2gd")) // other - reason
            {
                ActivityStarter.startActivity(activity,
                        OtherReasonForActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                OtherReasonForActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("y2imfIjE4zt")) // other - intervention
            {
                ActivityStarter.startActivity(activity,
                        OtherReferredForInterventionActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                OtherReferredForInterventionActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("TC7YSoNEUag")) // overweight - management
            {
                ActivityStarter.startActivity(activity,
                        OverweightManagementActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                OverweightManagementActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("S4DegY3OjJv")) // overweight - intervention
            {
                ActivityStarter.startActivity(activity,
                        OverweightIntervensionActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                OverweightIntervensionActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("ctwLm9rn8gr")) // overweight - outcome
            {
                ActivityStarter.startActivity(activity,
                        OverweightOutcomeActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                OverweightOutcomeActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("mjjxR9aGJ4P")) // stunting - intervention
            {
                ActivityStarter.startActivity(activity,
                        StuntingInterventionActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                StuntingInterventionActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("iEylwjAa5Cq")) // stunting - management
            {
                ActivityStarter.startActivity(activity,
                        StuntingManagementActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                StuntingManagementActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("L4MJKSCcUof")) // stunting - management
            {
                ActivityStarter.startActivity(activity,
                        StuntingOutcomeActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                StuntingOutcomeActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("QKsx9TfOJ3m")) // supplementary - outcome
            {
                ActivityStarter.startActivity(activity,
                        SupplementaryOutcomeActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                SupplementaryOutcomeActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("du2KnwyeL32")) // supplementary - intervention
            {
                ActivityStarter.startActivity(activity,
                        SupplementaryInterventionActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                SupplementaryInterventionActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("YweAFncBjUm")) // therapeutic - intervention
            {
                ActivityStarter.startActivity(activity,
                        TherapeuticInterventionActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                TherapeuticInterventionActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("B8Jbdgg7Ut1")) // therapeutic - management
            {
                ActivityStarter.startActivity(activity,
                        TherapeuticManagementActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                TherapeuticManagementActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("RtC4CcoEs4J")) // therapeutic - outcome
            {
                ActivityStarter.startActivity(activity,
                        TherapeuticOutcomeActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                TherapeuticOutcomeActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else {
                ActivityStarter.startActivity(
                        activity,
                        EventFormActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                EventFormActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
        });
    }




    private OrganisationUnit orgUnit(String orgUnitUid) {
        return Sdk.d2().organisationUnitModule().organisationUnits().uid(orgUnitUid).blockingGet();
    }

    private String valueAt(List<TrackedEntityDataValue> values, String stageUid) {
        for (TrackedEntityDataValue dataValue : values) {
            ProgramStageDataElement programStageDataElement = Sdk.d2().programModule().programStageDataElements()
                    .byDataElement().eq(dataValue.dataElement())
                    .byProgramStage().eq(stageUid)
                    .one().blockingGet();
            if (programStageDataElement.displayInReports()) {
                return String.format("%s: %s", programStageDataElement.displayName(), dataValue.value());
            }
        }

        return null;
    }

    private CategoryOptionCombo optionCombo(String attrOptionCombo) {
        return Sdk.d2().categoryModule().categoryOptionCombos().uid(attrOptionCombo).blockingGet();
    }

    private void setConflicts(String trackedEntityInstanceUid, ListItemWithSyncHolder holder) {
        TrackerImportConflictsAdapter adapter = new TrackerImportConflictsAdapter();
        holder.recyclerView.setAdapter(adapter);
        adapter.setTrackerImportConflicts(Sdk.d2().importModule().trackerImportConflicts()
                .byTrackedEntityInstanceUid().eq(trackedEntityInstanceUid).blockingGet());
    }


    public void setSource(DataSource<?, Event> dataSource) {
        this.source = dataSource;
    }

    public void invalidateSource() {
        source.invalidate();
    }




}