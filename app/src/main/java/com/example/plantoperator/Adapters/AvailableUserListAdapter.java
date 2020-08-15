package com.example.plantoperator.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.POJO.UserDetails;
import com.example.plantoperator.R;

import java.util.List;

public class AvailableUserListAdapter extends RecyclerView.Adapter<AvailableUserListAdapter.AvailableUserListViewHolder> {
    List<UserDetails> list_available_users;

    public AvailableUserListAdapter(List<UserDetails> list_available_users) {
        this.list_available_users = list_available_users;
    }

    @NonNull
    @Override
    public AvailableUserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.available_user_list_adapter, parent, false);

        return new AvailableUserListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AvailableUserListViewHolder holder, int position) {
        holder.available_list_name_element.setText(list_available_users.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list_available_users.size();
    }

    public class AvailableUserListViewHolder extends RecyclerView.ViewHolder{
        TextView available_list_name_element;
        View itemView;
        public AvailableUserListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            available_list_name_element = itemView.findViewById(R.id.available_list_name_element);
        }
    }
}
