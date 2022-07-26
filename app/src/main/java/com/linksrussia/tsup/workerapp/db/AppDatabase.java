package com.linksrussia.tsup.workerapp.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.linksrussia.tsup.workerapp.db.dao.MeasureDao;
import com.linksrussia.tsup.workerapp.db.dao.SeriesDao;
import com.linksrussia.tsup.workerapp.db.entities.Measure;
import com.linksrussia.tsup.workerapp.db.entities.Series;

@Database(entities = {Measure.class, Series.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MeasureDao measureDao();

    public abstract SeriesDao seriesDao();
}
