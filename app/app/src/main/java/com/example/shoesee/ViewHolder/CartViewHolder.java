package com.example.shoesee.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoesee.ItemClickListener;
import com.example.shoesee.R;

import java.util.ArrayList;
import java.util.List;


public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtName, txtPrice,txtQuantity, txtSize;
    private ItemClickListener itemClickListener;
    public ImageView addBtn,minusBtn;


    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtName = itemView.findViewById(R.id.item_name);
        txtPrice = itemView.findViewById(R.id.item_price);
        txtQuantity = itemView.findViewById(R.id.cart_qty);
        txtSize = itemView.findViewById(R.id.size);
        addBtn = itemView.findViewById(R.id.add_btn);
        minusBtn = itemView.findViewById(R.id.minus_btn);


    }


    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getBindingAdapterPosition(), false);

    }

}
