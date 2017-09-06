package de.android.fhwsapp.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import de.android.fhwsapp.R;
import de.android.fhwsapp.pdfDownloaderViewer.PdfViewer;

public class WebViewFragment extends Fragment {

    private WebView webView = null;
    private View view;
    private String url;
    private String js;
    private String[] savedDataForPermissionRequest;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;
    private ProgressDialog mDialog;

    public static String URL;
    public static String JS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_webview, container, false);

//        url = getArguments().getString("url");
//        js = getArguments().getString("js");

        url = URL;
        js = JS;

        webView = (WebView) view.findViewById(R.id.webView);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = mPrefs.edit();
        mDialog = new ProgressDialog(getContext());

        mDialog.setMessage("Lade Daten...");
        mDialog.show();
        webView.setVisibility(View.INVISIBLE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url2) {
                super.onPageFinished(view, url2);

                String myCookies = CookieManager.getInstance().getCookie(url2);
                editor.putString("Cookie", myCookies);
                editor.apply();

                if (url2.equals("https://studentenportal.fhws.de/login")) {

                    //führt bei login screen das javascript aus
                    view.loadUrl(js);

                } else if(url2.contains("pdf?semester=")) {

                    String semester = url2.substring(url2.length() - 6);
                    downloadPdf(url2, "FHWS-Dokumente", "Immatrikulation " + semester);

                } else if(url2.contains("history/pdf")) {

                    downloadPdf(url2, "FHWS-Dokumente", "Studienverlauf");

                } else if(url2.contains("grades/pdf")) {

                    downloadPdf(url2, "FHWS-Dokumente", "Notenauszug");

                } else if(!url2.equals(url)) {

                    webView.loadUrl(url);

                } else {

                    stopDialog();

                }

            }
        });

        return view;
    }

    private void stopDialog() {

        if (mDialog != null) {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
        webView.setVisibility(View.VISIBLE);

    }

    private void downloadPdf(String url, String folderName, String fileName) {

        if(checkIfAlreadyhavePermission()) new PdfViewer(getContext(), getActivity()).viewPdf(url, folderName, fileName);
        else {

            savedDataForPermissionRequest = new String[] {url, folderName, fileName };
            requestForSpecificPermission();
        }

    }

    private boolean checkIfAlreadyhavePermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(savedDataForPermissionRequest != null) {

                        new PdfViewer(getContext(), getActivity()).viewPdf(savedDataForPermissionRequest[0], savedDataForPermissionRequest[1], savedDataForPermissionRequest[2]);

                    }

                } else {

                    Toast.makeText(getContext(), "Berechtigungen werden benötigt, um die Dokumente zu speichern.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
