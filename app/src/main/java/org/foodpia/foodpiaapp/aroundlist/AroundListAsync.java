package org.foodpia.foodpiaapp.aroundlist;

import android.os.AsyncTask;
import android.util.Log;

import org.foodpia.foodpiaapp.search.Food;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by thnt on 2016-06-15.
 */
public class AroundListAsync extends AsyncTask<String, Void, String>{
    String TAG = getClass().getName();
    BufferedReader buffr;
    StringBuffer sb = new StringBuffer();
    AroundListAdapter aroundListAdapter;

    public AroundListAsync(AroundListAdapter aroundListAdapter) {
        this.aroundListAdapter = aroundListAdapter;
    }

    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        try {
            url = new URL(params[0]);
            HttpURLConnection con = null;
            con = (HttpURLConnection)url.openConnection();
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.connect();

            buffr = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String data=null;
            while(true){
                data = buffr.readLine();
                if(data==null)break;
                sb.append(data+"\n");
            }
            buffr.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {

        try {
            JSONArray jsonArray = new JSONArray(s);
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                Log.d(TAG, "jsonObject는 너뭐냐"+jsonObject);

                Food food = new Food();

                food.food_id = jsonObject.getInt("food_id");
                food.title = jsonObject.getString("title");
                food.nickname = jsonObject.getJSONObject("fmember").getString("nickname");
                food.latitude = jsonObject.getJSONObject("location").getDouble("latitude");
                food.longitude = jsonObject.getJSONObject("location").getDouble("longitude");
                food.photo_id = jsonObject.getString("photo_id");
                food.filename = jsonObject.getJSONObject("photo").getString("filename");

                String ext = food.filename.substring(food.filename.lastIndexOf(".")+1, food.filename.length()); // filename을 바탕으로 확장자를 알아내자

                Log.d(TAG, "food_id : "+food.food_id);
                Log.d(TAG, "title : "+food.title);
                Log.d(TAG, "nickname : "+food.nickname);
                Log.d(TAG, "latitude : "+food.latitude);
                Log.d(TAG, "longitude : "+food.longitude);
                Log.d(TAG, "photo_id : "+food.photo_id);

                aroundListAdapter.foodList.add(food);

                AroundListImgAsync aroundListImgAsync = new AroundListImgAsync(aroundListAdapter);
                aroundListImgAsync.execute("http://bbungbbunge.cafe24.com/data/"+food.photo_id+"."+ext);
            }
            aroundListAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
