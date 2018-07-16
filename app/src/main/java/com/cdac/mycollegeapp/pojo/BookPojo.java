package com.cdac.mycollegeapp.pojo;

/**
 * Created by lenovo1 on 10/15/2016.
 */
public class BookPojo {

    private String book_id="", book_title="", book_description="",book_download_url="",book_rate="";

    public String getBook_rate() {
        return book_rate;
    }

    public void setBook_rate(String book_rate) {
        this.book_rate = book_rate;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getBook_description() {
        return book_description;
    }

    public void setBook_description(String book_description) {
        this.book_description = book_description;
    }

    public String getBook_download_url() {
        return book_download_url;
    }

    public void setBook_download_url(String book_download_url) {
        this.book_download_url = book_download_url;
    }
}
