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
import com.lfm.app.Fragments.ProductInfoBottom;
import com.lfm.app.Interfaces.HistoryUpdated;
import com.lfm.app.Models.Products;
import com.lfm.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrendingRecyclerAdapter extends RecyclerView.Adapter<TrendingRecyclerAdapter.Viewholder> {

    Context mContext;
    ArrayList<Products> trendingList = new ArrayList<>();
    FragmentManager supportFragmentManager;
    HistoryUpdated historyUpdated;

    public TrendingRecyclerAdapter(ArrayList<Products> trendingList, Context context, FragmentManager supportFragmentManager, HistoryUpdated historyUpdated) {
        mContext = context;
        this.trendingList = trendingList;
        this.supportFragmentManager = supportFragmentManager;
        this.historyUpdated = historyUpdated;

    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_products_layout, parent, false);

        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingRecyclerAdapter.Viewholder holder, int position) {

        Products products = trendingList.get(position);
        holder.productName.setText(products.getName());
        String price1="Rs."+products.getPrice()+".00";
        holder.productPrice.setText(price1);

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
                                            .into(holder.productImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    holder.productImage.setImageResource(R.drawable.default_image);
                                }
                            });
                        } else {

                            holder.productImage.setImageResource(R.drawable.default_image);
                        }
                    })
                    .addOnFailureListener(e -> {

                        holder.productImage.setImageResource(R.drawable.default_image);
                    });
        }else {
            holder.productImage.setImageResource(R.drawable.default_image);
        }


        if (position == 0) {
            holder.crownImage.setVisibility(View.VISIBLE);
        } else {
            holder.crownImage.setVisibility(View.GONE);
        }

        //opening bottom sheet
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductInfoBottom bottomSheet = new ProductInfoBottom(mContext, products, historyUpdated);
                bottomSheet.show(supportFragmentManager, "ModalBottomSheet");
            }
        });


//        Bundle bundle = new Bundle();
//        bundle.putString("partyId", partyId );
//        BottomSheetDialog bottomSheet = new BottomSheetDialog();
//        bottomSheet.setArguments(bundle);
//        bottomSheet.show(getSupportFragmentManager(), "ModalBottomSheet");

    }

    @Override
    public int getItemCount() {
        return trendingList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        ImageView productImage;
        CircleImageView crownImage;
        TextView productName, productPrice;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            productImage = (ImageView) itemView.findViewById(R.id.productImage);
            crownImage = (CircleImageView) itemView.findViewById(R.id.crown);
            productName = (TextView) itemView.findViewById(R.id.productName);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
        }
    }
}
