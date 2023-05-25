package com.example.shoesee.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.shoesee.R;
import com.example.shoesee.SessionManager;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    TextInputLayout fullName,email,phoneNo,password;
    TextView fullNameLabel,usernameLabel;
    Button logout,home;
    ;

    String _USERNAME, _NAME, _EMAIL, _PHONENO, _PASSWORD;

    DatabaseReference reference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile,container,false);

        reference = FirebaseDatabase.getInstance().getReference("users");


        //Hooks
        fullName = root.findViewById(R.id.full_name_profile);
        email = root.findViewById(R.id.email_profile);
        phoneNo = root.findViewById(R.id.phone_no_profile);
        password = root.findViewById(R.id.password_profile);
        fullNameLabel = root.findViewById(R.id.full_name_field);
        usernameLabel = root.findViewById(R.id.user_name_field);
        logout = root.findViewById(R.id.logout);
        home = root.findViewById(R.id.home);


        //Show All Data
        shoeAllUserData();
        return root;
    }

    private void shoeAllUserData() {

        SessionManager sessionManager = new SessionManager(getActivity(),SessionManager.SESSION_USERSESSION);
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

    }

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

    }*/
}