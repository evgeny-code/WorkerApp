package com.linksrussia.tsup.workerapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.linksrussia.tsup.workerapp.App;
import com.linksrussia.tsup.workerapp.db.dao.MeasureDao;
import com.linksrussia.tsup.workerapp.db.entities.Measure;
import com.linksrussia.tsup.workerapp.dto.DeviceData;
import com.linksrussia.tsup.workerapp.util.ES;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class DataReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION = "DataReceiver";
    public static final String DATA_EXTRA = "DATA_EXTRA";

    private final long currentSeriesId;
    private final Consumer<List<Measure>> renderCallback;

    final MeasureDao measureDao = App.db.measureDao();

    public DataReceiver(long currentSeriesId, Consumer<List<Measure>> renderCallback) {
        this.currentSeriesId = currentSeriesId;
        this.renderCallback = renderCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        DeviceData deviceData = ES.GSON.fromJson(intent.getStringExtra(DATA_EXTRA), DeviceData.class);

        Measure measure = new Measure();
        measure.gj = Double.valueOf(deviceData.getGJ());
        measure.cg = Double.valueOf(deviceData.getCG());
        measure.timeMills = new Date().getTime();
        measure.seriesId = currentSeriesId;
        measureDao.insertAll(measure);

        renderCallback.accept(measureDao.getBySeries(currentSeriesId));
    }
}
