package com.example.teamproject_toto;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

// CommunityCommentFragment.java 작성자 : 이아연
// 커뮤니티 게시판의 게시글을 누르면 해당 게시글과 댓글을 보여주는 fragment
// 댓글 작성도 가능하다.
public class CommunityCommentFragment extends Fragment implements onBackPressedListener{

    FirebaseFirestore db = FirebaseFirestore.getInstance(); // 데이터베이스
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String kinds; // 어느 커뮤니티인지 저장하는 String

    RecyclerView comment;
    CommunityCommentAdapter adapter;
    ArrayList<CommunityCommentInfo> comments = new ArrayList<>();

    Bitmap phot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_comment, container, false);

        TextView nickname = view.findViewById(R.id.citem_nickname);
        TextView date = view.findViewById(R.id.citem_date);
        TextView title = view.findViewById(R.id.citem_title);
        TextView content = view.findViewById(R.id.citem_content);
        final ImageView img = view.findViewById(R.id.citem_img);

        if(getArguments() != null){
            nickname.setText(getArguments().getString("nickname")); // 게시글 작성자 닉네임
            title.setText(getArguments().getString("title")); // 게시글 제목
            date.setText(getArguments().getString("date")); // 게시글 날짜
            content.setText(getArguments().getString("content")); // 게시글 내용
            kinds = getArguments().getString("kinds"); // 커뮤니티 종류

            //FirebaseStorage 인스턴스를 생성
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            // 위의 저장소를 참조하는 파일명으로 지정
            StorageReference storageReference = firebaseStorage.getReference()
                    .child(kinds+"/"+getArguments().getString("img")); // 커뮤니티의 해당 게시글의 이미지 가져오기
            //StorageReference에서 파일 다운로드 URL 가져옴
            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // Glide 이용하여 이미지뷰에 로딩
                        Glide.with(getContext())
                                .load(task.getResult())
                                .into(img);
                    } else {
                        // URL을 가져오지 못하면
                        img.setVisibility(View.GONE);
                    }
                }
            });

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

        // fragment에 들어왔을 때, 저장되어 있던 댓글들 모두 
        comment_update();

        return view;
    }

    // Button들이 눌렀을 때 onClickListener. switch문으로 각각 동작
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.comment_plus: // 댓글 작성 버튼
                    getView().findViewById(R.id.edit_commentTab).setVisibility(View.VISIBLE); // 댓글 작성 칸 보이게
                    break;
                case R.id.edit_comment_btn: // 댓글 작성 후 완료 버튼
                    EditText editText = getView().findViewById(R.id.comment_et);
                    if (editText.getText().length() > 0){ // 댓글이 쓰여있다면
                        Date now = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");
                        String str = format.format(now);

                        CommunityCommentInfo info = new CommunityCommentInfo(str, editText.getText().toString());

                        // 해당 게시글 댓글을 데이터 베이스에 저장
                        db.collection(kinds).document(getArguments().getString("date"))
                                .collection("comment").document(format.format(now)).set(info);

                        editText.setText(""); // 작성한것이 남아있지 않도록 공백으로
                        getView().findViewById(R.id.edit_commentTab).setVisibility(View.INVISIBLE);

                        // 댓글 작성 후 업데이트해서 보이도록
                        comment_update();
                    }
                    break;
                case R.id.ccexit_btn: // 해당 프래그먼트 취소(뒤로 가기) 버튼
                    Goback();
                    break;
                case R.id.ccancle_btn: // 댓글 작성 취소 버튼
                EditText et = getView().findViewById(R.id.comment_et);
                et.setText(""); // 작성하던것이 남아있지 않도록 공백으로
                getView().findViewById(R.id.edit_commentTab).setVisibility(View.INVISIBLE);
                break;
            }

        }
    };

    // 뒤로 가기 메소드
    public void Goback(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        CommunityBoardFragment communityFragment = new CommunityBoardFragment(); // CommunityBoardFragment로 돌아간다.
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("kinds", kinds); // 커뮤니티 종류 보내기
        communityFragment.setArguments(bundle);
        transaction.replace(R.id.mainFrame, communityFragment).commit();
    }

    // 저장되어 있던 댓글을 불러오는 메소드
    public void comment_update(){
        comments.clear(); // 댓글 리스트 비우기

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
                                    // 댓글 리스트에 저장된 값 추가
                                }

                                // adapter를 적용해서 댓글이 보이도록 함
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

    // 핸드폰 뒤로가기 버튼 클릭시 뒤로 가기 메소드 실행
    @Override
    public void onBackPressed() {
        Goback();
    }

}
