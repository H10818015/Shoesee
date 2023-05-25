package com.example.shoesee.ViewHolder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoesee.ApplicationMain;
import com.example.shoesee.OrderListModel;
import com.example.shoesee.R;
import com.example.shoesee.UpdateSelectedItem;

import java.util.ArrayList;

public class ConfirmOrderActivityAdapter extends RecyclerView.Adapter<ConfirmOrderActivityAdapter.ConfirmOrderViewHolder> {

    private ArrayList<OrderListModel> orderListModels;
    Activity activity;


    public ConfirmOrderActivityAdapter(Activity activity){
        this.activity = activity;
        orderListModels = ((UpdateSelectedItem) ApplicationMain.getMyContext()).getItem();

    }


    public  class ConfirmOrderViewHolder extends RecyclerView.ViewHolder{

        TextView name,price;
        public ConfirmOrderViewHolder(@NonNull View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.item_price);

        }
    }



    @NonNull
    @Override
    public ConfirmOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);
        ConfirmOrderViewHolder confirmOrderViewHolder = new ConfirmOrderViewHolder(view);
        return confirmOrderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConfirmOrderViewHolder holder, int position) {
        OrderListModel currentItem = orderListModels.get(position);
        holder.name.setText(currentItem.getName());

    }

    @Override
    public int getItemCount() {
        return orderListModels.size();
    }
}
