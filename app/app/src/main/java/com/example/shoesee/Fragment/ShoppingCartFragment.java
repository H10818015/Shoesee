package com.example.shoesee.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class ShoppingCartFragment extends Fragment {

    RecyclerView orderRv;
    Button confirmBtn;
    ImageView imageView;
    TextView txtPrice;
    int TotalPrice = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_shopping_cart,container,false);
        SessionManager sessionManager = new SessionManager(getActivity(),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> userDetail = sessionManager.getUsersDetailFromSession();

        orderRv = root.findViewById(R.id.order_rv);
        orderRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        orderRv = root.findViewById(R.id.order_rv);
        imageView = root.findViewById(R.id.uppage_btn);
        txtPrice = root.findViewById(R.id.total_text);
        confirmBtn = root.findViewById(R.id.confirm_button);

        return root;
    }
    @Override
    public void onStart() {
        super.onStart();

        SessionManager sessionManager = new SessionManager(getActivity(),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> userDetail = sessionManager.getUsersDetailFromSession();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart1> option =
                new FirebaseRecyclerOptions.Builder<Cart1>()
                        .setQuery(cartListRef.child("User View")
                                .child(userDetail.get(SessionManager.KEY_USERNAME)).child("Products"),Cart1.class)
                        .build();

        FirebaseRecyclerAdapter<Cart1, CartViewHolder> adapter
                =new FirebaseRecyclerAdapter<Cart1, CartViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart1 model) {

                holder.txtName.setText(model.getPname());
                holder.txtPrice.setText(model.getPrice());
                holder.txtQuantity.setText(model.getQuantity());
                holder.txtSize.setText(model.getPsize());


                int oneTypeProductPrice = (Integer.valueOf(model.getPrice()))*Integer.valueOf(model.getQuantity());
                TotalPrice = TotalPrice + oneTypeProductPrice;
                txtPrice.setText(String.valueOf(TotalPrice));

                Log.d("Sucess",model.getPname());


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        orderRv.setAdapter(adapter);
        adapter.startListening();

    }
}