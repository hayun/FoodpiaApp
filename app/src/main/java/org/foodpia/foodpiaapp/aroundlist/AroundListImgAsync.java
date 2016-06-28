package org.foodpia.foodpiaapp.aroundlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by thnt on 2016-06-15.
 */
public class AroundListImgAsync extends AsyncTask<String, Void, Bitmap> {

    AroundListAdapter aroundListAdapter;

    public AroundListImgAsync(AroundListAdapter aroundListAdapter) {
        this.aroundListAdapter = aroundListAdapter;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmapImg = null;

        try {
            URL url = new URL(params[0]);
            // Character is converted to 'UTF-8' to prevent broken

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.connect();

            InputStream is = con.getInputStream();
            bitmapImg = BitmapFactory.decodeStream(is);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return bitmapImg;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        aroundListAdapter.imgList.add(bitmap);
        aroundListAdapter.notifyDataSetChanged();
    }
}
