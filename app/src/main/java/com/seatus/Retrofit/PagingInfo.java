package com.seatus.Retrofit;

import com.google.gson.annotations.Expose;

/**
 * Created by rohail on 03-Mar-17.
 */
public class PagingInfo {

    @Expose
    public int total_records;
    @Expose
    public int current_page;
    @Expose
    public int total_pages;
    @Expose
    public int limit;
}
