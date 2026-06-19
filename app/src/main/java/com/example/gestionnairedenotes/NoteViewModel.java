package com.example.gestionnairedenotes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository repository;
    private final MediatorLiveData<List<Note>> filteredNotes = new MediatorLiveData<>();

    private LiveData<List<Note>> currentSource;
    private String currentQuery = "";
    private boolean currentFavoritesOnly = false;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        currentSource = repository.getFilteredNotes(currentQuery, currentFavoritesOnly);
        filteredNotes.addSource(currentSource, filteredNotes::setValue);
    }

    public LiveData<List<Note>> getFilteredNotes() {
        return filteredNotes;
    }

    public void setSearchQuery(String query) {
        currentQuery = query == null ? "" : query;
        refresh();
    }

    public void setFavoritesOnly(boolean favoritesOnly) {
        currentFavoritesOnly = favoritesOnly;
        refresh();
    }

    private void refresh() {
        filteredNotes.removeSource(currentSource);
        currentSource = repository.getFilteredNotes(currentQuery, currentFavoritesOnly);
        filteredNotes.addSource(currentSource, filteredNotes::setValue);
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void toggleFavorite(Note note) {
        note.setFavorite(!note.isFavorite());
        repository.update(note);
    }
}