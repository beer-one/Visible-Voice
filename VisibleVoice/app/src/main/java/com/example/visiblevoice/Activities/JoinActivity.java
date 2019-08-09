package com.example.visiblevoice.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.visiblevoice.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinActivity extends AppCompatActivity {
    private EditText idEditText;
    private EditText pwEditText;
    private Button joinButton;
    public static String id;
    public static boolean exs=false;
//    private FirebaseDatabase mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        idEditText=(EditText)findViewById(R.id.idEditText);
        pwEditText=(EditText)findViewById(R.id.pwEditText);
        joinButton=(Button)findViewById(R.id.joinButton);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id=idEditText.getText().toString();
                String pw=pwEditText.getText().toString();
                //아이디 유효성 검사 (영문소문자, 숫자만 허용)
                for (int i = 0; i < id.length(); i++) {
                    char ch = id.charAt(i);
                    if (!(ch >= '0' && ch <= '9') && !(ch >= 'a' && ch <= 'z')&&!(ch >= 'A' && ch <= 'Z')) {
                        Toast.makeText(JoinActivity.this,"아이디는 숫자와 영문자만 가능합니다",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                if(id==null || id.length()<4 || id.length()>12) {
                    Toast.makeText(JoinActivity.this,"아이디를 4~12자 이내로 입력하세요",Toast.LENGTH_LONG).show();
                    return;
                }
                if(pw==null || pw.length()<4) {
                    Toast.makeText(JoinActivity.this,"비밀번호를 4자 이상 입력하세요",Toast.LENGTH_LONG).show();
                    return;
                }
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
                Log.d("song","get key : " + myRef);

                // Read from the database
                myRef.addValueEventListener(new ValueEventListener() {

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
                }

                myRef.child(id).setValue(pw);
                Log.d("song", "set value " + pw);
                Toast.makeText(JoinActivity.this,"회원가입에 성공했습니다.",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
