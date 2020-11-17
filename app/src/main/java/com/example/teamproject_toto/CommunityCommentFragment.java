package com.example.teamproject_toto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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


public class CommunityCommentFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String kinds;

    RecyclerView comment;
    CommunityCommentAdapter adapter;
    ArrayList<CommunityCommentInfo> comments = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_comment, container, false);

        TextView nickname = view.findViewById(R.id.citem_nickname);
        TextView date = view.findViewById(R.id.citem_date);
        TextView title = view.findViewById(R.id.citem_title);
        TextView content = view.findViewById(R.id.citem_content);

        if(getArguments() != null){
            nickname.setText(getArguments().getString("nickname"));
            title.setText(getArguments().getString("title"));
            date.setText(getArguments().getString("date"));
            content.setText(getArguments().getString("content"));
            kinds = getArguments().getString("kinds");
        }

        Button comment_plus = view.findViewById(R.id.comment_plus);
        comment_plus.setOnClickListener(onClickListener);
        Button edit_comment_btn = view.findViewById(R.id.edit_comment_btn);
        edit_comment_btn.setOnClickListener(onClickListener);
        ImageButton exit_btn = view.findViewById(R.id.ccexit_btn);
        exit_btn.setOnClickListener(onClickListener);
        ImageButton ccancle_btn = view.findViewById(R.id.ccancle_btn);
        ccancle_btn.setOnClickListener(onClickListener);

        comment = view.findViewById(R.id.comment);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        comment.setLayoutManager(layoutManager);

        comment_update();

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.comment_plus:
                    getView().findViewById(R.id.edit_commentTab).setVisibility(View.VISIBLE);
                    break;
                case R.id.edit_comment_btn:
                    EditText editText = getView().findViewById(R.id.comment_et);
                    if (editText.getText().length() > 0){
                        Date now = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");
                        String str = format.format(now);

                        CommunityCommentInfo info = new CommunityCommentInfo(str, editText.getText().toString());

                        db.collection(kinds).document(getArguments().getString("date"))
                                .collection("comment").document(format.format(now)).set(info);

                        editText.setText("");
                        getView().findViewById(R.id.edit_commentTab).setVisibility(View.INVISIBLE);

                        comment_update();
                    }
                    break;
                case R.id.ccexit_btn:
                    Goback();
                    break;
                case R.id.ccancle_btn:
                EditText et = getView().findViewById(R.id.comment_et);
                et.setText("");
                getView().findViewById(R.id.edit_commentTab).setVisibility(View.INVISIBLE);
                break;
            }

        }
    };

    public void Goback(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        CommunityBoardFragment communityFragment = new CommunityBoardFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("kinds", kinds);
        communityFragment.setArguments(bundle);
        transaction.replace(R.id.mainFrame, communityFragment).commit();
    }

    public void comment_update(){
        comments.clear();

        db.collection(kinds).document(getArguments().getString("date")).collection("comment")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult() != null){
                                ArrayList<CommunityCommentInfo> comments = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    String date = (String)document.get("date");
                                    String comment = (String) document.get("comment");
                                    CommunityCommentInfo info = new CommunityCommentInfo(date, comment);
                                    comments.add(info);
                                }

                                adapter = new CommunityCommentAdapter(comments);
                                comment.setAdapter(adapter);
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
