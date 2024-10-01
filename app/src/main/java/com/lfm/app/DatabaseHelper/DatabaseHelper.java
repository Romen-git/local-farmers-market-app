package com.lfm.app.DatabaseHelper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lfm.app.Models.Products;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "liked";
    private static final String TABLE_NAME = "products";

    public DatabaseHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE_NAME + "(id TEXT ,pid TEXT PRIMARY KEY, name TEXT," +
                "description TEXT,price TEXT,amazon TEXT,image TEXT,category TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public boolean addText(Products products){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("pid", products.getId());
        contentValues.put("name", products.getName());
        contentValues.put("description", products.getDescription());
        contentValues.put("price", products.getPrice());
        contentValues.put("category", products.getCategoryid());

        sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
        return true;
    }

    public boolean deleteRow(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "pid=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        db.delete(TABLE_NAME, whereClause, whereArgs);
        return true;
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
        cursor.close();
        return arrayList;
    }
}
