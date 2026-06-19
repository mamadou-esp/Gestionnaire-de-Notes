package com.example.gestionnairedenotes;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    public abstract NoteDao noteDao();

    // Implémentation du pattern Singleton pour éviter d'ouvrir plusieurs instances
    private static volatile NoteDatabase INSTANCE;

    // Executor pour lancer les requêtes en arrière-plan (sans bloquer l'interface graphique)
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static NoteDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (NoteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    NoteDatabase.class, "notes_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}