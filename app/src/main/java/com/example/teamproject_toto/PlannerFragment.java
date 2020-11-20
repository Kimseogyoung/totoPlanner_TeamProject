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
import android.view.WindowManager;
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

//일정 프래그먼트
public class PlannerFragment extends Fragment implements onBackPressedListener{

    private static final String TAG="PlannerActivity";

    // 현재 날짜 저장
    static Date today = new Date();
    // date_tv 전역변수로
    TextView date_tv;
    // 유저의 파이어 베이스
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // 플래너의 일정 리스트
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
            // 달력에서 날짜를 받아왔다면 today에 저장해야함
            int year = getArguments().getInt("Year"); // 전달한 key 값 (1900이 추가되서 들어옴)
            int month = getArguments().getInt("Month"); // 전달한 key 값
            int day = getArguments().getInt("Day");
            
            // today에 값 저장해주기
            today.setYear(year - 1900); // 1900 빼주기
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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 키보드 올라올 때, 화면도 올라오게

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

        //리스트뷰 컨텍스트 추가
        registerForContextMenu(plan_list);

        ImageButton random_btn = (ImageButton)getView().findViewById(R.id.random_btn);//랜덤 소확행
        Button photoUpload_btn = (Button)getView().findViewById(R.id.photoupload_btn);//사진선택 버튼
        Button write_btn =(Button)getView().findViewById(R.id.upload_btn);//게시글 업로드
        Button exit_btn= (Button)getView().findViewById(R.id.uploadtapExit_btn);//창닫기


