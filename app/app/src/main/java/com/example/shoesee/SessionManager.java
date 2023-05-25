package com.example.shoesee;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import javax.sql.StatementEvent;

public class SessionManager {

    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;

    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "RememberMe";

    //User session
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHONENO = "phoneNo";
    public static final String KEY_USERNAME = "username";

    //remember me session
    private static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_SESSIONPASSWORD = "password";
    public static final String KEY_SESSIONUSERNAME = "username";



    public SessionManager(Context _context, String sessionName){
        context = _context;
        userSession = _context.getSharedPreferences(sessionName,Context.MODE_PRIVATE);
        editor = userSession.edit();
    }

    //Users Login Session

    public void createLoginSession(String fullName,String username,String email,String password,String phoneNo){

        editor.putBoolean(IS_LOGIN,true);

        editor.putString(KEY_FULLNAME,fullName);
        editor.putString(KEY_USERNAME,username);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_PASSWORD,password);
        editor.putString(KEY_PHONENO,phoneNo);

        editor.commit();

    }

    public HashMap<String,String> getUsersDetailFromSession(){
        HashMap<String, String> userData = new HashMap<String,String>();

        userData.put(KEY_FULLNAME,userSession.getString(KEY_FULLNAME,null));
        userData.put(KEY_USERNAME,userSession.getString(KEY_USERNAME,null));
        userData.put(KEY_EMAIL,userSession.getString(KEY_EMAIL,null));
        userData.put(KEY_PASSWORD,userSession.getString(KEY_PASSWORD,null));
        userData.put(KEY_PHONENO,userSession.getString(KEY_PHONENO,null));

        return userData;
    }

    public boolean checkLogin(){
        if (userSession.getBoolean(IS_LOGIN,false)){
            return true;
        }
        else
            return false;
    }

    public void  logoutUserFromSession(){
        editor.clear();
        editor.commit();
    }


    //Users Login Session

    public void createRememberMeSession(String username,String password){

        editor.putBoolean(IS_REMEMBERME,true);
        editor.putString(KEY_SESSIONUSERNAME,username);
        editor.putString(KEY_SESSIONPASSWORD,password);

        editor.commit();

    }

    public HashMap<String,String> getRememberMeDetailFromSession(){
        HashMap<String, String> userData = new HashMap<String,String>();

        userData.put(KEY_SESSIONUSERNAME,userSession.getString(KEY_SESSIONUSERNAME,null));
        userData.put(KEY_SESSIONPASSWORD,userSession.getString(KEY_SESSIONPASSWORD,null));

        return userData;
    }

    public boolean checkRememberMe(){
        if (userSession.getBoolean(IS_REMEMBERME,false)){
            return true;
        }
        else
            return false;
    }

}
