package org.foodpia.foodpiaapp.search;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Yun on 2016-06-15.
 */
public class FoodImageLoader {
    final String imgURL="http://bbungbbunge.cafe24.com/data/";
    ImageAsyncTask imageAsyncTask;
    OnFinishImageLoaderListener onFinishImageLoaderListener;

    ImageView info_img;

    private class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            String url=urls[0];
            Bitmap image=getImage(url);


            if(onFinishImageLoaderListener!=null){
                if(image==null){
                    onFinishImageLoaderListener.onFail();
                }else{
                    onFinishImageLoaderListener.onSuccess();
                }
            }

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            info_img.setImageBitmap(image);
        }
    }

    public void loadImage(ImageView info_img, String filename, OnFinishImageLoaderListener onFinishImageLoaderListener, boolean thumbnail){
        this.onFinishImageLoaderListener=onFinishImageLoaderListener;
        this.info_img=info_img;

        if(imageAsyncTask!=null){
            imageAsyncTask.cancel(true);
            imageAsyncTask=null;
        }

        imageAsyncTask=new ImageAsyncTask();
        if(thumbnail) {
            imageAsyncTask.execute(imgURL + "thumbnail/" + filename);
        }else{
            imageAsyncTask.execute(imgURL + filename);
        }
    }

    private Bitmap getImage(String urlString){
        try {
            URL url=new URL(urlString);
            HttpURLConnection con= (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.connect();

            InputStream is=con.getInputStream();

            Bitmap image= BitmapFactory.decodeStream(is);

            is.close();

            return image;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
