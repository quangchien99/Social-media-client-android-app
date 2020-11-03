package com.qcp.facebookapp.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.qcp.facebookapp.R;
import com.qcp.facebookapp.constant.Const;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String PROFILE_NAME = "profileName";
    private ViewPager vpHome;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private PagerAdapter pagerAdapter;
    private MenuItem prevMenuItem;
    private Menu menu;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById();
        pagerAdapter = new com.qcp.facebookapp.adapter.PagerAdapter(getSupportFragmentManager());
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        this.menu = menu;
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("qcpp", "Searching for" + query);
                SharedPreferences.Editor editor = getSharedPreferences(Const.SEARCH_NAME, MODE_PRIVATE).edit();
                editor.putString(Const.SEARCH_NAME, query);
                editor.apply();
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        MenuItem itemChangePassword = menu.findItem(R.id.item_change_pw);
        MenuItem itemLogOut = menu.findItem(R.id.item_log_out);
        itemChangePassword.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(getApplicationContext(), "Changpw clicked", Toast.LENGTH_SHORT).show();
                goToChangePassword();
                return true;
            }
        });
        itemLogOut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Toast.makeText(getApplicationContext(), "Log out clicked", Toast.LENGTH_SHORT).show();
                logOut();
                return true;
            }
        });
        return true;
    }

    private void logOut() {
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Logging out")
                .setMessage("Are you sure to log out?")
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = getSharedPreferences(PROFILE_NAME, MODE_PRIVATE).edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .show();
    }

    private void goToChangePassword() {
        Intent intent = new Intent(HomeActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
    }


    private void findViewById() {
        vpHome = findViewById(R.id.vp_home);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.tool_bar_top);
        navigationView = findViewById(R.id.nav_drawer);
        drawerLayout = findViewById(R.id.layout_drawer);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                Log.d("qcpp", "Drawer is already open");
                drawerLayout.closeDrawer(GravityCompat.START);

            } else {
                drawerLayout.openDrawer(GravityCompat.START);
                Log.d("qcpp", "Drawer opened");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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