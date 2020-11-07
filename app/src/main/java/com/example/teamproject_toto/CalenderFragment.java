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

    private int Year, Month, Day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CalendarView calendarView = getView().findViewById(R.id.calenderView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() // 날짜 선택 이벤트
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                Year = year; Month = month; Day = dayOfMonth;
            }
        });

        Button confirm_btn = getView().findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlannerFragment fragment = new PlannerFragment(); // Fragment 생성
                Bundle bundle = new Bundle();
                bundle.putInt("Year", Year); // Key, Value
                bundle.putInt("Month", Month); // Key, Value
                bundle.putInt("Day", Day);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.mainFrame, fragment);
                transaction.commit();
            }
        });

        Button return_btn = getView().findViewById(R.id.return_btn);
        return_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                PlannerFragment fragment = new PlannerFragment();
                transaction.replace(R.id.mainFrame, fragment);
                transaction.commit();
            }
        });
    }

}