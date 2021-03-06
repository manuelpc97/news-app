package com.example.android.news_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Detail extends Fragment {

    WebView webview;
    String url;
    ProgressBar loading;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.article_detail, container, false);

        webview = (WebView) rootView.findViewById(R.id.article_webview);
        loading = rootView.findViewById(R.id.pb2);
        loading.setVisibility(View.VISIBLE);
        url = getArguments().getString("URL");
        loading.setVisibility(View.INVISIBLE);
        webview.loadUrl(url);
        return rootView;
    }
}
