package com.example.gestionnairedenotes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM notes " +
            "WHERE title LIKE '%' || :query || '%' " +
            "AND (:favoritesOnly = 0 OR favorite = 1) " +
            "ORDER BY dateMillis DESC")
    LiveData<List<Note>> getFilteredNotes(String query, boolean favoritesOnly);
}