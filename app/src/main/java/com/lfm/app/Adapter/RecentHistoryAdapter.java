package com.lfm.app.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lfm.app.DatabaseHelper.DatabaseHelper;
import com.lfm.app.Fragments.ProductInfoBottom;
import com.lfm.app.Models.Products;
import com.lfm.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecentHistoryAdapter extends RecyclerView.Adapter<RecentHistoryAdapter.Viewholder> {

    private ArrayList<Products> recentHistory = new ArrayList<>();
    private final Context context;
    int click =0;
    DatabaseHelper databaseHelper;
    FragmentManager fragmentManager;

    public RecentHistoryAdapter(ArrayList<Products> recentHistory, DatabaseHelper databaseHelper, Context context, androidx.fragment.app.FragmentManager fragmentManager) {
        this.recentHistory = recentHistory;
        this.context = context;
        this.databaseHelper=databaseHelper;
        this.fragmentManager = fragmentManager;
    }




    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.lfm.app.R.layout.history_layout,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  RecentHistoryAdapter.Viewholder holder, int position) {

        Products products = recentHistory.get(position);

        holder.pName.setText(products.getName());
        String price1="Rs."+products.getPrice()+".00";
        holder.pPrice.setText(price1);
        holder.pDesc.setText(products.getDescription());

        String productId = products.getId();

        if (productId != null) {
            String folderPath = "product_images/" + productId + "/";
            StorageReference folderReference = FirebaseStorage.getInstance().getReference(folderPath);


            folderReference.listAll()
                    .addOnSuccessListener(listResult -> {

                        if (!listResult.getItems().isEmpty()) {

                            StorageReference imageReference = listResult.getItems().get(0);
                            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.get().load(uri)
                                            .fit()
                                            .placeholder(R.drawable.placeholder_image)
                                            .error(R.drawable.error_image)
                                            .into(holder.pImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    holder.pImage.setImageResource(R.drawable.default_image);
                                }
                            });
                        } else {

                            holder.pImage.setImageResource(R.drawable.default_image);
                        }
                    })
                    .addOnFailureListener(e -> {

                        holder.pImage.setImageResource(R.drawable.default_image);
                    });
        }else {
            holder.pImage.setImageResource(R.drawable.default_image);
        }

        //Picasso.get().load(products.getImage())
               // .into(holder.pImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductInfoBottom bottomSheet = new ProductInfoBottom(context, products);
                bottomSheet.show(fragmentManager, "model");
            }
        });


    }

    @Override
    public int getItemCount() {
        return recentHistory.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{
        private TextView pName,pDesc,pPrice;
        private ImageView pImage;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            pName = (TextView) itemView.findViewById(R.id.pName);
            pPrice = (TextView) itemView.findViewById(R.id.pPrice);
            pDesc = (TextView) itemView.findViewById(R.id.pDesc);
            pImage = (ImageView) itemView.findViewById(R.id.pImage);

        }
    }
}
