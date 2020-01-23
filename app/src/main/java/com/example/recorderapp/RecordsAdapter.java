package com.example.recorderapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder> {
    private List<Record> mRecordList;
    private OnRecordClickListener mOnRecordClickListener;
    private static final String TAG = "RecordsAdapter";

    public RecordsAdapter(OnRecordClickListener onRecordClickListener) {
        mOnRecordClickListener = onRecordClickListener;
    }

    @NonNull
    @Override
    public RecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.record_items, parent, false);
        return new RecordsViewHolder(view, mOnRecordClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mRecordList.size() != 0) {
            return mRecordList.size();

        } else {
            return 0;
        }
    }

    public void setRecordList(List<Record> recordList) {
        mRecordList = recordList;
        notifyDataSetChanged();
    }

    public class RecordsViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView duration;


        public RecordsViewHolder(@NonNull View itemView, OnRecordClickListener onRecordClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.title_record);
            duration = itemView.findViewById(R.id.duration);
            mOnRecordClickListener = onRecordClickListener;
        }

        public void bind(int position) {
            Record record = mRecordList.get(position);
            title.setText(record.getTitle());
            duration.setText(record.getDuration());
            itemView.setOnClickListener(v -> {
                mOnRecordClickListener.onRecordClick();
                Toast.makeText(itemView.getContext(), "" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            });

        }
    }
}
