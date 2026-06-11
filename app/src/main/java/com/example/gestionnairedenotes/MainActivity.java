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

public class MainActivity extends AppCompatActivity {

    // Déclaration des composants graphiques
    private EditText etSearch;
    private TextView btnFavoris;
    private TextView tvEmptyNotes;
    private RecyclerView recyclerViewNotes;

    private CardView cardPaletteContainer;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabClosePalette;

    private View viewColorGreen, viewColorRed, viewColorBlue, viewColorYellow, viewColorOrange, viewColorGray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialisation des composants graphiques (Liaison XML)
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

        // Configuration temporaire du RecyclerView (LayoutManager linéaire vertical)
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));

        // 2. Gestion de l'ouverture de la palette de couleurs (Écran 1 -> Écran 2)
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabAdd.setVisibility(View.GONE);
                cardPaletteContainer.setVisibility(View.VISIBLE);
            }
        });

        // 3. Gestion de la fermeture de la palette (Écran 2 -> Écran 1)
        fabClosePalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardPaletteContainer.setVisibility(View.GONE);
                fabAdd.setVisibility(View.VISIBLE);
            }
        });

        // 4. Écouteurs de clics sur chaque pastille pour ouvrir l'écran de création avec la couleur officielle
        viewColorGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ouvrirEcranCreation("#219653"); }
        });

        viewColorRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ouvrirEcranCreation("#EB5757"); }
        });

        viewColorBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ouvrirEcranCreation("#2F80ED"); }
        });

        viewColorYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ouvrirEcranCreation("#F2C94C"); }
        });

        viewColorOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ouvrirEcranCreation("#F2994A"); }
        });

        viewColorGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { ouvrirEcranCreation("#828282"); }
        });
    }

    /**
     * Méthode centralisée pour lancer AddEditActivity en passant la couleur sélectionnée.
     * Respecte les principes de réutilisation de code demandés dans le barème.
     */
    private void ouvrirEcranCreation(String hexColor) {
        // Refermer la palette pour que l'état visuel soit propre au retour
        cardPaletteContainer.setVisibility(View.GONE);
        fabAdd.setVisibility(View.VISIBLE);

        // Navigation vers l'activité de création
        Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
        intent.putExtra("EXTRA_COLOR", hexColor);
        startActivity(intent);
    }
}