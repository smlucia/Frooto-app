package com.example.frootsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterUserActivity extends AppCompatActivity {


    Button btnCreateUserAccount;
    EditText txtUserName, txtUserContact, txtUserEmail, txtUserPassword;
    ProgressDialog loadingBar;
    FirebaseAuth fAuth;
    public static final String TAG = "TAG";

    FirebaseFirestore fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        btnCreateUserAccount = findViewById(R.id.btnCreateUserAccount);

        txtUserName = findViewById(R.id.txtUserName);
        txtUserContact = findViewById(R.id.txtUserContact);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        txtUserPassword = findViewById(R.id.txtUserPassword);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        loadingBar = new ProgressDialog(this);

        //        if(fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
//            finish();
//        }

        btnCreateUserAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        final String name = txtUserName.getText().toString();
        final String contact = txtUserContact.getText().toString();
        final String email = txtUserEmail.getText().toString();
        final String password = txtUserPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please Enter your name", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(contact)) {
            Toast.makeText(this, "Please Enter your phone number", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter your email", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter your password", Toast.LENGTH_LONG).show();
        } else if (password.length() < 8) {
            txtUserPassword.setError("Password should have minimum 8 characters");
            return;
        } else if (contact.length() < 10) {
            txtUserContact.setError("Contact number should have 10 numbers");
            return;
        } else if (contact.length() > 10) {
            txtUserContact.setError("Contact number cannot have more than 10 numbers");
            return;
        } else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the account");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterUserActivity.this, "user created", Toast.LENGTH_SHORT).show();

                        userID = fAuth.getCurrentUser().getUid(); //to retrieve uid of the current user
                        DocumentReference documentReference = fStore.collection("Users").document(userID);  //create collection as users and create document to user
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("contact", contact);
                        user.put("email", email);
                        user.put("password", password);

                        //insert data into cloud database
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: user profile is created for " + userID);


                            }
                        });

                        navigateHomeActivity();


                    } else {
                        Toast.makeText(RegisterUserActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }


//        final DatabaseReference RootRef;
//        RootRef = FirebaseDatabase.getInstance().getReference();
//
//        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(!(dataSnapshot.child("Users").child(contact).exists())){
//                            HashMap<String, Object> userdataMap = new HashMap<>();
//                            userdataMap.put("contact", contact);
//                            userdataMap.put("email", email);
//                            userdataMap.put("name", name);
//                            userdataMap.put("password", password);
//
//                            RootRef.child("Users").child(contact).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//                                        Toast.makeText(RegisterUserActivity.this, "Congratulations, Your account has been created", Toast.LENGTH_SHORT).show();
//                                        loadingBar.dismiss();
//
//                                        Intent intent =  new Intent(RegisterUserActivity.this, HomeActivity.class);
//                                        startActivity(intent);
//                                    }else{
//                                        loadingBar.dismiss();
//                                        Toast.makeText(RegisterUserActivity.this, "Error, Please Try again", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//
//                        }else{
//                            Toast.makeText(RegisterUserActivity.this, "This "+contact+ " already exists !", Toast.LENGTH_LONG).show();
//                            loadingBar.dismiss();
//                            Toast.makeText(RegisterUserActivity.this, "Please try again using another phone number", Toast.LENGTH_SHORT).show();
//
//                            Intent intent =  new Intent(RegisterUserActivity.this, LoginActivity.class);
//                            startActivity(intent);
//
//                        }
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//


            });


        }


    }

    private void navigateHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}