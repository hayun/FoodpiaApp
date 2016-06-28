package org.foodpia.foodpiaapp.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Administrator on 2016-06-07.
 */
public class SignupAsync extends AsyncTask<String, Void, String> {
    Context context;
    String TAG=this.getClass().getName();
    URL url;
    HttpURLConnection con;
    BufferedWriter buffw;

    public SignupAsync(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuffer sb=new StringBuffer();
        try {
            url=new URL(params[0]); //2_5 ~~~8889/device/rest/board, 편지봉투 역할
            con= (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(7000);
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            //2_5 data의 양이 많을 수 있으니 buffer 계열 사용
            buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
            JSONObject jsonObject=new JSONObject();

            try {
                jsonObject.put("nickname", params[2]);
                jsonObject.put("email", params[1]);
                jsonObject.put("pwd", params[3]);
                jsonObject.put("phone", params[4]);

                buffw.write(jsonObject.toString());
                Log.d(TAG, "sb의 값은?" + jsonObject.toString());
                buffw.flush(); //출력 계열의 마무리에 써야함;;;;;
                int code=con.getResponseCode();
                Log.d(TAG, "code=? "+code);
                buffw.close();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Toast.makeText(context, "가입성공", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(context, IntroActivity.class);
        context.startActivity(intent);
    }
}
