package com.example.plantoperator.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.Adapters.AvailableUserListAdapter;
import com.example.plantoperator.POJO.UserDetails;
import com.example.plantoperator.R;
import com.example.plantoperator.SelectUserForCycleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SelectUsers extends Fragment{

    RecyclerView available_user_list;
    AvailableUserListAdapter available_user_list_adapter;
    List<UserDetails> list_available_users = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SelectUsers() {

    }


    public static SelectUsers newInstance(String param1, String param2) {
        SelectUsers fragment = new SelectUsers();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        available_user_list = view.findViewById(R.id.available_user_list);
        available_user_list.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot:task.getResult()){
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                        list_available_users.add(userDetails);

                    }
                    available_user_list_adapter = new AvailableUserListAdapter(list_available_users, (SelectUserForCycleActivity) getActivity());

                    available_user_list.setAdapter(available_user_list_adapter);
                }


            }
        });

    }





}