package org.foodpia.foodpiaapp.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
public class Layout_detail_register extends AppCompatActivity {
    TextView detail_title, detail_content, detail_nickname;
    ImageView detail_img;
    Context context;
    String title, nickname, content, filename;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_register);

        context = this;

        detail_title = (TextView) findViewById(R.id.detail_title);
        detail_nickname = (TextView) findViewById(R.id.detail_nickname);
        detail_content = (TextView) findViewById(R.id.detail_content);
        detail_img = (ImageView) findViewById(R.id.detail_photo);

        showDetail();
    }

    public void showDetail() {       //상세보기 뿌려주는 메소드
        title = getIntent().getStringExtra("title");
        nickname = getIntent().getStringExtra("nickname");
        content = getIntent().getStringExtra("content");
        filename = getIntent().getStringExtra("filename");


        FoodImageLoader foodImageLoade = new FoodImageLoader();
        foodImageLoade.loadImage(detail_img, filename, null, false);

        detail_title.setText(title);
        detail_nickname.setText(nickname);
        detail_content.setText(content);
    }

    public void delete(View view) {
        {
            DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteSet();
                }
            };

            DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };

            new AlertDialog.Builder(this).setTitle("정말 삭제하시겠습니까?")
                    .setPositiveButton("삭제하기",ok).setNegativeButton("취소하기", cancel).show();
        }
    }

    public void deleteSet() {
        Toast.makeText(this, "삭제", Toast.LENGTH_SHORT).show();

        String food_id = getIntent().getStringExtra("food_id");
        String fmember_id=getIntent().getStringExtra("fmember_id");

        DetailDeleteAsync detailDeleteAsync = new DetailDeleteAsync(this);
        detailDeleteAsync.execute(food_id, fmember_id);
    }

    public void update(View view) {
        Toast.makeText(this, "수정", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, DetailUpdate.class);
        intent.putExtra("title", title);
        intent.putExtra("nickname", nickname);
        intent.putExtra("content", content);
        intent.putExtra("filename", filename);
        startActivity(intent);

    }
}
