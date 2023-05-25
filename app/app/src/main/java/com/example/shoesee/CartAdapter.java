package com.example.shoesee;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;


public class CartAdapter{

    public class Cartviewholder extends RecyclerView.ViewHolder{

        public TextView itemName, itemPrice;
        public Spinner itemQuantity;
        private ItemClickListener itemClickListener;

        public Cartviewholder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemQuantity = itemView.findViewById(R.id.spinner_qty);
        }
    }

}
