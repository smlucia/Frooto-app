package com.example.frootsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class LoginActivity extends AppCompatActivity {

    EditText login_contact, login_password;
    TextView txtAccountNav, txtNavigateShopAcc;
    Button btnLogin;
    ProgressDialog loadingBar;
    FirebaseAuth fAuth;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtAccountNav = findViewById(R.id.txtIDontHaveAccount);
        login_contact = findViewById(R.id.login_contact);
        login_password = findViewById(R.id.login_password);
        txtNavigateShopAcc = findViewById(R.id.txtShopLogin);

        btnLogin = findViewById(R.id.btnLogin);

        loadingBar = new ProgressDialog(this);

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();


        txtNavigateShopAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateShopAcc();
            }
        });

        txtAccountNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateRegister();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }


    private void loginUser() {


        String contact = login_contact.getText().toString().trim();
        String password = login_password.getText().toString().trim();

        if (TextUtils.isEmpty(contact)) {
            login_contact.setError("Email is Required");
            return;
        } else if (TextUtils.isEmpty(password)) {
            login_password.setError("Password is required");
            return;
        } else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the account");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(contact, password);
        }

    }

    private void AllowAccessToAccount(final String contact, String password) {


        fAuth.signInWithEmailAndPassword(contact, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                    navigateHome();
                    loadingBar.dismiss();


                } else {
                    Toast.makeText(LoginActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    public void navigateRegister() {
        Intent intent1 = new Intent(this, RegisterUserActivity.class);
        startActivity(intent1);
    }


    public void navigateHome() {
        startActivity(new Intent(LoginActivity.this, HomeActivity.class));

    }

    private void navigateShopAcc() {
        Intent intent2 = new Intent(LoginActivity.this, ShopLoginActivity.class);
        startActivity(intent2);

    }


}