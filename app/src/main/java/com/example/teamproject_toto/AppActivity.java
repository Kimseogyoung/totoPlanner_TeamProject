package com.example.teamproject_toto;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app);

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


}
