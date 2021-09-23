package com.example.frootsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frootsapp.Model.Offer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class AddOfferActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    EditText offer_name, promo_code, offer_description;
    Button btn_add_offer;
    ProgressDialog progressDialog;

    Offer offer;
    private ImageView offer_image;
    private Uri imageUri;

    private FirebaseFirestore fStore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);


        offer_name = findViewById(R.id.offer_name);
        promo_code = findViewById(R.id.promo_code);
        offer_description = findViewById(R.id.offer_description);
        btn_add_offer = findViewById(R.id.btn_add_offer);
        offer_image = findViewById(R.id.offer_image);
        progressDialog = new ProgressDialog(this);

        offer_image = findViewById(R.id.offer_image);

        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        offer = new Offer();
        offer_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
        sendposttodatabase();
    }

    private void sendposttodatabase() {
        btn_add_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Posting to database");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                final String name = offer_name.getText().toString().trim();
                final String promo = promo_code.getText().toString().trim();
                final String description = offer_description.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(AddOfferActivity.this, "please enter offer name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(promo)) {
                    Toast.makeText(AddOfferActivity.this, "Please enter promo code", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(description)) {
                    Toast.makeText(AddOfferActivity.this, "Please enter description", Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(promo) && !TextUtils.isEmpty(description) && imageUri != null) {

                    final StorageReference filepath = storageReference.child("offer_image").child(imageUri.getLastPathSegment());
                    filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloaduri = uri;

                                    DocumentReference documentReference = fStore.collection("offer").document(name);

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("offername", name);
                                    map.put("promocode", promo);
                                    map.put("offerdescription", description);
                                    map.put("offerimage", downloaduri.toString());

                                    documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: Successful ");
                                            navigateProductList();


                                        }
                                    });


                                }
                            });

                            clearControls();
                        }
                    });
                }

            }
        });
    }

    private void navigateProductList() {
        Intent intent_1 = new Intent(this, ShopOfferList.class);
        startActivity(intent_1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            offer_image.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image....");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progressPercent = (100.00 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        pd.setMessage("Progress " + (int) progressPercent + "%");
                    }


                });
    }

    public void clearControls() {
        offer_name.setText("");
        promo_code.setText("");
        offer_description.setText("");

    }
}