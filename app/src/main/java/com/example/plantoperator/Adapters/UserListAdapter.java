package com.example.plantoperator.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.Dialogs.UserDetailsDialogFragment;
import com.example.plantoperator.POJO.UserDetails;
import com.example.plantoperator.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> implements Filterable {
    List<UserDetails> arrayListUserDetails;
    List<UserDetails> arrayListUserDetailsAll;

    ActionMode actionMode;


    private boolean multiSelect = false;
    private List<UserDetails> selectedItems = new ArrayList<>();


    public UserListAdapter(List<UserDetails> myDataset) {
        arrayListUserDetails = myDataset;
        this.arrayListUserDetailsAll = new ArrayList<>(arrayListUserDetails);
    }


    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            actionMode = mode;
            mode.getMenuInflater().inflate(R.menu.contextual_tool_bar_menu_admin, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }


        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            for (UserDetails userDetails : selectedItems) {
                arrayListUserDetails.remove(userDetails);
                arrayListUserDetailsAll.remove(userDetails);

                FirebaseFirestore.getInstance().collection("Users").whereEqualTo("name", userDetails.getName())
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference documentReference = FirebaseFirestore.getInstance().
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
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_viewholder, parent, false);

        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.name.setText(arrayListUserDetails.get(position).getName());
        holder.phone_no.setText(arrayListUserDetails.get(position).getPhone_number());

        holder.update(arrayListUserDetails.get(position));


    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        TextView name, phone_no;

        RelativeLayout relativeLayout;

        ImageButton recycler_view_menu;

        View itemView;

        Context mContext;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mContext = itemView.getContext();

            name = itemView.findViewById(R.id.list_name_element);
            phone_no = itemView.findViewById(R.id.list_phone_no_element);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            recycler_view_menu = itemView.findViewById(R.id.recycler_view_menu);

            recycler_view_menu.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            showPopUpMenu(view);

        }

        private void showPopUpMenu(final View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.recycler_view_item_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }


        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String nameToDialog = name.getText().toString();
            String phoneToDialog = phone_no.getText().toString();
            String cityToDialog = arrayListUserDetails.get(getAdapterPosition()).getCity();
            String addressToDialog = arrayListUserDetails.get(getAdapterPosition()).getAddress();

            UserDetailsDialogFragment userDetailsDialogFragment2 = new UserDetailsDialogFragment(nameToDialog, phoneToDialog,cityToDialog,addressToDialog,getAdapterPosition());
            userDetailsDialogFragment2.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "myDialog");

            return true;
        }


        void selectItem(UserDetails item) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    relativeLayout.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    relativeLayout.setBackgroundColor(Color.LTGRAY);
                }
            }
        }


        void update(final UserDetails userDetails) {
            if (selectedItems.contains(userDetails)) {
                relativeLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                relativeLayout.setBackgroundColor(Color.WHITE);
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity) view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(userDetails);
                    return true;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectItem(userDetails);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return arrayListUserDetails.size();
    }


    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<UserDetails> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(arrayListUserDetailsAll);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (UserDetails userDetails : arrayListUserDetailsAll) {
                    if (userDetails.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(userDetails);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            arrayListUserDetails.clear();
            arrayListUserDetails.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();

        }
    };


}

