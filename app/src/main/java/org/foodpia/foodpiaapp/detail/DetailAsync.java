package org.foodpia.foodpiaapp.detail;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kimgeunho on 2016-06-16.
 */
public class DetailAsync extends AsyncTask<Integer, Void, String>{
    String TAG=this.getClass().getName();
    URL url;
    HttpURLConnection con;

    Context context;

    BufferedReader buffr;
    StringBuffer sb;

    String user;
    String register;

    String stateIcon;

    public DetailAsync(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Integer... params) {
        try {
            url=new URL("http://bbungbbunge.cafe24.com/device/food/"+params[0]+".do");
            con= (HttpURLConnection) url.openConnection();

            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type","application/json");
            con.setRequestProperty("Accept", "application/json");

            user=Integer.toString(params[1]);

            Log.d("MainActivity","food_id와 fmember_id는? "+params[0]+"//"+params[1]);

            buffr=new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));

            String data=null;
            sb=new StringBuffer();

            while (true){
                data=buffr.readLine();
                if(data==null)break;
                sb.append(data);
            }

            Log.d(TAG, sb.toString());
            JSONObject jsonObject=new JSONObject(sb.toString());
            register=jsonObject.getString("fmember_id");

            int code=con.getResponseCode();

            Log.d(TAG, ""+code);

            buffr.close();
            con.disconnect();
            con=null;
            url=null;

            url=new URL("http://bbungbbunge.cafe24.com/device/like/"+register+"/"+user+".do");
            con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setRequestProperty("Content-Type","application/json");
            con.setRequestProperty("Accept","application/json");

            int existCode=con.getResponseCode();
            if(existCode==HttpURLConnection.HTTP_OK){
                Log.d(TAG, "좋아요 누름");
                stateIcon="likeit";
            }else{
                Log.d(TAG, "좋아요 안누름");
                stateIcon="unlikeit";
            }

            con.disconnect();
            con=null;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        Intent intent=null;

        try {
            JSONObject jsonObject=new JSONObject(s.toString());
            JSONObject member=jsonObject.getJSONObject("fmember");
            JSONObject photo=jsonObject.getJSONObject("photo");
            JSONObject location=jsonObject.getJSONObject("location");

            String title=jsonObject.getString("title");
            String content=jsonObject.getString("content");
            String photo_id=jsonObject.getString("photo_id");
            String food_id=jsonObject.getString("food_id");
            String location_id=location.getString("location_id");
            String fmember_id=member.getString("fmember_id");

            String nickname=member.getString("nickname");

            String filename=photo.getString("filename");

            String ext=filename.substring(filename.lastIndexOf(".")+1,filename.length());

            if(user.equals(register)){       //등록자가 상세페이지를 열때
                intent=new Intent(context, Layout_detail_register.class);

                intent.putExtra("title", title);
                intent.putExtra("content", content);
                intent.putExtra("photo_id", photo_id);
                intent.putExtra("nickname", nickname);
                intent.putExtra("filename",food_id+"."+ext);

                //delete할때 사용할 아이디
                intent.putExtra("food_id",food_id);
                intent.putExtra("location_id", location_id);
                intent.putExtra("photo_id", photo_id);
                intent.putExtra("fmember_id", fmember_id);

                context.startActivity(intent);
            }else{      //일반 유저가 상세페이지를 열때
                intent=new Intent(context, Layout_detail_user.class);

                intent.putExtra("title", title);
                intent.putExtra("content", content);
                intent.putExtra("photo_id", photo_id);
                intent.putExtra("nickname", nickname);
                intent.putExtra("filename",food_id+"."+ext);
                intent.putExtra("user", user);
                intent.putExtra("register", register);
                intent.putExtra("stateIcon", stateIcon);

                context.startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(s);
    }
}
