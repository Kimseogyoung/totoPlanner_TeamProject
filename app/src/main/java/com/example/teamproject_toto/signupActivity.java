package com.example.teamproject_toto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class signupActivity extends Activity {
    private FirebaseAuth mAuth;
    private static final String TAG="SignupActivity";

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.signup);
        setTitle("회원가입");

        findViewById(R.id.signup_btn).setOnClickListener(onClickListener);
        findViewById(R.id.gotoLogin_btn).setOnClickListener(onClickListener);

        mAuth = FirebaseAuth.getInstance();

    }

    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.signup_btn:
                    signup();
                    break;
                case R.id.gotoLogin_btn:
                    myStartActivity(loginActivity.class);
                    break;

            }
        }
    };


    private void signup(){
        final String name = ((EditText)findViewById(R.id.signup_name)).getText().toString();
        String email = ((EditText)findViewById(R.id.signup_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.signup_password)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.signup_password2)).getText().toString();

        if(name.length()>0 && email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0){
            if(password.equals(passwordCheck)){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(getApplicationContext(),"회원가입이 성공했습니다.",Toast.LENGTH_SHORT).show();
                                    profileUpdate(name);

                                    //UI
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                                    //UI
                                }

                                // ...
                            }
                        });
            }else{
                Toast.makeText(this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"회원정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void profileUpdate(String name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        if(user!=null){
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                               //업데이트 완료
                            }
                        }
                    });
        }

    }
}

