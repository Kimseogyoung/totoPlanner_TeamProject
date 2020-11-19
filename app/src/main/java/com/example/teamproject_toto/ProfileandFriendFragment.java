package com.example.teamproject_toto;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
import java.util.HashMap;
import java.util.Map;

//내정보/친구목록 프래그먼트 클래스
public class ProfileandFriendFragment extends Fragment implements onBackPressedListener {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    
    ProfileandFriendFragment f;//어댑터에 this를 전달하기위한 변수

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    Date today = new Date();//오늘 날짜를 받아오기위한 객체
    EditText fc;//추가할 친구의 친구코드 작성칸 editText
    String myfriendcode;//나의 친구코드
    ImageView profileImage;//내 프로필 이미지


    private static final String TAG="FriendFragment";
    private ArrayList<MemberInfo> myfriendList =new ArrayList<>();//내 친구리스트 <MemberInfo>
    private ArrayList<String> frienduidList =new ArrayList<>();//내 친구리스트 문자열(uid)리스트


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.profileandfriend_fragment,container,false);

        f=this;
        recyclerView = (RecyclerView)view.findViewById(R.id.friendlistView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        setup();//초기설정

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));
        profileImage= view.findViewById(R.id.profile_img);

        view.findViewById(R.id.addfriend_btn).setOnClickListener(onClickListener);//친추버튼
        view.findViewById(R.id.logout_btn).setOnClickListener(onClickListener);//로그아웃버튼
        view.findViewById(R.id.profilechange_btn).setOnClickListener(onClickListener);//프로필 사진 수정버튼
        fc=(EditText)view.findViewById(R.id.addfriend_edittext);

        return view;
    }

    //클릭 이벤트리스너 추가
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.addfriend_btn://친구추가버튼 클릭 시
                    addFriend(fc.getText().toString());//친구코드.toString 으로 인수전달
                    break;
                case R.id.profilechange_btn://프로필 사진 변경 버튼 클릭 시
                    openPhotoPopup();//사진 선택팝업 호출
                    break;
                case R.id.logout_btn://로그아웃버튼 클릭 시
                    FirebaseAuth.getInstance().signOut();//로그아웃

                    Intent intent = new Intent(getContext(), loginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;


            }
        }
    };
    private void setup(){//기본화면 업데이트,프로필업데이트,친구목록업데이트

        DocumentReference docRef = db.collection("users").document(user.getUid());//내 회원정보 문서 레퍼런스
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        
                        //회원정보 받아오기
                        String name=""+document.get("name");
                        String email="이메일 : "+user.getEmail();
                        String phonenumber="전화번호 : "+document.get("phoneNumber");
                        myfriendcode=""+document.get("friendcode");
                        String code ="친구 코드 : "+myfriendcode;

                        //내정보 칸에 출력
                        TextView nametext= getView().findViewById(R.id.profile_username);
                        TextView emailtext= getView().findViewById(R.id.profile_email);
                        TextView phonenumbertext= getView().findViewById(R.id.profile_phonenumber);
                        TextView friendcodetext= getView().findViewById(R.id.profile_userfriendcode);

                        nametext.setText(name);
                        emailtext.setText(email);
                        phonenumbertext.setText(phonenumber);
                        friendcodetext.setText(code);

                        //프로필 이미지 로드
                        loadProfileImg(document);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        loadmyFriendList();//친구리스트 로드 함수 호출

    }
    //파이어베이스 storage에 저장된 내 프로필 사진을 이미지뷰에 출력하는 함수
    private void loadProfileImg(DocumentSnapshot document){
        //FirebaseStorage 인스턴스를 생성
        // 위의 저장소를 참조하는 파일명으로 지정
        StorageReference storageReference = firebaseStorage.getReference().child("profile/"+document.get("icon"));
        //StorageReference에서 파일 다운로드 URL 가져옴
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Glide 이용하여 이미지뷰에 로딩
                    Glide.with(getContext())
                            .load(task.getResult())
                            .into(profileImage);
                } else {
                    // URL을 가져오지 못하면
                    profileImage.setImageResource(R.drawable.basic);//기본 프로필이미지로 설정
                }
            }
        });
    }

    //친구목록 데이터를 읽어와서 어뎁터 업데이트하는 함수
    private  void loadmyFriendList(){

        frienduidList=new ArrayList<>();//친구 목록 초기화
        myfriendList=new ArrayList<>();

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        ArrayList<String> list = (ArrayList<String>) document.get("friends");//내 데이터베이스에서 friends필드에 있는 리스트를 읽음
                        if (list != null){
                            for (String str : list){//str은 친구의 uid

                                frienduidList.add(str);//읽어온 데이터를 코드상 친구 uid리스트에 추가
                                
                                DocumentReference docRef2 = db.collection("users").document(str);//데이터베이스에서 str에 해당하는 친구 회원정보 접근
                                docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document != null) {
                                              
                                                //친구 프로필사진, 이름을 읽어와서 어댑터에 출력
                                                MemberInfo m=new MemberInfo( ""+document.get("icon")
                                                        ,(String) document.get("name"),"","","");

                                                myfriendList.add(m);//친구리스트에 MemberInfo객체로 add

                                                //어댑터 업데이트
                                                adapter = new ProfileandFriendAdapter(myfriendList,getContext(),f);
                                                recyclerView.setAdapter(adapter);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    //친구목록에 새 친구를 추가하는 함수
    //String friendcode는 사용자가 입력한 친구코드
    private void addFriend(final String friendcode){
        if(!friendcode.equals(myfriendcode) ) {//자신의 친구코드를 입력하지 않았을때 탐색시작

            final CollectionReference usersRef = db.collection("users");
            Query query = usersRef.whereEqualTo("friendcode", friendcode);//users컬렉션에서 friendcode필드의 데이터값이 String friendcode인 문서 찾기
            query.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    final String frindid=""+document.getId();
                                    if (!frienduidList.contains(frindid)) {//내 친구 목록에 해당 유저가 없다면
                                        
                                        //내 데이터에 친구추가
                                        DocumentReference Ref1 = db.collection("users").document(user.getUid());
                                        Ref1.update("friends", FieldValue.arrayUnion(frindid));//데이터베이스의 "friends"필드에 맨뒤에 값 add
                                        
                                        //내 타임라인에 친구 게시글 추가
                                        db.collection("users").document(frindid).collection("timeline")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                //친구 게시글  document 반복문

                                                                //내 타임라인에 친구 게시글 추가
                                                                db.collection("user-timeline").document(user.getUid()).
                                                                        collection("timeline").document(""+document.getId())
                                                                        .set(document.getData());
                                                            }
                                                        } else {
                                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });



                                        //친구 데이터에 나를 추가
                                        DocumentReference Ref2 = db.collection("users").document(frindid);
                                        Ref2.update("friends", FieldValue.arrayUnion(user.getUid()));
                                        //친구타임라인에 내게시글 추가
                                        db.collection("users").document(user.getUid()).collection("timeline")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                //내 타임라인 게시글 document반복

                                                                //친구 타임라인에 추가
                                                                db.collection("user-timeline").document(frindid).
                                                                        collection("timeline").document(""+document.getId())
                                                                        .set(document.getData());
                                                            }
                                                        } else {
                                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });

                                        //어뎁터 업데이트
                                        loadmyFriendList();
                                        break;
                                    }
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
    
    //친구삭제
    public void removeFriend(int idx){

        final String usercode=frienduidList.get(idx);
        frienduidList.remove(idx);
        myfriendList.remove(idx);

        //파이어베이스 내 친구목록에서 제거
        DocumentReference docRef1 = db.collection("users").document(user.getUid());
        docRef1.update("friends", FieldValue.arrayRemove(usercode));
        //파이어베이스 내 타임라인에서 친구게시글 제거
        final CollectionReference usersRef1 = db.collection("user-timeline").document(user.getUid()).collection("timeline");

        Query query = usersRef1.whereEqualTo("writercode", usercode);//게시글 필드의 writercode가 삭제할 친구의 uid인 경우의 문서만 제거
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                                       db.collection("user-timeline").document(user.getUid())
                                                               .collection("timeline").document(document.getId())
                                                                .delete();
                                                   }
                                               }
                                           }
                                       });

        //친구목록에서 나 제거
        DocumentReference docRef2 = db.collection("users").document(usercode);
        docRef2.update("friends", FieldValue.arrayRemove(user.getUid()));
        //친구타임라인에서 내 게시글 제거
        final CollectionReference usersRef2 = db.collection("user-timeline").document(usercode).collection("timeline");       
        Query query2 = usersRef2.whereEqualTo("writercode", user.getUid());
        query2.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("user-timeline").document(usercode)
                                        .collection("timeline").document(document.getId())
                                        .delete();
                            }
                        }
                    }
                });

        //어댑터 업데이트
        adapter = new ProfileandFriendAdapter(myfriendList,getContext(),f);
        recyclerView.setAdapter(adapter);
    }

    //친구 계획창 열기
    public void openPlanTap(int idx){

        //현재 날짜를 문자열로 변환
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ss = format.format(today);
        final String usercode=frienduidList.get(idx);

        final FrameLayout plantap= getView().findViewById(R.id.planTap);
        final TextView text= getView().findViewById(R.id.plantap_text);
        Button btn=getView().findViewById(R.id.plantap_delbtn);
        plantap.setVisibility(View.VISIBLE);//팝업창 visible

        btn.setOnClickListener(new View.OnClickListener() {//닫기버튼 클릭했을때
            @Override
            public void onClick(View view) {
                plantap.setVisibility(View.INVISIBLE);
                text.setText("");
            }
        });
        //선택한 친구의 오늘 일정을 받아와서 출력하기
        db.collection("users").document(usercode)
                .collection("planner").document(ss)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   DocumentSnapshot document = task.getResult();
                                                   if (document != null) {

                                                       ArrayList<String> list = (ArrayList<String>) document.get("text");//일정이름
                                                       ArrayList<Boolean> list2 = (ArrayList<Boolean>) document.get("cv");//일정 달성여부
                                                       String str="";
                                                       if(list!=null){
                                                           //TextView에 출력
                                                           for (int i=0; i<list.size();i++) {
                                                               str += " "+(list2.get(i)?" ✔":"❌")+"  "+list.get(i)+"\n";
                                                           }
                                                           text.setText(str);
                                                       }
                                                       else  text.setText("오늘 일정이 없습니다.");
                                                   }
                                               }
                                           }
                });
    }

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;

    
    //프로필사진 선택 관련 팝업 열기
    private void openPhotoPopup(){

        final CharSequence[] list={"사진촬영","앨범선택","기본 이미지","취소"};//선택지 4개

        AlertDialog.Builder alertDialogBulider=new AlertDialog.Builder(getContext());

        alertDialogBulider.setTitle("프로필 사진 선택");
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
                            case 2://기본이미지
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("icon","");

                                db.collection("users").document(user.getUid()).update(map);
                                setup();

                                //원래 사진 삭제
                                StorageReference storageRef = firebaseStorage.getReference();
                                StorageReference desertRef = storageRef.child("profile/"+user.getUid());
                                desertRef.delete();

                                break;
                            case 3://취소
                                dialogInterface.dismiss();
                                break;
                        }
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBulider.create();
        alertDialog.show();
    }


    //사진촬영
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

    //사진촬영,앨범에서 선택하고 돌아왔을때 이미지 정보 받을 함수 액티비티 결과로 받음
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode != Activity.RESULT_OK )
            return;

        if(requestCode==PICK_FROM_ALBUM){
            try {
                // 선택한 이미지에서 비트맵 생성
                InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                uploadPhoto(BitmapFactory.decodeStream(in),user.getUid());

                in.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(requestCode==PICK_FROM_CAMERA){
            final Bundle extras = data.getExtras();
            if(extras!=null){
                uploadPhoto( (Bitmap) extras.getParcelable("data"),user.getUid());
            }
        }
    }
    private void uploadPhoto(final Bitmap phot, String name){


        if(phot != null)
        {
            //정사각형으로 만들기 !!
            int newWidth;
            int newHeight;

            //더 큰 높이/너비에 맞춰서 늘리기
            if(phot.getWidth() < phot.getHeight()){
                newHeight=phot.getWidth();
                newWidth=phot.getWidth();
            }
            else{
                newHeight=phot.getHeight();
                newWidth=phot.getHeight();
            }
            Bitmap bit = Bitmap.createScaledBitmap(phot, newWidth, newHeight, true);

            //storage의 profile폴더에 사진 업로드
            StorageReference storageRef = firebaseStorage.getReference();
            StorageReference ImagesRef = storageRef.child("profile/"+name);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("icon",user.getUid());
                    db.collection("users").document(user.getUid()).update(map);

                    DocumentReference docRef = db.collection("users").document(user.getUid());

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    loadProfileImg(document);//storage 업로드 되었다면 내정보 프로필 이미지에 로드
                                }
                            }
                        }
                    });
                }
            });

        }
    }

    //첫 프래그먼트로 이동하기
    public void GoBack(){
        PlannerFragment fragment1=new PlannerFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrame,fragment1).commit();

    }

    @Override
    public void onBackPressed() {
            //뒤로가기 버튼 클릭시 호출되는 리스너 
        GoBack();
    }

}
