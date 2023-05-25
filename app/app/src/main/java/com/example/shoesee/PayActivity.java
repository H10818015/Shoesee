package com.example.shoesee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.shoesee.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PayActivity extends AppCompatActivity {

    String name, address, phone, city, area;
    RecyclerView recyclerView;
    Button pay_cancel, pay_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        recyclerView = findViewById(R.id.recyclerView_confirm);
        pay_cancel = findViewById(R.id.pay_cancel);
        pay_confirm = findViewById(R.id.pay_confirm_btn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pay_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PayActivity.this,MainPage.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        SessionManager sessionManager = new SessionManager(this, SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetail = sessionManager.getUsersDetailFromSession();


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart1> option =
                new FirebaseRecyclerOptions.Builder<Cart1>()
                        .setQuery(cartListRef.child(userDetail.get(SessionManager.KEY_USERNAME))
                        .child("Products"), Cart1.class)
                        .build();

        FirebaseRecyclerAdapter<Cart1, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart1, CartViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart1 model) {

                holder.txtName.setText(model.getPname());
                holder.txtPrice.setText(model.getPrice());
                holder.txtQuantity.setText(model.getQuantity());
                holder.txtSize.setText(model.getPsize());

            }


            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.confirm_item, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }



}
