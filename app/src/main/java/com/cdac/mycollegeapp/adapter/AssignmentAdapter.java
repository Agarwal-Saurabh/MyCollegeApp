package com.cdac.mycollegeapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cdac.mycollegeapp.R;
import com.cdac.mycollegeapp.pojo.AssignemntPojo;

import java.util.ArrayList;

/**
 * Created by lenovo1 on 7/1/2017.
 */

public class AssignmentAdapter extends ArrayAdapter {

    private Context context;
    private int layRes;
    private ArrayList<AssignemntPojo> arrayList;
    private LayoutInflater inflater;

    public AssignmentAdapter(Context context, int resource, ArrayList<AssignemntPojo> objects) {
        super(context, resource, objects);

        this.context = context;
        this.layRes  = resource;
        this.arrayList = objects;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(layRes, null);

        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        TextView textViewDes = (TextView) view.findViewById(R.id.textViewDes);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        AssignemntPojo pojo = arrayList.get(position);

        textViewDes.setText(pojo.getAssignment_message());
        textViewTitle.setText(pojo.getAssignment_title());

        Glide.with(context)
                .load(pojo.getAssignment_image_url())
                .crossFade()
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);

        return view;
    }
}
