package com.example.teamproject_toto;

import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.ImageView;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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

    private static final String TAG="PlannerActivity";

    // 현재 날짜 저장
    static Date today = new Date();
    // date_tv 전역변수로
    TextView date_tv;
    // 유저의 파이어 베이스
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // 리스트뷰에 추가할 리스트, 체크상태 표시할 리스트
    ArrayList<PlannerItems> items = new ArrayList<PlannerItems>();
    //리스트뷰
    ListView plan_list;


    Bitmap photo;
    String username;
    int planidx;
    String planname;
    ArrayList<String> userFriends=new ArrayList<String>();

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
        plan_list = getView().findViewById(R.id.plan_list);//리스트뷰
        loadUsername();
        loadUserFriends();

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

        ImageButton random_btn = (ImageButton)getView().findViewById(R.id.random_btn);//랜덤 소확행
        Button photoUpload_btn = (Button)getView().findViewById(R.id.photoupload_btn);//사진선택 버튼
        Button write_btn =(Button)getView().findViewById(R.id.upload_btn);//게시글 업로드
        Button exit_btn= (Button)getView().findViewById(R.id.uploadtapExit_btn);//창닫기


        photoUpload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoPopup();
            }
        });

        write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                items.get(planidx).setUploaded(true);
                DataStore();

                SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM 월 dd일 HH:mm:ss");
                Date now = new Date();
                String ss = format.format(now);

                SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
                String ss2 = format2.format(now);
                getView().findViewById(R.id.uploadTap).setVisibility(View.INVISIBLE);

                uploadPhoto(photo,ss2+username);
                String title = "\"" + planname + "\" 달성 완료!";
                writemyTimelinedata(username,ss, title,
                        ss2+username,popupText.getText().toString(),ss2);
                //loadUserFriends();
                writeFriendsTimelinedata(username,ss,title,
                        ss2+username,popupText.getText().toString(),ss2);



            }
        });
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getView().findViewById(R.id.uploadTap).setVisibility(View.INVISIBLE);
            }
        });

        //random_btn -> 하루 한 번만 하게 하는 거랑, 삭제하면 다시 할 수 있게 하는 거 추가@@@@@
        random_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RandomList randomList = new RandomList();
                String random = "😁 " + randomList.getRandomitem();

                ArrayList<String> temp = new ArrayList<String>();
                for (PlannerItems plannerItems : items){
                    temp.add(plannerItems.getText());
                }

                if (!temp.contains(random)){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                    if (simpleDateFormat.format(today).equals(simpleDateFormat.format(new Date()))){

                        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                        dlg.setTitle("오늘의 소확행은?"); //제목

                        // 아이템에 넣어주기
                        PlannerItems item = new PlannerItems(random, false,false);

                        items.add(item);
                        DataStore();
                        adapterSet();

                        dlg.setMessage(random); // 메시지
                        dlg.show();

                    } else Toast.makeText(getContext(),"소확행은 오늘만!",Toast.LENGTH_SHORT).show();

                } else Toast.makeText(getContext(),"소확행은 한번만!",Toast.LENGTH_SHORT).show();

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
                ItemEmpty();
                break;

            case R.id.edit_item:
                final LinearLayout item_menu = getActivity().findViewById(R.id.item_menu);
                item_menu.setVisibility(View.VISIBLE);
                final ImageButton edit_btn = getView().findViewById(R.id.edit_btn);
                
                // 컨텍스트 편집 취소
                ImageButton cancle_btn = getView().findViewById(R.id.cancel_btn);
                cancle_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText edit_et = getView().findViewById(R.id.edit_et);
                        edit_et.setText("");
                        item_menu.setVisibility(View.INVISIBLE);

                    }
                });

                edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText edit_et = getView().findViewById(R.id.edit_et);
                        if (edit_et.getText().length() > 0){
                            //PlannerItems item = new PlannerItems(edit_et.getText().toString(), false);
                            PlannerItems item = new PlannerItems(edit_et.getText().toString(), false,false);
                            items.set(index, item);
                        }
                        edit_et.setText("");
                        DataStore();
                        adapterSet();
                        item_menu.setVisibility(View.INVISIBLE);
                    }
                });
                break;

            case R.id.upload_item:
                if(items.get(index).getCv() && !items.get(index).getUploaded()){// 체크박스(일정달성여부) 체크되었는지 확인해야함@@@@@@@@@@@@@@@@@

                    planidx=index;
                    planname = items.get(index).getText();//선택한일정내용
                    getView().findViewById(R.id.uploadTap).setVisibility(View.VISIBLE);

                    TextView phototext=getView().findViewById(R.id.photourl_text);
                    phototext.setText("첨부된 사진 없음" );
                    EditText editText=getView().findViewById(R.id.edit_Text);
                    editText.setText("");
                }
                else{
                    Toast.makeText(getContext(),"이미 업로드했거나, 달성하지 않은 일정입니다.",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    View.OnClickListener Editing = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText plan_edit = getView().findViewById(R.id.plan_edit);
            String st = plan_edit.getText().toString();

            if (st.length() > 0){
                PlannerItems item = new PlannerItems(st, false,false);
                items.add(item);

                plan_edit.setText("");

                DataStore();
                ItemEmpty();
                adapterSet();
            }
        }
    };

    public void adapterSet(){
        PlannerAdapter adapter = new PlannerAdapter();

        for (int i = 0; i < items.size(); i++){
            adapter.addItem(items.get(i));
        }

        plan_list.setAdapter(adapter);
    }

    public void ItemEmpty(){

        if (items.isEmpty()){
            getView().findViewById(R.id.noPlan).setVisibility(View.VISIBLE);
        } else getView().findViewById(R.id.noPlan).setVisibility(View.INVISIBLE);

    }

    public void DataStore(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ss = format.format(today);

        ArrayList<String> textlist = new ArrayList<String>();
        ArrayList<Boolean> cvlist = new ArrayList<Boolean>();
        ArrayList<Boolean> uplist = new ArrayList<Boolean>();

        for (PlannerItems item : items){
            textlist.add(item.getText());
            cvlist.add(item.getCv());
            uplist.add(item.getUploaded());
        }

        Map map = new HashMap<String, ArrayList>();
        map.put("text", textlist);
        map.put("cv", cvlist);
        map.put("uploaded",uplist);


        db.collection("users").document(user.getUid())
                .collection("planner").document(ss).set(map);


    }

    public void DataLoad(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        final String ss = format.format(today);

        DocumentReference docRef = db.collection("users").document(user.getUid())
                .collection("planner").document(ss);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {

                        ArrayList<String> list = (ArrayList<String>) document.get("text");
                        ArrayList<Boolean> clist = (ArrayList<Boolean>) document.get("cv");
                        ArrayList<Boolean> ulist = (ArrayList<Boolean>) document.get("uploaded");

                        plan_list = getView().findViewById(R.id.plan_list);

                        if (list != null && clist != null) {
                            if (list.size() == clist.size()){
                                for (int i = 0; i < list.size(); i++){
                                    PlannerItems item = new PlannerItems(list.get(i), clist.get(i),ulist.get(i));
                                    items.add(item);
                                }
                            } else Log.d(TAG, "크기 다름 이상");
                        } else Log.d(TAG, "list/clist/ulist 비었음");
                    } else {
                        Log.d(TAG, "no such file");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                ItemEmpty();
                adapterSet();
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
                TextView phototext=getView().findViewById(R.id.photourl_text);
                phototext.setText("사진첨부 완료" );


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(requestCode==PICK_FROM_CAMERA){
            final Bundle extras = data.getExtras();
            if(extras!=null){
                photo = extras.getParcelable("data");
                TextView phototext=getView().findViewById(R.id.photourl_text);
                phototext.setText("사진첨부 완료" );
            }

        }

    }
    private void uploadPhoto(final Bitmap phot, String name){


        if(phot != null)
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();
            StorageReference ImagesRef = storageRef.child("images/"+name);


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
    private void writemyTimelinedata(String name, String data,String title, String img,String content,String docuName){

        TimelineboardInfo timelineboardInfo=new TimelineboardInfo(user.getUid(),name, data,title,img,content,docuName);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(user!=null){

            db.collection("user-timeline").document(user.getUid())
                    .collection("timeline").document(docuName).set(timelineboardInfo);
            db.collection("users").document(user.getUid())
                    .collection("timeline").document(docuName).set(timelineboardInfo)
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
    private  void writeFriendsTimelinedata(String name, String data,String title, String img,String content,String docuName){

        TimelineboardInfo timelineboardInfo=new TimelineboardInfo(user.getUid(),name, data,title,img,content,docuName);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(userFriends!=null){
            for(String friend : userFriends){
                db.collection("user-timeline").document(friend)
                        .collection("timeline").document(docuName).set(timelineboardInfo);
            }
        }
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


    private void loadUserFriends(){

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        ArrayList<String> list = (ArrayList<String>) document.get("friends");
                        if (list != null){
                            for (String str : list){
                                Log.d(TAG, "add"+str);
                                userFriends.add(str);
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

}
