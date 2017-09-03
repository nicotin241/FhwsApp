package de.android.fhwsapp.webView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

public class MyWebView extends Fragment {

    private WebView webView = null;
    private View view;
    private String url;
    private String js;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_webview, container, false);

        url = getArguments().getString("url");
        js = getArguments().getString("js");

        webView = (WebView) view.findViewById(R.id.webView);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = mPrefs.edit();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url2) {
                super.onPageFinished(view, url2);

                String myCookies = CookieManager.getInstance().getCookie(url);
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

                }

            }
        });

        return view;
    }

    private void downloadPdf(String url, String folderName, String fileName) {
        new PdfViewer(getContext(), getActivity()).viewPdf(url, folderName, fileName);
    }

}
