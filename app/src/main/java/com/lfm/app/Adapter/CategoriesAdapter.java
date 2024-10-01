package com.lfm.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lfm.app.Models.CategoriesModel;
import com.lfm.app.R;
import com.lfm.app.ViewAllProductsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.Viewholder> {

    ArrayList<CategoriesModel> categoriesList = new ArrayList<>();
    Context mContext;
    public CategoriesAdapter(ArrayList<CategoriesModel> categoriesList, Context context) {
        this.categoriesList=categoriesList;
        this.mContext=context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.lfm.app.R.layout.categories_layout,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  CategoriesAdapter.Viewholder holder, int position) {
        CategoriesModel model = categoriesList.get(position);
        holder.categoryName.setText(model.getName());

        String folderPath = "product_categories/" + model.getId() + "/";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference folderRef = storageRef.child(folderPath);

        folderRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get().load(uri)
                            .fit()
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into(holder.categoryImage);
                    //Picasso.get().load(uri).into(holder.categoryImage);
                });

            }
        }).addOnFailureListener(exception -> {

            Log.e("Home", "Error listing categories in folder: " + exception.getMessage());
        });

        /*Picasso.get().load(model.getImage())
                .into(holder.categoryImage);*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ViewAllProductsActivity.class);
                intent.putExtra("type","category");
                intent.putExtra("category",model.getName());
                mContext.startActivity(intent);

                /*if (model.getName().equalsIgnoreCase("Vegetables")){

                    Intent intent = new Intent(mContext, ViewAllProductsActivity.class);
                    intent.putExtra("type","category");
                    intent.putExtra("category","Vegetables");
                    mContext.startActivity(intent);

                }else if(model.getName().equalsIgnoreCase("Meats")){
                    Intent intent = new Intent(mContext, ViewAllProductsActivity.class);
                    intent.putExtra("type","category");
                    intent.putExtra("category","Meats");
                    mContext.startActivity(intent);
                }else if(model.getName().equalsIgnoreCase("Fresh Produce")){
                    Intent intent = new Intent(mContext, ViewAllProductsActivity.class);
                    intent.putExtra("type","category");
                    intent.putExtra("category","Fresh Produce");
                    mContext.startActivity(intent);
                }else if(model.getName().equalsIgnoreCase("Organic Delights")){
                    Intent intent = new Intent(mContext, ViewAllProductsActivity.class);
                    intent.putExtra("type","category");
                    intent.putExtra("category","Organic Delights");
                    mContext.startActivity(intent);
                }else if(model.getName().equalsIgnoreCase("Dairy Delicacies")){
                    Intent intent = new Intent(mContext, ViewAllProductsActivity.class);
                    intent.putExtra("type","category");
                    intent.putExtra("category","Dairy Delicacies");
                    mContext.startActivity(intent);
                }*/
            }
        });


    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        public TextView categoryName;
        public CircleImageView categoryImage;
        public Viewholder(@NonNull  View itemView) {
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.categoryName);
            categoryImage = (CircleImageView) itemView.findViewById(R.id.categoryImage);
        }
    }
}
