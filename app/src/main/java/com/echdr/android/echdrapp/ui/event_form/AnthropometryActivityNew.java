package com.echdr.android.echdrapp.ui.event_form;

import static android.text.TextUtils.isEmpty;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.processors.PublishProcessor;

public class AnthropometryActivityNew extends AppCompatActivity {

    private String eventUid;
    private String programUid;
    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private FormType formType;
    private GraphView heightGraph;
    private GraphView weightGraph;
    private String selectedChild;
    private String sex;
    private TextView textView_Date;
    private ImageView datePicker;

    private String orgUnit;
    private TextView AgeInWeeksTxt;
    private Context context;
    private DatePickerDialog.OnDateSetListener setListener;
    private EditText heightTxt;
    private EditText weightTxt;
    private Button saveButton;
    private Button plotGraphButton;
    private TrackedEntityAttributeValue birthday;

    Map<Integer, double[]> heightDataWHO;
    Map<Integer, double[]> weightDataWHO;

    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               FormType type, String teiID) {
        Intent intent = new Intent(context, AnthropometryActivityNew.class);
        intent.putExtra(IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(IntentExtra.TYPE.name(), type.name());
        intent.putExtra(IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anthropometry_new);

        textView_Date = findViewById(R.id.anthropometryDate);
        datePicker = findViewById(R.id.anthropometry_date_pick);
        heightTxt = findViewById(R.id.anthropometryHeight);
        weightTxt = findViewById(R.id.anthropometryWeight);
        saveButton = findViewById(R.id.anthropometrySave);
        heightGraph = findViewById(R.id.heightforageAnthropometry);
        weightGraph = findViewById(R.id.weightforageAnthropometry);
        AgeInWeeksTxt = findViewById(R.id.ageInWeeks);
        plotGraphButton = findViewById(R.id.plotGraph);

        eventUid = getIntent().getStringExtra(IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(IntentExtra.TEI_ID.name());
        formType = FormType.valueOf(getIntent().getStringExtra(IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(IntentExtra.OU_UID.name());

        engineInitialization = PublishProcessor.create();

        // Get the birthday of the child
        birthday = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(selectedChild)
                .byTrackedEntityAttribute().eq("qNH202ChkV3")
                .one().blockingGet();

        // Get the sex of the child
        TrackedEntityAttributeValue sex_d = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(selectedChild)
                .byTrackedEntityAttribute().eq("lmtzQrlHMYF")
                .one().blockingGet();
        
        sex = sex_d.value();

        selectDataSets();
        plotGraph();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        EventFormService.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        EventFormService.clear();
        setResult(RESULT_OK);
        finish();
    }

    private void selectDataSets()
    {
        DataValuesWHO d = DataValuesWHO.getInstance();
        if(sex.equals("Male"))
        {
            d.initializeheightForAgeBoys();
            d.initializeweightForAgeBoys();
            heightDataWHO = d.getHeightForAgeBoys();
            weightDataWHO = d.getWeightForAgeBoys();
        }else
        {
            d.initializeweightForAgeGirls();
            d.initializeheightForAgeGirls();
            heightDataWHO = d.getHeightForAgeGirls();
            weightDataWHO = d.getWeightForAgeGirls();

        }

    }

    private void plotGraph()
    {


    }


}