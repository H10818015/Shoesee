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

import com.example.shoesee.MainPage;
import com.example.shoesee.R;
import com.example.shoesee.ViewHolder.bootsViewHolder;
import com.example.shoesee.Data.bootsData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class bootsActivity extends AppCompatActivity {

    private DatabaseReference ProductsRef;
    RecyclerView recyclerView;
    ImageView back_btn,cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boots);

        back_btn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.recyclerView);
        cart = findViewById(R.id.cart_boots);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("shoes").child("boots");

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(bootsActivity.this,ConfirmOrderActivity.class);
                startActivity(intent1);
            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(bootsActivity.this,MainPage.class);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<bootsData> options =
                new FirebaseRecyclerOptions.Builder<bootsData>()
                        .setQuery(ProductsRef, bootsData.class)
                        .build();


        FirebaseRecyclerAdapter<bootsData, bootsViewHolder> adapter =
                new FirebaseRecyclerAdapter<bootsData, bootsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull bootsViewHolder holder, int position, @NonNull bootsData model)
                    {
                        holder.txtBrand.setText(model.getBrand());
                        holder.txtName.setText(model.getName());
                        holder.txtPrice.setText(model.getPrice());
                        Picasso.get().load(model.getImg()).error(R.drawable.boots).into(holder.imageView);

                        Log.d("Sucess",model.getId());



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(bootsActivity.this,BootsDetailActivity.class);
                                intent.putExtra("id",model.getId());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public bootsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_rv_item_latout, parent, false);
                        bootsViewHolder holder = new bootsViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}