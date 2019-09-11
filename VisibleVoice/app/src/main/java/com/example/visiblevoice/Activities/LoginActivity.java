package com.example.visiblevoice.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.visiblevoice.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Intent intent;
    private Button loginBtn, joinBtn;
    public static EditText idText, pwText;
    public static Context mContext;

    // google login
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user;
    private SignInButton googleBtn;


    // naver login
    private static String OAUTH_CLIENT_ID = "2IAoQP6YXk6yEW2DKokS";
    private static String OAUTH_CLIENT_SECRET = "hQO7EThGM0";
    private static String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인 테스트";

    private OAuthLoginButton oAuthLoginButton;
    public static OAuthLogin mOAuthLoginModule;
    private OAuthLoginHandler mOAuthLoginHandler;
    public Map<String,String> mUserInfoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // init
        mContext=LoginActivity.this;

        idText = (EditText) findViewById(R.id.emailEditText);
        pwText = (EditText) findViewById(R.id.passwordEditText);


        loginBtn = findViewById(R.id.loginBtn);
        joinBtn = findViewById(R.id.joinBtn);

        loginBtn.setOnClickListener(this);
        joinBtn.setOnClickListener(this);

//        logout();

        googleLoginInit();
        naverLoginInit();
    }
    public void logout(){
        try{
            Log.d("song","try google log out");
            FirebaseAuth.getInstance().signOut();
        }catch (Exception e){

        }
        try {
            Log.d("song","try naver log out");
            mOAuthLoginModule=OAuthLogin.getInstance();
            boolean isSuccessDeleteToken = mOAuthLoginModule.logoutAndDeleteToken(mContext);

            if (!isSuccessDeleteToken) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Log.d("song", "errorCode:" + mOAuthLoginModule.getLastErrorCode(mContext));
                Log.d("song", "errorDesc:" + mOAuthLoginModule.getLastErrorDesc(mContext));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.loginBtn:
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users").child(idText.getText().toString());
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("song", "data snapshot: " + dataSnapshot);
                        String value = dataSnapshot.getValue(String.class);
                        Log.d("song", "Value is: " + value);
                        if (value.equals(pwText.getText().toString())) {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("email", idText.getText().toString());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 다시 확인해주세요.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.d("song", "Failed to read value.", error.toException());
                    }
                });
                break;
            case R.id.joinBtn:
                intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
        }
    }

    private void naverLoginInit() {
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(LoginActivity.this, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

        oAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        oAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
        oAuthLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("song", "naver button click listener");
                mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
            }
        });

        mOAuthLoginHandler = new OAuthLoginHandler() {
            RequestApiTask apiTask;
            @Override
            public void run(boolean success) {
                if (success) {
                    if(apiTask==null) apiTask=new RequestApiTask();
                    apiTask.execute();
                } else {
                    String errorDesc = mOAuthLoginModule.getLastErrorDesc(LoginActivity.this);
                    Log.d("song", "error desc : " + errorDesc);
                }

            }
        };
    }

    private void googleLoginInit() {
        googleBtn = (SignInButton) findViewById(R.id.btn_googleSignIn);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("song", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();

                            Log.d("song", "user:" + user);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("email", user.getEmail());
                            startActivity(intent);
//                          finish();
                        } else {
                            Log.d("song", "Authentication Failed");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d("song", "request code sign in");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d("song", "Google sign in failed " + e.getMessage());
            }
        }
    }

    private class RequestApiTask extends AsyncTask<Void, Void, Void> {
        private String userEmail;
        public String getUserEmail(){ return userEmail; }
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
            String at = mOAuthLoginModule.getAccessToken(getBaseContext());
            mUserInfoMap = requestNaverUserInfo(mOAuthLoginModule.requestApi(getBaseContext(), at, url));
            return null;
        }

        protected void onPostExecute(Void content) {
            userEmail=mUserInfoMap.get("email");
            if (userEmail == null) {
                Toast.makeText(getBaseContext(), "로그인 실패하였습니다.  잠시후 다시 시도해 주세요!!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("song","onPostExceccute user info map : "+ userEmail);

                Intent intent = new Intent(LoginActivity.mContext, MainActivity.class);
                intent.putExtra("email", userEmail);
                startActivity(intent);
//              finish();
            }
        }
    }
    private Map<String,String> requestNaverUserInfo(String data) { // xml 파싱
        String f_array[] = new String[9];

        try {
            XmlPullParserFactory parserCreator = XmlPullParserFactory
                    .newInstance();
            XmlPullParser parser = parserCreator.newPullParser();
            InputStream input = new ByteArrayInputStream(
                    data.getBytes("UTF-8"));
            parser.setInput(input, "UTF-8");

            int parserEvent = parser.getEventType();
            String tag;
            boolean inText = false;
            boolean lastMatTag = false;

            int colIdx = 0;

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.compareTo("xml") == 0) {
                            inText = false;
                        } else if (tag.compareTo("data") == 0) {
                            inText = false;
                        } else if (tag.compareTo("result") == 0) {
                            inText = false;
                        } else if (tag.compareTo("resultcode") == 0) {
                            inText = false;
                        } else if (tag.compareTo("message") == 0) {
                            inText = false;
                        } else if (tag.compareTo("response") == 0) {
                            inText = false;
                        } else {
                            inText = true;

                        }
                        break;
                    case XmlPullParser.TEXT:
                        tag = parser.getName();
                        if (inText) {
                            if (parser.getText() == null) {
                                f_array[colIdx] = "";
                            } else {
                                f_array[colIdx] = parser.getText().trim();
                            }

                            colIdx++;
                        }
                        inText = false;
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        inText = false;
                        break;

                }

                parserEvent = parser.next();
            }
        } catch (Exception e) {
            Log.e("dd", "Error in network call", e);
        }
        Map<String,String> resultMap = new HashMap<>();
        resultMap.put("email"           ,f_array[0]);
        Log.d("song","naver user email: "+f_array[0]);
        return resultMap;
    }

}
