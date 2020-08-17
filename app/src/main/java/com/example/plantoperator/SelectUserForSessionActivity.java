package com.example.plantoperator;

import android.graphics.Color;
import android.os.Bundle;
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
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.viewpager.widget.ViewPager;

import com.example.plantoperator.Fragments.SelectDestination;
import com.example.plantoperator.Fragments.SelectUsers;
import com.google.android.material.transition.platform.MaterialArcMotion;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;

import java.util.ArrayList;
import java.util.List;

public class SelectUserForSessionActivity extends AppCompatActivity {
    Toolbar select_user_sesssion_toolbar;

    ViewPager user_and_location;
    ViewPagerAdapter user_and_location_adapter;
    Fragment selectUser,selectDestination;

    TextView next,prev;
    ImageButton next_button,prev_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        setContentView(R.layout.acitvity_select_user_for_session);
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());

        getWindow().setSharedElementEnterTransition(buildTransition());
        getWindow().setSharedElementExitTransition(buildTransition());
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
                user_and_location.setCurrentItem(0,true);
                prev_button.setVisibility(View.INVISIBLE);
                next_button.setVisibility(View.VISIBLE);
                prev.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);

            }
        });



    }



    private MaterialContainerTransform buildTransition(){
        com.google.android.material.transition.platform.MaterialContainerTransform materialContainerTransform = new com.google.android.material.transition.platform.MaterialContainerTransform();
        materialContainerTransform.addTarget(R.id.user_location_viewpager);
        materialContainerTransform.setAllContainerColors(Color.parseColor("#ffffff"));
        materialContainerTransform.setDuration(500);
        materialContainerTransform.setPathMotion(new MaterialArcMotion());
        materialContainerTransform.setInterpolator(new FastOutLinearInInterpolator());
        materialContainerTransform.setFadeMode(MaterialContainerTransform.FADE_MODE_CROSS);
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