package com.example.teamproject_toto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CommunityCommentAdapter extends RecyclerView.Adapter<CommunityCommentAdapter.ItemViewHolder> {
    private ArrayList<CommunityCommentInfo> listData = new ArrayList<>();

    public CommunityCommentAdapter(ArrayList<CommunityCommentInfo> data){
        listData=data;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.communitycommentitems, parent, false);

        return new ItemViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        CommunityCommentInfo data = listData.get(i);

        // 데이터 결합
        holder.dateText.setText(data.getDate());
        holder.commentText.setText(data.getComment());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView dateText, commentText;

        ItemViewHolder(final View itemView) {
            super(itemView);
            dateText =itemView.findViewById(R.id.comment_date);
            commentText = itemView.findViewById(R.id.comment_tv);

        }
    }




}