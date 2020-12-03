package com.example.plantoperator.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.Adapters.SessionUserListAdapter;
import com.example.plantoperator.POJO.SessionUserCustomerDetails;
import com.example.plantoperator.R;
import com.example.plantoperator.SelectUserForCycleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class UserFragment extends Fragment {
    RecyclerView session_user_list;
    SessionUserListAdapter sessionUserListAdapter;
    FloatingActionButton add_user_to_session;
    ArrayList<SessionUserCustomerDetails> session_user_customer_details_list = new ArrayList<>();
    TextView empty_user_textview;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    public UserFragment getInstance() {
        return UserFragment.this;
    }


    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        View v = inflater.inflate(R.layout.fragment_in_session, container, false);

        session_user_list = v.findViewById(R.id.session_user_recyclerview);
        empty_user_textview = v.findViewById(R.id.empty_user_textView);
        session_user_list.setLayoutManager(new LinearLayoutManager(getActivity()));

        sessionUserListAdapter = new SessionUserListAdapter(session_user_customer_details_list);
        session_user_list.setAdapter(sessionUserListAdapter);
        checkIfFirestoreUpdated();
        return v;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        add_user_to_session = view.findViewById(R.id.add_user_to_session_button);

        add_user_to_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().size() == 0){
                            Snackbar.make(view, "Please add Drivers to Plant!", Snackbar.LENGTH_SHORT).
                                    setBackgroundTint(Color.LTGRAY).setTextColor(Color.RED).setDuration(2500).show();
                        }
                        else{
                            Intent intent = new Intent(getActivity(), SelectUserForCycleActivity.class);
                            ActivityOptionsCompat option = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), (View) add_user_to_session, "shared_element_trans");
                            startActivity(intent, option.toBundle());
                        }
                    }
                });

            }
        });
    }

    public void checkIfFirestoreUpdated() {
        FirebaseFirestore.getInstance().collection("Session").document("session123")
                .collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("onEvent ", "SessionListener: Failed " + error);
                }
                if (value.size() == 0) {
                    empty_user_textview.setVisibility(View.VISIBLE);
                    cycleNotExists();

                } else {
                    empty_user_textview.setVisibility(View.INVISIBLE);
                    for (DocumentSnapshot documentSnapshot : value) {
                        addUserToCycle(documentSnapshot);
                    }
                }
            }
        });
    }

    private void addUserToCycle(DocumentSnapshot documentSnapshot) {
        Log.d("addUserToCycleCalled:", "called");
        SessionUserCustomerDetails fetched_user = documentSnapshot.toObject(SessionUserCustomerDetails.class);

        if (session_user_customer_details_list.size() == 0) {
            session_user_customer_details_list.add(fetched_user);
            sessionUserListAdapter.notifyDataSetChanged();

        } else {
            checkUserExistsInCycle(fetched_user);

        }
    }

    private void checkUserExistsInCycle(SessionUserCustomerDetails fetched_user) {
        Log.d("checkUserExistsInCycle", "checkUserExistsInCycle: called");
        int user_exists = 0;
        for (SessionUserCustomerDetails existing_user : session_user_customer_details_list) {
            String fetched_name = fetched_user.userDetails.getName();
            String existing_name = existing_user.userDetails.getName();

            if (fetched_name.equals(existing_name)) {
                user_exists = 1;
                break;
            }
        }

        if (user_exists == 0) {
            Log.d("checkUserExistsInCycle", "checkUserExistsInCycle: no");
            session_user_customer_details_list.add(fetched_user);
            sessionUserListAdapter.notifyDataSetChanged();
        } else {
            Log.d("checkUserExistsInCycle:", "no");
        }

    }

    private void cycleNotExists() {
        session_user_customer_details_list.clear();
        sessionUserListAdapter.notifyDataSetChanged();
    }


}


