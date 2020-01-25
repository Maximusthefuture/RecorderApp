package com.example.recorderapp;

import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder> {
    private List<Record> mRecordList;
    private OnRecordClickListener mOnRecordClickListener;
    private static final String TAG = "RecordsAdapter";
    private List<File> mFiles;
    private Utils mUtils = new Utils();

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
        return mFiles == null ? 0 : mFiles.size();
//        return mRecordList == null ? 0 : mRecordList.size();
//        if (mRecordList.size() != 0) {
//            return mRecordList.size();
//
//        } else {
//            return 0;
//        }
    }

    public void setRecordList(List<Record> recordList) {
        mRecordList = recordList;
        notifyDataSetChanged();
    }

    public void setFiles(List<File> files) {
        mFiles = files;
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
            File file = mFiles.get(position);

//            Record record = mRecordList.get(position);
            title.setText(file.getName());
            duration.setText(String.valueOf(mUtils.getRecordLength(file)) + " s");


            itemView.setOnClickListener(v -> {
                mOnRecordClickListener.onRecordClick(file);
                notifyDataSetChanged();
                notifyItemInserted(getAdapterPosition() - 1);

//                Toast.makeText(itemView.getContext(), "" + mRecordList.get(i), Toast.LENGTH_SHORT).show();
            });

        }
    }
}
