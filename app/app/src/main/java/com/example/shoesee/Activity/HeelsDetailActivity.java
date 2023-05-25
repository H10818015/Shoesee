package com.example.shoesee.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoesee.Data.bootsData;
import com.example.shoesee.Data.heelsData;
import com.example.shoesee.R;
import com.example.shoesee.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class HeelsDetailActivity extends AppCompatActivity {

    ImageView cart,shoes,back_btn;
    TextView textViewBrand,textViewName,textViewPrice;
    Button addToCartBtn;
    Spinner size,quantity;
    private String id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heels_detail);

        id = getIntent().getStringExtra("id1");

        back_btn = findViewById(R.id.back_btn);
        addToCartBtn = findViewById(R.id.add_to_cart);
        shoes = findViewById(R.id.shoes2);
        textViewBrand = findViewById(R.id.brand2);
        textViewName = findViewById(R.id.shoes_name2);
        textViewPrice = findViewById(R.id.price2);
        size = findViewById(R.id.spinner_size_heels);
        quantity = findViewById(R.id.spinner_qty_heels);
        cart = findViewById(R.id.cart);

        getHeelsDetails(id);


        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(HeelsDetailActivity.this,ConfirmOrderActivity.class);
                startActivity(intent1);
            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HeelsDetailActivity.this,heelsActivity.class);
                startActivity(intent);
            }
        });

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToCartList();
            }
        });

    }

    private void getHeelsDetails(String CartDate) {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("shoes").child("high heels");
        productRef.child(CartDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    heelsData heelsData = dataSnapshot.getValue(com.example.shoesee.Data.heelsData.class);

                    textViewBrand.setText(heelsData.getBrand());
                    textViewPrice.setText(heelsData.getPrice());
                    textViewName.setText(heelsData.getName());
                    Picasso.get().load(heelsData.getImg()).into(shoes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addingToCartList() {

        SessionManager sessionManager = new SessionManager(this,SessionManager.SESSION_USERSESSION);
        HashMap<String,String> userDetail = sessionManager.getUsersDetailFromSession();

        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pbrand", textViewBrand.getText().toString());
        cartMap.put("pname", textViewName.getText().toString());
        cartMap.put("price", textViewPrice.getText().toString());
        cartMap.put("psize", size.getSelectedItem().toString());
        cartMap.put("id", id);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", quantity.getSelectedItem().toString());

        cartListRef.child(userDetail.get(SessionManager.KEY_USERNAME))
                .child("Products").child(id)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(HeelsDetailActivity.this, "已加入購物車", Toast.LENGTH_SHORT).show();

                    }
                });

    }

}