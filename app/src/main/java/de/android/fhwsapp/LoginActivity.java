package de.android.fhwsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    public static final String K_NUMBER = "kNumber";
    public static final String PASSWORD = "password";


    private SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;
    private Context mContext;
    private ProgressDialog mDialog;

    private boolean didOnce = false;
    private String url, js;

    // UI references.
    private EditText mK_Nummer;
    private EditText mPasswordView;
    private Button signInButton;
    private Button skip_login_button;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        initLayout();
        clearCookies();

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = mPrefs.edit();

    }

    private void login() {

        editor.putBoolean("signedIn", true);
        editor.putString(K_NUMBER, mK_Nummer.getText().toString());
        editor.putString(PASSWORD, mPasswordView.getText().toString());
        editor.apply();

        startActivity(new Intent(this, MainActivity.class));

        this.finish();

    }

    private void initLayout() {

        setTitle("Anmelden");

        mK_Nummer = (EditText) findViewById(R.id.kNummer);
        mPasswordView = (EditText) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.email_sign_in_button);
        skip_login_button = (Button) findViewById(R.id.skip_login_button);

        mDialog = new ProgressDialog(mContext);

        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inputOk()) {

                    mDialog.setMessage("Anmelden...");
                    mDialog.show();
                    didOnce = false;
                    checkLoginData();

                }

            }
        });

        skip_login_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                editor.putBoolean("signedIn", true);
                editor.apply();

                startActivity(new Intent(mContext, MainActivity.class));
                finish();

            }
        });

    }

    private boolean inputOk() {

        String testString = mK_Nummer.getText().toString();

        if (testString.equals("")) {
            makeToast("Bitte gib deine k-Nummer ein, z.B. k12345.");
            return false;
        } else if (!testString.startsWith("k")) {
            makeToast("Deine k-Nummer muss mit k beginnen.");
            return false;
        } else if (testString.length() > 6) {
            makeToast("Deine k-Nummer ist zu lang.");
            return false;
        } else if (testString.length() < 6) {
            makeToast("Deine k-Nummer ist zu kurz.");
            return false;
        } else if (mPasswordView.getText().toString().equals("")) {
            makeToast("Bitte gib dein Passwort ein.");
            return false;
        } else return true;

    }

    private void makeToast(final String text) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void checkLoginData() {

        url = "https://studentenportal.fhws.de/home";
        js = "javascript:" +
                "document.getElementsByName('password')[0].value = '" + mPasswordView.getText().toString() + "';" +
                "document.getElementsByName('username')[0].value = '" + mK_Nummer.getText().toString() + "';" +
                "document.getElementsByClassName('btn btn-primary')[0].click()";

        webView = (WebView) findViewById(R.id.loginWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url2) {

                super.onPageFinished(view, url2);

                if (url2.equals("https://studentenportal.fhws.de/login")) {

                    if (didOnce) {

                        stopDialog();
                        Toast.makeText(mContext, "Passwort oder k-Nummer falsch!", Toast.LENGTH_SHORT).show();

                    } else {

                        didOnce = true;
                        view.loadUrl(js);
                    }

                } else if (url2.equals(url)) {

                    stopDialog();
                    Toast.makeText(mContext, "Login erfolgreich!", Toast.LENGTH_SHORT).show();
                    login();

                }
            }
        });

    }

    private void stopDialog() {

        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }

    }

    public static void clearCookies() {

        android.webkit.CookieManager cookieManager = CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                // a callback which is executed when the cookies have been removed
                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    Log.d(TAG, "Cookie removed: " + aBoolean);
                }
            });
        } else cookieManager.removeAllCookie();

    }

}

