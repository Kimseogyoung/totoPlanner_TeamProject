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
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText, dateText;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameText= itemView.findViewById(R.id.item_name);
            dateText =itemView.findViewById(R.id.item_date);
        }
        void onBind(TimelineboardInfo data) {
            nameText.setText(data.getTitle());
            dateText.setText(data.getDate());

        }

    }
}
