package com.imeshperera.ecomapp.activities;

import android.os.Bundle;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.imeshperera.ecomapp.R;
import com.imeshperera.ecomapp.fragments.CategoriesFragment;
import com.imeshperera.ecomapp.fragments.HomeFragment;
import com.imeshperera.ecomapp.fragments.OrdersFragment;
import com.imeshperera.ecomapp.fragments.ProfileFragment;
import com.imeshperera.ecomapp.fragments.WishlistFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load home by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), false);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment(), false);
                return true;
            } else if (id == R.id.nav_categories) {
                loadFragment(new CategoriesFragment(), false);
                return true;
            } else if (id == R.id.nav_wishlist) {
                loadFragment(new WishlistFragment(), false);
                return true;
            } else if (id == R.id.nav_orders) {
                loadFragment(new OrdersFragment(), false);
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment(), false);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
        transaction.replace(R.id.home_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        currentFragment = fragment;
    }
}