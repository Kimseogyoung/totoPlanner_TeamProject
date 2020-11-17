package com.example.teamproject_toto;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CommunityFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        TextView daily_life_tv = view.findViewById(R.id.daily_life_tv);
        TextView employment_tv = view.findViewById(R.id.employment_tv);
        TextView exercise_tv = view.findViewById(R.id.exercise_tv);
        TextView smallhappy_tv = view.findViewById(R.id.smallhappy_tv);

        daily_life_tv.setOnClickListener(OnClickListener);
        employment_tv.setOnClickListener(OnClickListener);
        exercise_tv.setOnClickListener(OnClickListener);
        smallhappy_tv.setOnClickListener(OnClickListener);

        return view;
    }

    View.OnClickListener OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            CommunityBoardFragment fragment = new CommunityBoardFragment();
            Bundle bundle = new Bundle();
            switch (view.getId()){
                case R.id.daily_life_tv:
                    transaction.replace(R.id.mainFrame, fragment);
                    bundle.putString("kinds", "daily-life");
                    fragment.setArguments(bundle);
                    transaction.commit();
                    break;

                case R.id.employment_tv:
                    transaction.replace(R.id.mainFrame, fragment);
                    bundle.putString("kinds", "employment");
                    fragment.setArguments(bundle);
                    transaction.commit();
                    break;

                case R.id.exercise_tv:
                    transaction.replace(R.id.mainFrame, fragment);
                    bundle.putString("kinds", "exercise");
                    fragment.setArguments(bundle);
                    transaction.commit();
                    break;

                case R.id.smallhappy_tv:
                    transaction.replace(R.id.mainFrame, fragment);
                    bundle.putString("kinds", "smallhappy");
                    fragment.setArguments(bundle);
                    transaction.commit();
                    break;
            }
        }
    };






}