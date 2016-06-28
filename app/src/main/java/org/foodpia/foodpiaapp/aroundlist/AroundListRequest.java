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
import java.util.ArrayList;

/**
 * Created by thnt on 2016-06-17.
 */
public class AroundListRequest {
    AroundListAsync aroundListAsync;
    OnAroundListRequestListener onAroundListRequestListener;
    String TAG = getClass().getName();
    BufferedReader buffr;
    String register;
    String user;
    String stateIcon;

    private class AroundListAsync extends AsyncTask<String, Void, Food> {

        @Override
        protected Food doInBackground(String... params) {
            Food food = null;
            String sb = downJson(params[0]);
            ArrayList<Food> foodList = jsonParse(sb);

            onAroundListRequestListener.success(foodList);
            return food;
        }

        @Override
        protected void onPostExecute(Food food) {
        }
    }

    public void getFoodList(String lati, String longi, OnAroundListRequestListener onAroundListRequestListener) {
        this.onAroundListRequestListener = onAroundListRequestListener;

        if (aroundListAsync != null) {
            aroundListAsync.cancel(true);
            aroundListAsync = null;
        }

        aroundListAsync = new AroundListAsync();
        aroundListAsync.execute("http://bbungbbunge.cafe24.com/device/food/"+lati+"/"+longi+".do");
    }

    public ArrayList<Food> jsonParse(String sb) {
        ArrayList<Food> foodList = new ArrayList<Food>();

        try {
            JSONArray jsonArray = new JSONArray(sb);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Log.d(TAG, "jsonObject는 너뭐냐" + jsonObject);

                Food food = new Food();

                food.food_id = jsonObject.getInt("food_id");
                food.title = jsonObject.getString("title");
                food.nickname = "찬수"; //jsonObject.getString("");
                food.latitude = jsonObject.getJSONObject("location").getDouble("latitude");
                food.longitude = jsonObject.getJSONObject("location").getDouble("longitude");
                food.photo_id = jsonObject.getString("photo_id");
                food.filename = jsonObject.getJSONObject("photo").getString("filename");
                food.fmember_id = jsonObject.getJSONObject("fmember").getInt("fmember_id");
                food.numberOfLikesFromFmember_id = jsonObject.getInt("likeit_sum");
                // 여기까지 food객체에 다 담았잖아.. 응? 그렇잖아요 !!

                /*Log.d(TAG, "food_id : " + food.food_id);
                Log.d(TAG, "title : " + food.title);
                Log.d(TAG, "nickname : " + food.nickname);
                Log.d(TAG, "latitude : " + food.latitude);
                Log.d(TAG, "longitude : " + food.longitude);
                Log.d(TAG, "photo_id : " + food.photo_id);*/

                foodList.add(food);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return foodList;
    }

    public String downJson(String urlStr) {
        StringBuffer sb = new StringBuffer();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection con = null;
            con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.connect();

            buffr = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String data = null;
            while (true) {
                data = buffr.readLine();
                if (data == null) break;
                sb.append(data + "\n");
            }
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
        }
        return sb.toString();
    }

}
