package com.echdr.android.echdrapp.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.data.service.ActivityStarter;
import com.echdr.android.echdrapp.ui.main.MainActivity;
import com.echdr.android.echdrapp.ui.programs.ProgramsActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.disposables.Disposable;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private Disposable disposable;

    private String serverUrlEditText;
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;

    public static Intent getLoginActivityIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_yash);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        serverUrlEditText = "https://www.erhmis.fhb.health.gov.lk/erhmis2356/api/";
        usernameEditText = findViewById(R.id.username_yash);
        passwordEditText = findViewById(R.id.password_yash);
        loginButton = findViewById(R.id.btnLogin);
        //loadingProgressBar = findViewById(R.id.loginProgressBar);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            //loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                if (Sdk.d2().programModule().programs().blockingCount() > 0) {
                    ActivityStarter.startActivity(this, ProgramsActivity.getProgramActivityIntent(this),true);
                } else {
                    ActivityStarter.startActivity(this, MainActivity.getMainActivityIntent(this),true);
                }
            }
            setResult(Activity.RESULT_OK);
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(
                        //serverUrlEditText.getText().toString()
                        serverUrlEditText,
                        usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };

        //serverUrlEditText.addTextChangedListener(afterTextChangedListener);
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
            }
            return false;
        });

        loginButton.setOnClickListener(v -> login());


    }

    private void login() {
        //loadingProgressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        //String serverUrl = serverUrlEditText.getText().toString();
        String serverUrl = serverUrlEditText;

        disposable = loginViewModel
                .login(username, password, serverUrl)
                .doOnTerminate(() -> loginButton.setVisibility(View.VISIBLE))
                .subscribe(u -> {}, t -> {});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }


    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}