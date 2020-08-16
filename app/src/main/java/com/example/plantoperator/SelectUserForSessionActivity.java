package com.example.plantoperator;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

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
    Fragment selectUser,selectDestination;
    ImageButton next_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        setContentView(R.layout.activity_select_user_for_session);
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        getWindow().setSharedElementEnterTransition(buildTransition());
        getWindow().setSharedElementExitTransition(buildTransition());
        getWindow().setSharedElementReenterTransition(buildTransition());
        super.onCreate(savedInstanceState);

        select_user_sesssion_toolbar = findViewById(R.id.session_tool_bar);
        user_and_location = findViewById(R.id.user_location_viewpager);
        selectUser = new SelectUsers();
        selectDestination = new SelectDestination();

        ViewPagerAdapter user_and_location_adapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        user_and_location_adapter.addFragment(selectUser);
        user_and_location_adapter.addFragment(selectDestination);
        user_and_location.setAdapter(user_and_location_adapter);

        setSupportActionBar(select_user_sesssion_toolbar);
        getSupportActionBar().setTitle("Add Users to Session");

        select_user_sesssion_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }

    private MaterialContainerTransform buildTransition(){
        com.google.android.material.transition.platform.MaterialContainerTransform materialContainerTransform = new com.google.android.material.transition.platform.MaterialContainerTransform();
        materialContainerTransform.addTarget(R.id.containers);
        materialContainerTransform.setAllContainerColors(Color.parseColor("#ffffff"));
        materialContainerTransform.setDuration(500);
        materialContainerTransform.setPathMotion(new MaterialArcMotion());
        materialContainerTransform.setInterpolator(new FastOutLinearInInterpolator());;
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