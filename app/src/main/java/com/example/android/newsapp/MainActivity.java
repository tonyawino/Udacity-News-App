package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private String mUrl = "https://content.guardianapis.com/search?show-tags=contributor&show-fields=thumbnail&api-key=test";
    private ListView listView;
    private ProgressBar progressBar;
    private TextView no_items;
    private SharedPreferences sharedPreferences;

    //Creates URLs if the provided string is valid
    public static URL createUrl(String url) {
        URL myUrl = null;
        if (url == null || url.equals("")) {
            return null;
        } else {
            try {
                myUrl = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return myUrl;
    }

    //Returns the JSON response from an InputStream
    public static String getJson(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    //Returns a list of news items from a JSON object
    public static List<News> getNews(String json) {
        ArrayList<News> list = new ArrayList<>();
        //Return null if no JSON objects are available
        if (json == null || json.equals(""))
            return null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject response = jsonObject.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String title = result.getString("webTitle");
                String section = result.getString("sectionName");
                String date = result.getString("webPublicationDate");
                JSONObject fields = result.optJSONObject("fields");
                URL thumbnail;
                if (fields != null)
                    thumbnail = createUrl(fields.optString("thumbnail"));//Get the thumbnail URL if the Fields JSONObject is not null
                else
                    thumbnail = null;
                JSONArray tags = result.optJSONArray("tags");
                String author;
                if (tags.length() < 1)
                    author = "";//If the tags JSONArray is empty, set the author to empty string
                else {
                    JSONObject tag = tags.getJSONObject(0);
                    author = tag.getString("webTitle");//If the tags JSONArray is not empty, get the author
                }
                URL url = createUrl(result.getString("webUrl"));//Get the website URL for the story
                //If the thumbnail is empty, pass null, otherwise create the thumbnail
                list.add(new News(title, section, author, date, thumbnail == null ? null : BitmapFactory.decodeStream(thumbnail.openStream()), url));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_bar);
        listView = findViewById(R.id.list);
        no_items = findViewById(R.id.text_no_items);
        listView.setEmptyView(no_items);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        PreferenceManager.setDefaultValues(this, R.xml.activity_settings, false);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        //If connected to the internet, fetch items, otherwise notify user of no connection
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            no_items.setVisibility(View.GONE);
            getSupportLoaderManager().initLoader(0, null, this).forceLoad();
        } else {
            progressBar.setVisibility(View.GONE);
            no_items.setText(R.string.no_internet);
            no_items.setVisibility(View.VISIBLE);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            //Open web page with the news when news item is clicked
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News) parent.getAdapter().getItem(position);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(news.getUrl())));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int i, @Nullable Bundle bundle) {
        //Read user settings for number of news items to display and preferred sections
        String number_of_news_items = sharedPreferences.getString(getString(R.string.settings_news_items_key), getString(R.string.default_number));
        String home_section = sharedPreferences.getString(getString(R.string.settings_home_key), getString(R.string.default_home));
        Uri.Builder builder = Uri.parse(mUrl).buildUpon();
        builder.appendQueryParameter("page-size", number_of_news_items);
        builder.appendQueryParameter("section", home_section);
        return new NewsLoader(this, builder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> news) {
        //Hide the progressbar when finished and add items to the listview
        progressBar.setVisibility(View.GONE);
        no_items.setText(R.string.no_items);
        listView.setAdapter(new NewsAdapter(this, news));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        //Make the listview empty
        listView.setAdapter(new NewsAdapter(this, null));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Create the menu
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Go to settings
        if (id == R.id.menu_settings)
            startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Fetch data again using the new settings
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);
        getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
    }
}
