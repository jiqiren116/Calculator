package com.example.calculator;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
            Intent intent = new Intent("com.example.calculator.MY_BROADCAST");

            // 指定广播接收器的组件名
            intent.setComponent(new ComponentName("com.example.calculator",
                    "com.example.calculator.MyBroadcastReceiver"));

            //将表达式expression放到intent中
            intent.putExtra("expression", holder.historyExpression.getText().toString());

            v.getContext().sendBroadcast(intent);

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
