package com.lfm.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lfm.app.BuyActivity;
import com.lfm.app.CustomDatabase;
import com.lfm.app.DatabaseHelper.DatabaseHelper;
import com.lfm.app.DatabaseHelper.ProductHistory;
import com.lfm.app.ImageViewActivity;
import com.lfm.app.Interfaces.HistoryUpdated;
import com.lfm.app.LoginActivity;
import com.lfm.app.Models.Products;
import com.lfm.app.R;
import com.lfm.app.ReportActivity;
import com.lfm.app.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

public class ProductInfoBottom extends BottomSheetDialogFragment {

    private Context mContext;
    private Products products;
    private ArrayList<Products> savedProducts = new ArrayList<>();
    ImageView imageView, imageView2;
    ImageView like;
    Button buyBtn;
    DatabaseHelper databaseHelper;
    ProductHistory productHistory;
    RelativeLayout parent;
    Button report;
    int click = 0;
    CardView amazonIcon;
    FirebaseUser mAuth;

    HistoryUpdated historyUpdated;
    TextView productName, productPrice, productDesc;

    ActivityMainBinding binding;

    public ProductInfoBottom(Context mContext, Products products, HistoryUpdated historyUpdated) {
        this.mContext = mContext;
        this.products = products;
        this.historyUpdated = historyUpdated;
    }

    public ProductInfoBottom(Context mContext, Products products) {
        this.mContext = mContext;
        this.products = products;
        this.historyUpdated = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_info_bottom, container);

        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        imageView = view.findViewById(R.id.mainImage);
        imageView2 = view.findViewById(R.id.img1);
        productName = (TextView) view.findViewById(R.id.pName);
        productPrice = (TextView) view.findViewById(R.id.pPrice);
        parent = (RelativeLayout) view.findViewById(R.id.parent);
        productDesc = (TextView) view.findViewById(R.id.pDesc);
        like = (ImageView) view.findViewById(R.id.like);
        report = (Button) view.findViewById(R.id.productReport);
        buyBtn = (Button) view.findViewById(R.id.buyBtn);
        amazonIcon = (CardView) view.findViewById(R.id.amazonIcon);
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_main, container, false);

        databaseHelper = new DatabaseHelper(getContext());
        productHistory = new ProductHistory(getContext());

        ArrayList<Products> arrayList = new ArrayList<>();
        arrayList = databaseHelper.getAllData();
        for (Products products1 : arrayList) {
            if (products1.getId().equalsIgnoreCase(products.getId())) {
                like.setImageResource(R.drawable.heart_filled2);
                ++click;
            }
        }

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mAuth != null) {
                    Toast.makeText(getContext(), "Buying Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), BuyActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }

            }
        });
        amazonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Buying Successful", Toast.LENGTH_SHORT).show();
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click == 0) {
                    like.setImageResource(R.drawable.heart_filled2);
                    ++click;
                    if (databaseHelper.addText(products))
                        Toast.makeText(mContext, "Added to cart", Toast.LENGTH_SHORT).show();
                    ArrayList<Products> arrayList = new ArrayList<>();
                    arrayList = databaseHelper.getAllData();
                    if (arrayList.size() == 0) {
                        binding.cartCount.setVisibility(View.GONE);
                    } else {
                        binding.cartCount.setText(String.valueOf(arrayList.size()));
                        binding.cartCount.setVisibility(View.VISIBLE);
                    }

                } else {
                    like.setImageResource(R.drawable.heart2);
                    --click;
                    if (databaseHelper.deleteRow(products.getId())) {
                        Toast.makeText(mContext, "Removed from cart", Toast.LENGTH_SHORT).show();
                        ArrayList<Products> arrayList = new ArrayList<>();
                        arrayList = databaseHelper.getAllData();
                        if (arrayList.size() == 0) {
                            binding.cartCount.setVisibility(View.GONE);
                        } else {
                            binding.cartCount.setText(String.valueOf(arrayList.size()));
                            binding.cartCount.setVisibility(View.VISIBLE);
                        }
                    }

                }

            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ReportActivity.class);
                intent.putExtra("product", products.getName());
                startActivity(intent);
            }
        });

        //reported();
        addInHistory();

        productName.setText(products.getName());
        String price1 = "Rs."+products.getPrice() + "/" + products.getUnit();
        productPrice.setText(price1);
        productDesc.setText(products.getDescription());

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
                                            .into(imageView);
                                    Picasso.get().load(uri)
                                            .fit()
                                            .placeholder(R.drawable.placeholder_image)
                                            .error(R.drawable.error_image)
                                            .into(imageView2);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    imageView.setImageResource(R.drawable.default_image);
                                }
                            });
                        } else {

                            imageView.setImageResource(R.drawable.default_image);
                        }
                    })
                    .addOnFailureListener(e -> {

                        imageView.setImageResource(R.drawable.default_image);
                    });
        } else {
            imageView.setImageResource(R.drawable.default_image);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ImageViewActivity.class);

                intent.putExtra("uri", products.getName());

                startActivity(intent);

            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ImageViewActivity.class);

                intent.putExtra("uri", products.getName());

                startActivity(intent);
            }
        });

        return view;
    }
    /*public void reported(){
        CustomDatabase customDatabase = new CustomDatabase() ;
        CollectionReference products  = customDatabase.getSettings().collection("reports");
        products.document(this.products.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String solved = documentSnapshot.getString("solved");
                    if (solved.equalsIgnoreCase("false")){
                        report.setText("In Review");
                        report.setEnabled(false);
                    }else{
                        report.setEnabled(true);
                    }
                }

            }
        });
    }*/

    public void addInHistory() {
        if (historyUpdated != null) {
            ArrayList<Products> products = productHistory.getAllData();
            historyUpdated.getUpdateResult(true);
            if (products.size() < 4) {

                productHistory.addProducts(this.products);
            } else {
                int random = ThreadLocalRandom.current().nextInt(0, 3);
                productHistory.updateProducts(this.products, random);
            }
        }
    }
}
