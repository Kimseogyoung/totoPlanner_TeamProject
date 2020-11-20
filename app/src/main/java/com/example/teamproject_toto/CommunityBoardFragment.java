package com.example.teamproject_toto;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

// CommunityBoardFragment.java 작성자 : 이아연
// 커뮤니티 기능으로 앱 사용자들이 모두 익명으로 함께 사용할 수 있다.
// 각 커뮤니티의 종류에 따라 저장/로드 하는 경로를 다르게 하고 java파일은 하나로만 사용
public class CommunityBoardFragment extends Fragment implements onBackPressedListener{
    FirebaseFirestore db = FirebaseFirestore.getInstance(); // 데이터 베이스 객체 생성
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인해 있는 유저

    RecyclerView communityList;

    ArrayList<CommunityboardInfo> cboardList = new ArrayList<CommunityboardInfo>(); // 게시글 

    // 어느 게시판인지 저장할 String
    String kinds;

    CommunityAdapter adapter;

    Bitmap photo;
    CommunityBoardFragment f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communityboard, container, false);

        // 어느 게시판인지 받아오기 -> 받아온 값에 따라 저장/로드할 경로가 달라진다.
        if (getArguments() != null){
            switch (getArguments().getString("kinds")){
                case "daily-life":
                    kinds = "daily-life"; // 일상 생활 게시판
                    break;
                case "employment":
                    kinds = "employment"; // 취업 게시판
                    break;
                case "exercise":
                    kinds = "exercise"; // 운동 게시판
                    break;
                case "smallhappy":
                    kinds = "smallhappy"; // 소확행 게시판
                    // 모든 사용자가 같은 날 같은 소확행을 받기 때문에 서로 공유하도록 만들었다.
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
        Button cphotoupload_btn = view.findViewById(R.id.cphotoupload_btn);
        cphotoupload_btn.setOnClickListener(onClickListener);

        communityList = view.findViewById(R.id.communityboard_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        communityList.setLayoutManager(layoutManager);

        c_update(); // 커뮤니티에 들어왔을 때 저장되어 있던 내용 모두 불러오기

        adapter = new CommunityAdapter(cboardList, getContext(), f,kinds); // 리사이클러 뷰 아이템 adapter
        // 아이템이 선택된다면, 해당 아이템만 보여주는 댓글 작성 가능한 fragment로 이동
        adapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() { 
            @Override
            public void onItemClick(View v, int position) {
                CommunityboardInfo info = adapter.getListData().get(position);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                CommunityCommentFragment fragment = new CommunityCommentFragment(); // CommunityCommentFragment
                Bundle bundle = new Bundle();
                bundle.putString("nickname", info.getNickname()); // 아이템 작성자 닉네임
                bundle.putString("content", info.getContent()); // 아이템 내용
                bundle.putString("title", info.getTitle()); // 아이템 제목
                bundle.putString("date", info.getDate()); // 아이템이 작성된 날짜
                bundle.putString("kinds", kinds); // 커뮤니티 종류
                bundle.putString("img",info.getImg()); // 아이템 이미지
                fragment.setArguments(bundle); // 위에것들 전달
                transaction.replace(R.id.mainFrame, fragment);
                transaction.commit();
            }
        });

        return view;
    }

    // Button들의 onclickListenter. switch문으로 아이디를 받아서 각각 동작
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.cupload_btn: // 아이템 작성칸 버튼
                    getView().findViewById(R.id.cuploadTap).setVisibility(View.VISIBLE);
                    // 작성 칸이 눈에 보이게 된다.

                    EditText nickname_text1 = getView().findViewById(R.id.nickname_et);
                    EditText cedit_text1 = getView().findViewById(R.id.cedit_Text);
                    EditText cedit_title1 = getView().findViewById(R.id.ctitle_Text);

                    nickname_text1.setText(""); // 전에 쓰던것이 남아있지 않게 공백으로
                    cedit_text1.setText(""); // 전에 쓰던것이 남아있지 않게 공백으로
                    cedit_title1.setText(""); // 전에 쓰던것이 남아있지 않게 공백으로
                    break;
                case R.id.back: // 뒤로 가기 버튼
                    Goback();
                    break;
                case R.id.cupload_real_btn: // 작성을 모두 완료했다면 업로드를 하는 버튼
                    EditText nickname_text = getView().findViewById(R.id.nickname_et);
                    String nickname = nickname_text.getText().toString();
                    EditText cedit_text = getView().findViewById(R.id.cedit_Text);
                    String str = cedit_text.getText().toString();
                    EditText cedit_title = getView().findViewById(R.id.ctitle_Text);
                    String title = cedit_title.getText().toString();

                    // 모든 내용을 작성했다면
                    if (nickname.length() > 0 && cedit_text.length() > 0 && cedit_title.length() > 0){
                        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");
                        String ss = format.format(new Date());

                        writemyCommunitydata(nickname,ss,title,str,ss+ " "+nickname); // 데이터 베이스에 저장

                        nickname_text.setText(""); // 다시 켰을 때 남아있지 않게 공백으로
                        cedit_text.setText(""); // 다시 켰을 때 남아있지 않게 공백으로
                        cedit_title.setText(""); // 다시 켰을 때 남아있지 않게 공백으로
                        getView().findViewById(R.id.cuploadTap).setVisibility(View.INVISIBLE);
                        // 작성 칸은 다시 보이지 않게 바꾼다

                        uploadPhoto(photo,ss+ " " +nickname); // firebase stroage에 이미지 저장
                        refresh(); // 새로 고침을 통해 작성한 내용을 보여준다.
                    } else {
                        // 내용을 모두 채우지 않았을 때 토스트 메시지
                        Toast.makeText(getContext(),"모든 내용을 채우세요.",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.cuploadtapExit_btn: // 작성 칸을 나가는 버튼
                    getView().findViewById(R.id.cuploadTap).setVisibility(View.INVISIBLE);
                    break;

                case R.id.refresh_btn: // 새로 고침 버튼
                    refresh();
                    break;

                case R.id.cphotoupload_btn: // 사진을 업로드하는 버튼
                    openPhotoPopup();
                    break;

            }
        }
    };

    // 새로 고침 메소드
    // 다른 사용자가 게시물을 올렸을 때 새로고침하면 보인다.
    public void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    // 뒤로 가기 메소드
    // 다시 커뮤니티 선택 프래그먼트로 간다.
    public void Goback(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().remove(this).commit();
        CommunityFragment communityFragment = new CommunityFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.mainFrame, communityFragment).commit();
    }

    // 커뮤니티에 게시물이 있는지 판단하는 메소드
    public void ItemEmpty(){

        if (cboardList.isEmpty()){ // 게시물이 없다면
            getView().findViewById(R.id.noBoard).setVisibility(View.VISIBLE); // 게시물이 없다는 것을 알려준다.
        } else getView().findViewById(R.id.noBoard).setVisibility(View.INVISIBLE);

    }

    // 사진을 업로드 하는 메소드
    private void openPhotoPopup(){

        final CharSequence[] list={"사진촬영","앨범선택","취소"}; // 3가지 선택

        AlertDialog.Builder alertDialogBulider=new AlertDialog.Builder(getContext());

        alertDialogBulider.setTitle("업로드 할 사진 선택");
        alertDialogBulider.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0://사진촬영
                        takePicture();
                        break;
                    case 1://앨범에서 선택
                        takeAlbum();
                        break;
                    case 2://취소
                        dialogInterface.dismiss();
                        break;
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBulider.create();
        alertDialog.show();
    }
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;

    private void takePicture(){ // 사진을 찍을 수 있게 해준다.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    private void takeAlbum(){ // 앨범에서 사진을 선택할 수 있게 해준다.
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    // 사진을 선택했을 때 처리
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode != Activity.RESULT_OK )
            return;

        if(requestCode==PICK_FROM_ALBUM){ // 앨범에서 사진을 선택했을 때
            try {
                // 선택한 이미지에서 비트맵 생성
                InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                photo = BitmapFactory.decodeStream(in);
                in.close();
                TextView phototext=getView().findViewById(R.id.cphotourl_text);
                phototext.setText("사진첨부 완료" );


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(requestCode==PICK_FROM_CAMERA){ // 카메라로 사진을 찍었을 때
            final Bundle extras = data.getExtras();
            if(extras!=null){
                photo = extras.getParcelable("data");
                TextView phototext=getView().findViewById(R.id.cphotourl_text);
                phototext.setText("사진첨부 완료" );
            }

        }

    }
    
    // 사진을 올릴 때 firebase의 storage의 알맞는 경로에 사진을 저장한다.
    private void uploadPhoto(final Bitmap phot, String name){


        if(phot != null)
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();
            StorageReference ImagesRef = storageRef.child(kinds+"/"+name); // 각 커뮤니티의 storage


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            phot.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datata = baos.toByteArray();

            UploadTask uploadTask = ImagesRef.putBytes(datata);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                }
            });

        }
    }



    // 작성한 커뮤니티 게시글을 firestore에 저장
    private void writemyCommunitydata(String nickname, String date, String title, String content, String img){

        CommunityboardInfo communityboardInfo = new CommunityboardInfo(nickname, title, content, date, img);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(user!=null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss"); // 문서는 날짜-시간별로 저장
            String ss = format.format(new Date());

            db.collection(kinds).document(ss).set(communityboardInfo);

        }

    }


    // 커뮤니티 게시글 업데이트
    public void c_update(){

        cboardList.clear(); // 게시글 리스트 비우기

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
                                    String img = (String) document.getData().get("img");
                                    CommunityboardInfo info = new CommunityboardInfo(nickname, title,
                                            content, date, img);

                                    cboardList.add(0,info); // 업데이트할 데이터를 게시글 리스트에 추가
                                }
                                ItemEmpty(); // 게시글이 없을때와 있을 때를 따로 처리
                                communityList.setAdapter(adapter); // adapter를 통해서 저장되어 있던 아이템들을 리사이클러 뷰에 보여준다.
                            } else {
                                Log.d(TAG, "task is null");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    // 휴대폰의 뒤로가기 선택시 Goback 메소드 실행
    @Override
    public void onBackPressed() {
        Goback();
    }


}
