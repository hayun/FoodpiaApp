package org.foodpia.foodpiaapp.search;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Yun on 2016-06-10.
 */
public class AddressSearcher {

    public static final String SEARCH_ADDRESS_BY_WGS = "https://apis.daum.net/local/geo/coord2detailaddr?apikey=%s&y=%f&x=%f&inputCoordSystem=WGS84&output=json";

    final String apikey="25af4971faf3c92316c6c7ec8c98d4b5";

    AddressAsyncTask addressAsyncTask;
    OnFinishSearchAddressListener onFinishSearchAddressListener;


    private class AddressAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String url=params[0];
            String json=getJsonList(url);
            Address address=parsing(json);


            if (onFinishSearchAddressListener != null) {
                if (address == null) {
                    onFinishSearchAddressListener.onFail();
                } else {
                    onFinishSearchAddressListener.onSuccess(address);
                }
            }
            return null;
        }
    }


    public void searchAddress(double latitude, double longitude, OnFinishSearchAddressListener onFinishSearchAddressListener) {
        this.onFinishSearchAddressListener = onFinishSearchAddressListener;

        if(addressAsyncTask!=null){
            addressAsyncTask.cancel(true);
            addressAsyncTask=null;
        }

        String url=buildKeywordSearchApiUrlString(latitude, longitude);
        addressAsyncTask=new AddressAsyncTask();
        addressAsyncTask.execute(url);
    }


    private String buildKeywordSearchApiUrlString(double latitude, double longitude) {
        return String.format(SEARCH_ADDRESS_BY_WGS, apikey, latitude, longitude);
    }


    private String getJsonList(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(4000 /* milliseconds */);
            con.setConnectTimeout(7000 /* milliseconds */);
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.connect();

            BufferedReader buffr=new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer sb=new StringBuffer();
            String data=null;
            while(true){
                data=buffr.readLine();
                if(data==null)break;
                sb.append(data+"\n");
            }

            buffr.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private Address parsing(String json) {

        Address address = new Address();

        try {
            JSONObject jsonObject = new JSONObject(json);

            address.setRegionId(jsonObject.getString("regionId"));
            address.setRegion(jsonObject.getString("region"));
            address.setX((float) jsonObject.getDouble("x"));//경도 좌표 (WGS84)
            address.setY((float) jsonObject.getDouble("y"));//위도 좌표 (WGS84)


            ////// 신주소 관련
            JSONObject newAddr=jsonObject.getJSONObject("new");
            address.setNew_bunji(newAddr.getString("bunji"));
            address.setNew_ho(newAddr.getString("ho"));
            address.setNew_name(newAddr.getString("name"));
            address.setNew_roadName(newAddr.getString("roadName"));


            ////// 구주소 관련
            JSONObject oldAddr=jsonObject.getJSONObject("old");
            address.setOld_bunji(oldAddr.getString("bunji"));
            address.setOld_san(oldAddr.getString("san"));
            address.setOld_ho(oldAddr.getString("ho"));
            address.setOld_name(oldAddr.getString("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return address;
    }
}
