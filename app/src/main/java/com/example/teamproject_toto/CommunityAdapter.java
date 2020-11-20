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


// CommunityAdapter.java 작성자 : 이아연
// 커뮤니티 리사이클러뷰에 추가될 아이템들의 adapter이다.
public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ItemViewHolder> {
    private ArrayList<CommunityboardInfo> listData = new ArrayList<>(); // CommunityboardInfo ArrayList
    private OnItemClickListener mListener = null ; // 아이템 클릭 리스너를 위부에 생성하기 위해 만든 리스너 변수
    FirebaseStorage storage; // 사진을 불러올때 접근하기 위한 stroage
    private Context context;
    CommunityBoardFragment fragment;
    String kinds; // 어느 커뮤니티인지를 저장하는 string. 해당 커뮤니티의 아이템들과 사진들을 불러와야 한다.

    public CommunityAdapter(ArrayList<CommunityboardInfo> data, Context c, CommunityBoardFragment f, String k){
        // 커뮤니티 생성자
        listData=data; // CommunityboardInfo의 ArrayList를 받아서 저장.
        context = c;
        storage= FirebaseStorage.getInstance("gs://todaytogether-8a723.appspot.com/");
        fragment=f;
        kinds = k; // 어떤 커뮤니티인지 받아서 저장.
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
        // kinds를 이용해서 해당 커뮤니티의 사진을 가져온다.
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
                    // URL을 가져오지 못하면 이미지뷰를 보이지 않게
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
        // 커뮤니티 사용자 닉네임, 업로드 날짜, 제목, 내용이 들어갈 TextView 생성
        public TextView nameText, dateText, titleText, contentText; 
        public ImageView Img; // 커뮤니티 아이템에 들어갈 이미지뷰

        ItemViewHolder(final View itemView) {
            super(itemView);
            nameText= itemView.findViewById(R.id.citem_nickname);
            dateText =itemView.findViewById(R.id.citem_date);
            titleText=itemView.findViewById(R.id.citem_title);
            contentText=itemView.findViewById(R.id.citem_content);
            Img=itemView.findViewById(R.id.citem_img);

            itemView.setOnClickListener(new View.OnClickListener() { // 아이템이 선택 되었을 때 리스너
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition() ; // 아이템 위치 가져오기
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            // 아이템이 선택되면 외부에서 onItemClick을 실행할 수 있게 mListener 설정
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

    // OnItemClickListener을 외부에서 사용가능하도록 interface 만듦
    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }

    // OnItemClickListener을 외부에서 사용가능하도록 interface 만듦
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }



}
