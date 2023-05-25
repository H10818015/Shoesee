package com.example.shoesee.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shoesee.MainPage;
import com.example.shoesee.PayActivity;
import com.example.shoesee.R;
import com.example.shoesee.SessionManager;
import com.example.shoesee.VerifyPhoneNo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity {

    Button cancel, confirm;
    TextInputLayout regOrderName, regOrderPhoneNo, regOrderAddress;
    Spinner spinner_city,spinner_city_area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        confirm = findViewById(R.id.order_confirm_btn);
        cancel = findViewById(R.id.order_cancel);
        regOrderName = findViewById(R.id.reg_order_name);
        regOrderAddress = findViewById(R.id.reg_order_address);
        regOrderPhoneNo = findViewById(R.id.reg_order_phone);
        spinner_city = findViewById(R.id.spinner_city);
        spinner_city_area = findViewById(R.id.spinner_city_area);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.Spinner_city));
        spinner_city.setAdapter(adapter);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validateOrderName() | !validateOrderAddress() | !validateOrderPhoneNo()){
                    return;
                }
                addToOrderList();

            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, MainPage.class);
                startActivity(intent);
            }
        });

        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                selectedaddress(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }



    private void addToOrderList() {

        SessionManager sessionManager = new SessionManager(this,SessionManager.SESSION_USERSESSION);
        HashMap<String,String> userDetail = sessionManager.getUsersDetailFromSession();

        String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());


        final HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("01_name", regOrderName.getEditText().getText().toString());
        cartMap.put("05_address", regOrderAddress.getEditText().getText().toString());
        cartMap.put("02_phone", regOrderPhoneNo.getEditText().getText().toString());
        cartMap.put("03_city", spinner_city.getSelectedItem().toString());
        cartMap.put("04_area", spinner_city_area.getSelectedItem().toString());
        cartMap.put("06_date", saveCurrentDate);
        cartMap.put("07_time", saveCurrentTime);


        AlertDialog dialog = new AlertDialog.Builder(OrderActivity.this)
                .setTitle("請確認資訊無誤!!")
                .setMessage("訂購人 : "+regOrderName.getEditText().getText().toString()+"\n"+"電話 : "+regOrderPhoneNo.getEditText().getText().toString()+"\n"
                +spinner_city.getSelectedItem().toString()  +spinner_city_area.getSelectedItem().toString()+"\n"+regOrderAddress.getEditText().getText().toString())
                .setNegativeButton("取消", (dialog1, which) -> dialog1.dismiss())
                .setPositiveButton("確認",(dialog2, which) -> {
                    Intent intent = new Intent(OrderActivity.this,PayActivity.class);
                    startActivity(intent);
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child(userDetail.get(SessionManager.KEY_USERNAME))
                            .child("information")
                            .updateChildren(cartMap);

                    dialog2.dismiss();
                }).create();
        dialog.show();


    }

    private Boolean validateOrderName() {
        String val = regOrderName.getEditText().getText().toString();

        if (val.isEmpty()) {
            regOrderName.setError("請輸入姓名");
            return false;
        } else {
            regOrderName.setError(null);
            regOrderName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateOrderAddress() {
        String val = regOrderAddress.getEditText().getText().toString();

        if (val.isEmpty()) {
            regOrderAddress.setError("請輸入地址");
            return false;
        } else {
            regOrderAddress.setError(null);
            regOrderAddress.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateOrderPhoneNo() {
        String val = regOrderPhoneNo.getEditText().getText().toString();

        if (val.isEmpty()) {
            regOrderPhoneNo.setError("請輸入電話");
            return false;
        } else {
            regOrderPhoneNo.setError(null);
            regOrderPhoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private void selectedaddress(int position) {
        if (position == 0) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.taipei_city)));
        }

        else if (position == 1) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.new_taipei_city)));
        }

        else if (position == 2) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.taoyuan_city)));
        }

        else if (position == 3) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.keelung_city)));
        }

        else if (position == 4) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.hsinchu_county)));
        }

        else if (position == 5) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.hsinchu_city)));
        }

        else if (position == 6) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.yilan_county)));
        }

        else if (position == 7) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.hualien_county)));
        }

        else if (position == 8) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.taitung_county)));
        }

        else if (position == 9) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.taichung_city)));
        }

        else if (position == 10) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.miaoli_county)));
        }

        else if (position == 11) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.nantou_county)));
        }

        else if (position == 12) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.yunlin_county)));
        }

        else if (position == 13) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.changhua_county)));
        }

        else if (position == 14) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.tainan_city)));
        }

        else if (position == 15) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.kaohsiung_city)));
        }

        else if (position == 16) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.chiayi_county)));
        }

        else if (position == 17) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.chiayi_city)));
        }

        else if (position == 18) {

            spinner_city_area.setAdapter(new ArrayAdapter(getApplicationContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    getResources().getStringArray(R.array.pingtung_county)));
        }
    }

}


