package com.example.teamproject_toto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
//MainActivity.java 파일 코드 작성자 : 김서경
public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        //사진찍기 권한 허용 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            } else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        if(user==null){
            //현재 로그인되어있지않다면 로그인화면으로 이동
            myStartActivity(loginActivity.class);
        }
        else{
            //로그인되어있으면 현재화면 유지하고, 일정 프래그먼트로 이동
            PlannerFragment fragment1 = new PlannerFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mainFrame, fragment1);
            transaction.commit();

        }

        findViewById(R.id.planner_btn).setOnClickListener(onClickListener);
        findViewById(R.id.timeline_btn).setOnClickListener(onClickListener);
        findViewById(R.id.community_btn).setOnClickListener(onClickListener);
        findViewById(R.id.profileFriend_btn).setOnClickListener(onClickListener);


    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch(view.getId()){
                case R.id.planner_btn://일정버튼 클릭시
                    PlannerFragment fragment1 = new PlannerFragment();
                    transaction.replace(R.id.mainFrame, fragment1);
                    transaction.commit();
                    break;

                case R.id.timeline_btn://타임라인 버튼 클릭시
                    TimelineFragment fragment2 = new TimelineFragment();
                    transaction.replace(R.id.mainFrame, fragment2);
                    transaction.commit();
                    break;

                case R.id.community_btn://커뮤니티버튼 클릭시
                     CommunityFragment fragment = new CommunityFragment();
                    transaction.replace(R.id.mainFrame, fragment);
                    transaction.commit();

                    break;

                case R.id.profileFriend_btn:// 내 정보 버튼 클릭시
                    ProfileandFriendFragment fragment4 = new ProfileandFriendFragment();

                    transaction.replace(R.id.mainFrame, fragment4);
                    transaction.commit();
                    break;
            }
        }
    };

    //class c에 해당하는 액티비티로 이동하는 함수
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
   
    public void onBackPressed() {

        //프래그먼트 onBackPressedListener사용
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            if (fragment instanceof onBackPressedListener) {
                ((onBackPressedListener) fragment).onBackPressed();
                return;
            }
        }
    }



}
