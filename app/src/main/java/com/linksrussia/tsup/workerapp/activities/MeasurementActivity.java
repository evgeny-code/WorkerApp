package com.linksrussia.tsup.workerapp.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.linksrussia.tsup.workerapp.App;
import com.linksrussia.tsup.workerapp.R;
import com.linksrussia.tsup.workerapp.db.dao.MeasureDao;
import com.linksrussia.tsup.workerapp.db.entities.Measure;
import com.linksrussia.tsup.workerapp.receivers.DataReceiver;
import com.linksrussia.tsup.workerapp.util.DialogUtil;
import com.linksrussia.tsup.workerapp.util.ES;
import com.linksrussia.tsup.workerapp.util.ShareUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class MeasurementActivity extends AppCompatActivity {

    public static final String SERIES_ID = "MEASURE_ID";
    public static final String SERIES_NAME = "MEASURE_NAME";

    public static final DialogUtil DIALOG_UTIL = new DialogUtil();

    final MeasureDao measureDao = App.getAppDatabase().measureDao();
    final List<View> renderedRows = new ArrayList<>();

    private DataReceiver dataReceiver;
    private Consumer<List<Measure>> renderCallback = new Consumer<List<Measure>>() {
        @Override
        public void accept(List<Measure> measures) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            LayoutInflater layoutInflater = getLayoutInflater();
            TableLayout measurementDataLayout = findViewById(R.id.measurementDataLayout);

            // measurementDataLayout.removeAllViews();
            renderedRows.forEach(view -> measurementDataLayout.removeView(view));
            renderedRows.clear();

            for (Measure measure : measures) {
                View inflate = layoutInflater.inflate(R.layout.item_measure, measurementDataLayout, false);

                ((TextView) inflate.findViewById(R.id.itemMeasureDateTime)).setText(dateFormat.format(new Date(measure.timeMills)));
                ((TextView) inflate.findViewById(R.id.itemMeasureCG)).setText("" + measure.cg);
                ((TextView) inflate.findViewById(R.id.itemMeasureGJ)).setText("" + measure.gj);
                inflate.findViewById(R.id.itemMeasurementDelete).setOnClickListener(v -> {
                    measureDao.delete(measure);
                    measures.remove(measure);

                    this.accept(measures);
                });


                measurementDataLayout.addView(inflate);
                renderedRows.add(inflate);
            }
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        Intent intent = getIntent();
        TextView measurementNameTextView = findViewById(R.id.measurementName);
        measurementNameTextView.setText(intent.getStringExtra(SERIES_NAME).toUpperCase(Locale.ROOT) + ":");

        long seriesId = intent.getLongExtra(SERIES_ID, -1L);
        dataReceiver = new DataReceiver(seriesId, renderCallback);
        registerReceiver(dataReceiver, new IntentFilter(DataReceiver.INTENT_ACTION));

        renderCallback.accept(measureDao.getBySeries(seriesId));

        findViewById(R.id.fab).setOnClickListener(v -> {
            ShareUtil.sendData(MeasurementActivity.this, measureDao.getBySeries(seriesId));
        });

        ES.restartReceiveData();
        BluetoothDevice selectedDevice = App.getApplicationScope().selectedDevice;

        if (null == selectedDevice) {
            DIALOG_UTIL.onCreateNoDeviceForkDialog(this).show();
        } else {
            Snackbar.make(findViewById(R.id.measurementDataLayout),
                            String.format("Ожидание данных с прибора %s [%s]", selectedDevice.getName(), selectedDevice.getAddress()),
                            Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ES.stopReceiveData();
        if (null != dataReceiver)
            unregisterReceiver(dataReceiver);
    }
}