package de.android.fhwsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    public static final String K_NUMBER = "kNumber";
    public static final String PASSWORD = "password";


    private SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;

    private Context mContext;

    // UI references.
    private EditText mK_Nummer;
    private EditText mPasswordView;
    private Button signInButton;
    private Button skip_login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        initLayout();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = mPrefs.edit();

    }

    private void login() {

        editor.putBoolean("signedIn", true);
        editor.putString(K_NUMBER,mK_Nummer.getText().toString());
        editor.putString(PASSWORD,mPasswordView.getText().toString());
        editor.apply();

        startActivity(new Intent(this, MainActivity.class));

        this.finish();

    }

    private void initLayout() {

        setTitle("Anmelden");

        mK_Nummer = (EditText) findViewById(R.id.kNummer);
        mPasswordView = (EditText) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.email_sign_in_button);
        skip_login_button  = (Button) findViewById(R.id.skip_login_button);

        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        skip_login_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

}

