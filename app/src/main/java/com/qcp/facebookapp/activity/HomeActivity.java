package com.qcp.facebookapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.qcp.facebookapp.R;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ViewPager vpHome;
    private BottomNavigationView bottomNavigationView;
    private PagerAdapter pagerAdapter;
    private MenuItem prevMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById();
        pagerAdapter = new com.qcp.facebookapp.adapter.PagerAdapter(getSupportFragmentManager());
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        vpHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vpHome.setAdapter(pagerAdapter);
    }

    private void findViewById() {
        vpHome = findViewById(R.id.vp_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                vpHome.setCurrentItem(0);
                break;
            case R.id.nav_friends:
                vpHome.setCurrentItem(1);
                break;
            case R.id.nav_profile:
                vpHome.setCurrentItem(2);
                break;
        }
        return true;
    }
}