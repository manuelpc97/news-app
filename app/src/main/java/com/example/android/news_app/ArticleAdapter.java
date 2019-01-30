package com.example.android.news_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.news_app.NYTimes.Article;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.Integer.valueOf;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>{
    int number_articles;
    List<Article> articles;
    OkHttpClient client;
    Request request;
    View view;

    public ArticleClickListener listener;

    public interface ArticleClickListener{
        void onArticleClickListener(Article clickedArticle);
    }

    ArticleAdapter(int number, List<Article> articles, View view, ArticleClickListener listener){
        number_articles = number;
        this.articles =articles;
        client = new OkHttpClient();
        this.view = view;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        int layoutId = R.layout.article_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttach = false;

        View view = inflater.inflate(layoutId, viewGroup,shouldAttach);
        ArticleViewHolder viewHolder = new ArticleViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder viewHolder, int i) {
        viewHolder.bind(articles.get(i));
    }

    @Override
    public int getItemCount() {
        return number_articles;
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title_textview;
        ImageView image_view;
        Article article;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            title_textview = (TextView) itemView.findViewById(R.id.article_item_title);
            image_view = (ImageView) itemView.findViewById(R.id.article_item_image);
            itemView.setOnClickListener(this);
        }

        public void bind(Article article){
            this.article = article;
            title_textview.setText(this.article.title);
            if(this.article.multimedia.length > 0){
                Log.i("success", this.article.multimedia[0].url);
                setImage(this.article.multimedia[0].url,this.article.thumbnail_standard);
            }
        }

        public void setImage(String url, String thumbnail){
           Glide.with(view).load(url).thumbnail(Glide.with(view).load(thumbnail)).into(image_view);
        }

        @Override
        public void onClick(View v) {
            listener.onArticleClickListener(this.article);
        }
    }
}
