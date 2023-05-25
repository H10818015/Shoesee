package com.example.shoesee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class Login extends AppCompatActivity {

    Button callSignUp, login_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username, password;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    CheckBox rememberMe;
    EditText usernameEditText,passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //設定隱藏標題
        //getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);


        //All elements Hooks
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        callSignUp = findViewById(R.id.signup_screen);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username_login);
        password = findViewById(R.id.password_login);
        login_btn = findViewById(R.id.login_btn);
        usernameEditText = findViewById(R.id.login_username_edittext);
        passwordEditText = findViewById(R.id.login_password_edittext);
        rememberMe = findViewById(R.id.remember_me);

        SessionManager sessionManager = new SessionManager(Login.this,SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String,String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
            usernameEditText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONUSERNAME));
            passwordEditText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));

        }


    }

    private Boolean validateUsername() {
        String val = username.getEditText().getText().toString();
        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();
        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public void loginUser(View view) {
        //Validate Login Info
        if (!validateUsername() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {
        progressBar.setVisibility(View.VISIBLE);
        //Get users Field values
        final String userEnteredUsername = username.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();

        if (rememberMe.isChecked()){
            SessionManager sessionManager = new SessionManager(Login.this,SessionManager.SESSION_REMEMBERME);
            sessionManager.createRememberMeSession(userEnteredUsername,userEnteredPassword);
        }





        //Set Firebase Root reference
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    username.setError(null);
                    username.setErrorEnabled(false);
                    String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userEnteredPassword)) {
                        username.setError(null);
                        username.setErrorEnabled(false);
                        String nameFromDB = dataSnapshot.child(userEnteredUsername).child("name").getValue(String.class);
                        String emailFromDB = dataSnapshot.child(userEnteredUsername).child("email").getValue(String.class);
                        String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
                        String phoneNoFromDB = dataSnapshot.child(userEnteredUsername).child("phoneNo").getValue(String.class);

                        //Create Session
                        SessionManager sessionManager = new SessionManager(Login.this,SessionManager.SESSION_USERSESSION);
                        sessionManager.createLoginSession(nameFromDB,usernameFromDB,emailFromDB,passwordFromDB,phoneNoFromDB);

                        startActivity(new Intent(getApplicationContext(), MainPage.class));

                    } else {
                        progressBar.setVisibility(View.GONE);
                        password.setError("Wrong Password");
                        password.requestFocus();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    username.setError("No such User exist");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //Call SignUp Screen
    public void callSignUpScreen(View view) {
        //To call next activity
        Intent intent = new Intent(Login.this, SignUp.class);

        //create pairs for animation
        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair<View, String>(image, "logo_image"); //1st one is the element and 2nd is the transition name of animation.
        pairs[1] = new Pair<View, String>(logoText, "logo_text");
        pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
        pairs[3] = new Pair<View, String>(username, "username_tran");
        pairs[4] = new Pair<View, String>(password, "password_tran");
        pairs[5] = new Pair<View, String>(login_btn, "button_tran");
        pairs[6] = new Pair<View, String>(callSignUp, "login_signup_tran");

        //Call next activity by attaching the animation with it.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
            startActivity(intent, options.toBundle());
        }
    }

}