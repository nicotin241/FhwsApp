package de.android.fhwsapp.webView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import de.android.fhwsapp.LoginActivity;
import de.android.fhwsapp.R;
import de.android.fhwsapp.pdfDownloaderViewer.PdfViewer;

import static android.content.ContentValues.TAG;

public class MyWebView extends Fragment implements View.OnClickListener {

    private WebView webView = null;
    private Button btnDownloadView;
    private boolean didOnce = false;
    private boolean imma = false;
    private View view;
    private String url;
    private String js;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_webview, container, false);

        url = getArguments().getString("url");
        js = getArguments().getString("js");

        webView = (WebView) view.findViewById(R.id.webView);
        btnDownloadView = (Button) view.findViewById(R.id.btnDownloadView);

        btnDownloadView.setOnClickListener(this);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url2) {
                super.onPageFinished(view, url2);

                if(didOnce) {

                    //wird verwendet um von der Startseite nach dem Login zu der eigentlichen Seite zu gelangen
                    if(!url2.equals(url))
                        webView.loadUrl(url);

                    //button logik
                    if(url.equals("https://studentenportal.fhws.de/history")){
                        btnDownloadView.setText(btnDownloadView.getText()+"\nStudienverlauf");
                        btnDownloadView.setVisibility(View.VISIBLE);
                    } else if(url.equals("https://studentenportal.fhws.de/cert")){
                        btnDownloadView.setText(btnDownloadView.getText()+"\nImmatrikulationsbescheinigung\n");
                        btnDownloadView.setVisibility(View.VISIBLE);
                        imma = true;
                    }

                    return;
                }

                didOnce = true;

                //führt bei login screen das javascript aus
                view.loadUrl(js);

            }
        });

        return view;
    }

    public void prepareDownload(){
        if(imma) {

            //get Semester

            final String folderName = "Immatrikulationsbescheinigung";

            String myJsString = "document.getElementsByName('semester')[0].options[document.getElementsByName('semester')[0].selectedIndex].value";

            if (android.os.Build.VERSION.SDK_INT >= 19) {
                webView.evaluateJavascript("(function() { return getStringToMyAndroid('" + myJsString + "'); })();", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String semester) {
                        downloadPdf("https://studentenportal.fhws.de/cert/pdf?semester="+semester,folderName,semester);
                    }
                });
            }else{
                WebChromeClient MyWebChromeClient = new WebChromeClient() {
                    @Override
                    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                        try {
                            String semester = java.net.URLDecoder.decode(message, "UTF-8");
                            downloadPdf("https://studentenportal.fhws.de/cert/pdf?semester="+semester,folderName,semester);
                        } catch (UnsupportedEncodingException e) {
                            Toast.makeText(getContext(),"Es ist leider ein Fehler aufgetreten",Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                };

                webView.setWebChromeClient(MyWebChromeClient);

                webView.loadUrl("javascript:" +"alert("+myJsString+")");
            }
        }else{
            downloadPdf("https://studentenportal.fhws.de/history/pdf", "Studienverlauf","studienverlauf");
        }
    }

    private void downloadPdf(String url, String folderName, String fileName){
        new PdfViewer(getContext(),getActivity()).viewPdf(url,folderName,fileName);
    }

    @Override
    public void onClick(View v) {
        try {
            prepareDownload();
        }catch (Exception e){
            Toast.makeText(getContext(),"Es ist leider ein Fehler aufgetreten",Toast.LENGTH_LONG).show();
        }
    }
}
