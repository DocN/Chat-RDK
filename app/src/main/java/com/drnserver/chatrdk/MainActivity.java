package com.drnserver.chatrdk;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.drnserver.chatrdk.service.ServiceUtils;
import com.drnserver.chatrdk.ui.FriendsFragment;
import com.drnserver.chatrdk.ui.GroupFragment;
import com.drnserver.chatrdk.ui.ProfileSearchFragment;


/*
place holder meme for auth key lol

keytool -exportcert -list -v -alias androiddebugkey -keystore %USERPROFILE%\.android\debug.keystore
keytool password: android
SHA1: 2A:97:EB:E0:CA:C8:F1:37:13:F7:8C:FA:1C:F3:AA:15:55:88:01:AD
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    private FloatingActionButton floatButton;
    private SectionsPageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Starting.");
        //floatButton = (FloatingActionButton) findViewById(R.id.fab);
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.container);
  //      setupViewPager(mViewPager);

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(mViewPager);

        /*ALEX: bottom navigation view set icon size */
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }

        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        /*ALEX: bottom navigation view  onclick*/
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.singlechat) {
                    //
                    System.out.println("singlechat");
                    item.setChecked(true);
                    System.out.println("we reached here");
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.main_content, new FriendsFragment()).commit();

                    return true;
                }

                if (item.getItemId() == R.id.groupchat) {
                    //
                    System.out.println("groupchat");
                    item.setChecked(true);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.main_content, new GroupFragment()).commit();

                    return true;
                }

                if (item.getItemId() == R.id.friends) {
                    //
                    System.out.println("friends");
                    item.setChecked(true);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.main_content, new ProfileSearchFragment()).commit();

                    return true;
                }

                if (item.getItemId() == R.id.settings) {
                    //
                    System.out.println("setting");
                    item.setChecked(true);
                    goToProfile();
                    return true;
                }

                return false;
            }
        });


        /** ALEX: ifs govern moving from profile activity to other fragments*/
        if(getIntent().hasExtra("index")) {
            String fragment  = getIntent().getExtras().getString("index");

            if(fragment.equals("singlechat")) {
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.main_content, new FriendsFragment()).commit();
            }

            if(fragment.equals("groupchat")) {

                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.main_content, new GroupFragment()).commit();
            }

            if(fragment.equals("friends")) {
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.main_content, new ProfileSearchFragment()).commit();
            }
        } else {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.main_content, new GroupFragment()).commit();
        }
    }



    private void setupViewPager(ViewPager viewPager) {
        adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new GroupFragment(), "Group Chat");
        adapter.addFragment(new FriendsFragment(), "Friends");
        adapter.addFragment(new ProfileSearchFragment(), "Profile Search");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ServiceUtils.stopServiceFriendChat(MainActivity.this.getApplicationContext(), false);
                if (adapter.getItem(position) instanceof FriendsFragment) {
                    floatButton.setVisibility(View.VISIBLE);
                    floatButton.setOnClickListener(((FriendsFragment) adapter.getItem(position)).onClickFloatButton.getInstance(MainActivity.this));
                    floatButton.setImageResource(R.drawable.plus);
                } else if (adapter.getItem(position) instanceof GroupFragment) {
                    floatButton.setVisibility(View.VISIBLE);
                    floatButton.setOnClickListener(((GroupFragment) adapter.getItem(position)).onClickFloatButton.getInstance(MainActivity.this));
                    floatButton.setImageResource(R.drawable.ic_float_add_group);
                    GroupFragment groupClient = (GroupFragment) adapter.getItem(position);
                } else {
                    floatButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuaction, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.openSettings:
                this.goToProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void goToProfile() {
        Intent myIntent = new Intent(MainActivity.this, ProfilePage.class);
        MainActivity.this.startActivity(myIntent);
    }


}
