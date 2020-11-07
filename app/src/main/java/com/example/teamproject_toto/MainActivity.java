package com.example.teamproject_toto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

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

        if(user==null){
            //현재 로그인되어있지않다면 로그인화면으로
            myStartActivity(loginActivity.class);
        }
        else{
            //로그인되어있으면 현재화면 유지

            //아직 데이터베이스에 저장한 내용 받아오는거 없음 추가예정일듯
        }

        findViewById(R.id.planner_btn).setOnClickListener(onClickListener);
        findViewById(R.id.timeline_btn).setOnClickListener(onClickListener);
        findViewById(R.id.community_btn).setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch(view.getId()){
                case R.id.planner_btn:
                    PlannerFragment fragment1 = new PlannerFragment();
                    transaction.replace(R.id.mainFrame, fragment1);
                    transaction.commit();
                    break;

                case R.id.timeline_btn:
                    TimelineFragment fragment2 = new TimelineFragment();
                    transaction.replace(R.id.mainFrame, fragment2);
                    transaction.commit();
                    break;

                case R.id.community_btn:
                    CommunityFragment fragment = new CommunityFragment();
                    transaction.replace(R.id.mainFrame, fragment);
                    transaction.commit();
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
