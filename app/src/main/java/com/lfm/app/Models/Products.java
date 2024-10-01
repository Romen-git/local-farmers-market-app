package com.lfm.app.Models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Products implements Parcelable {


    String id;
    String name;
    String description;
    Long price;
    String categoryid;

    boolean active;

    Timestamp createdat;

    Long stock;
    String unit;


    Products() {

    }


    protected Products(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readLong();
        categoryid = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            active = in.readBoolean();
        }
        stock = in.readLong();

        unit = in.readString();
        createdat = new Timestamp(new Date(in.readLong()));

    }

    public static final Creator<Products> CREATOR = new Creator<Products>() {
        @Override
        public Products createFromParcel(Parcel in) {
            return new Products(in);
        }

        @Override
        public Products[] newArray(int size) {
            return new Products[size];
        }
    };


    public Products(String name, String description, long price, String pid, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.id = pid;
        this.categoryid = category;
    }

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

    public String getPrice() {
        return String.valueOf(price);
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedat() {
        return createdat;
    }

    public void setCreatedat(Timestamp createdat) {
        this.createdat = createdat;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeLong(price);
        dest.writeString(categoryid);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(active);
        }
        dest.writeLong(stock);
        dest.writeString(unit);
        dest.writeLong(createdat.toDate().getTime());
    }
}
