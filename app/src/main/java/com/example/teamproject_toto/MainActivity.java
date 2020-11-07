package com.example.teamproject_toto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    TimelineFragment timelineFragment = new TimelineFragment();

    private static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment, timelineFragment).commit();

        if(user==null){
            //현재 로그인되어있지않다면
            myStartActivity(loginActivity.class);
        }
        else{
            //로그인되어있으면 현재화면 유지


            //아직 데이터베이스에 저장한 내용 받아오는거 없음
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
