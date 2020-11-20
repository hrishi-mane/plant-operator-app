package com.example.plantoperator.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.POJO.SessionUserCustomerDetails;
import com.example.plantoperator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SessionUserListAdapter extends RecyclerView.Adapter<SessionUserListAdapter.SessionUserListViewHolder> {
    ArrayList<SessionUserCustomerDetails> session_user_customer_details_list;

    ArrayList<SessionUserCustomerDetails> selectedUserCustomer = new ArrayList<>();

    Boolean multiSelect = false;

    ActionMode mode;

    public SessionUserListAdapter(ArrayList<SessionUserCustomerDetails> session_user_customer_details_list) {
        this.session_user_customer_details_list = session_user_customer_details_list;
    }

    private ActionMode.Callback actionModeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            multiSelect = true;
            mode = actionMode;
            mode.getMenuInflater().inflate(R.menu.contextual_tool_bar_menu_admin, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            for (SessionUserCustomerDetails sessionUserCustomerDetails : selectedUserCustomer) {
                session_user_customer_details_list.remove(sessionUserCustomerDetails);

                FirebaseFirestore.getInstance().collection("Session").document("session123")
                        .collection("Users").whereEqualTo("userDetails", sessionUserCustomerDetails.userDetails)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            DocumentReference documentReference = FirebaseFirestore.getInstance().
                                    collection("Session").document("session123").
                                    collection("Users").document(documentSnapshot.getId());

                            documentReference.delete();
                        }
                    }
                });
            }
            mode.finish();
            return true;

        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            multiSelect = false;
            selectedUserCustomer.clear();
            notifyDataSetChanged();
        }
    };

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
        holder.update(session_user_customer_details_list.get(position));
    }


    @Override
    public int getItemCount() {
        return session_user_customer_details_list.size();
    }

    public class SessionUserListViewHolder extends RecyclerView.ViewHolder {
        TextView session_list_name_element, session_list_destination_element;
        View itemView;
        RelativeLayout relativeLayout;

        public SessionUserListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            session_list_name_element = itemView.findViewById(R.id.session_list_name_element);
            session_list_destination_element = itemView.findViewById(R.id.session_list_destination_element);

            relativeLayout = itemView.findViewById(R.id.user_list_layout);
        }

        void update(final SessionUserCustomerDetails sessionUserCustomerDetails) {
            if (selectedUserCustomer.contains(sessionUserCustomerDetails)) {
                relativeLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                relativeLayout.setBackgroundColor(Color.WHITE);
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallBack);

                    selectItem(sessionUserCustomerDetails);
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectItem(sessionUserCustomerDetails);
                }
            });
        }

        private void selectItem(SessionUserCustomerDetails sessionUserCustomerDetails) {
            if (multiSelect) {
                if (selectedUserCustomer.contains(sessionUserCustomerDetails)) {
                    relativeLayout.setBackgroundColor(Color.WHITE);
                    selectedUserCustomer.remove(sessionUserCustomerDetails);

                } else {
                    relativeLayout.setBackgroundColor(Color.LTGRAY);
                    selectedUserCustomer.add(sessionUserCustomerDetails);
                }
            }
        }

    }

}

