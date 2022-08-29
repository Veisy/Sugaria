package com.vyy.sekerimremake;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.vyy.sekerimremake.databinding.ActivityMainBinding;
import com.vyy.sekerimremake.features.chart.data.source.local.ChartDbHelper;
import com.vyy.sekerimremake.features.chart.domain.model.ChartRowModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private static final int CREATE_FILE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setNavigationComponents();
    }

    private void setNavigationComponents() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.catalogMasterFragment, R.id.chartMasterFragment).build();

        // Handle toolbar and bottom navigation menu.
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.toolbar.setVisibility(View.GONE);
            } else {
                binding.toolbar.setVisibility(View.VISIBLE);
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                    && ((destination.getId() == R.id.catalogDetailsFragment) && (destination.getId() == R.id.chartMasterFragment))) {
                binding.bottomNavigation.setVisibility(View.GONE);
            } else {
                binding.bottomNavigation.setVisibility(View.VISIBLE);
            }
        });

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_saveChart) {
                createXlsFile();
                return true;
            }
            return false;
        });
    }

    private void createXlsFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.ms-excel");
        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.excel_file_name));

        // Specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOCUMENTS);
        }

        startActivityForResult(intent, CREATE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            if (data != null) {
                saveChart(data.getData());
            }
//        }
        }
    }

    private void saveChart(Uri uri) {
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet(getString(R.string.measurements));

        writeDataToSheet(hssfSheet);

        try {
            OutputStream outputStream = this.getContentResolver().openOutputStream(uri);

            hssfWorkbook.write(outputStream);

            outputStream.flush();
            outputStream.close();

            Toast.makeText(MainActivity.this, getText(R.string.saved_succesfully), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(MainActivity.this, getText(R.string.operation_failed), Toast.LENGTH_SHORT).show();
            if (e.getMessage() != null) {
                Log.d("SaveChart", e.getMessage());
            } else {
                Log.d("SaveChart", Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private void writeDataToSheet(HSSFSheet hssfSheet) {
        List<ChartRowModel> chartRowModels = new ChartDbHelper(this).getEveryone();

        String[] titles = {getString(R.string.date), getString(R.string.morning_empty_measurement),
                getString(R.string.morning_full_measurement), getString(R.string.afternoon_empty_measurement),
                getString(R.string.afternoon_full_measurement), getString(R.string.evening_empty_measurement),
                getString(R.string.evening_full_measurement), getString(R.string.night)};

        //Iterate over the sheet rows and write the data
        for (int row = 0; row < chartRowModels.size() + 1; row++) {
            HSSFRow hssfRow = hssfSheet.createRow(row);

            String[] cellValues;
            if (row == 0) {
                // Titles
                cellValues = titles;

            } else {
                // Since the first row is for titles, the model index is 'row - 1'
                ChartRowModel theDay = chartRowModels.get(row - 1);
                cellValues = convertModelToTableCells(theDay);
            }

            for (int index = 0; index < cellValues.length; index++) {
                HSSFCell hssfCell = hssfRow.createCell(index);
                hssfCell.setCellValue(cellValues[index]);
            }
        }
    }

    private String[] convertModelToTableCells(ChartRowModel rowModel) {
        String date = rowModel.getDayOfMonth() + "/" + (rowModel.getMonth() + 1) + "/" + rowModel.getYear();

        int[] measurementValues = {rowModel.getMorningEmpty(), rowModel.getMorningFull(),
                rowModel.getAfternoonEmpty(), rowModel.getAfternoonFull(), rowModel.getEveningEmpty(),
                rowModel.getEveningFull(), rowModel.getNight()};

        String[] cellValues = new String[measurementValues.length + 1];
        cellValues[0] = date;

        for (int index = 1; index < cellValues.length; index++) {
            // Since the first cell is for date, the measurement index is 'index - 1'
            if (measurementValues[index - 1] == 0) {
                // Zero means empty measurement value
                cellValues[index] = "-";
            } else {
                cellValues[index] = String.valueOf(measurementValues[index - 1]);
            }
        }
        Log.d("SaveChart", "Row Values:" + Arrays.toString(cellValues));
        return cellValues;
    }
}