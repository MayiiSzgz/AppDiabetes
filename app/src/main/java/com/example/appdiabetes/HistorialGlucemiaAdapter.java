package com.example.appdiabetes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialGlucemiaAdapter extends RecyclerView.Adapter<HistorialGlucemiaAdapter.GlucemiaViewHolder> {

    private List<Glucemia> glucemiaList;

    public HistorialGlucemiaAdapter(List<Glucemia> glucemiaList) {
        this.glucemiaList = glucemiaList;
    }

    @NonNull
    @Override
    public GlucemiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_glucemia, parent, false);
        return new GlucemiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlucemiaViewHolder holder, int position) {
        Glucemia glucemia = glucemiaList.get(position);
        holder.bind(glucemia);
    }

    @Override
    public int getItemCount() {
        return glucemiaList.size();
    }

    public class GlucemiaViewHolder extends RecyclerView.ViewHolder {

        private TextView valorTextView;
        private TextView unidadTextView;

        public GlucemiaViewHolder(@NonNull View itemView) {
            super(itemView);
            valorTextView = itemView.findViewById(R.id.valorTextView);
            unidadTextView = itemView.findViewById(R.id.unidadTextView);
        }

        public void bind(Glucemia glucemia) {
            valorTextView.setText(String.valueOf(glucemia.getValor()));
            unidadTextView.setText(glucemia.getUnidad());
        }
    }
}
