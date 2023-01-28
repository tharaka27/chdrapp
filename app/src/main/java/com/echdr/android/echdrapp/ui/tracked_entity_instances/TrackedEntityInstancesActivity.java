package com.echdr.android.echdrapp.ui.tracked_entity_instances;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.echdr.android.echdrapp.LocaleHelper;
import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.databinding.ActivityTrackedEntityInstanceSearchBinding;
import com.echdr.android.echdrapp.ui.base.ListActivity;
import com.echdr.android.echdrapp.ui.splash.LanguageContext;
import com.echdr.android.echdrapp.ui.tracked_entity_instances.search.SearchFormAdapter;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.textfield.TextInputEditText;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCollectionRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class TrackedEntityInstancesActivity extends ListActivity {

    private ActivityTrackedEntityInstanceSearchBinding binding;
    private CompositeDisposable compositeDisposable;
    private String selectedProgram;
    private final int ENROLLMENT_RQ = 1210;
    private TrackedEntityInstanceAdapter adapter;
    private Context context ;

    private String savedAttribute;
    private String savedProgram;
    private EditText searchText;

    private Button allBtn;
    private Button activeBtn;
    private Button completeBtn;


    private enum IntentExtra {
        TRACKED_ENTITY_INSTANCE
    }

    private enum Status{
        ACTIVE ,COMPLETE
    }

    public static Intent getTrackedEntityInstancesActivityIntent(Context context, String trackedEntityInstanceUid) {
        context = LocaleHelper.setLocale(context, LanguageContext.getLanguageContext().getLanguage());
        Intent intent = new Intent(context, TrackedEntityInstancesActivity.class);
        intent.putExtra(IntentExtra.TRACKED_ENTITY_INSTANCE.name(), trackedEntityInstanceUid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUp(R.layout.activity_tracked_entity_instances, R.id.trackedEntityInstancesRecyclerView);
        compositeDisposable = new CompositeDisposable();
        context = this;
        savedAttribute = "zh4hiarsSD5";
        savedProgram = "hM6Yt9FQL0n";

        allBtn = findViewById(R.id.all);
        activeBtn = findViewById(R.id.active);
        completeBtn = findViewById(R.id.complete);

        observeTrackedEntityInstances();
        searchText = findViewById(R.id.searchTextTEI);
        Button submitButton = findViewById(R.id.downloadDataButton);


        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty())
                {
                    System.out.println(editable.toString());
                    search(editable.toString());
                }else{
                    observeTrackedEntityInstances();
                }

            }
        });

        allBtn.setBackgroundColor(Color.CYAN);
        completeBtn.setBackgroundColor(Color.WHITE);
        activeBtn.setBackgroundColor(Color.WHITE);

        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observeTrackedEntityInstances();
                allBtn.setBackgroundColor(Color.CYAN);
                completeBtn.setBackgroundColor(Color.WHITE);
                activeBtn.setBackgroundColor(Color.WHITE);
            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActiveOrComplete(Status.COMPLETE);
                allBtn.setBackgroundColor(Color.WHITE);
                completeBtn.setBackgroundColor(Color.CYAN);
                activeBtn.setBackgroundColor(Color.WHITE);
            }
        });

        activeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectActiveOrComplete(Status.ACTIVE);
                allBtn.setBackgroundColor(Color.WHITE);
                completeBtn.setBackgroundColor(Color.WHITE);
                activeBtn.setBackgroundColor(Color.CYAN);
            }
        });

    }

    private void search(String name) {
        adapter = new TrackedEntityInstanceAdapter(this);
        System.out.println("Came here");
        recyclerView.setAdapter(adapter);

        getTrackedEntityInstanceQuery(name).observe(this, trackedEntityInstancePagedList -> {
            adapter.setSource(trackedEntityInstancePagedList.getDataSource());
            adapter.submitList(trackedEntityInstancePagedList);
        });

        System.out.println("Came here after setting");
    }

    private void selectActiveOrComplete(Status status) {
        adapter = new TrackedEntityInstanceAdapter(this);
        recyclerView.setAdapter(adapter);

        getActiveOrComplete(status).observe(this, trackedEntityInstancePagedList -> {
            adapter.setSource(trackedEntityInstancePagedList.getDataSource());
            adapter.submitList(trackedEntityInstancePagedList);
        });

    }


    private LiveData<PagedList<TrackedEntityInstance>> getTrackedEntityInstanceQuery(String name) {

        List<OrganisationUnit> organisationUnits = Sdk.d2().organisationUnitModule().organisationUnits()
                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                .byRootOrganisationUnit(true)
                .blockingGet();

        List<String> organisationUids = new ArrayList<>();
        if (!organisationUnits.isEmpty()) {
            organisationUids = UidsHelper.getUidsList(organisationUnits);
        }


        return Sdk.d2().trackedEntityModule()
                .trackedEntityInstanceQuery()
                //.byOrgUnits().in(organisationUids)
                //.byOrgUnitMode().eq(OrganisationUnitMode.DESCENDANTS)
                .byProgram().eq(savedProgram)
                //.byEnrollmentStatus().eq(EnrollmentStatus.COMPLETED)
                .byFilter(savedAttribute).like(name)
                .getPaged(100);

        /*
        return Sdk.d2().trackedEntityModule()
                .trackedEntityInstanceQuery()
                .byOrgUnits().in(organisationUids)
                .byOrgUnitMode().eq(OrganisationUnitMode.DESCENDANTS)
                .byProgram().eq(savedProgram)
                .byFilter(savedAttribute).like(String.valueOf(searchText))
                .onlineFirst().getPaged(15);

         */
    }

    private LiveData<PagedList<TrackedEntityInstance>> getActiveOrComplete(Status status)
    {

        if(status == Status.ACTIVE)
        {
            return Sdk.d2().trackedEntityModule()
                    .trackedEntityInstanceQuery()
                    .byProgram().eq(savedProgram)
                    .byEnrollmentStatus().eq(EnrollmentStatus.ACTIVE)
                    .getPaged(100);
        }else if(status == Status.COMPLETE)
        {
            return Sdk.d2().trackedEntityModule()
                    .trackedEntityInstanceQuery()
                    .byProgram().eq(savedProgram)
                    .byEnrollmentStatus().eq(EnrollmentStatus.COMPLETED)
                    .getPaged(100);
        }else
            {
                Toast.makeText(this, "Else", Toast.LENGTH_LONG).show();
                return Sdk.d2().trackedEntityModule()
                        .trackedEntityInstanceQuery()
                        .getPaged(100);
        }
    }


    private void observeTrackedEntityInstances() {
        adapter = new TrackedEntityInstanceAdapter(this);
        recyclerView.setAdapter(adapter);
        try {

            getTeiRepository().getPaged(30).observe(this, trackedEntityInstancePagedList -> {
                adapter.setSource(trackedEntityInstancePagedList.getDataSource());
                adapter.submitList(trackedEntityInstancePagedList);
            });
        }
        catch(Exception e){
            setUp(R.layout.activity_tracked_entity_instances, R.id.trackedEntityInstancesRecyclerView);
            System.out.println(e.toString());
        }

    }

    private TrackedEntityInstanceCollectionRepository getTeiRepository() {
        TrackedEntityInstanceCollectionRepository teiRepository = null;
        try{
                teiRepository = Sdk.d2().trackedEntityModule()
                        .trackedEntityInstances()
                        .byDeleted().isFalse()
                        .withTrackedEntityAttributeValues();
        }
        catch (Exception e){
            Toast.makeText(this, "This data is partially filled", Toast.LENGTH_LONG).show();
            System.out.println(e.toString());
        }

        return teiRepository;
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
        if(requestCode == ENROLLMENT_RQ && resultCode == RESULT_OK){
            adapter.invalidateSource();
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onResume(){
        super.onResume();
        observeTrackedEntityInstances();
    }


}
