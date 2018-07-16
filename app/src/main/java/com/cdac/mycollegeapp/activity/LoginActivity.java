package com.cdac.mycollegeapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText editTextEmail;
    EditText editTextPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences =
                getSharedPreferences("MyFile", MODE_PRIVATE);
        if (preferences.getBoolean("isLogin", false))
        {
            startActivity(new Intent(this, Dashboard.class));
            finish();
        }

        init();
    }

    private void init() {
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPass = (EditText) findViewById(R.id.password);
    }

    public void login(View v) {
        String email = editTextEmail.getText().toString();
        String pass = editTextPass.getText().toString();
        boolean flag = false;
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
        if (!flag) {
            HashMap<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("password", pass);

            loginWebServiceCall(map);

        }
    }

    private void loginWebServiceCall(final HashMap<String, String> map) {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();

        StringRequest request =
                new StringRequest(Request.Method.POST, URLHelper.LOGIN, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();

                        Log.d("1234", "onResponse: "+response);

                        handleLoginResult(response);

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.cancel();
                                Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
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

    public void register(View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    private void handleLoginResult(String response) {

        try {
            JSONObject object = new JSONObject(response);

            String result = object.getString("result");
            if (result.equals("1"))
            {
                Toast.makeText(this, object.getString("msg"), Toast.LENGTH_SHORT).show();

                String info = object.getString("info");

                SharedPreferences preferences =
                        getSharedPreferences("MyFile", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("info", info);
                editor.putBoolean("isLogin", true);
                editor.commit();

                startActivity(new Intent(this, Dashboard.class));
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
