package com.lfm.app.Models;

public class CategoriesModel {

    public CategoriesModel() {

    }

    public String id, name,description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoriesModel(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
