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

        //프래그먼트2 타임라인으로 전환
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment, timelineFragment).commit();

        if(user==null){
            //현재 로그인되어있지않다면 로그인화면으로
            myStartActivity(loginActivity.class);
        }
        else{
            //로그인되어있으면 현재화면 유지


            //아직 데이터베이스에 저장한 내용 받아오는거 없음 추가예정일듯
        }

        //로그아웃버튼
        //findViewById(R.id.logout_btn).setOnClickListener(onClickListener);

    }
/* 클릭 이벤트리스너 (로그아웃 버튼 클릭 포함)
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
    */
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
