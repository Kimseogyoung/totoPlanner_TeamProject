package com.example.teamproject_toto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ItemViewHolder> {
    private ArrayList<TimelineboardInfo> listData = new ArrayList<>();

    public TimelineAdapter(ArrayList<TimelineboardInfo> data){
        listData=data;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timelineitems, parent, false);

        return new ItemViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int i) {
        TimelineboardInfo data = listData.get(i);

        // 데이터 결합
        holder.nameText.setText(data.getName());
        holder.dateText.setText(data.getDate());
        holder.titleText.setText(data.getTitle());
        holder.contentText.setText(data.getContent());
        holder.iconImg.setImageResource(data.getIcon());
        holder.Img.setImageResource(data.getImg());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText, dateText, titleText,contentText;
        public ImageView iconImg, Img;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameText= itemView.findViewById(R.id.item_name);
            dateText =itemView.findViewById(R.id.item_date);
            titleText=itemView.findViewById(R.id.item_title);
            contentText=itemView.findViewById(R.id.item_content);
            iconImg=itemView.findViewById(R.id.item_icon);
            Img=itemView.findViewById(R.id.item_img);
        }
        void onBind(TimelineboardInfo data) {
            nameText.setText(data.getName());
            dateText.setText(data.getDate());
            titleText.setText(data.getTitle());
            contentText.setText(data.getContent());
            iconImg.setImageResource(data.getIcon());
            Img.setImageResource(data.getImg());

        }

    }
}