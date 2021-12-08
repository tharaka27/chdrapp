package com.echdr.android.echdrapp.ui.splash;

import androidx.appcompat.app.AppCompatActivity;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.service.ActivityStarter;
import com.echdr.android.echdrapp.ui.main.MainActivity;
import com.echdr.android.echdrapp.ui.tracked_entity_instances.ChildDetailsActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class LanguageSelection extends AppCompatActivity {
    private Spinner languageSpinner;
    protected String[] languageArray;
    private Context context;
    private Button languageButton;



    public static Intent getLanguageSelectionActivityIntent(Context context) {
        Intent intent = new Intent(context, LanguageSelection.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        languageSpinner = findViewById(R.id.language_selection_spinner);
        languageButton = findViewById(R.id.saveLanguage);
        context = this;


        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(context,
                R.array.language,
                android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setOnItemSelectedListener(new LanguageTypeSpinnerClass());

        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityStarter.startActivity(LanguageSelection.this, MainActivity.getMainActivityIntent(context),true);
            }
        });
    }

    class LanguageTypeSpinnerClass implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            //Toast.makeText(v.getContext(), "Your choose :" +
            //sexArray[position], Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void EnglishSelected()
    {
        System.out.println("Language selected");
    }
}