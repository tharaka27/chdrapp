package com.echdr.android.echdrapp.ui.splash;

import androidx.appcompat.app.AppCompatActivity;
import com.echdr.android.echdrapp.LocaleHelper;
import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.service.ActivityStarter;
import com.echdr.android.echdrapp.service.Constants;
import com.echdr.android.echdrapp.ui.main.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class LanguageSelection extends AppCompatActivity {
    private Context context;
    private Button btnSinhala, btnEnglish, btnTamil;

    public static Intent getLanguageSelectionActivityIntent(Context context) {
        Intent intent = new Intent(context, LanguageSelection.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        btnSinhala = findViewById(R.id.btnHindi);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnTamil = findViewById(R.id.btnTamil);

        changeLanguage(btnEnglish, Constants.APP_LANGUAGE_ENGLISH);
        changeLanguage(btnSinhala, Constants.APP_LANGUAGE_SINHALA);
        changeLanguage(btnTamil, Constants.APP_LANGUAGE_TAMIL);

    }

    void changeLanguage(Button button, String Language){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = LocaleHelper.setLocale(LanguageSelection.this, Language);
                ActivityStarter.startActivity(LanguageSelection.this, MainActivity.getMainActivityIntent(context),true);
            }
        });
    }

}