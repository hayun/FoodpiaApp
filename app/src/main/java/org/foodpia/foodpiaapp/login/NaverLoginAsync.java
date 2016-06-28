package org.foodpia.foodpiaapp.login;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.foodpia.foodpiaapp.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Created by kimgeunho on 2016-06-13.
 */
public class NaverLoginAsync extends AsyncTask<String, Void, String> {
    String TAG = this.getClass().getName();
    URL url, serverUrl;
    HttpURLConnection con;
    BufferedReader buffr, rebuffr;
    BufferedWriter buffw;
    StringBuffer sb, fmember;
    Context context;
    int existCode;

    public NaverLoginAsync(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject res=null;

        try {
            url = new URL(params[0]);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Host", "openapi.naver.com");
            con.setRequestProperty("User-Agent", "curl/7.43.0");
            con.setRequestProperty("Accept", "*/*");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + params[1]);
            con.setDoInput(true);
            con.setRequestMethod("GET");

            //Log.d(TAG, "접속여부? "+url);

            int code = con.getResponseCode();
            Log.d(TAG, "성공여부는? " + code);

            if (code == HttpURLConnection.HTTP_OK) {        //서버로 부터 json받아오기
                buffr = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                String data = null;
                sb = new StringBuffer();

                while (true) {
                    data = buffr.readLine();
                    if (data == null) break;
                    sb.append(data);
                }
                buffr.close();
                con.disconnect();
                con = null;
                url = null;

                JSONObject jsonObject = new JSONObject(sb.toString());
                res = jsonObject.getJSONObject("response");

                String email=res.getString("email");
                String id=res.getString("id");

                JSONObject reJsonObject =new JSONObject();
                reJsonObject.put("email", email);
                reJsonObject.put("id", id);

                Log.d(TAG, "rejson is "+ reJsonObject.toString());

                //이미 회원인 경우
                try {
                    serverUrl = new URL("http://bbungbbunge.cafe24.com/device/user/login.do");
                    con= (HttpURLConnection) serverUrl.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");

                    buffw=new BufferedWriter(new OutputStreamWriter(con.getOutputStream(),"UTF-8"));
                    buffw.write(reJsonObject.toString());
                    buffw.flush();
                    buffw.close();

                    Log.d(TAG, "접속여부? "+serverUrl);

                    existCode = con.getResponseCode();
                    Log.d(TAG, "성공여부는? " + existCode );



                    String member=null;
                    fmember=new StringBuffer();

                    if(existCode==HttpURLConnection.HTTP_OK){
                        rebuffr=new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));

                        while (true) {
                            member = rebuffr.readLine();
                            if(member==null)break;

                            fmember.append(member);
                        }
                        Log.d(TAG, "fmember is " +fmember.toString());

                        con.disconnect();
                        con=null;
                        url=null;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (sb == null) {
            Log.d(TAG, "정보가 넘어오지 않았음.");
            return null;
        } else {
            return res.toString();
        }
    }

    @Override
    protected void onPostExecute(String fmember) {
        Intent intent=null;
        try {
            JSONObject obj=new JSONObject(fmember.toString());
            String email=obj.getString("email");
            String nickname=obj.getString("nickname");
            String age=obj.getString("age");
            String gender=obj.getString("gender");
            String id=obj.getString("id");
            String name=obj.getString("name");
            String birthday=obj.getString("birthday");

            int fmember_id=jsonParse(fmember);

            if(existCode==HttpURLConnection.HTTP_OK){
                intent = new Intent(context, org.foodpia.foodpiaapp.MainActivity.class);
            }else {
                intent = new Intent(context, RegistAdd_naver.class);
            }
            intent.putExtra("email", email);
            intent.putExtra("nickname", nickname);
            intent.putExtra("age", age);
            intent.putExtra("gender", gender);
            intent.putExtra("id", id);
            intent.putExtra("name", name);
            intent.putExtra("birthday", birthday);
            intent.putExtra("fmember_id", fmember_id);

            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, fmember);
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
