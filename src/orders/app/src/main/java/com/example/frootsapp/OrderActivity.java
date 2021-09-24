package com.example.frootsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.frootsapp.Model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private TextView name;
    private TextView price;
    private TextView description;
    private TextView amount;
    private ImageView image;
    private TextView totAmount;
    private Button orderBtn;
    private Button addQty;
    private Button remQty;
    private int qty = 1;
    private int tempTot;
    private int count = 0;
    //    private Order order;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userId;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        name = findViewById(R.id.juiceName);
        price = findViewById(R.id.juicePrice);
        description = findViewById(R.id.juiceDescription);
        orderBtn = findViewById(R.id.btnOrder);
        amount = findViewById(R.id.juiceAmount);
        totAmount = findViewById(R.id.juiceTotAmount);
        addQty = findViewById(R.id.btnAmountAdd);
        remQty = findViewById(R.id.btnAmountNeg);
        image = findViewById(R.id.product_image);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        fStore.collection("item").document(getIntent().getStringExtra("name"))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Product product = documentSnapshot.toObject(Product.class);
                Picasso.get().load(product.getImage()).into(image);
            }
        });


        name.setText("Name: " + getIntent().getStringExtra("name"));
        price.setText("Price: " + getIntent().getStringExtra("price"));
        description.setText("Description: " + getIntent().getStringExtra("description"));
        amount.setText(Integer.toString(qty));

        tempTot = qty * Integer.parseInt(getIntent().getStringExtra("price"));
        totAmount.setText("Total Price: " + tempTot);

        addQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qty += 1;
                amount.setText(Integer.toString(qty));
                tempTot = qty * Integer.parseInt(getIntent().getStringExtra("price"));
                totAmount.setText("Total Price: " + tempTot);
            }
        });

        remQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty != 1) {
                    qty -= 1;
                    amount.setText(Integer.toString(qty));
                    tempTot = qty * Integer.parseInt(getIntent().getStringExtra("price"));
                    totAmount.setText("Total Price: " + tempTot);
                } else {
                    Toast.makeText(OrderActivity.this, "Minimum one item is required!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++count;

                if (count == 1) {
                    orderBtn.setText("Added to cart");

                    userId = fAuth.getCurrentUser().getUid(); //to retrieve uid of the current user
                    Map<String, Object> order = new HashMap<>();
                    order.put("userId", userId);
                    order.put("name", getIntent().getStringExtra("name"));
                    order.put("price", getIntent().getStringExtra("price"));
                    order.put("qty", Integer.toString(qty));
                    order.put("tempTotal", Integer.toString(tempTot));

                    //insert data into cloud database
                    fStore.collection("tempOrder").add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
                } else {
                    Toast.makeText(OrderActivity.this, "Order already added", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}