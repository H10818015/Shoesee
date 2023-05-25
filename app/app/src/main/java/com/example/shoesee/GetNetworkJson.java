package com.example.shoesee;
import android.os.AsyncTask;
import android.util.Log;

import com.example.shoesee.Fragment.IdentifyFragment;
import com.example.shoesee.Fragment.IdentifyFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetNetworkJson extends AsyncTask<String,Void,String> {
    String data = "";
    InputStream inputStream = null;
    @Override
    //doInBackground ： 執行中，在背景做任務。
    protected String doInBackground(String... urlstrings) {
        try {
            URL url = new URL(urlstrings[0]); //初始化
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(); //取得連線之物件
            InputStream inputStream = httpURLConnection.getInputStream();
            //輸入串流的代表物件InputStream//對取得的資料進行讀取
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //宣告一個型態為BufferedReader的物件變數
            //new BufferedReader表示以BufferedReader類別建構一個物件
            // new InputStreamReader(inputStream)
            //表示接受一個inputStream物件來建構一個InputStreamReader物件
            String message = "";
            String line = null;
            while ((line = bufferedReader.readLine()) != null) { //  line不等於空值的時候
                message += line.replaceAll("/n","\n");
                data = message;
                Log.i("錯誤",message);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        //onPostExecute： 執行後，最後的結果會在這邊。
        IdentifyFragment.messageText.setText(data);
    }
}
