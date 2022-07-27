package com.linksrussia.tsup.workerapp.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.linksrussia.tsup.workerapp.App;
import com.linksrussia.tsup.workerapp.R;
import com.linksrussia.tsup.workerapp.db.dao.MeasureDao;
import com.linksrussia.tsup.workerapp.db.dao.SeriesDao;
import com.linksrussia.tsup.workerapp.db.entities.Measure;
import com.linksrussia.tsup.workerapp.db.entities.Series;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static final Random RANDOM = new Random(System.currentTimeMillis());

    private final MeasureDao measureDao = App.db.measureDao();
    private final SeriesDao seriesDao = App.db.seriesDao();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.selectDeviceBtn).setOnClickListener(this::selectDeviceClick);
        findViewById(R.id.measurementBtn).setOnClickListener(this::measurementClick);
        // findViewById(R.id.generateBtn).setOnClickListener(this::generateClick);

        BluetoothDevice selectedDevice = App.selectedDevice;
        if (null != selectedDevice) {
            Snackbar.make(findViewById(R.id.selectDeviceBtn),
                            String.format("Вы выбрали прибор %s [%s]", selectedDevice.getName(), selectedDevice.getAddress()),
                            Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void generateClick(View btn) {
        for (Series series : seriesDao.getAll()) {
            for (int i = 0; i < 12; i++) {
                Measure measure = new Measure();
                measure.cg = RANDOM.nextGaussian();
                measure.gj = RANDOM.nextGaussian();
                measure.timeMills = System.currentTimeMillis();
                measure.seriesId = series.id;

                measureDao.insertAll(measure);
            }
        }

        Snackbar.make(btn, "Данные сгенерены", Snackbar.LENGTH_LONG).show();
    }


    private void selectDeviceClick(View btn) {
        startActivity(new Intent(this, SelectDeviceActivity.class));
    }

    private void measurementClick(View btn) {
        startActivity(new Intent(this, SeriesActivity.class));
    }

}