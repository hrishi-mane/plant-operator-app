package com.example.plantoperator.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.POJO.UserDetails;
import com.example.plantoperator.R;
import com.example.plantoperator.SelectUserForSessionActivity;

import java.util.ArrayList;
import java.util.List;

public class AvailableUserListAdapter extends RecyclerView.Adapter<AvailableUserListAdapter.AvailableUserListViewHolder>{
    List<UserDetails> list_available_users;
    ArrayList<String> selected_list_available_users = new ArrayList<>();
    AvailableUserListAdapterToSelectUser availableUserListAdapterToSelectUser;


    public AvailableUserListAdapter() {

    }

    public AvailableUserListAdapter(List<UserDetails> list_available_users) {
        this.list_available_users = list_available_users;
        availableUserListAdapterToSelectUser = new SelectUserForSessionActivity();
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



    public interface AvailableUserListAdapterToSelectUser{
        void sendData(List<String> selected_list_available_users);
    }

    public class AvailableUserListViewHolder extends RecyclerView.ViewHolder{
        TextView available_list_name_element;
        View itemView;
        CheckBox select_user;
        public AvailableUserListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            available_list_name_element = itemView.findViewById(R.id.available_list_name_element);

            select_user = itemView.findViewById(R.id.select_user_checkbox);

            select_user.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isClicked) {
                    if(isClicked){
                        selected_list_available_users.add(available_list_name_element.getText().toString());
                        availableUserListAdapterToSelectUser.sendData(selected_list_available_users);

                        Log.d("CheckBox", "onCheckedChanged: clicked" + selected_list_available_users);
                    }
                    else{
                        selected_list_available_users.remove(available_list_name_element.getText().toString());
                        Log.d("CheckBox", "onCheckedChanged: unclicked" + selected_list_available_users);
                    }
                }
            });
        }

    }


}
