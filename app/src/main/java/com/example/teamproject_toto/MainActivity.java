package com.example.teamproject_toto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user==null){
            //현재 로그인되어있지않다면
            myStartActivity(loginActivity.class);
        }
        else{
            for (UserInfo profile : user.getProviderData()) {

                String name = profile.getDisplayName();
                //String email = profile.getEmail();
                //Uri photoUrl = profile.getPhotoUrl();
                if(name!=null){
                    if(name.length()==0)
                        myStartActivity(loginActivity.class);
                }
            }
        }

        findViewById(R.id.logout_btn).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.logout_btn:
                    FirebaseAuth.getInstance().signOut();//로그아웃
                    myStartActivity(loginActivity.class);
                    break;
            }
        }
    };
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}