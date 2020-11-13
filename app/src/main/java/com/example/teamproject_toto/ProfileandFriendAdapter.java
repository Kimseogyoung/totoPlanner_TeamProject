package com.example.teamproject_toto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfileandFriendAdapter extends RecyclerView.Adapter<ProfileandFriendAdapter.ItemViewHolder> {
    private ArrayList<MemberInfo> listData = new ArrayList<MemberInfo>();

    public ProfileandFriendAdapter(ArrayList<MemberInfo> data){
        listData = data;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profileandfirenditems, parent, false);

        return new ItemViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        MemberInfo data = listData.get(i);

        // 데이터 결합
        holder.nameText.setText(data.getName());
        holder.iconImg.setImageResource(data.getIcon());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public ImageView iconImg;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameText= itemView.findViewById(R.id.friendlist_name);
            iconImg=itemView.findViewById(R.id.friendlist_icon);

        }
        void onBind(TimelineboardInfo data) {

            nameText.setText(data.getName());
            iconImg.setImageResource(data.getIcon());

        }

    }
}
