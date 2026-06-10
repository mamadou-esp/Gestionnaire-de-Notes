package com.example.gestionnairedenotes;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    private static volatile NoteDatabase instance;
    public abstract NoteDao noteDao();

    // Pool de threads pour exécuter les requêtes en arrière-plan (hors du thread UI principal)
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static NoteDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (NoteDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    NoteDatabase.class, "base_de_donnees_notes")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}