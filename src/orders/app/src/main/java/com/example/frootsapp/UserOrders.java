package com.example.frootsapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frootsapp.Model.ShopOrder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
//import com.rey.material.widget.ImageView;
//import com.bumptech.glide.Glide;


public class UserOrders extends AppCompatActivity {


    private static final String TAG = "TAG";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    FirebaseUser currentUser;
    FirebaseAuth fAuth;

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView itemList;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        firebaseFirestore = FirebaseFirestore.getInstance();
        itemList = findViewById(R.id.customer_recycler_view);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Query query = firebaseFirestore.collection("order").whereEqualTo("userId", user.getUid());
        FirestoreRecyclerOptions<ShopOrder> options = new FirestoreRecyclerOptions.Builder<ShopOrder>().setQuery(query, ShopOrder.class).build();

        adapter = new FirestoreRecyclerAdapter<ShopOrder, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_order_item, parent, false);
                return new ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ProductsViewHolder holder, final int position, @NonNull final ShopOrder model) {

                if (model.isReady() == false) {
                    holder.list_ready.setText("Status: Order not complete");
                    holder.collectBtn.setText("Wait For Your Order");
                    holder.list_ready.setTextColor(Color.parseColor("#FF0044"));
                    holder.collectBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(UserOrders.this, "Wait till we finish your order!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    holder.list_ready.setText("Status: Order Complete");
                    holder.list_ready.setTextColor(Color.parseColor("#32CD32"));
                    holder.collectBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            firebaseFirestore.collection("order").document(model.getOrderId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(UserOrders.this, "Order Collected", Toast.LENGTH_SHORT).show();
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
                    });
                }

                holder.list_name.setText("User Email: " + model.getUserEmail());
                holder.list_price.setText("Price: " + model.getTotalAmount());
                holder.list_orderId.setText("Order Id: " + model.getOrderId());
//                holder.collectBtn.setText("Co");


            }
        };

        itemList.setHasFixedSize(true);
        itemList.setLayoutManager(new LinearLayoutManager(this));
        itemList.setAdapter(adapter);

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();


        setUpToolbar();
        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:

                        Intent intent = new Intent(UserOrders.this, HomeActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_Profile:
                        startActivity(new Intent(UserOrders.this, UserProfile.class));
                        break;
                    case R.id.nav_offers:
                        startActivity(new Intent(UserOrders.this, UserOfferList.class));
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(UserOrders.this, "Logged Out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserOrders.this, LoginActivity.class));
                        break;

                    case R.id.nav_previousOrders:
                        startActivity(new Intent(UserOrders.this, CartActivity.class));
                        break;
                    case R.id.nav_pending_orders:
                        startActivity(new Intent(UserOrders.this, UserOrders.class));
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


        //to retrieve name of the logged user in the navigation header // but this is not working :-(
//        View HeaderView = navigationView.getHeaderView(0);
//        TextView userNameTextView = HeaderView.findViewById(R.id.header_userName);

        //updateNavHeader();


    }


    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    class ProductsViewHolder extends RecyclerView.ViewHolder {

        private final TextView list_name;
        private final TextView list_price;
        private final TextView list_orderId;
        private final TextView list_ready;
        private final Button collectBtn;
        private final RelativeLayout parentLayout;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.shopTxtName);
            list_price = itemView.findViewById(R.id.shopTxtPrice);
            list_orderId = itemView.findViewById(R.id.shopTxtOrderId);
            list_ready = itemView.findViewById(R.id.shopTxtReady);
            collectBtn = itemView.findViewById(R.id.collectBtn);
            parentLayout = itemView.findViewById(R.id.user_order_item_list);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}



