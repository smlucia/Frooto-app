package com.example.frootsapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShopUpdate extends AppCompatActivity {
    public static final String TAG = "TAG";
    CircleImageView editImgLogo;
    EditText txtShopName, txtShopEmail, txtShopPhone, txtShopLocation, txtShopOwnerName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser shopID;
    StorageReference storageReference;
    Button saveButton;
    String userID;
    String EMAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_update);

        Intent data = getIntent();
        String shopname = data.getStringExtra("Shop name");
        String ownername = data.getStringExtra("owner name");
        String phone = data.getStringExtra("phone");
        String email = data.getStringExtra("email");
        String location = data.getStringExtra("location");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        shopID = fAuth.getCurrentUser();

        txtShopName = findViewById(R.id.ed_txtShopName);
        txtShopEmail = findViewById(R.id.ed_txtShopEmail);
        txtShopPhone = findViewById(R.id.ed_txtShopPhone);
        txtShopLocation = findViewById(R.id.ed_txtShopLocation);
        txtShopOwnerName = findViewById(R.id.ed_txtShopOwnerName);
        editImgLogo = findViewById(R.id.editImgLogo);
        saveButton = findViewById(R.id.btnSave_shop);

//        userID = fAuth.getCurrentUser().getUid();
////        Log.d(TAG, "onCreate: User" + userID);
//
//        StorageReference profileRef = storageReference.child("/users/ "+userID+"/download.jpg");
////        Log.d(TAG, "onCreate: storage" + profileRef);
//        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
////                Log.d(TAG, "onSuccess: Image"+uri);
//                Picasso.get().load(uri).into(editImgLogo);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
////                Log.d(TAG, "onFailure: "+ exception);
//            }
//        });
//        editImgLogo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //open Gallery
//                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(openGalleryIntent,1000);
//            }
//        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtShopName.getText().toString().isEmpty() || txtShopPhone.getText().toString().isEmpty() || txtShopEmail.getText().toString().isEmpty()) {
                    Toast.makeText(ShopUpdate.this, "one or many fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String email = txtShopEmail.getText().toString();
                shopID.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = fStore.collection("shop owners").document(shopID.getUid());
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("Shop name", txtShopName.getText().toString());
                        edited.put("owner name", txtShopOwnerName.getText().toString());
                        edited.put("email", txtShopEmail.getText().toString());
                        edited.put("location", txtShopLocation.getText().toString());
                        edited.put("phone", txtShopPhone.getText().toString());
                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ShopUpdate.this, "Account updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), ShopProfileActivity.class));
                                finish();
                            }
                        });
                        Toast.makeText(ShopUpdate.this, "Email is changed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShopUpdate.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        txtShopEmail.setText(email);
        txtShopPhone.setText(phone);
        txtShopLocation.setText(location);
        txtShopName.setText(shopname);
        txtShopOwnerName.setText(ownername);
        Log.d(TAG, "onCreate: " + email);

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
        //in this we are overwrite same image with new one because user can only have one profile image
        //upload image to firebase storage
        final StorageReference fileRef = storageReference.child("/users/ " + fAuth.getCurrentUser().getUid() + "/download.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(UserProfile.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(editImgLogo);

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShopUpdate.this, "Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }
}