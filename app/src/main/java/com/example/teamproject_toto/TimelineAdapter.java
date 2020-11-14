package com.example.teamproject_toto;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ItemViewHolder> {
    private ArrayList<TimelineboardInfo> listData = new ArrayList<>();
    FirebaseStorage storage;
    private Context context;

    public TimelineAdapter(ArrayList<TimelineboardInfo> data, Context c){
        context=c;
        listData=data;
        storage= FirebaseStorage.getInstance("gs://todaytogether-8a723.appspot.com/");
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timelineitems, parent, false);

        return new ItemViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int i) {
        TimelineboardInfo data = listData.get(i);


        // 데이터 결합
        holder.nameText.setText(data.getName());
        holder.dateText.setText(data.getDate());
        holder.titleText.setText(data.getTitle());
        holder.contentText.setText(data.getContent());


            //FirebaseStorage 인스턴스를 생성
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            // 위의 저장소를 참조하는 파일명으로 지정
            StorageReference storageReference = firebaseStorage.getReference().child("images/"+data.getImg());
            //StorageReference에서 파일 다운로드 URL 가져옴
            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // Glide 이용하여 이미지뷰에 로딩
                        Glide.with(context)
                                .load(task.getResult())
                                .into(holder.Img);
                    } else {
                        // URL을 가져오지 못하면
                        holder.Img.setVisibility(View.GONE);
                    }
                }
            });
            StorageReference storageReference2 = firebaseStorage.getReference().child("profile/"+data.getWritercode());
            //StorageReference에서 파일 다운로드 URL 가져옴
            storageReference2.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                     if (task.isSuccessful()) {
                        // Glide 이용하여 이미지뷰에 로딩
                        Glide.with(context)
                            .load(task.getResult())
                            .into(holder.iconImg);
                    }
                     else{
                         //못가져오면
                         holder.iconImg.setImageResource(R.drawable.basic);
                     }
                }
            });



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
        }/*
        void onBind(TimelineboardInfo data) {
            nameText.setText(data.getName());
            dateText.setText(data.getDate());
            titleText.setText(data.getTitle());
            contentText.setText(data.getContent());
            iconImg.setImageResource(data.getIcon());
            Img.setImageResource(data.getImg());

        }*/

    }
}