package org.foodpia.foodpiaapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.nhn.android.naverlogin.OAuthLogin;

import org.foodpia.foodpiaapp.R;

/**
 * Created by kimgeunho on 2016-06-14.
 */
public class TestActivity extends AppCompatActivity{
    private OAuthLogin mOAuthLogin;
    TextView txt_email,txt_name,txt_age,txt_nickname;

    public TestActivity() {
        mOAuthLogin=IntroActivity.mOAuthLogin;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);

        String email=getIntent().getStringExtra("email");
        String name=getIntent().getStringExtra("name");
        String birthday=getIntent().getStringExtra("age");
        String nickname=getIntent().getStringExtra("nickname");

        txt_email= (TextView) findViewById(R.id.txt_email);
        txt_name= (TextView) findViewById(R.id.txt_name);
        txt_age= (TextView) findViewById(R.id.txt_age);
        txt_nickname= (TextView) findViewById(R.id.txt_nickname);

        txt_email.setText(email);
        txt_name.setText(name);
        txt_age.setText(birthday);
        txt_nickname.setText(nickname);
    }

    public void logout(View view){
        mOAuthLogin.logout(this);

        Intent intent=new Intent(this, IntroActivity.class);
        startActivity(intent);
    }
}
