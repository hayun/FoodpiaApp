package org.foodpia.foodpiaapp.detail;

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
 * Created by kimgeunho on 2016-06-18.
 */
    public class DetailDeleteAsync extends AsyncTask<String, Void, String>{
    String TAG=this.getClass().getName();
    URL url;
    HttpURLConnection con;
    BufferedWriter buffw;
    Context context;

    public DetailDeleteAsync(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject jsonObject=new JSONObject();
        try {
            url=new URL("http://bbungbbunge.cafe24.com/device/food.do");
            con= (HttpURLConnection) url.openConnection();

            con.setRequestMethod("DELETE");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type","application/json");
            con.setRequestProperty("Accept","application/json");

            jsonObject.put("food_id", params[0]);
            jsonObject.put("fmember_id",params[1]);

            Log.d("MainActivity","con?"+con);

            Log.d("MainActivity","food_id와 fmember_id는? "+params[0]+"//"+params[1]);

            Log.d("MainActivity", jsonObject.toString());

            buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream(),"UTF-8"));
            Log.d("MainActivity", "응답1");
            buffw.write(jsonObject.toString());
            Log.d("MainActivity", "응답2");
            buffw.flush();
            Log.d("MainActivity", "응답3");

            int code=con.getResponseCode();
            Log.d("MainActivity", "응답코드는? "+code);

            buffw.close();
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
    protected void onPreExecute() {
        Intent intent=new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
