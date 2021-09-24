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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ShopProductList extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    FirebaseUser currentUser;
    FirebaseAuth fAuth;

    FloatingActionButton BtnAddItem;


    private FirebaseFirestore firebaseFirestore;
    private RecyclerView itemList;
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product_list);

        firebaseFirestore = FirebaseFirestore.getInstance();
        itemList = findViewById(R.id.recycler_view);
        BtnAddItem = findViewById(R.id.BtnAddItem);

        Query query = firebaseFirestore.collection("item");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();

        BtnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateAddProduct();
                //shared animation
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ShopProductList.this, ViewCompat.getTransitionName());
            }
        });


        adapter = new FirestoreRecyclerAdapter<Product, ShopProductList.ProductsViewHolder>(options) {

            @NonNull
            @Override
            public ShopProductList.ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
                return new ShopProductList.ProductsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ProductsViewHolder holder, int position, @NonNull final Product model) {
                holder.list_name.setText("Name: " + model.getName());
                holder.list_price.setText("Price: " + model.getPrice());
                holder.list_description.setText("Description: " + model.getDescription());
                Picasso.get().load(model.getImage()).into(holder.imageM);

                holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ShopProductList.this, Item.class);
                        intent.putExtra("name", model.getName());
                        intent.putExtra("price", model.getPrice());
                        intent.putExtra("description", model.getDescription());
//                        intent.putExtra("image", model.getImage());
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

                        Intent intent = new Intent(ShopProductList.this, ShopProductList.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_Profile:
                        startActivity(new Intent(ShopProductList.this, ShopProfileActivity.class));
                        break;
                    case R.id.nav_pending_orders:
                        startActivity(new Intent(ShopProductList.this, ShopOrderList.class));
                        break;
                    case R.id.nav_offers:
                        startActivity(new Intent(ShopProductList.this, ShopOfferList.class));
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(ShopProductList.this, "Logged Out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ShopProductList.this, LoginActivity.class));
                        break;

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

    private class ProductsViewHolder extends RecyclerView.ViewHolder {

        private final TextView list_name;
        private final TextView list_price;
        private final TextView list_description;
        private final ImageView imageM;
        private final RelativeLayout parentLayout;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            list_name = itemView.findViewById(R.id.txtName);
            list_price = itemView.findViewById(R.id.txtPrice);
            list_description = itemView.findViewById(R.id.txtDescription);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            imageM = itemView.findViewById(R.id.product_image);

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

    public void navigateAddProduct() {
        Intent intent_1 = new Intent(this, AddProductActivity.class);
        startActivity(intent_1);
    }

}