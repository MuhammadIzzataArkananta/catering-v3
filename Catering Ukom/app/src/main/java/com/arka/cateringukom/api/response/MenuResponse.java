package com.arka.cateringukom.api.response;

import com.arka.cateringukom.api.model.Menu;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MenuResponse {
    @SerializedName("menus")
    private List<Menu> menus;

    public MenuResponse(List<Menu> menus) {
        this.menus = menus;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
}

