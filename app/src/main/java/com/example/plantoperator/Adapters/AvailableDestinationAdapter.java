package com.example.plantoperator.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.R;

import java.util.List;


public class AvailableDestinationAdapter extends RecyclerView.Adapter<AvailableDestinationAdapter.AvailableDestinationViewHolder>{
    List<String> list_available_destination;
    Integer selectedItemPos = -1;
    Integer lastItemSelectedPos = -1;




    public AvailableDestinationAdapter(List<String> list_available_destination) {
        this.list_available_destination = list_available_destination;

    }

    @NonNull
    @Override
    public AvailableDestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.available_destination_list_viewholder, parent, false);
        return new AvailableDestinationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableDestinationViewHolder holder, int position) {
        holder.available_list_destination_element.setText(list_available_destination.get(position));
        if(position == selectedItemPos){
            holder.selected_icon.setVisibility(View.VISIBLE);
        }
        else{
            holder.selected_icon.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return list_available_destination.size();
    }

    public class AvailableDestinationViewHolder extends RecyclerView.ViewHolder{

        TextView available_list_destination_element;
        View itemView;
        String destination;
        ImageView selected_icon;

        public AvailableDestinationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            available_list_destination_element = itemView.findViewById(R.id.available_list_destination_element);
            selected_icon = itemView.findViewById(R.id.selected_icon);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    destination = available_list_destination_element.getText().toString();

                    selectedItemPos = getAdapterPosition();
                    if(lastItemSelectedPos == -1){
                        lastItemSelectedPos = selectedItemPos;
                    }
                    else{
                        notifyItemChanged(lastItemSelectedPos);
                        lastItemSelectedPos = selectedItemPos;
                    }
                    notifyItemChanged(selectedItemPos);
                }
            });

        }

    }

}