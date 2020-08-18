package com.example.plantoperator;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.viewpager.widget.ViewPager;

import com.example.plantoperator.Adapters.AvailableUserListAdapter;
import com.example.plantoperator.Fragments.SelectDestination;
import com.example.plantoperator.Fragments.SelectUsers;
import com.example.plantoperator.POJO.PlantDetails;
import com.example.plantoperator.POJO.SessionDetails;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.transition.platform.MaterialArcMotion;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SelectUserForSessionActivity extends AppCompatActivity implements  AvailableUserListAdapter.AvailableUserListAdapterToSelectUser{
    Toolbar select_user_sesssion_toolbar;

    ViewPager user_and_location;
    ViewPagerAdapter user_and_location_adapter;
    Fragment selectUser,selectDestination;

    TextView next,prev;
    ImageButton next_button,prev_button;
    boolean mState = false;
    PlantDetails plantDetails;
    ArrayList<String> list_available_users;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        setContentView(R.layout.acitvity_select_user_for_session);
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());

        getWindow().setSharedElementEnterTransition(buildTransition());
        getWindow().setSharedElementReturnTransition(buildTransition());
        getWindow().setSharedElementReenterTransition(buildTransition());

        super.onCreate(savedInstanceState);

        select_user_sesssion_toolbar = findViewById(R.id.session_tool_bar);
        user_and_location = findViewById(R.id.user_location_viewpager);
        selectUser = new SelectUsers();
        selectDestination = new SelectDestination();

        user_and_location_adapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        user_and_location_adapter.addFragment(selectUser);
        user_and_location_adapter.addFragment(selectDestination);
        user_and_location.setAdapter(user_and_location_adapter);

        setSupportActionBar(select_user_sesssion_toolbar);
        getSupportActionBar().setTitle("Select Users");

        select_user_sesssion_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        next = findViewById(R.id.next_textview);
        prev = findViewById(R.id.prev_textview);
        next_button = findViewById(R.id.next_button);
        prev_button = findViewById(R.id.prev_button);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setTitle("Select Destination");
                mState = true;
                invalidateOptionsMenu();

                user_and_location.setCurrentItem(2, true);

                next_button.setVisibility(View.INVISIBLE);
                prev_button.setVisibility(View.VISIBLE);
                next.setVisibility(View.INVISIBLE);
                prev.setVisibility(View.VISIBLE);
            }
        });

        prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setTitle("Select Users");
                mState = false;
                invalidateOptionsMenu();

                user_and_location.setCurrentItem(0,true);

                prev_button.setVisibility(View.INVISIBLE);
                next_button.setVisibility(View.VISIBLE);
                prev.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_user_for_session_toolbar_menu,menu);
        MenuItem save_icon = menu.getItem(0);
        if(mState){
            save_icon.setVisible(true);
        }
        else{
            save_icon.setVisible(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_save_icon){
            FirebaseFirestore.getInstance().collection("Plants")
                    .document("101").get().
                    addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    plantDetails = documentSnapshot.toObject(PlantDetails.class);
                    insertSessionData(plantDetails);
                }
            });

            return true;
        }
        return false;
    }
    
    private void insertSessionData(PlantDetails plantDetails) {
        SessionDetails sessionDetails = new SessionDetails(plantDetails,"session123");
        FirebaseFirestore.getInstance().collection("Session").
                add(sessionDetails).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("Plant-POJO", "onSuccess: Plant POJO Added to Session");
                updateSession();
            }
        });

    }

    private void updateSession() {
        Log.d("list_available_users", "updateSession: " + this.list_available_users.size());
    }

    @Override
    public void sendData(List<String> selected_list_available_users) {
        this.list_available_users = new ArrayList<>();
        this.list_available_users.addAll(selected_list_available_users);
    }

    private MaterialContainerTransform buildTransition(){
        MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
        materialContainerTransform.addTarget(R.id.containers);
        materialContainerTransform.setAllContainerColors(Color.parseColor("#ffffff"));
        materialContainerTransform.setDuration(400);
        materialContainerTransform.setPathMotion(new MaterialArcMotion());
        materialContainerTransform.setInterpolator(new FastOutSlowInInterpolator());
        return materialContainerTransform;

    }



    private class ViewPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> fragmentList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        private void addFragment(Fragment fragment){
            fragmentList.add(fragment);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }




}