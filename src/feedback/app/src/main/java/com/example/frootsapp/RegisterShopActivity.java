package com.example.frootsapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class RegisterShopActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText shopName, ownerName, shopEmail, shopPwd, shopPhone, shopLocation;
    Button shopRegBtn;
    TextView shopLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String shopID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_shop);


        shopEmail = findViewById(R.id.txtShopEmail);
        shopPwd = findViewById(R.id.txtShopPwd);
        shopPhone = findViewById(R.id.txtConNo);
        shopLocation = findViewById(R.id.txtLocation);
        shopRegBtn = findViewById(R.id.btnShopReg);
        shopLoginBtn = findViewById(R.id.btnLoginShopLabel);
        shopName = findViewById(R.id.txtShopName);
        ownerName = findViewById(R.id.txtOwnerName);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


//        if (fAuth.getCurrentUser() != null) {
//            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//            finish();
//        }

        shopLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ShopLoginActivity.class));

            }
        });

        shopRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = shopEmail.getText().toString();
                final String password = shopPwd.getText().toString();
                final String phone = shopPhone.getText().toString();
                final String location = shopLocation.getText().toString();
                final String shopname = shopName.getText().toString();
                final String owner = ownerName.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    shopEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    shopPwd.setError("Enter the password");
                    return;
                }

                if (password.length() < 8) {
                    shopPwd.setError("Password should have minimum 8 characters");
                    return;
                }

                if (phone.length() < 10) {
                    shopPhone.setError("Contact number should have 10 numbers");
                    return;
                } else if (phone.length() > 10) {
                    shopPhone.setError("Contact number cannot have more than 10 numbers");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterShopActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(getApplicationContext(), HomeActivity.class));


                            shopID = fAuth.getCurrentUser().getUid(); //to retrieve uid of the current user
                            DocumentReference documentReference = fStore.collection("shop owners").document(shopID);  //create collection as users and create document to user
                            Map<String, Object> shop = new HashMap<>();
                            shop.put("Shop name", shopname);
                            shop.put("owner name", owner);
                            shop.put("email", email);
                            shop.put("password", password);
                            shop.put("phone", phone);
                            shop.put("location", location);

                            //insert data into cloud database
                            documentReference.set(shop).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: shop Account is created  ");


                                }
                            });


                        } else {
                            Toast.makeText(RegisterShopActivity.this, "Error!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                });

                startActivity(new Intent(getApplicationContext(), ShopProductList.class));


            }


        });
    }
}