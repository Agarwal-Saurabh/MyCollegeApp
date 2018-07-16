package com.cdac.mycollegeapp.pojo;

/**
 * Created by lenovo1 on 10/15/2016.
 */
public class CollegeUpdatesPojo {

    private String college_updates_id="", college_update_title="",college_name= "", college_update_message="",college_update_image_url="";

    public String getCollege_name() {
        return college_name;
    }

    public void setCollege_name(String college_name) {
        this.college_name = college_name;
    }

    public String getCollege_updates_id() {
        return college_updates_id;
    }

    public void setCollege_updates_id(String college_updates_id) {
        this.college_updates_id = college_updates_id;
    }

    public String getCollege_update_title() {
        return college_update_title;
    }

    public void setCollege_update_title(String college_update_title) {
        this.college_update_title = college_update_title;
    }

    public String getCollege_update_message() {
        return college_update_message;
    }

    public void setCollege_update_message(String college_update_message) {
        this.college_update_message = college_update_message;
    }

    public String getCollege_update_image_url() {
        return college_update_image_url;
    }

    public void setCollege_update_image_url(String college_update_image_url) {
        this.college_update_image_url = college_update_image_url;
    }
}
