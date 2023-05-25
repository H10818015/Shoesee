package com.example.shoesee.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.shoesee.R;
import com.example.shoesee.Activity.bootsActivity;
import com.example.shoesee.Activity.heelsActivity;
import com.example.shoesee.Activity.slipperActivity;
import com.example.shoesee.Activity.sneakersActivity;

public class DashBoardFragment extends Fragment{

    private CardView bootsCard,heelsCard,slipperCard,sneakersCard;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.dashboard_fragment,container,false);
        bootsCard = root.findViewById(R.id.bootsCard);
        heelsCard = root.findViewById(R.id.heelsCard);
        slipperCard = root.findViewById(R.id.slipperCard);
        sneakersCard = root.findViewById(R.id.sneakersCard);

        bootsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),bootsActivity.class);
                getActivity().startActivity(intent);
            }
        });

        heelsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),heelsActivity.class);
                getActivity().startActivity(intent);
            }
        });

        slipperCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),slipperActivity.class);
                getActivity().startActivity(intent);
            }
        });

        sneakersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),sneakersActivity.class);
                getActivity().startActivity(intent);
            }
        });


        return root;
    }

}
