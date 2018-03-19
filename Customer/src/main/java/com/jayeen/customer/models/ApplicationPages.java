/**
 *
 */
package com.jayeen.customer.models;


import com.google.gson.annotations.SerializedName;
import com.jayeen.customer.utils.Const;

import java.io.Serializable;

public class ApplicationPages implements Serializable {
    @SerializedName(Const.Params.ID)
    public int id;
    @SerializedName(Const.Params.TITLE)
    public String title;
    @SerializedName(Const.Params.CONTENT)
    public String Data;

    @SerializedName(Const.Params.ICON)
    public String icon = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
