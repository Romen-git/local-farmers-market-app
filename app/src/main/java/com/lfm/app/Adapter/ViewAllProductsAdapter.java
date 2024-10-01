package com.lfm.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lfm.app.Fragments.HomeFragment;
import com.lfm.app.Fragments.ProductInfoBottom;
import com.lfm.app.Models.Products;
import com.lfm.app.R;
import com.lfm.app.ViewAllProductsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewAllProductsAdapter extends RecyclerView.Adapter<ViewAllProductsAdapter.Viewholder> {

    ArrayList<Products> products = new ArrayList<>();
    Context context;

    public ViewAllProductsAdapter(ViewAllProductsActivity viewAllProductsActivity, ArrayList<Products> products) {
        context = viewAllProductsActivity;
        this.products = products;

    }

    public ViewAllProductsAdapter(FragmentActivity activity, ArrayList<Products> arrayList) {
        this.context = activity;
        this.products = arrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        view = LayoutInflater.from(parent.getContext()).inflate(com.lfm.app.R.layout.view_all_products,parent,false);


        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewAllProductsAdapter.Viewholder holder, int position) {

        Products model = products.get(position);
        holder.productName.setText(model.getName());
        String price1="Rs."+model.getPrice()+".00";
        holder.productPrice.setText(price1);
        holder.productDescription.setText(model.getDescription());

        String productId = model.getId();

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
                            System.out.println("err1");
                            holder.productImage.setImageResource(R.drawable.default_image);
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("err2");
                        holder.productImage.setImageResource(R.drawable.default_image);
                    });
        }else {
            System.out.println("err3");
            holder.productImage.setImageResource(R.drawable.default_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                ProductInfoBottom bottomSheet = new ProductInfoBottom(context, model, HomeFragment.historyUpdated);
                bottomSheet.show(manager, "ModalBottomSheet");
            }
        });
        holder.productShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "LFM" );
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        TextView productName,productPrice,productDescription;
        ImageView productImage,productShare;
        public Viewholder(@NonNull  View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.pName);
            productDescription = (TextView) itemView.findViewById(R.id.pDesc);
            productPrice = (TextView) itemView.findViewById(R.id.pPrice);
            productImage = (ImageView) itemView.findViewById(R.id.pImage);
            productShare = (ImageView) itemView.findViewById(R.id.shareBtn);
        }
    }
}
