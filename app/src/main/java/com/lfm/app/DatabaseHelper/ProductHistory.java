package com.lfm.app.DatabaseHelper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lfm.app.Models.Products;

import java.util.ArrayList;

public class ProductHistory extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "history";
    private static final String TABLE_NAME = "products";

    public ProductHistory(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,pid TEXT , name TEXT," +
                "description TEXT,price TEXT,amazon TEXT,image TEXT,category TEXT)";
        db.execSQL(createTable);
    }

    public boolean addProducts(Products products){

        if (!CheckIsDataAlreadyInDBorNot(products.getId())){
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("pid", products.getId());
            contentValues.put("name", products.getName());
            contentValues.put("description", products.getDescription());
            contentValues.put("price", products.getPrice());

            sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        }

        return true;
    }

    @SuppressLint("Range")
    public boolean CheckIsDataAlreadyInDBorNot(String pid) {

        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "select * from "+TABLE_NAME;

        Cursor cursor = db.rawQuery(Query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            if (pid != null && pid.equalsIgnoreCase(cursor.getString(cursor.getColumnIndex("pid")))) {
                cursor.close();
                return true;
            }
            cursor.moveToNext();
        }
        cursor.close();
        return false;
    }
    public boolean updateProducts(Products products, int id){
        if (!CheckIsDataAlreadyInDBorNot(products.getId())) {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("pid", products.getId());
            contentValues.put("name", products.getName());
            contentValues.put("description", products.getDescription());
            contentValues.put("price", products.getPrice());


            sqLiteDatabase.update(TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(id)});
        }
        return true;
    }


    public boolean deleteRow(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Products> getAllData(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        ArrayList<Products> arrayList= new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from "+TABLE_NAME,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            @SuppressLint("Range") Products products = new Products(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getLong(cursor.getColumnIndex("price")),
                    cursor.getString(cursor.getColumnIndex("pid")),cursor.getString(cursor.getColumnIndex("category")));
            arrayList.add(products);
            cursor.moveToNext();
        }

        return arrayList;
    }
}
