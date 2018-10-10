package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private String mUrl;

    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Nullable
    @Override
    //Connect to the internet and fetch the data
    public List<News> loadInBackground() {
        URL url = MainActivity.createUrl(mUrl);
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        String json = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            //If it does not connect, log the message, otherwise read from the inputstream
            if (urlConnection.getResponseCode() != 200)
                Log.e("Unable to connect", "Unable to connect");
            else {
                inputStream = urlConnection.getInputStream();
                json = MainActivity.getJson(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Disconnect when finished, and close the inputstream
        if (urlConnection != null)
            urlConnection.disconnect();
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException eo) {
                eo.printStackTrace();
            }
        }
        return MainActivity.getNews(json);
    }
}
