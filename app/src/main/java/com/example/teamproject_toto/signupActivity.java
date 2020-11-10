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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
        final String phoneNumber = ((EditText)findViewById(R.id.signup_number)).getText().toString();
        String email = ((EditText)findViewById(R.id.signup_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.signup_password)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.signup_password2)).getText().toString();

        if(name.length()>0 &&phoneNumber.length()>0 && email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0){
            if(password.equals(passwordCheck)){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    profileUpdate(name,phoneNumber);
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
    private void profileUpdate(String name, String phoneNumber){

        //데이터베이스에 회원정보 등록
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        MemberInfo memberInfo =new MemberInfo(name,phoneNumber);
        if(user!=null){
            db.collection("users").document(user.getUid()).set(memberInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"회원가입에 성공했습니다.",Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(getApplicationContext(),"회원가입에 실패했습니다."+e.toString(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
}

