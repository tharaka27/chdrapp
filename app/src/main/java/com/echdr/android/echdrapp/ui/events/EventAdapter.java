package com.echdr.android.echdrapp.ui.events;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.echdr.android.echdrapp.ui.event_form.EventFormActivity;
import com.echdr.android.echdrapp.ui.event_form.OtherEvaluationActivity;
import com.echdr.android.echdrapp.ui.event_form.OtherFoodInsecurityActivity;
import com.echdr.android.echdrapp.ui.event_form.OtherInadequateWaterActivity;
import com.echdr.android.echdrapp.ui.event_form.OtherInterventionPoverty;
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
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.echdr.android.echdrapp.data.service.StyleBinderHelper.setBackgroundColor;
import static com.echdr.android.echdrapp.data.service.StyleBinderHelper.setState;

public class EventAdapter extends PagedListAdapter<Event, ListItemWithSyncHolder> {

    private final AppCompatActivity activity;
    private DataSource<?, Event> source;
    private String selectedChild;
    private HashMap<String, String[]> programStageNames;
    private Context context;



    public EventAdapter(AppCompatActivity activity, String selectedChild) {
        super(new DiffByIdItemCallback<>());
        this.activity = activity;
        this.selectedChild = selectedChild;
        this.context = activity.getBaseContext();

        programStageNames = new HashMap<>();
        programStageNames.put("pI5JAmTcjE4", new String[]{context.getResources().getString(R.string.an_stage)}); // age in months
        programStageNames.put("iWycCg6C2gd", new String[]{context.getResources().getString(R.string.st_reason)}); //
        programStageNames.put("O9FEeIYqGRH", new String[]{context.getResources().getString(R.string.st_risk)});
        programStageNames.put("y2imfIjE4zt", new String[]{context.getResources().getString(R.string.st_reff)});
        programStageNames.put("TC7YSoNEUag", new String[]{context.getResources().getString(R.string.st_mana)});
        programStageNames.put("S4DegY3OjJv", new String[]{context.getResources().getString(R.string.st_inter)});
        programStageNames.put("ctwLm9rn8gr", new String[]{context.getResources().getString(R.string.st_out)});
        programStageNames.put("iEylwjAa5Cq", new String[]{context.getResources().getString(R.string.st_mana)});
        programStageNames.put("mjjxR9aGJ4P", new String[]{context.getResources().getString(R.string.st_inter)});
        programStageNames.put("L4MJKSCcUof", new String[]{context.getResources().getString(R.string.st_out)});
        programStageNames.put("KN0o3H6x8IH", new String[]{context.getResources().getString(R.string.st_indication)});
        programStageNames.put("du2KnwyeL32", new String[]{context.getResources().getString(R.string.st_inter)}); // number of thriposha packets given
        programStageNames.put("QKsx9TfOJ3m", new String[]{context.getResources().getString(R.string.st_out)});
        programStageNames.put("B8Jbdgg7Ut1", new String[]{context.getResources().getString(R.string.st_mana)});
        programStageNames.put("YweAFncBjUm", new String[]{context.getResources().getString(R.string.st_inter)});
        programStageNames.put("RtC4CcoEs4J", new String[]{context.getResources().getString(R.string.st_out)});
        programStageNames.put("bXWTWS8lkbv", new String[]{context.getResources().getString(R.string.oth_inter_poverty_title)});
        programStageNames.put("m7IDhrn3y22", new String[]{context.getResources().getString(R.string.oth_food_insecurity_title)});
        programStageNames.put("EHn8MUIERRM", new String[]{context.getResources().getString(R.string.oth_indequate_title)});
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
        try
        {
            holder.title.setText(programStageNames.get(event.programStage())[0]);
        }
        catch (Exception e)
        {
            System.out.println("[ERROR] Error occured when setting program stage name in " +
                    "adapter");
            holder.title.setText("");
        }
        //holder.title.setText(programStageNames.get(event.programStage())[0]);

        /* selecting subtitle two */
        String sub_two_data = "";

        try {

            if (event.programStage().equals("pI5JAmTcjE4")) // anthropometry
            {
                sub_two_data = "Height :" + getDataElementFromEvent("cDXlUgg1WiZ", event.uid())
                        + "cm Weight " + String.valueOf(Float.parseFloat(
                        getDataElementFromEvent("rBRI27lvfY5", event.uid())) / 1000)
                        + "kg";
            } else if (event.programStage().equals("iWycCg6C2gd")) //other - reason for enrollment
            {
                sub_two_data = "MAM: " + getDataElementFromEvent("QNV3Qb2kjx8", event.uid())
                        + " SAM: " + getDataElementFromEvent("AOKp3oQPyYP", event.uid());
            } else if (event.programStage().equals("TC7YSoNEUag")) //obesity - management
            {
                sub_two_data = "Nutrition seen: "
                        + getDataElementFromEvent("FMJdAftRK7q", event.uid());
            } else if (event.programStage().equals("S4DegY3OjJv")) //obesity - Intervention
            {
                sub_two_data = "Nutrition councelling given: "
                        + getDataElementFromEvent("FMJdAftRK7q", event.uid());
            } else if (event.programStage().equals("du2KnwyeL32")) //supplementary - intervention
            {
                sub_two_data = "Num thriposha packets given: " +
                        getDataElementFromEvent("h9Sv7i87Ks1", event.uid());
            } else if (event.programStage().equals("iEylwjAa5Cq")) //stunting - Management
            {
                sub_two_data = "Paediatrician/ MO/Nutrition seen: " +
                        getDataElementFromEvent("KdN0rkDaYLD", event.uid());
            } else if (event.programStage().equals("mjjxR9aGJ4P")) //stunting - Intervention
            {
                sub_two_data = "IYCF/Nutrition counselling: " +
                        getDataElementFromEvent("Xpf2G3fhTUb", event.uid());
            }
        }catch (Exception e)
        {
            sub_two_data = "partially filled data";
        }
        holder.subtitle2.setText(sub_two_data);

        holder.subtitle1.setText(DateFormatHelper.formatDate(event.eventDate()));
        //holder.rightText.setText(DateFormatHelper.formatDate(event.eventDate()));
        holder.icon.setImageResource(R.drawable.ic_programs_black_24dp);
        holder.delete.setVisibility(View.VISIBLE);

        holder.delete.setOnClickListener(view -> {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
            builder1.setMessage("Are you sure you want to delete the Event?");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
            builder1.setPositiveButton(
                    "confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                Sdk.d2().eventModule().events().uid(event.uid()).blockingDelete();
                                invalidateSource();
                                notifyDataSetChanged();
                            } catch (D2Error d2Error) {
                                d2Error.printStackTrace();
                            }
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();


        });
        setBackgroundColor(R.color.colorAccentDark, holder.icon);
        setState(event.state(), holder.syncIcon);
        setConflicts(event.uid(), holder);

        holder.itemView.setOnClickListener(view->{

            if(event.program().equals("hM6Yt9FQL0n"))
            {
                ActivityStarter.startActivity(activity,  // anthropometry
                        AnthropometryActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                AnthropometryActivity.FormType.CHECK,
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
            else if(event.programStage().equals("bXWTWS8lkbv")) // other - Poverty & Poor Income Management
            {
                ActivityStarter.startActivity(activity,
                        OtherInterventionPoverty.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                OtherInterventionPoverty.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }
            else if(event.programStage().equals("m7IDhrn3y22")) // other - food insecurity
            {
                ActivityStarter.startActivity(activity,
                        OtherFoodInsecurityActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                OtherFoodInsecurityActivity.FormType.CHECK,
                                selectedChild
                        ), false
                );
            }

            else if(event.programStage().equals("EHn8MUIERRM")) // other - inadequate water
            {
                ActivityStarter.startActivity(activity,
                        OtherInadequateWaterActivity.getFormActivityIntent(
                                activity,
                                event.uid(),
                                event.program(),
                                event.organisationUnit(),
                                OtherInadequateWaterActivity.FormType.CHECK,
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
            else if(event.programStage().equals("L4MJKSCcUof")) // stunting - outcome
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
                System.out.println("[Error] Program not selected properly");
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

    private String getDataElementFromEvent(String dataElement, String captureEvent)
    {
        TrackedEntityDataValueObjectRepository valueRepository =
                Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                        .value(
                                captureEvent,
                                dataElement
                        );

        String currentValue = valueRepository.blockingExists() ?
                valueRepository.blockingGet().value() : "";

        return currentValue;
    }


}