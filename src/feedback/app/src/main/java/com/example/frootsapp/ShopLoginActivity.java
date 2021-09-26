package com.example.frootsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ShopLoginActivity extends AppCompatActivity {

    TextView noShopAcc;
    Button btnLogin;
    EditText email, password;
    ProgressDialog loadingBar;
    FirebaseAuth fAuth;
    FirebaseUser currentUser;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_login);

        noShopAcc = findViewById(R.id.txtNoAccShop);
        btnLogin = findViewById(R.id.btnLoginShopLabel);
        email = findViewById(R.id.txtLogEmail);
        password = findViewById(R.id.txtLogPassword);
        loadingBar = new ProgressDialog(this);
        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();
        final FirebaseFirestore fStore = FirebaseFirestore.getInstance();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String logEmail = email.getText().toString().trim();
                String logPassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(email.toString())) {
                    email.setError("Email is Required");
                    return;
                } else if (TextUtils.isEmpty(logPassword)) {
                    password.setError("Password is required");
                    return;
                } else {
                    loadingBar.setTitle("Login Account");
                    loadingBar.setMessage("Please wait, while we are checking the account");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    fAuth.signInWithEmailAndPassword(logEmail, logPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        private static final String TAG = "TAG";

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                fStore.collection("shop owners").whereEqualTo("email", user.getEmail()).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> shopTask) {
                                                if (shopTask.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : shopTask.getResult()) {
                                                        count++;
                                                        Toast.makeText(ShopLoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                                        navigateHome();
                                                        loadingBar.dismiss();
                                                    }
                                                    if (count == 0) {
                                                        Toast.makeText(ShopLoginActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(ShopLoginActivity.this, ShopLoginActivity.class));
                                                        count = 0;
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", shopTask.getException());
                                                }
                                            }
                                        });

                            } else {
                                Toast.makeText(ShopLoginActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }

            }
        });

        noShopAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterShop();
            }
        });
    }

    private void navigateHome() {
        Intent intent = new Intent(ShopLoginActivity.this, ShopProductList.class);
        startActivity(intent);
    }

    public void RegisterShop() {
        Intent i = new Intent(ShopLoginActivity.this, RegisterShopActivity.class);
        startActivity(i);
    }
}