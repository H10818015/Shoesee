package com.example.shoesee.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.shoesee.ViewHolder.heelsViewHolder;
import com.example.shoesee.Data.heelsData;
import com.example.shoesee.MainPage;
import com.example.shoesee.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class heelsActivity extends AppCompatActivity {

    private DatabaseReference ProductsRef;
    RecyclerView recyclerView;
    ImageView back_btn, cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heels);

        back_btn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.recyclerView_heels);
        cart = findViewById(R.id.cart_heels);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("shoes").child("high heels");

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(heelsActivity.this,ConfirmOrderActivity.class);
                startActivity(intent1);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(heelsActivity.this,MainPage.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<heelsData> options =
                new FirebaseRecyclerOptions.Builder<heelsData>()
                        .setQuery(ProductsRef, heelsData.class)
                        .build();


        FirebaseRecyclerAdapter<heelsData, heelsViewHolder> adapter =
                new FirebaseRecyclerAdapter<heelsData, heelsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull heelsViewHolder holder, int position, @NonNull heelsData model)
                    {
                        holder.txtBrand.setText(model.getBrand());
                        holder.txtName.setText(model.getName());
                        holder.txtPrice.setText(model.getPrice());
                        Picasso.get().load(model.getImg()).error(R.drawable.high_heels).into(holder.imageView);

                        Log.d("Sucess",model.getId());



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(heelsActivity.this,HeelsDetailActivity.class);
                                intent.putExtra("id1",model.getId());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public heelsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_rv_item_latout, parent, false);
                        heelsViewHolder holder = new heelsViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}