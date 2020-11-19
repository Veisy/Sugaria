package com.example.sekerimremake;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.sekerimremake.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.sekerimremake.databinding.ActivityMainBinding binding =
                ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*
        ChartDbHelper helper = new ChartDbHelper(this);
        ChartRowModel model;
        for(int i = 0; i < 11; i++) {
            for(int j = 1; j < 30; j++){
                model = new ChartRowModel(
                        -1,
                        j,
                        i,
                        2020,
                        new Random().nextInt(75) + 65,
                        new Random().nextInt(95) + 95,
                        new Random().nextInt(75) + 65,
                        new Random().nextInt(95) + 95,
                        new Random().nextInt(75) + 65,
                        new Random().nextInt(95) + 95,
                        new Random().nextInt(75) + 85
                        );
                helper.addOne(model);
            }
        }
         */

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                 .findFragmentById(R.id.navigation_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

}