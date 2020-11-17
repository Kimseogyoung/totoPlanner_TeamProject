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

public class loginActivity extends Activity {
    private FirebaseAuth mAuth;
    private static final String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.login);

        findViewById(R.id.login_btn).setOnClickListener(onClickListener);
        findViewById(R.id.gotoSignup_btn).setOnClickListener(onClickListener);

        mAuth = FirebaseAuth.getInstance();

    }


    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.login_btn:
                    signup();
                    break;
                case R.id.gotoSignup_btn:
                    myStartActivity(signupActivity.class);
                    break;


            }
        }
    };


    private void signup(){
        String email = ((EditText)findViewById(R.id.login_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.login_password)).getText().toString();


        if(email.length() > 0 && password.length() > 0){

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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

                                // ...
                            }
                        }
                    });

        }else {
            Toast.makeText(this,"이메일 또는 비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }
    private void startToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    @Override
    public void onBackPressed(){//뒤로가기 클릭시
        super.onBackPressed();
        moveTaskToBack(true);//강제종료 3줄
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}

