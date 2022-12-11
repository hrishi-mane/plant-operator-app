package com.example.plantoperator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode.Callback;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.adapters.UserListAdapter;
import com.example.plantoperator.dialogs.UserDetailsDialogFragment;
import com.example.plantoperator.model.UserDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class AdminActivity extends AppCompatActivity implements UserDetailsDialogFragment.DialogToRecycler, Callback {
    FloatingActionButton add_users;
    Toolbar mToolBar;
    RecyclerView users;

    List<UserDetails> arrayList = new ArrayList<>();;

    UserListAdapter userListAdapter;

    LinearLayout empty_list_container;

    boolean update_user = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 4) {
            if (!(grantResults.length > 0)) {
                Toast.makeText(this, "The message permission is needed to send ID to the added USER.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat
                    .checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        4);

            }
        }

        empty_list_container = findViewById(R.id.empty_list_container);
        mToolBar = findViewById(R.id.toolBar_admin);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Manage Drivers");


        users = findViewById(R.id.user_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        users.setLayoutManager(linearLayoutManager);

        add_users = findViewById(R.id.add_users_button);
        add_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

            }
        });

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        checkIfFirestoreUpdated();
    }

    private void checkIfFirestoreUpdated() {

        FirebaseFirestore.getInstance().collection("Users").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.d("onEvent", "AdminListener: Failed " + error);
                        }
                        else if (value.size() == 0) {
                            empty_list_container.setVisibility(View.VISIBLE);
                            arrayList.clear();
                        }
                        else if (!update_user) {
                            arrayList = new ArrayList<>();

                            empty_list_container.setVisibility(View.INVISIBLE);
                            for (DocumentSnapshot documentSnapshot : value) {
                                UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                                arrayList.add(userDetails);

                            }
                            Log.d("LiveListener:", "Array size: " + arrayList.size());
                        }
                        else{
                            update_user = false;
                        }

                        userListAdapter = new UserListAdapter(arrayList);
                        users.setAdapter(userListAdapter);
                    }
                });
    }


    private void showDialog() {
        UserDetailsDialogFragment userDetailsDialogFragment = new UserDetailsDialogFragment();
        userDetailsDialogFragment.show(getSupportFragmentManager(), "example");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_bar_menu_admin, menu);
        MenuItem searchItem = menu.findItem(R.id.favorite);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }



    @Override
    public void passEditData(UserDetails userDetails, Integer adapterPosition) {
        update_user = true;
        arrayList.set(adapterPosition, userDetails);
        userListAdapter.notifyItemChanged(adapterPosition);
    }


    @Override
    public boolean onCreateActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.contextual_tool_bar_menu_admin, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(androidx.appcompat.view.ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.delete_icon) {
            return true;
        }
        return false;
    }


    @Override
    public void onDestroyActionMode(androidx.appcompat.view.ActionMode mode) {
    }


}



