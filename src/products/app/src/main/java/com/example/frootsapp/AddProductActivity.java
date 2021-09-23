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

import com.example.frootsapp.Model.Product;
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

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    EditText product_name, product_price, product_description;
    Button btn_save_product;
    ProgressDialog progressDialog;

    Product product;
    private ImageView product_image;
    private Uri imageUri;

    private FirebaseFirestore fStore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        product_description = findViewById(R.id.product_description);
        btn_save_product = findViewById(R.id.btn_save_product);
        product_image = findViewById(R.id.product_image);
        progressDialog = new ProgressDialog(this);

        product_image = findViewById(R.id.product_image);

        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        product = new Product();
        product_image.setOnClickListener(new View.OnClickListener() {
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
        btn_save_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage("Posting to database");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                final String name = product_name.getText().toString().trim();
                final String price = product_price.getText().toString().trim();
                final String description = product_description.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(AddProductActivity.this, "please enter name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(price)) {
                    Toast.makeText(AddProductActivity.this, "Please enter price", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(description)) {
                    Toast.makeText(AddProductActivity.this, "Please enter description", Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(price) && !TextUtils.isEmpty(description) && imageUri != null) {

                    final StorageReference filepath = storageReference.child("product_image").child(imageUri.getLastPathSegment());
                    filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloaduri = uri;

                                    DocumentReference documentReference = fStore.collection("item").document(name);

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("name", name);
                                    map.put("price", price);
                                    map.put("description", description);
                                    map.put("image", downloaduri.toString());

                                    documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: hari bn  ");
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
        Intent intent_1 = new Intent(this, ShopProductList.class);
        startActivity(intent_1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            product_image.setImageURI(imageUri);
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
        product_name.setText("");
        product_price.setText("");
        product_description.setText("");

    }
}