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

import com.example.plantoperator.Adapters.AvailableDestinationAdapter;
import com.example.plantoperator.Adapters.AvailableUserListAdapter;
import com.example.plantoperator.Fragments.SelectDestination;
import com.example.plantoperator.Fragments.SelectUsers;
import com.example.plantoperator.POJO.CustomerDetails;
import com.example.plantoperator.POJO.PlantDetails;
import com.example.plantoperator.POJO.SessionInfo;
import com.example.plantoperator.POJO.SessionUserCustomerDetails;
import com.example.plantoperator.POJO.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.transition.platform.MaterialArcMotion;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectUserForCycleActivity extends AppCompatActivity
        implements AvailableUserListAdapter.AvailableUserListAdapterToSelectUser,
        AvailableDestinationAdapter.AvailableDestinationAdpToSelectUser {

    Toolbar select_user_sesssion_toolbar;

    ViewPager user_and_location;
    ViewPagerAdapter user_and_location_adapter;
    Fragment selectUser, selectDestination;

    TextView next, prev;
    ImageButton next_button, prev_button;
    boolean mState = false;
    List<String> list_available_users = new ArrayList<>();
    String selected_destination;
    List<SessionUserCustomerDetails> session_user_customer_details_list = new ArrayList<>();


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
                getSupportActionBar().setTitle("Select Location");
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

                user_and_location.setCurrentItem(0, true);

                prev_button.setVisibility(View.INVISIBLE);
                next_button.setVisibility(View.VISIBLE);
                prev.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_user_for_session_toolbar_menu, menu);
        MenuItem save_icon = menu.getItem(0);
        if (mState) {
            save_icon.setVisible(true);
        } else {
            save_icon.setVisible(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_save_icon) {
            getSessionPlant();

            finish();
            return true;
        }
        return false;
    }

    private void getSessionPlant() {
        FirebaseFirestore.getInstance().collection("Plants")
                .document("101").get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        PlantDetails plantDetails = documentSnapshot.toObject(PlantDetails.class);
                        insertSessionIdPlant(plantDetails, "session123");
                    }
                });
    }

    private void insertSessionIdPlant(PlantDetails plantDetails, String session_id) {
        SessionInfo sessionInfo = new SessionInfo(session_id, plantDetails);
        FirebaseFirestore.getInstance().collection("Session").document("session123").
                set(sessionInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getSessionCustomers();
            }
        });

    }

    private void getSessionCustomers() {

        FirebaseFirestore.getInstance().collection("Customers")
                .whereEqualTo("location_name", selected_destination).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                CustomerDetails customerDetails = documentSnapshot.toObject(CustomerDetails.class);
                getSessionUser(customerDetails);
            }
        });


    }

    private void getSessionUser(final CustomerDetails customerDetails) {
        Log.d("total_users", "getSessionUser: " + list_available_users);
        for (String available_user : list_available_users) {
            FirebaseFirestore.getInstance().collection("Users").
                    whereEqualTo("name", available_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);

                    SessionUserCustomerDetails sessionUserCustomerDetails = new SessionUserCustomerDetails(userDetails, customerDetails);
                    session_user_customer_details_list.add(sessionUserCustomerDetails);
                    insertSessionCustomerUsers(sessionUserCustomerDetails, documentSnapshot.getId());
                }
            });
        }
    }

    private void insertSessionCustomerUsers(SessionUserCustomerDetails sessionUserCustomerDetails, String session_user_id) {
        FirebaseFirestore.getInstance().collection("Session").document("session123")
                .collection("Users").document(session_user_id).set(sessionUserCustomerDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Session Status", "onSuccess: Session Created");
            }
        });
    }

    @Override
    public void sendAvailableUserData(List<String> selected_list_available_users) {

        list_available_users = selected_list_available_users;
        Log.d("list_users", "sendAvailableUserData: " + list_available_users);
    }

    @Override
    public void sendAvailableDestnData(String destination) {
        selected_destination = destination;
    }


    private MaterialContainerTransform buildTransition() {

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

        private void addFragment(Fragment fragment) {
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