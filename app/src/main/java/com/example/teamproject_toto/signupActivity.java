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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

//회원가입 액티비티
public class signupActivity extends Activity {
    private FirebaseAuth mAuth;
    private static final String TAG="SignupActivity";//디버깅 태그

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.signup);

        findViewById(R.id.signup_btn).setOnClickListener(onClickListener);
        findViewById(R.id.gotoLogin_btn).setOnClickListener(onClickListener);

        mAuth = FirebaseAuth.getInstance();

    }

    //클릭 이벤트리스너 등록
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.signup_btn://회원가입버튼 눌렀을때
                    signup();//회원가입
                    break;
                case R.id.gotoLogin_btn://로그인버튼 눌렀을때
                    myStartActivity(loginActivity.class);//로그인화면으로 이동
                    break;

            }
        }
    };

    //회원가입 함수
    private void signup(){
        final String name = ((EditText)findViewById(R.id.signup_name)).getText().toString();//회원정보 이름
        final String phoneNumber = ((EditText)findViewById(R.id.signup_number)).getText().toString();//회원정보 전화번호
        String email = ((EditText)findViewById(R.id.signup_email)).getText().toString();//회원정보 이메일(id)
        String password = ((EditText)findViewById(R.id.signup_password)).getText().toString();//회원정보 비밀번호
        String passwordCheck = ((EditText)findViewById(R.id.signup_password2)).getText().toString();//비밀번호 확인문자열

        if(name.length()>0 &&phoneNumber.length()>0 && email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0){
            //editText가 비어있을경우 if문 진입불가
            if(password.equals(passwordCheck)){//비밀번호 확인이 맞는지 
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {//회원가입 
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    profileUpdate(name,phoneNumber);///회원정보 등록 함수 호출
                                } else {
                                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(this,"비밀번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"회원정보를 입력해주세요.",Toast.LENGTH_SHORT).show();
        }
    }

    //class c에 해당하는 액티비티로 이동하는 함수
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    //데이터베이스에 회원정보 등록하는 함수
    private void profileUpdate(String name, String phoneNumber){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String friendcode=randomPassword(6);//친구코드 랜덤 생성
        MemberInfo memberInfo =new MemberInfo(user.getUid(),name,user.getEmail(),phoneNumber,friendcode);//회원정보 등록
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", user.getEmail());

        if(user!=null){
            db.collection("user-timeline").document(user.getUid()).set(map);
            db.collection("user-timeline").document(user.getUid()).collection("timeline");
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
                            Toast.makeText(getApplicationContext(),"회원가입에 실패했습니다."+e.toString(),Toast.LENGTH_SHORT).show();

                        }
                    });

        }
    }

    //랜덤으로 6자리의 친구코드를 생성하는 함수.
    public static String randomPassword (int length) {
        int index = 0;
        char[] charSet = new char[] {
                '0','1','2','3','4','5','6','7','8','9'
                ,'A','B','C','D','E','F','G','H','I','J','K','L','M'
                ,'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
                ,'a','b','c','d','e','f','g','h','i','j','k','l','m'
                ,'n','o','p','q','r','s','t','u','v','w','x','y','z'};

        StringBuffer sb = new StringBuffer();
        for (int i=0; i<length; i++) {
            index =  (int) (charSet.length * Math.random());
            sb.append(charSet[index]);
        }

        return sb.toString();

    }




}
