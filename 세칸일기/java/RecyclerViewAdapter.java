package com.example.mydiary_ver6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    public interface RecyclerViewAdapterEventListener{
        void onClick(View view);
    }

    private DiaryDB diaryListDB;
    private ArrayList<Diary> diaryArrayList;    //일기 데이터 저장할 ArrayList

    private RecyclerViewAdapterEventListener listener;      //어떤 아이템이 선택됐는지 알려줌(리사이클러뷰에서 선택한 메모를 불러오기 위함)

    public RecyclerViewAdapter(Context context){
        diaryListDB = new DiaryDB(context);
        diaryArrayList = diaryListDB.loadDiaryList();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView date;

        public ViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Diary diary = diaryArrayList.get(position);     //position에 맞는 diary 객체 반환

        //제목과 날짜를 목록 텍스트뷰에 설정
        holder.title.setText(diary.fileName);
        holder.date.setText(diary.date);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "일기 클릭됨", Toast.LENGTH_SHORT).show();
                if(listener != null){
                    listener.onClick(v);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (diaryArrayList == null){    //DB로부터 배열 받지 못한 경우
            return 0;
        }
        return diaryArrayList.size();
    }

    public RecyclerViewAdapter(Context context, RecyclerViewAdapterEventListener listener){
        diaryListDB = new DiaryDB(context);
        this.listener = listener;
    }

    public Diary getDiary(int position){
        if(diaryArrayList == null || diaryArrayList.size() < position){
            return null;
        }
        return diaryArrayList.get(position);
    }

    //메모 데이터 다시 조회, 바뀐 내용 적용
    public void refreshDiaryList(){
        diaryArrayList = diaryListDB.loadDiaryList();
        notifyDataSetChanged();
    }

}
