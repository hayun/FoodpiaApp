package org.foodpia.foodpiaapp.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.foodpia.foodpiaapp.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Administrator on 2016-06-09.
 * 앱 실행 시 최초 화면에 대한 액티비티
 */
public class IntroActivity extends AppCompatActivity implements View.OnClickListener{

    String TAG=this.getClass().getName();
    Intent intent=null;
    boolean isEmail;

    //naver
    public static OAuthLogin mOAuthLogin;
    NaverLoginAsync loginAsync;
    private Context context;
    private OAuthLoginButton naver_login_btn;

    private String ID="Fq4JmW75Yy15HzULz67R";
    private String SECRET="2jOCFPyRY_";
    private String NAME="FoodPia";

    //facebook
    CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());    //facebook 초기화 시키기
            //facebook callback

        setContentView(R.layout.intro);
        Button facebook_login_btn= (Button) findViewById(R.id.facebook_login_btn);

        facebook_login_btn.setOnClickListener(this);

        context=this;
        naverLoginM();
    }
    /////////////////////////////naver
    private void naverLoginM(){
        mOAuthLogin=OAuthLogin.getInstance();
        mOAuthLogin.init(this, ID,SECRET,NAME);

        naver_login_btn= (OAuthLoginButton) findViewById(R.id.naver_login_btn);
        naver_login_btn.setOAuthLoginHandler(mOAuthLoginHandler);
    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {

        @Override
        public void run(boolean b) {
            if(b){
                //Toast.makeText(context, "가입성공", Toast.LENGTH_SHORT).show();
                String token=mOAuthLogin.getAccessToken(context);
                String refreshToken=mOAuthLogin.getRefreshToken(context);
                long expiresAt=mOAuthLogin.getExpiresAt(context);
                String tokenType=mOAuthLogin.getTokenType(context);

                String url="https://openapi.naver.com/v1/nid/me";
                Log.d(TAG, "접속 주소는?? "+mOAuthLogin.getState(context));

                loginAsync=new NaverLoginAsync(context);
                loginAsync.execute(url, token);
            }else{
                Toast.makeText(context, "가입실패", Toast.LENGTH_SHORT).show();
            }
        }
    };


//////////////////////////facebook
    @Override
    public void onClick(View v) {           //페이스북 로그인 창 불러오기
        callbackManager=CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"));     //읽어올 범위 설정 기본적인 유저 프로필만 가져오기!!!
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.d(TAG, "성공!!!");
                Log.d(TAG, "토큰 : "+loginResult.getAccessToken().getToken());
                Log.d(TAG, "페이스북 유저 아이디 : "+loginResult.getAccessToken().getUserId());

                GraphRequest request=GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d(TAG, "페북 로그인 결과 "+object.toString());

                        try {
                            JSONObject obj=new JSONObject(object.toString());
                            String email=obj.getString("email");
                            String id=obj.getString("id");

                            FacebookLoginCheckAsync facebookLoginCheckAsync=new FacebookLoginCheckAsync(context);
                            facebookLoginCheckAsync.execute(obj.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters=new Bundle();
                parameters.putString("fields","email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void btnClick(View view){
        int page=view.getId();
        switch (page){
            case R.id.intro_signup :  intent=new Intent(this, SignupActivity.class);
                startActivity(intent);break;
            case R.id.intro_login :  intent=new Intent(this, LoginActivity.class);
                startActivity(intent);break;
        }
    }

}
