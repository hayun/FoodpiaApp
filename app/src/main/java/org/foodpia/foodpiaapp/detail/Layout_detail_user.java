package org.foodpia.foodpiaapp.detail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.foodpia.foodpiaapp.R;
import org.foodpia.foodpiaapp.search.FoodImageLoader;

/**
 * Created by kimgeunho on 2016-06-15.
 */
public class Layout_detail_user extends AppCompatActivity {
    TextView detail_title,detail_content,detail_nickname;
    ImageView detail_img,detail_like;
    Context context;
    boolean flag=true;
    String user;
    String register;
    String stateIcon;
    LikeState likeState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_user);

        context=this;

        detail_title= (TextView) findViewById(R.id.detail_title);
        detail_nickname= (TextView) findViewById(R.id.detail_nickname);
        detail_content= (TextView) findViewById(R.id.detail_content);
        detail_img= (ImageView) findViewById(R.id.detail_photo);
        detail_like= (ImageView) findViewById(R.id.detail_like);

        showDetail();

        likeState=new LikeState(register, user);
        flag=likeState.iconImg(flag,stateIcon,detail_like);

    }

    public void showDetail(){       //상세보기 뿌려주는 메소드
        String title=getIntent().getStringExtra("title");
        String nickname=getIntent().getStringExtra("nickname");
        String content=getIntent().getStringExtra("content");
        String filename=getIntent().getStringExtra("filename");
        user=getIntent().getStringExtra("user");
        register=getIntent().getStringExtra("register");
        stateIcon=getIntent().getStringExtra("stateIcon");



        FoodImageLoader foodImageLoade=new FoodImageLoader();
        foodImageLoade.loadImage(detail_img, filename, null, false);

        detail_title.setText(title);
        detail_nickname.setText(nickname);
        detail_content.setText(content);
    }

    public void likeFood(View view){
            flag=likeState.likeFood(this, flag, detail_like);
    }

    public void chatting(View view){
        Toast.makeText(this, "채팅", Toast.LENGTH_SHORT).show();
    }
}
