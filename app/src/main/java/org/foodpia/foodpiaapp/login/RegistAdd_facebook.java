package org.foodpia.foodpiaapp.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.foodpia.foodpiaapp.R;

/**
 * Created by kimgeunho on 2016-06-15.
 */
public class RegistAdd_facebook extends AppCompatActivity {
    String TAG=this.getClass().getName();
    TextView regist_email;
    EditText regist_nickname, regist_phone;
    String email,id;
    FacebookLoginAsync facebookLoginAsync;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_add);

        getEmail();
    }

    public void getEmail() {     //email주소 가져오기
        regist_email = (TextView) findViewById(R.id.regist_email);

        email = getIntent().getStringExtra("email");
        id=getIntent().getStringExtra("id");
        regist_email.setText(email);
    }

    public void regist(View view) {
        regist_nickname = (EditText) findViewById(R.id.regist_nickname);
        regist_phone = (EditText) findViewById(R.id.regist_phone);

        String nickname = regist_nickname.getText().toString();
        String phone = regist_phone.getText().toString();

        if(nickname.length()<1){
            Toast.makeText(this, "활동할 닉네임을 입력하세요", Toast.LENGTH_SHORT).show();return;
        }

        facebookLoginAsync=new FacebookLoginAsync(this);
        facebookLoginAsync.execute(email, nickname, phone, id);
    }
}
