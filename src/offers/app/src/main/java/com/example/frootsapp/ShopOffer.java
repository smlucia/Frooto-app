package com.example.frootsapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.frootsapp.Model.Offer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ShopOffer extends AppCompatActivity {

    private static final String TAG = "TAG";
    private TextView name;
    private TextView promo;
    private TextView description;
    private ImageView image;
    private AlertDialog alertDialog;

    private Button btnDelete, btnUpdate;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    Offer offer1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_offer_update);
        name = findViewById(R.id.shopOfferName);
        promo = findViewById(R.id.shopOfferPromo);
        image = findViewById(R.id.offer_image_shop);
        description = findViewById(R.id.shopOfferDescription);
        btnDelete = findViewById(R.id.offerBtnDelete);
        btnUpdate = findViewById(R.id.offerBtnUpdate);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        //to get image
        fStore.collection("offer").document(getIntent().getStringExtra("offername"))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                com.example.frootsapp.Model.Offer offer = documentSnapshot.toObject(com.example.frootsapp.Model.Offer.class);
                Picasso.get().load(offer.getOfferimage()).into(image);
            }
        });


        offer1 = new Offer();

        name.setText("Offer Name : " + getIntent().getStringExtra("offername"));
        promo.setText("Promo Code : " + getIntent().getStringExtra("promocode"));
        description.setText("Offer Description : " + getIntent().getStringExtra("offerdescription"));
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem();
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText(getIntent().getStringExtra("offername"));
                promo.setText(getIntent().getStringExtra("promocode"));
                description.setText(getIntent().getStringExtra("offerdescription"));

                Intent intent = new Intent(v.getContext(), UpdateOffer.class);
                intent.putExtra("offername", name.getText().toString());
                intent.putExtra("promocode", promo.getText().toString());
                intent.putExtra("offerdescription", description.getText().toString());
                startActivity(intent);

            }
        });
    }

    private void navigaeShopList() {
        Intent intent_1 = new Intent(this, ShopProductList.class);
        startActivity(intent_1);
    }

    private void deleteItem() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to delete this file ?")
                .setPositiveButton(R.string.alert_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.i("MainActivity", "You choose Yes");

                        FirebaseFirestore.getInstance().collection("offer").document(getIntent().getStringExtra("offername"))
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                navigaeShopList();
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.alert_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("MainActivity", "You choose No");
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}