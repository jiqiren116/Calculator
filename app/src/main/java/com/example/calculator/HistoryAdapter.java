package com.example.calculator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<CalculationHistory> historyList;

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
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
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
