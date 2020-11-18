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

public class CommunityBoardFragment extends Fragment implements onBackPressedListener{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    RecyclerView communityList;

    ArrayList<CommunityboardInfo> cboardList = new ArrayList<CommunityboardInfo>();

    // 어느 게시판
    String kinds;

    CommunityAdapter adapter;

    Bitmap photo;
    CommunityBoardFragment f;

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
        Button cphotoupload_btn = view.findViewById(R.id.cphotoupload_btn);
        cphotoupload_btn.setOnClickListener(onClickListener);

        communityList = view.findViewById(R.id.communityboard_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        communityList.setLayoutManager(layoutManager);

        c_update();

        adapter = new CommunityAdapter(cboardList, getContext(), f,kinds);
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
                bundle.putString("img",info.getImg());
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
                        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");
                        String ss = format.format(new Date());

                        writemyCommunitydata(nickname,ss,title,str,ss+ " "+nickname);

                        nickname_text.setText("");
                        cedit_text.setText("");
                        cedit_title.setText("");
                        getView().findViewById(R.id.cuploadTap).setVisibility(View.INVISIBLE);

                        uploadPhoto(photo,ss+ " " +nickname);
                        refresh();
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

                case R.id.cphotoupload_btn:
                    openPhotoPopup();
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

    private void openPhotoPopup(){

        final CharSequence[] list={"사진촬영","앨범선택","취소"};

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

    private void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    private void takeAlbum(){
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode != Activity.RESULT_OK )
            return;

        if(requestCode==PICK_FROM_ALBUM){
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
        else if(requestCode==PICK_FROM_CAMERA){
            final Bundle extras = data.getExtras();
            if(extras!=null){
                photo = extras.getParcelable("data");
                TextView phototext=getView().findViewById(R.id.cphotourl_text);
                phototext.setText("사진첨부 완료" );
            }

        }

    }
    private void uploadPhoto(final Bitmap phot, String name){


        if(phot != null)
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();
            StorageReference ImagesRef = storageRef.child(kinds+"/"+name);


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



    private void writemyCommunitydata(String nickname, String date, String title, String content, String img){

        CommunityboardInfo communityboardInfo = new CommunityboardInfo(nickname, title, content, date, img);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(user!=null){
            SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");
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
                                    String img = (String) document.getData().get("img");
                                    CommunityboardInfo info = new CommunityboardInfo(nickname, title,
                                            content, date, img);

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

    @Override
    public void onBackPressed() {
        Goback();
    }


}
