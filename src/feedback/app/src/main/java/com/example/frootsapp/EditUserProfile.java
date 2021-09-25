package com.example.frootsapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Fade;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUserProfile extends AppCompatActivity {

    public static final String TAG = "TAG";

    EditText editName, editContact, editEmail;
    CircleImageView EditProfileImageView;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        Intent data = getIntent();
        final String name = data.getStringExtra("name");
        String email = data.getStringExtra("email");
        String contact = data.getStringExtra("contact");


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();


        editName = findViewById(R.id.ed_EditProfName);
        editContact = findViewById(R.id.ed_editProfPhone);
        editEmail = findViewById(R.id.ed_EditprofEmail);
        EditProfileImageView = findViewById(R.id.iv_EditprofImage);
        saveBtn = findViewById(R.id.btn_save);
        //deleteBtn = findViewById(R.id.btnDelete);

        StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(EditProfileImageView);

            }
        });

        EditProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open Gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });


        //update edited user profile data
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editName.getText().toString().isEmpty() || editContact.getText().toString().isEmpty() || editEmail.getText().toString().isEmpty()) {
                    Toast.makeText(EditUserProfile.this, "one or many fields are empty", Toast.LENGTH_SHORT).show();
                    return;

                }

                final String email = editEmail.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docRef = fStore.collection("Users").document(user.getUid());

                        Map<String, Object> edited = new HashMap<>();
                        edited.put("email", editEmail.getText().toString());
                        edited.put("name", editName.getText().toString());
                        edited.put("contact", editContact.getText().toString());

                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditUserProfile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), UserProfile.class));
                                finish();

                            }
                        });

                        Toast.makeText(EditUserProfile.this, "Email is changed", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditUserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        editEmail.setText(email);
        editContact.setText(contact);
        editName.setText(name);
        Log.d(TAG, "onCreate: " + name + " " + email + " " + contact);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();


                //profImage.setImageURI(imageUri);


                uploadImageToFirebase(imageUri);

            }
        }

    }


    private void uploadImageToFirebase(Uri imageUri) {
        //in this we are overwrite same image with new one because user can only have one profile image
        //upload image to firebase storage
        final StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(UserProfile.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(EditProfileImageView); //picasso is use to set the profile image


                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditUserProfile.this, "Failed", Toast.LENGTH_SHORT).show();


            }
        });


    }

}