package com.qcp.facebookapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.qcp.facebookapp.R;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static final String PROFILE_NAME = "profileName";
    private ViewPager vpHome;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private PagerAdapter pagerAdapter;
    private MenuItem prevMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById();
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getItemId() == R.id.search) {
//                    Toast.makeText(getApplicationContext(), "Check search", Toast.LENGTH_SHORT).show();
//                } else {
//
//                }
//                return false;
//            }
//        });
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
                } else {
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        Log.d("qcppTag","Check create Menu 1");
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.top_app_bar, menu);
//        MenuItem searchItem = menu.findItem(R.id.search);
//        SearchManager searchManager = (SearchManager) HomeActivity.this.getSystemService(Context.SEARCH_SERVICE);
//        Log.d("qcppTag","Check create Menu 2");
//        SearchView searchView = null;
//        if (searchItem != null) {
//            searchView = (SearchView) searchItem.getActionView();
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    Toast.makeText(getApplicationContext(), "Search" + query, Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//
//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    return false;
//                }
//            });
//        }
//        return super.onCreateOptionsMenu(menu);
//    }

    private void findViewById() {
        vpHome = findViewById(R.id.vp_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.tool_bar_top);
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