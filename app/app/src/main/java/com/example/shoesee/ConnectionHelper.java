package com.example.shoesee;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {

    Connection con;
    String name,pass,ip,port,database;

    public Connection connectionclass(){

        ip = "127.0.0.1";
        database = "item";
        name = "web";
        pass = "web123";
        port = "8888";


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;

        try{
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL= "jdbc:jtds:sqlserver://"+ ip + ":"+ port+";"+ "databasename="+ database+";user="+name+";password="+pass+";";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (Exception ex){
            Log.e("Error ", ex.getMessage());
        }
        return connection;
    }
}
