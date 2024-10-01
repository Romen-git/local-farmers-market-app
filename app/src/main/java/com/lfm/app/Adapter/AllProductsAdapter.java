package com.lfm.app.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lfm.app.Fragments.ProductInfoBottom;
import com.lfm.app.Interfaces.HistoryUpdated;
import com.lfm.app.Models.Products;
import com.lfm.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllProductsAdapter extends RecyclerView.Adapter<AllProductsAdapter.Viewholder> {

    Context mContext;
    ArrayList<Products> trendingList = new ArrayList<>();
    FragmentManager supportFragmentManager;
    HistoryUpdated historyUpdated;

    public AllProductsAdapter(ArrayList<Products> trendingList, Context context, FragmentManager supportFragmentManager, HistoryUpdated historyUpdated) {
        mContext = context;
        this.trendingList = trendingList;
        this.supportFragmentManager = supportFragmentManager;
        this.historyUpdated = historyUpdated;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.lfm.app.R.layout.product_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllProductsAdapter.Viewholder holder, int position) {

        Products products = trendingList.get(position);
        holder.productName.setText(products.getName());
        String price1 = "Rs." + products.getPrice() + ".00";
        holder.productPrice.setText(price1);

        String folderPath = "product_images/" + products.getId() + "/";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference folderRef = storageRef.child(folderPath);

        folderRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(holder.productImage);
                });
                break;
            }
        }).addOnFailureListener(exception -> {

            Log.e("Home", "Error listing items in folder: " + exception.getMessage());
        });

        //opening bottom sheet
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductInfoBottom bottomSheet = new ProductInfoBottom(mContext, products, historyUpdated);
                bottomSheet.show(supportFragmentManager, "ModalBottomSheet");
            }
        });
    }

    @Override
    public int getItemCount() {
        int limit = 4;
        if (trendingList.size() > limit) {
            return limit;
        } else {
            return trendingList.size();
        }

    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productPrice;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.productImage);
            productName = (TextView) itemView.findViewById(R.id.productName);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
        }
    }
}
