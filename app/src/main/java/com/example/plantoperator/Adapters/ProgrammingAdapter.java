package com.example.plantoperator.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.R;

import java.util.List;

public class ProgrammingAdapter extends RecyclerView.Adapter<ProgrammingAdapter.ProgrammingViewHolder> {
    public RecyclerViewOnClickListener listener;
    private List<String> data;
    public ProgrammingAdapter(List<String> data, RecyclerViewOnClickListener listener){
        this.data = data;
        Log.d("recycler", "insideConstructorPA: " + this.data);
        this.listener = listener;
    }


    @NonNull
    @Override
    public ProgrammingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("recycler", "onCreateViewHolder: inside");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_layout,parent,false);
        return new ProgrammingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgrammingViewHolder holder, int position) {
        Log.d("recycler", "onBindViewHolder: inside");
        String title = data.get(position);
        holder.txtTitle.setText(title);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface RecyclerViewOnClickListener{
        void onClick(View v, int position);
    }

    public class ProgrammingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imgIcon;
        TextView txtTitle;

            public ProgrammingViewHolder(@NonNull View itemView) {
                super(itemView);
                txtTitle =(TextView) itemView.findViewById(R.id.txtTitle);
                itemView.setOnClickListener(this);
            }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
