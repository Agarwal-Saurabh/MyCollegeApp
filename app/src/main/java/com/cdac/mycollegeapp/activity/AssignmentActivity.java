package com.cdac.mycollegeapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cdac.mycollegeapp.R;
import com.cdac.mycollegeapp.adapter.AssignmentAdapter;
import com.cdac.mycollegeapp.helper.URLHelper;
import com.cdac.mycollegeapp.pojo.AssignemntPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssignmentActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<AssignemntPojo> arrayList = new ArrayList<>();

    private AssignmentAdapter assignmentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Assignments");
        getSupportActionBar().setSubtitle("this is subtile");

        init();

        assignmentAdapter = new AssignmentAdapter(this, R.layout.assignment_list_item, arrayList);
        listView.setAdapter(assignmentAdapter);

        fetchValuesFromServer();
    }

    private void init() {
        listView = (ListView) findViewById(R.id.listView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AssignmentActivity.this, AddAssignmentActivity.class));
            }
        });
    }

    private void fetchValuesFromServer() {
        SharedPreferences preferences =
                getSharedPreferences("MyFile", MODE_PRIVATE);
        final HashMap<String, String> map = new HashMap<>();
        try {
            JSONObject object = new JSONObject(preferences.getString("info", ""));
            map.put("college_id", object.getString("college_id"));
            map.put("college_branch", object.getString("college_branch"));
            map.put("college_year", object.getString("college_year"));
            map.put("college_section", object.getString("college_section"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();

        StringRequest request =
                new StringRequest(Request.Method.POST, URLHelper.GETCOLLEGEASSIGNMENT, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();

                        Log.d("1234", "onResponse: "+response);

                        handleAssignmentResult(response);

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.cancel();
                                Toast.makeText(AssignmentActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return map;
                    }
                };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    private void handleAssignmentResult(String response) {
        try {
            JSONObject object = new JSONObject(response);

            JSONArray array = object.getJSONArray("item");

            for (int i =0; i<array.length() ;i++)
            {
                JSONObject jsonObject = array.getJSONObject(i);
                AssignemntPojo pojo = new AssignemntPojo();
                pojo.setAssignment_id(jsonObject.getString("assignment_id"));
                pojo.setAssignment_title(jsonObject.getString("assignment_title"));
                pojo.setAssignment_message(jsonObject.getString("assignment_message"));
                pojo.setAssignment_image_url(jsonObject.getString("assignment_image_url"));

                arrayList.add(pojo);
            }
            assignmentAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
