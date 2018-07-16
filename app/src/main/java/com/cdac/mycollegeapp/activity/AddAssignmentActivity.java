package com.cdac.mycollegeapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cdac.mycollegeapp.R;
import com.cdac.mycollegeapp.helper.URLHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddAssignmentActivity extends AppCompatActivity {

    private EditText title, message;
    private ImageView image_view;
    private int PICK_IMAGE_REQUEST = 1;
    private int REQUEST_PERMISSION_REQ_CODE  =11;
    private Uri selectImageUri  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        getSupportActionBar().setTitle("Add Assignment");

        init();
        methodListener();
    }

    private void init() {
        title = (EditText) findViewById(R.id.title);
        message = (EditText) findViewById(R.id.message);
        image_view = (ImageView) findViewById(R.id.image_view);
    }

    private void methodListener() {
        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    public void addAssignment(View view)
    {

        String enteredTitle = title.getText().toString();
        String enteredMessage = message.getText().toString();

        if (enteredTitle.equals("") || enteredMessage.equals(""))
        {
            Toast.makeText(this, "all fields are required", Toast.LENGTH_SHORT).show();
        }
        else {
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

            map.put("title", enteredTitle);
            map.put("message", enteredMessage);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectImageUri);
                String imageData = getStringImage(bitmap);
                map.put("image", imageData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            callAddAssignmentWebService(map);
        }

    }

    public void chooseImage() {
        if (!checkPermissionForImage()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_REQ_CODE);
        }

    }

    private boolean checkPermissionForImage()
    {
        boolean flagPermission = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
        return flagPermission;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_REQ_CODE)
        {
            Toast.makeText(this, "image granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
                selectImageUri = data.getData();
                Glide.with(this)
                        .load(selectImageUri)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(image_view);
            }
        }
    }

    private void callAddAssignmentWebService(final HashMap<String, String> map) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();

        StringRequest request =
                new StringRequest(Request.Method.POST, URLHelper.ADDASSIGNMENT, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();
                        Log.d("1234", "onResponse: "+response);
                        handleAddAssignmentResult(response);

                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.cancel();
                                Toast.makeText(AddAssignmentActivity.this, "error", Toast.LENGTH_SHORT).show();
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

    private void handleAddAssignmentResult(String response) {

        try {
            JSONObject object = new JSONObject(response);

            if (object.getString("result").equals("1"))
            {
                Toast.makeText(this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AssignmentActivity.class));
                finish();
            }
            else {
                Toast.makeText(this, "assignement can't be added", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}
