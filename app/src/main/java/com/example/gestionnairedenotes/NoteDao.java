package com.example.gestionnairedenotes;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface NoteDao {

    // Insérer une nouvelle note
    @Insert
    void insert(Note note);

    // Mettre à jour une note existante
    @Update
    void update(Note note);

    // Récupérer toutes les notes (les plus récentes en premier)
    @Query("SELECT * FROM table_notes ORDER BY id DESC")
    LiveData<List<Note>> getAllNotes();

    // Récupérer uniquement les notes favorites (Filtre Favoris)
    @Query("SELECT * FROM table_notes WHERE isFavori = 1 ORDER BY id DESC")
    List<Note> getFavoriteNotes();

    // Rechercher une note par son titre (Recherche)
    @Query("SELECT * FROM table_notes WHERE titre LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    List<Note> searchNotesByTitle(String searchQuery);
}