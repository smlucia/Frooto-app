package com.example.frootsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ShopProfileActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    //CircleImageView imgLogo;
    TextView appName, shopName, shopLocation, ownerName, shopEmail, shopPhone;
    ImageView btnEditShopAccount, btnDelete, btnHome;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_profile);

        appName = findViewById(R.id.txtAppName2);
        shopName = findViewById(R.id.txtShopNameLabel);
        shopLocation = findViewById(R.id.txtShopLocationLabel);
        ownerName = findViewById(R.id.txtOwnerLabel);
        shopEmail = findViewById(R.id.txtEmailLabel);
        shopPhone = findViewById(R.id.txtPhoneLabel);
        btnEditShopAccount = findViewById(R.id.btnEditShopAccount);
        btnDelete = findViewById(R.id.offerBtnDelete);
        btnHome = findViewById(R.id.btnHome);
        View imgLogo = findViewById(R.id.imgLogo);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        userID = fAuth.getCurrentUser().getUid();
        Log.d(TAG, "onCreate: User" + userID);

        StorageReference profileRef = storageReference.child("/users/" + userID + "/download.jpg");
        Log.d(TAG, "onCreate: storage" + profileRef);
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: Image" + uri);
                Picasso.get().load(uri).into((ImageView) imgLogo);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: " + exception);
            }
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        fStore.collection("shop owners")
                .whereEqualTo("email", user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: dfdlfjdsfhdslsdilfhsdiofds" + user.getEmail());
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ownerName.setText((String) document.get("owner name"));
                                shopEmail.setText((String) document.get("email"));
                                shopPhone.setText((String) document.get("phone"));
                                shopName.setText((String) document.get("Shop name"));
                                shopLocation.setText((String) document.get("location"));

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_2 = new Intent(ShopProfileActivity.this, ShopProductList.class);
                startActivity(intent_2);
            }
        });

        btnEditShopAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), ShopUpdate.class);

                intent.putExtra("userId", userID);
                intent.putExtra("phone", shopPhone.getText().toString());
                intent.putExtra("Shop name", shopName.getText().toString());
                intent.putExtra("email", shopEmail.getText().toString());
                intent.putExtra("location", shopLocation.getText().toString());
                intent.putExtra("owner name", ownerName.getText().toString());

                startActivity(intent);

            }

        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });

    }

    private void deleteAccount() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to delete this file ?")
                .setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.i("MainActivity", "You choose Yes");

                        Log.d(TAG, "Account Deleted");
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "OK! Works fine!");
                                    startActivity(new Intent(ShopProfileActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "hari ", e);
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.alert_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("MainActivity", "You choose No");
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                uploadImageToFirebase(imageUri);

            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri) {

        final StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(UserProfile.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageView imgLogo = null;
                        Picasso.get().load(uri).into(imgLogo);


                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShopProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();


            }
        });

    }
}