package com.example.gestionnairedenotes;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Query("SELECT * FROM table_notes ORDER BY id DESC")
    List<Note> getAllNotes();

    @Query("SELECT * FROM table_notes WHERE isFavoris = 1 ORDER BY id DESC")
    List<Note> getFavoriteNotes();

    @Query("SELECT * FROM table_notes WHERE titre LIKE :searchQuery ORDER BY id DESC")
    List<Note> searchNotesByTitle(String searchQuery);
}