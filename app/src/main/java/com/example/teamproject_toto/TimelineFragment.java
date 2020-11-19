package com.example.teamproject_toto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimelineFragment extends Fragment implements onBackPressedListener{
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    TimelineFragment f;

    private ArrayList<TimelineboardInfo> boardList = new ArrayList<>();

    private static final String TAG="TimelineFragment";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String username;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.timeline_fragment,container,false);

        f=this;
        recyclerView = (RecyclerView)view.findViewById(R.id.timelineView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));

        loadUsername();
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
                                        ""+document.getData().get("content"),
                                        ""+document.getData().get("id")));


                            }
                            if(boardList.size()<1){
                                getView().findViewById(R.id.noTimeline).setVisibility(View.VISIBLE);
                            }
                            else{
                                getView().findViewById(R.id.noTimeline).setVisibility(View.INVISIBLE);
                            }
                            adapter = new TimelineAdapter(boardList,getContext(),f);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    //댓글 창을 열고 댓글작성할 수 있다.
    public void openCommentTap(final int index){

        reloadComment(index);


        final LinearLayout commenttap= getView().findViewById(R.id.timeline_commentTap);
        TextView titletext= getView().findViewById(R.id.timeline_commentTitle);
        Button btn=getView().findViewById(R.id.timeline_commentupload);
        final EditText editText=getView().findViewById(R.id.timeline_commentedit);

        commenttap.setVisibility(View.VISIBLE);


        //idx는 현재 누른 아이템
        //boardlist에서 받아온 idx값으로 현재 보드의 id와 writercode를 알아내서
        //user-writercode-timeline-id-comment경로로 코멘트작성


        titletext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commenttap.setVisibility(View.INVISIBLE);
                editText.setText("");
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat format = new SimpleDateFormat("MM월 dd일 HH:mm:ss");
                Date now = new Date();
                final String ss = format.format(now);

                Map map = new HashMap<String, String>();
                map.put("text", editText.getText().toString());
                map.put("time",ss);
                map.put("name",username);

                String boardId=boardList.get(index).getId();
                String boardWritercode=boardList.get(index).getWritercode();

                db.collection("users").document(""+boardWritercode)
                        .collection("timeline").document(""+boardId).collection("comment")
                        .document(""+ss).set(map);
                editText.setText("");
                reloadComment(index);

            }
        });

    }
    //댓글을 다시 로드하는 함수
    public void reloadComment(int idx){
        final TextView commenttext=getView().findViewById(R.id.timeline_comment);

        db.collection("users").document(boardList.get(idx).getWritercode())
                .collection("timeline").document(boardList.get(idx).getId()).collection("comment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String newcomment="";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document!=null){
                                    newcomment += document.getData().get("time")+"  "+document.getData().get("name")+
                                        "\n"+document.getData().get("text")+"\n\n";
                                }
                            }
                            commenttext.setText(newcomment);
                        }
                    }
                });

    }
    private void loadUsername(){
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        username = (String) document.get("name");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    public void GoBack(){
        PlannerFragment fragment1=new PlannerFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrame,fragment1).commit();

    }

    @Override
    public void onBackPressed() {
        GoBack();
    }
}
