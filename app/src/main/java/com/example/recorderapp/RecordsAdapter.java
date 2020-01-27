package com.example.recorderapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder> {

    private static final String TAG = "RecordsAdapter";
    private OnRecordClickListener mOnRecordClickListener;
    private List<File> mFiles;

    public RecordsAdapter(OnRecordClickListener onRecordClickListener) {
        mOnRecordClickListener = onRecordClickListener;

    }

    @NonNull
    @Override
    public RecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_items, parent, false);
        return new RecordsViewHolder(view, mOnRecordClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mFiles == null ? 0 : mFiles.size();
    }


    public void setFiles(List<File> files) {
        mFiles = files;
        notifyDataSetChanged();

    }

    public class RecordsViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public RecordsViewHolder(@NonNull View itemView, OnRecordClickListener onRecordClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.title_record);
            mOnRecordClickListener = onRecordClickListener;
        }

        public void bind(int position) {
            File file = mFiles.get(position);
            title.setText(file.getName());

            itemView.setOnClickListener(v -> {
                mOnRecordClickListener.onRecordClick(file);

            });
        }
    }
}
