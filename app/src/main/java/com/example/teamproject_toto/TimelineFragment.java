package com.example.teamproject_toto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TimelineFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.timeline_fragment,container,false);

        recyclerView = (RecyclerView)view.findViewById(R.id.timelineView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        //타임라인 데이터 업데이트

        update();
        return view;

    }
    public void update(){
        ArrayList<TimelineboardInfo> boardList = new ArrayList<>();
        boardList.add(new TimelineboardInfo("서경","11월 29일 11:29","처음",0,"하이루"));

        adapter = new TimelineAdapter(boardList);
        recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }
    //


}
