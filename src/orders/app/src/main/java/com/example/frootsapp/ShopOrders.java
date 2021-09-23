package com.example.frootsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.frootsapp.Model.Order;
import com.example.frootsapp.Model.OrderReview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopOrders extends AppCompatActivity {

    private static final String TAG = "TAG";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    private OrderReview orderReview;
    private Order order;
    private FirebaseFirestore fStore;
    private ListView listView;
    private Button orderBtn;
    private int totalPrice = 0;
    private TextView totalPriceText;
    private TextView status;
    private int count = 0;

    List<Order> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_orders);

        orderBtn = findViewById(R.id.shopSubmitOrder);
        listView = findViewById(R.id.shopListView);
        totalPriceText = findViewById(R.id.shopTotalAmount);
        status = findViewById(R.id.status);


        fStore = FirebaseFirestore.getInstance();
        orders = new ArrayList<>();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fStore.collection("order").whereEqualTo("orderId", getIntent().getStringExtra("orderId"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => gfmfkldgmgjfdgd" + document.getData());
                                boolean ready = (boolean) document.get("ready");
                                String userId = (String) document.get("userId");
                                String orderId = (String) document.get("orderId");
                                String totAmount = (String) document.get("totalAmount");
                                totalPrice += Integer.parseInt(totAmount);
                                orders = document.toObject(OrderDocument.class).orderList;
                                if (ready == true) {
                                    status.setText("Status: Ready");
                                } else {
                                    status.setText("Status: Not Ready");
                                }

                            }
                            Log.d(TAG, "onComplete: order blah blah " + orders);
                            ShopOrderAdapter adapter = new ShopOrderAdapter(ShopOrders.this, R.layout.shop_order_item, orders);
                            listView.setAdapter(adapter);
                            totalPriceText.setText("Total Price: " + totalPrice);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++count;
                if (count == 1) {
                    for (int i = 0; i < orders.size(); i++) {
                        fStore.collection("order").document(getIntent().getStringExtra("orderId"))
                                .update("ready", true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error updating document", e);
                                    }
                                });
                    }

                    Toast.makeText(ShopOrders.this, "Order Completed", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ShopOrders.this, ShopOrderList.class));
                } else {
                    Toast.makeText(ShopOrders.this, "Order Completed already", Toast.LENGTH_SHORT).show();
                }

            }
        });

        setUpToolbar();
        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:

                        Intent intent = new Intent(ShopOrders.this, ShopProductList.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_Profile:
                        startActivity(new Intent(ShopOrders.this, UserProfile.class));
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(ShopOrders.this, "Logged Out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ShopOrders.this, LoginActivity.class));
                        break;

//Paste your privacy policy link

//                    case  R.id.nav_Policy:{
//
//                        Intent browserIntent  = new Intent(Intent.ACTION_VIEW , Uri.parse(""));
//                        startActivity(browserIntent);
//
//                    }
                    //       break;
                    case R.id.nav_share: {

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "http://play.google.com/store/apps/detail?id=" + getPackageName();
                        String shareSub = "Try now";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share using"));

                    }
                    break;
                }
                return false;
            }
        });


//        Log.d(TAG, "onCreate: data", orders.get(0));


    }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }
}