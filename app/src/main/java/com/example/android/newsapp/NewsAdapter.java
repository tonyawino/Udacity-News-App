package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(@NonNull Context context, List<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    //Return individual inflated views
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyViewHolder viewHolder = new MyViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_news, parent, false);
            viewHolder.imageNews = convertView.findViewById(R.id.image_news);
            viewHolder.textAuthor = convertView.findViewById(R.id.text_author);
            viewHolder.textDate = convertView.findViewById(R.id.text_date);
            viewHolder.textSection = convertView.findViewById(R.id.text_section);
            viewHolder.textTitle = convertView.findViewById(R.id.text_title);
            convertView.setTag(viewHolder);
        }
        viewHolder = (MyViewHolder) convertView.getTag();
        News news = getItem(position);
        viewHolder.textTitle.setText(news.getTitle());
        viewHolder.textSection.setText(news.getSection());
        //If the date is available, set it, otherwise hide the view
        if (news.getDate() != null) {
            viewHolder.textDate.setText(news.getDate());
            viewHolder.textDate.setVisibility(View.VISIBLE);
        } else
            viewHolder.textDate.setVisibility(View.GONE);
        //If the author is available, set it, otherwise hide the view
        if (news.getAuthor().equals("") || news.getAuthor() == null)
            viewHolder.textAuthor.setVisibility(View.GONE);
        else {
            viewHolder.textAuthor.setVisibility(View.VISIBLE);
            viewHolder.textAuthor.setText(news.getAuthor());
        }
        //If there is an image, set it, otherwise hide the view
        if (news.getImage() == null)
            viewHolder.imageNews.setVisibility(View.GONE);
        else {
            viewHolder.imageNews.setVisibility(View.VISIBLE);
            viewHolder.imageNews.setImageBitmap(news.getImage());
        }
        return convertView;
    }

    //Holds the views
    static class MyViewHolder {
        ImageView imageNews;
        TextView textSection;
        TextView textTitle;
        TextView textAuthor;
        TextView textDate;
    }
}
