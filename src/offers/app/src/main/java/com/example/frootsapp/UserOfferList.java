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

import com.example.frootsapp.Model.Offer;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class UserOfferList extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    FirebaseUser currentUser;
    FirebaseAuth fAuth;


    private FirebaseFirestore firebaseFirestore;
    private RecyclerView offerList;
    private FirestoreRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        firebaseFirestore = FirebaseFirestore.getInstance();
        offerList = findViewById(R.id.recycler_view);


        Query query = firebaseFirestore.collection("offer");
        FirestoreRecyclerOptions<Offer> options = new FirestoreRecyclerOptions.Builder<Offer>().setQuery(query, Offer.class).build();


        adapter = new FirestoreRecyclerAdapter<Offer, UserOfferList.OffersViewHolder>(options) {

            @NonNull
            @Override
            public UserOfferList.OffersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listoffer, parent, false);
                return new UserOfferList.OffersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final OffersViewHolder holder, int position, @NonNull final Offer model) {
                holder.list_name.setText("Offer Name: " + model.getOffername());
                holder.list_promo.setText("Promo Code: " + model.getPromocode());
                holder.list_description.setText("Description: " + model.getOfferdescription());
                Picasso.get().load(model.getOfferimage()).into(holder.imageM);

                holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserOfferList.this, Offer.class);
                        intent.putExtra("name", model.getOffername());
                        intent.putExtra("price", model.getPromocode());
                        intent.putExtra("description", model.getOfferdescription());
                        //intent.putExtra("image", model.getImage());
                        startActivity(intent);
                    }
                });
            }
        };

        offerList.setHasFixedSize(true);
        offerList.setLayoutManager(new LinearLayoutManager(this));
        offerList.setAdapter(adapter);

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();


        setUpToolbar();
        navigationView = findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:

                        Intent intent = new Intent(UserOfferList.this, UserOfferList.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_Profile:
                        startActivity(new Intent(UserOfferList.this, ShopProfileActivity.class));
                        break;
                    case R.id.nav_pending_orders:
                        startActivity(new Intent(UserOfferList.this, ShopOrderList.class));
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(UserOfferList.this, "Logged Out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserOfferList.this, LoginActivity.class));
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

    private class OffersViewHolder extends RecyclerView.ViewHolder {

        private final TextView list_name;
        private final TextView list_promo;
        private final TextView list_description;
        private final ImageView imageM;
        private final RelativeLayout parentLayout;

        public OffersViewHolder(@NonNull View offerView) {
            super(offerView);

            list_name = offerView.findViewById(R.id.txtOfferName);
            list_promo = offerView.findViewById(R.id.txtPromoCode);
            list_description = offerView.findViewById(R.id.txtOfferDescription);
            parentLayout = offerView.findViewById(R.id.parent_layout);
            imageM = offerView.findViewById(R.id.offerImage);

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