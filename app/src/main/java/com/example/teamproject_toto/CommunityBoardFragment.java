package com.example.teamproject_toto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class CommunityBoardFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    RecyclerView communityList;

    ArrayList<CommunityboardInfo> cboardList = new ArrayList<CommunityboardInfo>();

    // 어느 게시판
    String kinds;

    CommunityAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communityboard, container, false);

        // 어느 게시판인지 받아오기
        if (getArguments() != null){
            switch (getArguments().getString("kinds")){
                case "daily-life":
                    kinds = "daily-life";
                    break;
                case "employment":
                    kinds = "employment";
                    break;
                case "exercise":
                    kinds = "exercise";
                    break;
                case "smallhappy":
                    kinds = "smallhappy";
                    break;
            }
        }

        TextView community_title = view.findViewById(R.id.community_title);
        community_title.setText(kinds.toUpperCase());

        Button cupload_btn = view.findViewById(R.id.cupload_btn);
        cupload_btn.setOnClickListener(onClickListener);
        Button back = view.findViewById(R.id.back);
        back.setOnClickListener(onClickListener);

        Button cupload_real_btn = view.findViewById(R.id.cupload_real_btn);
        cupload_real_btn.setOnClickListener(onClickListener);
        ImageButton cexit_btn= view.findViewById(R.id.cuploadtapExit_btn);//창닫기
        cexit_btn.setOnClickListener(onClickListener);
        Button refresh_btn = view.findViewById(R.id.refresh_btn);
        refresh_btn.setOnClickListener(onClickListener);

        communityList = view.findViewById(R.id.communityboard_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        communityList.setLayoutManager(layoutManager);

        c_update();

        adapter = new CommunityAdapter(cboardList);
        adapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                CommunityboardInfo info = adapter.getListData().get(position);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                CommunityCommentFragment fragment = new CommunityCommentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("nickname", info.getNickname());
                bundle.putString("content", info.getContent());
                bundle.putString("title", info.getTitle());
                bundle.putString("date", info.getDate());
                bundle.putString("kinds", kinds);
                fragment.setArguments(bundle);
                transaction.replace(R.id.mainFrame, fragment);
                transaction.commit();
            }
        });

        return view;
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.cupload_btn:
                    getView().findViewById(R.id.cuploadTap).setVisibility(View.VISIBLE);

                    EditText nickname_text1 = getView().findViewById(R.id.nickname_et);
                    EditText cedit_text1 = getView().findViewById(R.id.cedit_Text);
                    EditText cedit_title1 = getView().findViewById(R.id.ctitle_Text);

                    nickname_text1.setText("");
                    cedit_text1.setText("");
                    cedit_title1.setText("");
                    break;
                case R.id.back:
                    Goback();
                    break;
                case R.id.cupload_real_btn:
                    EditText nickname_text = getView().findViewById(R.id.nickname_et);
                    String nickname = nickname_text.getText().toString();
                    EditText cedit_text = getView().findViewById(R.id.cedit_Text);
                    String str = cedit_text.getText().toString();
                    EditText cedit_title = getView().findViewById(R.id.ctitle_Text);
                    String title = cedit_title.getText().toString();

                    if (nickname.length() > 0 && cedit_text.length() > 0 && cedit_title.length() > 0){
                        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 hh:mm:ss");
                        String ss = format.format(new Date());

                        writemyCommunitydata(nickname,ss,title,str);

                        nickname_text.setText("");
                        cedit_text.setText("");
                        cedit_title.setText("");
                        getView().findViewById(R.id.cuploadTap).setVisibility(View.INVISIBLE);

                        c_update();
                    } else {
                        Toast.makeText(getContext(),"모든 내용을 채우세요.",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.cuploadtapExit_btn:
                    getView().findViewById(R.id.cuploadTap).setVisibility(View.INVISIBLE);
                    getView().findViewById(R.id.cuploadTap).setVisibility(View.INVISIBLE);
                    break;

                case R.id.refresh_btn:
                    refresh();
                    break;

            }
        }
    };

    public void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    public void Goback(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        CommunityFragment communityFragment = new CommunityFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrame, communityFragment).commit();
    }

    public void ItemEmpty(){

        if (cboardList.isEmpty()){
            getView().findViewById(R.id.noBoard).setVisibility(View.VISIBLE);
        } else getView().findViewById(R.id.noBoard).setVisibility(View.INVISIBLE);

    }



    private void writemyCommunitydata(String nickname, String date, String title, String content){

        CommunityboardInfo communityboardInfo = new CommunityboardInfo(nickname, title, content, date);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(user!=null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 hh:mm:ss");
            String ss = format.format(new Date());

            db.collection(kinds).document(ss).set(communityboardInfo);

        }

    }


    // 커뮤니티 게시글 업데이트
    public void c_update(){
        cboardList.clear();

        db.collection(kinds)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult() != null){
                                Log.d(TAG, task.getResult().toString());
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    String nickname = (String)document.get("nickname");
                                    String title = (String) document.get("title");
                                    String date = (String) document.get("date");
                                    String content = (String) document.get("content");
                                    CommunityboardInfo info = new CommunityboardInfo(nickname, title,
                                            content, date);

                                    cboardList.add(0,info);
                                }
                                ItemEmpty();
                                communityList.setAdapter(adapter);
                            } else {
                                Log.d(TAG, "task is null");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


}
