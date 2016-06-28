package org.foodpia.foodpiaapp.detail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.foodpia.foodpiaapp.R;
import org.foodpia.foodpiaapp.regist.RegistActivity;
import org.foodpia.foodpiaapp.search.FoodImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by kimgeunho on 2016-06-18.
 */
public class DetailUpdate extends AppCompatActivity {
    String TAG=this.getClass().getName();
    RegistActivity registActivity;
    ImageView update_photo;
    TextView update_title, update_nickname, update_content;
    Uri uri;
    File file;
    InputStream is;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;

    String title, nickname, content, filename;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_update);

        update_photo = (ImageView) findViewById(R.id.update_photo);
        update_title= (TextView) findViewById(R.id.update_title);
        update_nickname= (TextView) findViewById(R.id.update_nickname);
        update_content= (TextView) findViewById(R.id.update_content);


        title=getIntent().getStringExtra("title");
        nickname=getIntent().getStringExtra("nickname");
        content=getIntent().getStringExtra("content");
        filename=getIntent().getStringExtra("filename");

        updateContent();
    }

    public void updateContent(){
        FoodImageLoader foodImageLoader=new FoodImageLoader();
        foodImageLoader.loadImage(update_photo, filename,null,false);

        update_title.setText(title);
        update_nickname.setText(nickname);
        update_content.setText(content);
    }

    public void addUpdatePhoto(View view) {
        {
            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakePhotoAction();
                }
            };
            DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakeAlbumAction();
                }
            };

            DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };

            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setPositiveButton("사진촬영", cameraListener)
                    .setNeutralButton("앨범선택", albumListener)
                    .setNegativeButton("취소", cancelListener)
                    .show();
        }
    }

    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void doTakeAlbumAction() // 앨범에서 이미지 가져오기
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != -1)
            return;

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                Log.d(TAG, "사진을 선택해서 가져옴");
                // 인텐트에서 사진 추출
                uri = data.getData();
                update_photo.setImageURI(uri);

                // 파일명을 얻어오자 !!
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToNext();
                String path = cursor.getString(cursor.getColumnIndex("_data"));

                file = new File(path);

                // String filename = path.substring(path.lastIndexOf("/")+1);
                cursor.close();

                Log.d(TAG, file.getName());

                try {
                    is = this.getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            case PICK_FROM_CAMERA: {
                try {
                    uri = data.getData(); // 저장되는 순간 그 이미지 파일의 uri를 얻어와서
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()); // uri로 비트맵 추출
                    image_bitmap = Bitmap.createScaledBitmap(image_bitmap, 500, 500, true); // 비트맵이 너무 크면 화면에 표현이 안되므로 임의로 줄이자
                    update_photo.setImageBitmap(image_bitmap); // 셋비트맵!!!!
                    is = this.getContentResolver().openInputStream(uri);
                } catch (Exception e) {
                    return;
                }
                break;
            }

        }
    }
}
