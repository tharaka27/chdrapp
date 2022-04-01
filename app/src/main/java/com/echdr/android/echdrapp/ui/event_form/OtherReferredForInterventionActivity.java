package com.echdr.android.echdrapp.ui.event_form;

import static android.text.TextUtils.isEmpty;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.forms.EventFormService;
import com.echdr.android.echdrapp.data.service.forms.RuleEngineService;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueObjectRepository;

import java.util.Date;
import java.util.List;

import io.reactivex.processors.PublishProcessor;

public class OtherReferredForInterventionActivity extends AppCompatActivity {

    private String eventUid;
    private String programUid;
    private String selectedChild;
    private OtherReferredForInterventionActivity.FormType formType;
    private String orgUnit;

    private TextView textView_Date;
    private CheckBox checkbox_Food_Insecurity;
    private CheckBox checkbox_Inadequate_Water;
    private CheckBox checkbox_Poor_Income;


    private Button saveButton;
    private ImageView datePicker;

    private PublishProcessor<Boolean> engineInitialization;
    private RuleEngineService engineService;
    private DatePickerDialog.OnDateSetListener setListener;
    private Context context;

    private enum IntentExtra {
        EVENT_UID, PROGRAM_UID, OU_UID, TYPE, TEI_ID
    }

    public enum FormType {
        CREATE, CHECK
    }

    public static Intent getFormActivityIntent(Context context, String eventUid,
                                               String programUid, String orgUnitUid,
                                               OtherReferredForInterventionActivity.FormType type, String teiID) {
        Intent intent = new Intent(context, OtherReferredForInterventionActivity.class);
        intent.putExtra(OtherReferredForInterventionActivity.IntentExtra.EVENT_UID.name(), eventUid);
        intent.putExtra(OtherReferredForInterventionActivity.IntentExtra.PROGRAM_UID.name(), programUid);
        intent.putExtra(OtherReferredForInterventionActivity.IntentExtra.OU_UID.name(), orgUnitUid);
        intent.putExtra(OtherReferredForInterventionActivity.IntentExtra.TYPE.name(), type.name());
        intent.putExtra(OtherReferredForInterventionActivity.IntentExtra.TEI_ID.name(), teiID);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_referred_for_intervention);

        textView_Date = findViewById(R.id.otherinterventionDate);
        datePicker         = findViewById(R.id.refferd_for_date_pick);
        checkbox_Poor_Income  = findViewById(R.id.poor_income_Checkbox);
        checkbox_Food_Insecurity = findViewById(R.id.food_insecurity_Checkbox);
        checkbox_Inadequate_Water  = findViewById(R.id.inadequate_water_Checkbox);
        //saveButton         = findViewById(R.id.otherrefferedForSave);

        context = this;

        eventUid = getIntent().getStringExtra(OtherReferredForInterventionActivity.IntentExtra.EVENT_UID.name());
        programUid = getIntent().getStringExtra(OtherReferredForInterventionActivity.IntentExtra.PROGRAM_UID.name());
        selectedChild = getIntent().getStringExtra(OtherReferredForInterventionActivity.IntentExtra.TEI_ID.name());
        formType = OtherReferredForInterventionActivity.FormType.valueOf(getIntent().getStringExtra(OtherReferredForInterventionActivity.IntentExtra.TYPE.name()));
        orgUnit = getIntent().getStringExtra(OtherReferredForInterventionActivity.IntentExtra.OU_UID.name());


        engineInitialization = PublishProcessor.create();


        Date date = new Date();
        String s_day          = (String) DateFormat.format("dd",   date); // 20
        String s_monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String s_year         = (String) DateFormat.format("yyyy", date); // 2013

        final int year = Integer.parseInt(s_year);
        final int month = Integer.parseInt(s_monthNumber);
        final int day = Integer.parseInt(s_day);

        /*
        textView_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicked et date");
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context, android.R.style.Theme_Holo_Light_Dialog, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(year, month, day);
            }
        });

         *

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;


                String date = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", dayOfMonth) ;
                textView_Date.setText(date);
            }
        };
        System.out.println("[INFO] Outside check other reffered for intervention");

         */

        if(formType == OtherReferredForInterventionActivity.FormType.CHECK)
        {
            System.out.println("[INFO] Inside check other reffered for intervention");
            System.out.println(getDataElement("eS218sAeBrF")); // food insecurity
            System.out.println(getDataElement("u42hzwuTyyD")); // inadequate water sanitation
            System.out.println(getDataElement("VjMbDm82Aoi")); // poor income
            System.out.println(getDataElement("iOPlxAXrenG")); // intervention date

            // set date
            try{
                String prev_date = getDataElement("iOPlxAXrenG");
                if(!prev_date.isEmpty())
                {
                    textView_Date.setText(prev_date);
                }
            }
            catch (Exception e)
            {
                textView_Date.setText("");
            }

            // set poor income
            try{
                if(getDataElement("VjMbDm82Aoi").equals("true"))
                {
                    checkbox_Poor_Income.setChecked(true);
                }
                else{
                    checkbox_Poor_Income.setChecked(false);
                }
            }
            catch (Exception e)
            {
                checkbox_Poor_Income.setChecked(false);
            }

            // set food insecurity
            try{
                if(getDataElement("eS218sAeBrF").equals("true"))
                {
                    checkbox_Food_Insecurity.setChecked(true);
                }
                else {
                    checkbox_Food_Insecurity.setChecked(false);
                }
            }
            catch (Exception e)
            {
                checkbox_Food_Insecurity.setChecked(false);
            }

            // set inadequate water
            try{
                if(getDataElement("u42hzwuTyyD").equals("true"))
                {
                    checkbox_Inadequate_Water.setChecked(true);
                }
                else
                {
                    checkbox_Inadequate_Water.setChecked(false);
                }
            }
            catch (Exception e)
            {
                checkbox_Inadequate_Water.setChecked(false);
            }

        }
        else{
            textView_Date.setText("Click here to set Date");
        }

