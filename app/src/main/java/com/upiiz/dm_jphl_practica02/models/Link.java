package com.upiiz.dm_jphl_practica02.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Link implements Serializable {

    @SerializedName("href")
    private String href;

    public Link() {
    }

    public Link(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
