package com.example.calculator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private static final String TAG = "HistoryAdapter";
    private ArrayList<CalculationHistory> historyList;

    public HistoryAdapter(ArrayList<CalculationHistory> calculationHistoryList) {
        this.historyList = calculationHistoryList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView historyExpression;
        TextView historyTimestamp;

        public ViewHolder(View view) {
            super(view);
            historyExpression = view.findViewById(R.id.history_expression);
            historyTimestamp = view.findViewById(R.id.history_timestamp);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "你点击了！！！");

        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CalculationHistory history = historyList.get(position);
        holder.historyExpression.setText(history.getExpression());
        holder.historyTimestamp.setText(history.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

}
