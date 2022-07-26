package com.linksrussia.tsup.workerapp.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.linksrussia.tsup.workerapp.db.entities.Series;

import java.util.List;

@Dao
public interface SeriesDao {

    @Query("SELECT * FROM Series")
    List<Series> getAll();

    @Insert
    void insertAll(Series... series);

    @Delete
    void delete(Series series);
}
