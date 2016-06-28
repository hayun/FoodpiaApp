package org.foodpia.foodpiaapp.search;

import android.os.AsyncTask;
import android.util.Log;

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
import java.util.List;

/**
 * Created by Yun on 2016-06-13.
 */
public class FoodSearcher {

    final String myURL="http://bbungbbunge.cafe24.com/device/food/%f/%f/%f/%f.do";
    FoodSearcherTask foodSearcherTask;
    OnFinishSearchFoodListener onFinishSearchFoodListener;



    private class FoodSearcherTask extends AsyncTask<String, Void, Void> {
        //푸드 찾기
        protected Void doInBackground(String... urls) {
            String url=urls[0];
            String json=getFoodListJson(url);
            List<Food> foodList=parsing(json);
            if (onFinishSearchFoodListener != null) {
                if (foodList == null) {
                    onFinishSearchFoodListener.onFail();
                } else {
                    onFinishSearchFoodListener.onSuccess(foodList);
                }
            }
            return null;
        }
    }

    public void searchFood(double latitude, double longitude, int zoomLevel, OnFinishSearchFoodListener onFinishSearchFoodListener){
        this.onFinishSearchFoodListener = onFinishSearchFoodListener;

        if (foodSearcherTask != null) {
            foodSearcherTask.cancel(true);
            foodSearcherTask = null;
        }

        String url = buildKeywordSearchApiUrlString(latitude, longitude, zoomLevel);
        foodSearcherTask = new FoodSearcherTask();
        foodSearcherTask.execute(url);
    }

    private String buildKeywordSearchApiUrlString(double latitude, double longitude, int zoomLevel){
        double d=getDistance(zoomLevel);
        return String.format(myURL, latitude-d, latitude+d, longitude-d, longitude+d);
    }

    private double getDistance(int zoomLevel){
        double distance=0.0063;
        if(zoomLevel>=9){
            distance*=9;
        }else if(zoomLevel<=1){
            distance*=1;
        }else{
            distance*=zoomLevel;
        }
        return distance;
    }

    private String getFoodListJson(String urlString){
        try {
            URL url=new URL(urlString);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();

            BufferedReader buffr=new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer sb=new StringBuffer();
            String data=null;
            while (true){
                data=buffr.readLine();
                if(data==null)break;
                sb.append(data+"\n");
            }

            buffr.close();
            return sb.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Food> parsing(String jsonList) {
        List<Food> foodList=new ArrayList();

        if(jsonList.length()<1){
            return foodList;
        }

        try {
            JSONArray foodListJson = new JSONArray(jsonList);
            for(int i=0;i<foodListJson.length();i++){
                JSONObject obj=foodListJson.getJSONObject(i);

                int food_id=obj.getInt("food_id");
                String title=obj.getString("title");
                String content=obj.getString("content");

                JSONObject fmember=obj.getJSONObject("fmember");
                int fmember_id=fmember.getInt("fmember_id");
                String nickname=fmember.getString("nickname");

                JSONObject location=obj.getJSONObject("location");
                double latitude=location.getDouble("latitude");
                double longitude=location.getDouble("longitude");


                JSONObject photo=obj.getJSONObject("photo");
                String filename=photo.getString("filename");
                String thumbnailImgFileName=food_id+filename.substring(filename.lastIndexOf("."),filename.length());

                Food food=new Food();
                food.food_id=food_id;
                food.title=title;
                food.content=content;
                food.fmember_id=fmember_id;
                food.nickname=nickname;
                //food.nickname="임의 닉네임";
                food.latitude=latitude;
                food.longitude=longitude;
                food.thumbImgFileName=thumbnailImgFileName;

                foodList.add(food);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return foodList;
    }
}
