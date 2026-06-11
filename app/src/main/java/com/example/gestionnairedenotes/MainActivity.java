package com.example.gestionnairedenotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etSearch;
    private TextView btnFavoris;
    private TextView tvEmptyNotes;
    private RecyclerView recyclerViewNotes;

    private CardView cardPaletteContainer;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabClosePalette;

    private View viewColorGreen, viewColorRed, viewColorBlue, viewColorYellow, viewColorOrange, viewColorGray;

    // Ajout de l'adaptateur pour la liste
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSearch = findViewById(R.id.etSearch);
        btnFavoris = findViewById(R.id.btnFavoris);
        tvEmptyNotes = findViewById(R.id.tvEmptyNotes);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);

        cardPaletteContainer = findViewById(R.id.cardPaletteContainer);
        fabAdd = findViewById(R.id.fabAdd);
        fabClosePalette = findViewById(R.id.fabClosePalette);

        viewColorGreen = findViewById(R.id.viewColorGreen);
        viewColorRed = findViewById(R.id.viewColorRed);
        viewColorBlue = findViewById(R.id.viewColorBlue);
        viewColorYellow = findViewById(R.id.viewColorYellow);
        viewColorOrange = findViewById(R.id.viewColorOrange);
        viewColorGray = findViewById(R.id.viewColorGray);

        // --- NOUVEAU : Configuration du RecyclerView et de son Adaptateur ---
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter();
        recyclerViewNotes.setAdapter(noteAdapter);
        // -------------------------------------------------------------------

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabAdd.setVisibility(View.GONE);
                cardPaletteContainer.setVisibility(View.VISIBLE);
            }
        });

        fabClosePalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardPaletteContainer.setVisibility(View.GONE);
                fabAdd.setVisibility(View.VISIBLE);
            }
        });

        viewColorGreen.setOnClickListener(v -> ouvrirEcranCreation("#219653"));
        viewColorRed.setOnClickListener(v -> ouvrirEcranCreation("#EB5757"));
        viewColorBlue.setOnClickListener(v -> ouvrirEcranCreation("#2F80ED"));
        viewColorYellow.setOnClickListener(v -> ouvrirEcranCreation("#F2C94C"));
        viewColorOrange.setOnClickListener(v -> ouvrirEcranCreation("#F2994A"));
        viewColorGray.setOnClickListener(v -> ouvrirEcranCreation("#828282"));
    }

    private void ouvrirEcranCreation(String hexColor) {
        cardPaletteContainer.setVisibility(View.GONE);
        fabAdd.setVisibility(View.VISIBLE);

        Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
        intent.putExtra("EXTRA_COLOR", hexColor);
        startActivity(intent);
    }

    // --- NOUVEAU : Chargement automatique des notes à chaque retour sur l'écran ---
    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        // Exécution de la requête en arrière-plan
        NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // Récupération de la liste depuis la BDD
                final List<Note> notes = NoteDatabase.getInstance(MainActivity.this).noteDao().getAllNotes();

                // Mise à jour de l'interface graphique sur le thread principal
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (notes.isEmpty()) {
                            // Bascule sur l'Écran 1 (Liste Vide)
                            tvEmptyNotes.setVisibility(View.VISIBLE);
                            recyclerViewNotes.setVisibility(View.GONE);
                        } else {
                            // Bascule sur l'Écran 3 (Liste remplie)
                            tvEmptyNotes.setVisibility(View.GONE);
                            recyclerViewNotes.setVisibility(View.VISIBLE);
                            noteAdapter.setNotes(notes);
                        }
                    }
                });
            }
        });
    }
}