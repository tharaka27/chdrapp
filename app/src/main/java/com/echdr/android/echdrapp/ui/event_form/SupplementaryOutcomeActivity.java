package com.echdr.android.echdrapp.ui.event_form;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;
import com.echdr.android.echdrapp.service.Service.UnenrollmentService;
import com.echdr.android.echdrapp.service.Setter.DateSetter;
import com.echdr.android.echdrapp.service.Validator.OverweightOutcomeValidator;
import com.echdr.android.echdrapp.service.Validator.SupplementaryOutcomeValidator;
import com.echdr.android.echdrapp.service.util;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.processors.PublishProcessor;

public class SupplementaryOutcomeActivity extends AppCompatActivity {
    private static final String TAG = "SupplementaryOutcomeActivity";
    private String eventUid;
    private String programUid;
    private String selectedChild;
    private SupplementaryOutcomeActivity.FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private Spinner spinner_Enrollment;
    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;
    protected String[] other_type_array;
    protected String[] other_type_array_english;

    private TrackedEntityAttributeValue birthday;


    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               SupplementaryOutcomeActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, SupplementaryOutcomeActivity.class);
        intent.putExtra(SupplementaryOutcomeActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(SupplementaryOutcomeActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(SupplementaryOutcomeActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(SupplementaryOutcomeActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(SupplementaryOutcomeActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplementary_outcome);

        textView_Date = findViewById(R.id.editTextDateSuppOutcome);
        spinner_Enrollment = findViewById(R.id.supplementary_outcome_Enrollment_spinner);
        saveButton         = findViewById(R.id.supplementaryOutcomeSave);
        datePicker         = findViewById(R.id.supp_out_date_pick);

        context = this;

        eventUid = getIntent().getStringExtra(SupplementaryOutcomeActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(SupplementaryOutcomeActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(SupplementaryOutcomeActivity.IntentExtra.TEI_ID.name());
        formType = SupplementaryOutcomeActivity.FormType.valueOf(getIntent().getStringExtra(SupplementaryOutcomeActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(SupplementaryOutcomeActivity.IntentExtra.OU_UID.name());

        other_type_array = getResources().getStringArray(R.array.supp_outcome_type);
        other_type_array_english = getResources().getStringArray(R.array.supp_outcome_type_english);


        engineInitialization = PublishProcessor.create();

        birthday = Sdk.d2().trackedEntityModule().trackedEntityAttributeValues()
                .byTrackedEntityInstance().eq(selectedChild)
                .byTrackedEntityAttribute().eq("qNH202ChkV3")
                .one().blockingGet();
        //Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        String s_day          = (String) DateFormat.format("dd",   date); // 20
        String s_monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String s_year         = (String) DateFormat.format("yyyy", date); // 2013

        final int year = Integer.parseInt(s_year);
        final int month = Integer.parseInt(s_monthNumber);
        final int day = Integer.parseInt(s_day);

        DateSetter.setContext(context);
        DateSetter.setBirthday(birthday);
        DateSetter.setSetListener(setListener);
        DateSetter.setTextView(textView_Date);
        DateSetter.setImageView(datePicker);
        DateSetter.setDate(year, month, day, 365*5+2);

        //TODO test new util.setTextView and util.getSpinnerSelection

        util.setSpinner(context, spinner_Enrollment, R.array.supp_outcome_type);

        // Load the existing values - form.CHECK
        if(formType == SupplementaryOutcomeActivity.FormType.CHECK)
        {

            // set date
            util.setTextView(textView_Date, "lSy6bC6y3UD", eventUid);

            // set enrollment type
            spinner_Enrollment.setSelection(
                    util.getSpinnerSelection(eventUid,"rTHNOXde3wr", other_type_array));

        }
        else{
            textView_Date.setText(getString(R.string.date_button_text));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean wasSuccessful = saveElements();
                if(wasSuccessful) {
                    finishEnrollment();
                }
            }
        });


        if (EventFormService.getInstance().init(
                Sdk.d2(),
                eventUid,
                programUid,
                getIntent().getStringExtra(SupplementaryOutcomeActivity.IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();
    }

    @SuppressLint("LongLogTag")
    private boolean saveElements()
    {
        SupplementaryOutcomeValidator supplementaryOutcomeValidator = new SupplementaryOutcomeValidator();
        supplementaryOutcomeValidator.setTextView_Date(textView_Date);
        supplementaryOutcomeValidator.setContext(context);
        supplementaryOutcomeValidator.setSpinner_Enrollment(spinner_Enrollment);
        supplementaryOutcomeValidator.setBirthday(birthday);

        if(!supplementaryOutcomeValidator.validate()){
            Log.e(TAG, "Validation failure" );
            return false;
        }

        Map<String, String> dataElements = new HashMap<>();
        dataElements.put("lSy6bC6y3UD", textView_Date.getText().toString());
        dataElements.put("rTHNOXde3wr", other_type_array_english[spinner_Enrollment.getSelectedItemPosition()]);

        UnenrollmentService.setSelectedChild(selectedChild);
        UnenrollmentService.setContext(context);

        UnenrollmentService.unenroll( textView_Date.getText().toString(),
                other_type_array_english[spinner_Enrollment.getSelectedItemPosition()]
                ,getString(R.string.unenroll_supplimentory), dataElements, "tc6RsYbgGzm",
                eventUid, orgUnit, engineInitialization,
                ()->{
                    finishEnrollment();
                    return null;
                });

        return UnenrollmentService.isSuccessfulUnenrollment();
    }

    @Override
    protected void onResume(){
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

    private void finishEnrollment() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (formType == SupplementaryOutcomeActivity.FormType.CREATE)
            EventFormService.getInstance().delete();
        setResult(RESULT_CANCELED);
        finish();
    }

}
