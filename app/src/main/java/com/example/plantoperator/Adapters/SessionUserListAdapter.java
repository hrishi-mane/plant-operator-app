package com.example.plantoperator.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.POJO.SessionUserCustomerDetails;
import com.example.plantoperator.R;

import java.util.ArrayList;

public class SessionUserListAdapter extends RecyclerView.Adapter<SessionUserListAdapter.SessionUserListViewHolder> {
    ArrayList<SessionUserCustomerDetails> session_user_customer_details_list;

    public SessionUserListAdapter(ArrayList<SessionUserCustomerDetails> session_user_customer_details_list) {
        this.session_user_customer_details_list = session_user_customer_details_list;
    }

    @NonNull
    @Override
    public SessionUserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_user_list_viewholder, parent, false);
        return new SessionUserListViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull SessionUserListViewHolder holder, int position) {
        holder.session_list_name_element.setText(session_user_customer_details_list.get(position).
                userDetails.getName());
        holder.session_list_destination_element.setText(session_user_customer_details_list.get(position).
                customerDetails.getLocation_name());
    }


    @Override
    public int getItemCount() {
        return session_user_customer_details_list.size();
    }

    public class SessionUserListViewHolder extends RecyclerView.ViewHolder{
        TextView session_list_name_element , session_list_destination_element;
        public SessionUserListViewHolder(@NonNull View itemView) {
            super(itemView);
            session_list_name_element = itemView.findViewById(R.id.session_list_name_element);
            session_list_destination_element = itemView.findViewById(R.id.session_list_destination_element);

        }
    }
}
