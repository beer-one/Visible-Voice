package com.example.visiblevoice.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.visiblevoice.Data.User;
import com.example.visiblevoice.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
   // private DatabaseReference myRef;
    private FirebaseFirestore db;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        idEditText=(EditText)findViewById(R.id.idEditText);
        pwEditText=(EditText)findViewById(R.id.pwEditText);
        rePwEditText=(EditText)findViewById(R.id.rePwEditText);
        //myRef = FirebaseDatabase.getInstance().getReference("users");
        firebaseAuth = firebaseAuth.getInstance();
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        joinButton=(Button)findViewById(R.id.joinButton);
        progressDialog = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()

                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id=idEditText.getText().toString();
                pw=pwEditText.getText().toString();
                String re=rePwEditText.getText().toString();
                //아이디 유효성 검사 (영문소문자, 숫자만 허용)

                if(!re.equals(pw)) {
                    Toast.makeText(JoinActivity.this,"비밀번호를 다시 확인해주세요",Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d("song","get key : " + db);
                createUser(id, pw);
            }
        });
    }

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
                            //myRef.addListenerForSingleValueEvent(checkRegister);
                            Map<String, String> user_data = new HashMap<>();
                            user_data.put("deviceToken",deviceToken);
                            db.collection("users").document(id)
                                    .set(user_data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("firestore", "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("firestore", "Error writing document", e);
                                        }
                                    });
                            progressDialog.dismiss();
                            finish();
                            //startActivity(new Intent(JoinActivity.this, LoginActivity.class));
                        } else {
                            // 회원가입 실패
                            Log.d("회원가입 실패",task.getException().getMessage());
                            Log.d("회원가입 실패", task.getException().getClass().toString());

                            if (task.getException() instanceof FirebaseAuthWeakPasswordException)
                                Toast.makeText(JoinActivity.this, "최소 6자리 이상 비밀번호를 입력하십시오.", Toast.LENGTH_SHORT).show();
                            else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                Toast.makeText(JoinActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                            else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                Toast.makeText(JoinActivity.this, "이메일 형식과 맞지않습니다.", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                        }


                    }
                });
    }
}