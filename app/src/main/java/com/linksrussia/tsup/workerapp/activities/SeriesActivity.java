package com.linksrussia.tsup.workerapp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.linksrussia.tsup.workerapp.App;
import com.linksrussia.tsup.workerapp.R;
import com.linksrussia.tsup.workerapp.databinding.ActivitySeriesBinding;
import com.linksrussia.tsup.workerapp.db.dao.MeasureDao;
import com.linksrussia.tsup.workerapp.db.dao.SeriesDao;
import com.linksrussia.tsup.workerapp.db.entities.Series;
import com.linksrussia.tsup.workerapp.util.DialogUtil;
import com.linksrussia.tsup.workerapp.util.ShareUtil;

public class SeriesActivity extends AppCompatActivity {
    public static final DialogUtil DIALOG_UTIL = new DialogUtil();

    private final SeriesDao seriesDao = App.getAppDatabase().seriesDao();
    private final MeasureDao measureDao = App.getAppDatabase().measureDao();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Dialog dialog = DIALOG_UTIL.onCreateAddSeriesDialog(this);
        final ActivitySeriesBinding binding = ActivitySeriesBinding.inflate(getLayoutInflater());
        binding.fab.setOnClickListener(view -> dialog.show());
        setContentView(binding.getRoot());

        renderData();
    }

    public void renderData() {
        LayoutInflater layoutInflater = getLayoutInflater();
        GridLayout measurementLayout = findViewById(R.id.measurementLayout);

        measurementLayout.removeAllViews();

        for (Series series : seriesDao.getAll()) {
            View inflate = layoutInflater.inflate(R.layout.item_series, measurementLayout, false);

            inflate.findViewById(R.id.itemMeasurementExport).setOnClickListener(view -> {
                ShareUtil.sendData(SeriesActivity.this, measureDao.getBySeries(series.id));
            });
            inflate.findViewById(R.id.itemMeasurementDelete).setOnClickListener(view -> {
                new AlertDialog.Builder(this)
                        .setTitle("Нужно подтверждение")
                        .setMessage("Вы уверены что хотите удалить серю измерений?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("ДА", (dialogInterface, i) -> {
                            seriesDao.delete(series);
                            renderData();
                        })
                        .setNegativeButton("НЕТ", null).show();
            });
            ((TextView) inflate.findViewById(R.id.itemMeasurementName)).setText(series.name);

            inflate.setOnClickListener(view -> {
                startActivity(new Intent(this, MeasurementActivity.class)
                        .putExtra(MeasurementActivity.SERIES_ID, series.id)
                        .putExtra(MeasurementActivity.SERIES_NAME, series.name));
            });


            measurementLayout.addView(inflate);
        }
    }


}