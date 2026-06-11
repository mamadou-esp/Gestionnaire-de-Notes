package com.example.gestionnairedenotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotes;
    private NoteAdapter noteAdapter;
    private TextView tvEmptyNotes;

    private FloatingActionButton fabAdd;
    private View cardPaletteContainer;
    private View viewColorRed, viewColorBlue, viewColorYellow, viewColorGreen, viewColorOrange, viewColorGray;
    private FloatingActionButton fabClosePalette;
    private boolean isFabExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation de la liste
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        tvEmptyNotes = findViewById(R.id.tvEmptyNotes);

        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter();
        recyclerViewNotes.setAdapter(noteAdapter);

        // Liaison des boutons
        fabAdd = findViewById(R.id.fabAdd);
        cardPaletteContainer = findViewById(R.id.cardPaletteContainer);
        fabClosePalette = findViewById(R.id.fabClosePalette);

        viewColorRed = findViewById(R.id.viewColorRed);
        viewColorBlue = findViewById(R.id.viewColorBlue);
        viewColorYellow = findViewById(R.id.viewColorYellow);
        viewColorGreen = findViewById(R.id.viewColorGreen);
        viewColorOrange = findViewById(R.id.viewColorOrange);
        viewColorGray = findViewById(R.id.viewColorGray);

        // Ouverture / Fermeture du menu
        fabAdd.setOnClickListener(view -> toggleFabMenu());
        fabClosePalette.setOnClickListener(view -> toggleFabMenu());

        // Envoi de la couleur vers la page de création
        viewColorRed.setOnClickListener(v -> openAddEditActivityWithColor("#FF5252"));
        viewColorBlue.setOnClickListener(v -> openAddEditActivityWithColor("#448AFF"));
        viewColorYellow.setOnClickListener(v -> openAddEditActivityWithColor("#FFEB3B"));
        viewColorGreen.setOnClickListener(v -> openAddEditActivityWithColor("#4CAF50"));
        viewColorOrange.setOnClickListener(v -> openAddEditActivityWithColor("#F2994A"));
        viewColorGray.setOnClickListener(v -> openAddEditActivityWithColor("#828282"));
    }

    private void toggleFabMenu() {
        if (isFabExpanded) {
            cardPaletteContainer.setVisibility(View.GONE);
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            cardPaletteContainer.setVisibility(View.VISIBLE);
            fabAdd.setVisibility(View.GONE);
        }
        isFabExpanded = !isFabExpanded;
    }

    private void openAddEditActivityWithColor(String colorHex) {
        Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
        intent.putExtra("SELECTED_COLOR_FROM_MAIN", colorHex);
        startActivity(intent);

        toggleFabMenu(); // Ferme le menu en partant
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotesFromDatabase();
    }

    private void loadNotesFromDatabase() {
        NoteDatabase.databaseWriteExecutor.execute(() -> {
            List<Note> notes = NoteDatabase.getInstance(MainActivity.this).noteDao().getAllNotes();
            runOnUiThread(() -> {
                if (notes.isEmpty()) {
                    tvEmptyNotes.setVisibility(View.VISIBLE);
                    recyclerViewNotes.setVisibility(View.GONE);
                } else {
                    tvEmptyNotes.setVisibility(View.GONE);
                    recyclerViewNotes.setVisibility(View.VISIBLE);
                    noteAdapter.setNotes(notes);
                }
            });
        });
    }
}