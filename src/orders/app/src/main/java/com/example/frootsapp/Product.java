package com.example.frootsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Product extends AppCompatActivity {

    private TextView name;
    private TextView price;
    private TextView description;
    private TextView amount;
    private Button orderBtn;
    private Button addQty;
    private int qty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        name = findViewById(R.id.juiceName);
        price = findViewById(R.id.juicePrice);
        description = findViewById(R.id.juiceDescription);
        orderBtn = findViewById(R.id.btnOrder);
        amount = findViewById(R.id.juiceAmount);
        addQty = findViewById(R.id.btnAmount);

        name.setText("Name: " + getIntent().getStringExtra("name"));
        price.setText("Price: " + getIntent().getStringExtra("price"));
        description.setText("Description: " + getIntent().getStringExtra("description"));
        amount.setText(Integer.toString(qty));
        addQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qty += 1;
                amount.setText(Integer.toString(qty));
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderBtn.setText("Added to cart");
            }
        });
    }

//    EditText  EditProductName, EditProductPrice, EditProductDescription;
//    ImageView EditProductImage;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_product);
//
//
//
//        EditProductName.setText("Name: " + getIntent().getStringExtra("name"));
//        EditProductPrice.setText("Price: " + getIntent().getStringExtra("price"));
//        EditProductDescription.setText("Description: " + getIntent().getStringExtra("description"));
//
//        EditProductName = findViewById(R.id.EditProductName);
//        EditProductPrice = findViewById(R.id.EditProductPrice);
//        EditProductDescription = findViewById(R.id.EditProductDescription);
//        EditProductImage = findViewById(R.id.EditProductImage);
//
//        EditProductImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(Product.this,"profile image clicked",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//    }
}