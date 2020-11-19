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

//TimelineFragment.java 파일 코드 작성자 : 김서경
//타임라인 프래그먼트
public class TimelineFragment extends Fragment implements onBackPressedListener{
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    TimelineFragment f;

    private ArrayList<TimelineboardInfo> boardList = new ArrayList<>();//타임라인의 게시글 리스트

    private static final String TAG="TimelineFragment";//디버깅 태그

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String username;//사용자 이름

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.timeline_fragment,container,false);

        f=this;
        recyclerView = (RecyclerView)view.findViewById(R.id.timelineView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        //사용자이름 저장
        loadUsername();
        
        //타임라인 새로고침
        update();
        return view;

    }
    //타임라인 데이터를 읽고 어뎁터에 업데이트하는 함수(새로고침)
    public void update(){
        db.collection("user-timeline").document(user.getUid()).collection("timeline")//타임라인컬렉션에 있는 모든 문서
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //boardList에 타임라인 게시글 정보 추가
                                boardList.add(0,
                                        new TimelineboardInfo(""+document.getData().get("writercode"),
                                        ""+document.getData().get("name"),
                                        ""+document.getData().get("date"),
                                        ""+document.getData().get("title"),
                                        ""+document.getData().get("img"),
                                        ""+document.getData().get("content"),
                                        ""+document.getData().get("id")));

                            }
                            //만약 게시글이 하나도없다면 텍스트뷰 출력
                            if(boardList.size()<1){
                                getView().findViewById(R.id.noTimeline).setVisibility(View.VISIBLE);
                            }
                            else{//하나라도 있다면 invisible
                                getView().findViewById(R.id.noTimeline).setVisibility(View.INVISIBLE);
                            }
                            
                            //어댑터 업데이트
                            adapter = new TimelineAdapter(boardList,getContext(),f);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    //댓글 창을 열고 댓글작성하는 함수
    public void openCommentTap(final int index){

        reloadComment(index);//데이터베이스에 저장된 각 게시글의 댓글들을 읽어오고 출력

        final LinearLayout commenttap= getView().findViewById(R.id.timeline_commentTap);
        TextView titletext= getView().findViewById(R.id.timeline_commentTitle);
        Button btn=getView().findViewById(R.id.timeline_commentupload);
        final EditText editText=getView().findViewById(R.id.timeline_commentedit);

        commenttap.setVisibility(View.VISIBLE);//레이아웃 visible


       
        titletext.setOnClickListener(new View.OnClickListener() {//"댓글"TextView가 클릭되면 댓글창 접기
            @Override
            public void onClick(View view) {
                commenttap.setVisibility(View.INVISIBLE);
                editText.setText("");
            }
        });
        
         //idx는 현재 누른 아이템 인덱스
        //boardlist에서 받아온 idx값으로 현재 보드의 id와 writercode를 알아내서
        //user-writercode-timeline-id-comment경로로 코멘트를 작성할 수있다.
        btn.setOnClickListener(new View.OnClickListener() {//댓글 작성버튼 클릭 시 이벤트 리스너
            @Override
            public void onClick(View view) {
                SimpleDateFormat format = new SimpleDateFormat("MM월 dd일 HH:mm:ss");//작성시간 받아오기
                Date now = new Date();
                final String ss = format.format(now);

                Map map = new HashMap<String, String>();//데이터 필드로 등록할 값 map으로 연결
                map.put("text", editText.getText().toString());
                map.put("time",ss);
                map.put("name",username);

                String boardId=boardList.get(index).getId();
                String boardWritercode=boardList.get(index).getWritercode();

                db.collection("users").document(""+boardWritercode)
                        .collection("timeline").document(""+boardId).collection("comment")//해당 게시글 comment컬렉션에 문서 등록
                        .document(""+ss).set(map);
                editText.setText("");
                reloadComment(index);//댓글 리로드

            }
        });

    }
    //댓글을 다시 로드하는 함수
    public void reloadComment(int idx){//idx 는 현재 선택한 게시글 번호
        final TextView commenttext=getView().findViewById(R.id.timeline_comment);

        //해당게시글의 comment컬렉션을 접근하여 모든 댓글 document를 읽는다.
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
                                    //TextView에 댓글 출력
                                    newcomment += document.getData().get("time")+"  "+document.getData().get("name")+
                                        "\n"+document.getData().get("text")+"\n\n";
                                }
                            }
                            commenttext.setText(newcomment);
                        }
                    }
                });

    }
    
    //사용자 이름을 받아오는 함수.
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


    //첫번째 프래그먼트로 이동하는 함수.
    public void GoBack(){
        PlannerFragment fragment1=new PlannerFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrame,fragment1).commit();

    }

    //뒤로가기 이벤트리스너
    @Override
    public void onBackPressed() {
        GoBack();
    }
}
