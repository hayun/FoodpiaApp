package org.foodpia.foodpiaapp.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.foodpia.foodpiaapp.MainActivity;
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
public class FacebookLoginAsync extends AsyncTask<String, Void, String>{
    Context context;
    String TAG=this.getClass().getName();
    URL url, serverUrl;
    HttpURLConnection con;
    BufferedWriter buffw;
    boolean isEmail;

    public FacebookLoginAsync(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject jsonObject=new JSONObject();
        JSONObject res=null;
        try {
            url=new URL("http://bbungbbunge.cafe24.com/device/user.do");
            con= (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type","application/json");
            con.setRequestProperty("Accept","application/json");

            buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));

            jsonObject.put("email", params[0]);
            jsonObject.put("nickname", params[1]);
            jsonObject.put("phone", params[2]);
            jsonObject.put("id", params[3]);

            buffw.write(jsonObject.toString());
            buffw.flush();
            buffw.close();

            int code=con.getResponseCode();
            Log.d(TAG, "접속 여부? "+code);

            con.disconnect();
            con=null;

            //이미 회원인 경우
            serverUrl = new URL("http://bbungbbunge.cafe24.com/device/user/emailCheck.do");
            con= (HttpURLConnection) serverUrl.openConnection();
            con.setConnectTimeout(7000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream(),"UTF-8"));
            buffw.write(jsonObject.toString());
            buffw.flush();
            buffw.close();

            Log.d(TAG, "접속여부? "+serverUrl);

            int existCode = con.getResponseCode();
            Log.d(TAG, "성공여부는? " + existCode );

            if(existCode!=HttpURLConnection.HTTP_OK){
                Log.d(TAG, "이미 존재하는 이메일 주소입니다");
                isEmail=true;
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Intent intent=null;

        intent = new Intent(context, org.foodpia.foodpiaapp.MainActivity.class);

        context.startActivity(intent);
    }
}
