package com.example.plantoperator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantoperator.Adapters.AvailableUserListAdapter;
import com.example.plantoperator.POJO.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.transition.platform.MaterialArcMotion;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectUserForSessionActivity extends AppCompatActivity {
    Toolbar session_tool_bar;

    List<UserDetails>  list_available_users = new ArrayList<>();

    RecyclerView available_user_list;

    AvailableUserListAdapter available_user_list_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        setContentView(R.layout.activity_select_user_for_session);
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementEnterTransition(buildTransition());
        getWindow().setSharedElementExitTransition(buildTransition());
        getWindow().setSharedElementReenterTransition(buildTransition());
        super.onCreate(savedInstanceState);

        session_tool_bar = findViewById(R.id.session_tool_bar);

        available_user_list = findViewById(R.id.available_user_list_recycler);
        available_user_list.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(session_tool_bar);
        getSupportActionBar().setTitle("Select Users");

        session_tool_bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FirebaseFirestore.getInstance().collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot:task.getResult()){
                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                        list_available_users.add(userDetails);
                    }
                }
                available_user_list_adapter = new AvailableUserListAdapter(list_available_users);

                available_user_list.setAdapter(available_user_list_adapter);
            }
        });



    }

















































    private MaterialContainerTransform buildTransition(){
        com.google.android.material.transition.platform.MaterialContainerTransform materialContainerTransform = new com.google.android.material.transition.platform.MaterialContainerTransform();
        materialContainerTransform.addTarget(R.id.containers);
        materialContainerTransform.setAllContainerColors(Color.parseColor("#ffffff"));
        materialContainerTransform.setDuration(500);
        materialContainerTransform.setPathMotion(new MaterialArcMotion());
        materialContainerTransform.setInterpolator(new FastOutLinearInInterpolator());
        materialContainerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_CROSS);
        return materialContainerTransform;

    }
}