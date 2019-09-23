package com.example.visiblevoice.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.visiblevoice.Data.User;
import com.example.visiblevoice.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Iterator;

public class JoinActivity extends AppCompatActivity {
    private EditText idEditText;
    private EditText pwEditText;
    private EditText rePwEditText;
    private Button joinButton;
    private  String id;
    private String pw;
    public static boolean exs=false;
    private FirebaseAuth firebaseAuth;
//    private FirebaseDatabase mDatabase;
    private String deviceToken;
    private DatabaseReference myRef;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        idEditText=(EditText)findViewById(R.id.idEditText);
        pwEditText=(EditText)findViewById(R.id.pwEditText);
        rePwEditText=(EditText)findViewById(R.id.rePwEditText);
        myRef = FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth = firebaseAuth.getInstance();
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        joinButton=(Button)findViewById(R.id.joinButton);
        progressDialog = new ProgressDialog(this);
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id=idEditText.getText().toString();
                pw=pwEditText.getText().toString();
                String re=rePwEditText.getText().toString();
                //아이디 유효성 검사 (영문소문자, 숫자만 허용)
                boolean check=true;
                for (int i = 0; i < id.length(); i++) {
                    char ch = id.charAt(i);
                   /* if (!(ch >= '0' && ch <= '9') && !(ch >= 'a' && ch <= 'z')&&!(ch >= 'A' && ch <= 'Z')) {
                        Toast.makeText(JoinActivity.this,"아이디는 숫자와 영문자만 가능합니다",Toast.LENGTH_LONG).show();
                        return;
                    }*/
                    if (check && ((ch >= 'a' && ch <= 'z')||(ch >= 'A' && ch <= 'Z'))) {
                        check=false;
                    }
                }
                if(check){
                    Toast.makeText(JoinActivity.this,"아이디에는 영어가 포함되어야합니다.",Toast.LENGTH_LONG).show();
                    return;
                }

                /*if(id==null || id.length()<4 || id.length()>12) {
                    Toast.makeText(JoinActivity.this,"아이디를 4~12자 이내로 입력하세요",Toast.LENGTH_LONG).show();
                    return;
                }*/
                if(pw==null || pw.length()<4) {
                    Toast.makeText(JoinActivity.this,"비밀번호를 4자 이상 입력하세요",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!re.equals(pw)) {
                    Toast.makeText(JoinActivity.this,"비밀번호를 다시 확인해주세요",Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d("song","get key : " + myRef);
                createUser(id, pw);

                // Read from the database

                /*myRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        if(dataSnapshot.child(id).exists()){
                            Log.d("song","exists");
                            exs=true;
                        }
                        else{
                            Log.d("song","not exists");
                            exs=false;
                        }
//                        String value = dataSnapshot.getValue(String.class);
//                        Log.d("song", "Value is: " + value);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.d("song", "Failed to read value.", error.toException());
                    }
                });

                if(exs) {
                    Log.d("song","exs is true");
                    Toast.makeText(JoinActivity.this,"이미 존재하는 아이디 입니다.",Toast.LENGTH_LONG).show();
                    return;
                }*/

               /* User user = new User(id,deviceToken);
                myRef.child(id).setValue(user);
                //myRef.child(id).setValue(pw);
                Log.d("song", "set value " + pw);
                Toast.makeText(JoinActivity.this,"회원가입에 성공했습니다.",Toast.LENGTH_LONG).show();
                finish();*/
            }
        });
    }
    private ValueEventListener checkRegister = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
            while (child.hasNext()) {
                if (idEditText.getText().toString().equals(child.next().getKey())) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 아이디 입니다.", Toast.LENGTH_LONG).show();
                    myRef.removeEventListener(this);
                    return;
                }
            }
            User user = new User(id,deviceToken);
            myRef.push().setValue(user);
            Log.d("사용자", "set value " + user);
        }


        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    private void createUser(final String id, String pw) {
        progressDialog.setMessage("등록중입니다. 기다려 주세요...");
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(id, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            Toast.makeText(JoinActivity.this,"회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                            // 성공 이후 rdb에 회원정보 upload
                            //databaseReference.addListenerForSingleValueEvent(checkRegister);
                            myRef.addListenerForSingleValueEvent(checkRegister);
                            //startActivity(new Intent(JoinActivity.this, LoginActivity.class));
                        } else {
                            // 회원가입 실패
                            Log.d("회원가입 실패",task.getException().getMessage());
                            Toast.makeText(JoinActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            //finish();
                        }
                        finish();
                        progressDialog.dismiss();

                    }
                });
    }
}