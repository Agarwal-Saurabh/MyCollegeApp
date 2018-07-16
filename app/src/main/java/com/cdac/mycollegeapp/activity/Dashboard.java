package com.cdac.mycollegeapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cdac.mycollegeapp.R;
import com.cdac.mycollegeapp.helper.DownloadHelper;
import com.cdac.mycollegeapp.helper.URLHelper;
import com.cdac.mycollegeapp.pojo.AdvertisementPojo;
import com.cdac.mycollegeapp.pojo.BookPojo;
import com.cdac.mycollegeapp.pojo.CollegeUpdatesPojo;
import com.cdac.mycollegeapp.pojo.NewsPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.width;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ArrayList<NewsPojo> newsPojoArrayList = new ArrayList<>();
    ArrayList<BookPojo> bookPojoArrayList = new ArrayList<>();
    ArrayList<CollegeUpdatesPojo> collegeUpdatesPojoArrayList = new ArrayList<>();
    ArrayList<AdvertisementPojo> advertisementPojoArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        navigationDrawerCreate(toolbar);
        fetchDataFromServer();
    }

    private void navigationDrawerCreate(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        ImageView img = (ImageView) header.findViewById(R.id.imageView);
        TextView navName = (TextView) header.findViewById(R.id.nav_name);
        TextView navMail = (TextView) header.findViewById(R.id.textView);

        SharedPreferences preferences =
                getSharedPreferences("MyFile", MODE_PRIVATE);

        try {
            JSONObject object = new JSONObject(preferences.getString("info", ""));
          navMail.setText(object.getString("email"));
           navName.setText(object.getString("name"));
            Log.d("234",object.getString("email")+object.getString("name"));
            DownloadHelper.loadImageWithGlideURL(this, object.getString("college_logo_url"), img);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.action_assignment) {
            // Handle the camera action
            startActivity(new Intent(this, AssignmentActivity.class));
        } else if (id == R.id.action_logout) {
            SharedPreferences preferences =
                    getSharedPreferences("MyFile", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchDataFromServer() {

        fetchAdvertisement();
        fetchBook();
        fetchNews();
        fetchCollegeUpdates();
    }

    private void fetchAdvertisement() {

        StringRequest request =
                new StringRequest(Request.Method.POST, URLHelper.GETMARKETINGADS, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        parseAdJSON(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void parseAdJSON(String response) {

        try {
            JSONObject object = new JSONObject(response);


            JSONArray array = object.getJSONArray("item");

            for (int i = 0; i < array.length(); i++) {
                JSONObject jObject = array.getJSONObject(i);

                AdvertisementPojo adPojo = new AdvertisementPojo();

                adPojo.setMarketing_id(jObject.getString("marketing_id"));
                adPojo.setMarketing_title(jObject.getString("marketing_title"));
                adPojo.setMarketing_message(jObject.getString("marketing_message"));
                adPojo.setMarketing_image_url(jObject.getString("marketing_image_url"));

                advertisementPojoArrayList.add(adPojo);

            }
            setAdsHorizontalScroll((LinearLayout) findViewById(R.id.llAdvertisement));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAdsHorizontalScroll(LinearLayout linearLayout) {
        for (int i = 0; i < advertisementPojoArrayList.size(); i++) {
            ImageView imageView = new ImageView(this);
            final AdvertisementPojo advertisementPojo = advertisementPojoArrayList.get(i);
            Log.d("1234", advertisementPojo.getMarketing_id());

            DownloadHelper.loadImageWithGlideURL(this, advertisementPojo.getMarketing_image_url(), imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(advertisementPojo.getMarketing_title(), advertisementPojo.getMarketing_message(), advertisementPojo.getMarketing_image_url(), null);
                }
            });

            linearLayout.addView(imageView);
        }


    }

    private void showDialog(String title, String message, String url, String rate) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.home_dialog_layout, null, false);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(view)
                .create();

        ImageView img = (ImageView) view.findViewById(R.id.image_view_dialog);
        DownloadHelper.loadImageWithGlideURL(this, url, img);
        TextView txtView = (TextView) view.findViewById(R.id.text_view_dialog);
        TextView rateView = (TextView) view.findViewById(R.id.text_view_rate);
        txtView.setText(message);
        rateView.setText(rate);
        dialog.show();


    }

    private void fetchBook() {

        StringRequest request =
                new StringRequest(Request.Method.POST, URLHelper.GETBOOKS, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        parseBookJSON(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void parseBookJSON(String response) {

        try {
            JSONObject object = new JSONObject(response);


            JSONArray array = object.getJSONArray("item");

            for (int i = 0; i < array.length(); i++) {
                JSONObject jObject = array.getJSONObject(i);

                BookPojo bookpojo = new BookPojo();
                bookpojo.setBook_id(jObject.getString("book_id"));
                bookpojo.setBook_title(jObject.getString("book_title"));
                bookpojo.setBook_description(jObject.getString("book_description"));
                bookpojo.setBook_download_url(jObject.getString("book_download_url"));
                bookpojo.setBook_rate(jObject.getString("book_rate"));

                bookPojoArrayList.add(bookpojo);

            }
            setBookHorizontalScroll((LinearLayout) findViewById(R.id.llBook));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setBookHorizontalScroll(LinearLayout linearLayout) {

        for (int i = 0; i < bookPojoArrayList.size(); i++) {
            ImageView imageView = new ImageView(this);
            final BookPojo bookPojo = bookPojoArrayList.get(i);


            DownloadHelper.loadImageWithGlideURL(this, bookPojo.getBook_download_url(), imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(bookPojo.getBook_title(), bookPojo.getBook_description(), bookPojo.getBook_download_url(), bookPojo.getBook_rate());
                }
            });

            linearLayout.addView(imageView);
        }

    }

    private void fetchNews() {


        StringRequest request =
                new StringRequest(Request.Method.POST, URLHelper.GETNEWS, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        parseNewsJSON(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void parseNewsJSON(String response) {

        try {
            JSONObject object = new JSONObject(response);


            JSONArray array = object.getJSONArray("item");

            for (int i = 0; i < array.length(); i++) {
                JSONObject jObject = array.getJSONObject(i);

                NewsPojo newsPojo = new NewsPojo();
                newsPojo.setNews_id(jObject.getString("news_id"));
                newsPojo.setNews_title(jObject.getString("news_title"));
                newsPojo.setNews_message(jObject.getString("news_message"));
                newsPojo.setNews_image_url(jObject.getString("news_image_url"));
                newsPojoArrayList.add(newsPojo);

            }
            setNewsHorizontalScroll((LinearLayout) findViewById(R.id.llJaipurNews));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setNewsHorizontalScroll(LinearLayout linearLayout) {

        for (int i = 0; i < newsPojoArrayList.size(); i++) {
            ImageView imageView = new ImageView(this);
            final NewsPojo newsPojo = newsPojoArrayList.get(i);


            DownloadHelper.loadImageWithGlideURL(this, newsPojo.getNews_image_url(), imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(newsPojo.getNews_title(), newsPojo.getNews_message(), newsPojo.getNews_image_url(), null);
                }
            });

            linearLayout.addView(imageView);
        }
    }

    private void fetchCollegeUpdates() {

        StringRequest request =
                new StringRequest(Request.Method.POST, URLHelper.GETCOLLEGEUPDATES, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        parseNewsJSON(response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

}
