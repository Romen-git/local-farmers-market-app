package com.lfm.app.Fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.lfm.app.LoginActivity;
import com.lfm.app.MainActivity;
import com.lfm.app.Models.BillingDetails;
import com.lfm.app.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSettingsFragment extends Fragment {


    View view;
    LinearLayout layout;
    FirebaseUser mAuth;
    String name;

    private static final int PERMISSIONS_REQUEST_READ_MEDIA = 456;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    CircleImageView circleImageView;

    BillingDetails billingDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(com.lfm.app.R.layout.fragment_profile_settings, container, false);
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        TextView textView = view.findViewById(R.id.userName);
        LinearLayout linearLayout = view.findViewById(R.id.profile_linear);
        circleImageView = view.findViewById(R.id.profileImage);
        Button button = view.findViewById(R.id.signin);
        RelativeLayout relativeLayout = view.findViewById(R.id.logout);
        EditText editText = view.findViewById(R.id.username1);
        Button button1 = view.findViewById(R.id.userNameSave);
        RelativeLayout relativeLayout1 = view.findViewById(R.id.myaccount);
        RelativeLayout relativeLayout2 = view.findViewById(R.id.myphone);
        RelativeLayout relativeLayout3 = view.findViewById(R.id.myaddress);
        RelativeLayout relativeLayout4 = view.findViewById(R.id.mycity);
        RelativeLayout relativeLayout5 = view.findViewById(R.id.mystate);
        RelativeLayout relativeLayout6 = view.findViewById(R.id.myzipcode);
        TextView textView1 = view.findViewById(R.id.guser_phone_name);
        TextView textView2 = view.findViewById(R.id.guser_address_name);
        TextView textView3 = view.findViewById(R.id.guser_city_name);
        TextView textView4 = view.findViewById(R.id.guser_state_name);
        TextView textView5 = view.findViewById(R.id.guser_zipcode_name);
        EditText editText1 = view.findViewById(R.id.guser_phone_name_edit);
        EditText editText2 = view.findViewById(R.id.guser_address_name_edit);
        EditText editText3 = view.findViewById(R.id.guser_city_name_edit);
        EditText editText4 = view.findViewById(R.id.guser_state_name_edit);
        EditText editText5 = view.findViewById(R.id.guser_zipcode_name_edit);
        Button button2 = view.findViewById(R.id.userBillingSave);

        if (mAuth != null) {
            getBillingDetails();
            textView.setText(mAuth.getDisplayName());
            if (mAuth.getPhotoUrl() != null) {
                String photoUrl = mAuth.getPhotoUrl().toString();
                Picasso.get().load(photoUrl).resize(200, 0).centerCrop().into(circleImageView);

            } else {
                circleImageView.setImageResource(R.drawable.default_avatar);
            }
        } else {
            textView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        final String[] name = new String[]{""};

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGoogleSignIn(mAuth)) {
                    name[0] = String.valueOf(textView.getText());
                    textView.setVisibility(View.GONE);
                    editText.setVisibility(View.VISIBLE);
                    editText.setText(name[0]);
                    button1.setVisibility(View.VISIBLE);
                }

            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(String.valueOf(editText.getText()))
                        .build();
                mAuth.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String updatedName = String.valueOf(editText.getText());
                        editText.setVisibility(View.GONE);
                        button1.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(updatedName);
                        Toast.makeText(getContext(), "Profile Name updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile Name update failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View v) {
                if (mAuth != null) {
                    if (!isGoogleSignIn(mAuth)) {
                        String[] permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
                        if (ContextCompat.checkSelfPermission(v.getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                            // Permissions not granted
                            ActivityCompat.requestPermissions((AppCompatActivity) v.getContext(), permissions, PERMISSIONS_REQUEST_READ_MEDIA);
                        } else {
                            // Permissions granted
                            openImagePicker();
                        }
                    }
                }

            }
        });

        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth != null) {
                    getBillingDetails();
                    if (billingDetails != null) {
                        relativeLayout.setVisibility(View.GONE);
                        relativeLayout1.setVisibility(View.GONE);
                        if (!billingDetails.getPhone().isEmpty()) {
                            relativeLayout2.setVisibility(View.VISIBLE);
                            textView1.setText(billingDetails.getPhone());
                        } else {
                            relativeLayout2.setVisibility(View.VISIBLE);
                            textView1.setVisibility(View.GONE);
                            editText1.setVisibility(View.VISIBLE);
                        }
                        if (!billingDetails.getAddress().isEmpty()) {
                            relativeLayout3.setVisibility(View.VISIBLE);
                            textView2.setText(billingDetails.getAddress());
                        } else {
                            relativeLayout3.setVisibility(View.VISIBLE);
                            textView2.setVisibility(View.GONE);
                            editText2.setVisibility(View.VISIBLE);
                        }
                        if (!billingDetails.getCity().isEmpty()) {
                            relativeLayout4.setVisibility(View.VISIBLE);
                            textView3.setText(billingDetails.getCity());
                        } else {
                            relativeLayout4.setVisibility(View.VISIBLE);
                            textView3.setVisibility(View.GONE);
                            editText3.setVisibility(View.VISIBLE);
                        }
                        if (!billingDetails.getState().isEmpty()) {
                            relativeLayout5.setVisibility(View.VISIBLE);
                            textView4.setText(billingDetails.getState());
                        } else {
                            relativeLayout5.setVisibility(View.VISIBLE);
                            textView4.setVisibility(View.GONE);
                            editText4.setVisibility(View.VISIBLE);
                        }
                        if (!billingDetails.getZipcode().isEmpty()) {
                            relativeLayout6.setVisibility(View.VISIBLE);
                            textView5.setText(billingDetails.getZipcode());
                        } else {
                            relativeLayout6.setVisibility(View.VISIBLE);
                            textView5.setVisibility(View.GONE);
                            editText5.setVisibility(View.VISIBLE);
                        }

                        button2.setVisibility(View.VISIBLE);

                    }
                }
            }
        });

        final String[] phone = new String[]{""};
        final String[] address = new String[]{""};
        final String[] city = new String[]{""};
        final String[] state = new String[]{""};
        final String[] zipcode = new String[]{""};

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone[0] = String.valueOf(textView1.getText());
                textView1.setVisibility(View.GONE);
                editText1.setVisibility(View.VISIBLE);
                editText1.setText(phone[0]);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                address[0] = String.valueOf(textView2.getText());
                textView2.setVisibility(View.GONE);
                editText2.setVisibility(View.VISIBLE);
                editText2.setText(address[0]);
            }
        });

        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city[0] = String.valueOf(textView3.getText());
                textView3.setVisibility(View.GONE);
                editText3.setVisibility(View.VISIBLE);
                editText3.setText(city[0]);
            }
        });

        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state[0] = String.valueOf(textView4.getText());
                textView4.setVisibility(View.GONE);
                editText4.setVisibility(View.VISIBLE);
                editText4.setText(state[0]);
            }
        });

        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zipcode[0] = String.valueOf(textView5.getText());
                textView5.setVisibility(View.GONE);
                editText5.setVisibility(View.VISIBLE);
                editText5.setText(zipcode[0]);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidLength(String.valueOf(editText1.getText())) || isValidLength(String.valueOf(textView1.getText()))) {
                    if (areAllCharactersNumbers(String.valueOf(editText1.getText())) || areAllCharactersNumbers(String.valueOf(textView1.getText()))) {
                        if (isFirstCharacterZero(String.valueOf(editText1.getText())) || isFirstCharacterZero(String.valueOf(textView1.getText()))) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            CollectionReference usersCollection = db.collection("users");

                            DocumentReference userDocument = usersCollection.document(mAuth.getUid());


                            String phone = "";
                            String address = "";
                            String city = "";
                            String state = "";
                            String zipcode = "";
                            if (editText1.getVisibility() == View.VISIBLE) {
                                phone = String.valueOf(editText1.getText());
                                editText1.setVisibility(View.GONE);
                                textView1.setVisibility(View.VISIBLE);
                                textView1.setText(phone);
                            } else {
                                phone = String.valueOf(textView1.getText());
                            }
                            if (editText2.getVisibility() == View.VISIBLE) {
                                address = String.valueOf(editText2.getText());
                                editText2.setVisibility(View.GONE);
                                textView2.setVisibility(View.VISIBLE);
                                textView2.setText(address);
                            } else {
                                address = String.valueOf(textView2.getText());
                            }
                            if (editText3.getVisibility() == View.VISIBLE) {
                                city = String.valueOf(editText3.getText());
                                editText3.setVisibility(View.GONE);
                                textView3.setVisibility(View.VISIBLE);
                                textView3.setText(city);
                            } else {
                                city = String.valueOf(textView3.getText());
                            }
                            if (editText4.getVisibility() == View.VISIBLE) {
                                state = String.valueOf(editText4.getText());
                                editText4.setVisibility(View.GONE);
                                textView4.setVisibility(View.VISIBLE);
                                textView4.setText(state);
                            } else {
                                state = String.valueOf(textView4.getText());
                            }
                            if (editText5.getVisibility() == View.VISIBLE) {
                                zipcode = String.valueOf(editText5.getText());
                                editText5.setVisibility(View.GONE);
                                textView5.setVisibility(View.VISIBLE);
                                textView5.setText(zipcode);
                            } else {
                                zipcode = String.valueOf(textView5.getText());
                            }

                            Map<String, Object> data = new HashMap<>();
                            data.put("phone", phone);
                            data.put("address", address);
                            data.put("city", city);
                            data.put("state", state);
                            data.put("zipcode", zipcode);

                            userDocument.update(data)
                                    .addOnSuccessListener(aVoid -> {

                                        Toast.makeText(getContext(), "Billing details updated", Toast.LENGTH_SHORT).show();
                                        button2.setVisibility(View.GONE);
                                        relativeLayout2.setVisibility(View.GONE);
                                        relativeLayout3.setVisibility(View.GONE);
                                        relativeLayout4.setVisibility(View.GONE);
                                        relativeLayout5.setVisibility(View.GONE);
                                        relativeLayout6.setVisibility(View.GONE);
                                        relativeLayout.setVisibility(View.VISIBLE);
                                        relativeLayout1.setVisibility(View.VISIBLE);

                                    })
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(getContext(), "Error updating data in Firestore", Toast.LENGTH_SHORT).show();
                                        Log.e("Firestore", "Error updating data", e);
                                    });
                        } else {
                            Toast.makeText(getContext(), "Phone number should start with 0", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Phone number should only have numbers", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Phone number should have 10 characters", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            Picasso.get().load(selectedImageUri).into(circleImageView);

            // Upload
            uploadImageToStorage();
        }
    }

    private boolean isGoogleSignIn(FirebaseUser user) {
        if (user != null && !user.getProviderData().isEmpty()) {

            String providerId = user.getProviderData().get(1).getProviderId();
            return "google.com".equals(providerId);
        }
        return false;
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImageToStorage() {
        if (selectedImageUri != null) {
            // unique filename
            String filename = "user_profile_image_" + System.currentTimeMillis();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(mAuth.getUid()).child(filename);

            // Upload image
            storageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // success
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    // saveUrl
                                    saveImageUrlToAuth(downloadUrl.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // error
                            Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveImageUrlToAuth(String imageUrl) {

        if (mAuth.getPhotoUrl() != null) {
            String fullPath = Uri.parse(String.valueOf(mAuth.getPhotoUrl())).getPath();

            String prefixToRemove = "/v0/b/fir-storage-8ec77.appspot.com/o/";

            String pathWithoutPrefix = fullPath.replaceFirst(prefixToRemove, "");

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(pathWithoutPrefix);

            // Upload image
            storageRef.delete()
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            System.out.println("success");
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // error
                            System.out.println("fail");
                        }
                    });
        }

        if (mAuth != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(imageUrl))
                    .build();

            mAuth.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // success

                            if (mAuth.getPhotoUrl() != null) {
                                String photoUrl = mAuth.getPhotoUrl().toString();
                                Picasso.get().load(photoUrl).resize(200, 0).centerCrop().into(circleImageView);

                            } else {
                                circleImageView.setImageResource(R.drawable.default_avatar);
                            }

                            Toast.makeText(getContext(), "Profile image updated successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            // fails
                            Toast.makeText(getContext(), "Failed to update profile image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean isKeyboardVisible(Activity activity) {
        Rect rect = new Rect();
        View rootView = activity.getWindow().getDecorView();
        rootView.getWindowVisibleDisplayFrame(rect);
        int screenHeight = rootView.getHeight();
        int keypadHeight = screenHeight - rect.bottom;
        return keypadHeight > screenHeight * 0.15;
    }

    private void getBillingDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference usersCollection = db.collection("users");

        DocumentReference userDocument = usersCollection.document(mAuth.getUid());

        userDocument.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    billingDetails = document.toObject(BillingDetails.class);

                    Log.d("Firestore", "got billing ");
                } else {

                    Log.e("Firestore", "User document does not exist for ID: " + mAuth.getUid());
                }
            } else {
                Log.e("Firestore", "Error getting user details: ", task.getException());
            }
        });
    }

    private boolean isValidLength(String str) {
        return str != null && str.length() == 10;
    }

    private static boolean areAllCharactersNumbers(String str) {

        return str != null && str.matches("\\d+");
    }

    private static boolean isFirstCharacterZero(String str) {

        return str != null && str.length() > 0 && str.charAt(0) == '0';
    }

}
