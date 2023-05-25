package com.example.shoesee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserProfile extends AppCompatActivity {


    TextInputLayout fullName,email,phoneNo,password;
    TextView fullNameLabel,usernameLabel;
    Button logout,home;
    ;

    String _USERNAME, _NAME, _EMAIL, _PHONENO, _PASSWORD;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //設定隱藏標題
        //getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);

        reference = FirebaseDatabase.getInstance().getReference("users");


        //Hooks
        fullName = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_no_profile);
        password = findViewById(R.id.password_profile);
        fullNameLabel = findViewById(R.id.full_name_field);
        usernameLabel = findViewById(R.id.user_name_field);
        logout = findViewById(R.id.logout);
        home = findViewById(R.id.home);


        //Show All Data
        shoeAllUserData();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfile.this,MainPage.class);
                startActivity(intent);
            }
        });

    }

    private void shoeAllUserData() {

        SessionManager sessionManager = new SessionManager(UserProfile.this,SessionManager.SESSION_USERSESSION);
        HashMap<String,String> userDetail = sessionManager.getUsersDetailFromSession();

        String _fullName = userDetail.get(SessionManager.KEY_FULLNAME);
        String _username = userDetail.get(SessionManager.KEY_USERNAME);
        String _email = userDetail.get(SessionManager.KEY_EMAIL);
        String _phoneNo = userDetail.get(SessionManager.KEY_PHONENO);
        String _password = userDetail.get(SessionManager.KEY_PASSWORD);

        fullNameLabel.setText(_fullName);
        usernameLabel.setText(_username);
        fullName.getEditText().setText(_fullName);
        email.getEditText().setText(_email);
        phoneNo.getEditText().setText(_phoneNo);
        password.getEditText().setText(_password);


    }

    /*public void update(View view) {

        if (isNameChanged() || isPasswordChanged()) {
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_LONG).show();
        }
        else Toast.makeText(this, "Data is same and can not be updated", Toast.LENGTH_LONG).show();

    }*/

    private boolean isPasswordChanged() {
        if (!_PASSWORD.equals(password.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("password").setValue(password.getEditText().getText().toString());
            _PASSWORD = password.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isNameChanged() {

        if (!_NAME.equals(fullName.getEditText().getText().toString())) {
            reference.child(_USERNAME).child("name").setValue(fullName.getEditText().getText().toString());
            _NAME = fullName.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }

    }



}