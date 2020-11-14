package com.example.teamproject_toto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class TimelineFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    private ArrayList<TimelineboardInfo> boardList = new ArrayList<>();

    private static final String TAG="TimelineFragment";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.timeline_fragment,container,false);

        recyclerView = (RecyclerView)view.findViewById(R.id.timelineView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));

        //타임라인 데이터 업데이트
        update();
        return view;

    }
    public void update(){


        db.collection("user-timeline").document(user.getUid()).collection("timeline")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Log.d(TAG, document.getId() + " => " + document.getData().get("name"));
                                boardList.add(0,
                                        new TimelineboardInfo(""+document.getData().get("writercode"),
                                        ""+document.getData().get("name"),
                                        ""+document.getData().get("date"),
                                        ""+document.getData().get("title"),
                                        ""+document.getData().get("img"),
                                        ""+document.getData().get("content")));


                            }
                            adapter = new TimelineAdapter(boardList,getContext());
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        for(TimelineboardInfo b:boardList){
            Log.d(TAG, b.toString()+"");
        }

        //adapter.notifyDataSetChanged();
    }
    //


}