        /*
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveElements();
            }
        });

         */

        if (EventFormService.getInstance().init(
                Sdk.d2(),
                eventUid,
                programUid,
                getIntent().getStringExtra(OtherReferredForInterventionActivity.IntentExtra.OU_UID.name())))
            this.engineService = new RuleEngineService();
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
        if (formType == OtherReferredForInterventionActivity.FormType.CREATE)
            EventFormService.getInstance().delete();
        setResult(RESULT_CANCELED);
        finish();
    }

    private String getDataElement(String dataElement){
        TrackedEntityDataValueObjectRepository valueRepository =
                Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                        .value(
                                eventUid,
                                dataElement
                        );

        String currentValue = valueRepository.blockingExists() ?
                valueRepository.blockingGet().value() : "";

        return currentValue;
    }


    private void saveDataElement(String dataElement, String value){
        TrackedEntityDataValueObjectRepository valueRepository;
        try {
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                    .value(
                            EventFormService.getInstance().getEventUid(),
                            dataElement
                    );
        }catch (Exception e)
        {
            EventFormService.getInstance().init(
                    Sdk.d2(),
                    eventUid,
                    programUid,
                    getIntent().getStringExtra(OtherReferredForInterventionActivity.IntentExtra.OU_UID.name()));
            valueRepository = Sdk.d2().trackedEntityModule().trackedEntityDataValues()
                    .value(
                            EventFormService.getInstance().getEventUid(),
                            dataElement
                    );
        }

        String currentValue = valueRepository.blockingExists() ?
                valueRepository.blockingGet().value() : "";

        if (currentValue == null)
            currentValue = "";

        try{
            if(!isEmpty(value))
            {
                valueRepository.blockingSet(value);
            }else
            {
                valueRepository.blockingDeleteIfExist();
            }
        } catch (D2Error d2Error) {
            d2Error.printStackTrace();
        }finally {
            if (!value.equals(currentValue)) {
                engineInitialization.onNext(true);
            }
        }
    }



    private void saveElements()
    {
        if(textView_Date.getText().toString().equals("Click here to set Date")||
                textView_Date.getText().toString().isEmpty())
        {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Date Not Selected");
            builder1.setCancelable(true);

            builder1.setNegativeButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            return;
        }




        // un-enroll from the program
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setIcon(R.drawable.baby_girl);
        builderSingle.setTitle("Proceed to un-enroll ?");
        builderSingle.setMessage("This action will un-enroll the child \n" +
                "from the other health/non-health program");

        builderSingle.setNegativeButton("un-enroll", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                // get latest enrollment
                List<Enrollment> enrollmentStatus = Sdk.d2().enrollmentModule().enrollments()
                        .byTrackedEntityInstance().eq(selectedChild)
                        .byProgram().eq("iUgzznPsePB")
                        .orderByCreated(RepositoryScope.OrderByDirection.DESC)
                        .blockingGet();

                String enrollmentID = "";

                if(!enrollmentStatus.isEmpty())
                {
                    enrollmentID = enrollmentStatus.get(0).uid();
                }

                EnrollmentObjectRepository rep = Sdk.d2().enrollmentModule().enrollments()
                        .uid(enrollmentID);
                try {
                    rep.setStatus(EnrollmentStatus.COMPLETED);


                    System.out.println(getDataElement("eS218sAeBrF")); // food insecurity
                    System.out.println(getDataElement("u42hzwuTyyD")); // inadequate water sanitation
                    System.out.println(getDataElement("VjMbDm82Aoi")); // poor income
                    System.out.println(getDataElement("iOPlxAXrenG")); // intervention date

                    saveDataElement("iOPlxAXrenG", textView_Date.getText().toString());
                    saveDataElement("eS218sAeBrF", checkbox_Food_Insecurity.isChecked() ? "true" : "");
                    saveDataElement("u42hzwuTyyD", checkbox_Inadequate_Water.isChecked() ? "true" : "");
                    saveDataElement("VjMbDm82Aoi", checkbox_Poor_Income.isChecked() ? "true" : "");

                    finishEnrollment();

                } catch (D2Error d2Error) {
                    d2Error.printStackTrace();
                    Toast.makeText(context, "Un-enrolling unsuccessful",
                            Toast.LENGTH_LONG).show();
                }


                dialog.dismiss();
                return;
            }
        });

        builderSingle.show();


    }

/*
    private void selectDate(int year, int month, int day)
    {
        System.out.println("Clicked et date");
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, android.R.style.Theme_Holo_Light_Dialog, setListener, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private String makeDate(int year, int month, int DayofMonth)
    {
        return DayofMonth + "/" + month + "/" + year;
    }

 */


}



