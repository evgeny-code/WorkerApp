package com.linksrussia.tsup.workerapp.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import lombok.ToString;

@Entity(foreignKeys = @ForeignKey(entity = Series.class, parentColumns = "id", childColumns = "series_id", onDelete = ForeignKey.CASCADE))
@ToString
public class Measure {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public Double gj;
    public Double cg;

    public long timeMills;

    public Double lon;
    public Double lat;

    @ColumnInfo(name = "series_id")
    public long seriesId;
}
