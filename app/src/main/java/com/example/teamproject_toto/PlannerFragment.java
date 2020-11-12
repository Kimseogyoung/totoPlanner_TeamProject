package com.example.teamproject_toto;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.remote.Datastore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class PlannerFragment extends Fragment {

    // 현재 날짜 저장
    Date today = new Date();
    // date_tv 전역변수로
    TextView date_tv;
    // 유저의 파이어 베이스
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // 리스트뷰에 추가할 리스트
    static ArrayList<String> items = new ArrayList<String>();
    //리스트뷰
    ListView plan_list;

    String username;
    String planname;
    boolean planPlckmode=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments() != null) {
            int year = getArguments().getInt("Year"); // 전달한 key 값
            int month = getArguments().getInt("Month"); // 전달한 key 값
            int day = getArguments().getInt("Day");
            today.setYear(year - 1900); // 대체... 비추천 써서 그런가....
            today.setMonth(month);
            today.setDate(day);
            }

        return inflater.inflate(R.layout.fragment_planner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Initialize();
        plan_list = getView().findViewById(R.id.plan_list);//리사이클러뷰

        //게시글작성탭 내용
        final EditText popupText=getView().findViewById(R.id.edit_Text);

        // fragment 생성 buttons
        ImageButton planEdit_btn = getView().findViewById(R.id.PlanEdit_btn); // 일정 편집
        final TextView date_tv = getView().findViewById(R.id.date_tv); // 달력 fragment
        date_tv.setOnClickListener(myFragment);

        // planEdit_btn
        planEdit_btn.setOnClickListener(Editing);

        // 하루 이동 buttons
        ImageButton yesterday_btn = getView().findViewById(R.id.yesterday_btn);
        ImageButton tomorrow_btn = getView().findViewById(R.id.tomorrow_btn);
        yesterday_btn.setOnClickListener(dayShift);
        tomorrow_btn.setOnClickListener(dayShift);

//         리스트뷰 컨텍스트 추가
        registerForContextMenu(plan_list);

        ImageButton planUpload_btn = (ImageButton)getView().findViewById(R.id.planCheck_btn);//업로드할 일정 선택
        Button photoUpload_btn = (Button)getView().findViewById(R.id.photoupload_btn);//사진선택 버튼
        Button write_btn =(Button)getView().findViewById(R.id.upload_btn);//게시글 업로드
        Button exit_btn= (Button)getView().findViewById(R.id.uploadtapExit_btn);//창닫기
        planUpload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                planPlckmode = true;//일정 선택 모드 활성화
                Toast.makeText(getContext(),"업로드할 일정을 선택하세요",Toast.LENGTH_SHORT).show();
            }
        });
        photoUpload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoPopup();
            }
        });
        write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getView().findViewById(R.id.uploadTap).setVisibility(View.INVISIBLE);
                writeTimelinedata(username,"1월1일",planname+"달성 완료!",
                        0,popupText.getText().toString());


            }
        });
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getView().findViewById(R.id.uploadTap).setVisibility(View.INVISIBLE);
            }
        });

        //리스트뷰 아이템클릭이벤트 리스너 (왜 안될까?@@@@@@@@@@@)
        plan_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(planPlckmode==true){
                    if(true){//나중에 체크박스(일정달성여부) 체크되었는지 확인해야함@@@@@@@@@@@@@@@@@
                        planPlckmode=false;
                        planname=items.get(position);//선택한일정내용
                        getView().findViewById(R.id.uploadTap).setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }


    public void Initialize(){
        SimpleDateFormat dateformat = new SimpleDateFormat("   yyyy년 \n MM월 dd일");
        String date = dateformat.format(today);
        date_tv = getView().findViewById(R.id.date_tv);
        date_tv.setText(date);
        items.clear();

        // 저장된 값 가져오기
        DataLoad();
    }

    private Date getNextDay(Date today){
        Calendar cal=Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    private Date getPreviousDay(Date today){
        Calendar cal=Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    // 컨텍스트 메뉴
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.itemselectedmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final int index= info.position;

        switch (item.getItemId()){

            case R.id.delete_item:
                items.remove(index);
                adapterSet();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String ss = format.format(today);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("list", items);
                db.collection("users").document(user.getUid())
                        .collection("planner").document(ss).set(map);
                break;

            case R.id.edit_item:
                final LinearLayout item_menu = getActivity().findViewById(R.id.item_menu);
                item_menu.setVisibility(View.VISIBLE);
                Button edit_btn = getView().findViewById(R.id.edit_btn);

                edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText edit_et = getView().findViewById(R.id.edit_et);
                        if (edit_et.getText().length() > 0){
                            items.set(index, edit_et.getText().toString());
                        }
                        edit_et.setText("");
                        adapterSet();

                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                        String ss = format.format(today);
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("list", items);
                        db.collection("users").document(user.getUid())
                                .collection("planner").document(ss).set(map);
                        item_menu.setVisibility(View.INVISIBLE);
                    }
                });
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void adapterSet(){
        PlannerAdapter adapter = new PlannerAdapter();

        for (String str : items){
            adapter.addItem(str);
        }

        plan_list.setAdapter(adapter);
    }


    View.OnClickListener Editing = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText plan_edit = getView().findViewById(R.id.plan_edit);
            String st = plan_edit.getText().toString();

            if (st.length() > 0){

                items.add(st);
                adapterSet();


                plan_edit.setText("");

                DataStore();
            }
        }
    };


    public void DataStore(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ss = format.format(today);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", items);

        db.collection("users").document(user.getUid())
                .collection("planner").document(ss).set(map);

    }

    public void DataLoad(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ss = format.format(today);

        DocumentReference docRef = db.collection("users").document(user.getUid())
                .collection("planner").document(ss);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        ArrayList<String> list = (ArrayList<String>) document.get("list");

                        plan_list = getView().findViewById(R.id.plan_list);

                        if (list != null) {
                            for (String str : list) {
                                items.add(str);
                            }
                            adapterSet();
                        } else adapterSet();

                    } else {
                        Log.d(TAG, "no such file");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    View.OnClickListener myFragment = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    CalenderFragment fragment2 = new CalenderFragment();
                    transaction.replace(R.id.mainFrame, fragment2);
                    transaction.commit();
        }
    };

    View.OnClickListener dayShift = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.yesterday_btn:
                    today = getPreviousDay(today);
                    SimpleDateFormat dateformat1 = new SimpleDateFormat("   yyyy년 \n MM월 dd일");
                    String date1 = dateformat1.format(today);
                    date_tv.setText(date1);
                    Initialize();
                    break;

                case R.id.tomorrow_btn:
                    today = getNextDay(today);
                    SimpleDateFormat dateformat2 = new SimpleDateFormat("   yyyy년 \n MM월 dd일");
                    String date2 = dateformat2.format(today);
                    date_tv.setText(date2);
                    Initialize();
                    break;
            }
        }
    };


    private void openPhotoPopup(){
        Log.e("hi","누름");
        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                takePicture();
            }
        };
        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                takeAlbum();
            }
        };
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(getContext())
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("사진촬영", cameraListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("취소", cancelListener)
                .show();

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
                Bitmap photo = BitmapFactory.decodeStream(in);
                in.close();
                uploadPhoto(photo,"album");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(requestCode==PICK_FROM_CAMERA){
            final Bundle extras = data.getExtras();
            if(extras!=null){
                Bitmap photo = extras.getParcelable("data");
                uploadPhoto(photo,"camera");
            }

        }

    }
    private void uploadPhoto(final Bitmap photo, String name){


        if(photo != null)
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();
            StorageReference ImagesRef = storageRef.child("images/"+name+"jpg");


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                    TextView phototext=getView().findViewById(R.id.photourl_text);
                    phototext.setText("사진첨부 완료" );
                }
            });

        }
    }
    private void writeTimelinedata(String name, String data,String title, int img,String content){
        TimelineboardInfo timelineboardInfo=new TimelineboardInfo(name, data,title,img,content);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(user!=null){
            db.collection("users").document(user.getUid())
                    .collection("timeline").document("201101121212").set(timelineboardInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(),"업로드에 성공했습니다",Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(getContext(),"업로드에 실패했습니다."+e.toString(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }

}