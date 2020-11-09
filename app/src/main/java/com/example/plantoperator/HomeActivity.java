package com.example.plantoperator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.plantoperator.Dialogs.DeleteCycleConfirmationFragment;
import com.example.plantoperator.Fragments.ReceiverFragment;
import com.example.plantoperator.Fragments.UserFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity{

    Toolbar mToolBar;
    ViewPager mViewPager;
    TabLayout mTabLayout;
    Fragment mUserFragment,mMapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolBar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolBar);

        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);

        mTabLayout.setupWithViewPager(mViewPager);

        mMapFragment = new ReceiverFragment();
        mUserFragment = new UserFragment();


        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        mViewPagerAdapter.addFragment(mMapFragment, "MAP");
        mViewPagerAdapter.addFragment(mUserFragment, "IN-SESSION");
        mViewPager.setAdapter(mViewPagerAdapter);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> titles= new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);

        }

        private void addFragment(Fragment fragment, String title){
            fragmentList.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tool_bar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.admin_page_option){
            Intent intent = new Intent(HomeActivity.this, AdminActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.delete_cycle){
            showConfirmationDialog();
        }
        return true;
    }

    private void showConfirmationDialog() {
        DeleteCycleConfirmationFragment deleteCycleConfirmationFragment = new DeleteCycleConfirmationFragment();
        deleteCycleConfirmationFragment.show(getSupportFragmentManager(), "AlertDialog");
    }
}