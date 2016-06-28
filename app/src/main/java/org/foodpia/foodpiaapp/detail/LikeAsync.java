package org.foodpia.foodpiaapp.detail;

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
 * Created by kimgeunho on 2016-06-16.
 */
public class LikeAsync extends AsyncTask<String, Void, String>{
    String TAG=this.getClass().getName();
    URL url;
    HttpURLConnection con;
    BufferedWriter buffw;
    @Override
    protected String doInBackground(String... params) {
        try {
            url=new URL("http://bbungbbunge.cafe24.com/device/like.do");
            con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type","application/json");
            con.setRequestProperty("Accept","application/json");

            JSONObject jsonObject=new JSONObject();
            jsonObject.put("provider_id",params[0]);
            jsonObject.put("reviewer_id", params[1]);

            Log.d(TAG, jsonObject.toString());

            buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream(),"UTF-8"));
            buffw.write(jsonObject.toString());
            buffw.flush();
            buffw.close();

            int code=con.getResponseCode();
            Log.d(TAG, ""+code);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
