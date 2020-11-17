package com.example.teamproject_toto;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ItemViewHolder> {
    private ArrayList<CommunityboardInfo> listData = new ArrayList<>();
    private OnItemClickListener mListener = null ;

    public CommunityAdapter(ArrayList<CommunityboardInfo> data){
        listData=data;
    }

    public ArrayList<CommunityboardInfo> getListData() {
        return listData;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.communityitems, parent, false);

        return new ItemViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        CommunityboardInfo data = listData.get(i);

        // 데이터 결합
        holder.nameText.setText(data.getNickname());
        holder.dateText.setText(data.getDate());
        holder.titleText.setText(data.getTitle());
        holder.contentText.setText(data.getContent());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText, dateText, titleText, contentText;

        ItemViewHolder(final View itemView) {
            super(itemView);
            nameText= itemView.findViewById(R.id.citem_nickname);
            dateText =itemView.findViewById(R.id.citem_date);
            titleText=itemView.findViewById(R.id.citem_title);
            contentText=itemView.findViewById(R.id.citem_content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(view, pos);
                        }
                    }
                }
            });

        }
        void onBind(CommunityboardInfo data) {
            nameText.setText(data.getNickname());
            dateText.setText(data.getDate());
            titleText.setText(data.getTitle());
            contentText.setText(data.getContent());

        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }



}