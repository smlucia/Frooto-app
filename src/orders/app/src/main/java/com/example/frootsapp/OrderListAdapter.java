package com.example.frootsapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.frootsapp.Model.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrderListAdapter extends ArrayAdapter<Order> {
    private static final String TAG = "TAG";
    private final Context mContext;
    private final int mResource;
    private ArrayList<Order> orders = new ArrayList<Order>();

    public OrderListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Order> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.orders = objects;
    }

    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int totAmount = getItem(position).getTotAmount();
        String name = getItem(position).getName();
        int price = getItem(position).getPrice();
        int qty = getItem(position).getQty();
        String userId = getItem(position).getUserId();
        String orderId = getItem(position).getOrderId();

        final FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        final Order order = new Order(name, totAmount, price, qty, userId, orderId);

        convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);

        TextView orderName = convertView.findViewById(R.id.orderName);
        TextView orderPrice = convertView.findViewById(R.id.orderPrice);
        TextView orderQty = convertView.findViewById(R.id.orderQty);
        TextView orderAmount = convertView.findViewById(R.id.orderAmount);
        Button deleteBtn = convertView.findViewById(R.id.orderDelete);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: delete " + orders.get(position).getOrderId());
                Toast.makeText(mContext, orders.get(position).getName() + " deleted!", Toast.LENGTH_SHORT).show();

                fStore.collection("tempOrder").document(orders.get(position).getOrderId())
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


                orders.remove(position);
                notifyDataSetChanged();
            }
        });

        orderName.setText(name);
        orderPrice.setText(price + "");
        orderQty.setText(qty + "");
        orderAmount.setText(totAmount + "");
        return convertView;
    }
}
