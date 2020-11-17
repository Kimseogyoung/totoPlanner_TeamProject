package com.example.teamproject_toto;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfileandFriendAdapter extends RecyclerView.Adapter<ProfileandFriendAdapter.ItemViewHolder> {
    private ArrayList<MemberInfo> listData = new ArrayList<MemberInfo>();
    Context context;
    ProfileandFriendFragment fragment;

    public ProfileandFriendAdapter(ArrayList<MemberInfo> data, Context c, ProfileandFriendFragment fragment){
        context=c;
        listData = data;
        this.fragment=fragment;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profileandfirenditems, parent, false);

        return new ItemViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int i) {
        MemberInfo data = listData.get(i);

        // 데이터 결합
        holder.nameText.setText(data.getName());

        //FirebaseStorage 인스턴스를 생성
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        // 위의 저장소를 참조하는 파일명으로 지정
        StorageReference storageReference = firebaseStorage.getReference().child("profile/"+data.getIcon());
        //StorageReference에서 파일 다운로드 URL 가져옴
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // Glide 이용하여 이미지뷰에 로딩
                    Glide.with(context)
                            .load(task.getResult())
                            .into(holder.iconImg);
                } else {
                    // URL을 가져오지 못하면
                    holder.iconImg.setImageResource(R.drawable.basic);
                }
            }
        });
        holder.pos=i;
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.removeFriend(holder.pos);
            }
        });
        holder.planBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.openPlanTap(holder.pos);
            }
        });


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public ImageView iconImg;
        public Button delBtn,planBtn;
        public int pos;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameText= itemView.findViewById(R.id.friendlist_name);
            iconImg=itemView.findViewById(R.id.friendlist_icon);
            delBtn=itemView.findViewById(R.id.friendlist_delbtn);
            planBtn=itemView.findViewById(R.id.friendlist_planbtn);

        }

        /*
        void onBind(TimelineboardInfo data) {

            nameText.setText(data.getName());
            iconImg.setImageResource(data.getIcon());

        }

         */

    }
}
