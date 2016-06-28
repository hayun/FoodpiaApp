package org.foodpia.foodpiaapp.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.foodpia.foodpiaapp.R;

/**
 * Created by Administrator on 2016-06-10.
 * 로그인을 위한 액티비티
 */
public class LoginActivity extends AppCompatActivity{
    EditText login_id, login_pwd;
    String id, pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        login_id=(EditText)findViewById(R.id.login_id);
        login_pwd=(EditText)findViewById(R.id.login_pwd);
    }

    public void login(View view){
        LoginAsync loginAsync=new LoginAsync(this); //2_5 last
        String path="http://bbungbbunge.cafe24.com/device/user/login.do";

        id=login_id.getText().toString();
        pwd=login_pwd.getText().toString();

        boolean result =false;
        // 이메일 입력(+DB 정보와 비교 필요)
        if(id==""){
            Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();return;
        }
        for(int i=1;i<id.length(); i++){
            if(id.charAt(i)=='@'){
                result=true;
            }
        }

        if(result == false){
            Toast.makeText(this, "올바르지 않은 메일 형식입니다", Toast.LENGTH_SHORT).show();return;
        }

        //비밀번호 입력(+DB 정보와 비교 필요)
        if(pwd.length()<2){
            Toast.makeText(this, "비밀번호는 8자 이상 입력해주세요", Toast.LENGTH_SHORT).show();return;
        }


        loginAsync.execute(path, id, pwd); //2_5 last 가변형 인자의 사용법!
    }

}
