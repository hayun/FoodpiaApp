package org.foodpia.foodpiaapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.foodpia.foodpiaapp.R;

/**
 * Created by Administrator on 2016-06-09.
 * 회원가입창 구현을 위한 액티비티
 */
public class SignupActivity extends AppCompatActivity {
    String TAG=this.getClass().getName();
    EditText sign_email, sign_nickname, sign_pwd, sign_pwdCheck, sign_phone;
    String email, nickname, pwd, pwdCheck, phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.signup);

    sign_email=(EditText)findViewById(R.id.sign_email);
    sign_nickname=(EditText)findViewById(R.id.sign_nickname);
    sign_pwd=(EditText)findViewById(R.id.sign_pwd);
    sign_pwdCheck=(EditText)findViewById(R.id.sign_pwdCheck);
    sign_phone=(EditText)findViewById(R.id.sign_phone);
}

    public void signup(View view){

        SignupAsync signupAsync=new SignupAsync(this); //2_5 last
        String path="http://bbungbbunge.cafe24.com/device/user.do";

        email=sign_email.getText().toString();
        Log.d(TAG, "email=?"+email);
        nickname=sign_nickname.getText().toString();
        pwd=sign_pwd.getText().toString();
        pwdCheck=sign_pwdCheck.getText().toString();
        phone=sign_phone.getText().toString();

        signupCheck(view);
        signupAsync.execute(path, email, nickname,pwd,phone); //2_5 last 가변형 인자의 사용법!
    }

    public void signupCheck(View view){
        boolean result =false;
        // 이메일 입력(중복체크 기능 추가)
        if(email==""){
            Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();return;
        }
        for(int i=1;i<email.length(); i++){
            if(email.charAt(i)=='@'){
                result=true;
            }
        }

        if(result == false){
            Toast.makeText(this, "올바르지 않은 메일 형식입니다", Toast.LENGTH_SHORT).show();return;
        }

        // 닉네임 입력(중복체크 기능 추가)
        if(nickname.length()<1){
            Toast.makeText(this, "활동할 닉네임을 입력하세요", Toast.LENGTH_SHORT).show();return;
        }

        //비밀번호 입력
        if(pwd.length()<2 && pwdCheck.length()<2){
            Toast.makeText(this, "비밀번호는 8자 이상 입력해주세요", Toast.LENGTH_SHORT).show();return;
        }

        // 비밀번호 체크
        if(!pwd.equals(pwdCheck)){
            Toast.makeText(this, "입력하신 비밀번호가 다릅니다", Toast.LENGTH_SHORT).show();return;
        }
    }
}
