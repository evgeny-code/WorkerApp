package com.linksrussia.tsup.workerapp.db.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@NoArgsConstructor
@Entity(indices = {@Index(unique = true, value = "name")})
@ToString
public class Series {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    public Series(String name) {
        this.name = name;
    }
}