        //코드 작성자 : 김서경
        //사진 업로드버튼 클릭 이벤트리스너 
        photoUpload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoPopup();
            }
        });
        //코드 작성자 : 김서경
        //타임라인 게시글 업로드버튼 클릭 이벤트리스너
        write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                items.get(planidx).setUploaded(true);
                DataStore();

                //현재 시간 받아와서 문자열 생성
                SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM 월 dd일 HH:mm:ss");
                Date now = new Date();
                String ss = format.format(now);

                SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmss");
                String ss2 = format2.format(now);
                getView().findViewById(R.id.uploadTap).setVisibility(View.INVISIBLE);

                uploadPhoto(photo,ss2+username);
                String title = "\"" + planname + "\" 달성 완료!"; //게시글 제목
                writemyTimelinedata(username,ss, title,
                        ss2+username,popupText.getText().toString(),ss2);//내 타임라인 데이터에 추가
                writeFriendsTimelinedata(username,ss,title,
                        ss2+username,popupText.getText().toString(),ss2);//친구 타임라인 데이터에 추가



            }
        });
        //코드 작성자 : 김서경
        //게시글 작성팝업 나가기 버튼 클릭 이벤트리스너 
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getView().findViewById(R.id.uploadTap).setVisibility(View.INVISIBLE);//
            }
        });

        // 코드 작성자 : 이아연
        //random_btn 클릭 이벤트
        random_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RandomList randomList = new RandomList(); 
                String random = "😁 " + randomList.getRandomitem(); // 랜덤 리스트에서 오늘의 소확행 받아오기

                // planner의 일정들을 일시적으로 리스트에 저장 -> 소확행이 중복 되었는지 확인하기 위해
                ArrayList<String> temp = new ArrayList<String>();
                for (PlannerItems plannerItems : items){
                    temp.add(plannerItems.getText());
                }

                if (!temp.contains(random)){ // 일정 리스트에 소확행이 없다면
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                    
                    // today가 소확행을 받는 date와 같은지 확인 -> 다른 날짜에 소확행을 받을 수 없게 하기 위해
                    if (simpleDateFormat.format(today).equals(simpleDateFormat.format(new Date()))){

                        AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                        dlg.setTitle("오늘의 소확행은?"); //제목

                        // 아이템에 넣어주기
                        PlannerItems item = new PlannerItems(random, false,false);

                        items.add(item); // 일정 리스트에 소확행 추가
                        DataStore(); // 데이터 베이스에 저장
                        adapterSet(); // adapter를 적용해서 눈에 보이게

                        dlg.setMessage(random); // 메시지
                        dlg.show();

                    } else Toast.makeText(getContext(),"소확행은 오늘만!",Toast.LENGTH_SHORT).show(); // today와 소확행을 부르는 날짜가 맞지 않을 경우

                } else Toast.makeText(getContext(),"소확행은 한번만!",Toast.LENGTH_SHORT).show(); // 소확행을 중복해서 하는 경우

            }
        });
    }


    // 코드 작성자 : 이아연
    // 해당 프래그먼트를 처음 들어갔을 때 초기화하는 메소드
    public void Initialize(){
        SimpleDateFormat dateformat = new SimpleDateFormat("   yyyy년 \n MM월 dd일");
        String date = dateformat.format(today); 
        date_tv = getView().findViewById(R.id.date_tv);
        date_tv.setText(date);
        items.clear(); // 일정 리스트는 비우기

        // 저장된 값 가져오기
        DataLoad();
    }

    // 코드 작성자 : 이아연
    // today의 다음 날로 가는 메소드
    private Date getNextDay(Date today){
        Calendar cal=Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    // 코드 작성자 : 이아연
    // today의 전 날로 가는 메소드
    private Date getPreviousDay(Date today){
        Calendar cal=Calendar.getInstance();
        cal.setTime(today);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    // 코드 작성자 : 이아연
    // 일정 리스트의 컨텍스트 메뉴 적용
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

            // 코드 작성자 : 이아연 
            case R.id.delete_item: // 일정 아이템 삭제
                items.remove(index); // 일정 리스트에서 해당 인덱스 삭제
                adapterSet(); // adapter를 적용해서 삭제한 상태로 리스트뷰 변경
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String ss = format.format(today);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("list", items); 
                
                // 해당 일정이 삭제된 일정 리스트를 데이터 베이스에 저장
                db.collection("users").document(user.getUid())
                        .collection("planner").document(ss).set(map);
                
                // 아이템이 있는지 없는지 판단해서 처리하는 코드
                ItemEmpty();
                break;

            // 코드 작성자 : 이아연
            case R.id.edit_item: // 일정 아이템 편집
                final LinearLayout item_menu = getActivity().findViewById(R.id.item_menu);
                item_menu.setVisibility(View.VISIBLE); // 일정 편집 칸 보이도록
                final ImageButton edit_btn = getView().findViewById(R.id.edit_btn);
                
                // 코드 작성자 : 이아연
                // 일정 아이템 편집 취소
                ImageButton cancle_btn = getView().findViewById(R.id.cancel_btn);
                cancle_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText edit_et = getView().findViewById(R.id.edit_et);
                        edit_et.setText(""); // 작성한 것이 사라지도록 공백으로
                        item_menu.setVisibility(View.INVISIBLE);

                    }
                });

                // 일정 아이템 편집 완료
                edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText edit_et = getView().findViewById(R.id.edit_et);
                         if (edit_et.getText().length() > 0 && !items.get(index).getCv()){ // 편집하는 내용이 있고 체크되어 있지 않다면
                            //PlannerItems item = new PlannerItems(edit_et.getText().toString(), false);
                            PlannerItems item = new PlannerItems(edit_et.getText().toString(), false,false);
                            items.set(index, item); // 일정 리스트에 추가
                        }
                        edit_et.setText(""); // 작성한 내용이 사라지도록 공백으로
                        DataStore(); // 데이터 베이스에 저장
                        adapterSet(); // adapter를 적용해서 편집한 내용을 리스트뷰에 
                        item_menu.setVisibility(View.INVISIBLE);
                    }
                });
                break;
            
            //코드 작성자 : 김서경 
            case R.id.upload_item://업로드 버튼 클릭 시
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");//현재 날짜 문자열 받아와서
                if (simpleDateFormat.format(today).equals(simpleDateFormat.format(new Date()))){//오늘이 아닐경우 false
                    if(items.get(index).getCv() && !items.get(index).getUploaded()){// 체크박스(일정달성여부)체크 안되어있을시 false

                        planidx=index;
                        planname = items.get(index).getText();//선택한일정내용
                        getView().findViewById(R.id.uploadTap).setVisibility(View.VISIBLE);//팝업 

                        TextView phototext=getView().findViewById(R.id.photourl_text);
                        phototext.setText("첨부된 사진 없음" );
                        EditText editText=getView().findViewById(R.id.edit_Text);
                        editText.setText("");
                    }
                    else
                        Toast.makeText(getContext(),"이미 업로드했거나, 달성하지 않은 일정입니다.",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),"오늘 일정만 업로드 할 수 있습니다.",Toast.LENGTH_SHORT).show();
                }
        }
        return super.onContextItemSelected(item);
    }

    // 코드 작성자 : 이아연
    // 일정을 추가하는 버튼 클릭 리스너
    View.OnClickListener Editing = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText plan_edit = getView().findViewById(R.id.plan_edit);
            String st = plan_edit.getText().toString();

            if (st.length() > 0){ // 추가하는 내용이 있다면
                PlannerItems item = new PlannerItems(st, false,false);
                items.add(item); // 일정 리스트에 추가

                plan_edit.setText(""); // 추가한 내용이 사라지도록 공백으로

                DataStore(); // 데이터베이스에 저장
                ItemEmpty(); // 일정 아이템이 있는지 없는지 판단해서 처리
                adapterSet(); // adpter를 적용해서 추가한 일정을 listview에 적용함
            }
        }
    };

    // 코드 작성자 : 이아연
    // listview에 adapter를 적용하는 메소드
    public void adapterSet(){
        PlannerAdapter adapter = new PlannerAdapter();

        for (int i = 0; i < items.size(); i++){
            adapter.addItem(items.get(i)); // adapter에 일정 리스트에 있는 아이템 추가
        }

        plan_list.setAdapter(adapter); // 리스트뷰에 adapter 적용
    }

    // 코드 작성자 : 이아연
    // 일정 아이템이 있는지 없는지를 판단해서 처리하는 메소드
    public void ItemEmpty(){

        if (items.isEmpty()){ //일정 아이템이 없다면
            getView().findViewById(R.id.noPlan).setVisibility(View.VISIBLE); // 일정이 없다는 표시 보이게
        } else getView().findViewById(R.id.noPlan).setVisibility(View.INVISIBLE);

    }

    // 코드 작성자 : 이아연
    // 데이터 베이스에 일정, 일정 달성 여부(체크박스), 업로드 여부 저장
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


        // 문서는 날짜별로 저장
        db.collection("users").document(user.getUid())
                .collection("planner").document(ss).set(map);


    }

    // 코드 작성자 : 이아연
    // 데이터 베이스에 저장된 값을 불러오는 메소드
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

                        ArrayList<String> list = (ArrayList<String>) document.get("text"); // 일정 텍스트 리스트
                        ArrayList<Boolean> clist = (ArrayList<Boolean>) document.get("cv"); // 일정 달성 여부 (boolean) 리스트
                        ArrayList<Boolean> ulist = (ArrayList<Boolean>) document.get("uploaded"); // 업로드 여부 (boolean) 리스트

                        plan_list = getView().findViewById(R.id.plan_list);

                        if (list != null && clist != null) {
                            if (list.size() == clist.size()){ // 일정 텍스트 리스트와 일정 달성 여부 리스트는 항상 크기가 똑같아야함
                                for (int i = 0; i < list.size(); i++){
                                    PlannerItems item = new PlannerItems(list.get(i), clist.get(i),ulist.get(i));
                                    items.add(item); // 일정 리스트에 추가
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
                adapterSet(); // adapter 적용해서 불러온 값 리스트뷰에 적용
            }
        });

    }


    // 코드 작성자 : 이아연
    // 날짜가 표시된 textview를 클릭시 달력 fragment로 감.
    View.OnClickListener myFragment = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    CalenderFragment fragment2 = new CalenderFragment();
                    transaction.replace(R.id.mainFrame, fragment2);
                    transaction.commit();
        }
    };

    // 코드 작성자 : 이아연
    // 화살표 버튼을 클릭시 실해오디는 OnclickListener
    View.OnClickListener dayShift = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.yesterday_btn: // 전 날 버튼
                    today = getPreviousDay(today); // today 값이 전 날로 바뀜
                    SimpleDateFormat dateformat1 = new SimpleDateFormat("   yyyy년 \n MM월 dd일");
                    String date1 = dateformat1.format(today);
                    date_tv.setText(date1);
                    Initialize(); // 다시 초기화
                    break;

                case R.id.tomorrow_btn: // 다음 날 버튼
                    today = getNextDay(today); // today 값이 다음날로 바뀜
                    SimpleDateFormat dateformat2 = new SimpleDateFormat("   yyyy년 \n MM월 dd일");
                    String date2 = dateformat2.format(today);
                    date_tv.setText(date2);
                    Initialize(); // 다시 
                    break;
            }
        }
    };

    
    //코드 작성자 : 김서경 
    //사진 선택 팝업창 열기
    private void openPhotoPopup(){

        final CharSequence[] list={"사진촬영","앨범선택","취소"};//3가지 선택지

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


    //코드 작성자 : 김서경 
    //카메라 촬영
    private void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    //코드 작성자 : 김서경 
    private void takeAlbum(){
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    //코드 작성자 : 김서경 
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //카메라,앨범에서 사진선택후 결과값 받아오기
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
    //코드 작성자 : 김서경 
    //선택한 사진을 파이어베이스 storage에 업로드
    private void uploadPhoto(final Bitmap phot, String name){
        //phot - 사진 비트맵 , name - 저장될 사진의 이름

        if(phot != null)
        {
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();
            StorageReference ImagesRef = storageRef.child("images/"+name);//storage의 images폴더에 업로드


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
    //코드 작성자 : 김서경 
    //내 타임라인에 내 게시물 데이터 추가하기 함수
    //"users"컬렉션 -> user.uid문서 -> "timeline"컬렉션에 새 문서 추가
    private void writemyTimelinedata(String name, String data,String title, String img,String content,String docuName){

        TimelineboardInfo timelineboardInfo=new TimelineboardInfo(user.getUid(),name, data,title,img,content,docuName);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(user!=null){

            db.collection("user-timeline").document(user.getUid())
                    .collection("timeline").document(docuName).set(timelineboardInfo);//"user-timeline"컬렉션의 내 타임라인에도 게시물 추가
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
    //코드 작성자 : 김서경 
    //친구 타임라인에 내 게시물 데이터 추가하기 함수
    //"users"컬렉션 -> 내 친구 문서 -> "timeline"컬렉션에 새 문서 추가
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
    //코드 작성자 : 김서경 
    //데이터베이스에서 내 회원정보의 "name"필드 값 받아와서 username변수에 할당하는 함수
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

    //코드 작성자 : 김서경 
    //데이터 베이스에서 내 회원정보의 "friends"필드 값 받아와서 userFriends리스트에 추가하는 함수
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
    //코드 작성자 : 김서경 
    //뒤로가기 버튼눌렀을때 액티비티 종료
    @Override
    public void onBackPressed() {
        getActivity().finish();
    }

}
