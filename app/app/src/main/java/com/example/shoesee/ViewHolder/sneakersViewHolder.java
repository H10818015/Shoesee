package com.example.shoesee.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoesee.ItemClickListener;
import com.example.shoesee.R;

public class sneakersViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

    public TextView txtBrand, txtName, txtPrice;
    public ImageView imageView;
    public ItemClickListener listener;

    public sneakersViewHolder(@NonNull View itemView) {
        super(itemView);
        txtBrand = itemView.findViewById(R.id.brand0);
        txtName = itemView.findViewById(R.id.shoes_name0);
        txtPrice = itemView.findViewById(R.id.price0);
        imageView = itemView.findViewById(R.id.imageView);

    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }


    @Override
    public void onClick(View view) {

        listener.onClick(view, getBindingAdapterPosition(), false);

    }
}
