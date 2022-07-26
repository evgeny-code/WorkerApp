package com.linksrussia.tsup.workerapp.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.linksrussia.tsup.workerapp.db.entities.Measure;

import java.util.List;

@Dao
public interface MeasureDao {
    @Query("SELECT * FROM measure")
    List<Measure> getAll();

    @Query("SELECT * FROM measure WHERE series_id=:seriesId")
    List<Measure> getBySeries(long seriesId);

    @Insert
    void insertAll(Measure... measures);

    @Delete
    void delete(Measure measure);
}
