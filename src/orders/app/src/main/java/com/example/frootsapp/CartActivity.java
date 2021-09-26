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
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    private Order order;
    private FirebaseFirestore fStore;
    private ListView listView;
    private Button orderBtn;
    private int totalPrice = 0;
    private TextView totalPriceText;
    private int count = 0;

    private ArrayList<Order> orders;
    private ArrayList<Order> newOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        orderBtn = findViewById(R.id.submitOrder);
        listView = findViewById(R.id.listView);
        totalPriceText = findViewById(R.id.totalAmount);
//        final TextView status = findViewById(R.id.status);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        fStore = FirebaseFirestore.getInstance();
        orders = new ArrayList<>();

        fStore.collection("tempOrder").whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String name = (String) document.get("name");
                                int totAmount = Integer.parseInt((String) document.get("tempTotal"));
                                int price = Integer.parseInt((String) document.get("price"));
                                int qty = Integer.parseInt((String) document.get("qty"));
                                order = new Order(name, totAmount, price, qty, user.getUid(), document.getId());
                                orders.add(order);

                                totalPrice += totAmount;
                                Log.d(TAG, "onComplete: order" + totalPrice);

                            }
                            Log.d(TAG, "onComplete: order" + orders);
                            OrderListAdapter adapter = new OrderListAdapter(CartActivity.this, R.layout.order_item, orders);
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
                    int tot = 0;
                    for (int i = 0; i < orders.size(); i++) {
                        tot += orders.get(i).getTotAmount();
                        Log.d(TAG, "onClick: order " + orders.get(i));
                    }

                    if (tot == 0) {
                        Toast.makeText(CartActivity.this, "Please select some items first", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> orderDetails = new HashMap<>();
                    orderDetails.put("totalAmount", Long.toString(tot));
                    orderDetails.put("userId", orders.get(0).getUserId());
                    orderDetails.put("ready", false);
                    orderDetails.put("orderId", orders.get(0).getOrderId());
                    orderDetails.put("userEmail", user.getEmail());
                    orderDetails.put("orderList", orders);
                    fStore.collection("order").document(orders.get(0).getOrderId()).set(orderDetails);
                    Log.d(TAG, "onClick: Total " + tot);
                    orderBtn.setText("Ordered");
//                    status.setText("Status: Ready");

                    for (int i = 0; i < orders.size(); i++) {
                        fStore.collection("tempOrder").document(orders.get(i).getOrderId())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
                    }

                    Toast.makeText(CartActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CartActivity.this, "Order Placed already", Toast.LENGTH_SHORT).show();
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

                        Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_Profile:
                        startActivity(new Intent(CartActivity.this, UserProfile.class));
                        break;
                    case R.id.nav_pending_orders:
                        startActivity(new Intent(CartActivity.this, UserOrders.class));
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(CartActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CartActivity.this, LoginActivity.class));
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