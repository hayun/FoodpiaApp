package org.foodpia.foodpiaapp.detail;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import org.foodpia.foodpiaapp.R;

/**
 * Created by kimgeunho on 2016-06-17.
 */
public class LikeState {
    String register;
    String user;
    boolean flag;

    public LikeState(String register, String user) {
        this.register = register;
        this.user = user;
    }

    public boolean likeFood(Context context, boolean flag, ImageView imageView){
        if(flag) {
            Toast.makeText(context, "좋아요를 눌렀습니다.", Toast.LENGTH_SHORT).show();
            imageView.setImageResource(R.drawable.like);
            LikeAsync likeAsync=new LikeAsync();
            likeAsync.execute(register, user);
            flag=false;
        }else{
            Toast.makeText(context, "좋아요를 취소하였습니다.", Toast.LENGTH_SHORT).show();
            imageView.setImageResource(R.drawable.food_like);
            LikeDeleteAsync likeDeleteAsync=new LikeDeleteAsync();
            likeDeleteAsync.execute(register, user);
            flag=true;
        }

        return flag;
    }

    public boolean iconImg(boolean flag, String stateIcon, ImageView imageView){
        switch (stateIcon){
            case "likeit" : imageView.setImageResource(R.drawable.like);flag=false;break;
            case "unlikeit" : imageView.setImageResource(R.drawable.food_like);flag=true;break;
        }

        return flag;
    }

}
