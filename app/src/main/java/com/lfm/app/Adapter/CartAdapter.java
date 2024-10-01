package com.lfm.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lfm.app.CartActivity;
import com.lfm.app.DatabaseHelper.DatabaseHelper;
import com.lfm.app.Fragments.ProductInfoBottom;
import com.lfm.app.Models.Products;
import com.lfm.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {

    Context mContext;
    int click = 1;
    FragmentManager fragmentManager;
    DatabaseHelper databaseHelper;

    ArrayList<Products> arrayList = new ArrayList<>();

    public CartAdapter(CartActivity cartActivity, ArrayList<Products> arrayList, DatabaseHelper databaseHelper, FragmentManager fragmentManager) {
        mContext = cartActivity;
        this.fragmentManager = fragmentManager;
        this.arrayList = arrayList;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.Viewholder holder, int position) {

        Products products = arrayList.get(position);

        holder.pName.setText(products.getName());
        String price1 = "Rs." + products.getPrice() + ".00";
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
        } else {
            holder.pImage.setImageResource(R.drawable.default_image);
        }

        //Picasso.get().load(products.getImage())
        // .into(holder.pImage);

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = v.findViewById(R.id.cartCount);
                if (click == 0) {
                    holder.likeBtn.setImageResource(R.drawable.heart_filled2);
                    ++click;
                    if (databaseHelper.addText(products)) {

                        Toast.makeText(mContext, "Added to cart", Toast.LENGTH_SHORT).show();

                    }

                } else {
                    holder.likeBtn.setImageResource(R.drawable.heart2);
                    --click;
                    if (databaseHelper.deleteRow(products.getId())) {

                        Toast.makeText(mContext, "Removed from cart", Toast.LENGTH_SHORT).show();

                    }


                }

            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "LFM");
                sendIntent.setType("text/plain");
                mContext.startActivity(sendIntent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductInfoBottom bottomSheet = new ProductInfoBottom(mContext, products);
                bottomSheet.show(fragmentManager, "ModalBottomSheet");
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        private TextView pName, pDesc, pPrice;
        private ImageView pImage, likeBtn, shareBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            pName = (TextView) itemView.findViewById(R.id.pName);
            pPrice = (TextView) itemView.findViewById(R.id.pPrice);
            pDesc = (TextView) itemView.findViewById(R.id.pDesc);
            pImage = (ImageView) itemView.findViewById(R.id.pImage);
            likeBtn = (ImageView) itemView.findViewById(R.id.likeBtn);
            shareBtn = (ImageView) itemView.findViewById(R.id.shareBtn);
        }
    }
}
