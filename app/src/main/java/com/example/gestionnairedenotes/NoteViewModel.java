package com.example.gestionnairedenotes;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
    }

    public void insert(Note note) {
        NoteDatabase.databaseWriteExecutor.execute(() -> noteDao.insert(note));
    }

    public void update(Note note) {
        NoteDatabase.databaseWriteExecutor.execute(() -> noteDao.update(note));
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
}
