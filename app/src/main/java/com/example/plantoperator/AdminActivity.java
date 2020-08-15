package com.example.plantoperator;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.Adapters.UserListAdapter;
import com.example.plantoperator.Fragments.UserDetailsDialogFragment;
import com.example.plantoperator.POJO.UserDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.view.ActionMode.Callback;

import java.util.ArrayList;
import java.util.List;


public class AdminActivity extends AppCompatActivity implements UserDetailsDialogFragment.DialogToRecycler, Callback{
    FloatingActionButton fab;
    Toolbar mToolBar;
    RecyclerView users;

    List<UserDetails> arrayList = new ArrayList<>();

    UserListAdapter userListAdapter,userListAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        mToolBar = findViewById(R.id.toolBar_admin);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Manage Users");


        users = findViewById(R.id.user_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        users.setLayoutManager(linearLayoutManager);


        FirebaseFirestore.getInstance().collection("Users").orderBy("name", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                    arrayList.add(userDetails);

                }

                userListAdapter = new UserListAdapter(arrayList);
                users.setAdapter(userListAdapter);
            }
        });


        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();

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
    public void passData(UserDetails userDetails) {

        arrayList.add(userDetails);
        userListAdapter = new UserListAdapter(arrayList);
        users.setAdapter(userListAdapter);


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



