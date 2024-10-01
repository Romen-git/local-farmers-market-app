package com.lfm.app.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lfm.app.Adapter.AllProductsAdapter;
import com.lfm.app.Adapter.CategoriesAdapter;
import com.lfm.app.Adapter.RecentHistoryAdapter;
import com.lfm.app.Adapter.TrendingRecyclerAdapter;
import com.lfm.app.CustomDatabase;
import com.lfm.app.DatabaseHelper.DatabaseHelper;
import com.lfm.app.DatabaseHelper.ProductHistory;
import com.lfm.app.Interfaces.HistoryUpdated;
import com.lfm.app.Models.BannerModel;
import com.lfm.app.Models.CategoriesModel;
import com.lfm.app.Models.Products;
import com.lfm.app.R;
import com.lfm.app.ViewAllProductsActivity;

import java.util.ArrayList;

import io.realm.mongodb.mongo.MongoDatabase;

public class HomeFragment extends Fragment implements HistoryUpdated {


    ProductHistory productHistory;
    LinearLayout noHistoryImage;
    View mMainView;
    Button viewAllBtn,viewlocalAllBtn;
    RecyclerView mTrendingView,mProductsAcrossView,mCategoriesView,mLocalViews,mRecentView;
    MongoDatabase mongoDatabase;
    ImageView bannerImage,midbannerImage;
    TrendingRecyclerAdapter mAdapter;

    NestedScrollView nestedScrollView;

    public static HistoryUpdated historyUpdated;
    AllProductsAdapter mAdapter2;
    AllProductsAdapter mAdapter4;
    CategoriesAdapter mAdapter3;
    RecentHistoryAdapter mRecentAdapter;


    ArrayList<Products> trendingList = new ArrayList<>();
    ArrayList<Products> abannerHomellProducts = new ArrayList<>();
    ArrayList<CategoriesModel> categoriesList = new ArrayList<>();
    ArrayList<Products> locals = new ArrayList<>();
    ArrayList<Products> recentHistory = new ArrayList<>();
    ArrayList<Products> allProducts = new ArrayList<>();

    ArrayList<String>ranks = new ArrayList<>();
    ArrayList<BannerModel>banners = new ArrayList<>();
    Button clearHistory;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initialize(){
        mTrendingView = (RecyclerView) mMainView.findViewById(com.lfm.app.R.id.trendingView);
        mProductsAcrossView = (RecyclerView) mMainView.findViewById(R.id.productsAcrossSl);
        mCategoriesView = (RecyclerView) mMainView.findViewById(R.id.categoriesView);
        mRecentView = (RecyclerView) mMainView.findViewById(R.id.recentProduct);

        bannerImage = (ImageView) mMainView.findViewById(R.id.bannerHome);
        midbannerImage = (ImageView) mMainView.findViewById(R.id.midBanner);
        clearHistory = (Button) mMainView.findViewById(R.id.clearHistory);
        noHistoryImage = (LinearLayout) mMainView.findViewById(R.id.noHistory);
        viewAllBtn = (Button) mMainView.findViewById(R.id.viewAllBtn);

        nestedScrollView = (NestedScrollView) mMainView.findViewById(R.id.nestedScroll);
        historyUpdated = (HistoryUpdated) this;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_home, container, false);
        initialize();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                addCategories();
                getAllProducts();

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
                mAdapter = new TrendingRecyclerAdapter(trendingList,getContext(),requireActivity().getSupportFragmentManager(),historyUpdated);
                mTrendingView.setLayoutManager(linearLayoutManager);
                mTrendingView.setAdapter(mAdapter);

                //products accross adapter
                mProductsAcrossView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                mAdapter2 = new AllProductsAdapter(allProducts,getContext(),requireActivity().getSupportFragmentManager(),historyUpdated);
                mProductsAcrossView.setAdapter(mAdapter2);


                //Recent Product History
                productHistory = new ProductHistory(getContext());
                recentHistory = productHistory.getAllData();
                DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

                mRecentAdapter = new RecentHistoryAdapter(recentHistory,databaseHelper,getContext(),getFragmentManager());
                LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(getContext());
                mRecentView.setLayoutManager(linearLayoutManager4);
                mRecentView.setAdapter(mRecentAdapter);

            }
        },1000);



        if (recentHistory.size() == 0){
            noHistoryImage.setVisibility(View.VISIBLE);
        }else{
            noHistoryImage.setVisibility(View.GONE);
        }

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productHistory.deleteRow();
                recentHistory.clear();
                noHistoryImage.setVisibility(View.VISIBLE);
                mRecentAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Product view history cleared", Toast.LENGTH_SHORT).show();
            }
        });

        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ViewAllProductsActivity.class);
                intent.putExtra("type","all");
                startActivity(intent);
            }
        });


        return mMainView;
    }

    private void getAllProducts(){
        allProducts.clear();
        CustomDatabase customDatabase = new CustomDatabase() ;
        CollectionReference products  = customDatabase.getSettings().collection("products");
        products.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                for (QueryDocumentSnapshot datasnapshot : snapshots){
                    Products products1 = datasnapshot.toObject(Products.class);
                    products1.setId(datasnapshot.getId());
                    allProducts.add(products1);
                    mAdapter2.notifyDataSetChanged();

                }

            }
        }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

            }
        });
    }
    private void addCategories(){

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference categoriesRef = rootRef.collection("productcategories");

        categoriesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    categoriesList.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        CategoriesModel categoryModel = document.toObject(CategoriesModel.class);
                        categoryModel.setId(document.getId());
                        categoriesList.add(categoryModel);
                    }
                    LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false);
                    mCategoriesView.setLayoutManager(linearLayoutManager2);
                    mAdapter3 = new CategoriesAdapter(categoriesList,getContext());
                    mCategoriesView.setAdapter(mAdapter3);
                } else {
                    Toast.makeText(getContext(),"Error in loading categories",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void getUpdateResult(boolean isUpdated) {
        if (isUpdated){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recentHistory.clear();
                    recentHistory.addAll(productHistory.getAllData());
                    mRecentAdapter.notifyDataSetChanged();
                    if(recentHistory.size() > 0)
                        noHistoryImage.setVisibility(View.GONE);
                }
            },1000);
        }
    }
}
