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

import com.example.shoesee.ViewHolder.sneakersViewHolder;
import com.example.shoesee.MainPage;
import com.example.shoesee.R;
import com.example.shoesee.Data.sneakersData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class sneakersActivity extends AppCompatActivity {

    private DatabaseReference ProductsRef;
    RecyclerView recyclerView;
    ImageView back_btn, cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sneakers);

        back_btn = findViewById(R.id.back_btn);
        cart = findViewById(R.id.cart_sneakers);
        recyclerView = findViewById(R.id.recyclerView_sneakers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("shoes").child("sneakers");


        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(sneakersActivity.this,ConfirmOrderActivity.class);
                startActivity(intent1);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sneakersActivity.this, MainPage.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<sneakersData> options =
                new FirebaseRecyclerOptions.Builder<sneakersData>()
                        .setQuery(ProductsRef, sneakersData.class)
                        .build();


        FirebaseRecyclerAdapter<sneakersData, sneakersViewHolder> adapter =
                new FirebaseRecyclerAdapter<sneakersData, sneakersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull sneakersViewHolder holder, int position, @NonNull sneakersData model)
                    {
                        holder.txtBrand.setText(model.getBrand());
                        holder.txtName.setText(model.getName());
                        holder.txtPrice.setText(model.getPrice());
                        Picasso.get().load(model.getImg()).error(R.drawable.sport_shoes).into(holder.imageView);

                        Log.d("Sucess",model.getId());



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(sneakersActivity.this,SneakersDetailActivity.class);
                                intent.putExtra("id3",model.getId());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public sneakersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_rv_item_latout, parent, false);
                        sneakersViewHolder holder = new sneakersViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}