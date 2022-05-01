package com.example.sekerimremake;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sekerimremake.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.sekerimremake.databinding.ActivityMainBinding binding =
                ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.checkFragment, R.id.catalogFragment, R.id.chartFragment, R.id.alarmFragment).build();

        // Handle toolbar and bottom navigation menu.
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.toolbar.setVisibility(View.GONE);
            } else {
                binding.toolbar.setVisibility(View.VISIBLE);
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                    && ((destination.getId() == R.id.catalogItemFragment) && (destination.getId() == R.id.chartFragment))) {
                binding.bottomNavigation.setVisibility(View.GONE);
            } else {
                binding.bottomNavigation.setVisibility(View.VISIBLE);
            }
        });

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);
    }
}