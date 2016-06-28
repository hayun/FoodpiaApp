package org.foodpia.foodpiaapp.regist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.foodpia.foodpiaapp.MainActivity;
import org.foodpia.foodpiaapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Chansoo on 2016-06-02.
 */
public class RegistActivity extends Activity {

    String TAG = this.getClass().getName();

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private Uri mImageCaptureUri;

    ImageView regist_photo;

    TextView txt_location; // 위치가 들어갈 텍스트뷰
    MultiAutoCompleteTextView edit_comment; // 코멘트입력창
    EditText edit_title;

    Spinner select_providerType; // ProviderType 설정 스피너
    ArrayList<String> option_list; // ListView에 표시할 데이터를 저장하는 Arraylist
    TextView txt_providerType;

    Spinner select_areaCode; // 지역번호 설정 스피너
    ArrayList<String> area_list; // 지역번호 리스트

    RegistAsync registAsync; // JSON으로 서버에 뿌려주기 위해
    String url = "http://bbungbbunge.cafe24.com/device/food.do";
    Uri uri; // 카메라로 사진을 찍건, 앨범에서 가져오던지 uri값을 사진의 담고있을 변수
    InputStream is; // 찍던가 선택하는 순간 인풋스트림에 담아둠 !!
    File file;

    EditText edit_number;

    String providerType_id = "1"; // 초기값 1(개인) 설정
    String areaNum = "010"; // 초기값 010 설정

    TextView txt_latitude, txt_longitude;
    Intent intent_fromMap;
    Double latitude = 0.0; // 디폴트값이 필요한가해!?
    Double longitude = 0.0;

    String food_address; // 주소가 담길 String
    int fmember_id; // fmember_id가 담길 String

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photocomment_layout);

        intent_fromMap = getIntent(); // 경도 및 위도 넘겨받기 위한 인텐트 생성 !
        latitude = intent_fromMap.getDoubleExtra("latitude", latitude);
        longitude = intent_fromMap.getDoubleExtra("longitude", longitude);
        food_address = intent_fromMap.getStringExtra("food_address");
        fmember_id = intent_fromMap.getIntExtra("fmember_id", -1); //

        regist_photo = (ImageView) findViewById(R.id.regist_photo); // 등록사진이 담길 ImageView

        edit_title = (EditText) findViewById(R.id.edit_title);
        edit_comment = (MultiAutoCompleteTextView) findViewById(R.id.comment); // 코멘트가 담길 EditText

        txt_location = (TextView) findViewById(R.id.txt_location); // 위치가 담길 TextView
        txt_location.setText(food_address); // 앞서서 구해온 주소를 그냥 받아서 세팅
        // txt_location.setText("위도는 : "+latitude+" / 경도는 : "+longitude);

        txt_providerType = (TextView) findViewById(R.id.txt_providerType); // 스피너 오른쪽 글씨 제어하면서 테스트하려고 썼는데 이젠 쓸일없음

        select_providerType = (Spinner) findViewById(R.id.select_providerType); // 스피너 가져오려면 알아야지?
        initProviderSpinner(); // Provider 스피너 초기화

        select_areaCode = (Spinner) findViewById(R.id.select_areaCode); // 지역번호 스피너 가져와야지?
        initAreaSpinner();

        edit_number = (EditText) findViewById(R.id.edit_number);

        txt_latitude = (TextView) findViewById(R.id.txt_latitude);
        txt_longitude = (TextView) findViewById(R.id.txt_longitude);

    }

    public void initAreaSpinner() {
        String[] strTextList_area = {"010", "011", "02", "031"};

        area_list = new ArrayList<String>();

        for (int i = 0; i < strTextList_area.length; i++) {
            area_list.add(strTextList_area[i]);
        }

        ArrayAdapter<String> spinner_adapter_area;

        spinner_adapter_area = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, area_list);

        spinner_adapter_area.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        select_areaCode.setAdapter(spinner_adapter_area);

        AdapterView.OnItemSelectedListener itemSelectedListener_area = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String strItem_area = area_list.get(position);
                areaNum = strItem_area;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        select_areaCode.setOnItemSelectedListener(itemSelectedListener_area); // spinner에 listner 달아주자 !

    }

    public void initProviderSpinner() {
        String[] strTextList = {"개인", "식당", "상점", "제과점"};

        option_list = new ArrayList<String>();

        for (int i = 0; i < strTextList.length; i++) {
            option_list.add(strTextList[i]);
        }

        ArrayAdapter<String> spinner_adapter;

        spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, option_list);

        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        select_providerType.setAdapter(spinner_adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String strItem = option_list.get(position); // 몇번째인지
                if (strItem == "개인") {
                    providerType_id = "1";
                } else if (strItem == "식당") {
                    providerType_id = "2";
                } else if (strItem == "상점") {
                    providerType_id = "3";
                } else if (strItem == "제과점") {
                    providerType_id = "4";
                }
                //Toast.makeText(getApplicationContext(), providerType_id + " 선택했지?", Toast.LENGTH_SHORT).show(); // 테스트용이니까뭐 필요없음
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {// 취소를 눌렀을때 나오는 곳 둘다 있어야됨(카더라)
                // Toast.makeText(getParent(), "암것도 안누름", Toast.LENGTH_SHORT).show();
            }
        };
        select_providerType.setOnItemSelectedListener(itemSelectedListener); // spinner에 listner 달아주자 !
    }

    public void getLocation(View view) {
        // Toast.makeText(this, "getLocation()!!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void registFood(View view) {
        // Toast.makeText(this, "등록완료!!!!", Toast.LENGTH_SHORT).show();

        String content = edit_comment.getText().toString(); // 코멘트넣고~~!
        String location = txt_location.getText().toString(); // 위치도 넣고오~_~
        String phone = edit_number.getText().toString();
        String title =  edit_title.getText().toString();

        Log.d(TAG, "content"+content);
        Log.d(TAG, "location"+location);
        Log.d(TAG, "phone"+phone);
        Log.d(TAG, "title"+title);

        if(file==null){
            Toast.makeText(this, "사진을 넣어주세요.", Toast.LENGTH_SHORT).show();
            return;
        }else if(title.length()==0){
            Toast.makeText(this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }else if(content.length()==0){
            Toast.makeText(this, "코멘트를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }else if(phone.length()==0){
            Toast.makeText(this, "전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }else if(location.length()==0){
            Toast.makeText(this, "위치를 지정하세요", Toast.LENGTH_SHORT).show();
            return;
        }else {

            registAsync = new RegistAsync(this, is);
            registAsync.execute(url, title, content, String.valueOf(fmember_id), providerType_id, (areaNum + phone), Double.toString(latitude), Double.toString(longitude), file.getName());
            //, comment, providerType_id, (areaNum+phone), "123", "456", "1"[
        }
    }

    public void addPhoto(View view) {
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
                regist_photo.setImageURI(uri);

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
                    regist_photo.setImageBitmap(image_bitmap); // 셋비트맵!!!!
                    is = this.getContentResolver().openInputStream(uri);
                } catch (Exception e) {
                    return;
                }
                break;
            }

        }
    }
}