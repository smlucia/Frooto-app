package com.example.frootsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.example.frootsapp.Model.Product;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;
//import com.rey.material.widget.ImageView;
//import com.bumptech.glide.Glide;


public class HomeActivity extends AppCompatActivity {


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
        setContentView(R.layout.activity_home);

        firebaseFirestore = FirebaseFirestore.getInstance();
        itemList = findViewById(R.id.recycler_view);

        Query query = firebaseFirestore.collection("item");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();

        adapter = new FirestoreRecyclerAdapter<Product, ProductsViewHolder>(options) {
            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
                return new ProductsViewHolder(view);
            }
            /*public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listoffer, parent, false);
                return new ProductsViewHolder(view1);
            }*/

            @Override
            protected void onBindViewHolder(@NonNull final ProductsViewHolder holder, int position, @NonNull final Product model) {
                holder.list_name.setText("Name : " + model.getName());
                holder.list_price.setText("Price(LKR) : " + model.getPrice());
                holder.list_description.setText("Description : " + model.getDescription());
                Picasso.get().load(model.getImage()).into(holder.imageM);
                holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, OrderActivity.class);
                        intent.putExtra("name", model.getName());
                        intent.putExtra("price", model.getPrice());
                        intent.putExtra("description", model.getDescription());
                        startActivity(intent);
                    }
                });
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

                        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_Profile:
                        startActivity(new Intent(HomeActivity.this, UserProfile.class));
                        break;
                    case R.id.nav_pending_orders:
                        startActivity(new Intent(HomeActivity.this, UserOrders.class));
                        break;
                    case R.id.nav_offers:
                        startActivity(new Intent(HomeActivity.this, UserOfferList.class));
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(HomeActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        break;

                    case R.id.nav_previousOrders:
                        startActivity(new Intent(HomeActivity.this, CartActivity.class));
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
        private final TextView list_description;
        private final RelativeLayout parentLayout;
        private final ImageView imageM;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.txtName);
            list_price = itemView.findViewById(R.id.txtPrice);
            list_description = itemView.findViewById(R.id.txtDescription);
            imageM = itemView.findViewById(R.id.product_image);
            parentLayout = itemView.findViewById(R.id.parent_layout);
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



