package com.example.frootsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpdateOffer extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText editName, editPromo, editDescription;
    Button btnSave;
    private FirebaseFirestore fStore;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri imageUri;
    private ImageView editOfferImage;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_offer);

        Intent data = getIntent();
        final String name = data.getStringExtra("offername");
        String promo = data.getStringExtra("promocode");
        String description = data.getStringExtra("offerdescription");

        editName = findViewById(R.id.txtOffername);
        editPromo = findViewById(R.id.txtPromocode);
        editDescription = findViewById(R.id.txtOfferdescription);
        btnSave = findViewById(R.id.btn_save);
        editOfferImage = findViewById(R.id.EditOfferImage);
        progressDialog = new ProgressDialog(this);

        fStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        fStore.collection("offer").document(getIntent().getStringExtra("offername"))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                com.example.frootsapp.Model.Offer offer = documentSnapshot.toObject(com.example.frootsapp.Model.Offer.class);
                Picasso.get().load(offer.getOfferimage()).into(editOfferImage);
            }
        });

        editOfferImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        editName.setText(name);
        editPromo.setText(promo);
        editDescription.setText(description);

        Log.d(TAG, "onCreate: " + name + " " + promo + " " + description + " " + imageUri);

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
        sendposttodatabase();
    }

    private void sendposttodatabase() {
        btnSave.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (editName.getText().toString().isEmpty() || editPromo.getText().toString().isEmpty() || editDescription.getText().toString().isEmpty()) {
                    Toast.makeText(UpdateOffer.this, "one or many fields are empty", Toast.LENGTH_SHORT).show();
                    return;

                }
                progressDialog.setMessage("Posting to database");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                final StorageReference filepath = storageReference.child("offerImage").child(imageUri.getLastPathSegment());
                filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloaduri = uri;


                                String Name = editName.getText().toString();

                                DocumentReference docRef = fStore.collection("offer").document(Name);

                                Map<String, Object> edited = new HashMap<>();
                                edited.put("offername", editName.getText().toString());
                                edited.put("promocode", editPromo.getText().toString());
                                edited.put("offerdescription", editDescription.getText().toString());
                                edited.put("offerimage", downloaduri.toString());

                                docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(UpdateOffer.this, "Offer updated", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), ShopOfferList.class));
                                        finish();

                                    }


                                });


                            }
                        });


                    }
                });

            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            editOfferImage.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image....");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("offerimage/" + randomKey);

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
}