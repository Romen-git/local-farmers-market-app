package com.lfm.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.lfm.app.Adapter.CartAdapter;
import com.lfm.app.DatabaseHelper.DatabaseHelper;
import com.lfm.app.Models.Products;
import com.lfm.app.databinding.ActivityCartBinding;
import com.lfm.app.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ArrayList<Products> arrayList = new ArrayList<>();
    ActivityCartBinding binding;
    CartAdapter mAdapter;
    DatabaseHelper databaseHelper;

    ActivityMainBinding binding1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);
        arrayList = databaseHelper.getAllData();
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cart);

        mAdapter = new CartAdapter(this,arrayList,databaseHelper,getSupportFragmentManager());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.cartView.setLayoutManager(layoutManager);
        binding.cartView.setAdapter(mAdapter);



        mAdapter.notifyDataSetChanged();
        Log.e("size", String.valueOf(arrayList.size()));

        if (arrayList.size() == 0)
            binding.emptyCart.setVisibility(View.VISIBLE);
        else
            binding.emptyCart.setVisibility(View.GONE);

    }
}