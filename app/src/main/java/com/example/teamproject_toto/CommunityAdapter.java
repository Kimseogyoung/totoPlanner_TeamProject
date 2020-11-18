package com.example.teamproject_toto;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ItemViewHolder> {
    private ArrayList<CommunityboardInfo> listData = new ArrayList<>();
    private OnItemClickListener mListener = null ;
    FirebaseStorage storage;
    private Context context;
    CommunityBoardFragment fragment;
    String kinds;

    public CommunityAdapter(ArrayList<CommunityboardInfo> data, Context c, CommunityBoardFragment f, String k){
        listData=data;
        context = c;
        storage= FirebaseStorage.getInstance("gs://todaytogether-8a723.appspot.com/");
        fragment=f;
        kinds = k;
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
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int i) {
        CommunityboardInfo data = listData.get(i);

        // 데이터 결합
        holder.nameText.setText(data.getNickname());
        holder.dateText.setText(data.getDate());
        holder.titleText.setText(data.getTitle());
        holder.contentText.setText(data.getContent());

        //FirebaseStorage 인스턴스를 생성
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        // 위의 저장소를 참조하는 파일명으로 지정
        StorageReference storageReference = firebaseStorage.getReference().child(kinds+"/"+data.getImg());
        //StorageReference에서 파일 다운로드 URL 가져옴
        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    holder.Img.setVisibility(View.VISIBLE);
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

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText, dateText, titleText, contentText;
        public ImageView Img;

        ItemViewHolder(final View itemView) {
            super(itemView);
            nameText= itemView.findViewById(R.id.citem_nickname);
            dateText =itemView.findViewById(R.id.citem_date);
            titleText=itemView.findViewById(R.id.citem_title);
            contentText=itemView.findViewById(R.id.citem_content);
            Img=itemView.findViewById(R.id.citem_img);

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