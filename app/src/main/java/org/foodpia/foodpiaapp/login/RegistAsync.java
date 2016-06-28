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
import java.net.URL;

/**
 * Created by kimgeunho on 2016-06-15.
 */
public class RegistAsync extends AsyncTask<String, Void, String>{
    Context context;

    public RegistAsync(Context context) {
        this.context = context;
    }

    String TAG=this.getClass().getName();
    URL url;
    HttpURLConnection con;
    BufferedWriter buffw;

    @Override
    protected String doInBackground(String... params) {
        try {
            JSONObject jsonObject = new JSONObject();

            url = new URL("http://bbungbbunge.cafe24.com/device/user.do");
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type","application/json");
            con.setRequestProperty("Accept", "application/json");

            jsonObject.put("email", params[0]);
            jsonObject.put("nickname", params[1]);
            jsonObject.put("phone", params[2]);
            if(params[3]!=null){
                jsonObject.put("id", params[3]);
            }

            buffw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
            buffw.write(jsonObject.toString());
            buffw.flush();
            buffw.close();

            int resp = con.getResponseCode();
            Log.d(TAG, "" + resp);

            con.disconnect();
            con = null;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Intent intent=new Intent(context, org.foodpia.foodpiaapp.MainActivity.class);
        context.startActivity(intent);
    }
}
