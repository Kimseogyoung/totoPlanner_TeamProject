package com.example.teamproject_toto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    final ArrayList<String> items = new ArrayList<String>();


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

        // fragment 생성 buttons
        Button planEdit_btn = getView().findViewById(R.id.PlanEdit_btn); // 일정 편집 fragment
        final TextView date_tv = getView().findViewById(R.id.date_tv); // 달력 fragment
        date_tv.setOnClickListener(myFragment);

        // planEdit_btn
        planEdit_btn.setOnClickListener(Editing);

        // 하루 이동 buttons
        ImageButton yesterday_btn = getView().findViewById(R.id.yesterday_btn);
        ImageButton tomorrow_btn = getView().findViewById(R.id.tomorrow_btn);
        yesterday_btn.setOnClickListener(dayShift);
        tomorrow_btn.setOnClickListener(dayShift);

        ImageButton button = getView().findViewById(R.id.Button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataStore();
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

    View.OnClickListener Editing = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText plan_edit = getView().findViewById(R.id.plan_edit);
            String st = plan_edit.getText().toString();

            if (st.length() > 0){
                ListView plan_list = getView().findViewById(R.id.plan_list);
                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_list_item_1,
                        items
                );
                items.add(st);
                plan_list.setAdapter(listAdapter);
                plan_edit.setText("");
            }
        }
    };

    public void DataStore(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ss = format.format(today);
        db.collection("user-planner").document(user.getUid()).update(ss, items);
        // 이게 처음에 필드가 아무것도 없으면 실행안됨. 회원가입 거기서 이 부분 필드도 실행해야 할 것 같음
    }

    public void DataLoad(){
        DocumentReference docRef = db.collection("user-planner").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                    String ss = format.format(today);
                    if (document != null) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.get(ss));
                        ArrayList<String> list = (ArrayList<String>) document.get(ss);
                        if (list != null){
                            for (String str : list){
                                items.add(str);
                            }
                            ListView plan_list = getView().findViewById(R.id.plan_list);
                            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1,
                                    items
                            );
                            plan_list.setAdapter(listAdapter);
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


}