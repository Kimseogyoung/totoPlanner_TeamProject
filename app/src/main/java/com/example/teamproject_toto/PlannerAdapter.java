package com.example.teamproject_toto;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;
//import static com.example.teamproject_toto.PlannerFragment.checks;
import static com.example.teamproject_toto.PlannerFragment.today;


public class PlannerAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<PlannerItems> listViewItemList = new ArrayList<PlannerItems>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // ListViewAdapter의 생성자
    public PlannerAdapter(){
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.planner_items, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        final TextView textView = (TextView) convertView.findViewById(R.id.pitem_tv) ;
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.pitem_cb);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final PlannerItems listViewItem = listViewItemList.get(position);


        // 아이템 내 각 위젯에 데이터 반영
        textView.setText(listViewItem.getText());
        checkBox.setChecked(listViewItem.getCv());
        if (checkBox.isChecked()) textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    listViewItem.setCv(true);
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else {
                    listViewItem.setCv(false);
                    textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                }


                CVStore();

            }
        });


        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(PlannerItems item) {
        listViewItemList.add(item);

    }

    public void CVStore(){

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String ss = format.format(today);

        ArrayList<Boolean> list = new ArrayList<Boolean>();

        for(int i = 0; i < listViewItemList.size(); i++){
            list.add(listViewItemList.get(i).getCv());
        }

        db.collection("users").document(user.getUid())
                .collection("planner").document(ss).update("cv", list);

    }




}