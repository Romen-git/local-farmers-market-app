package com.lfm.app.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class ViewAllProductGridADapter extends RecyclerView.Adapter<ViewAllProductGridADapter.Viewholder> {

    Context context;
    ArrayList<Products> locals = new ArrayList<>();
    public ViewAllProductGridADapter(ViewAllProductsActivity viewAllProductsActivity, ArrayList<Products> locals) {
        context = viewAllProductsActivity;
        this.locals=locals;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(com.lfm.app.R.layout.local_product_layout,parent,false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Products model = locals.get(position);
String price1="Rs."+model.getPrice()+".00";
        holder.productPrice.setText(price1);
        holder.productName.setText(model.getName());
        holder.productDesc.setText(model.getDescription());

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

                            holder.productImage.setImageResource(R.drawable.default_image);
                        }
                    })
                    .addOnFailureListener(e -> {

                        holder.productImage.setImageResource(R.drawable.default_image);
                    });
        }else {
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
    }

    @Override
    public int getItemCount() {
        return locals.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        TextView productName,productPrice,productDesc;
        ImageView productImage;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.productName);
            productDesc = (TextView) itemView.findViewById(R.id.productDesc2);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
            productImage = (ImageView) itemView.findViewById(R.id.productImage);
        }
    }
}
