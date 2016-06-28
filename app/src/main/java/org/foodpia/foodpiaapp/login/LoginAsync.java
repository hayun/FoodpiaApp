package org.foodpia.foodpiaapp.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Administrator on 2016-06-07.
 * 로그인을 위한 async
 */
public class LoginAsync extends AsyncTask<String, Void, String> {
    Context context;
    String TAG = this.getClass().getName();
    URL url;
    HttpURLConnection con;
    BufferedWriter buffw;
    BufferedReader buffr;
    int code;
    StringBuffer fmember;

    public LoginAsync(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuffer sb = new StringBuffer();
        try {
            url = new URL(params[0]); //2_5 ~~~8889/device/rest/board, 편지봉투 역할
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            //2_5 data의 양이 많을 수 있으니 buffer 계열 사용
            buffw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("email", params[1]);
            jsonObject.put("pwd", params[2]);

            buffw.write(jsonObject.toString());
            Log.d(TAG, "sb의 값은?" + jsonObject.toString());
            buffw.flush(); //출력 계열의 마무리에 써야함;;;;;

            /////////////////////////////////////////로그인 후 fmember_id 받아오기
            code = con.getResponseCode();
            Log.d(TAG, "code=? " + code);

            fmember = new StringBuffer();


            if(code==HttpURLConnection.HTTP_OK) {
                buffr = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                String data = null;
                while (true) {
                    data = buffr.readLine();
                    if (data == null) break;
                    fmember.append(data);

                }
                buffr.close();
            }
            buffw.close();
            Log.d(TAG, "fmember is "+ fmember);

            con.disconnect();
            con=null;
            url=null;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(fmember==null) {
            fmember.append("break");
        }
        return fmember.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        if(s=="break"){
            Toast.makeText(context, "로그인실패", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(context, LoginActivity.class);
            return;
        }
        int fmember_id=jsonParse(s);
        super.onPostExecute(s);
        if(code==HttpURLConnection.HTTP_OK) {
            Toast.makeText(context, "로그인성공", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, org.foodpia.foodpiaapp.MainActivity.class);
            intent.putExtra("fmember_id", fmember_id);
            context.startActivity(intent);
        }else{
            Toast.makeText(context, "로그인실패", Toast.LENGTH_SHORT).show();
        }
    }

    public int jsonParse(String fmember){
        int fmember_id=0;
        try {
            JSONObject jsonObject=new JSONObject(fmember);
            fmember_id=jsonObject.getInt("fmember_id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fmember_id;
    }
}
