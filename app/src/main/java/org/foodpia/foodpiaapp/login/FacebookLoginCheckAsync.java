package org.foodpia.foodpiaapp.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kimgeunho on 2016-06-15.
 */
public class FacebookLoginCheckAsync extends AsyncTask<String, Void, String>{
    Context context;
    String TAG=this.getClass().getName();
    URL url, serverUrl;
    HttpURLConnection con;
    BufferedWriter buffw;
    int code;
    String email;

    public FacebookLoginCheckAsync(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            //이미 회원인 경우
            serverUrl = new URL("http://bbungbbunge.cafe24.com/device/user/emailCheck.do");
            con= (HttpURLConnection) serverUrl.openConnection();
            con.setConnectTimeout(7000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            String member=params[0].toString();
            JSONObject jsonObject=new JSONObject(member);
            email=jsonObject.getString("email");

            buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream(),"UTF-8"));
            buffw.write(member);
            //Log.d(TAG, "params is "+params[0]);
            buffw.flush();
            buffw.close();

            Log.d(TAG, "접속여부? "+serverUrl);

            code = con.getResponseCode();
            Log.d(TAG, "성공여부는? " + code);

            /* if(code==HttpURLConnection.HTTP_OK){
                Log.d(TAG, "이미 존재하는 이메일 주소입니다");
            }*/


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return email.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Intent intent=null;

        if(code==HttpURLConnection.HTTP_OK){
            intent = new Intent(context, org.foodpia.foodpiaapp.MainActivity.class);
        }else {
            intent = new Intent(context, RegistAdd_facebook.class);
            intent.putExtra("email", s);
        }
        context.startActivity(intent);
    }
}

