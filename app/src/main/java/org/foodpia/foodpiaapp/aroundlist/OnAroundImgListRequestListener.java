package org.foodpia.foodpiaapp.aroundlist;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by thnt on 2016-06-17.
 */
public interface OnAroundImgListRequestListener {
    public void success(Bitmap img);
    public void fail();
}
