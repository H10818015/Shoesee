package com.example.shoesee.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shoesee.ViewHolder.CartViewHolder;
import com.example.shoesee.Cart1;
import com.example.shoesee.R;
import com.example.shoesee.SessionManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {

    RecyclerView orderRv;
    Button confirmBtn;
    ImageView imageView, addBtn, minusBtn;
    TextView txtPrice, txtQuantity, txtTotal;


    private int  TotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);


        orderRv = findViewById(R.id.order_rv);
        imageView = findViewById(R.id.uppage_btn);
        txtPrice = findViewById(R.id.total_text1);
        confirmBtn = findViewById(R.id.confirm_button);
        addBtn = findViewById(R.id.add_btn);
        minusBtn = findViewById(R.id.minus_btn);
        txtQuantity = findViewById(R.id.cart_qty);
        txtTotal = findViewById(R.id.total_text1);

        orderRv.setLayoutManager(new LinearLayoutManager(this));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmOrderActivity.this, OrderActivity.class);
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

            private int ItemSize;


            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart1 model) {

                holder.txtName.setText(model.getPname());
                holder.txtPrice.setText(model.getPrice());
                holder.txtQuantity.setText(model.getQuantity());
                holder.txtSize.setText(model.getPsize());
              //  Log.d("111", String.valueOf(TotalPrice));

                // 重新計算總價
                int itemPrice = Integer.parseInt(model.getPrice()) * Integer.parseInt(model.getQuantity());
                TotalPrice = 0;  // 先將 TotalPrice 設為 0
                for (int i = 0; i < getSnapshots().size(); i++) {
                    Cart1 cart = getSnapshots().get(i);
                    TotalPrice += (Integer.parseInt(cart.getPrice()) * Integer.parseInt(cart.getQuantity()));
                }
                txtTotal.setText(String.valueOf(TotalPrice));


                /*int oneTypeProductPrice = (Integer.valueOf(model.getPrice())) * Integer.valueOf(model.getQuantity());
                TotalPrice = TotalPrice + oneTypeProductPrice;
                txtTotal.setText(String.valueOf(TotalPrice));

                Log.d("one", String.valueOf(oneTypeProductPrice));
                Log.d("qty", model.getQuantity());
                Log.d("num", model.getPrice());
                Log.d("total", String.valueOf(TotalPrice));*/

               /* for (int i = 0; i < getSnapshots().size(); i++){
                    int num = Integer.parseInt(model.getQuantity());
                    int price = Integer.parseInt(model.getPrice());

                    TotalPrice = TotalPrice + (num * price);

                }*/


                //Log.d("Sucess", String.valueOf(TotalPrice));


                holder.addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Integer numQty = Integer.parseInt(String.valueOf(model.getQuantity()));
                        if (numQty > 4){
                            AlertDialog.Builder dialog1 = new AlertDialog.Builder(ConfirmOrderActivity.this)
                                    .setMessage("抱歉，本次結帳最多購買5件")
                                    .setNegativeButton("確認", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                            dialog1.show();
                        }else{
                            model.setQuantity(String.valueOf(numQty + 1));
                            holder.txtQuantity.setText(new StringBuilder().append(model.getQuantity()));
                            UpdateFirebase(model);

                            int itemPrice = Integer.parseInt(model.getPrice()) * Integer.parseInt(model.getQuantity());
                            TotalPrice += itemPrice;  // 將 TotalPrice 增加 itemPrice 的值
                            txtTotal.setText(String.valueOf(TotalPrice));
                        }
                    }

                    public void UpdateFirebase(Cart1 model) {
                        FirebaseDatabase.getInstance().getReference("Cart List")
                                .child(userDetail.get(SessionManager.KEY_USERNAME)).child("Products")
                                .child(model.getId()).setValue(model)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                    }
                });
              //  Log.d("222", String.valueOf(TotalPrice));

                holder.minusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Integer numQty = Integer.parseInt(String.valueOf(model.getQuantity()));
                        if (numQty <= 1){
                            AlertDialog dialog = new AlertDialog.Builder(ConfirmOrderActivity.this)
                                    .setMessage("你確認要刪除嗎?")
                                    .setNegativeButton("取消", (dialog1, which) -> dialog1.dismiss())
                                    .setPositiveButton("確認",(dialog2, which) -> {
                                        notifyItemRemoved(holder.getBindingAdapterPosition());
                                        FirebaseDatabase.getInstance().getReference("Cart List")
                                                .child(userDetail.get(SessionManager.KEY_USERNAME)).child("Products")
                                                .child(model.getId()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        TotalPrice -= itemPrice;  // 將 TotalPrice 增加 itemPrice 的值
                                                        txtTotal.setText(String.valueOf(TotalPrice));
                                                    }
                                                });
                                        dialog2.dismiss();
                                    }).create();
                            dialog.show();

                        }
                        else{
                            model.setQuantity(String.valueOf(numQty - 1));
                            holder.txtQuantity.setText(new StringBuilder().append(model.getQuantity()));
                            UpdateFirebase(model);

                            int itemPrice = Integer.parseInt(model.getPrice()) * Integer.parseInt(model.getQuantity());
                            TotalPrice -= itemPrice;  // 將 TotalPrice 減少 itemPrice 的值
                            txtTotal.setText(String.valueOf(TotalPrice));  // 更新 txtTotal 的顯示
                        }
                    }

                    public void UpdateFirebase(Cart1 model) {

                        FirebaseDatabase.getInstance().getReference("Cart List")
                                .child(userDetail.get(SessionManager.KEY_USERNAME)).child("Products")
                                .child(model.getId()).setValue(model)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                    }
                });
              //  Log.d("333", String.valueOf(TotalPrice));

                ItemSize = getSnapshots().size();
                /*for (int i = 0; i < ItemSize; i++){
                    TotalPrice += Integer.valueOf(model.getQuantity()) * Integer.parseInt(String.valueOf(model.getPrice()));
                    Log.d("666", String.valueOf(TotalPrice));
                    Log.d("777", String.valueOf(model.getTotal()));

                    txtTotal.setText(String.valueOf(TotalPrice));
                }

                Log.d("555" , String.valueOf(getSnapshots().size()));*/


                /*int oneTypeProductPrice = (Integer.valueOf(model.getPrice())) * Integer.valueOf(model.getQuantity());
                TotalPrice = TotalPrice + oneTypeProductPrice;
                txtTotal.setText(String.valueOf(TotalPrice));

                //Log.d("Sucess", model.getPname());


                ArrayList<Integer> arrayList = new ArrayList();

                for (int i = 0; i < ItemSize; i++){
                    arrayList.add(model.getTotal());
                    Log.d("Sucess", String.valueOf(arrayList));
                }
                //arrayList.add(model.getTotal());


                Log.d("Sucess", String.valueOf(ItemSize));

               // Log.d("Sucess", String.valueOf(arrayList));

                Log.d("Sucess", String.valueOf(arrayList));*/

            /*    TotalPrice = 0;
                for (int i = 0; i < ItemSize; i++){
                    int oneTypeProductPrice = (Integer.valueOf(model.getPrice())) * Integer.valueOf(model.getQuantity());
                    TotalPrice = TotalPrice + oneTypeProductPrice;
                }
                txtTotal.setText(String.valueOf(TotalPrice));*/





            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                txtTotal.setText(String.valueOf(TotalPrice));
                Log.d("Sucess", String.valueOf(TotalPrice));

            }


            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };


        orderRv.setAdapter(adapter);
        adapter.startListening();

    }
}

