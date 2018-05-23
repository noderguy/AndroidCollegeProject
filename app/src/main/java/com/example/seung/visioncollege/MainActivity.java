package com.example.seung.visioncollege;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // 컴포넌트 조작을 위해 변수 선언
    private Button buttonRegister;
    private EditText editTextEmail, editTextPassword;
    private TextView textViewSignin, textViewSignup;
    private ProgressDialog progressDialog;

    private int checkCounter = 0;
    private Session session;

    //파이어베이스 전용 인증 기능 구현시 필요.
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        session = new Session(this);

        progressDialog = new ProgressDialog(this);
        editTextEmail =  findViewById(R.id.editTextEmail);
        editTextPassword =  findViewById(R.id.editTextPassword);
//        buttonRegister =  findViewById(R.id.buttonRegister);
        textViewSignup =  findViewById(R.id.textViewSignup);
        textViewSignin = findViewById(R.id.textViewSignin);
//        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);


        // 배경 그라디언트 애니메이션

        ConstraintLayout constraintLayout = findViewById(R.id.relativeLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(1000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        // 로그인 상태에서 앱을 닫았을 경우에도 앱 로그인 상태 지속
        if (session.loggedIn()){
            startActivity(new Intent(MainActivity.this, LoggedActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseAuth.getCurrentUser() != null && checkCounter > 0){
            // 이미 로그인된 유저인지 확인
            Toast.makeText(this, "접속중인 계정입니다.", Toast.LENGTH_LONG).show();
        }
        checkCounter += 1;

    }


    /////////////////////////////////Database//////////////////////////////////////////


    // 이메일 형식이 맞는지 확인.
    boolean isEmailValid(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Click 이벤트  1. 로그인 이벤트 2. 회원가입 이벤트

    // 유저 로그인 로직

    private void userLogin(){
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();


        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "이메일과 비밀번호 모두 입력해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("로그인 중...");
        progressDialog.show();

        //파이어베이스 DB내 계정 존재여부 확인

//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //로그인 세션
                            session.setLoggedIn(true);
                            progressDialog.dismiss();
                            //액티비티 전환

                            Intent intent = new Intent(MainActivity.this, LoggedActivity.class);
                            startActivity(intent);

                            finish();
                        }
                        else if(!isEmailValid(editTextEmail.getText().toString())){
                            Toast.makeText(MainActivity.this, "올바르지 않은 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }

                        else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(MainActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }

                        else{
                            Toast.makeText(MainActivity.this, "존재하지 않는 이메일 계정입니다.", Toast.LENGTH_LONG).show();
                            progressDialog.cancel();
                        }
                    }
                });
    }

    // 유저 회원가입 로직

//    private void registerUser(){
//        final String email = editTextEmail.getText().toString().trim();
//        final String username = editTextUsername.getText().toString().trim();
//        final String password = editTextPassword.getText().toString().trim();
//
//        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
//            Toast.makeText(this, "이메일과 비밀번호 모두 입력해주세요.", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        progressDialog.setMessage("연결 중...");
//        progressDialog.show();
//
//
//        // 유저 회원가입 로직 내부에 파이어베이스 인증 서비스 이용
//
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()){
//                            //유저가 성공적으로 가입, 로그인
//                            //여기부터 프로필 액티비티 가동
//                            Toast.makeText(MainActivity.this, "가입 성공!", Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();
//
//                            // 닉네임이랑 패스워드 유저 생성자로 집어넣어서 DB에 닉네임과 패스워드 묶음으로 저장
//                            generateUser(username, password);
//
//                            //로그인 세션
//                            session.setLoggedIn(true);
//                            // 회원가입 성공시 메인페이지 이동
//                            Intent intent = new Intent(MainActivity.this, LoggedActivity.class);
//                            intent.putExtra("username", username);
//                            startActivity(intent);
//                            finish();
//                        }
//
//                        else if(!isEmailValid(editTextEmail.getText().toString())){
//                            Toast.makeText(MainActivity.this, "올바르지 않은 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
//                            progressDialog.cancel();
//                        }
//                        else if(password.length() < 6){
//                            Toast.makeText(MainActivity.this, "비밀번호는 6자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
//                            progressDialog.cancel();
//                        }
//
//                        else {
//                            Toast.makeText(MainActivity.this, "이미 존재하는 계정입니다.", Toast.LENGTH_LONG).show();
//                            progressDialog.cancel();
//                        }
//                    }
//                });
//    }
//
//    // 유저 닉네임, 비밀번호 DB에 저장하게 하는 메서드.
//    public void generateUser(String username, String password) {
//
//        // 데이터베이스의 인스턴스를 획득 후,  DB내의 users 데이터를 참조.
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference users = database.getReference("users");
//
//        // 유저 인스턴스 생성
//        User user = new User(username, password);
//        // 유저 인스턴스를 DB로 푸쉬.
//        users.push().setValue(user);
//    }



    public void onClick(View v){

        if ( v == textViewSignup){
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }

        if( v == textViewSignin){
            userLogin();
        }

    }

}