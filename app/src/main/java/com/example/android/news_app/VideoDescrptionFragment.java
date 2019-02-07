package com.example.android.news_app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class VideoDescrptionFragment extends Fragment {

    TextView description_textview;
    TextView title_textview;

    String title;
    String description;
    String image_url;

    public VideoDescrptionFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_description_fragment, container, false);

        description_textview = (TextView) view.findViewById(R.id.video_description_textview);
        title_textview = (TextView) view.findViewById(R.id.video_title_textview);

        Bundle bundle= getArguments();
        title = bundle.getString("TITLE");
        description = bundle.getString("DESCRIPTION");
        image_url = bundle.getString("URL");

        description_textview.setText(description);
        title_textview.setText(title);
        return view;
    }
}
