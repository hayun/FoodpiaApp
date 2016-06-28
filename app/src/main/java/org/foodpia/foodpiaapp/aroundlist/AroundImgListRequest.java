package org.foodpia.foodpiaapp.aroundlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by thnt on 2016-06-17.
 */
public class AroundImgListRequest {
    AroundListImgAsync aroundListImgAsync;
    OnAroundImgListRequestListener onAroundImgListRequestListener;
    AroundListAdapter aroundListAdapter;

    private class AroundListImgAsync extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            Bitmap bitmapImg = null;

            try {
                URL url = new URL(params[0]);
                // Character is converted to 'UTF-8' to prevent broken

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.connect();

                InputStream is = con.getInputStream();
                bitmapImg = BitmapFactory.decodeStream(is);
                is.close();
                con.disconnect();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            onAroundImgListRequestListener.success(bitmapImg);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            aroundListAdapter.notifyDataSetChanged();
        }
    }

    public void getImgList(AroundListAdapter aroundListAdapter ,String url,OnAroundImgListRequestListener onAroundImgListRequestListener){
        this.aroundListAdapter=aroundListAdapter;

        this.onAroundImgListRequestListener = onAroundImgListRequestListener;
        if(aroundListImgAsync!=null){
            aroundListImgAsync.cancel(true);
            aroundListImgAsync=null;
        }
        aroundListImgAsync = new AroundListImgAsync();
        aroundListImgAsync.execute(url);
    }
}
