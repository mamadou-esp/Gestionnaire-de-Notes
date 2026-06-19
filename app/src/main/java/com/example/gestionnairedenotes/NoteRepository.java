package com.example.gestionnairedenotes;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class NoteRepository {

    private final NoteDao noteDao;

    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
    }

    public LiveData<List<Note>> getAllNotes() {
        return noteDao.getAllNotes();
    }

    public void insert(Note note) {
        NoteDatabase.databaseWriteExecutor.execute(() -> noteDao.insert(note));
    }

    public void update(Note note) {
        NoteDatabase.databaseWriteExecutor.execute(() -> noteDao.update(note));
    }

    public void delete(Note note) {
        NoteDatabase.databaseWriteExecutor.execute(() -> noteDao.delete(note));
    }
}