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

import com.example.plantoperator.Adapters.AvailableDestinationAdapter;
import com.example.plantoperator.R;
import com.example.plantoperator.SelectUserForCycleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectDestination extends Fragment {

    RecyclerView available_destination_list;
    AvailableDestinationAdapter available_destination_list_adapter;
    List<String> list_available_destination = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SelectDestination() {
        // Required empty public constructor
    }


    public static SelectDestination newInstance(String param1, String param2) {
        SelectDestination fragment = new SelectDestination();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_destination, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        available_destination_list = view.findViewById(R.id.available_destination_list);
        available_destination_list.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseFirestore.getInstance().
                collection("Customers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot:task.getResult()){
                        list_available_destination.add(documentSnapshot.get("location_name").toString());
                    }
                }
                available_destination_list_adapter = new AvailableDestinationAdapter(list_available_destination, (SelectUserForCycleActivity)getActivity());

                available_destination_list.setAdapter(available_destination_list_adapter);
            }
        });

    }
}