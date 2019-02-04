package com.example.android.news_app;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.news_app.Youtube.Resource;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {

    public List<Resource> videos;
    public VideoClickListener listener;

    public VideosAdapter (List<Resource> videos, VideoClickListener listener){
        this.videos = videos;
        this.listener = listener;
    }


    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_card,viewGroup,false);
        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosViewHolder videosViewHolder, int i) {
        videosViewHolder.bind(videos.get(i));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }



    public interface VideoClickListener{
        void onVideoClickListener(Resource clickedVideo);
    }

    public class VideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView titleTextView;
        ImageView imageview;
        View view;
        Resource video;

        public VideosViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            titleTextView = (TextView)view.findViewById(R.id.video_title);
            imageview = (ImageView)view.findViewById(R.id.video_image);
            itemView.setOnClickListener(this);
        }

        public void bind(Resource video){
            this.video = video;
            titleTextView.setText(this.video.snippet.title);
            if(this.video.snippet.thumbnails.medium != null){
                Glide.with(view).load(this.video.snippet.thumbnails.medium.url).into(imageview);
            }else{
                Glide.with(view).load("https://upload.wikimedia.org/wikipedia/commons/e/e1/Logo_of_YouTube_%282015-2017%29.svg").into(imageview);
            }
        }

        @Override
        public void onClick(View v) {
            listener.onVideoClickListener(this.video);
        }
    }
}
