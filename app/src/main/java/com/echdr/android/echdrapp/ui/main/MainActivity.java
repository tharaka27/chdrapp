package com.echdr.android.echdrapp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.ActivityStarter;
import com.echdr.android.echdrapp.data.service.SyncStatusHelper;
import com.echdr.android.echdrapp.ui.code_executor.CodeExecutorActivity;
import com.echdr.android.echdrapp.ui.d2_errors.D2ErrorActivity;
import com.echdr.android.echdrapp.ui.data_sets.DataSetsActivity;
import com.echdr.android.echdrapp.ui.data_sets.instances.DataSetInstancesActivity;
import com.echdr.android.echdrapp.ui.enrollment_form.EnrollmentFormActivity;
import com.echdr.android.echdrapp.ui.enrollment_form.EnrollmentFormModified;
import com.echdr.android.echdrapp.ui.events.EventsActivity;
import com.echdr.android.echdrapp.ui.foreign_key_violations.ForeignKeyViolationsActivity;
import com.echdr.android.echdrapp.ui.programs.ProgramsActivity;
import com.echdr.android.echdrapp.ui.splash.LanguageSelection;
import com.echdr.android.echdrapp.ui.tracked_entity_instances.TrackedEntityInstancesActivity;
import com.echdr.android.echdrapp.ui.tracked_entity_instances.search.TrackedEntityInstanceSearchActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection;
import org.hisp.dhis.android.core.user.User;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity{

    private CompositeDisposable compositeDisposable;
    private final int ENROLLMENT_RQ = 1210;

    private Context context;
    private TextView numAllregistrations;
    private TextView numSupplementary;
    private TextView numTherapeutic;
    private TextView numOverweight;
    private TextView numStunting;
    private TextView numOtherHealth;
    private Button syncBtn;
    private Button UploadBtn;
    private Button languageBtn;
    private LinearLayout myAreaDetailsBtn;
    private LinearLayout createNewChild;
    private ProgressBar progressBar;
    private boolean isSyncing = false;

    public static Intent getMainActivityIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_yash);

        compositeDisposable = new CompositeDisposable();
        context = this;

        User user = getUser();
        TextView greeting = findViewById(R.id.greetingYash);
        greeting.setText(String.format("%s!", user.displayName()));


        inflateMainView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    private void inflateMainView() {

        languageBtn = findViewById(R.id.languageBtn);
        syncBtn = findViewById(R.id.syncBtn);
        UploadBtn = findViewById(R.id.uploadBtn);
        myAreaDetailsBtn = findViewById(R.id.area);
        createNewChild = findViewById(R.id.new_child);
        progressBar = findViewById(R.id.progressBar);

        //progressBar.setVisibility(View.VISIBLE);

        numAllregistrations = findViewById(R.id.numAll);
        numTherapeutic = findViewById(R.id.numTherapeutical);
        numOtherHealth = findViewById(R.id.numOther);
        numOverweight = findViewById(R.id.numOverweight);
        numStunting = findViewById(R.id.numStunting);
        numSupplementary = findViewById(R.id.numSup);

        numAllregistrations.setText(MessageFormat.format("{0}",
                SyncStatusHelper.trackedEntityInstanceCount()));
        numSupplementary.setText(MessageFormat.format("{0}",
                SyncStatusHelper.supplementaryCount()));
        numOtherHealth.setText(MessageFormat.format("{0}",
                SyncStatusHelper.otherCount()));
        numOverweight.setText(MessageFormat.format("{0}",
                SyncStatusHelper.overweightCount()));
        numStunting.setText(MessageFormat.format("{0}",
                SyncStatusHelper.stuntingCount()));
        numTherapeutic.setText(MessageFormat.format("{0}",
                SyncStatusHelper.therapeuticalCount()));

        myAreaDetailsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent searchAreaDetails = new Intent(getApplicationContext(), TrackedEntityInstancesActivity.class);
                startActivity(searchAreaDetails);

            }
        });

        createNewChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast t = Toast.makeText(context, "Clicked add new child", Toast.LENGTH_LONG);
                t.show();

                int ENROLLMENT_RQ = 1210;
                List<String> j = new ArrayList<>();
                j.add("hM6Yt9FQL0n");
                compositeDisposable.add(
                        Sdk.d2().programModule().programs().uid("hM6Yt9FQL0n").get()
                                .map(program -> Sdk.d2().trackedEntityModule().trackedEntityInstances()
                                        .blockingAdd(
                                                TrackedEntityInstanceCreateProjection.builder()
                                                        .organisationUnit(
                                                                Sdk.d2().organisationUnitModule().organisationUnits()
                                                                        .byProgramUids(Collections.singletonList("hM6Yt9FQL0n"))
                                                                        .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                                                                        .one().blockingGet().uid()
                                                        )
                                                        .trackedEntityType(program.trackedEntityType().uid())
                                                        .build()
                                        ))
                                //.map(teiUid -> EnrollmentFormActivity.getFormActivityIntent(
                                .map(teiUid -> EnrollmentFormModified.getModifiedFormActivityIntent(
                                        MainActivity.this,
                                        teiUid,
                                        "hM6Yt9FQL0n",
                                        Sdk.d2().organisationUnitModule().organisationUnits()
                                                //.byProgramUids(Collections.singletonList("hM6Yt9FQL0n"))
                                                .byProgramUids( j)
                                                .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                                                .one().blockingGet().uid()
                                ))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        activityIntent ->
                                                ActivityStarter.startActivityForResult(MainActivity.this,
                                                        activityIntent, ENROLLMENT_RQ),
                                        Throwable::printStackTrace
                                )
                );
            }
        });

        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                syncBtn.setClickable(false);
                syncBtn.setText("Syncing...");

                // First Upload any data
                uploadData();

                // Download Metadata and User data
                // Downloading data happens after metadata download automatically
                syncMetadata();

            }
        });

        UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityStarter.startActivity(MainActivity.this, LanguageSelection.getLanguageSelectionActivityIntent(context),true);
            }
        });
    }


    private void syncMetadata() {
        compositeDisposable.add(downloadMetadata()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(Throwable::printStackTrace)
                .doOnComplete(this::downloadData)
                .subscribe());
    }

    private Observable<D2Progress> downloadMetadata() {
        return Sdk.d2().metadataModule().download();
    }

    private void downloadData() {

        compositeDisposable.add(
                Observable.merge(
                        downloadTrackedEntityInstances(),
                        downloadSingleEvents(),
                        downloadAggregatedData()
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {

                            // set progress bar gone
                            progressBar.setVisibility(View.GONE);
                            syncBtn.setClickable(true);
                            //syncBtn.setText("SYNC DATA");
                            ActivityStarter.startActivity(this, MainActivity.getMainActivityIntent(this),
                                    true);
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    private Observable<D2Progress> downloadTrackedEntityInstances() {
        return Sdk.d2().trackedEntityModule().trackedEntityInstanceDownloader()
                .limitByOrgunit(true).limitByProgram(false).download();
    }

    private Observable<D2Progress> downloadSingleEvents() {
        return Sdk.d2().eventModule().eventDownloader()
                .limitByOrgunit(true).limitByProgram(false).download();
    }

    private Observable<D2Progress> downloadAggregatedData() {
        return Sdk.d2().aggregatedModule().data().download();
    }

    private User getUser() {
        return Sdk.d2().userModule().user().blockingGet();
    }

    private void uploadData() {
        compositeDisposable.add(
                Sdk.d2().fileResourceModule().fileResources().upload()
                        .concatWith(Sdk.d2().trackedEntityModule().trackedEntityInstances().upload())
                        .concatWith(Sdk.d2().dataValueModule().dataValues().upload())
                        .concatWith(Sdk.d2().eventModule().events().upload())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(() -> {
                            Toast t = Toast.makeText(context,
                                    "Syncing data complete", Toast.LENGTH_LONG);
                        })
                        .doOnError(Throwable::printStackTrace)
                        .subscribe());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /*
        if(requestCode == ENROLLMENT_RQ && resultCode == RESULT_OK){

        }*/
        super.onActivityResult(requestCode,resultCode,data);
    }


}