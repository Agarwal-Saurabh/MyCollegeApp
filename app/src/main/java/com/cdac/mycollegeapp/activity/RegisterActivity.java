package com.cdac.mycollegeapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cdac.mycollegeapp.R;
import com.cdac.mycollegeapp.helper.URLHelper;
import com.cdac.mycollegeapp.helper.ValidationHelper;
import com.cdac.mycollegeapp.pojo.CollegePojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPass;
    EditText editTextCPass;
    EditText editTextStudent_id;
    TextView textViewCollege;
    TextView textViewBranch;
    TextView textViewYear;
    TextView textViewSection;

    ArrayList<CollegePojo> collegePojoArrayList = new ArrayList<>();


    String college_id = "", college_branch = "", college_year = "", college_section = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        methodListener();

        fetchColleges();
    }

    private void init() {
        editTextName = (EditText) findViewById(R.id.name);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPass = (EditText) findViewById(R.id.password);
        editTextCPass = (EditText) findViewById(R.id.Cpassword);
        editTextStudent_id = (EditText) findViewById(R.id.student_id);

        textViewCollege = (TextView) findViewById(R.id.college);
        textViewBranch = (TextView) findViewById(R.id.branch);
        textViewYear = (TextView) findViewById(R.id.year);
        textViewSection = (TextView) findViewById(R.id.section);
    }

    private void methodListener() {

    }

    public void register(View v) {
        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String pass = editTextPass.getText().toString();
        String Cpass = editTextCPass.getText().toString();
        String student_id = editTextStudent_id.getText().toString();

        boolean flag = false;
        if (!ValidationHelper.validateName(name)) {
            editTextName.requestFocus();
            editTextName.setError("Enter Valid Name");
            flag = true;
        } else {
            editTextName.setError(null);
        }
        if (!ValidationHelper.validateEmail(email)) {
            editTextEmail.requestFocus();
            editTextEmail.setError("Enter Valid Email");
            flag = true;
        } else {
            editTextEmail.setError(null);
        }
        if (!ValidationHelper.validatePassword(pass)) {
            editTextPass.requestFocus();
            editTextPass.setError("Min Length 6");
            flag = true;
        } else {
            editTextPass.setError(null);
        }
        if (!ValidationHelper.validatePassword(Cpass)) {
            editTextCPass.requestFocus();
            editTextCPass.setError("Min Length 6");
            flag = true;
        } else {
            editTextCPass.setError(null);
        }
        if (!pass.equals(Cpass)) {
            editTextCPass.requestFocus();
            editTextCPass.setError("password must match");
            flag = true;
        } else {
            editTextCPass.setError(null);
        }

        if (!flag) {
            HashMap<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("name", name);
            map.put("dob", "");
            map.put("college_id", college_id);
            map.put("password", pass);
            map.put("fcm_id", "");
            map.put("college_branch", college_branch);
            map.put("college_year", college_year);
            map.put("student_id", student_id);
            map.put("college_section", college_section);

            callServiceForRegister(map);
        }
    }

    public void selectCollege(View v) {
        final String arr[] = new String[collegePojoArrayList.size()];
        for (int i = 0; i < collegePojoArrayList.size(); i++) {
            arr[i] = collegePojoArrayList.get(i).getCollege_name();
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select College")
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        textViewCollege.setText(arr[which]);
                        college_id = collegePojoArrayList.get(which).getCollege_id();
                    }
                })
                .create();
        dialog.show();
    }

    public void selectYear(View v) {
        final String arr[] = {"I", "II", "III", "IV"};
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Year")
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        college_year = arr[which];
                        textViewYear.setText(arr[which]);
                    }
                })
                .create();
        dialog.show();
    }

    public void selectBranch(View v) {
        final String arr[] = {"CSE", "IT", "EE", "EC", "CE", "ME"};
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Branch")
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        college_branch = arr[which];
                        textViewBranch.setText(arr[which]);
                    }
                })
                .create();
        dialog.show();
    }

    public void selectSection(View v) {
        final String arr[] = {"A", "B", "C", "D", "E", "F", "G"};
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select Section")
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        college_section = arr[which];
                        textViewSection.setText(arr[which]);
                    }
                })
                .create();
        dialog.show();
    }

    private void fetchColleges() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();


        StringRequest request =
                new StringRequest(Request.Method.POST, URLHelper.GETCOLEGES, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();
                        callHandleCollege(response);
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.cancel();
                                Toast.makeText(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();
                            }
                        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void callServiceForRegister(final HashMap<String, String> map) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();

        StringRequest request =
                new StringRequest(Request.Method.POST, URLHelper.SIGNUP, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();

                        Log.d("1234", "onResponse: "+response);

                        handleRegisterResult(response);

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.cancel();
                                Toast.makeText(RegisterActivity.this, "error", Toast.LENGTH_SHORT).show();
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

    private void callHandleCollege(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray array = jsonObject.getJSONArray("item");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                CollegePojo collegePojo = new CollegePojo();
                collegePojo.setCollege_id(object.getString("college_id"));
                collegePojo.setCollege_name(object.getString("college_name"));
                collegePojo.setCollege_logo_url(object.getString("college_logo_url"));

                collegePojoArrayList.add(collegePojo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleRegisterResult(String response) {

        try {
            JSONObject object = new JSONObject(response);

            String result = object.getString("result");
            if (result.equals("1"))
            {
                Toast.makeText(this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            else {
                if (object.has("msg")) {
                    Toast.makeText(this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
