package com.example.android.news_app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VideoDescrptionFragment extends Fragment {

    TextView description_textview;
    TextView title_textview;
    TextView channel_textview;

    String title;
    String description;
    String channel;

    public VideoDescrptionFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_description_fragment, container, false);

        description_textview = (TextView) view.findViewById(R.id.video_description_textview);
        title_textview = (TextView) view.findViewById(R.id.video_title_textview);
        channel_textview = (TextView) view.findViewById(R.id.video_channel_textview);

        Bundle bundle= getArguments();
        title = bundle.getString("TITLE");
        description = bundle.getString("DESCRIPTION");
        channel = bundle.getString("CHANNEL");

        description_textview.setText(description);
        title_textview.setText(title);
        channel_textview.setText(channel);

        return view;
    }
}
