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
}
