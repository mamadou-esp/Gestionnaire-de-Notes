package com.example.gestionnairedenotes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;

    private CardView cardPaletteContainer;
    private FloatingActionButton fabAddNote;
    private RecyclerView recyclerView;
    private TextView tvEmptyNotes;
    private EditText etSearch;

    private boolean isFilteringFavorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialisation des vues
        recyclerView = findViewById(R.id.recyclerViewNotes);
        tvEmptyNotes = findViewById(R.id.tvEmptyNotes);
        etSearch = findViewById(R.id.etSearch);
        cardPaletteContainer = findViewById(R.id.cardPaletteContainer);
        fabAddNote = findViewById(R.id.fabAdd);
        FloatingActionButton fabClosePalette = findViewById(R.id.fabClosePalette);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // 2. Configuration de l'adaptateur
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        // 2.5 Listeners de l'adaptateur (Doit être avant le ViewModel pour capter le premier chargement)
        setupAdapterListeners();

        // 3. Liaison avec le ViewModel
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, notes -> {
            if (notes != null) {
                adapter.setNotes(notes);
            }
        });

        // 4. Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note noteToDelete = adapter.getNoteAt(position);
                noteViewModel.delete(noteToDelete);
                Toast.makeText(MainActivity.this, "Note supprimée 🗑️", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        // 5. Gestion de la Palette
        fabAddNote.setOnClickListener(v -> {
            cardPaletteContainer.setVisibility(View.VISIBLE);
            fabAddNote.setVisibility(View.GONE);
        });

        fabClosePalette.setOnClickListener(v -> {
            cardPaletteContainer.setVisibility(View.GONE);
            fabAddNote.setVisibility(View.VISIBLE);
        });

        setupColorClick(R.id.viewColorGreen, "#219653");
        setupColorClick(R.id.viewColorRed, "#EB5757");
        setupColorClick(R.id.viewColorBlue, "#2F80ED");
        setupColorClick(R.id.viewColorYellow, "#F2C94C");
        setupColorClick(R.id.viewColorOrange, "#F2994A");
        setupColorClick(R.id.viewColorGray, "#828282");

        // 6. Recherche dynamique
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 7. Filtrage Favoris
        TextView btnFavoris = findViewById(R.id.btnFavoris);
        btnFavoris.setOnClickListener(v -> {
            isFilteringFavorites = !isFilteringFavorites;
            adapter.filterFavorites(isFilteringFavorites);
            btnFavoris.setText(isFilteringFavorites ? "★ Favoris" : "Favoris");
        });

        // 7.5 Dark Mode
        TextView btnTheme = findViewById(R.id.btnTheme);
        updateThemeIcon(btnTheme);
        btnTheme.setOnClickListener(v -> toggleTheme());

        // 9. Tri
        View btnTri = findViewById(R.id.btnTri);
        if (btnTri != null) {
            btnTri.setOnClickListener(this::showSortPopup);
        }
    }

    private void setupAdapterListeners() {
        adapter.setOnNotesCountChangedListener((visibleCount, totalCount) -> {
            if (visibleCount == 0) {
                recyclerView.setVisibility(View.GONE);
                tvEmptyNotes.setVisibility(View.VISIBLE);
                tvEmptyNotes.setText(totalCount > 0 ? "Aucun résultat trouvé 🔍" : "Aucune note");
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmptyNotes.setVisibility(View.GONE);
            }

            if (visibleCount == totalCount) {
                etSearch.setHint("Rechercher (" + totalCount + " notes)");
            } else {
                etSearch.setHint("Trouvé : " + visibleCount + " / " + totalCount);
            }
        });
    }

    private void updateThemeIcon(TextView btnTheme) {
        int nightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        btnTheme.setText(nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES ? "☀️" : "🌙");
    }

    private void toggleTheme() {
        int nightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void showSortPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenu().add(0, 0, 0, "📅 Plus récentes d'abord");
        popupMenu.getMenu().add(0, 1, 1, "🔤 Ordre alphabétique");
        popupMenu.setOnMenuItemClickListener(item -> {
            adapter.setSortType(item.getItemId());
            return true;
        });
        popupMenu.show();
    }

    private void setupColorClick(int viewId, String hexColor) {
        View colorView = findViewById(viewId);
        if (colorView != null) {
            colorView.setOnClickListener(v -> {
                cardPaletteContainer.setVisibility(View.GONE);
                fabAddNote.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra("EXTRA_COLOR", hexColor);
                startActivity(intent);
            });
        }
    }
}