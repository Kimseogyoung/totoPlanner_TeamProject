package com.example.teamproject_toto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Ref;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProfileandFriendFragment extends Fragment {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    EditText fc;
    String myfriendcord;



    private static final String TAG="FriendFragment";
    private ArrayList<MemberInfo> myfriendList =new ArrayList<>();
    private ArrayList<String> frienduidList =new ArrayList<>();


     @Nullable
     @Override
     public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.profileandfriend_fragment,container,false);


        recyclerView = (RecyclerView)view.findViewById(R.id.friendlistView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        setup();

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 1));

        view.findViewById(R.id.addfriend_btn).setOnClickListener(onClickListener);//친추버튼
        fc=(EditText)view.findViewById(R.id.addfriend_edittext);





         return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.addfriend_btn:
                    Log.d(TAG, "2");
                    addFriend(fc.getText().toString());
                    break;

            }
        }
    };

     private void setup(){//기본화면 업뎃,프로필업뎃,친구목록업뎃


         DocumentReference docRef = db.collection("users").document(user.getUid());
         docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if (task.isSuccessful()) {
                     DocumentSnapshot document = task.getResult();
                     if (document != null) {
                         String name=""+document.get("name");
                         String email="이메일 : "+user.getEmail();
                         String phonenumber="전화번호 : "+document.get("phoneNumber");
                         myfriendcord=""+document.get("friendcord");
                         String cord ="친구 코드 : "+myfriendcord;


                         TextView nametext= getView().findViewById(R.id.profile_username);
                         TextView emailtext= getView().findViewById(R.id.profile_email);
                         TextView phonenumbertext= getView().findViewById(R.id.profile_phonenumber);
                         TextView friendcordtext= getView().findViewById(R.id.profile_userfriendcord);

                         nametext.setText(name);
                         emailtext.setText(email);
                         phonenumbertext.setText(phonenumber);
                         friendcordtext.setText(cord);

                     } else {
                         Log.d(TAG, "No such document");
                     }
                 } else {
                     Log.d(TAG, "get failed with ", task.getException());
                 }
             }
         });


         loadmyFriendList();

     }

     //데이터 읽어와서 어뎁터 업데이트하는 함수
    //개느림 근데 개선불가 ㅋ;
    private  void loadmyFriendList(){

        myfriendList=new ArrayList<>();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        ArrayList<String> list = (ArrayList<String>) document.get("friends");
                        if (list != null){
                            for (String str : list){//str은 친구의 uid

                                frienduidList.add(str);
                                DocumentReference docRef2 = db.collection("users").document(str);
                                docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document != null) {

                                                MemberInfo m=new MemberInfo( Integer.parseInt(String.valueOf(document.get("icon")))
                                                        ,(String) document.get("name"),"","","");

                                                myfriendList.add(m);
                                                adapter = new ProfileandFriendAdapter(myfriendList);
                                                recyclerView.setAdapter(adapter);
                                            }
                                        }

                                    }
                                });
                            }
                            for(MemberInfo m :myfriendList){
                                Log.d(TAG, m.getName()+"");
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


    private void addFriend(final String friendcord){
         if(!friendcord.equals(myfriendcord) ){

             Log.d(TAG, myfriendcord);
             db.collection("users")
                     .get()
                     .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<QuerySnapshot> task) {
                             if (task.isSuccessful()) {
                                 for (QueryDocumentSnapshot document : task.getResult()) {

                                     if (("" + document.getData().get("friendcord")).equals(friendcord)) {
                                         if (!frienduidList.contains("" + document.getId())) {
                                             Log.d(TAG, "contain");
                                             int icon = 0;
                                             //내친구 데이터에 친구추가
                                             frienduidList.add("" + document.getId());
                                             DocumentReference Ref1 = db.collection("users").document(user.getUid());
                                             Ref1.update("friends", FieldValue.arrayUnion("" + document.getId()));

                                             //친구 데이터에 나를 추가
                                             DocumentReference Ref2 = db.collection("users").document("" + document.getId());
                                             Ref2.update("friends", FieldValue.arrayUnion(user.getUid()));


                                             //어뎁터 업데이트
                                             loadmyFriendList();

                                             //삭제는 이렇게
                                             //Ref2.update("friends", FieldValue.arrayRemove(user.getUid()));
                                             break;

                                         }
                                     }
                                 }

                             } else {
                                 Log.d(TAG, "Error getting documents: ", task.getException());
                             }
                         }
                     });
         }
    }
    private void setFriendsData(String userUID,ArrayList<String> list){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("friends", list);

        db.collection("users").document(userUID).update(map);
    }

}
