package com.example.teamproject_toto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CalenderFragment extends Fragment {
    // CalenderFragment.java 작성자 : 이아연
    // 달력 캘린더
    // 해당 달력에서 얻은 값을 PlannerFragment.java로 보낸다.

    private int Year, Month, Day; // 연도, 월, 일

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CalendarView calendarView = getView().findViewById(R.id.calenderView); // 캘린더 뷰

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() // 날짜 선택 이벤트
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                Year = year; Month = month; Day = dayOfMonth; // 선택된 날이 변하면 각자를 Year, Month, Day에 넣어주기
            }
        });

        Button confirm_btn = getView().findViewById(R.id.confirm_btn); // 날짜 확정 버튼
        confirm_btn.setOnClickListener(new View.OnClickListener() { // 날짜 확정 버튼 클릭 이벤트
            @Override
            public void onClick(View view) {
                PlannerFragment fragment = new PlannerFragment(); // Fragment 생성
                Bundle bundle = new Bundle(); // PlannerFragment로 보낼 값들을 넣기 위한 Bundle 생성
                bundle.putInt("Year", Year); // 연도 추가
                bundle.putInt("Month", Month); // 월 추가
                bundle.putInt("Day", Day); // 일 추가
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.mainFrame, fragment);
                transaction.commit();
            }
        });

        Button return_btn = getView().findViewById(R.id.return_btn); // 돌아가기 버튼
        return_btn.setOnClickListener(new View.OnClickListener(){ // 돌아가기 버튼 클릭 리스너
            @Override
            public void onClick(View view) { // 누르면 다시 PlannerFragment로 돌아간다
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                PlannerFragment fragment = new PlannerFragment();
                transaction.replace(R.id.mainFrame, fragment);
                transaction.commit();
            }
        });
    }

}
