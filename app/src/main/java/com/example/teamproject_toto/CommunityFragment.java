package com.example.teamproject_toto;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// CommunityFragment.java 작성자 : 이아연
// 여러 커뮤니티 중에서 어느 커뮤니티로 갈 것인지 결정함.
public class CommunityFragment extends Fragment implements onBackPressedListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        TextView daily_life_tv = view.findViewById(R.id.daily_life_tv); // 일상 공유 커뮤니티
        TextView employment_tv = view.findViewById(R.id.employment_tv); // 취업 커뮤니티
        TextView exercise_tv = view.findViewById(R.id.exercise_tv); // 운동 커뮤니티
        TextView smallhappy_tv = view.findViewById(R.id.smallhappy_tv); // 소확행 커뮤니티
        // 소확행은 모든 사용자가 같은 날, 같은 소확행을 얻으므로 커뮤니티를 통해서 공유가능.

        daily_life_tv.setOnClickListener(OnClickListener);
        employment_tv.setOnClickListener(OnClickListener);
        exercise_tv.setOnClickListener(OnClickListener);
        smallhappy_tv.setOnClickListener(OnClickListener);

        return view;
    }

    // 각 커뮤니티 TextView onClickListener
    View.OnClickListener OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            CommunityBoardFragment fragment = new CommunityBoardFragment();
            Bundle bundle = new Bundle();
            switch (view.getId()){
                case R.id.daily_life_tv: // 일상 공유 커뮤니티
                    transaction.replace(R.id.mainFrame, fragment);
                    bundle.putString("kinds", "daily-life"); // 일상 공유 커뮤니티라는 것을 넘긴다.
                    fragment.setArguments(bundle);
                    transaction.commit();
                    break;

                case R.id.employment_tv: // 취업 커뮤니티
                    transaction.replace(R.id.mainFrame, fragment);
                    bundle.putString("kinds", "employment"); // 취업 커뮤니티라는 것을 넘긴다.
                    fragment.setArguments(bundle);
                    transaction.commit();
                    break;

                case R.id.exercise_tv: // 운동 커뮤니티
                    transaction.replace(R.id.mainFrame, fragment); // 운동 커뮤니티라는 것을 넘긴다.
                    bundle.putString("kinds", "exercise");
                    fragment.setArguments(bundle);
                    transaction.commit();
                    break;

                case R.id.smallhappy_tv: // 소확행 커뮤니티
                    transaction.replace(R.id.mainFrame, fragment); // 소확행 커뮤니티라는 것을 넘긴다.
                    bundle.putString("kinds", "smallhappy");
                    fragment.setArguments(bundle);
                    transaction.commit();
                    break;
            }
        }
    };
    
    // 뒤로 가기 메소드
    public void GoBack(){
        PlannerFragment fragment1=new PlannerFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrame,fragment1).commit();

    }

    // 핸드폰 뒤로 가기 클릭시 뒤로 가기 메소드 실행
    @Override
    public void onBackPressed() {
        GoBack();
    }





}
