package com.example.teamproject_toto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


//loginActivity.java 파일 코드 작성자 : 김서경
//사용자 로그인 액티비티
public class loginActivity extends Activity {
    private FirebaseAuth mAuth;
    private static final String TAG="LoginActivity";//디버깅 태그

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.login);

        findViewById(R.id.login_btn).setOnClickListener(onClickListener);
        findViewById(R.id.gotoSignup_btn).setOnClickListener(onClickListener);

        mAuth = FirebaseAuth.getInstance();

    }


    //클릭이벤트 리스너 등록
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.login_btn://로그인버튼 클릭
                    login();//로그인 함수 호출
                    break;
                case R.id.gotoSignup_btn://회원가입 버튼 클릭
                    myStartActivity(signupActivity.class);//회원가입 액티비티로 이동
                    break;


            }
        }
    };


    //사용자 로그인 함수
    private void login(){
        String email = ((EditText)findViewById(R.id.login_email)).getText().toString();//아이디
        String password = ((EditText)findViewById(R.id.login_password)).getText().toString();//비밀번호


        if(email.length() > 0 && password.length() > 0){//이메일과 비밀번호를 입력했을때

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {//로그인 이벤트 리스너
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                startToast("로그인에 성공했습니다.");
                                FirebaseUser user = mAuth.getCurrentUser();
                                myStartActivity(MainActivity.class);

                            } else {
                                // If sign in fails, display a message to the user.
                                startToast(task.getException().toString());
                            }
                        }
                    });
        }else {
            Toast.makeText(this,"이메일 또는 비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }
    //Toast메시지를 만드는 함수
    private void startToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    //class c에 해당하는 액티비티로 이동하는 함수
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    //뒤로가기버튼 클릭시 강제종료
    @Override
    public void onBackPressed(){//뒤로가기 클릭시
        super.onBackPressed();
        moveTaskToBack(true);//강제종료 3줄
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}

